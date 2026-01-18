
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using static CFS_RFID.Utils;

public class MatDB
{

    public static List<Filament> mdb;

    public static void LoadFilaments(string pType)
    {
        JObject materials;
        mdb = new List<Filament>();
        try
        {
            if (CheckDBfile(pType.ToLower()))
            {
                materials = JObject.Parse(Encoding.ASCII.GetString(GetDBfile(pType.ToLower())));
            }
            else
            {
                return;
            }
            JObject result = (JObject)materials["result"];

            if (result != null)
            {
                JArray list = (JArray)result["list"];
                int i = 0;
                foreach (JToken itemToken in list)
                {
                    JObject item = (JObject)itemToken;
                    JObject baseObject = (JObject)item["base"];
                    Filament filament = new Filament
                    {
                        FilamentId = baseObject["id"].Value<string>().Trim(),
                        FilamentName = baseObject["name"].Value<string>().Trim(),
                        FilamentVendor = baseObject["brand"].Value<string>().Trim(),
                        FilamentType = baseObject["meterialType"].Value<string>().Trim(),
                        FilamentParam = item.ToString(Newtonsoft.Json.Formatting.None)
                    };
                    mdb.Add(filament);
                    i++;
                }
            }
        }
        catch { }
    }

    public static string GetVersion(string pType)
    {
        JObject materials;
        try
        {
            if (CheckDBfile(pType.ToLower()))
            {
                materials = JObject.Parse(Encoding.ASCII.GetString(GetDBfile(pType.ToLower())));
            }
            else
            {
                return "0";
            }
            JObject result = (JObject)materials["result"];
            if (result != null)
            {
                 return  result["version"].ToString();
            }
            return "0";
        }
        catch { return "0"; }
    }

    public static void SetVersion(string pType, string version)
    {
        try
        {
            string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + pType.ToLower() + ".json";
            JObject materials = JObject.Parse(Encoding.ASCII.GetString(GetDBfile(pType.ToLower())));
            JObject result = (JObject)materials["result"];
            if (result != null)
            {
                result["version"] = version;
                JsonSerializerSettings settings = new JsonSerializerSettings { MaxDepth = 2 };
                File.WriteAllText(filePath, JsonConvert.SerializeObject(materials, Formatting.Indented, settings), Encoding.ASCII);
            }
        }
        catch { }
    }

    public static Filament GetFilamentById(string filamentId)
    {
        try
        {
            foreach (Filament item in mdb)
            {
                if (item.FilamentId.Trim() == filamentId.Trim())
                {
                    return item;
                }
            }
            return null;
        }
        catch
        {
            return null;
        }
    }

    public static Filament GetFilamentByName(string filamentVendor, string filamentName)
    {
        try
        {
            foreach (Filament item in mdb)
            {
                if (item.FilamentName.Trim() == filamentName.Trim() && item.FilamentVendor.Trim() == filamentVendor.Trim())
                {
                    return item;
                }
            }
            return null;
        }
        catch
        {
            return null;
        }
    }

    public static List<Filament> GetFilamentsByVendor(string filamentVendor)
    {
        List<Filament> filaments = new List<Filament>();
        try
        {
            foreach (Filament item in mdb)
            {
                if (item.FilamentVendor.Trim() == filamentVendor.Trim())
                {
                    filaments.Add(item);
                }
            }
            return filaments;
        }
        catch
        {
            return filaments;
        }
    }

    public static List<Filament> GetAllFilaments()
    {
        return mdb;
    }

    public static void SaveFilaments(string pType, string version)
    {
        string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + pType.ToLower() + ".json";
        if (mdb == null || mdb.Count == 0)
        {
            return;
        }
        else
        {
            try
            {
                JArray list = new JArray();
                foreach (Filament filament in mdb)
                {
                    JObject jobject = JObject.Parse(filament.FilamentParam);
                    list.Add(jobject);
                }

                JObject result = new JObject();
                JObject json = new JObject();
                result.Add("list", list);
                result.Add("count", list.Count);
                result.Add("version", version);
                json.Add("code", 0);
                json.Add("msg", "ok");
                json.Add("reqId", "0");
                json.Add("result", result);
                JsonSerializerSettings settings = new JsonSerializerSettings { MaxDepth = 2 };
                File.WriteAllText(filePath, JsonConvert.SerializeObject(json, Formatting.Indented, settings), Encoding.ASCII);
            }
            catch { }
        }
    }

    public static void AddFilament(Filament filament)
    {
        try
        {
            JObject jobject = JObject.Parse(filament.FilamentParam);
            JObject baseObject = (JObject)jobject["base"];
            baseObject["id"] = filament.FilamentId;
            baseObject["brand"] = filament.FilamentVendor;
            baseObject["name"] = filament.FilamentName;
            baseObject["meterialType"] = filament.FilamentType;
            jobject["base"] = baseObject;
            filament.FilamentParam = JsonConvert.SerializeObject(jobject);
            mdb.Add(filament);
        }
        catch { }
    }

    public static void EditFilament(Filament filament)
    {
        try
        {
            foreach (Filament item in mdb)
            {
                if (item.FilamentId.Trim() == filament.FilamentId.Trim())
                {
                    mdb.Remove(item);
                    mdb.Add(filament);
                }
            }
        }
        catch { }
    }

    public static void RemoveFilament(Filament filament)
    {
        try
        {
            foreach (Filament item in mdb)
            {
                if (item.FilamentId.Trim() == filament.FilamentId.Trim())
                {
                    mdb.Remove(item);
                }
            }
        }
        catch { }
    }
}