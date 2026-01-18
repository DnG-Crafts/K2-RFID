using System;
using System.Collections.Generic;
using System.IO;
using System.IO.Compression;
using System.Text.RegularExpressions;

namespace CFS_RFID
{
    public class ColorMatcher
    {
        private class ColorEntry
        {
            public string Name { get; set; }
            public int R { get; set; }
            public int G { get; set; }
            public int B { get; set; }

            public ColorEntry(string name, string hex)
            {
                this.Name = name;
                int color = Convert.ToInt32(hex.Replace("#", ""), 16);
                this.R = (color >> 16) & 0xFF;
                this.G = (color >> 8) & 0xFF;
                this.B = (color) & 0xFF;
            }
        }

        private readonly List<ColorEntry> colorList = new List<ColorEntry>();

        public ColorMatcher()
        {
            LoadColorsFromResources();
        }

        private void LoadColorsFromResources()
        {
            try
            {
                byte[] resourceData = Properties.Resources.colors;
                using (MemoryStream ms = new MemoryStream(resourceData))
                using (ZipArchive archive = new ZipArchive(ms, ZipArchiveMode.Read))
                {
                    ZipArchiveEntry entry = archive.Entries.Count > 0 ? archive.Entries[0] : null;
                    if (entry != null)
                    {
                        using (StreamReader reader = new StreamReader(entry.Open()))
                        {
                            reader.ReadLine(); 
                            string line;
                            while ((line = reader.ReadLine()) != null)
                            {
                                string[] parts = Regex.Split(line, ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                                if (parts.Length >= 2)
                                {
                                    string name = parts[0].Replace("\"", "").Trim();
                                    string hex = parts[1].Trim();
                                    if (hex.StartsWith("#") && hex.Length == 7)
                                    {
                                        colorList.Add(new ColorEntry(name, hex));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch {}
        }

        public string FindNearestColor(string targetHex)
        {
            try
            {
                int targetColor = Convert.ToInt32(targetHex.Replace("#", ""), 16);
                int r1 = (targetColor >> 16) & 0xFF;
                int g1 = (targetColor >> 8) & 0xFF;
                int b1 = targetColor & 0xFF;
                double minDistance = double.MaxValue;
                string closestName = null;
                foreach (var entry in colorList)
                {
                    double distance = Math.Sqrt(
                        Math.Pow(r1 - entry.R, 2) +
                        Math.Pow(g1 - entry.G, 2) +
                        Math.Pow(b1 - entry.B, 2)
                    );
                    if (distance < minDistance)
                    {
                        minDistance = distance;
                        closestName = entry.Name;
                    }
                }
                return closestName;
            }
            catch
            {
                return String.Empty;
            }
        }
    }
}
