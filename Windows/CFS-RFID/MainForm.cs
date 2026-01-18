using PCSC;
using PCSC.Monitoring;
using System;
using System.Drawing;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using static CFS_RFID.Utils;

namespace CFS_RFID
{
    public partial class MainForm : Form
    {
        private ISCardContext context = null;
        private Monitor monitor;
        private ICardReader icReader;
        private Reader reader;
        byte[] encKey;
        private string DbVersion = "0", FwVersion;
        private string MaterialColor, PrinterType, MaterialName, MaterialID, MaterialWeight;
        private readonly System.Windows.Forms.ToolTip toolTip = new System.Windows.Forms.ToolTip(), balloonTip = new System.Windows.Forms.ToolTip();
        private TagMemoryForm tagMemoryForm = null;
        bool sidebarExpand = true;
        const int MaxWidth = 200;
        const int MinWidth = 0;

        public MainForm()
        {
            InitializeComponent();
            sidebarPanel.Width = MinWidth;
            sidebarPanel.Visible = false;
            sidebarExpand = false;
            SetupSidebarUI();
        }

        private void MainForm_Load(object sender, EventArgs e)
        {

            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            flowMenu.BackColor = ColorTranslator.FromHtml("#ffffff");
            sidebarPanel.BackColor = ColorTranslator.FromHtml("#ffffff");
            btnRead.BackColor = ColorTranslator.FromHtml("#1976D2");
            btnWrite.BackColor = ColorTranslator.FromHtml("#1976D2");
            lblAdd.ForeColor = ColorTranslator.FromHtml("#1976D2");

            materialName.Text = "PLA";
            materialWeight.Text = "1 KG";
            MaterialColor = "0000FF";
            btnColor.BackColor = ColorTranslator.FromHtml("#" + MaterialColor);
            btnMenu.ForeColor = BackColor;
            btnEdit.ForeColor = BackColor;
            btnDel.ForeColor = BackColor;
            btnAdd.ForeColor = BackColor;

            btnRead.FlatAppearance.BorderSize = 0;
            btnWrite.FlatAppearance.BorderSize = 0;
            btnColor.FlatAppearance.BorderSize = 0;
            btnDel.FlatAppearance.BorderSize = 0;
            btnEdit.FlatAppearance.BorderSize = 0;
            btnAdd.FlatAppearance.BorderSize = 0;
            btnMenu.FlatAppearance.BorderSize = 0;

            toolTip.SetToolTip(btnMenu, "Show Menu");
            toolTip.SetToolTip(btnDel, "Delete selected filament");
            toolTip.SetToolTip(btnEdit, "Edit selected filament");
            toolTip.SetToolTip(btnAdd, "Add a new filament");
            balloonTip.IsBalloon = true;

            printerModel.Items.AddRange(GetPrinterTypes());
            if (printerModel.Items.Count > 0)
            {
                try
                {
                    printerModel.SelectedIndex = Settings.GetSetting("printerType", 0);
                }
                catch
                {
                    printerModel.SelectedIndex = 0;
                }
            }
            else {
                Toast.Show(this,"Add a printer to get started", Toast.LENGTH_LONG, true);
                Timer delayTimer = new Timer();
                delayTimer.Interval = 1000;
                delayTimer.Tick += (s, ev) =>
                {
                    delayTimer.Stop();
                    OpenManage();
                };
                delayTimer.Start();
            }
                ConnectReader();
        }


        private void SetupSidebarUI()
        {
            flowMenu.Dock = DockStyle.Fill;
            flowMenu.AutoScroll = false; 
            AddMenuItem("nav_close", String.Empty, Properties.Resources.menu); 
            AddHeader("App");
            AddMenuItem("nav_settings", "Settings", Properties.Resources.settings);
            AddHeader("Printer Database");
            AddMenuItem("nav_manage", "Manage Printers", Properties.Resources.manage);
            AddMenuItem("nav_upload", "Upload Database", Properties.Resources.upload);
            AddMenuItem("nav_download", "Download Database", Properties.Resources.download);
            AddHeader("RFID Functions");
            AddMenuItem("nav_format", "Format Tag", Properties.Resources.format);
            AddMenuItem("nav_memory", "Read Tag Memory", Properties.Resources.memory);
        }

        private void AddHeader(string text)
        {
            Label lbl = new Label();
            lbl.Text = text.ToUpper();
            lbl.ForeColor = Color.Gray;
            lbl.Font = new Font("Calibri", 7, FontStyle.Bold);
            lbl.Size = new Size(MaxWidth, 30);
            lbl.TextAlign = ContentAlignment.BottomLeft;
            lbl.Padding = new Padding(10, 0, 0, 5);
            flowMenu.Controls.Add(lbl);
        }

        private void AddMenuItem(string id, string text, System.Drawing.Image icon)
        {
            Button btn = new Button();
            btn.Name = id;
            btn.Text = "    " + text;
            btn.Size = new Size(MaxWidth, 35);
            btn.FlatStyle = FlatStyle.Flat;
            btn.FlatAppearance.BorderSize = 0;
            btn.ForeColor = Color.Black;
            btn.Font = new Font("Microsoft Sans Serif", 9);
            btn.TextAlign = ContentAlignment.MiddleLeft;
            btn.ImageAlign = ContentAlignment.MiddleLeft;
            btn.Image = icon;
            btn.Padding = new Padding(10, 0, 0, 0);
            btn.TextImageRelation = TextImageRelation.ImageBeforeText;
            btn.Click += MenuItem_Click;
            flowMenu.Controls.Add(btn);
        }



        private void MenuItem_Click(object sender, EventArgs e)
        {
            Button clickedButton = (Button)sender;
            string menuId = clickedButton.Name;
            if (sidebarExpand) sidebarTimer.Start();
            switch (menuId)
            {
                case "nav_settings":
                    OpenSettings();
                    break;
                case "nav_upload":
                    OpenUpload();
                    break;
                case "nav_manage":
                    OpenManage();
                    break;
                case "nav_download":
                    OpenUpdate();
                    break;
                case "nav_format":
                    OpenFormat();
                    break;
                case "nav_memory":
                    OpenTagMemory();
                    break;

                default:
                    break;
            }
        }

        private void sidebarTimer_Tick(object sender, EventArgs e)
        {
            if (sidebarExpand)
            {
                sidebarPanel.Width -= 20;
                if (sidebarPanel.Width <= MinWidth)
                {
                    sidebarPanel.Width = MinWidth;
                    sidebarPanel.Visible = false;
                    sidebarExpand = false;
                    sidebarTimer.Stop();
                }
            }
            else
            {
                sidebarPanel.Width += 20;
                if (sidebarPanel.Width >= MaxWidth)
                {
                    sidebarPanel.Width = MaxWidth;
                    sidebarExpand = true;
                    sidebarTimer.Stop();
                }
            }
        }



        private void CardInserted(CardStatusEventArgs args)
        {
            try
            {
                icReader = context.ConnectReader(args.ReaderName, SCardShareMode.Shared, SCardProtocol.Any);
                reader = new Reader(icReader);
                byte[] uid = reader.GetData();
                if (uid == null) {
                    Invoke((MethodInvoker)delegate ()
                    {
                        Toast.Show(this, "Tag not compatible", Toast.LENGTH_LONG, true);
                    });
                    icReader.Dispose();
                    reader = null;
                    return;
                }
                if (!reader.LoadAuthenticationKeys(0, 0, KEY_DEFAULT))
                {
                    reader.LoadAuthenticationKeys(32, 0, KEY_DEFAULT);
                }
                encKey = CreateKey(uid);
                if (!reader.LoadAuthenticationKeys(0, 1, encKey))
                {
                    if (!reader.LoadAuthenticationKeys(32, 1, encKey))
                    {
                        Invoke((MethodInvoker)delegate ()
                        {
                            Toast.Show(this, "Failed to load encKey", Toast.LENGTH_SHORT);
                        });
                    }
                }
                Invoke((MethodInvoker)delegate ()
                {
                    lblUid.Text = BitConverter.ToString(uid).Replace("-", " ");
                    lblTagId.Visible = true;
                    if (string.IsNullOrEmpty(FwVersion))
                    {
                        FwVersion = ReaderVersion(reader);
                        Text = string.IsNullOrEmpty(FwVersion) ? "CFS RFID" : FwVersion;
                    }
                    if ((reader.Authentication10byte(7, 96, 0) || reader.Authentication6byte(7, 96, 0)))
                    {
                        balloonTip.Hide(imgEnc);
                        imgEnc.Visible = false;
                        lblUid.Left = lblTagId.Right;
                    }
                    else {
                        imgEnc.Visible = true;
                        balloonTip.SetToolTip(imgEnc, "Key: " + BitConverter.ToString(encKey).Replace("-", String.Empty));
                        lblUid.Left = imgEnc.Right;
                    }

                    if (tagMemoryForm != null && !tagMemoryForm.IsDisposed)
                    {
                        tagMemoryForm.UpdateReader(reader);
                    }

                    if (Settings.GetSetting("AutoRead", false))
                    {
                        ReadSpoolData();
                    }
                    else if (Settings.GetSetting("AutoWrite", false))
                    {
                        WriteSpoolData(MaterialID, MaterialColor, GetMaterialLength(MaterialWeight));
                    }
                });
            }
            catch { }
        }

        private void CardRemoved(CardStatusEventArgs args)
        {
            try
            {
                if (icReader != null)
                {
                    icReader.Dispose();
                    reader = null;
                }
                Invoke((MethodInvoker)delegate ()
                {
                    imgEnc.Visible = false;
                    lblUid.Text = string.Empty;
                    lblTagId.Visible = false;
                });
            }
            catch { }
        }


        private void ConnectReader()
        {
            lblConnect.Visible = true;
            lblConnect.ForeColor = Color.MediumSeaGreen;
            lblConnect.Text = "Connecting...";
            btnRead.Visible = false;
            btnWrite.Visible = false;
            btnColor.Visible = false;
            lblAdd.Visible = false;
            materialWeight.Visible = false;
            lblUid.Visible = false;
            ActiveControl = lblConnect;

            try
            {
                if (context == null)
                {
                    context = ContextFactory.Instance.Establish(SCardScope.System);
                }
                var readers = context.GetReaders();


                if (readers.Length > 0)
                {
                    monitor?.Dispose();
                    monitor = new Monitor(readers);
                    monitor.CardInserted += CardInserted;
                    monitor.CardRemoved += CardRemoved;
                    lblConnect.Visible = false;
                    lblConnect.Text = string.Empty;
                    btnRead.Visible = true;
                    btnWrite.Visible = true;
                    btnColor.Visible = true;
                    materialWeight.Visible = true;
                    lblUid.Visible = true;
                    if (Settings.GetSetting("EnableSm", false)) lblAdd.Visible = true;
                }
                else
                {

                    Toast.Show(this, "Connect Failed", Toast.LENGTH_SHORT);
                    lblConnect.Visible = true;
                    lblConnect.ForeColor = Color.IndianRed;
                    lblConnect.Text = "No Reader Found\nClick here to connect";
                    btnRead.Visible = false;
                    btnWrite.Visible = false;
                    btnColor.Visible = false;
                    lblAdd.Visible = false;
                    materialWeight.Visible = false;
                    lblUid.Visible = false;
                    ActiveControl = lblConnect;

                }
            }
            catch (Exception e)
            {
                lblConnect.Visible = true;
                lblConnect.ForeColor = Color.IndianRed;
                lblConnect.Text = "NFC reader failed";
                btnRead.Visible = false;
                btnWrite.Visible = false;
                btnColor.Visible = false;
                lblAdd.Visible = false;
                materialWeight.Visible = false;
                lblUid.Visible = false;
                ActiveControl = lblConnect;
                Toast.Show(this, e.Message, Toast.LENGTH_LONG, true);
            }
        }

 

        public void ReadSpoolData()
        {
            try
            {
                if (reader == null)
                {
                    Toast.Show(this, "Reader not connected", Toast.LENGTH_SHORT, true);
                }
                else
                {
                    if ((reader.Authentication10byte(7, 96, 0) || reader.Authentication6byte(7, 96, 0)))
                    {
                        Toast.Show(this, "Empty tag", Toast.LENGTH_SHORT);
                    }
                    else
                    {
                        string tagData = ReadTag(reader);
                        if (tagData != null && tagData.Length >= 40)
                        {
                            try
                            {
                                string materialId = tagData.Substring(12, 5);
                                string printerType = tagData.Substring(48).Trim();
                                if (printerModel.FindStringExact(printerType) != -1)
                                {
                                    printerModel.SelectedIndex = printerModel.FindStringExact(printerType);
                                    Application.DoEvents();
                                }
                                string[] materialInfo = GetMaterialName(materialId);
                                if (materialInfo != null && materialInfo.Length >= 2)
                                {
                                    MaterialColor = tagData.Substring(18, 6);
                                    string length = tagData.Substring(24, 4);
                                    btnColor.BackColor = ColorTranslator.FromHtml("#" + MaterialColor);
                                    MaterialName = materialInfo[0];
                                    vendorName.SelectedIndex = vendorName.FindStringExact(materialInfo[1]);
                                    materialName.SelectedIndex = materialName.FindStringExact(MaterialName);
                                    materialWeight.SelectedIndex = materialWeight.FindStringExact(GetMaterialWeight(length));
                                    Toast.Show(this, "Data read from tag", Toast.LENGTH_SHORT);
                                }
                                else
                                {
                                    Toast.Show(this, "Unknown or empty tag", Toast.LENGTH_SHORT);
                                }
                            }
                            catch
                            {
                                Toast.Show(this, "Error reading tag", Toast.LENGTH_SHORT, true);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Toast.Show(this, e.Message, Toast.LENGTH_LONG, true);
            }
        }


        void WriteSpoolData(string MaterialID, string Color, string Length)
        {
            bool encrypted = false;
            string filamentId = "1" + MaterialID;
            string vendorId = "0276";
            string color = "0" + Color;
            string serialNum = "000001";
            string reserve = "00000000000000";
            string tagData = "AB124" + vendorId + "A2" + filamentId + color + Length + serialNum + reserve + printerModel.Text;
            string paddedData = tagData.PadRight(96, ' ');
            byte[] fullDataBytes = Encoding.UTF8.GetBytes(paddedData);
            try
            {
                if (reader == null)
                {
                    Toast.Show(this, "Reader not connected", Toast.LENGTH_SHORT, true);
                    return;
                }

                if (reader.Authentication6byte(4, 96, 1) || reader.Authentication10byte(4, 96, 1))
                {
                    encrypted = true;
                }

                int keyS1 = encrypted ? 1 : 0;
                if (reader.Authentication6byte(4, 96, (byte)keyS1) || reader.Authentication10byte(4, 96, (byte)keyS1))
                {
                    byte[] s1Raw = new byte[48];
                    Array.Copy(fullDataBytes, 0, s1Raw, 0, 48);
                    byte[] s1ToDisk = CipherData(1, s1Raw);
                    for (int i = 0; i < 3; i++)
                    {
                        byte[] blockData = new byte[16];
                        Array.Copy(s1ToDisk, i * 16, blockData, 0, 16);
                        reader.UpdateBinaryBlocks((byte)(4 + i), 16, blockData);
                    }
                    if (!encrypted)
                    {
                        byte[] trailer = reader.ReadBinaryBlocks(7, 16);
                        if (trailer != null)
                        {
                            Array.Copy(encKey, 0, trailer, 0, 6);
                            Array.Copy(encKey, 0, trailer, 10, 6);
                            reader.UpdateBinaryBlocks(7, 16, trailer);
                            encrypted = true;
                            imgEnc.Visible = true;
                            lblUid.Left = imgEnc.Right;
                            balloonTip.SetToolTip(imgEnc, "Key: " + BitConverter.ToString(encKey).Replace("-", ""));
                        }
                    }
                }
                else
                {
                    Toast.Show(this, "Failed to authenticate", Toast.LENGTH_SHORT, true);
                    return;
                }
                if (reader.Authentication6byte(8, 96, 0) || reader.Authentication10byte(8, 96, 0))
                {
                    byte[] s2ToDisk = new byte[48];
                    Array.Copy(fullDataBytes, 48, s2ToDisk, 0, 48);
                    for (int i = 0; i < 3; i++)
                    {
                        byte[] blockData = new byte[16];
                        Array.Copy(s2ToDisk, i * 16, blockData, 0, 16);
                        reader.UpdateBinaryBlocks((byte)(8 + i), 16, blockData);
                    }
                }
                Toast.Show(this, "Data written to tag", Toast.LENGTH_SHORT);
            }
            catch (Exception e)
            {
                Toast.Show(this, e.Message, Toast.LENGTH_LONG, true);
            }
        }


        private void OpenSettings()
        {
            try
            {
                SettingsForm settingsForm = new SettingsForm
                {
                    StartPosition = FormStartPosition.CenterParent
                };
                DialogResult result = settingsForm.ShowDialog();
                if (Settings.GetSetting("EnableSm", false))
                {
                    lblAdd.Visible = true;
                }
                else
                {
                    lblAdd.Visible = false;
                }
                settingsForm.Dispose();
            }
            catch { }
        }


        private void OpenManage()
        {
            try
            {
                ManageForm manageForm = new ManageForm
                {
                    StartPosition = FormStartPosition.CenterParent
                };
                DialogResult result = manageForm.ShowDialog();
                if (result == DialogResult.OK)
                {
                    printerModel.Items.Clear();
                    printerModel.Items.AddRange(GetPrinterTypes());
                    if (printerModel.Items.Count > 0)
                    {
                        printerModel.SelectedIndex = 0;
                    }
                    Toast.Show(this, "Printer added", Toast.LENGTH_SHORT);
                }
                else if (result == DialogResult.No)
                {
                    printerModel.Items.Clear();
                    printerModel.Items.AddRange(GetPrinterTypes());
                    if (printerModel.Items.Count > 0)
                    {
                        printerModel.SelectedIndex = 0;
                    }
                    Toast.Show(this, "Printer removed", Toast.LENGTH_SHORT);
                }
                manageForm.Dispose();
            }
            catch { }
        }


        private void OpenUpdate()
        {
            try
            {
                UpdateForm updateForm = new UpdateForm
                {
                    SelectedPrinter = PrinterType,
                    StartPosition = FormStartPosition.CenterParent
                };
                DialogResult result = updateForm.ShowDialog();
                if (result == DialogResult.OK)
                {
                    vendorName.Items.Clear();
                    materialName.Items.Clear();
                    LoadMaterials(PrinterType);
                    DbVersion = GetDatabaseVersion(PrinterType);
                    vendorName.Items.AddRange(GetMaterialBrands());
                    vendorName.SelectedIndex = 0;
                }
                else if (result == DialogResult.No)
                {
                    Toast.Show(this, "No printer selected", Toast.LENGTH_SHORT);
                }
                updateForm.Dispose();
            }
            catch { }
        }

        private void OpenUpload()
        {
            try
            {
                UploadForm uploadForm = new UploadForm
                {
                    SelectedPrinter = PrinterType,
                    StartPosition = FormStartPosition.CenterParent
                };
                DialogResult result = uploadForm.ShowDialog();
                if (result == DialogResult.OK)
                {
                    vendorName.Items.Clear();
                    materialName.Items.Clear();
                    LoadMaterials(PrinterType);
                    DbVersion = GetDatabaseVersion(PrinterType);
                    vendorName.Items.AddRange(GetMaterialBrands());
                    vendorName.SelectedIndex = 0;
                }
                else if (result == DialogResult.No)
                {
                    Toast.Show(this, "No printer selected", Toast.LENGTH_SHORT);
                }
                uploadForm.Dispose();
            }
            catch { }
        }

        private void OpenFormat()
        {
            try
            {
                DialogResult result = MessageBox.Show(this,
                    "This will erase the tag and set the default MIFARE key",
                    "Format tag",
                    MessageBoxButtons.OKCancel,
                    MessageBoxIcon.Question, MessageBoxDefaultButton.Button2);
                if (result == DialogResult.OK)
                {
                    if (reader == null)
                    {
                        Toast.Show(this, "Error formatting tag", Toast.LENGTH_SHORT, true);
                    }
                    else
                    {
                        FormatTag(reader);
                        imgEnc.Visible = false;
                        lblUid.Left = lblTagId.Right;
                        Toast.Show(this, "Tag formatted", Toast.LENGTH_SHORT);
                    }
                }
            }
            catch (Exception ex)
            {
                Toast.Show(this, ex.Message, Toast.LENGTH_LONG, true);
            }
        }

        private void OpenTagMemory()
        {
            tagMemoryForm = new TagMemoryForm(reader)
            {
                StartPosition = FormStartPosition.CenterParent
            };
            tagMemoryForm.ShowDialog();
        }

        private void BtnRead_Click(object sender, EventArgs e)
        {
            ReadSpoolData();
        }

        private void BtnWrite_Click(object sender, EventArgs e)
        {
            WriteSpoolData(MaterialID, MaterialColor, GetMaterialLength(MaterialWeight));
        }

        private void BtnColor_Click(object sender, EventArgs e)
        {
            ColorDialog dlg = new ColorDialog
            {
                AllowFullOpen = true,
                FullOpen = true,
                AnyColor = true,
                Color = ColorTranslator.FromHtml("#" + MaterialColor)
            };
            if (dlg.ShowDialog() == DialogResult.OK)
            {
                btnColor.BackColor = dlg.Color;
                MaterialColor = (dlg.Color.ToArgb() & 0x00FFFFFF).ToString("X6");
            }
        }

        private void MaterialWeight_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                MaterialWeight = materialWeight.Items[materialWeight.SelectedIndex].ToString();
            }
            catch { }
        }

        private void PrinterModel_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                PrinterType = printerModel.Items[printerModel.SelectedIndex].ToString();
                LoadMaterials(PrinterType);
                DbVersion = GetDatabaseVersion(PrinterType);
                vendorName.Items.Clear();
                materialName.Items.Clear();
                vendorName.Items.AddRange(GetMaterialBrands());
                vendorName.SelectedIndex = 0;
            }
            catch { }
        }

        void AddFilament()
        {
            try
            {
                FilamentForm filamentForm = new FilamentForm
                {
                    SelkectedFilament = MaterialID,
                    IsEdit = false,
                    StartPosition = FormStartPosition.CenterParent
                };
                DialogResult result = filamentForm.ShowDialog();
                if (result == DialogResult.OK)
                {
                    SaveMaterials(PrinterType, DbVersion);
                    vendorName.Items.Clear();
                    materialName.Items.Clear();
                    LoadMaterials(PrinterType);
                    DbVersion = GetDatabaseVersion(PrinterType);
                    vendorName.Items.AddRange(GetMaterialBrands());
                    vendorName.SelectedIndex = 0;

                }
                filamentForm.Dispose();
            }
            catch { }
        }

        void EditFilament()
        {
            try
            {
                FilamentForm filamentForm = new FilamentForm
                {
                    SelkectedFilament = MaterialID,
                    IsEdit = true,
                    StartPosition = FormStartPosition.CenterParent
                };
                DialogResult result = filamentForm.ShowDialog();
                if (result == DialogResult.OK)
                {
                    SaveMaterials(PrinterType, DbVersion);
                    vendorName.Items.Clear();
                    materialName.Items.Clear();
                    LoadMaterials(PrinterType);
                    DbVersion = GetDatabaseVersion(PrinterType);
                    vendorName.Items.AddRange(GetMaterialBrands());
                    vendorName.SelectedIndex = 0;

                }
                filamentForm.Dispose();
            }
            catch { }
        }

        void DeleteFilament()
        {
            try
            {
                DialogResult result = MessageBox.Show(this,
                    "Do you want to Delete?\n\n    "
                    + vendorName.Text + "\n    "
                    + materialName.Text,
                    "Delete Filament",
                    MessageBoxButtons.OKCancel,
                    MessageBoxIcon.Warning, MessageBoxDefaultButton.Button2);
                if (result == DialogResult.OK)
                {
                    RemoveMaterial(MaterialID);
                    SaveMaterials(PrinterType, DbVersion);
                    vendorName.Items.Clear();
                    materialName.Items.Clear();
                    LoadMaterials(PrinterType);
                    DbVersion = GetDatabaseVersion(PrinterType);
                    vendorName.Items.AddRange(GetMaterialBrands());
                    vendorName.SelectedIndex = 0;
                }
            }
            catch { }
        }

        private void BtnDel_Click(object sender, EventArgs e)
        {
            DeleteFilament();
        }

        private void BtnAdd_Click(object sender, EventArgs e)
        {
            AddFilament();
        }

        private void BtnEdit_Click(object sender, EventArgs e)
        {
            EditFilament();
        }

        private void LblConnect_Click(object sender, EventArgs e)
        {
            ConnectReader();
        }

        private void BtnDel_MouseLeave(object sender, EventArgs e)
        {
            toolTip.Hide(btnDel);
        }

        private void BtnEdit_MouseLeave(object sender, EventArgs e)
        {
            toolTip.Hide(btnEdit);
        }

        private void ImgEnc_MouseLeave(object sender, EventArgs e)
        {
            balloonTip.Hide(imgEnc);
        }

        private void MainForm_LocationChanged(object sender, EventArgs e)
        {
            if (Toast.currentToastInstance != null && !Toast.currentToastInstance.IsDisposed)
            {
                Toast.currentToastInstance.UpdatePosition(this);
            }
        }

        private void MainForm_Click(object sender, EventArgs e)
        {
            if (sidebarExpand) sidebarTimer.Start();
        }

        private void ImgEnc_Click(object sender, EventArgs e)
        {
            try
            {
                Clipboard.Clear();
                Clipboard.SetText("UID: " + lblUid.Text + "\r\nKey: " + BitConverter.ToString(encKey).Replace("-", String.Empty));
                Toast.Show(this, "Key copied to clipboard", Toast.LENGTH_SHORT);
            }
            catch { }
        }

        private void BtnMenu_Click(object sender, EventArgs e)
        {
            if (sidebarTimer.Enabled) return;
            if (!sidebarExpand)
            {
                sidebarPanel.Visible = true;
            }
            sidebarTimer.Start();
        }

        private void MainForm_MouseMove(object sender, MouseEventArgs e)
        {
            if (sidebarExpand) sidebarTimer.Start();
        }

        private void BtnMenu_MouseLeave(object sender, EventArgs e)
        {
            toolTip.Hide(btnMenu);
        }

        private void LblAdd_Click(object sender, EventArgs e)
        {
            using (SmDialog dialog = new SmDialog(vendorName.Text, materialName.Text, MaterialColor, GetMaterialIntWeight(MaterialWeight)))
            {
                if (dialog.ShowDialog() == DialogResult.OK)
                {
                    string pType = printerModel.Text;
                    string colorName = dialog.ColorNameResult;
                    if (colorName == String.Empty)
                    {
                        colorName = MaterialColor;
                    }
                    Toast.Show(this, "Adding spool", Toast.LENGTH_SHORT);
                    Task.Run(() => {
                        return SmAddSpool(Settings.GetSetting("SmHost", String.Empty), Settings.GetSetting("SmPort", 7912), MaterialID, MaterialColor, colorName, GetMaterialIntWeight(MaterialWeight), pType);
                    }).ContinueWith(t => {
                        this.Invoke((MethodInvoker)delegate {
                            Toast.Show(this, t.Result, Toast.LENGTH_SHORT, t.Result.ToLower().StartsWith("error"));
                        });
                    });
                }
            }


        }

        private void BtnAdd_MouseLeave(object sender, EventArgs e)
        {
            toolTip.Hide(btnAdd);
        }

        private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            try { Environment.Exit(0); } catch { }
        }

        private void PrinterModel_SelectionChangeCommitted(object sender, EventArgs e)
        {
            Settings.SaveSetting("printerType", printerModel.SelectedIndex);
        }

        private void VendorName_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                materialName.Items.Clear();
                materialName.Items.AddRange(GetMaterialsByBrand(vendorName.Text));
                materialName.SelectedIndex = 0;
            }
            catch { }
        }

        private void MaterialName_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                MaterialID = GetMaterialID(vendorName.Text, materialName.Items[materialName.SelectedIndex].ToString());
            }
            catch { }
        }

    }

}
