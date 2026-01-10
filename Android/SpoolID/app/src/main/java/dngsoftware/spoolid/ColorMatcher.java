package dngsoftware.spoolid;

import android.content.Context;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ColorMatcher {

    private static class ColorEntry {
        String name;
        int r, g, b;

        ColorEntry(String name, String hex) {
            this.name = name;
            int color = Integer.parseInt(hex.replace("#", ""), 16);
            this.r = (color >> 16) & 0xFF;
            this.g = (color >> 8) & 0xFF;
            this.b = (color) & 0xFF;
        }
    }

    private final List<ColorEntry> colorList = new ArrayList<>();

    public ColorMatcher(Context context) {
        loadColors(context);
    }

    private void loadColors(Context context) {
        try (InputStream is = context.getAssets().open("colors.db");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry = zis.getNextEntry();
            if (entry != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
                String line;
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (parts.length >= 2) {
                        String name = parts[0].replace("\"", "").trim();
                        String hex = parts[1].trim();
                        if (hex.startsWith("#") && hex.length() == 7) {
                            colorList.add(new ColorEntry(name, hex));
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (Exception ignored) {}
    }

    public String findNearestColor(String targetHex) {
        try {
            int targetColor = Integer.parseInt(targetHex.replace("#", ""), 16);
            int r1 = (targetColor >> 16) & 0xFF;
            int g1 = (targetColor >> 8) & 0xFF;
            int b1 = (targetColor) & 0xFF;
            double minDistance = Double.MAX_VALUE;
            String closestName = null;
            for (ColorEntry entry : colorList) {
                double distance = Math.sqrt(Math.pow(r1 - entry.r, 2) + Math.pow(g1 - entry.g, 2) + Math.pow(b1 - entry.b, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestName = entry.name;
                }
            }
            return closestName;
        } catch (Exception ignored) {
            return null;
        }
    }
}