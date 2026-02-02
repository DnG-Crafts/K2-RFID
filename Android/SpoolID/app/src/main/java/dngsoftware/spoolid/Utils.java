package dngsoftware.spoolid;

import static java.lang.String.format;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.nfc.tech.MifareClassic;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


@SuppressLint("GetInstance")
public class Utils {

    public static String[] materialWeights = {
            "1 KG",
            "750 G",
            "600 G",
            "500 G",
            "250 G"
    };

    public static String[] printerTypes = {
            "K2",
            "K1",
            "HI",
    };

    public static String GetMaterialLength(String materialWeight) {
        switch (materialWeight) {
            case "1 KG":
                return "0330";
            case "750 G":
                return "0247";
            case "600 G":
                return "0198";
            case "500 G":
                return "0165";
            case "250 G":
                return "0082";
        }
        return "0330";
    }

    public static String GetMaterialWeight(String materialLength) {
        switch (materialLength) {
            case "0330":
                return "1 KG";
            case "0247":
                return "750 G";
            case "0198":
                return "600 G";
            case "0165":
                return "500 G";
            case "0082":
                return "250 G";
        }
        return "1 KG";
    }

    public static int GetMaterialIntWeight(String materialWeight) {
        switch (materialWeight) {
            case "1 KG":
                return 1000;
            case "750 G":
                return 750;
            case "600 G":
                return 600;
            case "500 G":
                return 500;
            case "250 G":
                return 250;
        }
        return 1000;
    }

    public static String[] filamentVendors = {
            "3Dgenius",
            "3DJake",
            "3DXTECH",
            "3D BEST-Q",
            "3D Hero",
            "3D-Fuel",
            "Aceaddity",
            "AddNorth",
            "Amazon Basics",
            "AMOLEN",
            "Ankermake",
            "Anycubic",
            "Atomic",
            "AzureFilm",
            "BASF",
            "Bblife",
            "BCN3D",
            "Beyond Plastic",
            "California Filament",
            "Capricorn",
            "CC3D",
            "Colour Dream",
            "colorFabb",
            "Comgrow",
            "Cookiecad",
            "Creality",
            "CERPRiSE",
            "Das Filament",
            "DO3D",
            "DOW",
            "DSM",
            "Duramic",
            "ELEGOO",
            "Eryone",
            "Essentium",
            "eSUN",
            "Extrudr",
            "Fiberforce",
            "Fiberlogy",
            "FilaCube",
            "Filamentive",
            "Fillamentum",
            "FLASHFORGE",
            "Formfutura",
            "Francofil",
            "FilamentOne",
            "Fil X",
            "GEEETECH",
            "Generic",
            "Giantarm",
            "Gizmo Dorks",
            "GreenGate3D",
            "HATCHBOX",
            "Hello3D",
            "IC3D",
            "IEMAI",
            "IIID Max",
            "INLAND",
            "iProspect",
            "iSANMATE",
            "Justmaker",
            "Keene Village Plastics",
            "Kexcelled",
            "LDO",
            "MakerBot",
            "MatterHackers",
            "MIKA3D",
            "NinjaTek",
            "Nobufil",
            "Novamaker",
            "OVERTURE",
            "OVVNYXE",
            "Polymaker",
            "Priline",
            "Printed Solid",
            "Protopasta",
            "Prusament",
            "Push Plastic",
            "R3D",
            "Re-pet3D",
            "Recreus",
            "Regen",
            "Sain SMART",
            "SliceWorx",
            "Snapmaker",
            "SnoLabs",
            "Spectrum",
            "SUNLU",
            "TTYT3D",
            "Tianse",
            "UltiMaker",
            "Valment",
            "Verbatim",
            "VO3D",
            "Voxelab",
            "VOXELPLA",
            "YOOPAI",
            "Yousu",
            "Ziro",
            "Zyltech"};

    public static String[] filamentTypes = {
            "ABS",
            "ASA",
            "HIPS",
            "PA",
            "PA-CF",
            "PC",
            "PLA",
            "PLA-CF",
            "PVA",
            "PP",
            "TPU",
            "PETG",
            "BVOH",
            "PET-CF",
            "PETG-CF",
            "PA6-CF",
            "PAHT-CF",
            "PPS",
            "PPS-CF",
            "PET",
            "ASA-CF",
            "PA-GF",
            "PETG-GF",
            "PP-CF",
            "PCTG"
    };

    public static String GetMaterialInfo(MatDB db, String materialId) {
        Filament item = db.getFilamentById(materialId);
        return item.filamentParam;
    }

    public static void setMaterialInfo(MatDB db, String materialId, String materialParam) {
        Filament item = db.getFilamentById(materialId);
        item.filamentParam = materialParam;
        db.updateItem(item);
    }

    public static String GetMaterialBrand(MatDB db, String materialId) {
        try {
            Filament item = db.getFilamentById(materialId);
            if (item == null) {
                return " ";
            }
            return item.filamentVendor;
        } catch (Exception e) {
            return " ";
        }
    }

    public static String[] GetMaterialName(MatDB db, String materialId) {
        try {
            String[] arrRet = new String[2];
            Filament item = db.getFilamentById(materialId);
            if (item == null) {
                return null;
            } else {
                arrRet[0] = item.filamentName;
                arrRet[1] = item.filamentVendor;
                return arrRet;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static List<MaterialItem> getMaterials(MatDB db, String brandName) {
        List<Filament> items = db.getFilamentsByVendor(brandName);
        List<MaterialItem> materialItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            materialItems.add(new MaterialItem(items.get(i).filamentName, items.get(i).filamentID));
        }
        return materialItems;
    }

    public static int getMaterialPos(ArrayAdapter<MaterialItem> adapter, String materialID) {
        for (int i = 0; i < adapter.getCount(); i++) {
            MaterialItem item = adapter.getItem(i);
            if (item != null && item.getMaterialID().equals(materialID)) {
                return i;
            }
        }
        return 0;
    }

    public static String[] getMaterialBrands(MatDB db) {
        List<Filament> items = db.getAllItems();
        Set<String> uniqueBrandsSet = new HashSet<>();
        for (Filament item : items) {
            uniqueBrandsSet.add(item.filamentVendor);
        }
        return uniqueBrandsSet.toArray(new String[0]);
    }

    public static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public static void SetPermissions(Context context) {
        String[] REQUIRED_PERMISSIONS = {Manifest.permission.NFC, Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Activity activity = (Activity) context;
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            String[] permsArray = permissionsToRequest.toArray(new String[0]);
            ActivityCompat.requestPermissions(activity, permsArray, 200);
        }
    }

    public static void playBeep() {
        new Thread(() -> {
            try {
                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50);
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 300);
                toneGenerator.stopTone();
                toneGenerator.release();
            } catch (Exception ignored) {
            }
        }).start();
    }

    public static byte[] createKey(byte[] tagId) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(new byte[]
                    {113, 51, 98, 117, 94, 116, 49, 110, 113, 102, 90, 40, 112, 102, 36, 49}, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            int x = 0;
            byte[] encB = new byte[16];
            for (int i = 0; i < 16; i++) {
                if (x >= 4) x = 0;
                encB[i] = tagId[x];
                x++;
            }
            return Arrays.copyOfRange(cipher.doFinal(encB), 0, 6);
        } catch (Exception ignored) {
            return MifareClassic.KEY_DEFAULT;
        }
    }

    public static byte[] cipherData(int mode, byte[] tagData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(new byte[]
                    {72, 64, 67, 70, 107, 82, 110, 122, 64, 75, 65, 116, 66, 74, 112, 50}, "AES");
            cipher.init(mode, secretKeySpec);
            return cipher.doFinal(tagData);
        } catch (Exception ignored) {}
        return tagData;
    }

    public static void restorePrinterDB(Context context, String psw, String host, String pType) throws Exception {
        JSONObject jsonDb = new JSONObject(getJsonDB(context,pType,"0.4"));
        setJsonDB(jsonDb.toString(2), psw, host, pType, "material_database.json");
        sendSShCommand(psw, host, "reboot");
    }

    public static void populateDatabase(Context context, MatDB db, String json, String pType) {
        try {
            JSONObject materials;
            if (json != null && !json.isEmpty()) {
                materials = new JSONObject(json);
            }else {
                return;
            }
            JSONObject result = new JSONObject(materials.getString("result"));
            SaveSetting(context, "version_" + pType, result.getLong("version"));
            JSONArray list = result.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject base = item.getJSONObject("base");
                String currentID = base.getString("id").trim();
                Filament existingFilament = db.getFilamentById(currentID);
                Filament filament = new Filament();
                filament.filamentID = currentID;
                filament.position = i;
                filament.filamentName = base.getString("name").trim();
                filament.filamentVendor = base.getString("brand").trim();
                filament.filamentParam = item.toString();
                if (existingFilament != null) {
                    filament.dbKey = existingFilament.dbKey;
                    db.updateItem(filament);
                } else {
                    db.addItem(filament);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void addFilament(MatDB db, JSONObject item ) {
        try {
            JSONObject base = item.getJSONObject("base");
            Filament filament = new Filament();
            filament.position = db.getItemCount();
            filament.filamentID = base.getString("id").trim();
            filament.filamentName = base.getString("name").trim();
            filament.filamentVendor = base.getString("brand").trim();
            filament.filamentParam = item.toString();
            db.addItem(filament);
        } catch (Exception ignored) {
        }
    }

    public static void removeFilament(MatDB db, String materialId) {
        try {
            Filament item = db.getFilamentById(materialId);
            db.deleteItem(item);
        } catch (Exception ignored) {
        }
    }

    public static void saveDBToPrinter(MatDB db, String psw, String host, String pType, String version, boolean reboot) throws Exception {
        JSONObject materials = new JSONObject(getJsonDB(psw, host, pType, "material_database.json"));
        JSONObject result = new JSONObject(materials.getString("result"));
        JSONArray list = new JSONArray();
        String ver = version;
        if (ver == null || ver.isEmpty() || ver.equals("0")) {
            ver = result.getString("version");
        }
        List<Filament> items = db.getAllItems();
        for (Filament item : items) {
            JSONObject jo = new JSONObject(item.filamentParam);
            list.put(jo);
        }
        materials.remove("result");
        result.remove("list");
        result.remove("count");
        result.remove("version");
        result.put("list", list);
        result.put("count", list.length());
        result.put("version", ver);
        materials.put("result", result);
        setJsonDB(materials.toString(2), psw, host, pType, "material_database.json");

        if (pType.toLowerCase().contains("k1")) {
            saveMatOption(psw, host, pType, materials, reboot);
        } else {
            if (reboot) {
                sendSShCommand(psw, host, "reboot");
            }
        }
    }

    public static void saveMatOption(String psw, String host, String pType, JSONObject materials, boolean reboot) throws Exception {
        JSONObject options = new JSONObject();
        JSONObject json = new JSONObject(materials.toString());
        JSONObject result = new JSONObject(json.getString("result"));
        JSONArray list = result.getJSONArray("list");
        Set<String> uniqueBrandsSet = new HashSet<>();
        for (int i = 0; i < list.length(); i++) {
            JSONObject items = new JSONObject(list.get(i).toString());
            JSONObject base = new JSONObject(items.getString("base"));
            uniqueBrandsSet.add(base.getString("brand"));
        }
        for (String brand : uniqueBrandsSet) {
            options.put(brand, new JSONObject());
            JSONObject vendor = new JSONObject(options.getString(brand));
            for (int i = 0; i < list.length(); i++) {
                JSONObject items = new JSONObject(list.get(i).toString());
                JSONObject base = new JSONObject(items.getString("base"));
                if (base.getString("brand").equals(brand)) {
                    String tmpType = base.getString("meterialType");
                    if (vendor.has(tmpType)) {
                        vendor.put(tmpType, vendor.getString(tmpType) + "\n" + base.getString("name"));
                    } else {
                        vendor.put(tmpType, base.getString("name"));
                    }
                }
            }
            options.put(brand, vendor);
        }
        setJsonDB(options.toString(2), psw, host, pType, "material_option.json");

        if (reboot) {
            sendSShCommand(psw, host, "reboot");
        }
    }

    public static String sendSShCommand(String psw, String host, String command) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession("root", host, 22);
        session.setPassword(psw);
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        session.connect();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channel.setOutputStream(baos);
        channel.setCommand(command);
        channel.connect(5000);
        while (true) {
            if (channel.isClosed()) {
                channel.disconnect();
                session.disconnect();
                return baos.toString();
            }
        }
    }

    public static String getJsonDB(String psw, String host, String pType, String fileName) {
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession("root", host, 22);
            session.setPassword(psw);
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            if (pType.toLowerCase().contains("k1")) {
                channel.setCommand("scp -f /usr/data/creality/userdata/box/" + fileName);
            } else {
                channel.setCommand("scp -f /mnt/UDISK/creality/userdata/box/" + fileName);
            }
            channel.connect(5000);
            StringBuilder jsonDb = new StringBuilder();
            byte[] buf = new byte[1024];
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            while (true) {
                try {
                    long filesize;
                    for (int i = 0; ; i++) {
                        int ret = in.read(buf, i, 1);
                        if (buf[i] == (byte) 0x0a) {
                            String hdr = new String(buf, 0, i);
                            filesize = Long.parseLong(hdr.split(" ")[1]);
                            break;
                        }
                    }
                    buf[0] = 0;
                    out.write(buf, 0, 1);
                    out.flush();
                    int i;
                    while (true) {
                        if (buf.length < filesize)
                            i = buf.length;
                        else
                            i = (int) filesize;
                        i = in.read(buf, 0, i);
                        if (i < 0) {
                            break;
                        }
                        jsonDb.append(new String(buf, 0, i));
                        filesize -= i;
                        if (filesize == 0L)
                            break;
                    }
                    buf[0] = 0;
                    out.write(buf, 0, 1);
                    out.flush();
                }catch (Exception ignored){
                    break;
                }
            }
            channel.disconnect();
            session.disconnect();
            return jsonDb.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void setJsonDB(String dbData, String psw, String host, String pType, String fileName) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession("root", host, 22);
        session.setPassword(psw);
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);
        session.connect();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();
        if (pType.toLowerCase().contains("k1")) {
            channel.setCommand("scp -p -t /usr/data/creality/userdata/box/" + fileName);
        } else {
            channel.setCommand("scp -p -t /mnt/UDISK/creality/userdata/box/" + fileName);
        }
        channel.connect(5000);
        out.write(("C0644 " + dbData.length() + " " + fileName + "\n").getBytes());
        out.flush();
        out.write(dbData.getBytes());
        out.flush();
        channel.disconnect();
        session.disconnect();
    }

    public static void restartApp(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
            assert intent != null;
            ComponentName componentName = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
            mainIntent.setPackage(context.getPackageName());
            context.startActivity(mainIntent);
            Runtime.getRuntime().exit(0);
        }
        catch (Exception ignored) {
            Runtime.getRuntime().exit(0);
        }
    }

    public static int getPositionByValue(Spinner spinner, String value) {
        ArrayAdapter<?> adapter = (ArrayAdapter<?>) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (Objects.requireNonNull(adapter.getItem(i)).toString().equalsIgnoreCase(value)) {
                    return i;
                }
            }
        }
        return 0;
    }

    public static String fetchDataFromApi(Context context, String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("User-Agent", context.getString(R.string.api_useragent));
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("__CXY_BRAND_", "creality");
        conn.setRequestProperty("__CXY_UID_", "");
        conn.setRequestProperty("__CXY_OS_LANG_", "0");
        conn.setRequestProperty("__CXY_DUID_", UUID.randomUUID().toString());
        conn.setRequestProperty("__CXY_APP_VER_", "1.0");
        conn.setRequestProperty("__CXY_APP_CH_", "CP_Beta");
        conn.setRequestProperty("__CXY_OS_VER_", context.getString(R.string.api_useragent));
        conn.setRequestProperty("__CXY_TIMEZONE_", "28800");
        conn.setRequestProperty("__CXY_APP_ID_", "creality_model");
        conn.setRequestProperty("__CXY_REQUESTID_", UUID.randomUUID().toString());
        conn.setRequestProperty("__CXY_PLATFORM_", "11");
        JSONObject body = new JSONObject();
        body.put("engineVersion", "3.0.0");
        if (apiUrl.contains("materialList")) body.put("pageSize", 500);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes(context.getString(R.string.utf_8));
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), context.getString(R.string.utf_8)))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    private static String readEntryContent(Context context, ZipInputStream zis) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = zis.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString(context. getString(R.string.utf_8));
    }

    private static String getZipUrl(Context context, String targetPrinterName, String targetNozzle) {
        try {
            String printerListJson = fetchDataFromApi(context, context.getString(R.string.api_printerlist));
            JSONObject root = new JSONObject(printerListJson);
            JSONArray printerList = root.getJSONObject("result").getJSONArray("printerList");
            for (int i = 0; i < printerList.length(); i++) {
                JSONObject printer = printerList.getJSONObject(i);
                if (printer.getString("name").equalsIgnoreCase(targetPrinterName)) {
                    JSONArray diameters = printer.getJSONArray("nozzleDiameter");
                    for (int j = 0; j < diameters.length(); j++) {
                        if (diameters.getString(j).equals(targetNozzle)) {
                            return printer.getString("zipUrl");
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static JSONArray findPrinters(Context context, String targetName, String targetNozzle) {
        JSONArray results = new JSONArray();
        try {
            String printerListJson = fetchDataFromApi(context, context.getString(R.string.api_printerlist));
            JSONObject root = new JSONObject(printerListJson);
            JSONArray printerList = root.getJSONObject("result").getJSONArray("printerList");
            for (int i = 0; i < printerList.length(); i++) {
                JSONObject printer = printerList.getJSONObject(i);
                String printerName = printer.getString("name");
                if (printerName.toLowerCase().contains(targetName.toLowerCase())) {
                    JSONArray nozzles = printer.getJSONArray("nozzleDiameter");
                    boolean hasNozzle = false;
                    for (int j = 0; j < nozzles.length(); j++) {
                        if (nozzles.getString(j).equals(targetNozzle)) {
                            hasNozzle = true;
                            break;
                        }
                    }
                    if (hasNozzle) {
                        results.put(printer);
                    }
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    public static JSONArray findPrinters(Context context, String[] targetNames, String targetNozzle) {
        JSONArray results = new JSONArray();
        try {
            String printerListJson = fetchDataFromApi(context, context.getString(R.string.api_printerlist));
            JSONObject root = new JSONObject(printerListJson);
            JSONArray printerList = root.getJSONObject("result").getJSONArray("printerList");
            for (int i = 0; i < printerList.length(); i++) {
                JSONObject printer = printerList.getJSONObject(i);
                String printerName = printer.getString("name").toLowerCase();
                boolean nameMatches = false;
                for (String target : targetNames) {
                    if (printerName.contains(target.toLowerCase())) {
                        nameMatches = true;
                        break;
                    }
                }
                if (nameMatches) {
                    JSONArray nozzles = printer.getJSONArray("nozzleDiameter");
                    boolean hasNozzle = false;
                    for (int j = 0; j < nozzles.length(); j++) {
                        if (nozzles.getString(j).equals(targetNozzle)) {
                            hasNozzle = true;
                            break;
                        }
                    }
                    if (hasNozzle) {
                        results.put(printer);
                    }
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    private static String processMaterials(String materialListJson, List<String> filamentJsonList, String zipVersion) {
        try {
            JSONObject targetRoot = new JSONObject();
            targetRoot.put("code", 0);
            targetRoot.put("msg", "ok");
            targetRoot.put("reqId", "0");
            JSONObject resultObj = new JSONObject();
            JSONArray finalListItemArray = new JSONArray();
            JSONObject listRoot = new JSONObject(materialListJson);
            JSONArray allBaseMaterials = listRoot.getJSONObject("result").getJSONArray("list");
            for (String filamentJson : filamentJsonList) {
                JSONObject sourceObj = new JSONObject(filamentJson);
                JSONObject metadata = sourceObj.getJSONObject("metadata");
                String targetName = metadata.getString("name");
                String engineVersion = sourceObj.getString("engine_version");
                JSONObject engineData = sourceObj.optJSONObject("engine_data");
                if (engineData == null) continue;
                JSONObject cleanBase = null;
                for (int i = 0; i < allBaseMaterials.length(); i++) {
                    JSONObject rawBase = allBaseMaterials.getJSONObject(i);
                    if (rawBase.getString("name").equals(targetName)) {
                        cleanBase = new JSONObject();
                        Iterator<String> baseKeys = rawBase.keys();
                        while (baseKeys.hasNext()) {
                            String key = baseKeys.next();
                            if (key.equals("createTime") || key.equals("status") || key.equals("userInfo")) continue;
                            cleanBase.put(key, rawBase.get(key));
                        }
                        break;
                    }
                }
                if (cleanBase != null) {
                    JSONObject listItem = new JSONObject();
                    listItem.put("engineVersion", engineVersion);
                    listItem.put("printerIntName", "F008");
                    JSONArray nozzleDiameter = new JSONArray();
                    nozzleDiameter.put("0.4");
                    listItem.put("nozzleDiameter", nozzleDiameter);
                    JSONObject kvParam = new JSONObject();
                    Iterator<String> engKeys = engineData.keys();
                    while (engKeys.hasNext()) {
                        String key = engKeys.next();
                        kvParam.put(key, engineData.get(key));
                    }
                    listItem.put("kvParam", kvParam);
                    listItem.put("base", cleanBase);
                    finalListItemArray.put(listItem);
                }
            }
            resultObj.put("list", finalListItemArray);
            resultObj.put("count", finalListItemArray.length());
            if (zipVersion != null && !zipVersion.isEmpty()) {
                resultObj.put("version", zipVersion);
            } else {
                resultObj.put("version", String.valueOf(System.currentTimeMillis() / 1000));
            }
            targetRoot.put("result", resultObj);
            return targetRoot.toString(2);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String getJsonDB(Context context, String targetPrinterName, String targetNozzle) {
        try {
            String zipUrl = getZipUrl(context, targetPrinterName, targetNozzle);
            if (zipUrl != null) {
                String materialListStr = fetchDataFromApi(context, context.getString(R.string.api_materiallist));
                URL url = new URL(zipUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                ZipInputStream zis = new ZipInputStream(connection.getInputStream());
                ZipEntry entry;
                List<String> filamentDataList = new ArrayList<>();
                String extractedVersion = null;
                while ((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (!entry.isDirectory() && !name.contains("/") && name.toLowerCase().endsWith(".json")) {
                        String content = readEntryContent(context, zis);
                        JSONObject rootDef = new JSONObject(content);
                        extractedVersion = rootDef.optString("version", null);
                    } else if (!entry.isDirectory() && name.toLowerCase().startsWith("materials/") && name.toLowerCase().endsWith(".json")) {
                        filamentDataList.add(readEntryContent(context, zis));
                    }
                    zis.closeEntry();
                }
                zis.close();
                if (!materialListStr.isEmpty() && !filamentDataList.isEmpty()) {
                    return processMaterials(materialListStr, filamentDataList, extractedVersion);
                } else {
                    return null;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static void loadImage(String urlString, ImageView imageView) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                imageView.post(() -> imageView.setImageBitmap(myBitmap));
            } catch (Exception ignored) {}
        }).start();
    }

    public static String GetSetting(Context context, String sKey, String sDefault) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPref.getString(sKey, sDefault);
    }

    public static boolean GetSetting(Context context, String sKey, boolean bDefault) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPref.getBoolean(sKey, bDefault);
    }

    public static int GetSetting(Context context, String sKey, int iDefault) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPref.getInt(sKey, iDefault);
    }

    public static long GetSetting(Context context, String sKey, long lDefault) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        return sharedPref.getLong(sKey, lDefault);
    }

    public static void SaveSetting(Context context, String sKey, String sValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(sKey, sValue);
        editor.apply();
    }

    public static void SaveSetting(Context context, String sKey, boolean bValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(sKey, bValue);
        editor.apply();
    }

    public static void SaveSetting(Context context, String sKey, int iValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(sKey, iValue);
        editor.apply();
    }

    public static void SaveSetting(Context context, String sKey, long lValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(sKey, lValue);
        editor.apply();
    }

    public static void setThemeMode(boolean enabled)
    {
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    public static void copyFileToUri(Context context, File sourceFile, Uri destinationUri) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = context.getContentResolver().openOutputStream(destinationUri)) {
            if (out == null) {
                throw new IOException("Failed to open output stream for URI: " + destinationUri);
            }
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    public static void copyFile(File sourceFile, File destinationFile) throws IOException {
        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    public static void copyUriToFile(Context context, Uri sourceUri, File destinationFile) throws IOException {
        try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
             OutputStream out = new FileOutputStream(destinationFile)) {
            if (in == null) {
                throw new IOException("Failed to open input stream for URI: " + sourceUri);
            }
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }

    public static class HexInputFilter implements InputFilter {
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


    public static String rgbToHex(int r, int g, int b) {
        return format("%02X%02X%02X", r, g, b);
    }

    public static boolean isValidHexCode(String hexCode) {
        Pattern pattern = Pattern.compile("^[0-9a-fA-F]{6}$");
        Matcher matcher = pattern.matcher(hexCode);
        return matcher.matches();
    }

    public static void setNfcLaunchMode(Context context, boolean allowLaunch ) {
        ComponentName componentName = new ComponentName(context, LaunchActivity.class);
        PackageManager packageManager = context.getPackageManager();
        if (allowLaunch) {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
        else {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public static String getMifareBlockDefinition(int sector, int blockInSector, int totalBlocksInSector) {
        if (sector == 0 && blockInSector == 0) {
            return "MANUFACTURER (UID)";
        }
        if (blockInSector == totalBlocksInSector - 1) {
            return "Keys A/B + Access Bits";
        }
        return "USER DATA";
    }

    public static String getTypeName(int type) {
        switch (type) {
            case MifareClassic.TYPE_CLASSIC: return "Mifare Classic";
            case MifareClassic.TYPE_PLUS: return "Mifare Plus";
            case MifareClassic.TYPE_PRO: return "Mifare Pro";
            default: return "Mifare";
        }
    }

    public static Object getDoubleOrNull(EditText editText) {
        String val = editText.getText().toString().trim();
        if (val.isEmpty()) return JSONObject.NULL;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return JSONObject.NULL;
        }
    }

    public static Object getIntOrNull(EditText editText) {
        String val = editText.getText().toString().trim();
        if (val.isEmpty()) return JSONObject.NULL;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return JSONObject.NULL;
        }
    }

    public static void hideKeyboard(View view) {
        if (view == null) return;
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String performSmRequest(Context context, String urlString, String method, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        if (jsonBody != null && (method.equals("POST") || method.equals("PATCH") || method.equals("PUT"))) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(context.getString(R.string.utf_8));
                os.write(input, 0, input.length);
            }
        }
        int responseCode = conn.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), context.getString(R.string.utf_8)));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        } else {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), context.getString(R.string.utf_8)));
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine.trim());
            }
            return null;
        }
    }

    public static int getContrastColor(@ColorInt int backgroundColor) {
        int red = Color.red(backgroundColor);
        int green = Color.green(backgroundColor);
        int blue = Color.blue(backgroundColor);
        double luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0;
        return (luminance > 0.5) ? Color.BLACK : Color.WHITE;
    }

    public static int[] presetColors() {
        return new int[]{
                Color.parseColor("#25C4DA"),
                Color.parseColor("#0099A7"),
                Color.parseColor("#0B359A"),
                Color.parseColor("#0A4AB6"),
                Color.parseColor("#11B6EE"),
                Color.parseColor("#90C6F5"),
                Color.parseColor("#FA7C0C"),
                Color.parseColor("#F7B30F"),
                Color.parseColor("#E5C20F"),
                Color.parseColor("#B18F2E"),
                Color.parseColor("#8D766D"),
                Color.parseColor("#6C4E43"),
                Color.parseColor("#E62E2E"),
                Color.parseColor("#EE2862"),
                Color.parseColor("#EA2A2B"),
                Color.parseColor("#E83D89"),
                Color.parseColor("#AE2E65"),
                Color.parseColor("#611C8B"),
                Color.parseColor("#8D60C7"),
                Color.parseColor("#B287C9"),
                Color.parseColor("#006764"),
                Color.parseColor("#018D80"),
                Color.parseColor("#42B5AE"),
                Color.parseColor("#1D822D"),
                Color.parseColor("#54B351"),
                Color.parseColor("#72E115"),
                Color.parseColor("#474747"),
                Color.parseColor("#668798"),
                Color.parseColor("#B1BEC6"),
                Color.parseColor("#58636E"),
                Color.parseColor("#F8E911"),
                Color.parseColor("#F6D311"),
                Color.parseColor("#F2EFCE"),
                Color.parseColor("#FFFFFF"),
                Color.parseColor("#000000")
        };
    }

}