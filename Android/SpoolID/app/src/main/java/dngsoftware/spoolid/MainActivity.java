package dngsoftware.spoolid;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static java.lang.String.format;
import static dngsoftware.spoolid.Utils.GetMaterialBrand;
import static dngsoftware.spoolid.Utils.GetMaterialInfo;
import static dngsoftware.spoolid.Utils.GetMaterialIntWeight;
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
import static dngsoftware.spoolid.Utils.findPrinters;
import static dngsoftware.spoolid.Utils.getJsonDB;
import static dngsoftware.spoolid.Utils.getMaterialBrands;
import static dngsoftware.spoolid.Utils.getMaterialPos;
import static dngsoftware.spoolid.Utils.getMaterials;
import static dngsoftware.spoolid.Utils.getMifareBlockDefinition;
import static dngsoftware.spoolid.Utils.getPositionByValue;
import static dngsoftware.spoolid.Utils.getTypeName;
import static dngsoftware.spoolid.Utils.isValidHexCode;
import static dngsoftware.spoolid.Utils.loadImage;
import static dngsoftware.spoolid.Utils.materialWeights;
import static dngsoftware.spoolid.Utils.playBeep;
import static dngsoftware.spoolid.Utils.populateDatabase;
import static dngsoftware.spoolid.Utils.presetColors;
import static dngsoftware.spoolid.Utils.printerTypes;
import static dngsoftware.spoolid.Utils.removeFilament;
import static dngsoftware.spoolid.Utils.restartApp;
import static dngsoftware.spoolid.Utils.restorePrinterDB;
import static dngsoftware.spoolid.Utils.rgbToHex;
import static dngsoftware.spoolid.Utils.saveDBToPrinter;
import static dngsoftware.spoolid.Utils.setMaterialInfo;
import static dngsoftware.spoolid.Utils.setNfcLaunchMode;
import static dngsoftware.spoolid.Utils.smAddSpool;
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
import android.graphics.Typeface;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import dngsoftware.spoolid.databinding.ActivityMainBinding;
import dngsoftware.spoolid.databinding.AddDialogBinding;
import dngsoftware.spoolid.databinding.EditDialogBinding;
import dngsoftware.spoolid.databinding.ManualDialogBinding;
import dngsoftware.spoolid.databinding.PickerDialogBinding;
import dngsoftware.spoolid.databinding.ManageDialogBinding;
import dngsoftware.spoolid.databinding.SaveDialogBinding;
import dngsoftware.spoolid.databinding.SettingsDialogBinding;
import dngsoftware.spoolid.databinding.TagDialogBinding;
import dngsoftware.spoolid.databinding.UpdateDialogBinding;

public class MainActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback, NavigationView.OnNavigationItemSelectedListener {
    private MatDB matDb;
    private filamentDB rdb;
    jsonItem[] jsonItems;
    ArrayAdapter<String> badapter, sadapter, padapter;
    ArrayAdapter<MaterialItem> madapter;
    List<String> printerDb;
    ColorMatcher matcher = null;
    private NfcAdapter nfcAdapter;
    Tag currentTag = null;
    int SelectedSize, SelectedBrand;
    String MaterialName, MaterialID, MaterialWeight, MaterialColor, PrinterType, MaterialVendor, SelectedPrinter;
    Dialog pickerDialog, customDialog, saveDialog, updateDialog, editDialog, addDialog, tagDialog, printerDialog, settingsDialog;
    AlertDialog inputDialog;
    tagAdapter tagAdapter;
    spinnerAdapter manageAdapter;
    RecyclerView tagView;
    private Toast currentToast;
    tagItem[] tagItems;
    long jsonVersion;
    boolean encrypted = false;
    byte[] encKey;
    private ActivityMainBinding main;
    private ManualDialogBinding manual;
    private Context context;
    Bitmap gradientBitmap;
    private ExecutorService executorService;
    private Handler mainHandler;
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
        mainHandler = new Handler(Looper.getMainLooper());
        setupActivityResultLaunchers();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PrinterManager manager = new PrinterManager(this);
        printerDb = manager.getList();

        PrinterType = GetSetting(this, "printer", "");

        if (GetSetting(this, "enablesm", false))
        {
            main.txtspman.setVisibility(View.VISIBLE);
            executorService.execute(() -> matcher = new ColorMatcher(context));
        }
        else {
            main.txtspman.setVisibility(View.INVISIBLE);
        }
        main.txtspman.setOnClickListener(view ->
        {
            if (GetSetting(this, "enablesm", false))
            {
                AddSpoolManSpool();
            }
        });

        if (PrinterType.isEmpty()) {
            SaveSetting(this, "newformat", true);
        } else {
            if (printerDb.isEmpty() && !GetSetting(this, "newformat", false)) {
                printerDb.add("K2");
                printerDb.add("K1");
                printerDb.add("HI");
                manager.saveList(printerDb);
                SaveSetting(this, "newformat", true);
            }
        }

        if (printerDb.isEmpty()) {
            openManage(true);
        }

        padapter = new ArrayAdapter<>(this, R.layout.spinner_item, printerDb);
        main.type.setAdapter(padapter);
        main.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SaveSetting(context, "printer", Objects.requireNonNull(padapter.getItem(position)).toLowerCase());
                SelectedPrinter = Objects.requireNonNull(padapter.getItem(position));
                PrinterType = SelectedPrinter.toLowerCase();
                setMatDb(PrinterType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        main.type.setSelection(getPositionByValue(main.type, PrinterType));

        main.colorview.setBackgroundColor(Color.argb(255, 0, 0, 255));
        MaterialColor = "0000FF";

        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                Bundle options = new Bundle();
                options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);
                nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A, options);
                if (!canMfc(this)) {
                    showToast(R.string.this_device_does_not_support_mifare_classic_tags, Toast.LENGTH_SHORT);
                    main.readbutton.setEnabled(false);
                    main.writebutton.setEnabled(false);
                    main.colorspin.setEnabled(false);
                    main.spoolsize.setEnabled(false);
                    main.colorview.setEnabled(false);
                    main.colorview.setBackgroundColor(Color.parseColor("#D3D3D3"));
                    main.lbltagid.setVisibility(View.INVISIBLE);
                    main.tagid.setVisibility(View.INVISIBLE);
                    main.txtmsg.setVisibility(View.VISIBLE);
                    main.txtspman.setVisibility(View.INVISIBLE);
                    main.txtmsg.setText(R.string.rfid_functions_disabled);
                }
            } else {
                showToast(R.string.please_activate_nfc, Toast.LENGTH_LONG);
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
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), getString(R.string.rfid_disabled_tap_here_to_enable_nfc).indexOf("Tap"),
                        getString(R.string.rfid_disabled_tap_here_to_enable_nfc).indexOf("Tap") + 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                main.txtmsg.setVisibility(View.VISIBLE);
                main.txtmsg.setText(spannableString);
                main.txtmsg.setGravity(Gravity.CENTER);
                main.txtspman.setVisibility(View.INVISIBLE);
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
            mainHandler.post(() -> {
                try {
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
                            MaterialVendor = main.brand.getItemAtPosition(main.brand.getSelectedItemPosition()).toString();
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
                            try {
                                MaterialItem selectedItem = (MaterialItem) parentView.getItemAtPosition(position);
                                MaterialName = selectedItem.getMaterialName();
                                MaterialID = selectedItem.getMaterialID();
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                } catch (Exception ignored) {
                }
            });
        } catch (Exception ignored) {
        }
    }


    void setMaterial(String brand) {
        madapter = new ArrayAdapter<>(this, R.layout.spinner_item, getMaterials(matDb, brand));
        main.material.setAdapter(madapter);
        main.material.setSelection(getMaterialPos(madapter, MaterialID));
        main.material.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    MaterialItem selectedItem = (MaterialItem) parentView.getItemAtPosition(position);
                    MaterialName = selectedItem.getMaterialName();
                    MaterialID = selectedItem.getMaterialID();
                } catch (Exception ignored) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (printerDb.isEmpty()) {
            openManage(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if (id == R.id.nav_upload) {
            openUpload();
        } else if (id == R.id.nav_download) {
            openUpdate();
        } else if (id == R.id.nav_export) {
            showExportDialog();
        } else if (id == R.id.nav_import) {
            showImportDialog();
        } else if (id == R.id.nav_manual) {
            openCustom();
        } else if (id == R.id.nav_format) {
            FormatTag();
        } else if (id == R.id.nav_memory) {
            loadTagMemory();
        } else if (id == R.id.nav_manage) {
            openManage(false);
        } else if (id == R.id.nav_settings) {
            openSettings();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
        if (saveDialog != null && saveDialog.isShowing()) {
            saveDialog.dismiss();
        }
        if (tagDialog != null && tagDialog.isShowing()) {
            tagDialog.dismiss();
        }
        if (printerDialog != null && printerDialog.isShowing()) {
            printerDialog.dismiss();
        }
        if (settingsDialog != null && settingsDialog.isShowing()) {
            settingsDialog.dismiss();
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
            mainHandler.post(() -> {
                if (currentTag.getId().length > 4) {
                    showToast(R.string.tag_not_compatible, Toast.LENGTH_SHORT);
                    main.tagid.setText(R.string.error);
                    return;
                }
                showToast(getString(R.string.tag_found) + bytesToHex(currentTag.getId()), Toast.LENGTH_SHORT);
                main.tagid.setText(bytesToHex(currentTag.getId()));
                encKey = createKey(currentTag.getId());
                CheckTag();
                if (encrypted) {
                    main.tagid.setText(format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId())));
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
                    if (currentTag.getId().length > 4) {
                        showToast(R.string.tag_not_compatible, Toast.LENGTH_SHORT);
                        main.tagid.setText(R.string.error);
                        return;
                    }
                    showToast(getString(R.string.tag_found) + bytesToHex(currentTag.getId()), Toast.LENGTH_SHORT);
                    main.tagid.setText(bytesToHex(currentTag.getId()));
                    encKey = createKey(currentTag.getId());
                    CheckTag();
                    if (encrypted) {
                        main.tagid.setText(format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId())));
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
                    showToast(R.string.error_reading_tag, Toast.LENGTH_SHORT);
                    encrypted = false;
                }
            } else {
                showToast(R.string.invalid_tag_type, Toast.LENGTH_SHORT);
            }
        }
    }

    String ReadTag() {
        if (currentTag == null) return null;
        MifareClassic mfc = MifareClassic.get(currentTag);
        if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
            try {
                mfc.connect();
                byte[] s1Data = new byte[48];
                byte[] s2Data = new byte[48];
                byte[] keyS1 = encrypted ? encKey : MifareClassic.KEY_DEFAULT;
                if (mfc.authenticateSectorWithKeyA(1, keyS1)) {
                    ByteBuffer buff1 = ByteBuffer.wrap(s1Data);
                    buff1.put(mfc.readBlock(4));
                    buff1.put(mfc.readBlock(5));
                    buff1.put(mfc.readBlock(6));
                } else {
                    showToast(R.string.authentication_failed, Toast.LENGTH_SHORT);
                    mfc.close();
                    return null;
                }
                if (mfc.authenticateSectorWithKeyA(2, MifareClassic.KEY_DEFAULT)) {
                    ByteBuffer buff2 = ByteBuffer.wrap(s2Data);
                    buff2.put(mfc.readBlock(8));
                    buff2.put(mfc.readBlock(9));
                    buff2.put(mfc.readBlock(10));
                }
                mfc.close();
                String part1;
                if (encrypted) {
                    byte[] decryptedS1 = cipherData(2, s1Data);
                    part1 = new String(decryptedS1, StandardCharsets.UTF_8);
                } else {
                    part1 = new String(s1Data, StandardCharsets.UTF_8);
                }
                String part2 = new String(s2Data, StandardCharsets.UTF_8);
                return (part1 + part2);
            } catch (Exception e) {
                showToast(R.string.error_reading_tag, Toast.LENGTH_SHORT);
            } finally {
                try {
                    mfc.close();
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    void WriteTag(String tagData) {
        if (currentTag != null) {
            executorService.execute(() -> {
                MifareClassic mfc = MifareClassic.get(currentTag);
                if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
                    try {
                        mfc.connect();
                        String paddedData = String.format("%-96s", tagData);
                        byte[] fullDataBytes = paddedData.getBytes(StandardCharsets.UTF_8);
                        byte[] keyS1 = encrypted ? encKey : MifareClassic.KEY_DEFAULT;
                        if (mfc.authenticateSectorWithKeyA(1, keyS1)) {
                            byte[] s1Raw = Arrays.copyOfRange(fullDataBytes, 0, 48);
                            byte[] s1ToDisk = cipherData(1, s1Raw);
                            for (int i = 0; i < 48; i += 16) {
                                mfc.writeBlock(4 + (i / 16), Arrays.copyOfRange(s1ToDisk, i, i + 16));
                            }
                            if (!encrypted) {
                                byte[] trailer = mfc.readBlock(7);
                                System.arraycopy(encKey, 0, trailer, 0, 6);
                                System.arraycopy(encKey, 0, trailer, 10, 6);
                                mfc.writeBlock(7, trailer);
                            }
                        } else {
                            showToast(R.string.authentication_failed, Toast.LENGTH_SHORT);
                            return;
                        }
                        if (mfc.authenticateSectorWithKeyA(2, MifareClassic.KEY_DEFAULT)) {
                            byte[] s2ToDisk = Arrays.copyOfRange(fullDataBytes, 48, 96); // Plain text
                            for (int i = 0; i < 48; i += 16) {
                                mfc.writeBlock(8 + (i / 16), Arrays.copyOfRange(s2ToDisk, i, i + 16));
                            }
                        }
                        if (!encrypted) {
                            encrypted = true;
                            mainHandler.post(() -> main.tagid.setText(String.format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId()))));
                        }
                        playBeep();
                        showToast(R.string.data_written_to_tag, Toast.LENGTH_SHORT);

                    } catch (Exception e) {
                        showToast(R.string.error_writing_to_tag, Toast.LENGTH_SHORT);
                    } finally {
                        try {
                            mfc.close();
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
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
                executorService.execute(() -> {
                    MifareClassic mfc = MifareClassic.get(currentTag);
                    if (mfc != null && mfc.getType() == MifareClassic.TYPE_CLASSIC) {
                        try {
                            mfc.connect();
                            byte[] currentAuthKey = encrypted ? encKey : MifareClassic.KEY_DEFAULT;
                            byte[] zeroData = new byte[16];
                            Arrays.fill(zeroData, (byte) 0);
                            if (mfc.authenticateSectorWithKeyA(1, currentAuthKey)) {
                                mfc.writeBlock(4, zeroData);
                                mfc.writeBlock(5, zeroData);
                                mfc.writeBlock(6, zeroData);
                                if (encrypted) {
                                    byte[] trailer1 = mfc.readBlock(7);
                                    System.arraycopy(MifareClassic.KEY_DEFAULT, 0, trailer1, 0, 6);
                                    System.arraycopy(MifareClassic.KEY_DEFAULT, 0, trailer1, 10, 6);
                                    mfc.writeBlock(7, trailer1);
                                }
                            }
                            if (mfc.authenticateSectorWithKeyA(2, MifareClassic.KEY_DEFAULT)) {
                                mfc.writeBlock(8, zeroData);
                                mfc.writeBlock(9, zeroData);
                                mfc.writeBlock(10, zeroData);
                            }
                            if (encrypted) {
                                encrypted = false;
                                mainHandler.post(() -> main.tagid.setText(bytesToHex(currentTag.getId())));
                            }
                            playBeep();
                            showToast(R.string.tag_formatted, Toast.LENGTH_SHORT);
                        } catch (Exception e) {
                            showToast(R.string.error_formatting_tag, Toast.LENGTH_SHORT);
                        } finally {
                            try {
                                mfc.close();
                            } catch (Exception ignored) {
                            }
                        }
                    } else {
                        showToast(R.string.invalid_tag_type, Toast.LENGTH_SHORT);
                    }
                });
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
            showToast(R.string.no_tag_found, Toast.LENGTH_SHORT);
        }
    }

    void ReadSpoolData() {
        executorService.execute(() -> {
            String tagData = ReadTag();
            if (tagData != null && tagData.length() >= 40) {
                mainHandler.post(() -> {
                    String MaterialID = tagData.substring(12, 17);
                    try {
                        String pId = tagData.substring(48, 96).trim();
                        if (!pId.isEmpty())
                            main.type.setSelection(getPositionByValue(main.type, pId));
                    } catch (Exception ignored) {
                    }
                    mainHandler.postDelayed(() -> {
                        try {
                            if (GetMaterialName(matDb, MaterialID) != null) {
                                MaterialColor = tagData.substring(18, 24);
                                String Length = tagData.substring(24, 28);
                                main.colorview.setBackgroundColor(Color.parseColor("#" + MaterialColor));
                                MaterialName = Objects.requireNonNull(GetMaterialName(matDb, MaterialID))[0];
                                main.brand.setSelection(badapter.getPosition(Objects.requireNonNull(GetMaterialName(matDb, MaterialID))[1]));
                                mainHandler.postDelayed(() -> main.material.setSelection(getMaterialPos(madapter, MaterialID)), 300);
                                main.spoolsize.setSelection(sadapter.getPosition(GetMaterialWeight(Length)));
                                showToast(R.string.data_read_from_tag, Toast.LENGTH_SHORT);
                            } else {
                                showToast(R.string.unknown_or_empty_tag, Toast.LENGTH_SHORT);
                            }
                        } catch (Exception ignored) {
                            showToast(R.string.error_reading_tag, Toast.LENGTH_SHORT);
                        }
                    }, 300);
                });
            } else {
                showToast(R.string.error_reading_tag, Toast.LENGTH_SHORT);
            }
        });
    }

    void WriteSpoolData(String MaterialID, String Color, String Length) {
        //SecureRandom random = new SecureRandom();
        String filamentId = "1" + MaterialID; //material_database.json
        String vendorId = "0276"; //0276 creality
        String color = "0" + Color;
        String serialNum = "000001"; //format(Locale.getDefault(), "%06d", random.nextInt(900000));
        String reserve = "00000000000000";
        String batch = "A2";
        WriteTag("AB124" + vendorId + batch + filamentId + color + Length + serialNum + reserve + PrinterType);
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
                    manual.txtcolor.setText(format("0%s", MaterialColor));
                } else {
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

                dl.photoImage.setImageResource(R.drawable.camera);
                dl.photoImage.setDrawingCacheEnabled(false);
                dl.photoImage.buildDrawingCache(false);
                dl.photoImage.setOnTouchListener(null);
                dl.clearImage.setVisibility(View.GONE);

            });
            pickerDialog.show();
        } catch (Exception ignored) {
        }
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
            manual.btnread.setOnClickListener(v -> executorService.execute(() -> {
                String tagData = ReadTag();
                if (tagData != null && tagData.length() >= 40) {
                    if (!tagData.startsWith("\0")) {
                        mainHandler.post(() -> {
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
                            showToast(R.string.data_read_from_tag, Toast.LENGTH_SHORT);
                        });
                    } else {
                        showToast(R.string.unknown_or_empty_tag, Toast.LENGTH_SHORT);
                    }
                } else {
                    showToast(R.string.error_reading_tag, Toast.LENGTH_SHORT);
                }
            }));
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
                    showToast(R.string.incorrect_tag_data_length, Toast.LENGTH_SHORT);
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
                showToast(R.string.values_reset, Toast.LENGTH_SHORT);
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
            dl.chkpres.setChecked(GetSetting(this, "preserve", false));
            dl.chkpres.setOnClickListener(v -> {
                SaveSetting(this, "preserve", dl.chkpres.isChecked());
            });
            executorService.execute(() -> {
                try {
                    String searchName = PrinterType;
                    String searchNozzle = "0.4";
                    JSONArray matches = findPrinters(this, searchName, searchNozzle);
                    mainHandler.post(() -> {
                        try {
                            if (matches.length() > 0) {
                                List<PrinterOption> options = new ArrayList<>();
                                for (int i = 0; i < matches.length(); i++) {
                                    JSONObject printer = matches.getJSONObject(i);
                                    String label = printer.getString("name");
                                    options.add(new PrinterOption(label, printer));
                                }
                                ArrayAdapter<PrinterOption> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, options);
                                dl.type.setAdapter(adapter);
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    PrinterOption option = adapter.getItem(i);
                                    if (option != null && option.displayName.equalsIgnoreCase(GetSetting(context, "update_select_" + PrinterType, ""))) {
                                        dl.type.setSelection(i);
                                        break;
                                    }
                                }
                                dl.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        PrinterOption selected = (PrinterOption) parent.getItemAtPosition(position);
                                        try {
                                            SaveSetting(context, "update_select_" + PrinterType, selected.displayName);
                                            String thumbnail = selected.data.getString("thumbnail");
                                            jsonVersion = selected.data.getLong("version");
                                            if (!dl.chkprnt.isChecked())
                                                dl.txtnewver.setText(format(Locale.getDefault(), getString(R.string.printer_version), jsonVersion));
                                            loadImage(thumbnail, dl.imgprinter);
                                        } catch (Exception ignored) {
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
                        } catch (Exception ignored) {
                        }
                    });
                } catch (Exception ignored) {
                }
            });

            dl.imgtext.setOnClickListener(v -> {
                PrinterOption selected = (PrinterOption) dl.type.getItemAtPosition(dl.type.getSelectedItemPosition());
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    SpannableString titleText = new SpannableString("Update Information");
                    titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
                    Date updDate = new Date(jsonVersion * 1000L);
                    DateFormat df = android.text.format.DateFormat.getMediumDateFormat(context);
                    SpannableString messageText = new SpannableString(selected.data.getString("name") + " (" +
                            selected.data.getString("printerIntName") + ")\n" + df.format(updDate) + "\n" + jsonVersion + " (" + selected.data.getString("showVersion") + ")\n\n" +
                            selected.data.getString("descriptionI18n") + "\n\n");
                    messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
                    builder.setTitle(titleText);
                    builder.setMessage(messageText);
                    builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                    if (alert.getWindow() != null) {
                        alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
                    }
                } catch (Exception ignored) {
                }
            });

            dl.chkprnt.setChecked(GetSetting(this, "fromprinter_" + PrinterType, false));
            dl.chkprnt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SaveSetting(this, "fromprinter_" + PrinterType, isChecked);
                if (isChecked) {
                    dl.txtaddress.setVisibility(View.VISIBLE);
                    dl.txtpsw.setVisibility(View.VISIBLE);
                    dl.lblpip.setVisibility(View.VISIBLE);
                    dl.lblpsw.setVisibility(View.VISIBLE);
                    dl.updatedesc.setVisibility(View.VISIBLE);
                    dl.updatedesc.setVisibility(View.VISIBLE);
                    dl.btnchk.setVisibility(View.VISIBLE);
                    dl.imgframe.setVisibility(View.INVISIBLE);
                    dl.type.setVisibility(View.INVISIBLE);
                    dl.typeborder.setVisibility(View.INVISIBLE);
                    dl.btnupd.setVisibility(View.INVISIBLE);
                    dl.txtnewver.setText("");
                } else {
                    dl.txtaddress.setVisibility(View.INVISIBLE);
                    dl.txtpsw.setVisibility(View.INVISIBLE);
                    dl.lblpip.setVisibility(View.INVISIBLE);
                    dl.lblpsw.setVisibility(View.INVISIBLE);
                    dl.updatedesc.setVisibility(View.INVISIBLE);
                    dl.updatedesc.setVisibility(View.INVISIBLE);
                    dl.btnchk.setVisibility(View.INVISIBLE);
                    dl.imgframe.setVisibility(View.VISIBLE);
                    dl.type.setVisibility(View.VISIBLE);
                    dl.typeborder.setVisibility(View.VISIBLE);
                    dl.btnupd.setVisibility(View.VISIBLE);
                    dl.txtnewver.setText(format(Locale.getDefault(), getString(R.string.printer_version), jsonVersion));
                }
                dl.txtmsg.setText("");
            });

            if (dl.chkprnt.isChecked()) {
                dl.txtaddress.setVisibility(View.VISIBLE);
                dl.txtpsw.setVisibility(View.VISIBLE);
                dl.lblpip.setVisibility(View.VISIBLE);
                dl.lblpsw.setVisibility(View.VISIBLE);
                dl.updatedesc.setVisibility(View.VISIBLE);
                dl.btnchk.setVisibility(View.VISIBLE);
                dl.imgframe.setVisibility(View.INVISIBLE);
                dl.type.setVisibility(View.INVISIBLE);
                dl.typeborder.setVisibility(View.INVISIBLE);
                dl.btnupd.setVisibility(View.INVISIBLE);
                dl.txtnewver.setText("");
            } else {
                dl.txtaddress.setVisibility(View.INVISIBLE);
                dl.txtpsw.setVisibility(View.INVISIBLE);
                dl.lblpip.setVisibility(View.INVISIBLE);
                dl.lblpsw.setVisibility(View.INVISIBLE);
                dl.btnchk.setVisibility(View.INVISIBLE);
                dl.btnupd.setVisibility(View.VISIBLE);
                dl.imgframe.setVisibility(View.VISIBLE);
                dl.type.setVisibility(View.VISIBLE);
                dl.typeborder.setVisibility(View.VISIBLE);
                dl.txtnewver.setText(format(Locale.getDefault(), getString(R.string.printer_version), jsonVersion));
                dl.updatedesc.setVisibility(View.INVISIBLE);
            }

            String sshDefault;
            if (PrinterType.toLowerCase().contains("hi")) {
                sshDefault = "Creality2024";
            } else if (PrinterType.toLowerCase().contains("k1")) {
                sshDefault = "creality_2023";
            } else {
                sshDefault = "creality_2024";
            }

            dl.txtpsw.setText(GetSetting(this, "psw_" + PrinterType, sshDefault));
            dl.txtaddress.setText(GetSetting(this, "host_" + PrinterType, ""));
            dl.btncls.setOnClickListener(v -> updateDialog.dismiss());
            dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), GetSetting(this, "version_" + PrinterType, -1L)));
            dl.txtprinter.setText(format(getString(R.string.creality_type), PrinterType.toUpperCase().replace("CREALITY", "")));

            dl.btnchk.setOnClickListener(v -> {
                String host = dl.txtaddress.getText().toString();
                String psw = dl.txtpsw.getText().toString();
                dl.txtmsg.setTextColor(getResources().getColor(R.color.text_color));
                dl.txtmsg.setText(R.string.checking_for_updates);
                long version = GetSetting(this, "version_" + PrinterType, -1L);
                dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), version));
                executorService.execute(() -> {
                    try {
                        String json;
                        if (GetSetting(this, "fromprinter_" + PrinterType, false)) {
                            SaveSetting(this, "host_" + PrinterType, host);
                            SaveSetting(this, "psw_" + PrinterType, psw);

                            if (host.isEmpty()) {
                                mainHandler.post(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_printer_ip_address);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            if (psw.isEmpty()) {
                                mainHandler.post(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_ssh_password);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            json = getJsonDB(psw, host, PrinterType, "material_database.json");
                        } else {
                            return;
                        }
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            mainHandler.post(() -> {
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
                            mainHandler.post(() -> {
                                dl.txtmsg.setTextColor(Color.RED);
                                dl.txtmsg.setText(R.string.unable_to_download_file_from_printer);
                            });
                        }
                    } catch (Exception ignored) {
                    }
                });
            });

            dl.btnupd.setOnClickListener(v -> {
                String host = GetSetting(this, "host_" + PrinterType, "");
                String psw = GetSetting(this, "psw_" + PrinterType, sshDefault);
                dl.txtmsg.setTextColor(getResources().getColor(R.color.text_color));
                dl.txtmsg.setText(R.string.downloading_update);
                dl.btnupd.setEnabled(false);
                executorService.execute(() -> {
                    try {
                        String json;
                        if (GetSetting(this, "fromprinter_" + PrinterType, false)) {
                            if (host.isEmpty()) {
                                mainHandler.post(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_printer_ip_address);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            if (psw.isEmpty()) {
                                mainHandler.post(() -> {
                                    dl.txtmsg.setTextColor(Color.RED);
                                    dl.txtmsg.setText(R.string.please_enter_ssh_password);
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtnewver.setText("");
                                });
                                return;
                            }
                            json = getJsonDB(psw, host, PrinterType, "material_database.json");
                        } else {
                            json = getJsonDB(this, PrinterType, "0.4");
                        }
                        mainHandler.post(() -> dl.txtmsg.setText(R.string.processing_update));
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            if (!dl.chkpres.isChecked()) matDb.deleteAll();
                            populateDatabase(this, matDb, json, PrinterType);
                            SaveSetting(this, "version_" + PrinterType, newVer);
                            mainHandler.postDelayed(() -> {
                                dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), newVer));
                                dl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                                dl.txtmsg.setText(R.string.update_successful);
                                mainHandler.postDelayed(() -> restartApp(context), 2000);
                            }, 2000);
                        } else {
                            mainHandler.post(() -> {
                                dl.txtmsg.setTextColor(Color.RED);
                                dl.txtmsg.setText(R.string.unable_to_download_file_from_printer);
                                dl.btnupd.setEnabled(true);
                            });
                        }
                    } catch (Exception ignored) {
                    }
                });
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
            mainHandler.post(() -> {
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
                        if (jsonItem.jKey.equalsIgnoreCase("meterialtype")) {
                            kvParam.put("filament_type", jsonItem.jValue);
                        } else if (jsonItem.jKey.equalsIgnoreCase("brand")) {
                            kvParam.put("filament_vendor", jsonItem.jValue);
                        } else if (jsonItem.jKey.equalsIgnoreCase("maxTemp")) {
                            if (jsonValue instanceof String) {
                                maxTemp = Integer.parseInt((String) jsonValue);
                                kvParam.put("nozzle_temperature_range_high", jsonItem.jValue);
                            } else if (jsonValue instanceof Integer) {
                                maxTemp = (Integer) jsonValue;
                                kvParam.put("nozzle_temperature_range_high", String.valueOf(jsonItem.jValue));
                            }
                        } else if (jsonItem.jKey.equalsIgnoreCase("minTemp")) {
                            if (jsonValue instanceof String) {
                                minTemp = Integer.parseInt((String) jsonValue);
                                kvParam.put("nozzle_temperature_range_low", jsonItem.jValue);
                            } else if (jsonValue instanceof Integer) {
                                minTemp = (Integer) jsonValue;
                                kvParam.put("nozzle_temperature_range_low", String.valueOf(jsonItem.jValue));
                            }
                        } else if (jsonItem.jKey.equalsIgnoreCase("isSoluble")) {
                            kvParam.put("filament_soluble", String.valueOf(Boolean.parseBoolean((String) jsonItem.jValue) ? 1 : 0));
                        } else if (jsonItem.jKey.equalsIgnoreCase("isSupport")) {
                            kvParam.put("filament_is_support", String.valueOf(Boolean.parseBoolean((String) jsonItem.jValue) ? 1 : 0));
                        }

                        if (jsonItem.jKey.equalsIgnoreCase("brand") || jsonItem.jKey.equalsIgnoreCase("name")
                                || jsonItem.jKey.equalsIgnoreCase("meterialtype") || jsonItem.jKey.equalsIgnoreCase("colors")
                                || jsonItem.jKey.equalsIgnoreCase("id")) {
                            base.put(jsonItem.jKey, jsonItem.jValue);
                        } else {
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
                        showToast("ID: " + base.get("id") + " already exists", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (base.get("id").toString().isBlank() || base.get("id").toString().isEmpty()) {
                        showToast("ID cannot be empty", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (base.get("id").toString().length() != 5) {
                        showToast("ID must be 5 digits", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (base.get("brand").toString().isBlank() || base.get("brand").toString().isEmpty()) {
                        showToast("Brand cannot be empty", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (base.get("name").toString().isBlank() || base.get("name").toString().isEmpty()) {
                        showToast("Name cannot be empty", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (base.get("meterialType").toString().isBlank() || base.get("meterialType").toString().isEmpty()) {
                        showToast("MeterialType cannot be empty", Toast.LENGTH_SHORT);
                        return;
                    }

                    info.put("base", base);
                    addFilament(matDb, info);
                    setMatDb(PrinterType);
                } catch (Exception ignored) {
                }
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

            mainHandler.post(() -> {
                recyclerView.removeAllViewsInLayout();
                recyclerView.setAdapter(null);
                recyclerView.setAdapter(recycleAdapter);
            });

            addDialog.show();
        } catch (Exception ignored) {
        }
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

            sdl.updatedesc.setText(getString(R.string.upload_desc_printer));
            sdl.chkprevent.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(this, "prevent_" + PrinterType, isChecked));
            sdl.chkprevent.setChecked(GetSetting(this, "prevent_" + PrinterType, true));

            sdl.chkreboot.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(this, "reboot_" + PrinterType, isChecked));
            sdl.chkreboot.setChecked(GetSetting(this, "reboot_" + PrinterType, true));

            sdl.chkreset.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {
                    sdl.btnupload.setText(R.string.reset);
                    sdl.chkreboot.setVisibility(View.INVISIBLE);
                    sdl.chkprevent.setVisibility(View.INVISIBLE);
                    sdl.updatedesc.setText(getString(R.string.upload_desc_printer).replace("update", "reset"));
                } else {
                    sdl.btnupload.setText(R.string.upload);
                    sdl.chkreboot.setVisibility(View.VISIBLE);
                    sdl.chkprevent.setVisibility(View.VISIBLE);
                    sdl.updatedesc.setText(getString(R.string.upload_desc_printer));
                }
            });

            String sshDefault;
            if (PrinterType.toLowerCase().contains("hi")) {
                sshDefault = "Creality2024";
            } else if (PrinterType.toLowerCase().contains("k1")) {
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
                sdl.txtmsg.setTextColor(getResources().getColor(R.color.text_color));
                if (sdl.chkreset.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    SpannableString titleText = new SpannableString("Warning!");
                    titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
                    SpannableString messageText;
                    messageText = new SpannableString("This will restore the default printer database");
                    messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
                    builder.setTitle(titleText);
                    builder.setMessage(messageText);
                    builder.setPositiveButton("Reset", (dialog, which) -> {
                        sdl.txtmsg.setText(R.string.resetting);
                        executorService.execute(() -> {
                            try {
                                restorePrinterDB(this, psw, host, PrinterType);
                                mainHandler.post(() -> {
                                    sdl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                                    sdl.txtmsg.setText(R.string.printer_database_has_been_reset);
                                });
                            } catch (Exception ignored) {
                                mainHandler.post(() -> {
                                    sdl.txtmsg.setTextColor(Color.RED);
                                    sdl.txtmsg.setText(R.string.error_resetting_database);
                                });
                            }
                        });
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
                    version = format("%s", GetSetting(this, "version_" + PrinterType, -1L));
                }
                executorService.execute(() -> {
                    try {
                        if (host.isEmpty()) {
                            mainHandler.post(() -> {
                                sdl.txtmsg.setTextColor(Color.RED);
                                sdl.txtmsg.setText(R.string.please_enter_printer_ip_address);
                            });
                            return;
                        }
                        if (psw.isEmpty()) {
                            mainHandler.post(() -> {
                                sdl.txtmsg.setTextColor(Color.RED);
                                sdl.txtmsg.setText(R.string.please_enter_ssh_password);
                            });
                            return;
                        }
                        mainHandler.post(() -> sdl.txtmsg.setText(R.string.uploading));
                        saveDBToPrinter(matDb, psw, host, PrinterType, version, reboot);
                        mainHandler.post(() -> {
                            sdl.txtmsg.setTextColor(ContextCompat.getColor(this, R.color.text_color));
                            sdl.txtmsg.setText(R.string.upload_successful);
                        });

                    } catch (Exception ignored) {

                        mainHandler.post(() -> {
                            sdl.txtmsg.setTextColor(Color.RED);
                            sdl.txtmsg.setText(R.string.error_uploading_to_printer);
                        });

                    }
                });
            });
            saveDialog.show();
        } catch (Exception ignored) {
        }
    }

    private void updateColorDisplay(PickerDialogBinding dl, int currentRed, int currentGreen, int currentBlue) {
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
        filters[0] = new Utils.HexInputFilter();
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
                showToast(R.string.invalid_hex_code_please_use_rrggbb_format, Toast.LENGTH_LONG);
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
                    SaveSetting(this, "RGB_VIEW", false);
                } else if (header.getId() == dl.gradientPickerHeader.getId()) {
                    SaveSetting(this, "PICKER_VIEW", false);
                } else if (header.getId() == dl.presetColorsHeader.getId()) {
                    SaveSetting(this, "PRESET_VIEW", false);
                } else if (header.getId() == dl.photoColorHeader.getId()) {
                    SaveSetting(this, "PHOTO_VIEW", false);
                }
            } else {
                content.setVisibility(View.VISIBLE);
                toggleIcon.setImageResource(R.drawable.ic_arrow_up);
                if (header.getId() == dl.rgbSlidersHeader.getId()) {
                    SaveSetting(this, "RGB_VIEW", true);
                } else if (header.getId() == dl.gradientPickerHeader.getId()) {
                    SaveSetting(this, "PICKER_VIEW", true);
                    if (gradientBitmap == null) {
                        setupGradientPicker(dl);
                    }
                } else if (header.getId() == dl.presetColorsHeader.getId()) {
                    SaveSetting(this, "PRESET_VIEW", true);
                } else if (header.getId() == dl.photoColorHeader.getId()) {
                    SaveSetting(this, "PHOTO_VIEW", true);
                }
            }
        });
    }


    private void setupActivityResultLaunchers() {
        try {
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
                                showToast(R.string.failed_to_get_export_directory, Toast.LENGTH_SHORT);
                            }
                        } else {
                            showToast(R.string.export_cancelled, Toast.LENGTH_SHORT);
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
                                showToast(R.string.failed_to_select_import_file, Toast.LENGTH_SHORT);
                            }
                        } else {
                            showToast(R.string.import_cancelled, Toast.LENGTH_SHORT);
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
                            showToast(R.string.storage_permission_denied_cannot_perform_action, Toast.LENGTH_LONG);
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
                            showToast(R.string.photo_capture_cancelled_or_failed, Toast.LENGTH_SHORT);
                        }
                    }
            );
        } catch (Exception ignored) {}
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
                    showToast(R.string.cannot_write_to_selected_directory, Toast.LENGTH_LONG);
                    return;
                }
                String dbBaseName = dbFile.getName().replace(".db", "");
                DocumentFile dbDestFile = pickedDir.createFile("application/octet-stream", dbBaseName + ".db");
                if (dbDestFile != null) {
                    copyFileToUri(this, dbFile, dbDestFile.getUri());
                } else {
                    showToast(R.string.failed_to_create_db_backup_file, Toast.LENGTH_LONG);
                    return;
                }
                showToast(R.string.database_exported_successfully, Toast.LENGTH_LONG);
            } catch (Exception e) {
                showToast(getString(R.string.database_saf_export_failed) + e.getMessage(), Toast.LENGTH_LONG);
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
                showToast(R.string.database_exported_successfully_to_downloads_folder, Toast.LENGTH_LONG);
            } catch (Exception e) {
                showToast(getString(R.string.database_legacy_export_failed) + e.getMessage(), Toast.LENGTH_LONG);
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
        if (!Uri.decode(sourceUri.toString()).toLowerCase().contains("material_database_" + PrinterType.toLowerCase())) {
            showToast(format(getString(R.string.incorrect_database_file_selected_the_s_database_is_required), PrinterType.toUpperCase()), Toast.LENGTH_LONG);
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
                showToast(R.string.database_imported_successfully, Toast.LENGTH_LONG);
            } catch (Exception e) {
                showToast(getString(R.string.database_saf_import_failed) + e.getMessage(), Toast.LENGTH_LONG);
            } finally {
                if (filamentDB.INSTANCE == null) {
                    filamentDB.getInstance(this, PrinterType);
                    setMatDb(PrinterType);
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
                    showToast(format(getString(R.string.incorrect_database_file_selected_the_s_database_is_required), PrinterType.toUpperCase()), Toast.LENGTH_LONG);
                    return;
                }
                if (!sourceDbFile.exists()) {
                    showToast(getString(R.string.backup_file_not_found_in_downloads) + sourceDbFile.getName(), Toast.LENGTH_LONG);
                    return;
                }
                File dbDir = dbFile.getParentFile();
                if (dbDir != null && !dbDir.exists()) {
                    boolean val = dbDir.mkdirs();
                }
                copyFile(sourceDbFile, dbFile);
                filamentDB.getInstance(this, PrinterType);
                setMatDb(PrinterType);
                showToast(R.string.database_imported_successfully, Toast.LENGTH_LONG);

            } catch (Exception e) {
                showToast(getString(R.string.database_legacy_import_failed) + e.getMessage(), Toast.LENGTH_LONG);
            } finally {
                if (filamentDB.INSTANCE == null) {
                    filamentDB.getInstance(this, PrinterType);
                    setMatDb(PrinterType);
                }
            }
        });
    }


    private void showImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SpannableString titleText = new SpannableString(getString(R.string.import_database));
        titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
        SpannableString messageText = new SpannableString(format(getString(R.string.restore_s_database), PrinterType.toUpperCase()));
        messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
        builder.setTitle(titleText);
        builder.setMessage(messageText);
        builder.setPositiveButton(R.string.simport, (dialog, which) -> checkPermissionAndStartAction(ACTION_IMPORT));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
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
        SpannableString messageText = new SpannableString(format(getString(R.string.backup_s_database_material_database_s_db), PrinterType.toUpperCase(), PrinterType.toLowerCase()));
        messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
        builder.setTitle(titleText);
        builder.setMessage(messageText);
        builder.setPositiveButton(R.string.export, (dialog, which) -> checkPermissionAndStartAction(ACTION_EXPORT));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
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
        } else {
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
                showToast(R.string.camera_permission_is_required_to_take_photos, Toast.LENGTH_SHORT);
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
                        colorDialog.txtcolor.setText(format("%06X", (0xFFFFFF & pixel)));
                        setSlidersFromColor(colorDialog, Color.argb(255, Color.red(pixel), Color.green(pixel), Color.blue(pixel)));
                    } catch (Exception ignored) {
                    }
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
            tagDialog.setTitle(R.string.tag_memory);
            TagDialogBinding tdl = TagDialogBinding.inflate(getLayoutInflater());
            View rv = tdl.getRoot();
            tagDialog.setContentView(rv);
            tdl.btncls.setOnClickListener(v -> tagDialog.dismiss());
            tdl.btnread.setOnClickListener(v -> readTagMemory(tdl));
            tagView = tdl.recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.scrollToPosition(0);
            tagView.setLayoutManager(layoutManager);
            tagItems = new tagItem[0];
            tagAdapter = new tagAdapter(this, tagItems);
            tagView.setAdapter(tagAdapter);
            tagDialog.show();
            if (currentTag != null) {
                readTagMemory(tdl);
            }
        } catch (Exception ignored) {
        }
    }


    void readTagMemory(TagDialogBinding tdl) {
        if (currentTag == null) {
            showToast(getString(R.string.no_tag_found), Toast.LENGTH_SHORT);
            return;
        }
        executorService.execute(() -> {
            try (MifareClassic mfc = MifareClassic.get(currentTag)) {
                try {
                    mfc.connect();
                    int sectorCount = mfc.getSectorCount();
                    int currentBlock = 0;
                    mainHandler.post(() -> tdl.lbldesc.setText(getTypeName(mfc.getType())));
                    tagItems = new tagItem[sectorCount * 16];
                    boolean auth;
                    for (int s = 0; s < sectorCount; s++) {
                        if (s == 1) {
                            byte[] key = MifareClassic.KEY_DEFAULT;
                            if (encrypted) {
                                key = encKey;
                            }
                            auth = mfc.authenticateSectorWithKeyA(1, key);
                        } else {
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
                                tagItems[currentBlock].tKey = format(Locale.getDefault(), "Block %d | %s", currentBlock, definition);
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
                            tagItems[currentBlock + 1] = new tagItem();
                            tagItems[currentBlock + 1].tKey = "FAILED AUTHENTICATION";
                            tagItems[currentBlock + 1].tValue = "Key Required";
                            tagItems[currentBlock + 1].tImage = AppCompatResources.getDrawable(this, R.drawable.failed);
                        }
                    }
                    tagItem[] filledItems = new tagItem[currentBlock + 1];
                    System.arraycopy(tagItems, 0, filledItems, 0, currentBlock + 1);
                    mainHandler.post(() -> {
                        tagAdapter = new tagAdapter(this, filledItems);
                        tagAdapter.setHasStableIds(true);
                        tagView.setAdapter(tagAdapter);
                    });
                } catch (Exception ignored) {
                    showToast(getString(R.string.error_reading_tag), Toast.LENGTH_SHORT);
                } finally {
                    try {
                        if (mfc.isConnected()) mfc.close();
                    } catch (Exception ignored) {
                    }
                }
            } catch (Exception ignored) {
                showToast(getString(R.string.no_tag_found), Toast.LENGTH_SHORT);
            }
        });
    }


    private void showToast(final Object content, final int duration) {
        mainHandler.post(() -> {
            try {
                if (currentToast != null) currentToast.cancel();
                if (content instanceof Integer) {
                    currentToast = Toast.makeText(this, (Integer) content, duration);
                } else if (content instanceof String) {
                    currentToast = Toast.makeText(this, (String) content, duration);
                } else {
                    currentToast = Toast.makeText(this, String.valueOf(content), duration);
                }
                currentToast.show();
            } catch (Exception ignored) {
            }
        });
    }


    void openManage(boolean isEmpty) {
        try {
            printerDialog = new Dialog(this, R.style.Theme_SpoolID);
            printerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            printerDialog.setCanceledOnTouchOutside(false);
            printerDialog.setTitle(R.string.update);
            ManageDialogBinding mdl = ManageDialogBinding.inflate(getLayoutInflater());
            View rv = mdl.getRoot();
            printerDialog.setContentView(rv);
            PrinterManager manager = new PrinterManager(this);
            List<String> items = manager.getList();

            if (isEmpty) {
                mdl.txtmsg.setTextColor(Color.RED);
                mdl.txtmsg.setText(R.string.add_a_printer_to_get_started);
            }

            executorService.execute(() -> {
                try {
                    String searchNozzle = "0.4";
                    JSONArray matches = findPrinters(this, printerTypes, searchNozzle);
                    mainHandler.post(() -> {
                        try {
                            if (matches.length() > 0) {
                                List<PrinterOption> options = new ArrayList<>();
                                for (int i = 0; i < matches.length(); i++) {
                                    JSONObject printer = matches.getJSONObject(i);
                                    String label = printer.getString("name");
                                    if (label.equalsIgnoreCase("creality hi") && printerDb.contains("HI")) {
                                        printer.put("name", "HI");
                                        options.add(new PrinterOption("HI", printer));
                                    } else {
                                        options.add(new PrinterOption(label, printer));
                                    }
                                }
                                Collections.sort(options, (o1, o2) -> o1.displayName.trim().compareToIgnoreCase(o2.displayName.trim()));
                                manageAdapter = new spinnerAdapter(this, options, printerDb);
                                mdl.type.setAdapter(manageAdapter);
                                mdl.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        PrinterOption selected = (PrinterOption) parent.getItemAtPosition(position);
                                        try {
                                            String thumbnail = selected.data.getString("thumbnail");

                                            if (items.contains(selected.data.getString("name"))) {
                                                mdl.btnrem.setVisibility(View.VISIBLE);
                                                mdl.txtmsg.setTextColor(Color.RED);
                                                mdl.txtmsg.setText(R.string.this_printer_is_already_added);
                                            } else {
                                                mdl.btnrem.setVisibility(View.INVISIBLE);
                                                if (!isEmpty) {
                                                    mdl.txtmsg.setText("");
                                                }
                                            }
                                            loadImage(thumbnail, mdl.imgprinter);
                                        } catch (Exception ignored) {
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });
                            }
                        } catch (Exception ignored) {
                        }
                    });
                } catch (Exception ignored) {
                }
            });

            mdl.btncls.setOnClickListener(v -> printerDialog.dismiss());

            mdl.btnrem.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                SpannableString titleText = new SpannableString(getString(R.string.remove_printer));
                titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
                SpannableString messageText = new SpannableString(format(getString(R.string.do_you_want_to_remove_s), PrinterType.toUpperCase()));
                messageText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, messageText.length(), 0);
                builder.setTitle(titleText);
                builder.setMessage(messageText);
                builder.setPositiveButton(R.string.remove, (dialog, which) -> {
                    int pos = mdl.type.getSelectedItemPosition();
                    if (pos != -1) {
                        String printerName = mdl.type.getItemAtPosition(pos).toString();
                        manager.removeItem(printerName);
                        items.remove(printerName);
                        mdl.btnrem.setVisibility(View.INVISIBLE);
                        mdl.txtmsg.setText("");
                        printerDb.remove(printerName);
                        String dbName = "material_database_" + printerName.toLowerCase();
                        filamentDB.getInstance(this, dbName).close();
                        deleteDatabase(dbName);
                        main.brand.setAdapter(null);
                        main.material.setAdapter(null);
                        padapter.notifyDataSetChanged();
                        manageAdapter.notifyDataSetChanged();
                        showToast(getString(R.string.removed) + printerName, Toast.LENGTH_SHORT);
                    }
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

            mdl.btnadd.setOnClickListener(v -> {
                mdl.btnadd.setEnabled(false);
                PrinterOption selected = (PrinterOption) mdl.type.getItemAtPosition(mdl.type.getSelectedItemPosition());
                executorService.execute(() -> {
                    try {
                        String printerName = selected.data.getString("name");
                        showToast(String.format(Locale.getDefault(), getString(R.string.adding_s), printerName), Toast.LENGTH_SHORT);
                        if (items.contains(printerName)) {
                            showToast(getString(R.string.printer_already_exists), Toast.LENGTH_SHORT);
                            mainHandler.post(() -> mdl.btnadd.setEnabled(true));
                            return;
                        }
                        String json = getJsonDB(this, printerName, "0.4");
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            filamentDB tdb = filamentDB.getInstance(this, printerName.toLowerCase());
                            populateDatabase(this, tdb.matDB(), json, printerName.toLowerCase());
                            SaveSetting(this, "version_" + printerName.toLowerCase(), newVer);
                            manager.addItem(printerName);
                            if (tdb.isOpen()) {
                                tdb.close();
                            }
                            printerDb.add(printerName);
                            mainHandler.postDelayed(() -> {
                                padapter.notifyDataSetChanged();
                                showToast(printerName + getString(R.string.added), Toast.LENGTH_SHORT);
                                printerDialog.dismiss();
                            }, 100);
                        } else {
                            showToast(getString(R.string.unable_to_download_printer_data), Toast.LENGTH_SHORT);
                            mainHandler.post(() -> mdl.btnadd.setEnabled(true));
                        }
                    } catch (Exception ignored) {
                        mainHandler.post(() -> {
                            printerDialog.dismiss();
                            showToast(getString(R.string.error_adding_printer), Toast.LENGTH_SHORT);
                        });
                    }
                });
            });
            printerDialog.show();
        } catch (Exception ignored) {
        }
    }


    void AddSpoolManSpool() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        SpannableString titleText = new SpannableString(getString(R.string.add_spool_to_spoolman));
        titleText.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), 0, titleText.length(), 0);
        builder.setTitle(titleText);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding, padding, 0);
        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        String[] headers = {"Vendor:", "Type:", "Color:", "Weight:"};
        String[] values = {MaterialVendor, MaterialName, MaterialColor, Utils.GetMaterialIntWeight(MaterialWeight) + "g"};
        for (int i = 0; i < headers.length; i++) {
            TableRow row = new TableRow(this);
            row.setPadding(0, 0, 0, 12);
            TextView tvHeader = new TextView(this);
            tvHeader.setText(headers[i]);
            tvHeader.setTypeface(null, Typeface.BOLD);
            tvHeader.setTextColor(Color.DKGRAY);
            tvHeader.setPadding(0, 0, 32, 0);
            TextView tvValue = new TextView(this);
            tvValue.setText(values[i]);
            tvValue.setTextColor(Color.DKGRAY);
            row.addView(tvHeader);
            row.addView(tvValue);
            table.addView(row);
        }
        container.addView(table);
        com.google.android.material.textfield.TextInputLayout textInputLayout =
                new com.google.android.material.textfield.TextInputLayout(new ContextThemeWrapper(this,
                        com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, (int) (12 * getResources().getDisplayMetrics().density), 0, 0);
        textInputLayout.setLayoutParams(lp);
        textInputLayout.setHint(R.string.enter_color_name);
        textInputLayout.setBoxBackgroundColor(Color.WHITE);
        textInputLayout.setHintEnabled(true);
        textInputLayout.setExpandedHintEnabled(false);
        com.google.android.material.textfield.TextInputEditText input =
                new com.google.android.material.textfield.TextInputEditText(textInputLayout.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        input.setTextColor(Color.BLACK);
        input.setText("");
        input.setHintTextColor(Color.LTGRAY);
        String colorNameHint = matcher.findNearestColor(MaterialColor);
        input.setHint(colorNameHint == null ? "Blue" : colorNameHint);
        InputFilter[] filters = new InputFilter[2];
        filters[0] = new Utils.TextInputFilter();
        filters[1] = new InputFilter.LengthFilter(32);
        input.setFilters(filters);
        textInputLayout.addView(input);
        container.addView(textInputLayout);
        builder.setView(container);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.submit, (dialog, which) -> {
            String colorName = Objects.requireNonNull(input.getText()).toString().trim();
            String smHost = GetSetting(this, "smhost", "");
            int smPort = GetSetting(this, "smport", 7912);
            if (!smHost.isEmpty()) {
                executorService.execute(() -> {
                    String ret;
                    if (colorName.isEmpty()) {
                        ret = smAddSpool(this, matDb, smHost, smPort, SelectedPrinter, MaterialID, MaterialColor, colorNameHint == null ? MaterialColor : colorNameHint, GetMaterialIntWeight(MaterialWeight));
                    } else {
                        ret = smAddSpool(this, matDb, smHost, smPort, SelectedPrinter, MaterialID, MaterialColor, colorName, GetMaterialIntWeight(MaterialWeight));
                    }
                    if (ret != null) showToast(ret, Toast.LENGTH_SHORT);
                });
            } else {
                showToast(getString(R.string.spoolman_host_is_not_set), Toast.LENGTH_SHORT);
            }
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawableResource(android.R.color.white);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1976D2"));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1976D2"));
        }
    }


    public void openSettings() {
        settingsDialog = new Dialog(this, R.style.Theme_SpoolID);
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setCanceledOnTouchOutside(false);
        settingsDialog.setTitle(R.string.settings);
        SettingsDialogBinding sdl = SettingsDialogBinding.inflate(getLayoutInflater());
        View rv = sdl.getRoot();
        settingsDialog.setContentView(rv);
        sdl.readswitch.setChecked(GetSetting(this, "autoread", false));
        sdl.readswitch.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(this, "autoread", isChecked));
        sdl.launchswitch.setChecked(GetSetting(this, "autoLaunch", true));
        sdl.launchswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNfcLaunchMode(this, isChecked);
            SaveSetting(this, "autoLaunch", isChecked);
        });
        sdl.spoolswitch.setChecked(GetSetting(this, "enablesm", false));
        sdl.smhost.setText(GetSetting(this, "smhost", ""));
        sdl.smport.setText(String.valueOf(GetSetting(this, "smport", 7912)));
        sdl.smhost.setEnabled(sdl.spoolswitch.isChecked());
        sdl.smport.setEnabled(sdl.spoolswitch.isChecked());
        sdl.spoolswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sdl.smhost.setEnabled(isChecked);
            sdl.smport.setEnabled(isChecked);
            if (isChecked) {
                sdl.smhost.setTextColor(getResources().getColor(R.color.text_color));
                sdl.smport.setTextColor(getResources().getColor(R.color.text_color));
                main.txtspman.setVisibility(View.VISIBLE);
                if (matcher == null)
                {
                    executorService.execute(() -> matcher = new ColorMatcher(context));
                }
            }
            else {
                sdl.smhost.setTextColor(Color.GRAY);
                sdl.smport.setTextColor(Color.GRAY);
                main.txtspman.setVisibility(View.INVISIBLE);
            }
            SaveSetting(this, "enablesm", isChecked);
        });
        if (sdl.spoolswitch.isChecked()) {
            sdl.smhost.setTextColor(getResources().getColor(R.color.text_color));
            sdl.smport.setTextColor(getResources().getColor(R.color.text_color));
        }
        else {
            sdl.smhost.setTextColor(Color.GRAY);
            sdl.smport.setTextColor(Color.GRAY);
        }
        sdl.btncls.setOnClickListener(v -> settingsDialog.dismiss());
        settingsDialog.setOnDismissListener(dialogInterface -> {
            SaveSetting(this, "smhost", Objects.requireNonNull(sdl.smhost.getText()).toString());
            SaveSetting(this, "smport", Integer.parseInt(Objects.requireNonNull(sdl.smport.getText()).toString()));
        });
        settingsDialog.show();
    }



}