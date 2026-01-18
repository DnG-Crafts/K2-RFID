using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Renci.SshNet;
using SixLabors.ImageSharp;
using SixLabors.ImageSharp.PixelFormats;
using SixLabors.ImageSharp.Processing;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using ImageSharpImage = SixLabors.ImageSharp.Image;

namespace CFS_RFID
{
    internal class Utils
    {

        public static byte[] KEY_DEFAULT = new byte[] { 255, 255, 255, 255, 255, 255 };

        public static void SaveMaterials(string pType, string version)
        {
            MatDB.SaveFilaments(pType.ToLower(), version);
        }

        public static void AddMaterial(Filament filament)
        {
            MatDB.AddFilament(filament);
        }

        public static void EditMaterial(Filament filament)
        {
            MatDB.EditFilament(filament);
        }

        public static void RemoveMaterial(Filament filament)
        {
            MatDB.RemoveFilament(filament);
        }

        public static void RemoveMaterial(string materialId)
        {
            MatDB.RemoveFilament(MatDB.GetFilamentById(materialId));
        }

        public static void LoadMaterials(string pType)
        {
            MatDB.LoadFilaments(pType);
        }

        public static string GetDatabaseVersion(string pType)
        {
            return MatDB.GetVersion(pType);
        }

        public static void SetDatabaseVersion(string pType, string version)
        {
            MatDB.SetVersion(pType, version);
        }

        public static string[] GetMaterials()
        {
            List<Filament> items = MatDB.GetAllFilaments();
            string[] materials = new string[items.Count];
            for (int i = 0; i < items.Count; i++)
            {
                materials[i] = items[i].FilamentName;
            }
            return materials;
        }

        public static string[] GetMaterialsByBrand(string materialBrand)
        {
            List<Filament> items = MatDB.GetFilamentsByVendor(materialBrand);
            string[] materials = new string[items.Count];
            for (int i = 0; i < items.Count; i++)
            {
                materials[i] = items[i].FilamentName;
            }
            return materials;
        }

        public static string[] GetMaterialName(string materialId)
        {
            Filament item = MatDB.GetFilamentById(materialId);
            if (item == null)
            {
                return null;
            }
            else
            {
                return new string[] { item.FilamentName, item.FilamentVendor };
            }
        }

        public static string GetMaterialParam(string materialId)
        {
            Filament item = MatDB.GetFilamentById(materialId);
            return item.FilamentParam;
        }

        public static string GetMaterialID(string materialVendor, string materialName)
        {
            Filament item = MatDB.GetFilamentByName(materialVendor, materialName);
            return item.FilamentId;
        }

        public static string GetMaterialBrand(string materialId)
        {
            Filament item = MatDB.GetFilamentById(materialId);
            return item.FilamentVendor;
        }

        public static Filament GetMaterialByID(string materialId)
        {
            return MatDB.GetFilamentById(materialId);
        }

        public static string[] GetMaterialBrands()
        {
            List<Filament> items = MatDB.GetAllFilaments();
            HashSet<string> uniqueBrandsSet = new HashSet<string>();
            foreach (Filament item in items)
            {
                uniqueBrandsSet.Add(item.FilamentVendor);
            }
            return uniqueBrandsSet.ToArray();
        }

        public static string GetMaterialLength(string materialWeight)
        {
            switch (materialWeight)
            {
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

        public static string GetMaterialWeight(string materialLength)
        {
            switch (materialLength)
            {
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

        public static int GetMaterialIntWeight(string materialWeight)
        {
            switch (materialWeight)
            {
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

        public static string[] printerTypes = {
            "K2",
            "K1",
            "HI"};

        public static byte[] CreateKey(byte[] tagId)
        {
            try
            {
                using (AesCryptoServiceProvider aesAlg = new AesCryptoServiceProvider())
                {
                    aesAlg.Mode = CipherMode.ECB;
                    aesAlg.Padding = PaddingMode.None;
                    aesAlg.Key = new byte[]
                    {113, 51, 98, 117, 94, 116, 49, 110, 113, 102, 90, 40, 112, 102, 36, 49};
                    ICryptoTransform encryptor = aesAlg.CreateEncryptor(aesAlg.Key, null);
                    int x = 0;
                    byte[] encB = new byte[16];
                    for (int i = 0; i < 16; i++)
                    {
                        if (x >= 4) x = 0;
                        encB[i] = tagId[x];
                        x++;
                    }
                    byte[] encryptedBytes = encryptor.TransformFinalBlock(encB, 0, encB.Length);

                    return encryptedBytes.Take(6).ToArray();
                }
            }
            catch (Exception)
            {
                return KEY_DEFAULT;
            }
        }

        public static byte[] CipherData(int mode, byte[] tagData)
        {
            try
            {
                using (AesCryptoServiceProvider aesAlg = new AesCryptoServiceProvider())
                {
                    aesAlg.Mode = CipherMode.ECB;
                    aesAlg.Padding = PaddingMode.None;
                    aesAlg.Key = new byte[]
                    {72, 64, 67, 70, 107, 82, 110, 122, 64, 75, 65, 116, 66, 74, 112, 50};
                    ICryptoTransform cryptoTransform;
                    if (mode == 1)
                    {
                        cryptoTransform = aesAlg.CreateEncryptor(aesAlg.Key, null);
                    }
                    else
                    {
                        cryptoTransform = aesAlg.CreateDecryptor(aesAlg.Key, null);
                    }
                    return cryptoTransform.TransformFinalBlock(tagData, 0, tagData.Length);
                }
            }
            catch (Exception)
            { }
            return null;
        }


        public static string ReadTag(Reader reader)
        {
            MemoryStream buff = new MemoryStream(96);
            if (reader.Authentication10byte(4, 96, 1) || reader.Authentication6byte(4, 96, 1))
            {
                byte[] s1Data = new byte[48];
                byte[] b4 = reader.ReadBinaryBlocks(4, 16);
                byte[] b5 = reader.ReadBinaryBlocks(5, 16);
                byte[] b6 = reader.ReadBinaryBlocks(6, 16);

                Array.Copy(b4, 0, s1Data, 0, 16);
                Array.Copy(b5, 0, s1Data, 16, 16);
                Array.Copy(b6, 0, s1Data, 32, 16);
                byte[] s1Decrypted = Utils.CipherData(0, s1Data);
                buff.Write(s1Decrypted, 0, 48);
            }
            else
            {
                throw new Exception("Failed to authenticate");
            }
            if (reader.Authentication10byte(8, 96, 0) || reader.Authentication6byte(8, 96, 0))
            {
                buff.Write(reader.ReadBinaryBlocks(8, 16), 0, 16);
                buff.Write(reader.ReadBinaryBlocks(9, 16), 0, 16);
                buff.Write(reader.ReadBinaryBlocks(10, 16), 0, 16);
            }
            return Encoding.UTF8.GetString(buff.ToArray()).Trim();
        }


        public static void FormatTag(Reader reader)
        {
            byte[] emptyData = new byte[16]; 
            if (reader.Authentication10byte(4, 96, 1) || reader.Authentication6byte(4, 96, 1))
            {
                for (byte i = 4; i <= 6; i++)
                {
                    reader.UpdateBinaryBlocks(i, 16, emptyData);
                }
                byte[] trailer = reader.ReadBinaryBlocks(7, 16);
                if (trailer != null)
                {
                    Array.Copy(KEY_DEFAULT, 0, trailer, 0, 6);  
                    Array.Copy(KEY_DEFAULT, 0, trailer, 10, 6); 
                    reader.UpdateBinaryBlocks(7, 16, trailer);
                }
            }
            if (reader.Authentication10byte(8, 96, 0) || reader.Authentication6byte(8, 96, 0))
            {
                for (byte i = 8; i <= 10; i++)
                {
                    reader.UpdateBinaryBlocks(i, 16, emptyData);
                }
                byte[] trailerS2 = reader.ReadBinaryBlocks(11, 16);
                if (trailerS2 != null)
                {
                    Array.Copy(KEY_DEFAULT, 0, trailerS2, 0, 6);
                    Array.Copy(KEY_DEFAULT, 0, trailerS2, 10, 6);
                    reader.UpdateBinaryBlocks(11, 16, trailerS2);
                }
            }
        }


        public static string ReaderVersion(Reader reader)
        {
            try
            {
                return Encoding.ASCII.GetString(reader.GetFirmwareVersion());
            }
            catch { return string.Empty; }
        }

        public static string RandomSerial()
        {
            using (RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider())
            {
                byte[] randomNumber = new byte[4];
                rng.GetBytes(randomNumber);
                int randomInt = BitConverter.ToInt32(randomNumber, 0);
                int randomNumberInRange = Math.Abs(randomInt % 900000);
                return randomNumberInRange.ToString("D6");
            }
        }

        public static bool CheckDBfile(string pType)
        {
            string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + pType + ".json";
            if (File.Exists(filePath))
            {
                return true;
            }
            return false;
        }

        public static byte[] GetDBfile(string pType)
        {
            string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + pType + ".json";
            try
            {
                byte[] fileBytes = File.ReadAllBytes(filePath);
                return fileBytes;
            }
            catch (Exception)
            {
                return null;
            }
        }

        public static void SetDBfile(string fileName, byte[] resData)
        {
            string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + fileName;
            if (File.Exists(filePath))
            {
                return;
            }
            string directory = Path.GetDirectoryName(filePath);
            if (!string.IsNullOrEmpty(directory) && !Directory.Exists(directory))
            {
                try
                {
                    Directory.CreateDirectory(directory);
                }
                catch (Exception)
                {
                    return;
                }
            }
            if (resData == null || resData.Length == 0)
            {
                return;
            }
            using (MemoryStream memoryStream = new MemoryStream(resData))
            {
                byte[] readBytes = new byte[memoryStream.Length];
                int bytesRead = memoryStream.Read(readBytes, 0, readBytes.Length);
                memoryStream.Position = 0;
                using (FileStream fileStream = File.Create(filePath))
                {
                    memoryStream.CopyTo(fileStream);
                }
            }

        }

        public static string[] GetPrinterTypes()
        {
            try
            {
                string folderPath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\";
                if (!Directory.Exists(folderPath))
                {
                    return new string[0];
                }
                string[] files = Directory.GetFiles(folderPath, "*.json");
                var printerNames = files.Select(file =>
                {
                    return Path.GetFileNameWithoutExtension(file);
                });
                return printerNames.OrderBy(name => name).ToArray();
            }
            catch
            {
                return new string[0];
            }
        }

        public static string SendSShCommand(string psw, string host, string command)
        {
            using (var client = new SshClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    using (var cmd = client.CreateCommand(command))
                    {
                        cmd.CommandTimeout = TimeSpan.FromSeconds(5);
                        return cmd.Execute();
                    }
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static void ResetJsonDB(string psw, string host, string pType, bool resetApp)
        {
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    byte[] resData = Encoding.UTF8.GetBytes(GetJsonDB(pType, "0.4"));
                    SetJsonDB(resData, psw, host, pType, "material_database.json");
                    if (resetApp)
                    {
                        string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + pType + ".json";
                        using (MemoryStream memoryStream = new MemoryStream(resData))
                        {
                            byte[] readBytes = new byte[memoryStream.Length];
                            int bytesRead = memoryStream.Read(readBytes, 0, readBytes.Length);
                            memoryStream.Position = 0;
                            using (FileStream fileStream = File.Create(filePath))
                            {
                                memoryStream.CopyTo(fileStream);
                            }
                        }
                    }
                    SendSShCommand(psw, host, "reboot");
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
            }
        }

        public static void SetJsonDB(string psw, string host, string pType)
        {
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    string filepath = "/mnt/UDISK/creality/userdata/box/material_database.json";
                    if (pType.ToLower().Contains("k1"))
                    {
                        filepath = "/usr/data/creality/userdata/box/material_database.json";
                    }
                    using (var stream = new MemoryStream(GetDBfile(pType.ToLower())))
                    {
                        client.Upload(stream, filepath);
                    }
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static void SetJsonDB(string dbData, string psw, string host, string pType, string fileName)
        {
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    string filepath = "/mnt/UDISK/creality/userdata/box/" + fileName;
                    if (pType.ToLower().Contains("k1"))
                    {
                        filepath = "/usr/data/creality/userdata/box/" + fileName;
                    }
                    using (var stream = new MemoryStream(Encoding.ASCII.GetBytes(dbData)))
                    {
                        client.Upload(stream, filepath);
                    }
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static void SetJsonDB(byte[] dbData, string psw, string host, string pType, string fileName)
        {
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    string filepath = "/mnt/UDISK/creality/userdata/box/" + fileName;
                    if (pType.ToLower().Contains("k1"))
                    {
                        filepath = "/usr/data/creality/userdata/box/" + fileName;
                    }
                    using (var stream = new MemoryStream(dbData))
                    {
                        client.Upload(stream, filepath);
                    }
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static void GetJsonDB(string psw, string host, string pType, string fileName)
        {
            string localfilePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + pType + ".json";
            string directory = Path.GetDirectoryName(localfilePath);
            if (!string.IsNullOrEmpty(directory) && !Directory.Exists(directory))
            {
                try
                {
                    Directory.CreateDirectory(directory);
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
            }
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    string filepath = "/mnt/UDISK/creality/userdata/box/" + fileName;
                    if (pType.ToLower().Contains("k1"))
                    {
                        filepath = "/usr/data/creality/userdata/box/" + fileName;
                    }
                    using (var stream = new FileStream(localfilePath, FileMode.Create))
                    {
                        client.Download(filepath, stream);
                    }
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static string GetJsonDB(string psw, string host, string pType)
        {
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    string filepath = "/mnt/UDISK/creality/userdata/box/material_database.json";
                    if (pType.ToLower().Contains("k1"))
                    {
                        filepath = "/usr/data/creality/userdata/box/material_database.json";
                    }
                    byte[] dbData;
                    using (var stream = new MemoryStream())
                    {
                        client.Download(filepath, stream);
                        dbData = stream.ToArray();
                    }
                    return Encoding.ASCII.GetString(dbData);
                }
                catch (Exception e)
                {
                    throw new Exception(e.Message);
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static string GetPrinterVersion(string psw, string host, string pType)
        {
            using (var client = new ScpClient(host, 22, "root", psw))
            {
                try
                {
                    client.ConnectionInfo.Timeout = TimeSpan.FromSeconds(5);
                    client.Connect();
                    string filepath = "/mnt/UDISK/creality/userdata/box/material_database.json";
                    if (pType.ToLower().Contains("k1"))
                    {
                        filepath = "/usr/data/creality/userdata/box/material_database.json";
                    }
                    byte[] dbData;
                    using (var stream = new MemoryStream())
                    {
                        client.Download(filepath, stream);
                        dbData = stream.ToArray();
                    }
                    JObject materials = JObject.Parse(Encoding.ASCII.GetString(dbData));
                    JObject result = (JObject)materials["result"];
                    return result["version"].ToString();
                }
                catch
                {
                    return "0";
                }
                finally
                {
                    if (client.IsConnected)
                    {
                        client.Disconnect();
                    }
                }
            }
        }

        public static void SaveMatOption(string psw, string host, string pType, bool reboot)
        {
            JObject materials = JObject.Parse(Encoding.ASCII.GetString(GetDBfile(pType.ToLower())));
            JObject options = new JObject();
            JObject result = (JObject)materials["result"];
            JArray list = (JArray)result["list"];
            HashSet<string> uniqueBrandsSet = new HashSet<string>();
            foreach (JToken itemToken in list)
            {
                JObject items = (JObject)itemToken;
                JObject baseObject = (JObject)items["base"];
                uniqueBrandsSet.Add(baseObject.Value<string>("brand"));
            }
            foreach (string brand in uniqueBrandsSet)
            {
                options[brand] = new JObject();
                JObject vendor = (JObject)options[brand];
                foreach (JToken itemToken in list)
                {
                    JObject items = (JObject)itemToken;
                    JObject baseObject = (JObject)items["base"];
                    if (baseObject.Value<string>("brand").Equals(brand))
                    {
                        string tmpType = baseObject.Value<string>("meterialType");
                        string name = baseObject.Value<string>("name");
                        if (vendor.ContainsKey(tmpType))
                        {
                            vendor[tmpType] = vendor.Value<string>(tmpType) + "\n" + name;
                        }
                        else
                        {
                            vendor[tmpType] = name;
                        }
                    }
                }
            }
            SetJsonDB(options.ToString(Formatting.Indented), psw, host, pType, "material_option.json");
            if (reboot)
            {
                SendSShCommand(psw, host, "reboot");
            }
        }


        public static string FetchDataFromApi(string apiUrl)
        {
            using (WebClient client = new WebClient())
            {
                string api_useragent = "BBL-Slicer/v01.09.03.50 (dark) Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.52";
                client.Headers[HttpRequestHeader.UserAgent] = api_useragent;
                client.Headers[HttpRequestHeader.ContentType] = "application/json";
                client.Headers.Add("__CXY_BRAND_", "creality");
                client.Headers.Add("__CXY_UID_", "");
                client.Headers.Add("__CXY_OS_LANG_", "0");
                client.Headers.Add("__CXY_DUID_", Guid.NewGuid().ToString());
                client.Headers.Add("__CXY_APP_VER_", "1.0");
                client.Headers.Add("__CXY_APP_CH_", "CP_Beta");
                client.Headers.Add("__CXY_OS_VER_", api_useragent);
                client.Headers.Add("__CXY_TIMEZONE_", "28800");
                client.Headers.Add("__CXY_APP_ID_", "creality_model");
                client.Headers.Add("__CXY_REQUESTID_", Guid.NewGuid().ToString());
                client.Headers.Add("__CXY_PLATFORM_", "11");
                var body = new JObject { ["engineVersion"] = "3.0.0" };
                if (apiUrl.Contains("materialList")) body["pageSize"] = 500;
                string jsonBody = body.ToString(Formatting.None);
                return client.UploadString(apiUrl, "POST", jsonBody);
            }
        }

        public static string GetZipUrl(string targetPrinterName, string targetNozzle)
        {
            try
            {
                string json = FetchDataFromApi("https://api.crealitycloud.com/api/cxy/v2/slice/profile/official/printerList");
                var root = JObject.Parse(json);
                var printerList = root["result"]?["printerList"] as JArray;
                if (printerList != null)
                {
                    foreach (var printer in printerList)
                    {
                        if (printer["name"]?.ToString().Equals(targetPrinterName, StringComparison.OrdinalIgnoreCase) == true)
                        {
                            var nozzles = printer["nozzleDiameter"] as JArray;
                            if (nozzles != null && nozzles.Any(n => n.ToString() == targetNozzle))
                            {
                                return printer["zipUrl"]?.ToString();
                            }
                        }
                    }
                }
            }
            catch { }
            return null;
        }

        public static JArray FindPrinters(string[] targetNames, string targetNozzle)
        {
            var results = new JArray();
            try
            {
                string json = FetchDataFromApi("https://api.crealitycloud.com/api/cxy/v2/slice/profile/official/printerList");
                var root = JObject.Parse(json);
                var printerList = root["result"]?["printerList"] as JArray;

                if (printerList != null)
                {
                    foreach (var printer in printerList)
                    {
                        string printerName = printer["name"]?.ToString().ToLower() ?? "";
                        bool nameMatches = targetNames.Any(t => printerName.Contains(t.ToLower()));
                        if (nameMatches)
                        {
                            var nozzles = printer["nozzleDiameter"] as JArray;
                            if (nozzles != null && nozzles.Any(n => n.ToString() == targetNozzle))
                            {
                                results.Add(printer);
                            }
                        }
                    }
                }
            }
            catch { }
            return results;
        }

        public static JArray FindPrinters(string targetName, string targetNozzle)
        {
            var results = new JArray();
            try
            {
                string json = FetchDataFromApi("https://api.crealitycloud.com/api/cxy/v2/slice/profile/official/printerList");
                var root = JObject.Parse(json);
                var printerList = root["result"]?["printerList"] as JArray;

                if (printerList != null)
                {
                    foreach (var printer in printerList)
                    {
                        string printerName = printer["name"]?.ToString().ToLower() ?? "";
                        if (printerName.IndexOf(targetName, StringComparison.OrdinalIgnoreCase) >= 0)
                        {
                            JArray nozzles = (JArray)printer["nozzleDiameter"];
                            bool hasNozzle = nozzles.Any(n => n.ToString() == targetNozzle);
                            if (hasNozzle)
                            {
                                results.Add(printer);
                            }
                        }
                    }
                }
            }
            catch { }
            return results;
        }
       

        public static string ProcessMaterials(string materialListJson, List<string> filamentJsonList, string zipVersion)
        {
            try
            {
                var finalListItemArray = new JArray();
                var listRoot = JObject.Parse(materialListJson);
                var allBaseMaterials = listRoot["result"]?["list"] as JArray;

                foreach (var filamentJson in filamentJsonList)
                {
                    var sourceObj = JObject.Parse(filamentJson);
                    string targetName = sourceObj["metadata"]?["name"]?.ToString();

                    var rawBase = allBaseMaterials?.FirstOrDefault(b => b["name"]?.ToString() == targetName) as JObject;
                    if (rawBase != null)
                    {
                        var cleanBase = new JObject();
                        foreach (var prop in rawBase.Properties())
                        {
                            if (new[] { "createTime", "status", "userInfo" }.Contains(prop.Name)) continue;
                            cleanBase.Add(prop.Name, prop.Value);
                        }

                        var listItem = new JObject
                        {
                            ["engineVersion"] = sourceObj["engine_version"],
                            ["printerIntName"] = "F008",
                            ["nozzleDiameter"] = new JArray { "0.4" },
                            ["kvParam"] = sourceObj["engine_data"],
                            ["base"] = cleanBase
                        };
                        finalListItemArray.Add(listItem);
                    }
                }

                var resultObj = new JObject
                {
                    ["list"] = finalListItemArray,
                    ["count"] = finalListItemArray.Count,
                    ["version"] = zipVersion ?? DateTimeOffset.UtcNow.ToUnixTimeSeconds().ToString()
                };

                var targetRoot = new JObject
                {
                    ["code"] = 0,
                    ["msg"] = "ok",
                    ["reqId"] = "0",
                    ["result"] = resultObj
                };

                return targetRoot.ToString(Formatting.Indented);
            }
            catch { return null; }
        }

        public static string GetJsonDB(string targetPrinterName, string targetNozzle)
        {
            try
            {
                string zipUrl = GetZipUrl(targetPrinterName, targetNozzle);
                if (string.IsNullOrEmpty(zipUrl)) return null;
                string materialListStr = FetchDataFromApi("https://api.crealitycloud.com/api/cxy/v2/slice/profile/official/materialList");
                var filamentDataList = new List<string>();
                string extractedVersion = null;
                using (WebClient client = new WebClient())
                {
                    byte[] zipData = client.DownloadData(zipUrl);
                    using (MemoryStream ms = new MemoryStream(zipData))
                    using (ZipArchive archive = new ZipArchive(ms))
                    {
                        foreach (ZipArchiveEntry entry in archive.Entries)
                        {
                            if (entry.FullName.EndsWith(".json", StringComparison.OrdinalIgnoreCase))
                            {
                                using (Stream entryStream = entry.Open())
                                using (StreamReader reader = new StreamReader(entryStream))
                                {
                                    string content = reader.ReadToEnd();

                                    if (!entry.FullName.Contains("/"))
                                    {
                                        var rootDef = JObject.Parse(content);
                                        extractedVersion = rootDef["version"]?.ToString();
                                    }
                                    else if (entry.FullName.StartsWith("materials/", StringComparison.OrdinalIgnoreCase))
                                    {
                                        filamentDataList.Add(content);
                                    }
                                }
                            }
                        }
                    }
                }
                if (filamentDataList.Any())
                    return ProcessMaterials(materialListStr, filamentDataList, extractedVersion);
            }
            catch { }
            return null;
        }


        public static void LoadPrinterImage(string urlString, PictureBox pictureBox)
        {
            new Thread(() =>
            {
                try
                {
                    using (WebClient client = new WebClient())
                    {
                        byte[] data = client.DownloadData(urlString);
                        if (urlString.ToLower().EndsWith(".webp"))
                        {
                            using (MemoryStream ms = new MemoryStream(data))
                            {
                                using (var image = ImageSharpImage.Load<Rgba32>(ms))
                                {
                                    image.Mutate(x => x.BackgroundColor(SixLabors.ImageSharp.Color.ParseHex("#F4F4F4")));
                                    using (var outStream = new MemoryStream())
                                    {
                                        image.SaveAsBmp(outStream);
                                        outStream.Position = 0;
                                        pictureBox.Image = System.Drawing.Image.FromStream(outStream);
                                        pictureBox.SizeMode = PictureBoxSizeMode.Zoom;
                                    }
                                }
                            }
                        }
                        else
                        {
                            using (MemoryStream ms = new MemoryStream(data))
                            {
                                Bitmap bmp = new Bitmap(ms);
                                pictureBox.Invoke((MethodInvoker)delegate
                                {
                                    pictureBox.Image?.Dispose();
                                    pictureBox.Image = bmp;
                                });
                            }
                        }
                    }
                }
                catch { }
            }).Start();
        }


        public static string[] filamentVendors = {
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

        public static string[] filamentTypes = {
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
            "PCTG"};



        public static string SmAddSpool(string host, int port, string materialID, string hexColor, string colorName, int weightGrams, string printerType)
        {
            string baseUrl = string.Format("http://{0}:{1}/api/v1", host, port);
            try
            {
                var localData = GetMaterialByID(materialID);
                if (localData == null) return "MaterialID " + materialID + " not found";
                string vendorName = localData.FilamentVendor.Trim();
                string fNameWithColor = localData.FilamentName.Trim() + " (" + colorName + ")";
                int vendorId = -1;
                string vRes = PerformSmRequest(baseUrl + "/vendor", "GET");
                if (vRes == null)
                {
                    return "Error adding spool";
                }
                else 
                { 
                    JArray vArray = JArray.Parse(vRes);
                    foreach (JObject v in vArray)
                    {
                        if (string.Equals(v["name"].ToString(), vendorName, StringComparison.OrdinalIgnoreCase))
                        {
                            vendorId = (int)v["id"];
                            break;
                        }
                    }
                }
                if (vendorId == -1)
                {
                    string vBody = JsonConvert.SerializeObject(new { 
                        name = vendorName, 
                        comment = "Created by: Cfs RFID"
                    });
                    string newV = PerformSmRequest(baseUrl + "/vendor", "POST", vBody);
                    if (newV != null)
                        vendorId = (int)JObject.Parse(newV)["id"];
                }
                int filamentId = -1;
                string fRes = PerformSmRequest(baseUrl + "/filament", "GET");
                if (fRes != null)
                {
                    JArray fArray = JArray.Parse(fRes);
                    foreach (JObject f in fArray)
                    {
                        int vIdCheck = (f["vendor"] != null && f["vendor"].Type != JTokenType.Null)
                                       ? (int)f["vendor"]["id"] : -1;

                        if (vIdCheck == vendorId && string.Equals(f["name"].ToString(), fNameWithColor, StringComparison.OrdinalIgnoreCase))
                        {
                            filamentId = (int)f["id"];
                            break;
                        }
                    }
                }
                if (filamentId == -1)
                {
                    var fBodyDict = new Dictionary<string, object>();
                    fBodyDict.Add("name", fNameWithColor);
                    fBodyDict.Add("vendor_id", vendorId);
                    fBodyDict.Add("color_hex", hexColor.Replace("#", ""));
                    fBodyDict.Add("comment", "Created by: Cfs RFID");
                    if (!string.IsNullOrEmpty(localData.FilamentParam))
                    {
                        JObject root = JObject.Parse(localData.FilamentParam);
                        if (root["base"] != null)
                        {
                            fBodyDict.Add("material", root["base"]["meterialType"].ToString());
                            fBodyDict.Add("diameter", (double)root["base"]["diameter"]);
                        }
                        if (root["kvParam"] != null)
                        {
                            JToken kvParam = root["kvParam"];
                            if (kvParam["nozzle_temperature"] != null)
                                fBodyDict.Add("settings_extruder_temp", int.Parse(kvParam["nozzle_temperature"].ToString()));
                            if (kvParam["hot_plate_temp"] != null)
                                fBodyDict.Add("settings_bed_temp", int.Parse(kvParam["hot_plate_temp"].ToString()));
                            if (kvParam["filament_density"] != null)
                                fBodyDict.Add("density", (double)kvParam["filament_density"]);
                        }
                    }
                    string newF = PerformSmRequest(baseUrl + "/filament", "POST", JsonConvert.SerializeObject(fBodyDict));
                    if (newF != null)
                        filamentId = (int)JObject.Parse(newF)["id"];
                }

                if (filamentId != -1)
                {
                    var sBodyObj = new
                    {
                        filament_id = filamentId,
                        initial_weight = weightGrams,
                        remaining_weight = weightGrams,
                        comment = "RFID tagged for " + printerType
                    };
                    string sBody = JsonConvert.SerializeObject(sBodyObj);
                    string ret = PerformSmRequest(baseUrl + "/spool", "POST", sBody);
                    return ret != null ? "Spool created for\n" + fNameWithColor : "Failed to create spool";
                }
            }
            catch (Exception e)
            {
                return "Error " + e.Message;
            }
            return null;
        }

        private static string PerformSmRequest(string url, string method, string jsonBody = null)
        {
            using (TimedWebClient client = new TimedWebClient())
            {
                client.Encoding = Encoding.UTF8;
                client.Headers[HttpRequestHeader.ContentType] = "application/json";
                client.Headers[HttpRequestHeader.Accept] = "application/json";
                try
                {
                    if (method == "GET")
                    {
                        return client.DownloadString(url);
                    }
                    else
                    {
                        return client.UploadString(url, method, jsonBody ?? "");
                    }
                }
                catch (WebException)
                {
                    return null;
                }
            }
        }


        public class TimedWebClient : WebClient
        {
            public int Timeout { get; set; } = 5000;
            protected override WebRequest GetWebRequest(Uri address)
            {
                WebRequest request = base.GetWebRequest(address);
                if (request != null)
                {
                    request.Timeout = this.Timeout;
                }
                return request;
            }
        }



    }



}
