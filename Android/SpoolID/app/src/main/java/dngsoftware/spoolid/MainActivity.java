package dngsoftware.spoolid;

import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static java.lang.String.format;
import static dngsoftware.spoolid.Utils.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.text.InputFilter;
import android.text.InputType;
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
import android.view.inputmethod.InputMethodManager;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
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
import dngsoftware.spoolid.databinding.SpoolDialogBinding;
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
    Dialog pickerDialog, customDialog, saveDialog, updateDialog, editDialog, addDialog, tagDialog, printerDialog, settingsDialog, spoolDialog;
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
    private Activity activity;
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
        activity = this;
        setThemeMode(GetSetting(context, "enabledm", false));

        main = ActivityMainBinding.inflate(getLayoutInflater());
        View rv = main.getRoot();
        setContentView(rv);
        SetPermissions(context);

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        setupActivityResultLaunchers();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        PrinterManager manager = new PrinterManager(context);
        printerDb = manager.getList();

        PrinterType = GetSetting(context, "printer", "");

        if (GetSetting(context, "enablesm", false))
        {
            main.txtspman.setVisibility(View.VISIBLE);
            executorService.execute(() -> matcher = new ColorMatcher(context));
        }
        else {
            main.txtspman.setVisibility(View.INVISIBLE);
        }
        main.txtspman.setOnClickListener(view ->
        {
            if (GetSetting(context, "enablesm", false))
            {
                openSpoolAdd();
            }
        });

        if (PrinterType.isEmpty()) {
            SaveSetting(context, "newformat", true);
        } else {
            if (printerDb.isEmpty() && !GetSetting(context, "newformat", false)) {
                printerDb.add("K2");
                printerDb.add("K1");
                printerDb.add("HI");
                manager.saveList(printerDb);
                SaveSetting(context, "newformat", true);
            }
        }

        if (printerDb.isEmpty()) {
            openManage(true);
        }

        padapter = new ArrayAdapter<>(context, R.layout.spinner_item, printerDb);
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

        main.txtcolor.setText(MaterialColor);
        main.txtcolor.setTextColor(getContrastColor(Color.parseColor("#" + MaterialColor)));

        main.colorview.setOnClickListener(view -> openPicker());
        main.readbutton.setOnClickListener(view -> ReadSpoolData());

        main.addbutton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            SpannableString titleText = new SpannableString("Create Filament?");
            titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
            SpannableString messageText = new SpannableString("Using " + MaterialName + " as a template");
            messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
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
                alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
            }
        });

        main.editbutton.setOnClickListener(view -> loadEdit());

        main.deletebutton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            SpannableString titleText = new SpannableString("Delete Filament?");
            titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
            SpannableString messageText = new SpannableString("Brand:  " + GetMaterialBrand(matDb, MaterialID) + "\nType:    " + MaterialName);
            messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
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
                alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
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

        sadapter = new ArrayAdapter<>(context, R.layout.spinner_item, materialWeights);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            nfcAdapter = NfcAdapter.getDefaultAdapter(context);
            if (nfcAdapter != null && nfcAdapter.isEnabled()) {
                Bundle options = new Bundle();
                options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);
                nfcAdapter.enableReaderMode(activity, this, NfcAdapter.FLAG_READER_NFC_A, options);
            }
        }catch (Exception ignored) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (nfcAdapter != null) {
                nfcAdapter.disableReaderMode(this);
            }
        } catch (Exception ignored) {}
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
        if (spoolDialog != null && spoolDialog.isShowing()) {
            spoolDialog.dismiss();
        }
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            try {
                nfcAdapter.disableReaderMode(activity);
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
                    main.tagid.setTextColor(ContextCompat.getColor(context,R.color.primary_error));
                    main.tagid.setText(R.string.error);
                    return;
                }
                showToast(getString(R.string.tag_found) + bytesToHex(currentTag.getId()), Toast.LENGTH_SHORT);
                main.tagid.setTextColor(ContextCompat.getColor(context,R.color.text_main));
                main.tagid.setText(bytesToHex(currentTag.getId()));
                encKey = createKey(currentTag.getId());
                CheckTag();
                if (encrypted) {
                    main.tagid.setText(format("\uD83D\uDD10 %s", bytesToHex(currentTag.getId())));
                }
                if (GetSetting(context, "autoread", false)) {
                    ReadSpoolData();
                }
            });
        } catch (Exception ignored) {
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

    void setMatDb(String pType) {
        try {
            if (rdb != null && rdb.isOpen()) {
                rdb.close();
            }
            rdb = filamentDB.getInstance(context, pType);
            matDb = rdb.matDB();
            mainHandler.post(() -> {
                try {
                    main.writebutton.setOnClickListener(view -> WriteSpoolData(MaterialID, MaterialColor, GetMaterialLength(MaterialWeight)));
                    badapter = new ArrayAdapter<>(context, R.layout.spinner_item, getMaterialBrands(matDb));
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
                    madapter = new ArrayAdapter<>(context, R.layout.spinner_item, getMaterials(matDb, badapter.getItem(main.brand.getSelectedItemPosition())));
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
        madapter = new ArrayAdapter<>(context, R.layout.spinner_item, getMaterials(matDb, brand));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            SpannableString titleText = new SpannableString(getString(R.string.format_tag_q));
            titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
            SpannableString messageText = new SpannableString(getString(R.string.erase_message));
            messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
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
                alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
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
                                main.txtcolor.setText(MaterialColor);
                                main.txtcolor.setTextColor(getContrastColor(Color.parseColor("#" + MaterialColor)));
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
            pickerDialog = new Dialog(context, R.style.Theme_SpoolID);
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
                            main.txtcolor.setText(MaterialColor);
                            main.txtcolor.setTextColor(getContrastColor(Color.parseColor("#" + MaterialColor)));
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
                    GetSetting(context, "RGB_VIEW", false)
            );
            setupCollapsibleSection(dl,
                    dl.gradientPickerHeader,
                    dl.gradientPickerContent,
                    dl.gradientPickerToggleIcon,
                    GetSetting(context, "PICKER_VIEW", true)
            );
            setupCollapsibleSection(dl,
                    dl.presetColorsHeader,
                    dl.presetColorsContent,
                    dl.presetColorsToggleIcon,
                    GetSetting(context, "PRESET_VIEW", true)
            );
            setupCollapsibleSection(dl,
                    dl.photoColorHeader,
                    dl.photoColorContent,
                    dl.photoColorToggleIcon,
                    GetSetting(context, "PHOTO_VIEW", false)
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
            customDialog = new Dialog(context, R.style.Theme_SpoolID);
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            customDialog.setCanceledOnTouchOutside(false);
            customDialog.setTitle(R.string.custom_tag_data);
            manual = ManualDialogBinding.inflate(getLayoutInflater());
            View rv = manual.getRoot();
            customDialog.setContentView(rv);
            manual.txtmonth.setText(GetSetting(context, "mon", getResources().getString(R.string.def_mon)));
            manual.txtday.setText(GetSetting(context, "day", getResources().getString(R.string.def_day)));
            manual.txtyear.setText(GetSetting(context, "yr", getResources().getString(R.string.def_yr)));
            manual.txtvendor.setText(GetSetting(context, "ven", getResources().getString(R.string.def_ven)));
            manual.txtbatch.setText(GetSetting(context, "bat", getResources().getString(R.string.def_bat)));
            manual.txtmaterial.setText(GetSetting(context, "mat", getResources().getString(R.string.def_mat)));
            manual.txtcolor.setText(GetSetting(context, "col", getResources().getString(R.string.def_col)));
            manual.txtlength.setText(GetSetting(context, "len", getResources().getString(R.string.def_len)));
            manual.txtserial.setText(GetSetting(context, "ser", getResources().getString(R.string.def_ser)));
            manual.txtreserve.setText(GetSetting(context, "res", getResources().getString(R.string.def_res)));
            manual.btncls.setOnClickListener(v -> customDialog.dismiss());
            manual.layoutColor.setEndIconOnClickListener(view -> openPicker());
            manual.layoutSerial.setEndIconOnClickListener(v -> {
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
                if (Objects.requireNonNull(manual.txtmonth.getText()).length() == 1 && Objects.requireNonNull(manual.txtday.getText()).length() == 2 && Objects.requireNonNull(manual.txtyear.getText()).length() == 2
                        && Objects.requireNonNull(manual.txtvendor.getText()).length() == 4 && Objects.requireNonNull(manual.txtbatch.getText()).length() == 2 && Objects.requireNonNull(manual.txtmaterial.getText()).length() == 6
                        && Objects.requireNonNull(manual.txtcolor.getText()).length() == 7 && Objects.requireNonNull(manual.txtlength.getText()).length() == 4
                        && Objects.requireNonNull(manual.txtserial.getText()).length() == 6 && Objects.requireNonNull(manual.txtreserve.getText()).length() == 6) {
                    WriteTag(manual.txtmonth.getText().toString() + manual.txtday.getText().toString() + manual.txtyear.getText().toString()
                            + manual.txtvendor.getText().toString() + manual.txtbatch.getText().toString() + manual.txtmaterial.getText().toString() + manual.txtcolor.getText().toString()
                            + manual.txtlength.getText().toString() + manual.txtserial.getText().toString() + manual.txtreserve.getText().toString());
                    SaveSetting(context, "mon", manual.txtmonth.getText().toString().toUpperCase());
                    SaveSetting(context, "day", manual.txtday.getText().toString().toUpperCase());
                    SaveSetting(context, "yr", manual.txtyear.getText().toString().toUpperCase());
                    SaveSetting(context, "ven", manual.txtvendor.getText().toString().toUpperCase());
                    SaveSetting(context, "bat", manual.txtbatch.getText().toString().toUpperCase());
                    SaveSetting(context, "mat", manual.txtmaterial.getText().toString().toUpperCase());
                    SaveSetting(context, "col", manual.txtcolor.getText().toString().toUpperCase());
                    SaveSetting(context, "len", manual.txtlength.getText().toString().toUpperCase());
                    SaveSetting(context, "ser", manual.txtserial.getText().toString().toUpperCase());
                    SaveSetting(context, "res", manual.txtreserve.getText().toString().toUpperCase());
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
                SaveSetting(context, "mon", Objects.requireNonNull(manual.txtmonth.getText()).toString().toUpperCase());
                SaveSetting(context, "day", Objects.requireNonNull(manual.txtday.getText()).toString().toUpperCase());
                SaveSetting(context, "yr", Objects.requireNonNull(manual.txtyear.getText()).toString().toUpperCase());
                SaveSetting(context, "ven", Objects.requireNonNull(manual.txtvendor.getText()).toString().toUpperCase());
                SaveSetting(context, "bat", Objects.requireNonNull(manual.txtbatch.getText()).toString().toUpperCase());
                SaveSetting(context, "mat", Objects.requireNonNull(manual.txtmaterial.getText()).toString().toUpperCase());
                SaveSetting(context, "col", Objects.requireNonNull(manual.txtcolor.getText()).toString().toUpperCase());
                SaveSetting(context, "len", Objects.requireNonNull(manual.txtlength.getText()).toString().toUpperCase());
                SaveSetting(context, "ser", Objects.requireNonNull(manual.txtserial.getText()).toString().toUpperCase());
                SaveSetting(context, "res", Objects.requireNonNull(manual.txtreserve.getText()).toString().toUpperCase());
                showToast(R.string.values_reset, Toast.LENGTH_SHORT);
            });
            customDialog.show();
        } catch (Exception ignored) {
        }
    }

    void openUpdate() {
        try {
            updateDialog = new Dialog(context, R.style.Theme_SpoolID);
            updateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            updateDialog.setCanceledOnTouchOutside(false);
            updateDialog.setTitle(R.string.update);
            UpdateDialogBinding dl = UpdateDialogBinding.inflate(getLayoutInflater());
            View rv = dl.getRoot();
            updateDialog.setContentView(rv);
            dl.chkpres.setChecked(GetSetting(context, "preserve", false));
            dl.chkpres.setOnClickListener(v -> {
                SaveSetting(context, "preserve", dl.chkpres.isChecked());
            });
            executorService.execute(() -> {
                try {
                    String searchName = PrinterType;
                    String searchNozzle = "0.4";
                    JSONArray matches = findPrinters(context, searchName, searchNozzle);
                    mainHandler.post(() -> {
                        try {
                            if (matches.length() > 0) {
                                List<PrinterOption> options = new ArrayList<>();
                                for (int i = 0; i < matches.length(); i++) {
                                    JSONObject printer = matches.getJSONObject(i);
                                    String label = printer.getString("name");
                                    options.add(new PrinterOption(label, printer));
                                }
                                ArrayAdapter<PrinterOption> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, options);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    SpannableString titleText = new SpannableString("Update Information");
                    titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
                    Date updDate = new Date(jsonVersion * 1000L);
                    DateFormat df = android.text.format.DateFormat.getMediumDateFormat(context);
                    SpannableString messageText = new SpannableString(selected.data.getString("name") + " (" +
                            selected.data.getString("printerIntName") + ")\n" + df.format(updDate) + "\n" + jsonVersion + " (" + selected.data.getString("showVersion") + ")\n\n" +
                            selected.data.getString("descriptionI18n") + "\n\n");
                    messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
                    builder.setTitle(titleText);
                    builder.setMessage(messageText);
                    builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
                    AlertDialog alert = builder.create();
                    alert.show();
                    if (alert.getWindow() != null) {
                        alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                    }
                } catch (Exception ignored) {
                }
            });

            dl.chkprnt.setChecked(GetSetting(context, "fromprinter_" + PrinterType, false));
            dl.chkprnt.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SaveSetting(context, "fromprinter_" + PrinterType, isChecked);
                if (isChecked) {
                    dl.layoutAddress.setVisibility(View.VISIBLE);
                    dl.layoutPsw.setVisibility(View.VISIBLE);
                    dl.updatedesc.setVisibility(View.VISIBLE);
                    dl.updatedesc.setVisibility(View.VISIBLE);
                    dl.btnchk.setVisibility(View.VISIBLE);
                    dl.imgframe.setVisibility(View.INVISIBLE);
                    dl.type.setVisibility(View.INVISIBLE);
                    dl.typeborder.setVisibility(View.INVISIBLE);
                    dl.btnupd.setVisibility(View.INVISIBLE);
                    dl.lbltype.setVisibility(View.INVISIBLE);
                    dl.txtnewver.setText("");
                } else {
                    dl.layoutAddress.setVisibility(View.INVISIBLE);
                    dl.layoutPsw.setVisibility(View.INVISIBLE);
                    dl.updatedesc.setVisibility(View.INVISIBLE);
                    dl.updatedesc.setVisibility(View.INVISIBLE);
                    dl.btnchk.setVisibility(View.INVISIBLE);
                    dl.imgframe.setVisibility(View.VISIBLE);
                    dl.type.setVisibility(View.VISIBLE);
                    dl.typeborder.setVisibility(View.VISIBLE);
                    dl.lbltype.setVisibility(View.VISIBLE);
                    dl.btnupd.setVisibility(View.VISIBLE);
                    dl.txtnewver.setText(format(Locale.getDefault(), getString(R.string.printer_version), jsonVersion));
                }
                dl.txtmsg.setText("");
            });

            if (dl.chkprnt.isChecked()) {
                dl.layoutAddress.setVisibility(View.VISIBLE);
                dl.layoutPsw.setVisibility(View.VISIBLE);
                dl.updatedesc.setVisibility(View.VISIBLE);
                dl.btnchk.setVisibility(View.VISIBLE);
                dl.imgframe.setVisibility(View.INVISIBLE);
                dl.type.setVisibility(View.INVISIBLE);
                dl.typeborder.setVisibility(View.INVISIBLE);
                dl.lbltype.setVisibility(View.INVISIBLE);
                dl.btnupd.setVisibility(View.INVISIBLE);
                dl.txtnewver.setText("");
            } else {
                dl.layoutAddress.setVisibility(View.INVISIBLE);
                dl.layoutPsw.setVisibility(View.INVISIBLE);
                dl.btnchk.setVisibility(View.INVISIBLE);
                dl.btnupd.setVisibility(View.VISIBLE);
                dl.imgframe.setVisibility(View.VISIBLE);
                dl.type.setVisibility(View.VISIBLE);
                dl.typeborder.setVisibility(View.VISIBLE);
                dl.lbltype.setVisibility(View.VISIBLE);
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

            dl.txtpsw.setText(GetSetting(context, "psw_" + PrinterType, sshDefault));
            dl.txtaddress.setText(GetSetting(context, "host_" + PrinterType, ""));
            dl.btncls.setOnClickListener(v -> updateDialog.dismiss());
            dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), GetSetting(context, "version_" + PrinterType, -1L)));
            dl.txtprinter.setText(format(getString(R.string.creality_type), PrinterType.toUpperCase().replace("CREALITY", "")));

            dl.btnchk.setOnClickListener(v -> {
                String host = Objects.requireNonNull(dl.txtaddress.getText()).toString();
                String psw = Objects.requireNonNull(dl.txtpsw.getText()).toString();
                dl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                dl.txtmsg.setText(R.string.checking_for_updates);
                long version = GetSetting(context, "version_" + PrinterType, -1L);
                dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), version));
                executorService.execute(() -> {
                    try {
                        String json;
                        if (GetSetting(context, "fromprinter_" + PrinterType, false)) {
                            SaveSetting(context, "host_" + PrinterType, host);
                            SaveSetting(context, "psw_" + PrinterType, psw);

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
                                    dl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                                    dl.txtmsg.setText(R.string.update_available);
                                } else {
                                    dl.btnupd.setVisibility(View.INVISIBLE);
                                    dl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
                String host = GetSetting(context, "host_" + PrinterType, "");
                String psw = GetSetting(context, "psw_" + PrinterType, sshDefault);
                dl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                dl.txtmsg.setText(R.string.downloading_update);
                dl.btnupd.setEnabled(false);
                executorService.execute(() -> {
                    try {
                        String json;
                        if (GetSetting(context, "fromprinter_" + PrinterType, false)) {
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
                            json = getJsonDB(context, PrinterType, "0.4");
                        }
                        mainHandler.post(() -> dl.txtmsg.setText(R.string.processing_update));
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            if (!dl.chkpres.isChecked()) matDb.deleteAll();
                            populateDatabase(context, matDb, json, PrinterType);
                            SaveSetting(context, "version_" + PrinterType, newVer);
                            mainHandler.postDelayed(() -> {
                                dl.txtcurver.setText(format(Locale.getDefault(), getString(R.string.current_version), newVer));
                                dl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
            editDialog = new Dialog(context, R.style.Theme_SpoolID);
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
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
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
                recyclerView.setAdapter(recycleAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (Math.abs(dy) > 10) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                            }
                            View focusedView = recyclerView.getFocusedChild();
                            if (focusedView != null) {
                                focusedView.clearFocus();
                            }
                        }
                    }
                });
            });
            editDialog.show();
        } catch (Exception ignored) {
        }
    }

    void loadAdd() {
        try {
            RecyclerView recyclerView;
            jsonAdapter recycleAdapter;
            addDialog = new Dialog(context, R.style.Theme_SpoolID);
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
                            kvParam.put("filament_soluble",jsonItem.jValue);
                        } else if (jsonItem.jKey.equalsIgnoreCase("isSupport")) {
                            kvParam.put("filament_is_support",  jsonItem.jValue);
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
                } catch (Exception ignored) {}
                addDialog.dismiss();
            });

            recyclerView = adl.recyclerView;

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(context);
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
                recyclerView.setAdapter(recycleAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (Math.abs(dy) > 10) {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
                            }
                            View focusedView = recyclerView.getFocusedChild();
                            if (focusedView != null) {
                                focusedView.clearFocus();
                            }
                        }
                    }
                });
            });
            addDialog.show();
        } catch (Exception ignored) {
        }
    }

    void openUpload() {
        try {
            saveDialog = new Dialog(context, R.style.Theme_SpoolID);
            saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            saveDialog.setCanceledOnTouchOutside(false);
            saveDialog.setTitle("Upload to Printer");
            SaveDialogBinding sdl = SaveDialogBinding.inflate(getLayoutInflater());
            View rv = sdl.getRoot();
            saveDialog.setContentView(rv);

            sdl.updatedesc.setText(getString(R.string.upload_desc_printer));
            sdl.chkprevent.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(context, "prevent_" + PrinterType, isChecked));
            sdl.chkprevent.setChecked(GetSetting(context, "prevent_" + PrinterType, true));

            sdl.chkreboot.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(context, "reboot_" + PrinterType, isChecked));
            sdl.chkreboot.setChecked(GetSetting(context, "reboot_" + PrinterType, true));

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
            sdl.txtpsw.setText(GetSetting(context, "psw_" + PrinterType, sshDefault));
            sdl.txtaddress.setText(GetSetting(context, "host_" + PrinterType, ""));

            sdl.btncls.setOnClickListener(v -> saveDialog.dismiss());

            sdl.btnupload.setOnClickListener(v -> {
                String host = Objects.requireNonNull(sdl.txtaddress.getText()).toString();
                String psw = Objects.requireNonNull(sdl.txtpsw.getText()).toString();
                SaveSetting(context, "host_" + PrinterType, host);
                SaveSetting(context, "psw_" + PrinterType, psw);
                boolean reboot = sdl.chkreboot.isChecked();
                sdl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                if (sdl.chkreset.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    SpannableString titleText = new SpannableString("Warning!");
                    titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
                    SpannableString messageText;
                    messageText = new SpannableString("context will restore the default printer database");
                    messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
                    builder.setTitle(titleText);
                    builder.setMessage(messageText);
                    builder.setPositiveButton("Reset", (dialog, which) -> {
                        sdl.txtmsg.setText(R.string.resetting);
                        executorService.execute(() -> {
                            try {
                                restorePrinterDB(context, psw, host, PrinterType);
                                mainHandler.post(() -> {
                                    sdl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
                        alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                    }
                    return;
                }

                final String version;
                if (sdl.chkprevent.isChecked()) {
                    version = "9876543210";
                } else {
                    version = format("%s", GetSetting(context, "version_" + PrinterType, -1L));
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
                            sdl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
        dl.txtcolor.setTextColor(getContrastColor(Color.parseColor("#" + hexCode)));
    }

    private void setupPresetColors(PickerDialogBinding dl) {
        dl.presetColorGrid.removeAllViews();
        for (int color : presetColors()) {
            Button colorButton = new Button(context);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(R.string.enter_hex_color_rrggbb);
        final EditText input = new EditText(context);
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
                    SaveSetting(context, "RGB_VIEW", false);
                } else if (header.getId() == dl.gradientPickerHeader.getId()) {
                    SaveSetting(context, "PICKER_VIEW", false);
                } else if (header.getId() == dl.presetColorsHeader.getId()) {
                    SaveSetting(context, "PRESET_VIEW", false);
                } else if (header.getId() == dl.photoColorHeader.getId()) {
                    SaveSetting(context, "PHOTO_VIEW", false);
                }
            } else {
                content.setVisibility(View.VISIBLE);
                toggleIcon.setImageResource(R.drawable.ic_arrow_up);
                if (header.getId() == dl.rgbSlidersHeader.getId()) {
                    SaveSetting(context, "RGB_VIEW", true);
                } else if (header.getId() == dl.gradientPickerHeader.getId()) {
                    SaveSetting(context, "PICKER_VIEW", true);
                    if (gradientBitmap == null) {
                        setupGradientPicker(dl);
                    }
                } else if (header.getId() == dl.presetColorsHeader.getId()) {
                    SaveSetting(context, "PRESET_VIEW", true);
                } else if (header.getId() == dl.photoColorHeader.getId()) {
                    SaveSetting(context, "PHOTO_VIEW", true);
                }
            }
        });
    }

    private void setupActivityResultLaunchers() {
        try {
            exportDirectoryChooser = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
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
                        } catch (Exception ignored) {}
                    }
            );

            importFileChooser = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
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

                        } catch (Exception ignored) {}
                    }
            );

            requestPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        try {
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

                        } catch (Exception ignored) {}
                    }
            );

            cameraLauncher = registerForActivityResult(
                    new ActivityResultContracts.TakePicturePreview(),
                    bitmap -> {
                        try {
                            if (bitmap != null && pickerDialog != null && pickerDialog.isShowing()) {
                                colorDialog.photoImage.setImageBitmap(bitmap);
                                setupPhotoPicker(colorDialog.photoImage);
                            } else {
                                showToast(R.string.photo_capture_cancelled_or_failed, Toast.LENGTH_SHORT);
                            }
                        } catch (Exception ignored) {}
                    }
            );
        } catch (Exception ignored) {}
    }

    private void checkPermissionAndStartAction(int actionType) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
                File dbFile = filamentDB.getDatabaseFile(context, PrinterType);
                filamentDB.closeInstance();
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);
                if (pickedDir == null || !pickedDir.exists() || !pickedDir.canWrite()) {
                    showToast(R.string.cannot_write_to_selected_directory, Toast.LENGTH_LONG);
                    return;
                }
                String dbBaseName = dbFile.getName().replace(".db", "");
                DocumentFile dbDestFile = pickedDir.createFile("application/octet-stream", dbBaseName + ".db");
                if (dbDestFile != null) {
                    copyFileToUri(context, dbFile, dbDestFile.getUri());
                } else {
                    showToast(R.string.failed_to_create_db_backup_file, Toast.LENGTH_LONG);
                    return;
                }
                showToast(R.string.database_exported_successfully, Toast.LENGTH_LONG);
            } catch (Exception e) {
                showToast(getString(R.string.database_saf_export_failed) + e.getMessage(), Toast.LENGTH_LONG);
            } finally {
                filamentDB.getInstance(context, PrinterType);
            }
        });
    }

    private void performLegacyExport() {
        executorService.execute(() -> {
            try {
                File dbFile = filamentDB.getDatabaseFile(context, PrinterType);
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
                filamentDB.getInstance(context, PrinterType);
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
                File dbFile = filamentDB.getDatabaseFile(context, PrinterType);
                File dbDir = dbFile.getParentFile();
                if (dbDir != null && !dbDir.exists()) {
                    boolean val = dbDir.mkdirs();
                }
                copyUriToFile(context, sourceUri, dbFile);
                filamentDB.getInstance(context, PrinterType);
                setMatDb(PrinterType);
                showToast(R.string.database_imported_successfully, Toast.LENGTH_LONG);
            } catch (Exception e) {
                showToast(getString(R.string.database_saf_import_failed) + e.getMessage(), Toast.LENGTH_LONG);
            } finally {
                if (filamentDB.INSTANCE == null) {
                    filamentDB.getInstance(context, PrinterType);
                    setMatDb(PrinterType);
                }
            }
        });
    }

    private void performLegacyImport() {
        executorService.execute(() -> {
            try {
                filamentDB.closeInstance();

                File dbFile = filamentDB.getDatabaseFile(context, PrinterType);
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
                filamentDB.getInstance(context, PrinterType);
                setMatDb(PrinterType);
                showToast(R.string.database_imported_successfully, Toast.LENGTH_LONG);

            } catch (Exception e) {
                showToast(getString(R.string.database_legacy_import_failed) + e.getMessage(), Toast.LENGTH_LONG);
            } finally {
                if (filamentDB.INSTANCE == null) {
                    filamentDB.getInstance(context, PrinterType);
                    setMatDb(PrinterType);
                }
            }
        });
    }

    private void showImportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        SpannableString titleText = new SpannableString(getString(R.string.import_database));
        titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
        SpannableString messageText = new SpannableString(format(getString(R.string.restore_s_database), PrinterType.toUpperCase()));
        messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
        builder.setTitle(titleText);
        builder.setMessage(messageText);
        builder.setPositiveButton(R.string.simport, (dialog, which) -> checkPermissionAndStartAction(ACTION_IMPORT));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
        }
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        SpannableString titleText = new SpannableString(getString(R.string.export_database));
        titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
        SpannableString messageText = new SpannableString(format(getString(R.string.backup_s_database_material_database_s_db), PrinterType.toUpperCase(), PrinterType.toLowerCase()));
        messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
        builder.setTitle(titleText);
        builder.setMessage(messageText);
        builder.setPositiveButton(R.string.export, (dialog, which) -> checkPermissionAndStartAction(ACTION_EXPORT));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
        if (alert.getWindow() != null) {
            alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
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
            tagDialog = new Dialog(context, R.style.Theme_SpoolID);
            tagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            tagDialog.setCanceledOnTouchOutside(false);
            tagDialog.setTitle(R.string.tag_memory);
            TagDialogBinding tdl = TagDialogBinding.inflate(getLayoutInflater());
            View rv = tdl.getRoot();
            tagDialog.setContentView(rv);
            tdl.btncls.setOnClickListener(v -> tagDialog.dismiss());
            tdl.btnread.setOnClickListener(v -> readTagMemory(tdl));
            tagView = tdl.recyclerView;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager.scrollToPosition(0);
            tagView.setLayoutManager(layoutManager);
            tagItems = new tagItem[0];
            tagAdapter = new tagAdapter(context, tagItems);
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
                                    tagItems[currentBlock].tImage = AppCompatResources.getDrawable(context, R.drawable.locked);
                                } else if (definition.contains("USER DATA")) {
                                    tagItems[currentBlock].tImage = AppCompatResources.getDrawable(context, R.drawable.writable);
                                } else {
                                    tagItems[currentBlock].tImage = AppCompatResources.getDrawable(context, R.drawable.internal);
                                }
                            }
                        } else {
                            tagItems[currentBlock + 1] = new tagItem();
                            tagItems[currentBlock + 1].tKey = "FAILED AUTHENTICATION";
                            tagItems[currentBlock + 1].tValue = "Key Required";
                            tagItems[currentBlock + 1].tImage = AppCompatResources.getDrawable(context, R.drawable.failed);
                        }
                    }
                    tagItem[] filledItems = new tagItem[currentBlock + 1];
                    System.arraycopy(tagItems, 0, filledItems, 0, currentBlock + 1);
                    mainHandler.post(() -> {
                        tagAdapter = new tagAdapter(context, filledItems);
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
                    currentToast = Toast.makeText(context, (Integer) content, duration);
                } else if (content instanceof String) {
                    currentToast = Toast.makeText(context, (String) content, duration);
                } else {
                    currentToast = Toast.makeText(context, String.valueOf(content), duration);
                }
                currentToast.show();
            } catch (Exception ignored) {
            }
        });
    }

    void openManage(boolean isEmpty) {
        try {
            printerDialog = new Dialog(context, R.style.Theme_SpoolID);
            printerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            printerDialog.setCanceledOnTouchOutside(false);
            printerDialog.setTitle(R.string.update);
            ManageDialogBinding mdl = ManageDialogBinding.inflate(getLayoutInflater());
            View rv = mdl.getRoot();
            printerDialog.setContentView(rv);
            PrinterManager manager = new PrinterManager(context);
            List<String> items = manager.getList();

            if (isEmpty) {
                mdl.txtmsg.setTextColor(Color.RED);
                mdl.txtmsg.setText(R.string.add_a_printer_to_get_started);
            }

            executorService.execute(() -> {
                try {
                    String searchNozzle = "0.4";
                    JSONArray matches = findPrinters(context, printerTypes, searchNozzle);
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
                                manageAdapter = new spinnerAdapter(context, options, printerDb);
                                mdl.type.setAdapter(manageAdapter);
                                mdl.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        PrinterOption selected = (PrinterOption) parent.getItemAtPosition(position);
                                        try {
                                            String thumbnail = selected.data.getString("thumbnail");
                                            if (items.contains(selected.data.getString("name"))) {
                                                mdl.btnrem.setVisibility(View.VISIBLE);
                                                mdl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.primary_error));
                                                mdl.txtmsg.setText(R.string.this_printer_is_already_added);
                                            } else {
                                                mdl.txtmsg.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                SpannableString titleText = new SpannableString(getString(R.string.remove_printer));
                titleText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.primary_brand)), 0, titleText.length(), 0);
                SpannableString messageText = new SpannableString(format(getString(R.string.do_you_want_to_remove_s), PrinterType.toUpperCase()));
                messageText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_main)), 0, messageText.length(), 0);
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
                        filamentDB.getInstance(context, dbName).close();
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
                    alert.getWindow().setBackgroundDrawableResource(R.color.background_alt);
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.primary_brand));
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
                        String json = getJsonDB(context, printerName, "0.4");
                        if (json != null && json.contains("\"kvParam\"")) {
                            JSONObject materials = new JSONObject(json);
                            JSONObject result = new JSONObject(materials.getString("result"));
                            long newVer = result.getLong("version");
                            filamentDB tdb = filamentDB.getInstance(context, printerName.toLowerCase());
                            populateDatabase(context, tdb.matDB(), json, printerName.toLowerCase());
                            SaveSetting(context, "version_" + printerName.toLowerCase(), newVer);
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

    void openSpoolAdd() {
        spoolDialog = new Dialog(context, R.style.Theme_SpoolID);
        spoolDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        spoolDialog.setCanceledOnTouchOutside(false);
        spoolDialog.setTitle(R.string.add_spool_to_spoolman);
        SpoolDialogBinding sdl = SpoolDialogBinding.inflate(getLayoutInflater());
        View rv = sdl.getRoot();
        spoolDialog.setContentView(rv);
        sdl.btncls.setOnClickListener(v -> {
            hideKeyboard(v);
            spoolDialog.dismiss();
        });

        sdl.containerVendor.setVisibility(View.VISIBLE);
        sdl.containerFilament.setVisibility(View.GONE);
        sdl.containerSpool.setVisibility(View.GONE);

        sdl.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hideKeyboard(sdl.tabLayout);
                sdl.containerVendor.setVisibility(View.GONE);
                sdl.containerFilament.setVisibility(View.GONE);
                sdl.containerSpool.setVisibility(View.GONE);
                switch (tab.getPosition()) {
                    case 0:
                        sdl.containerVendor.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        sdl.containerFilament.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        sdl.containerSpool.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        View.OnClickListener dateListener = v -> {
            TextInputEditText target = (TextInputEditText) v;
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Date")
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                target.setText(sdf.format(new Date(selection)));
            });
            picker.show(getSupportFragmentManager(), "DATE_PICKER");
        };

        sdl.sFirstUsed.setOnClickListener(dateListener);
        sdl.sLastUsed.setOnClickListener(dateListener);

        sdl.containerMain.setOnClickListener(v -> hideKeyboard(sdl.containerMain));
        sdl.containerButton.setOnClickListener(v -> hideKeyboard(sdl.containerButton));

        sdl.vComment.setText(String.format("Created by %s", context.getString(R.string.app_name)));
        sdl.fComment.setText(String.format("Created by %s", context.getString(R.string.app_name)));
        sdl.sComment.setText(String.format("RFID tagged for %s", SelectedPrinter));

        Filament localData = matDb.getFilamentById(MaterialID);

        if (localData.filamentParam != null && !localData.filamentParam.isEmpty()) {
            try {
                JSONObject root = new JSONObject(localData.filamentParam);
                JSONObject kvParam = root.optJSONObject("kvParam");
                JSONObject base = root.optJSONObject("base");
                if (base != null) {
                    sdl.fMaterial.setText(base.optString("meterialType"));
                    sdl.fDiameter.setText(String.format("%s", base.optDouble("diameter")));
                }
                if (kvParam != null) {
                    if (kvParam.has("nozzle_temperature"))
                        sdl.fTempExtruder.setText(String.format(Locale.getDefault(), "%d", Integer.parseInt(kvParam.getString("nozzle_temperature"))));
                    if (kvParam.has("hot_plate_temp"))
                        sdl.fTempBed.setText(String.format(Locale.getDefault(), "%d", Integer.parseInt(kvParam.getString("hot_plate_temp"))));
                    if (kvParam.has("filament_density"))
                        sdl.fDensity.setText(String.format("%s", kvParam.optDouble("filament_density")));
                }
            } catch (Exception ignored) {
            }
        }

        sdl.vName.setText(MaterialVendor);
        sdl.fMaterial.setText(MaterialName);
        sdl.sRemainingWeight.setText(String.format(Locale.getDefault(), "%d", Utils.GetMaterialIntWeight(MaterialWeight)));
        sdl.sInitialWeight.setText(String.format(Locale.getDefault(), "%d", Utils.GetMaterialIntWeight(MaterialWeight)));
        sdl.fColorHex.setText(MaterialColor);
        sdl.fExternalId.setText(MaterialID);
        String colorName = matcher.findNearestColor(MaterialColor);
        if (colorName == null || colorName.isEmpty())
        {
            colorName = MaterialColor;
        }
        sdl.fName.setText(String.format("%s (%s)", MaterialName, colorName));
        String[] directions = new String[] {"coaxial", "longitudinal"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, directions);
        sdl.fMultiColorDirection.setAdapter(adapter);

        sdl.btnadd.setOnClickListener(v -> {
            hideKeyboard(v);
            String smHost = GetSetting(context, "smhost", "");
            int smPort = GetSetting(context, "smport", 7912);

            if (!smHost.isEmpty()) {
                String baseUrl = "http://" + smHost + ":" + smPort + "/api/v1";

                executorService.execute(() -> {
                    try {
                        String vendorName = Objects.requireNonNull(sdl.vName.getText()).toString().trim();
                        int vendorId = -1;
                        String vRes = performSmRequest(context, baseUrl + "/vendor", "GET", null);

                        if (vRes != null) {
                            JSONArray vArray = new JSONArray(vRes);
                            for (int i = 0; i < vArray.length(); i++) {
                                JSONObject vo = vArray.getJSONObject(i);
                                if (vo.getString("name").equalsIgnoreCase(vendorName)) {
                                    vendorId = vo.getInt("id");
                                    break;
                                }
                            }
                        }

                        if (vendorId == -1) {
                            JSONObject vBody = new JSONObject();
                            vBody.put("name", vendorName);
                            vBody.put("comment", Objects.requireNonNull(sdl.vComment.getText()).toString());
                            vBody.put("empty_spool_weight", getDoubleOrNull(sdl.vEmptySpoolWeight));
                            vBody.put("external_id", Objects.requireNonNull(sdl.vExternalId.getText()).toString());
                            String newV = performSmRequest(context, baseUrl + "/vendor", "POST", vBody.toString());
                            if (newV != null) vendorId = new JSONObject(newV).getInt("id");
                        }

                        String filamentName = Objects.requireNonNull(sdl.fName.getText()).toString().trim();
                        int filamentId = -1;
                        String fRes = performSmRequest(context, baseUrl + "/filament", "GET", null);
                        if (fRes != null) {
                            JSONArray fArray = new JSONArray(fRes);
                            for (int i = 0; i < fArray.length(); i++) {
                                JSONObject f = fArray.getJSONObject(i);
                                int vIdCheck = f.has("vendor") && !f.isNull("vendor") ? f.getJSONObject("vendor").getInt("id") : -1;
                                if (vIdCheck == vendorId && f.getString("name").equalsIgnoreCase(filamentName)) {
                                    filamentId = f.getInt("id");
                                    break;
                                }
                            }
                        }

                        if (filamentId == -1) {
                            JSONObject fBody = new JSONObject();
                            fBody.put("name", filamentName);
                            fBody.put("vendor_id", vendorId);
                            fBody.put("material", Objects.requireNonNull(sdl.fMaterial.getText()).toString());
                            fBody.put("price", getDoubleOrNull(sdl.fPrice));
                            fBody.put("density", getDoubleOrNull(sdl.fDensity));
                            fBody.put("diameter", getDoubleOrNull(sdl.fDiameter));
                            fBody.put("weight", getDoubleOrNull(sdl.fWeight));
                            fBody.put("spool_weight", getDoubleOrNull(sdl.fSpoolWeight));
                            fBody.put("article_number", Objects.requireNonNull(sdl.fArticleNumber.getText()).toString());
                            fBody.put("comment", Objects.requireNonNull(sdl.fComment.getText()).toString());
                            fBody.put("settings_extruder_temp", getIntOrNull(sdl.fTempExtruder));
                            fBody.put("settings_bed_temp", getIntOrNull(sdl.fTempBed));
                            if (!Objects.requireNonNull(sdl.fMultiColorHexes.getText()).toString().isEmpty()) {
                                fBody.put("multi_color_hexes", sdl.fMultiColorHexes.getText().toString());
                                fBody.put("multi_color_direction", sdl.fMultiColorDirection.getText().toString());
                            }else {
                                fBody.put("color_hex", Objects.requireNonNull(sdl.fColorHex.getText()).toString().replace("#", ""));
                            }
                            fBody.put("external_id", Objects.requireNonNull(sdl.fExternalId.getText()).toString());
                            String newF = performSmRequest(context, baseUrl + "/filament", "POST", fBody.toString());
                            if (newF != null) filamentId = new JSONObject(newF).getInt("id");
                        }

                        if (filamentId != -1) {
                            JSONObject sBody = new JSONObject();
                            sBody.put("filament_id", filamentId);
                            sBody.put("price", getDoubleOrNull(sdl.sPrice));
                            if (!Objects.requireNonNull(sdl.sFirstUsed.getText()).toString().isEmpty())
                            {
                                sBody.put("first_used", Objects.requireNonNull(sdl.sFirstUsed.getText()).toString());
                            }
                            if (!Objects.requireNonNull(sdl.sLastUsed.getText()).toString().isEmpty())
                            {
                                sBody.put("last_used", Objects.requireNonNull(sdl.sLastUsed.getText()).toString());
                            }
                            sBody.put("initial_weight", getDoubleOrNull(sdl.sInitialWeight));
                            if (getDoubleOrNull(sdl.sRemainingWeight) != JSONObject.NULL)
                            {
                                sBody.put("remaining_weight", getDoubleOrNull(sdl.sRemainingWeight));

                            }else if (getDoubleOrNull(sdl.sUsedWeight) != JSONObject.NULL)
                            {
                                sBody.put("used_weight", getDoubleOrNull(sdl.sUsedWeight));
                            }
                            sBody.put("location", Objects.requireNonNull(sdl.sLocation.getText()).toString());
                            sBody.put("lot_nr", Objects.requireNonNull(sdl.sLotNr.getText()).toString());
                            sBody.put("comment", Objects.requireNonNull(sdl.sComment.getText()).toString());
                            sBody.put("archived", sdl.sArchived.isChecked());
                            String ret = performSmRequest(context, baseUrl + "/spool", "POST", sBody.toString());

                            if (ret != null) {
                                showToast("Spool created successfully!", Toast.LENGTH_SHORT);
                                mainHandler.post(() -> spoolDialog.dismiss());
                            } else {
                                showToast("Failed to create spool", Toast.LENGTH_SHORT);
                            }
                        }

                    } catch (Exception e) {
                        showToast("Error creating spool", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                showToast(getString(R.string.spoolman_host_is_not_set), Toast.LENGTH_SHORT);
            }
        });
        spoolDialog.show();
    }

    public void openSettings() {
        settingsDialog = new Dialog(context, R.style.Theme_SpoolID);
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setCanceledOnTouchOutside(false);
        settingsDialog.setTitle(R.string.settings);
        SettingsDialogBinding sdl = SettingsDialogBinding.inflate(getLayoutInflater());
        View rv = sdl.getRoot();
        settingsDialog.setContentView(rv);
        sdl.readswitch.setChecked(GetSetting(context, "autoread", false));
        sdl.readswitch.setOnCheckedChangeListener((buttonView, isChecked) -> SaveSetting(context, "autoread", isChecked));
        sdl.launchswitch.setChecked(GetSetting(context, "autoLaunch", true));
        sdl.launchswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setNfcLaunchMode(context, isChecked);
            SaveSetting(context, "autoLaunch", isChecked);
        });
        sdl.themeswitch.setChecked(GetSetting(context, "enabledm", false));
        sdl.themeswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SaveSetting(context, "enabledm", isChecked);
            setThemeMode(isChecked);
        });
        sdl.spoolswitch.setChecked(GetSetting(context, "enablesm", false));
        sdl.smhost.setText(GetSetting(context, "smhost", ""));
        sdl.smport.setText(String.valueOf(GetSetting(context, "smport", 7912)));
        sdl.smhost.setEnabled(sdl.spoolswitch.isChecked());
        sdl.smport.setEnabled(sdl.spoolswitch.isChecked());
        sdl.spoolswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sdl.smhost.setEnabled(isChecked);
            sdl.smport.setEnabled(isChecked);
            if (isChecked) {
                sdl.smhost.setTextColor(ContextCompat.getColor(context, R.color.text_main));
                sdl.smport.setTextColor(ContextCompat.getColor(context, R.color.text_main));
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
            SaveSetting(context, "enablesm", isChecked);
        });
        if (sdl.spoolswitch.isChecked()) {
            sdl.smhost.setTextColor(ContextCompat.getColor(context, R.color.text_main));
            sdl.smport.setTextColor(ContextCompat.getColor(context, R.color.text_main));
        }
        else {
            sdl.smhost.setTextColor(Color.GRAY);
            sdl.smport.setTextColor(Color.GRAY);
        }
        sdl.btncls.setOnClickListener(v -> settingsDialog.dismiss());
        settingsDialog.setOnDismissListener(dialogInterface -> {
            SaveSetting(context, "smhost", Objects.requireNonNull(sdl.smhost.getText()).toString());
            SaveSetting(context, "smport", Integer.parseInt(Objects.requireNonNull(sdl.smport.getText()).toString()));
        });
        settingsDialog.show();
    }

}