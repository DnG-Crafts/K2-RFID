package dngsoftware.spoolid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.nfc.tech.MifareClassic;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
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
        Filament item = db.getFilamentById(materialId);
        return item.filamentVendor;
    }

    public static String[] GetMaterialName(MatDB db, String materialId) {
        String[] arrRet = new String[2];
        Filament item = db.getFilamentById(materialId);
        if (item == null) {
            return null;
        } else {
            arrRet[0] = item.filamentName;
            arrRet[1] = item.filamentVendor;
            return arrRet;
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

    public static boolean canMfc(Context context) {
        FeatureInfo[] info = context.getPackageManager().getSystemAvailableFeatures();
        for (FeatureInfo i : info) {
            String name = i.name;
            if (name != null && name.equals("com.nxp.mifare")) {
                return true;
            }
        }
        return false;
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
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getAssetDB(Context context, String pType) {
        try {
            StringBuilder sb = new StringBuilder();
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(pType +".json");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static long getDBVersion(Context context, String pType) {
        try {
            JSONObject materials = new JSONObject(getAssetDB(context, pType));
            JSONObject result = new JSONObject(materials.getString("result"));
            return result.getLong("version");
        } catch (Exception ignored) {
            return 0;
        }
    }

    public static void populateDatabase(Context context, MatDB db, String json, String pType) {
        try {
            JSONObject materials;
            if (json != null && !json.isEmpty()) {
                materials = new JSONObject(json);
            }else {
                materials = new JSONObject(getAssetDB(context, pType));
            }
            JSONObject result = new JSONObject(materials.getString("result"));
            SaveSetting(context, "version_" + pType, result.getLong("version"));
            JSONArray list = result.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject base = item.getJSONObject("base");
                Filament filament = new Filament();
                filament.position = i;
                filament.filamentID = base.getString("id").trim();
                filament.filamentName = base.getString("name").trim();
                filament.filamentVendor = base.getString("brand").trim();
                filament.filamentParam = item.toString();
                db.addItem(filament);
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


    public static String getJsonDB(String pType, boolean fromPrinter)
    {
        URL url;
        HttpURLConnection urlConnection;
        String server_response;
        try {
            if (fromPrinter)
            {
                url = new URL( "http://" + pType +  "/downloads/defData/material_database.json");
            }
            else
            {
                url = new URL( "https://raw.githubusercontent.com/DnG-Crafts/K2-RFID/refs/heads/main/db/" + pType + ".json");
            }
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setInstanceFollowRedirects(false);
            final int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                server_response = readStream(urlConnection.getInputStream());
           } else {
                server_response = null;
            }
        }
        catch (Exception e)
        {
            server_response = null;
        }
        return server_response;
    }

    public static void restorePrinterDB(Context context, String psw, String host, String pType) throws Exception {
        JSONObject jsonDb = new JSONObject(getAssetDB(context, pType));
        setJsonDB(jsonDb.toString(2), psw, host, pType, "material_database.json");
        if (pType.equalsIgnoreCase("k1")) {
            JSONObject jsonDbo = new JSONObject(getAssetDB(context,"k1o"));
            setJsonDB(jsonDbo.toString(2), psw, host, pType, "material_option.json");
        }
        sendSShCommand(psw, host, "reboot");
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

        if (pType.equalsIgnoreCase("k1")) {
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
            if (pType.equalsIgnoreCase("k1")) {
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
        if (pType.equalsIgnoreCase("k1")) {
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

    private static String readStream(InputStream in) {
        try {
            int len;
            byte[] buf = new byte[ 1024 ];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while((len = in.read(buf)) > 0)
            {
                outputStream.write(buf, 0, len);
            }
            in.close();
            return outputStream.toString();
        } catch (Exception ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {}
            }
        }
        return null;
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
                if (Objects.requireNonNull(adapter.getItem(i)).toString().equals(value)) {
                    return i;
                }
            }
        }
        return 0;
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