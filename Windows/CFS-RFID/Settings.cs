using Microsoft.Win32;
using System;

namespace CFS_RFID
{
    internal class Settings
    {
        public static void SaveSetting(string keyName, bool value)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.CreateSubKey("CFS RFID\\Settings"))
                {
                    key?.SetValue(keyName, value ? 1 : 0, RegistryValueKind.DWord);
                }
            }
            catch (Exception){}
        }

        public static bool GetSetting(string keyName, bool defaultValue = false)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey("CFS RFID\\Settings"))
                {
                    if (key != null)
                    {
                        object value = key.GetValue(keyName);

                        if (value != null)
                        {
                            if (int.TryParse(value.ToString(), out int intValue))
                            {
                                return intValue != 0;
                            }
                        }
                    }
                    return defaultValue; 
                }
            }
            catch (Exception)
            {
                return defaultValue;
            }
        }

        public static void SaveSetting(string keyName, int value)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.CreateSubKey("CFS RFID\\Settings"))
                {
                    key?.SetValue(keyName, value, RegistryValueKind.DWord);
                }
            }
            catch (Exception){}
        }

        public static int GetSetting(string keyName, int defaultValue = 0)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey("CFS RFID\\Settings"))
                {
                    if (key != null)
                    {
                        object value = key.GetValue(keyName);

                        if (value != null)
                        {
                            if (int.TryParse(value.ToString(), out int parsedValue))
                            {
                                return parsedValue;
                            }
                            else if (value is int intValue)
                            {
                                return intValue;
                            }
                        }
                    }
                    return defaultValue;
                }
            }
            catch (Exception)
            {
                return defaultValue;
            }
        }

        public static void SaveSetting(string keyName, long value)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.CreateSubKey("CFS RFID\\Settings"))
                {
                    key?.SetValue(keyName, value, RegistryValueKind.QWord);
                }
            }
            catch (Exception){}
        }

        public static long GetSetting(string keyName, long defaultValue = 0)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey("CFS RFID\\Settings"))
                {
                    if (key != null)
                    {
                        object value = key.GetValue(keyName);
                        if (value != null)
                        {
                            if (long.TryParse(value.ToString(), out long parsedValue))
                            {
                                return parsedValue;
                            }
               
                            else if (value is long longValue)
                            {
                                return longValue;
                            }
                        }
                    }
                    return defaultValue;
                }
            }
            catch (Exception)
            {
                return defaultValue;
            }
        }

        public static void SaveSetting(string keyName, string value)
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.CreateSubKey("CFS RFID\\Settings"))
                {
                    key?.SetValue(keyName, value, RegistryValueKind.String);
                }
            }
            catch (Exception )
            {
 
            }
        }

        public static string GetSetting(string keyName, string defaultValue = "")
        {
            try
            {
                using (RegistryKey key = Registry.CurrentUser.OpenSubKey("CFS RFID\\Settings"))
                {
                    if (key != null)
                    {
                        object value = key.GetValue(keyName);

                        if (value != null)
                        {
                            return value.ToString();
                        }
                    }
                    return defaultValue;
                }
            }
            catch (Exception)
            {
                return defaultValue;
            }
        }
    }
}
