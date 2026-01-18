using Newtonsoft.Json.Linq;
using System;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static CFS_RFID.Utils;

namespace CFS_RFID
{
    public partial class ManageForm : Form
    {
        public ManageForm()
        {
            InitializeComponent();
        }

        private async void AddForm_Load(object sender, EventArgs e)
        {
            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            btnAdd.BackColor = ColorTranslator.FromHtml("#1976D2");
            btnCancel.BackColor = ColorTranslator.FromHtml("#1976D2");
            printerModel.Enabled = false; 
            try
            {
                JArray printers = await Task.Run(() =>
                    Utils.FindPrinters(Utils.printerTypes, "0.4")
                );
                var items = printers.Cast<JObject>().Select(p => new {
                    DisplayName = p["name"]?.ToString(),
                    FullData = p
                }).ToList();
                printerModel.DataSource = items;
                printerModel.DisplayMember = "DisplayName";

                if (printerModel.Items.Count > 0) printerModel.SelectedIndex = 0;
            }
            catch
            {
                Toast.Show(this, "Error finding printers", Toast.LENGTH_LONG, true);
            }
            finally
            {
                printerModel.Enabled = true; 
            }
        }

        private void BtnAdd_Click(object sender, EventArgs e)
        {
            var selected = printerModel.SelectedItem as dynamic;
            if (selected != null)
            {
                string printerName = selected.FullData["name"].ToString();
                if (btnAdd.Text.ToLower().Equals("add"))
                {
                    SetDBfile(printerName + ".json", Encoding.UTF8.GetBytes(GetJsonDB(printerName, "0.4")));
                    this.DialogResult = DialogResult.OK;
                }
                else
                {
                    string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + printerName + ".json";
                    if (File.Exists(filePath))
                    {
                        File.Delete(filePath);
                        btnAdd.Text = "Add";
                        this.DialogResult = DialogResult.No;
                    }
                }
                this.Close();
            }
        }

        private void BtnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void PrinterModel_SelectedIndexChanged(object sender, EventArgs e)
        {
            var selected = printerModel.SelectedItem as dynamic;
            if (selected != null)
            {
                LoadPrinterImage(selected.FullData["thumbnail"].ToString(), pb1);
                string printerName = selected.FullData["name"].ToString();
                string filePath = AppDomain.CurrentDomain.BaseDirectory + "\\material_database\\" + printerName + ".json";
                if (File.Exists(filePath))
                {
                    btnAdd.Text = "Delete";
                }
                else {
                    btnAdd.Text = "Add";
                }
            }
        }
    }
}
