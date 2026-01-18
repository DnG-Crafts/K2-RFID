using CFS_RFID.Properties;
using Newtonsoft.Json.Linq;
using System;
using System.Drawing;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using static CFS_RFID.Utils;

namespace CFS_RFID
{
    public partial class UpdateForm : Form
    {
        public string SelectedPrinter { get; set; }
        private string sshDefault;
        private string currentVersion;
        private string newVersion;
        private string apiVersion;
        private string SelectedDatabase;

        public UpdateForm()
        {
            InitializeComponent();
        }

        private async void UpdateForm_Load(object sender, EventArgs e)
        {

            btnCancel.BackColor = ColorTranslator.FromHtml("#1976D2");
            btnUpdate.BackColor = ColorTranslator.FromHtml("#1976D2");
            btnCheck.BackColor = ColorTranslator.FromHtml("#1976D2");
            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            lblPrinter.ForeColor = ColorTranslator.FromHtml("#1976D2");
            btnUpdate.FlatAppearance.BorderSize = 0;
            btnCancel.FlatAppearance.BorderSize = 0;
            btnCheck.FlatAppearance.BorderSize = 0;

            if (SelectedPrinter == null)
            {
                this.DialogResult = DialogResult.No;
                this.Close();
                return;
            }

            SelectedDatabase = SelectedPrinter;

            if (SelectedPrinter.ToLower().Contains("hi"))
            {
                sshDefault = Resources.hiPsw;
            }
            else if (SelectedPrinter.ToLower().Contains("k1"))
            {
                sshDefault = Resources.k1Psw;
            }
            else
            {
                sshDefault = Resources.k2Psw;
            }

            txtIP.Text = Settings.GetSetting("host_" + SelectedPrinter, string.Empty);
            txtPass.Text = Settings.GetSetting("psw_" + SelectedPrinter, sshDefault);
            lblPrinter.Text = SelectedPrinter;
            SetDescMessage(string.Format(Resources.downloadDesc, SelectedPrinter));
            currentVersion = GetDatabaseVersion(SelectedPrinter);
            lblCver.Text = currentVersion;

            panel1.Visible = true;
            btnCheck.Visible = false;
            lblNver.Visible = true;
            lblAvl.Visible = true;
            btnUpdate.Visible = true;

            try
            {
                printerModel.Enabled = false;
                var result = await Task.Run(() => {
                    JArray printerList = Utils.FindPrinters(SelectedPrinter, "0.4");
                    return new { printerList };
                });
                printerModel.Items.Clear();
                var items = result.printerList.Cast<JObject>().Select(p => new {
                    DisplayName = p["name"]?.ToString(),
                    FullData = p
                }).ToList();
                printerModel.DisplayMember = "DisplayName";
                printerModel.DataSource = items;
                if (printerModel.Items.Count > 0) printerModel.SelectedIndex = 0;

            }
            catch
            {
                lblMsg.Text = "Failed to retrieve printer data";
            }
            finally
            {
                printerModel.Enabled = true;
            }
        }


        private void BtnCheck_Click(object sender, EventArgs e)
        {
            try
            {
                Settings.SaveSetting("host_" + SelectedPrinter, txtIP.Text);
                Settings.SaveSetting("psw_" + SelectedPrinter, txtPass.Text);
                newVersion = GetPrinterVersion(txtPass.Text, txtIP.Text, SelectedPrinter);
                lblNver.Visible = true; 
                lblAvl.Visible = true;
                lblNver.Text = newVersion;
                if (long.Parse(newVersion) > long.Parse(currentVersion))
                {
                    btnUpdate.Visible = true;
                }
                else
                {
                    lblMsg.Text = "No update available";
                }
            }
            catch
            {
                lblMsg.Text = "Error checking version";
            }
        }

        private void BtnUpdate_Click(object sender, EventArgs e)
        {
            try
            {
                string dbData;
                if (chkFromPrinter.Checked)
                {
                    dbData = GetJsonDB(txtPass.Text, txtIP.Text, SelectedPrinter);
                }
                else
                {
                    dbData = GetJsonDB(SelectedDatabase, "0.4");
                }

                if (dbData != null && !dbData.Equals(string.Empty))
                {
                    JObject materials = JObject.Parse(dbData);
                    JObject result = (JObject)materials["result"];
                    newVersion = result["version"].ToString();
                    if (result != null)
                    {
                        JArray list = (JArray)result["list"];
                        foreach (JToken itemToken in list)
                        {
                            JObject item = (JObject)itemToken;
                            JObject baseObject = (JObject)item["base"];
                            Filament filament = GetMaterialByID(baseObject["id"].Value<string>().Trim());
                            if (filament != null)
                            {
                                filament.FilamentParam = item.ToString();
                                EditMaterial(filament);
                            }
                            else
                            {
                                filament = new Filament
                                {
                                    FilamentVendor = baseObject.GetValue("brand").ToString(),
                                    FilamentName = baseObject.GetValue("name").ToString(),
                                    FilamentId = baseObject.GetValue("id").ToString(),
                                    FilamentType = baseObject.GetValue("meterialType").ToString(),
                                    FilamentParam = item.ToString()
                                };
                                AddMaterial(filament);
                            }
                        }
                    }
                    Task task = Task.Run(() =>
                    {
                        lblMsg.Invoke((MethodInvoker)delegate ()
                        {
                            lblMsg.Text = "Database Updated";
                        });
                        SaveMaterials(SelectedPrinter, newVersion);
                        Thread.Sleep(1000);
                    }).ContinueWith(t =>
                    {
                        this.DialogResult = DialogResult.OK;
                        this.Close();
                    }, TaskScheduler.Default);
                }
                else
                {
                    lblMsg.Text = "Database not found";
                }
            }
            catch
            {
                lblMsg.Text = "Error updating database";
            }
        }

        private void BtnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void SetDescMessage(string msg)
        {
            rtbDesc.Text = msg;
            rtbDesc.BackColor = BackColor;
            if (rtbDesc.Find(" " + SelectedPrinter + " ") != -1)
            {
                rtbDesc.Select(rtbDesc.Find(" " + SelectedPrinter + " "), SelectedPrinter.Length + 2);
                rtbDesc.SelectionColor = ColorTranslator.FromHtml("#1976D2");
            }
            rtbDesc.DeselectAll();
            rtbDesc.SelectionChanged += (sender, e) => {
                lblPrinter.Focus();
            };
            rtbDesc.MouseDown += (sender, e) => {
                if (e.Button == MouseButtons.Right)
                {
                    rtbDesc.ContextMenuStrip = null;
                }
                else
                {
                    lblPrinter.Focus();
                }
            };
        }

        private void printerModel_SelectedIndexChanged(object sender, EventArgs e)
        {
            var selected = printerModel.SelectedItem as dynamic;
            if (selected != null)
            {
                LoadPrinterImage(selected.FullData["thumbnail"].ToString(), pb1);
                lblNver.Visible = true;
                lblAvl.Visible = true;
                apiVersion = selected.FullData["version"].ToString();
                lblNver.Text = apiVersion;
                SelectedDatabase = selected.FullData["name"].ToString();
            }
        }

        private void chkFromPrinter_CheckedChanged(object sender, EventArgs e)
        {
            if (chkFromPrinter.Checked)
            {
                panel1.Visible = false;
                btnUpdate.Visible = false;
                btnCheck.Visible = true;
                lblNver.Visible = false;
                lblAvl.Visible = false;
                lblNver.Text = "";
            }
            else { 
                panel1.Visible= true;
                btnCheck.Visible = false;
                btnUpdate.Visible = true;
                lblNver.Visible = true;
                lblAvl.Visible = true;
                lblNver.Text = apiVersion;
            }
        }
    }
}
