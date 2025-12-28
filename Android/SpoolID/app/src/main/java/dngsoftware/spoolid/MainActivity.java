package dngsoftware.spoolid;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static java.lang.String.format;
import static dngsoftware.spoolid.Utils.GetMaterialBrand;
import static dngsoftware.spoolid.Utils.GetMaterialInfo;
import static dngsoftware.spoolid.Utils.GetMaterialLength;
import static dngsoftware.spoolid.Utils.GetMaterialName;
import static dngsoftware.spoolid.Utils.GetMaterialWeight;
import static dngsoftware.spoolid.Utils.GetSetting;
import static dngsoftware.spoolid.Utils.SaveSetting;
import static dngsoftware.spoolid.Utils.SetPermissions;
import static dngsoftware.spoolid.Utils.addFilament;
import static dngsoftware.spoolid.Utils.bytesToHex;
import static dngsoftware.spoolid.Utils.canMfc;
import static dngsoftware.spoolid.Utils.cipherData;
import static dngsoftware.spoolid.Utils.copyFile;
import static dngsoftware.spoolid.Utils.copyFileToUri;
import static dngsoftware.spoolid.Utils.copyUriToFile;
import static dngsoftware.spoolid.Utils.createKey;
import static dngsoftware.spoolid.Utils.getDBVersion;
import static dngsoftware.spoolid.Utils.getJsonDB;
import static dngsoftware.spoolid.Utils.getMaterialBrands;
import static dngsoftware.spoolid.Utils.getMaterialPos;
import static dngsoftware.spoolid.Utils.getMaterials;
import static dngsoftware.spoolid.Utils.getMifareBlockDefinition;
import static dngsoftware.spoolid.Utils.getPositionByValue;
import static dngsoftware.spoolid.Utils.getTypeName;
import static dngsoftware.spoolid.Utils.materialWeights;
import static dngsoftware.spoolid.Utils.playBeep;
import static dngsoftware.spoolid.Utils.populateDatabase;
import static dngsoftware.spoolid.Utils.presetColors;
import static dngsoftware.spoolid.Utils.printerTypes;
import static dngsoftware.spoolid.Utils.removeFilament;
import static dngsoftware.spoolid.Utils.restartApp;
import static dngsoftware.spoolid.Utils.restorePrinterDB;
import static dngsoftware.spoolid.Utils.saveDBToPrinter;
import static dngsoftware.spoolid.Utils.setMaterialInfo;
import static dngsoftware.spoolid.Utils.setNfcLaunchMode;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONObject;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import dngsoftware.spoolid.databinding.ActivityMainBinding;
import dngsoftware.spoolid.databinding.AddDialogBinding;
import dngsoftware.spoolid.databinding.EditDialogBinding;
import dngsoftware.spoolid.databinding.ManualDialogBinding;
import dngsoftware.spoolid.databinding.PickerDialogBinding;
import dngsoftware.spoolid.databinding.SaveDialogBinding;
import dngsoftware.spoolid.databinding.TagDialogBinding;
import dngsoftware.spoolid.databinding.UpdateDialogBinding;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback, NavigationView.OnNavigationItemSelectedListener {
    private MatDB matDb;
    private filamentDB rdb;
    jsonItem[] jsonItems;
    ArrayAdapter<String> badapter, sadapter, padapter;
    ArrayAdapter<MaterialItem> madapter;
    private NfcAdapter nfcAdapter;
    Tag currentTag = null;
    int SelectedSize, SelectedBrand;
    String MaterialName, MaterialID, MaterialWeight, MaterialColor, PrinterType;
    Dialog pickerDialog, customDialog, saveDialog, updateDialog, editDialog, addDialog, tagDialog;
    AlertDialog inputDialog;
    tagAdapter tagAdapter;
    RecyclerView tagView;
    tagItem[] tagItems;
    boolean encrypted = false;
    byte[] encKey;
    private ActivityMainBinding main;
    private ManualDialogBinding manual;
    private Context context;
    Bitmap gradientBitmap;
    private ExecutorService executorService;
    private ActivityResultLauncher<Intent> exportDirectoryChooser;
    private ActivityResultLauncher<Intent> importFileChooser;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;
    private static final int ACTION_EXPORT = 1;
    private static final int ACTION_IMPORT = 2;
    private int pendingAction = -1;
    NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private PickerDialogBinding colorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        main = ActivityMainBinding.inflate(getLayoutInflater());
        View rv = main.getRoot();
        setContentView(rv);
        SetPermissions(this);

        executorService = Executors.newSingleThreadExecutor();
        setupActivityResultLaunchers();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem launchItem = navigationView.getMenu().findItem(R.id.nav_launch);
        SwitchCompat launchSwitch = Objects.requireNonNull(launchItem.getActionView()).findViewById(R.id.drawer_switch);
        launchSwitch.setChecked(GetSetting(this, "autoLaunch", true));
        launchSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNfcLaunchMode(this, isChecked);
            SaveSetting(this, "autoLaunch", isChecked);
        });

        MenuItem readItem = navigationView.getMenu().findItem(R.id.nav_read);
        SwitchCompat readSwitch = Objects.requireNonNull(readItem.getActionView()).findViewById(R.id.drawer_switch);
        readSwitch.setChecked(GetSetting(this, "autoread", false));
        readSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SaveSetting(this, "autoread", isChecked);
        });

        PrinterType = GetSetting(this, "printer", "k2");

        padapter = new ArrayAdapter<>(this, R.layout.spinner_item, printerTypes);
        main.type.setAdapter(padapter);
        main.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SaveSetting(context, "printer", Objects.requireNonNull(padapter.getItem(position)).toLowerCase());
                PrinterType = Objects.requireNonNull(padapter.getItem(position)).toLowerCase();
                setMatDb(PrinterType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        main.type.setSelection(getPositionByValue(main.type, PrinterType.toUpperCase()));

        main.colorview.setBackgroundColor(Color.argb(255, 0, 0, 255));
        MaterialColor = "0000FF";

        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                Bundle options = new Bundle();
                options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);
                nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A, options);
                if (!canMfc(this)) {
                    Toast.makeText(getApplicationContext(), R.string.this_device_does_not_support_mifare_classic_tags, Toast.LENGTH_SHORT).show();
                    main.readbutton.setEnabled(false);
                    main.writebutton.setEnabled(false);
                    main.colorspin.setEnabled(false);
                    main.spoolsize.setEnabled(false);
                    main.colorview.setEnabled(false);
                    main.colorview.setBackgroundColor(Color.parseColor("#D3D3D3"));
                    main.lbltagid.setVisibility(View.INVISIBLE);
                    main.tagid.setVisibility(View.INVISIBLE);
                    main.txtmsg.setVisibility(View.VISIBLE);
                    main.txtmsg.setText(R.string.rfid_functions_disabled);
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.please_activate_nfc, Toast.LENGTH_LONG).show();
                main.readbutton.setEnabled(false);
                main.writebutton.setEnabled(false);
                main.readbutton.setVisibility(View.INVISIBLE);
                main.writebutton.setVisibility(View.INVISIBLE);
                main.colorspin.setEnabled(false);
                main.spoolsize.setEnabled(false);
                main.colorview.setEnabled(false);
                main.colorview.setBackgroundColor(Color.parseColor("#D3D3D3"));
                main.lbltagid.setVisibility(View.INVISIBLE);
                main.tagid.setVisibility(View.INVISIBLE);
                SpannableString spannableString = new SpannableString(getString(R.string.rfid_disabled_tap_here_to_enable_nfc));
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")),  getString(R.string.rfid_disabled_tap_here_to_enable_nfc).indexOf("Tap"),
                        getString(R.string.rfid_disabled_tap_here_to_enable_nfc).indexOf("Tap")+22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                main.txtmsg.setVisibility(View.VISIBLE);
                main.txtmsg.setText(spannableString);
                main.txtmsg.setGravity(Gravity.CENTER);
                main.txtmsg.setOnClickListener(view -> {
                      startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                      finish();
                });
            }
        } catch (Exception ignored) {
        }

        main.colorview.setOnClickListener(view -> openPicker());
        main.readbutton.setOnClickListener(view -> ReadSpoolData());

        main.addbutton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            SpannableString titleText = new SpannableString("Create Filament?");
            titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
            SpannableString messageText = new SpannableString("Using " + MaterialName + " as a template");
            messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
            builder.setTitle(titleText);
            builder.setMessage(messageText);
            builder.setPositiveButton("Create", (dialog, which) -> {
                loadAdd();
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
            if (alert.getWindow() != null) {
                alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
            }
        });

        main.editbutton.setOnClickListener(view -> loadEdit());

        main.deletebutton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            SpannableString titleText = new SpannableString("Delete Filament?");
            titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
            SpannableString messageText = new SpannableString("Brand:  " + GetMaterialBrand(matDb, MaterialID) + "\nType:    " + MaterialName);
            messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
            builder.setTitle(titleText);
            builder.setMessage(messageText);
            builder.setPositiveButton("Delete", (dialog, which) -> {
                removeFilament(matDb, MaterialID);
                setMatDb(PrinterType);
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
            if (alert.getWindow() != null) {
                alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
            }
        });

        main.menubutton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        main.colorspin.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    openPicker();
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    break;
                default:
                    break;
            }
            return false;
        });

        sadapter = new ArrayAdapter<>(this, R.layout.spinner_item, materialWeights);
        main.spoolsize.setAdapter(sadapter);
        main.spoolsize.setSelection(SelectedSize);
        main.spoolsize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SelectedSize = main.spoolsize.getSelectedItemPosition();
                MaterialWeight = sadapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        ReadTagUID(getIntent());
    }


    void setMatDb(String pType) {
        try {
            if (rdb != null && rdb.isOpen()) {
                rdb.close();
            }

            rdb = filamentDB.getInstance(this, pType);
            matDb = rdb.matDB();

            if (matDb.getItemCount() == 0) {
                populateDatabase(this, matDb, null, pType);
            }

            runOnUiThread(() -> {
                main.writebutton.setOnClickListener(view -> WriteSpoolData(MaterialID, MaterialColor, GetMaterialLength(MaterialWeight)));
                badapter = new ArrayAdapter<>(this, R.layout.spinner_item, getMaterialBrands(matDb));
                main.brand.setAdapter(badapter);
                if (SelectedBrand < main.brand.getCount()) {
                    main.brand.setSelection(SelectedBrand);
                }
                main.brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        SelectedBrand = main.brand.getSelectedItemPosition();
                        setMaterial(badapter.getItem(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });

                madapter = new ArrayAdapter<>(this, R.layout.spinner_item, getMaterials(matDb, badapter.getItem(main.brand.getSelectedItemPosition())));
                main.material.setAdapter(madapter);
                main.material.setSelection(getMaterialPos(madapter, MaterialID));
                main.material.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        MaterialItem selectedItem = (MaterialItem) parentView.getItemAtPosition(position);
                        MaterialName = selectedItem.getMaterialBrand();
                        MaterialID = selectedItem.getMaterialID();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                    }
                });
            });
        } catch (Exception ignored) {}
    }


    void setMaterial(String brand) {
        madapter = new ArrayAdapter<>(this, R.layout.spinner_item, getMaterials(matDb, brand));
        main.material.setAdapter(madapter);
        main.material.setSelection(getMaterialPos(madapter, MaterialID));
        main.material.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MaterialItem selectedItem = (MaterialItem) parentView.getItemAtPosition(position);
                MaterialName = selectedItem.getMaterialBrand();
                MaterialID = selectedItem.getMaterialID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (pickerDialog != null && pickerDialog.isShowing()) {
            pickerDialog.dismiss();
        }
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        if (saveDialog != null && saveDialog.isShowing()) {
            saveDialog.dismiss();
        }
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.dismiss();
        }
        if (editDialog != null && editDialog.isShowing()) {
            editDialog.dismiss();
        }
        if (addDialog != null && addDialog.isShowing()) {
            addDialog.dismiss();
        }
        if (tagDialog != null && tagDialog.isShowing()) {
            tagDialog.dismiss();
        }
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            try {
                nfcAdapter.disableReaderMode(this);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (pickerDialog != null && pickerDialog.isShowing()) {
            pickerDialog.dismiss();
            openPicker();
        }
        if (inputDialog != null && inputDialog.isShowing()) {
            inputDialog.dismiss();
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        try {
            currentTag = tag;
            runOnUiThread(() -> {
                if (currentTag.getId().length > 4)
                {
                    Toast.makeText(getApplicationContext(), R.string.tag_not_compatible, Toast.LENGTH_SHORT).show();
                    main.tagid.setText(R.string.error);
                    return;
                }
                Toast.makeText(getApplicationContext(), getString(R.string.tag_found) + bytesToHex(currentTag.getId()), Toast.LENGTH_SHORT).show();
                main.tagid.setText(bytesToHex(currentTag.getId()));
                encKey = createKey(currentTag.getId());
                CheckTag();
                if (encrypted) {
                    main.tagid.setText(String.format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId())));
                }
                if (GetSetting(this, "autoread", false)) {
                    ReadSpoolData();
                }
            });
        } catch (Exception ignored) {
        }
    }

    void ReadTagUID(Intent intent) {
        if (intent != null) {
            try {
                if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
                    currentTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    assert currentTag != null;
                    if (currentTag.getId().length > 4)
                    {
                        Toast.makeText(getApplicationContext(), R.string.tag_not_compatible, Toast.LENGTH_SHORT).show();
                        main.tagid.setText(R.string.error);
                        return;
                    }
                    Toast.makeText(getApplicationContext(), getString(R.string.tag_found) + bytesToHex(currentTag.getId()), Toast.LENGTH_SHORT).show();
                    main.tagid.setText(bytesToHex(currentTag.getId()));
                    encKey = createKey(currentTag.getId());
                    CheckTag();
                    if (encrypted) {
                        main.tagid.setText(String.format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId())));
                    }
                    if (GetSetting(this, "autoread", false)) {
                        ReadSpoolData();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    void CheckTag() {
        if (currentTag != null) {
            MifareClassic mfc = MifareClassic.get(currentTag);
            if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
                try {
                    mfc.connect();
                    encrypted = mfc.authenticateSectorWithKeyA(1, encKey);
                    mfc.close();
                } catch (Exception ignored) {
                    Toast.makeText(getApplicationContext(), R.string.error_reading_tag, Toast.LENGTH_SHORT).show();
                    encrypted = false;
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.invalid_tag_type, Toast.LENGTH_SHORT).show();
            }
        }
    }

    String ReadTag() {
        if (currentTag != null) {
            MifareClassic mfc = MifareClassic.get(currentTag);
            if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
                try {
                    mfc.connect();
                    byte[] key = MifareClassic.KEY_DEFAULT;
                    if (encrypted) {
                        key = encKey;
                    }
                    boolean auth = mfc.authenticateSectorWithKeyA(1, key);
                    if (auth) {
                        byte[] data = new byte[48];
                        ByteBuffer buff = ByteBuffer.wrap(data);
                        buff.put(mfc.readBlock(4));
                        buff.put(mfc.readBlock(5));
                        buff.put(mfc.readBlock(6));
                        mfc.close();
                        if (encrypted) {
                            return new String(cipherData(2, buff.array()), StandardCharsets.UTF_8);
                        }
                        return new String(buff.array(), StandardCharsets.UTF_8);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                    mfc.close();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.error_reading_tag, Toast.LENGTH_SHORT).show();
                }
                try {
                    mfc.close();
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.invalid_tag_type, Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        return null;
    }

    void WriteTag(String tagData) {
        if (currentTag != null && tagData.length() == 40) {
            MifareClassic mfc = MifareClassic.get(currentTag);
            if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
                try {
                    mfc.connect();
                    byte[] key = MifareClassic.KEY_DEFAULT;
                    if (encrypted) {
                        key = encKey;
                    }
                    boolean auth = mfc.authenticateSectorWithKeyA(1, key);
                    if (auth) {
                        byte[] sectorData = cipherData(1, (tagData + "00000000").getBytes());
                        if (sectorData == null) {
                            mfc.close();
                            Toast.makeText(getApplicationContext(), R.string.failed_to_encrypt_data, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int blockIndex = 4;
                        for (int i = 0; i < sectorData.length; i += MifareClassic.BLOCK_SIZE) {
                            byte[] block = Arrays.copyOfRange(sectorData, i, i + MifareClassic.BLOCK_SIZE);
                            mfc.writeBlock(blockIndex, block);
                            blockIndex++;
                        }
                        if (!encrypted) {
                            byte[] data = mfc.readBlock(7);
                            System.arraycopy(encKey, 0, data, 0, encKey.length);
                            System.arraycopy(encKey, 0, data, 10, encKey.length);
                            mfc.writeBlock(7, data);
                            encrypted = true;
                            main.tagid.setText(String.format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId())));
                        }
                        playBeep();
                        Toast.makeText(getApplicationContext(), R.string.data_written_to_tag, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                    mfc.close();
                } catch (Exception ignored) {
                    Toast.makeText(getApplicationContext(), R.string.error_writing_to_tag, Toast.LENGTH_SHORT).show();
                }
                try {
                    mfc.close();
                } catch (Exception ignored) {
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.invalid_tag_type, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_writing_to_tag, Toast.LENGTH_SHORT).show();
        }
    }

    void FormatTag() {
        if (currentTag != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            SpannableString titleText = new SpannableString(getString(R.string.format_tag_q));
            titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
            SpannableString messageText = new SpannableString(getString(R.string.erase_message));
            messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
            builder.setTitle(titleText);
            builder.setMessage(messageText);
            builder.setPositiveButton(R.string.format, (dialog, which) -> {
                MifareClassic mfc = MifareClassic.get(currentTag);
                if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
                    try {
                        mfc.connect();
                        byte[] key = MifareClassic.KEY_DEFAULT;
                        if (encrypted) {
                            key = encKey;
                        }
                        boolean auth = mfc.authenticateSectorWithKeyA(1, key);
                        if (auth) {
                            byte[] sectorData = new byte[48];
                            Arrays.fill(sectorData, (byte) 0);
                            int blockIndex = 4;
                            for (int i = 0; i < sectorData.length; i += MifareClassic.BLOCK_SIZE) {
                                byte[] block = Arrays.copyOfRange(sectorData, i, i + MifareClassic.BLOCK_SIZE);
                                mfc.writeBlock(blockIndex, block);
                                blockIndex++;
                            }
                            if (encrypted) {
                                byte[] data = mfc.readBlock(7);
                                System.arraycopy(MifareClassic.KEY_DEFAULT, 0, data, 0, MifareClassic.KEY_DEFAULT.length);
                                System.arraycopy(MifareClassic.KEY_DEFAULT, 0, data, 10, MifareClassic.KEY_DEFAULT.length);
                                mfc.writeBlock(7, data);
                                encrypted = false;
                                main.tagid.setText(bytesToHex(currentTag.getId()));
                            }
                            playBeep();
                            Toast.makeText(getApplicationContext(), R.string.tag_formatted, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                        }
                        mfc.close();
                    } catch (Exception ignored) {
                        Toast.makeText(getApplicationContext(), R.string.error_formatting_tag, Toast.LENGTH_SHORT).show();
                    }
                    try {
                        mfc.close();
                    } catch (Exception ignored) {
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.invalid_tag_type, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
            if (alert.getWindow() != null) {
                alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_tag_found, Toast.LENGTH_SHORT).show();
        }
    }

    void ReadSpoolData() {
        final Handler handler = new Handler(Looper.getMainLooper());
        String tagData = ReadTag();
        if (tagData != null && tagData.length() >= 40) {
            String MaterialId = tagData.substring(12, 17);
            if (GetMaterialName(matDb, MaterialId) != null) {
                MaterialColor = tagData.substring(18, 24);
                String Length = tagData.substring(24, 28);
                main.colorview.setBackgroundColor(Color.parseColor("#" + MaterialColor));
                MaterialName = Objects.requireNonNull(GetMaterialName(matDb, MaterialId))[0];
                main.brand.setSelection(badapter.getPosition(Objects.requireNonNull(GetMaterialName(matDb, MaterialId))[1]));
                handler.postDelayed(() -> main.material.setSelection(getMaterialPos(madapter, MaterialId)), 200);
                main.spoolsize.setSelection(sadapter.getPosition(GetMaterialWeight(Length)));
                Toast.makeText(getApplicationContext(), R.string.data_read_from_tag, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.unknown_or_empty_tag, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_reading_tag, Toast.LENGTH_SHORT).show();
        }
    }

    void WriteSpoolData(String MaterialID, String Color, String Length) {
        //SecureRandom random = new SecureRandom();
        String filamentId = "1" + MaterialID; //material_database.json
        String vendorId = "0276"; //0276 creality
        String color = "0" + Color;
        String serialNum = "000001"; //format(Locale.getDefault(), "%06d", random.nextInt(900000));
        String reserve = "000000";
        WriteTag("AB124" + vendorId + "A2" + filamentId + color + Length + serialNum + reserve);
    }


    @SuppressLint("ClickableViewAccessibility")
    void openPicker() {
        try {
            pickerDialog = new Dialog(this, R.style.Theme_SpoolID);
            pickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pickerDialog.setCanceledOnTouchOutside(false);
            pickerDialog.setTitle(R.string.pick_color);
            PickerDialogBinding dl = PickerDialogBinding.inflate(getLayoutInflater());
            View rv = dl.getRoot();
            colorDialog = dl;

            pickerDialog.setContentView(rv);
            gradientBitmap = null;

            dl.btncls.setOnClickListener(v -> {
                MaterialColor = dl.txtcolor.getText().toString();
                if (customDialog != null && customDialog.isShowing()) {
                    manual.txtcolor.setText(String.format("0%s", MaterialColor));
                }else {
                    if (dl.txtcolor.getText().toString().length() == 6) {
                        try {
                            int color = Color.rgb(dl.redSlider.getProgress(), dl.greenSlider.getProgress(), dl.blueSlider.getProgress());
                            main.colorview.setBackgroundColor(color);
                        } catch (Exception ignored) {
                        }
                    }
                }
                pickerDialog.dismiss();
            });

            dl.redSlider.setProgress(Color.red(Color.parseColor("#" + MaterialColor)));
            dl.greenSlider.setProgress(Color.green(Color.parseColor("#" + MaterialColor)));
            dl.blueSlider.setProgress(Color.blue(Color.parseColor("#" + MaterialColor)));


            setupPresetColors(dl);
            updateColorDisplay(dl, dl.redSlider.getProgress(), dl.greenSlider.getProgress(), dl.blueSlider.getProgress());
            setupGradientPicker(dl);

            dl.gradientPickerView.setOnTouchListener((v, event) -> {
                v.performClick();
                if (gradientBitmap == null) {
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    float touchX = event.getX();
                    float touchY = event.getY();
                    int pixelX = Math.max(0, Math.min(gradientBitmap.getWidth() - 1, (int) touchX));
                    int pixelY = Math.max(0, Math.min(gradientBitmap.getHeight() - 1, (int) touchY));
                    int pickedColor = gradientBitmap.getPixel(pixelX, pixelY);
                    setSlidersFromColor(dl, Color.argb(255, Color.red(pickedColor), Color.green(pickedColor), Color.blue(pickedColor)));
                    return true;
                }
                return false;
            });

            setupCollapsibleSection(dl,
                    dl.rgbSlidersHeader,
                    dl.rgbSlidersContent,
                    dl.rgbSlidersToggleIcon,
                    GetSetting(this, "RGB_VIEW", false)
            );
            setupCollapsibleSection(dl,
                    dl.gradientPickerHeader,
                    dl.gradientPickerContent,
                    dl.gradientPickerToggleIcon,
                    GetSetting(this, "PICKER_VIEW", true)
            );
            setupCollapsibleSection(dl,
                    dl.presetColorsHeader,
                    dl.presetColorsContent,
                    dl.presetColorsToggleIcon,
                    GetSetting(this, "PRESET_VIEW", true)
            );
            setupCollapsibleSection(dl,
                    dl.photoColorHeader,
                    dl.photoColorContent,
                    dl.photoColorToggleIcon,
                    GetSetting(this, "PHOTO_VIEW", false)
            );


            SeekBar.OnSeekBarChangeListener rgbChangeListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateColorDisplay(dl, dl.redSlider.getProgress(), dl.greenSlider.getProgress(), dl.blueSlider.getProgress());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

            dl.redSlider.setOnSeekBarChangeListener(rgbChangeListener);
            dl.greenSlider.setOnSeekBarChangeListener(rgbChangeListener);
            dl.blueSlider.setOnSeekBarChangeListener(rgbChangeListener);

            dl.txtcolor.setOnClickListener(v -> showHexInputDialog(dl));

            dl.photoImage.setOnClickListener(v -> {
                Drawable drawable = ContextCompat.getDrawable(dl.photoImage.getContext(), R.drawable.camera);
                if (dl.photoImage.getDrawable() != null && drawable != null) {
                    if (Objects.equals(dl.photoImage.getDrawable().getConstantState(), drawable.getConstantState())) {
                        checkPermissionsAndCapture();
                    }
                } else {
                    checkPermissionsAndCapture();
                }
            });

            dl.clearImage.setOnClickListener(v -> {

                dl.photoImage.setImageResource( R.drawable.camera);
                dl.photoImage.setDrawingCacheEnabled(false);
                dl.photoImage.buildDrawingCache(false);
                dl.photoImage.setOnTouchListener(null);
                dl.clearImage.setVisibility(View.GONE);

            });



            pickerDialog.show();
        } catch (Exception ignored) {}
    }

    void openCustom() {
        try {
            customDialog = new Dialog(this, R.style.Theme_SpoolID);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setCanceledOnTouchOutside(false);
            customDialog.setTitle(R.string.custom_tag_data);
            manual = ManualDialogBinding.inflate(getLayoutInflater());
            View rv = manual.getRoot();
            customDialog.setContentView(rv);
            manual.txtmonth.setText(GetSetting(this, "mon", getResources().getString(R.string.def_mon)));
            manual.txtday.setText(GetSetting(this, "day", getResources().getString(R.string.def_day)));
            manual.txtyear.setText(GetSetting(this, "yr", getResources().getString(R.string.def_yr)));
            manual.txtvendor.setText(GetSetting(this, "ven", getResources().getString(R.string.def_ven)));
            manual.txtbatch.setText(GetSetting(this, "bat", getResources().getString(R.string.def_bat)));
            manual.txtmaterial.setText(GetSetting(this, "mat", getResources().getString(R.string.def_mat)));
            manual.txtcolor.setText(GetSetting(this, "col", getResources().getString(R.string.def_col)));
            manual.txtlength.setText(GetSetting(this, "len", getResources().getString(R.string.def_len)));
            manual.txtserial.setText(GetSetting(this, "ser", getResources().getString(R.string.def_ser)));
            manual.txtreserve.setText(GetSetting(this, "res", getResources().getString(R.string.def_res)));
            manual.btncls.setOnClickListener(v -> customDialog.dismiss());
            manual.btncol.setOnClickListener(view -> openPicker());
            manual.btnrnd.setOnClickListener(v -> {
                SecureRandom random = new SecureRandom();
                manual.txtserial.setText(format(Locale.getDefault(), "%06d", random.nextInt(900000)));
            });
            manual.btnread.setOnClickListener(v -> {
                String tagData = ReadTag();
                if (tagData != null && tagData.length() >= 40) {
                    if (!tagData.startsWith("\0")) {
                        manual.txtmonth.setText(tagData.substring(0, 1).toUpperCase());
                        manual.txtday.setText(tagData.substring(1, 3).toUpperCase());
                        manual.txtyear.setText(tagData.substring(3, 5).toUpperCase());
                        manual.txtvendor.setText(tagData.substring(5, 9).toUpperCase());
                        manual.txtbatch.setText(tagData.substring(9, 11).toUpperCase());
                        manual.txtmaterial.setText(tagData.substring(11, 17).toUpperCase());
                        manual.txtcolor.setText(tagData.substring(17, 24).toUpperCase());
                        manual.txtlength.setText(tagData.substring(24, 28).toUpperCase());
                        manual.txtserial.setText(tagData.substring(28, 34).toUpperCase());
                        manual.txtreserve.setText(tagData.substring(34, 40).toUpperCase());
                        Toast.makeText(getApplicationContext(), R.string.data_read_from_tag, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.unknown_or_empty_tag, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_reading_tag, Toast.LENGTH_SHORT).show();
                }
            });
            manual.btnwrite.setOnClickListener(v -> {
                if (manual.txtmonth.getText().length() == 1 && manual.txtday.getText().length() == 2 && manual.txtyear.getText().length() == 2
                        && manual.txtvendor.getText().length() == 4 && manual.txtbatch.getText().length() == 2 && manual.txtmaterial.getText().length() == 6
                        && manual.txtcolor.getText().length() == 7 && manual.txtlength.getText().length() == 4
                        && manual.txtserial.getText().length() == 6 && manual.txtreserve.getText().length() == 6) {
                    WriteTag(manual.txtmonth.getText().toString() + manual.txtday.getText().toString() + manual.txtyear.getText().toString()
                            + manual.txtvendor.getText().toString() + manual.txtbatch.getText().toString() + manual.txtmaterial.getText().toString() + manual.txtcolor.getText().toString()
                            + manual.txtlength.getText().toString() + manual.txtserial.getText().toString() + manual.txtreserve.getText().toString());
                    SaveSetting(this, "mon", manual.txtmonth.getText().toString().toUpperCase());
                    SaveSetting(this, "day", manual.txtday.getText().toString().toUpperCase());
                    SaveSetting(this, "yr", manual.txtyear.getText().toString().toUpperCase());
                    SaveSetting(this, "ven", manual.txtvendor.getText().toString().toUpperCase());
                    SaveSetting(this, "bat", manual.txtbatch.getText().toString().toUpperCase());
                    SaveSetting(this, "mat", manual.txtmaterial.getText().toString().toUpperCase());
                    SaveSetting(this, "col", manual.txtcolor.getText().toString().toUpperCase());
                    SaveSetting(this, "len", manual.txtlength.getText().toString().toUpperCase());
                    SaveSetting(this, "ser", manual.txtserial.getText().toString().toUpperCase());
                    SaveSetting(this, "res", manual.txtreserve.getText().toString().toUpperCase());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.incorrect_tag_data_length, Toast.LENGTH_SHORT).show();
                }
            });
            manual.btnfmt.setOnClickListener(v -> FormatTag());
            manual.btnrst.setOnClickListener(v -> {
                manual.txtmonth.setText(R.string.def_mon);
                manual.txtday.setText(R.string.def_day);
                manual.txtyear.setText(R.string.def_yr);
                manual.txtvendor.setText(R.string.def_ven);
                manual.txtbatch.setText(R.string.def_bat);
                manual.txtmaterial.setText(R.string.def_mat);
                manual.txtcolor.setText(R.string.def_col);
                manual.txtlength.setText(R.string.def_len);
                manual.txtserial.setText(R.string.def_ser);
                manual.txtreserve.setText(R.string.def_res);
                SaveSetting(this, "mon", manual.txtmonth.getText().toString().toUpperCase());
                SaveSetting(this, "day", manual.txtday.getText().toString().toUpperCase());
                SaveSetting(this, "yr", manual.txtyear.getText().toString().toUpperCase());
                SaveSetting(this, "ven", manual.txtvendor.getText().toString().toUpperCase());
                SaveSetting(this, "bat", manual.txtbatch.getText().toString().toUpperCase());
                SaveSetting(this, "mat", manual.txtmaterial.getText().toString().toUpperCase());
                SaveSetting(this, "col", manual.txtcolor.getText().toString().toUpperCase());
                SaveSetting(this, "len", manual.txtlength.getText().toString().toUpperCase());
                SaveSetting(this, "ser", manual.txtserial.getText().toString().toUpperCase());
                SaveSetting(this, "res", manual.txtreserve.getText().toString().toUpperCase());
                Toast.makeText(getApplicationContext(), R.string.values_reset, Toast.LENGTH_SHORT).show();
            });
            customDialog.show();
        } catch (Exception ignored) {
        }
    }

    void openUpdate() {
        try {
            updateDialog = new Dialog(this, R.style.Theme_SpoolID);
            updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            updateDialog.setCanceledOnTouchOutside(false);
            updateDialog.setTitle(R.string.update);
            UpdateDialogBinding dl = UpdateDialogBinding.inflate(getLayoutInflater());
            View rv = dl.getRoot();
            updateDialog.setContentView(rv);

            SpannableString spannableString = new SpannableString(String.format(Locale.getDefault(), getString(R.string.update_desc_printer), PrinterType.toUpperCase()));
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 41, 43, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dl.chkprnt.setChecked(GetSetting(this, "fromprinter_" + PrinterType, false));
            dl.chkprnt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SaveSetting(this, "fromprinter_" + PrinterType, isChecked);
                if (isChecked) {
                    dl.txtaddress.setVisibility(View.VISIBLE);
                    dl.txtpsw.setVisibility(View.VISIBLE);
                    dl.lblpip.setVisibility(View.VISIBLE);
                    dl.lblpsw.setVisibility(View.VISIBLE);
                    dl.updatedesc.setText(spannableString);
                } else {
                    dl.txtaddress.setVisibility(View.INVISIBLE);
                    dl.txtpsw.setVisibility(View.INVISIBLE);
                    dl.lblpip.setVisibility(View.INVISIBLE);
                    dl.lblpsw.setVisibility(View.INVISIBLE);
                    dl.updatedesc.setText(getString(R.string.update_desc));
                }
                dl.btnupd.setVisibility(View.INVISIBLE);
                dl.txtmsg.setText("");
                dl.txtnewver.setText("");
            });

            if (dl.chkprnt.isChecked()) {
                dl.txtaddress.setVisibility(View.VISIBLE);
                dl.txtpsw.setVisibility(View.VISIBLE);
                dl.lblpip.setVisibility(View.VISIBLE);
                dl.lblpsw.setVisibility(View.VISIBLE);
                dl.updatedesc.setText(spannableString);
            } else {
                dl.txtaddress.setVisibility(View.INVISIBLE);
                dl.txtpsw.setVisibility(View.INVISIBLE);
                dl.lblpip.setVisibility(View.INVISIBLE);
                dl.lblpsw.setVisibility(View.INVISIBLE);
                dl.updatedesc.setText(getString(R.string.update_desc));
            }

            String sshDefault;
            if (PrinterType.equalsIgnoreCase("hi")) {
                sshDefault = "Creality2024";
            } else if (PrinterType.equalsIgnoreCase("k1")) {
                sshDefault = "creality_2023";
            } else {
                sshDefault = "creality_2024";
            }

            dl.txtpsw.setText(GetSetting(this, "psw_" + PrinterType, sshDefault));
            dl.txtaddress.setText(GetSetting(this, "host_" + PrinterType, ""));
            dl.btncls.setOnClickListener(v -> updateDialog.dismiss());
            dl.btnupd.setVisibility(View.INVISIBLE);
            dl.txtcurver.setText(String.format(Locale.getDefault(), getString(R.string.current_version), GetSetting(this, "version_" + PrinterType, -1L)));
            dl.txtprinter.setText(String.format(getString(R.string.creality_type), PrinterType.toUpperCase()));

            dl.btnchk.setOnClickListener(v -> {
                String host = dl.txtaddress.getText().toString();
                String psw = dl.txtpsw.getText().toString();
                dl.txtmsg.setTextColor(getResources().getColor(R.color.text_color));
                dl.txtmsg.setText(R.string.checking_for_updates);
                long version = GetSetting(this, "version_" + PrinterType, -1L);
                dl.txtcurver.setText(String.format(Locale.getDefault(), getString(R.string.current_version), version));
                new Thread(() -> {
                    try {
                        String json;
                        if (GetSetting(this, "fromprinter_" + PrinterType, false)) {
                            SaveSetting(this, "host_" + PrinterType, host);
                            SaveSetting(this, "psw_" + PrinterType, psw);

                            if (host.isEmpty()) {
                                runOnUiThread(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_printer_ip_address);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            if (psw.isEmpty()) {
                                runOnUiThread(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_ssh_password);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            json = getJsonDB(psw, host, PrinterType, "material_database.json");
                        } else {
                            json = getJsonDB(PrinterType, false);
                        }
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            runOnUiThread(() -> {
                                dl.txtnewver.setText(format(Locale.getDefault(), getString(R.string.printer_version), newVer));
                                if (newVer > version) {
                                    dl.btnupd.setVisibility(View.VISIBLE);
                                    dl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                                    dl.txtmsg.setText(R.string.update_available);
                                } else {
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                                    dl.txtmsg.setText(R.string.no_update_available);
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                dl.txtmsg.setTextColor(Color.RED);
                                dl.txtmsg.setText(R.string.unable_to_download_file_from_printer);
                            });
                        }
                    } catch (Exception ignored) {
                    }
                }).start();
            });

            dl.btnupd.setOnClickListener(v -> {
                String host = GetSetting(this, "host_" + PrinterType, "");
                String psw = GetSetting(this, "psw_" + PrinterType, sshDefault);
                dl.txtmsg.setTextColor(getResources().getColor(R.color.text_color));
                dl.txtmsg.setText(R.string.downloading_update);
                final Handler handler = new Handler(Looper.getMainLooper());
                new Thread(() -> {
                    try {
                        String json;
                        if (GetSetting(this, "fromprinter_" + PrinterType, false)) {
                            if (host.isEmpty()) {
                                runOnUiThread(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_printer_ip_address);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            if (psw.isEmpty()) {
                                runOnUiThread(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_ssh_password);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            json = getJsonDB(psw, host, PrinterType, "material_database.json");
                        } else {
                            json = getJsonDB(PrinterType, false);
                        }
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            matDb.deleteAll();
                            populateDatabase(this, matDb, json, PrinterType);
                            SaveSetting(this, "version_" + PrinterType, newVer);
                            runOnUiThread(() -> {
                                dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), newVer));
                                dl.btnupd.setVisibility(View.INVISIBLE);
                                dl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                                dl.txtmsg.setText(R.string.update_successful);
                                handler.postDelayed(() -> restartApp(context), 2000);
                            });
                        } else {
                            runOnUiThread(() -> {
                                dl.txtmsg.setTextColor(Color.RED);
                                dl.txtmsg.setText(R.string.unable_to_download_file_from_printer);
                            });
                        }
                    } catch (Exception ignored) {
                    }
                }).start();
            });
            updateDialog.show();
        } catch (Exception ignored) {
        }
    }


    void loadEdit() {
        try {
            RecyclerView recyclerView;
            jsonAdapter recycleAdapter;
            editDialog = new Dialog(this, R.style.Theme_SpoolID);
            editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            editDialog.setCanceledOnTouchOutside(false);
            editDialog.setTitle(R.string.filament_info);
            EditDialogBinding edl = EditDialogBinding.inflate(getLayoutInflater());
            View rv = edl.getRoot();
            editDialog.setContentView(rv);
            edl.btncls.setOnClickListener(v -> editDialog.dismiss());

            edl.btnsave.setOnClickListener(v -> {
                try {
                    JSONObject info = new JSONObject(GetMaterialInfo(matDb, MaterialID));
                    JSONObject param = info.getJSONObject("kvParam");
                    for (jsonItem jsonItem : jsonItems) {
                        param.put(jsonItem.jKey, jsonItem.jValue);
                    }
                    setMaterialInfo(matDb, MaterialID, info.toString());
                } catch (Exception ignored) {
                }
                editDialog.dismiss();
            });

            edl.lbldesc.setText(MaterialName);
            recyclerView = edl.recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.scrollToPosition(0);
            recyclerView.setLayoutManager(layoutManager);

            JSONObject info = new JSONObject(GetMaterialInfo(matDb, MaterialID));
            JSONObject param = info.getJSONObject("kvParam");
            jsonItems = new jsonItem[param.length()];
            int i = 0;
            for (Iterator<String> it = param.keys(); it.hasNext(); ) {
                String key = it.next();
                jsonItems[i] = new jsonItem();
                jsonItems[i].jKey = key;
                jsonItems[i].jValue = param.get(key);
                i++;
            }

            recycleAdapter = new jsonAdapter(getBaseContext(), jsonItems);
            recycleAdapter.setHasStableIds(true);
            runOnUiThread(() -> {
                recyclerView.removeAllViewsInLayout();
                recyclerView.setAdapter(null);
                recyclerView.setAdapter(recycleAdapter);
            });
            editDialog.show();
        } catch (Exception ignored) {
        }
    }


    void loadAdd() {
        try {
            RecyclerView recyclerView;
            jsonAdapter recycleAdapter;
            addDialog = new Dialog(this, R.style.Theme_SpoolID);
            addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            addDialog.setCanceledOnTouchOutside(false);
            addDialog.setTitle(R.string.filament_info);
            AddDialogBinding adl = AddDialogBinding.inflate(getLayoutInflater());
            View rv = adl.getRoot();
            addDialog.setContentView(rv);
            adl.btncls.setOnClickListener(v -> addDialog.dismiss());

            adl.btnadd.setOnClickListener(v -> {
                try {
                    int maxTemp = 0, minTemp = 0;
                    JSONObject info = new JSONObject(GetMaterialInfo(matDb, MaterialID));
                    JSONObject base = info.getJSONObject("base");
                    JSONObject kvParam = info.getJSONObject("kvParam");

                    for (jsonItem jsonItem : jsonItems) {
                        Object jsonValue = jsonItem.jValue;
                        if (jsonItem.jKey.equalsIgnoreCase("meterialtype"))
                        {
                            kvParam.put("filament_type", jsonItem.jValue);
                        }
                        else if (jsonItem.jKey.equalsIgnoreCase("brand"))
                        {
                            kvParam.put("filament_vendor", jsonItem.jValue);
                        }
                        else if (jsonItem.jKey.equalsIgnoreCase("maxTemp"))
                        {
                            if (jsonValue instanceof String) {
                                maxTemp = Integer.parseInt((String) jsonValue);
                                kvParam.put("nozzle_temperature_range_high", jsonItem.jValue);
                            } else if (jsonValue instanceof Integer) {
                                maxTemp = (Integer) jsonValue;
                                kvParam.put("nozzle_temperature_range_high", String.valueOf(jsonItem.jValue));
                            }
                        }
                        else if (jsonItem.jKey.equalsIgnoreCase("minTemp"))
                        {
                            if (jsonValue instanceof String) {
                                minTemp = Integer.parseInt((String) jsonValue);
                                kvParam.put("nozzle_temperature_range_low", jsonItem.jValue);
                            } else if (jsonValue instanceof Integer) {
                                minTemp = (Integer) jsonValue;
                                kvParam.put("nozzle_temperature_range_low", String.valueOf(jsonItem.jValue));
                            }
                        }
                        else if (jsonItem.jKey.equalsIgnoreCase("isSoluble"))
                        {
                            kvParam.put("filament_soluble", String.valueOf(Boolean.parseBoolean((String) jsonItem.jValue) ? 1 : 0));
                        }
                        else if (jsonItem.jKey.equalsIgnoreCase("isSupport"))
                        {
                            kvParam.put("filament_is_support", String.valueOf(Boolean.parseBoolean((String) jsonItem.jValue) ? 1 : 0));
                        }

                        if (jsonItem.jKey.equalsIgnoreCase("brand") || jsonItem.jKey.equalsIgnoreCase("name")
                                || jsonItem.jKey.equalsIgnoreCase("meterialtype") || jsonItem.jKey.equalsIgnoreCase("colors")
                                || jsonItem.jKey.equalsIgnoreCase("id")) {
                            base.put(jsonItem.jKey, jsonItem.jValue);
                        }
                        else {
                            if (jsonItem.jValue instanceof Number) {
                                Number num = (Number) jsonItem.jValue;
                                if (num instanceof Float || num instanceof Double) {
                                    base.put(jsonItem.jKey, jsonItem.jValue);
                                } else if (num instanceof Integer || num instanceof Long || num instanceof Short || num instanceof Byte) {
                                    base.put(jsonItem.jKey, num);
                                } else {
                                    base.put(jsonItem.jKey, jsonItem.jValue);
                                }
                            } else if (jsonItem.jValue instanceof String) {
                                String stringValue = (String) jsonItem.jValue;
                                try {
                                    if (stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("true")) {
                                        boolean booleanValue = Boolean.parseBoolean(stringValue);
                                        base.put(jsonItem.jKey, booleanValue);
                                    } else if (stringValue.contains(".") || stringValue.contains("e") || stringValue.contains("E")) {
                                        base.put(jsonItem.jKey, jsonItem.jValue);
                                    } else {
                                        int intValue = Integer.parseInt(stringValue);
                                        base.put(jsonItem.jKey, intValue);
                                    }
                                } catch (Exception ignored) {
                                    base.put(jsonItem.jKey, jsonItem.jValue);
                                }
                            } else if (jsonItem.jValue instanceof Boolean) {
                                boolean booleanValue = (Boolean) jsonItem.jValue;
                                base.put(jsonItem.jKey, booleanValue);
                            } else {
                                base.put(jsonItem.jKey, jsonItem.jValue);
                            }
                        }
                    }

                    if (minTemp > 0 && maxTemp > 0) {
                        kvParam.put("nozzle_temperature", String.valueOf((minTemp + maxTemp) / 2));
                        kvParam.put("nozzle_temperature_initial_layer", String.valueOf((minTemp + maxTemp) / 2));
                    }

                    if (GetMaterialName(matDb, base.get("id").toString()) != null) {
                        Toast.makeText(getApplicationContext(), "ID: " + base.get("id") + " already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (base.get("id").toString().isBlank() || base.get("id").toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "ID cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (base.get("id").toString().length() != 5) {
                        Toast.makeText(getApplicationContext(), "ID must be 5 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (base.get("brand").toString().isBlank() || base.get("brand").toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Brand cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (base.get("name").toString().isBlank() || base.get("name").toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (base.get("meterialType").toString().isBlank() || base.get("meterialType").toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "MeterialType cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    info.put("base", base);
                    addFilament(matDb, info);
                    setMatDb(PrinterType);
                } catch (Exception ignored) {}
                addDialog.dismiss();
            });

            recyclerView = adl.recyclerView;

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
            layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager1.scrollToPosition(0);
            recyclerView.setLayoutManager(layoutManager1);

            JSONObject info = new JSONObject(GetMaterialInfo(matDb, MaterialID));
            JSONObject base = info.getJSONObject("base");

            jsonItems = new jsonItem[base.length()];
            int i = 0;
            for (Iterator<String> it = base.keys(); it.hasNext(); ) {
                String key = it.next();
                jsonItems[i] = new jsonItem();
                jsonItems[i].jKey = key;
                jsonItems[i].jValue = base.get(key);
                jsonItems[i].hintValue = base.get(key).toString();
                i++;
            }
            recycleAdapter = new jsonAdapter(getBaseContext(), jsonItems);
            recycleAdapter.setHasStableIds(true);

            runOnUiThread(() -> {
                recyclerView.removeAllViewsInLayout();
                recyclerView.setAdapter(null);
                recyclerView.setAdapter(recycleAdapter);
            });

            addDialog.show();
        } catch (Exception ignored) {}
    }


    void openUpload() {
        try {
            saveDialog = new Dialog(this, R.style.Theme_SpoolID);
            saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            saveDialog.setCanceledOnTouchOutside(false);
            saveDialog.setTitle("Upload to Printer");
            SaveDialogBinding sdl = SaveDialogBinding.inflate(getLayoutInflater());
            View rv = sdl.getRoot();
            saveDialog.setContentView(rv);
            SpannableStringBuilder spannableString = new SpannableStringBuilder(String.format(Locale.getDefault(), getString(R.string.upload_desc_printer), PrinterType.toUpperCase()));
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 41, 43, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sdl.updatedesc.setText(spannableString);

            sdl.chkprevent.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(this, "prevent_" + PrinterType, isChecked));
            sdl.chkprevent.setChecked(GetSetting(this, "prevent_" + PrinterType, true));

            sdl.chkreboot.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(this, "reboot_" + PrinterType, isChecked));
            sdl.chkreboot.setChecked(GetSetting(this, "reboot_" + PrinterType, true));

            sdl.chkreset.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SpannableStringBuilder ss = new SpannableStringBuilder(spannableString);
                if (isChecked) {
                    sdl.btnupload.setText(R.string.reset);
                    sdl.chkreboot.setVisibility(View.INVISIBLE);
                    sdl.chkprevent.setVisibility(View.INVISIBLE);
                    sdl.chkresetapp.setVisibility(View.VISIBLE);
                    sdl.updatedesc.setText(ss.replace(72, 78, "reset"));
                } else {
                    sdl.btnupload.setText(R.string.upload);
                    sdl.chkreboot.setVisibility(View.VISIBLE);
                    sdl.chkprevent.setVisibility(View.VISIBLE);
                    sdl.chkresetapp.setVisibility(View.INVISIBLE);
                    sdl.updatedesc.setText(ss.replace(72, 78, "update"));
                }
            });

            String sshDefault;
            if (PrinterType.equalsIgnoreCase("hi")) {
                sshDefault = "Creality2024";
            } else if (PrinterType.equalsIgnoreCase("k1")) {
                sshDefault = "creality_2023";
            } else {
                sshDefault = "creality_2024";
            }
            sdl.txtpsw.setText(GetSetting(this, "psw_" + PrinterType, sshDefault));
            sdl.txtaddress.setText(GetSetting(this, "host_" + PrinterType, ""));

            sdl.btncls.setOnClickListener(v -> saveDialog.dismiss());

            sdl.btnupload.setOnClickListener(v -> {
                String host = sdl.txtaddress.getText().toString();
                String psw = sdl.txtpsw.getText().toString();
                SaveSetting(this, "host_" + PrinterType, host);
                SaveSetting(this, "psw_" + PrinterType, psw);
                boolean reboot = sdl.chkreboot.isChecked();
                boolean resetapp = sdl.chkresetapp.isChecked();
                sdl.txtmsg.setTextColor(getResources().getColor(R.color.text_color));
                if (sdl.chkreset.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    SpannableString titleText = new SpannableString("Warning!");
                    titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
                    SpannableString messageText;
                    if (resetapp) {
                        messageText = new SpannableString("This will restore the default printer and app databases");
                    } else {
                        messageText = new SpannableString("This will restore the default printer database");
                    }
                    messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
                    builder.setTitle(titleText);
                    builder.setMessage(messageText);
                    builder.setPositiveButton("Reset", (dialog, which) -> {
                        sdl.txtmsg.setText(R.string.resetting);
                        new Thread(() -> {
                            try {
                                if (resetapp) {
                                    matDb.deleteAll();
                                    populateDatabase(this, matDb, null, PrinterType);
                                    runOnUiThread(() -> setMatDb(PrinterType));
                                }
                                restorePrinterDB(this, psw, host, PrinterType);
                                runOnUiThread(() -> {
                                    sdl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                                    if (resetapp) {
                                        sdl.txtmsg.setText(R.string.printer_and_app_databases_have_been_reset);
                                    } else {
                                        sdl.txtmsg.setText(R.string.printer_database_has_been_reset);
                                    }
                                });

                            } catch (Exception ignored) {
                                runOnUiThread(() -> {
                                    sdl.txtmsg.setTextColor(Color.RED);
                                    sdl.txtmsg.setText(R.string.error_resetting_database);
                                });
                            }
                        }).start();
                        dialog.dismiss();
                    });
                    builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                    if (alert.getWindow() != null) {
                        alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
                    }
                    return;
                }

                final String version;
                if (sdl.chkprevent.isChecked()) {
                    version = "9876543210";
                } else {
                    version = String.format("%s", GetSetting(this, "version_" + PrinterType, -1L));
                }
                new Thread(() -> {
                    try {
                        if (host.isEmpty()) {
                            runOnUiThread(() -> {
                                sdl.txtmsg.setTextColor(Color.RED);
                                sdl.txtmsg.setText(R.string.please_enter_printer_ip_address);
                            });
                            return;
                        }
                        if (psw.isEmpty()) {
                            runOnUiThread(() -> {
                                sdl.txtmsg.setTextColor(Color.RED);
                                sdl.txtmsg.setText(R.string.please_enter_ssh_password);
                            });
                            return;
                        }
                        runOnUiThread(() -> sdl.txtmsg.setText(R.string.uploading));
                        saveDBToPrinter(matDb, psw, host, PrinterType, version, reboot);
                        runOnUiThread(() -> {
                            sdl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                            sdl.txtmsg.setText(R.string.upload_successful);
                        });

                    } catch (Exception ignored) {

                        runOnUiThread(() -> {
                            sdl.txtmsg.setTextColor(Color.RED);
                            sdl.txtmsg.setText(R.string.error_uploading_to_printer);
                        });

                    }
                }).start();
            });
            saveDialog.show();
        } catch (Exception ignored) {
        }
    }

    private void updateColorDisplay(PickerDialogBinding dl,int currentRed,int currentGreen,int currentBlue) {
        int color = Color.rgb(currentRed, currentGreen, currentBlue);
        dl.colorDisplay.setBackgroundColor(color);
        String hexCode = rgbToHex(currentRed, currentGreen, currentBlue);
        dl.txtcolor.setText(hexCode);
        double alphaNormalized = 255.0;
        int blendedRed = (int) (currentRed * alphaNormalized + 244 * (1 - alphaNormalized));
        int blendedGreen = (int) (currentGreen * alphaNormalized + 244 * (1 - alphaNormalized));
        int blendedBlue = (int) (currentBlue * alphaNormalized + 244 * (1 - alphaNormalized));
        double brightness = (0.299 * blendedRed + 0.587 * blendedGreen + 0.114 * blendedBlue) / 255;
        if (brightness > 0.5) {
            dl.txtcolor.setTextColor(Color.BLACK);
        } else {
            dl.txtcolor.setTextColor(Color.WHITE);
        }

    }

    private String rgbToHex(int r, int g, int b) {
        return String.format("%02X%02X%02X", r, g, b);
    }

    private void setupPresetColors(PickerDialogBinding dl) {
        dl.presetColorGrid.removeAllViews();
        for (int color : presetColors()) {
            Button colorButton = new Button(this);
            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.preset_circle_size),
                    (int) getResources().getDimension(R.dimen.preset_circle_size)
            );
            params.setMargins(
                    (int) getResources().getDimension(R.dimen.preset_circle_margin),
                    (int) getResources().getDimension(R.dimen.preset_circle_margin),
                    (int) getResources().getDimension(R.dimen.preset_circle_margin),
                    (int) getResources().getDimension(R.dimen.preset_circle_margin)
            );
            colorButton.setLayoutParams(params);
            GradientDrawable circleDrawable = (GradientDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.circle_shape, null);
            assert circleDrawable != null;
            circleDrawable.setColor(color);
            colorButton.setBackground(circleDrawable);
            colorButton.setTag(color);
            colorButton.setOnClickListener(v -> {
                int selectedColor = (int) v.getTag();
                setSlidersFromColor(dl, selectedColor);
            });
            dl.presetColorGrid.addView(colorButton);
        }
    }

    private void setSlidersFromColor(PickerDialogBinding dl, int rgbColor) {
        dl.redSlider.setProgress(Color.red(rgbColor));
        dl.greenSlider.setProgress(Color.green(rgbColor));
        dl.blueSlider.setProgress(Color.blue(rgbColor));
        updateColorDisplay(dl, Color.red(rgbColor), Color.green(rgbColor), Color.blue(rgbColor));
    }

    private void showHexInputDialog(PickerDialogBinding dl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle(R.string.enter_hex_color_rrggbb);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        input.setHint(R.string.rrggbb);
        input.setTextColor(Color.BLACK);
        input.setHintTextColor(Color.GRAY);
        input.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        input.setText(rgbToHex(dl.redSlider.getProgress(), dl.greenSlider.getProgress(), dl.blueSlider.getProgress()));
        InputFilter[] filters = new InputFilter[3];
        filters[0] = new HexInputFilter();
        filters[1] = new InputFilter.LengthFilter(6);
        filters[2] = new InputFilter.AllCaps();
        input.setFilters(filters);
        builder.setView(input);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.submit, (dialog, which) -> {
            String hexInput = input.getText().toString().trim();
            if (isValidHexCode(hexInput)) {
                setSlidersFromColor(dl, Color.parseColor("#" + hexInput));
            } else {
                Toast.makeText(MainActivity.this, R.string.invalid_hex_code_please_use_rrggbb_format, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        inputDialog = builder.create();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidthPx = displayMetrics.widthPixels;
        float density = getResources().getDisplayMetrics().density;
        int maxWidthDp = 100;
        int maxWidthPx = (int) (maxWidthDp * density);
        int dialogWidthPx = (int) (screenWidthPx * 0.80);
        if (dialogWidthPx > maxWidthPx) {
            dialogWidthPx = maxWidthPx;
        }
        Objects.requireNonNull(inputDialog.getWindow()).setLayout(dialogWidthPx, WindowManager.LayoutParams.WRAP_CONTENT);
        inputDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog on the screen
        inputDialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = inputDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = inputDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            positiveButton.setTextColor(Color.parseColor("#82B1FF"));
            negativeButton.setTextColor(Color.parseColor("#82B1FF"));
        });
        inputDialog.show();
    }

    private static class HexInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder filtered = new StringBuilder();
            for (int i = start; i < end; i++) {
                char character = source.charAt(i);
                if (Character.isDigit(character) || (character >= 'a' && character <= 'f') || (character >= 'A' && character <= 'F')) {
                    filtered.append(character);
                }
            }
            return filtered.toString();
        }
    }

    private boolean isValidHexCode(String hexCode) {
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{6}$");
        Matcher matcher = pattern.matcher(hexCode);
        return matcher.matches();
    }


    void setupGradientPicker(PickerDialogBinding dl) {
        dl.gradientPickerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dl.gradientPickerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = dl.gradientPickerView.getWidth();
                int height = dl.gradientPickerView.getHeight();
                if (width > 0 && height > 0) {
                    gradientBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(gradientBitmap);
                    Paint paint = new Paint();
                    float[] hsv = new float[3];
                    hsv[1] = 1.0f;
                    for (int y = 0; y < height; y++) {
                        hsv[2] = 1.0f - (float) y / height;
                        for (int x = 0; x < width; x++) {
                            hsv[0] = (float) x / width * 360f;
                            paint.setColor(Color.HSVToColor(255, hsv));
                            canvas.drawPoint(x, y, paint);
                        }
                    }
                    dl.gradientPickerView.setBackground(new BitmapDrawable(getResources(), gradientBitmap));
                }
            }
        });
    }

    private void setupCollapsibleSection(PickerDialogBinding dl, LinearLayout header, final ViewGroup content, final ImageView toggleIcon, boolean isExpandedInitially) {
        content.setVisibility(isExpandedInitially ? View.VISIBLE : View.GONE);
        toggleIcon.setImageResource(isExpandedInitially ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
        header.setOnClickListener(v -> {
            if (content.getVisibility() == View.VISIBLE) {
                content.setVisibility(View.GONE);
                toggleIcon.setImageResource(R.drawable.ic_arrow_down);
                if (header.getId() == dl.rgbSlidersHeader.getId()) {
                    SaveSetting(this,"RGB_VIEW",false);
                }
                else if (header.getId() == dl.gradientPickerHeader.getId()) {
                    SaveSetting(this,"PICKER_VIEW",false);
                }
                else if (header.getId() == dl.presetColorsHeader.getId()) {
                    SaveSetting(this,"PRESET_VIEW",false);
                }
                else if (header.getId() == dl.photoColorHeader.getId()) {
                    SaveSetting(this,"PHOTO_VIEW",false);
                }
            } else {
                content.setVisibility(View.VISIBLE);
                toggleIcon.setImageResource(R.drawable.ic_arrow_up);
                if (header.getId() == dl.rgbSlidersHeader.getId()) {
                    SaveSetting(this,"RGB_VIEW",true);
                }
                else if (header.getId() == dl.gradientPickerHeader.getId()) {
                    SaveSetting(this,"PICKER_VIEW",true);
                    if (gradientBitmap == null) {
                        setupGradientPicker(dl);
                    }
                }
                else if (header.getId() == dl.presetColorsHeader.getId()) {
                    SaveSetting(this,"PRESET_VIEW",true);
                }
                else if (header.getId() == dl.photoColorHeader.getId()) {
                    SaveSetting(this,"PHOTO_VIEW",true);
                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_upload) {
            openUpload();
        } else if (id == R.id.nav_download) {
            openUpdate();
        }else if (id == R.id.nav_export) {
            showExportDialog();
        }else if (id == R.id.nav_import) {
            showImportDialog();
        }else if (id == R.id.nav_manual) {
            openCustom();
        }else if (id == R.id.nav_format) {
            FormatTag();
        }else if (id == R.id.nav_memory) {
            loadTagMemory();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupActivityResultLaunchers() {
        exportDirectoryChooser = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri treeUri = result.getData().getData();
                        if (treeUri != null) {
                            getContentResolver().takePersistableUriPermission(
                                    treeUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            );
                            performSAFExport(treeUri);
                        } else {
                            Toast.makeText(this, R.string.failed_to_get_export_directory, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.export_cancelled, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        importFileChooser = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            performSAFImport(fileUri);
                        } else {
                            Toast.makeText(this, R.string.failed_to_select_import_file, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, R.string.import_cancelled, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (pendingAction == ACTION_EXPORT) {
                            performLegacyExport();
                        } else if (pendingAction == ACTION_IMPORT) {
                            performLegacyImport();
                        }
                    } else {
                        Toast.makeText(this, R.string.storage_permission_denied_cannot_perform_action, Toast.LENGTH_LONG).show();
                    }
                    pendingAction = -1;
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        colorDialog.photoImage.setImageBitmap(bitmap);
                        setupPhotoPicker(colorDialog.photoImage);
                    } else {
                        // Handle failure or cancellation
                        Toast.makeText(this, R.string.photo_capture_cancelled_or_failed, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    private void checkPermissionAndStartAction(int actionType) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (actionType == ACTION_EXPORT) {
                    performLegacyExport();
                } else {
                    performLegacyImport();
                }
            } else {
                pendingAction = actionType;
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {
            if (actionType == ACTION_EXPORT) {
                startSAFExportProcess();
            } else {
                startSAFImportProcess();
            }
        }
    }


    private void startSAFExportProcess() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.select_backup_folder));
        exportDirectoryChooser.launch(intent);
    }


    private void performSAFExport(Uri treeUri) {
        executorService.execute(() -> {
            try {
                File dbFile = filamentDB.getDatabaseFile(this, PrinterType);
                filamentDB.closeInstance();
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                if (pickedDir == null || !pickedDir.exists() || !pickedDir.canWrite()) {
                    runOnUiThread(() -> Toast.makeText(this, R.string.cannot_write_to_selected_directory, Toast.LENGTH_LONG).show());
                    return;
                }
                String dbBaseName = dbFile.getName().replace(".db", "");
                DocumentFile dbDestFile = pickedDir.createFile("application/octet-stream", dbBaseName + ".db");
                if (dbDestFile != null) {
                    copyFileToUri(this, dbFile, dbDestFile.getUri());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, R.string.failed_to_create_db_backup_file, Toast.LENGTH_LONG).show());
                    return;
                }
                runOnUiThread(() -> Toast.makeText(this, R.string.database_exported_successfully, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.database_saf_export_failed) + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                filamentDB.getInstance(this, PrinterType);
            }
        });
    }


    private void performLegacyExport() {
        executorService.execute(() -> {
            try {
                File dbFile = filamentDB.getDatabaseFile(this, PrinterType);
                filamentDB.closeInstance();
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                if (!downloadsDir.exists()) {
                    boolean val = downloadsDir.mkdirs();
                }
                String dbBaseName = dbFile.getName().replace(".db", "");
                File dbDestFile = new File(downloadsDir, dbBaseName + ".db");
                copyFile(dbFile, dbDestFile);
                runOnUiThread(() -> Toast.makeText(this, R.string.database_exported_successfully_to_downloads_folder, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.database_legacy_export_failed) + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                filamentDB.getInstance(this, PrinterType);
            }
        });
    }


    private void startSAFImportProcess() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        String[] mimeTypes = {"application/x-sqlite3", "application/vnd.sqlite3", "application/octet-stream"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        importFileChooser.launch(intent);
    }


    private void performSAFImport(Uri sourceUri) {
        if (!sourceUri.toString().toLowerCase().contains("material_database_" + PrinterType.toLowerCase())) {
            runOnUiThread(() -> Toast.makeText(this, String.format(getString(R.string.incorrect_database_file_selected_the_s_database_is_required), PrinterType.toUpperCase()), Toast.LENGTH_LONG).show());
            return;
        }
        executorService.execute(() -> {
            try {
                filamentDB.closeInstance();
                File dbFile = filamentDB.getDatabaseFile(this, PrinterType);
                File dbDir = dbFile.getParentFile();
                if (dbDir != null && !dbDir.exists()) {
                    boolean val = dbDir.mkdirs();
                }
                copyUriToFile(this, sourceUri, dbFile);
                filamentDB.getInstance(this, PrinterType);
                setMatDb(PrinterType);
                SaveSetting(this, "version_" + PrinterType, getDBVersion(this, PrinterType));
                runOnUiThread(() -> Toast.makeText(this, R.string.database_imported_successfully, Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.database_saf_import_failed) + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (filamentDB.INSTANCE == null) {
                    filamentDB.getInstance(this, PrinterType);
                    setMatDb(PrinterType);
                    SaveSetting(this, "version_" + PrinterType, getDBVersion(this, PrinterType));
                }
            }
        });
    }


    private void performLegacyImport() {
        executorService.execute(() -> {
            try {
                filamentDB.closeInstance();

                File dbFile = filamentDB.getDatabaseFile(this, PrinterType);
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File sourceDbFile = new File(downloadsDir, dbFile.getName());
                if (!dbFile.getName().toLowerCase().contains("material_database_" + PrinterType.toLowerCase())) {
                    runOnUiThread(() -> Toast.makeText(this, format(getString(R.string.incorrect_database_file_selected_the_s_database_is_required), PrinterType.toUpperCase()), Toast.LENGTH_LONG).show());
                    return;
                }
                if (!sourceDbFile.exists()) {
                    runOnUiThread(() -> Toast.makeText(this, getString(R.string.backup_file_not_found_in_downloads) + sourceDbFile.getName(), Toast.LENGTH_LONG).show());
                    return;
                }
                File dbDir = dbFile.getParentFile();
                if (dbDir != null && !dbDir.exists()) {
                    boolean val = dbDir.mkdirs();
                }
                copyFile(sourceDbFile, dbFile);
                filamentDB.getInstance(this, PrinterType);
                setMatDb(PrinterType);
                SaveSetting(this, "version_" + PrinterType, getDBVersion(this, PrinterType));
                runOnUiThread(() -> Toast.makeText(this, R.string.database_imported_successfully, Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.database_legacy_import_failed) + e.getMessage(), Toast.LENGTH_LONG).show());
            } finally {
                if (filamentDB.INSTANCE == null) {
                    filamentDB.getInstance(this, PrinterType);
                    setMatDb(PrinterType);
                    SaveSetting(this, "version_" + PrinterType, getDBVersion(this, PrinterType));
                }
            }
        });
    }


    private void showImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SpannableString titleText = new SpannableString(getString(R.string.import_database));
        titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
        SpannableString messageText = new SpannableString(String.format(getString(R.string.restore_s_database), PrinterType.toUpperCase()));
        messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
        builder.setTitle(titleText);
        builder.setMessage(messageText);
        builder.setPositiveButton("Import", (dialog, which) -> checkPermissionAndStartAction(ACTION_IMPORT));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
        }
    }


    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SpannableString titleText = new SpannableString(getString(R.string.export_database));
        titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
        SpannableString messageText = new SpannableString(String.format(getString(R.string.backup_s_database_material_database_s_db), PrinterType.toUpperCase(), PrinterType.toLowerCase()));
        messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
        builder.setTitle(titleText);
        builder.setMessage(messageText);
        builder.setPositiveButton("Export", (dialog, which) -> checkPermissionAndStartAction(ACTION_EXPORT));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
        }
    }


    private void checkPermissionsAndCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
        else {
            takePicture();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.camera_permission_is_required_to_take_photos, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void takePicture() {
        if (cameraLauncher != null) {
            cameraLauncher.launch(null);
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupPhotoPicker(ImageView imageView) {
        colorDialog.clearImage.setVisibility(View.VISIBLE);
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache(true);
        imageView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                Bitmap bitmap = imageView.getDrawingCache();
                float touchX = event.getX();
                float touchY = event.getY();
                if (touchX >= 0 && touchX < bitmap.getWidth() && touchY >= 0 && touchY < bitmap.getHeight()) {
                    try {
                        int pixel = bitmap.getPixel((int) touchX, (int) touchY);
                        int r = Color.red(pixel);
                        int g = Color.green(pixel);
                        int b = Color.blue(pixel);
                        colorDialog.colorDisplay.setBackgroundColor(Color.rgb(r, g, b));
                        colorDialog.txtcolor.setText(String.format("%06X", (0xFFFFFF & pixel)));
                        setSlidersFromColor(colorDialog, Color.argb(255, Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
                    } catch (Exception ignored) {}
                }
            }
            return true;
        });
    }


    void loadTagMemory() {
        try {
            tagDialog = new Dialog(this, R.style.Theme_SpoolID);
            tagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            tagDialog.setCanceledOnTouchOutside(false);
            tagDialog.setTitle("Tag Memory");
            TagDialogBinding tdl = TagDialogBinding.inflate(getLayoutInflater());
            View rv = tdl.getRoot();
            tagDialog.setContentView(rv);
            tdl.btncls.setOnClickListener(v -> tagDialog.dismiss());
            tagView = tdl.recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.scrollToPosition(0);
            tagView.setLayoutManager(layoutManager);
            tdl.btnread.setOnClickListener(v -> readTagMemory(tdl));
            tagDialog.show();
            if (currentTag != null) {
                readTagMemory(tdl);
            }
        } catch (Exception ignored) {}
    }


    void readTagMemory(TagDialogBinding tdl)
    {
        if (currentTag == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_tag_found), Toast.LENGTH_SHORT).show();
            return;
        }
        try (MifareClassic mfc = MifareClassic.get(currentTag)) {
            try {
                mfc.connect();
                int sectorCount = mfc.getSectorCount();
                int currentBlock = 0;
                tdl.lbldesc.setText(getTypeName(mfc.getType()));
                tagItems = new tagItem[sectorCount * 16];
                boolean auth;
                for (int s = 0; s < sectorCount; s++) {
                    if (s == 1) {
                        byte[] key = MifareClassic.KEY_DEFAULT;
                        if (encrypted) {
                            key = encKey;
                        }
                        auth = mfc.authenticateSectorWithKeyA(1, key);
                    }
                    else {
                         auth = mfc.authenticateSectorWithKeyA(s, MifareClassic.KEY_DEFAULT);
                    }
                    if (auth) {
                        int firstBlock = mfc.sectorToBlock(s);
                        int blockCount = mfc.getBlockCountInSector(s);
                        for (int b = 0; b < blockCount; b++) {
                            currentBlock = firstBlock + b;
                            byte[] data = mfc.readBlock(currentBlock);
                            String hexString = bytesToHex(data);
                            String definition = getMifareBlockDefinition(s, b, blockCount);
                            tagItems[currentBlock] = new tagItem();
                            tagItems[currentBlock].tKey = String.format(Locale.getDefault(), "Block %d | %s", currentBlock, definition);
                            tagItems[currentBlock].tValue = hexString;
                            if (currentBlock == 0) {
                                tagItems[currentBlock].tImage = AppCompatResources.getDrawable(this, R.drawable.locked);
                            } else if (definition.contains("USER DATA")) {
                                tagItems[currentBlock].tImage = AppCompatResources.getDrawable(this, R.drawable.writable);
                            } else {
                                tagItems[currentBlock].tImage = AppCompatResources.getDrawable(this, R.drawable.internal);
                            }
                        }
                    } else {
                        tagItems[currentBlock+1] = new tagItem();
                        tagItems[currentBlock+1].tKey = "FAILED AUTHENTICATION";
                        tagItems[currentBlock+1].tValue = "Key Required";
                        tagItems[currentBlock+1].tImage = AppCompatResources.getDrawable(this, R.drawable.failed);
                    }
                }
                tagItem[] filledItems = new tagItem[currentBlock+1];
                System.arraycopy(tagItems, 0, filledItems, 0, currentBlock+1);
                tagAdapter = new tagAdapter(getBaseContext(), filledItems);
                tagAdapter.setHasStableIds(true);
                runOnUiThread(() -> {
                    tagView.removeAllViewsInLayout();
                    tagView.setAdapter(null);
                    tagView.setAdapter(tagAdapter);
                });
            } catch (Exception ignored) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_reading_tag), Toast.LENGTH_SHORT).show();
            }
            finally {
                try {if (mfc.isConnected()) mfc.close();} catch (Exception ignored) {}
            }
        } catch (Exception ignored) {
            Toast.makeText(getApplicationContext(), getString(R.string.no_tag_found), Toast.LENGTH_SHORT).show();
        }
    }
}