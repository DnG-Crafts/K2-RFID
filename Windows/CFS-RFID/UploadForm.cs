using CFS_RFID.Properties;
using System;
using System.Drawing;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using static CFS_RFID.Utils;

namespace CFS_RFID
{
    public partial class UploadForm : Form
    {
        public string SelectedPrinter { get; set; }
        private string sshDefault;

        public UploadForm()
        {
            InitializeComponent();
        }

        private void UploadForm_Load(object sender, EventArgs e)
        {
            btnCancel.BackColor = ColorTranslator.FromHtml("#1976D2");
            btnUpload.BackColor = ColorTranslator.FromHtml("#1976D2");
            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            lblPrinter.ForeColor = ColorTranslator.FromHtml("#1976D2");
            btnUpload.FlatAppearance.BorderSize = 0;
            btnCancel.FlatAppearance.BorderSize = 0;

            if (SelectedPrinter == null)
            {
                this.DialogResult = DialogResult.No;
                this.Close();
                return;
            }


            lblPrinter.Text = "Update " + SelectedPrinter + " Database";

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
            chkPrevent.Checked = Settings.GetSetting("prevent_" + SelectedPrinter, true);
            chkReboot.Checked = Settings.GetSetting("reboot_" + SelectedPrinter, true);
            SetDescMessage(string.Format(Resources.updateDesc, SelectedPrinter));
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
            if (rtbDesc.Find(" Update ") != -1)
            {
                rtbDesc.Select(rtbDesc.Find(" Update "), 8);
                rtbDesc.SelectionColor = ColorTranslator.FromHtml("#CD5C5C");
            }
            if (rtbDesc.Find(" Reset ") != -1)
            {
                rtbDesc.Select(rtbDesc.Find(" Reset "), 7);
                rtbDesc.SelectionColor = ColorTranslator.FromHtml("#CD5C5C");
            }
            rtbDesc.DeselectAll();
            rtbDesc.SelectionChanged += (sender, e) =>{
                lblPrinter.Focus();
            };
            rtbDesc.MouseDown += (sender, e) =>{
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

        private void ChkPrevent_CheckStateChanged(object sender, EventArgs e)
        {
           Settings.SaveSetting("prevent_" + SelectedPrinter, chkPrevent.Checked);
        }

        private void ChkReboot_CheckStateChanged(object sender, EventArgs e)
        {
            Settings.SaveSetting("reboot_" + SelectedPrinter, chkReboot.Checked);
        }

        private void ChkReset_CheckStateChanged(object sender, EventArgs e)
        {
            if (chkReset.Checked)
            {
                lblPrevent.Visible = false;
                lblReboot.Text = "Reset app database?";
                chkReboot.Visible = false;
                chkResetApp.Visible = true;
                chkPrevent.Visible = false;
                lblPrinter.Text = "Reset " + SelectedPrinter + " Database";
                SetDescMessage(string.Format(Resources.resetDesc, SelectedPrinter));
            }
            else
            {
                lblPrevent.Visible = true;
                lblReboot.Text = "Reboot printer?";
                chkReboot.Visible = true;
                chkResetApp.Visible = false;
                chkPrevent.Visible = true;
                lblPrinter.Text = "Update " + SelectedPrinter + " Database";
                SetDescMessage(string.Format(Resources.updateDesc, SelectedPrinter));
            }
        }

        private void BtnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void BtnUpload_Click(object sender, EventArgs e)
        {
            Settings.SaveSetting("host_" + SelectedPrinter, txtIP.Text);
            Settings.SaveSetting("psw_" + SelectedPrinter, txtPass.Text);
            if (!txtIP.Text.Equals(string.Empty) && !txtPass.Text.Equals(string.Empty))
            {
                try
                {
                    if (chkReset.Checked)
                    {
                        lblMsg.Text = "Resetting...";
                        ResetJsonDB(txtPass.Text, txtIP.Text, SelectedPrinter, chkResetApp.Checked);
                    }
                    else
                    {
                        lblMsg.Text = "Uploading...";
                        if (chkPrevent.Checked)
                        {
                            SetDatabaseVersion(SelectedPrinter, Resources.verPrevent);
                        }
                        else
                        {
                            string version = GetPrinterVersion(txtPass.Text, txtIP.Text, SelectedPrinter);
                            SetDatabaseVersion(SelectedPrinter, version);
                        }
                        SetJsonDB(txtPass.Text, txtIP.Text, SelectedPrinter);
                        if (SelectedPrinter.Equals("k1", StringComparison.OrdinalIgnoreCase))
                        {
                            SaveMatOption(txtPass.Text, txtIP.Text, SelectedPrinter, chkReboot.Checked);
                        }
                        else if (chkReboot.Checked)
                        {
                            SendSShCommand(txtPass.Text, txtIP.Text, "reboot");
                        }
                    }

                    string endMsg;
                    if (chkReset.Checked)
                    {
                        endMsg = "Reset complete\nRebooting printer";
                    }
                    else
                    {
                        if (chkReboot.Checked)
                        {
                            endMsg = "Upload complete\nRebooting printer";
                        }
                        else {
                            endMsg = "Upload complete";
                        }
                    }
                    Task task = Task.Run(() =>
                    {
                        lblMsg.Invoke((MethodInvoker)delegate ()
                        {
                            lblMsg.Text = endMsg;
                        });
                        Thread.Sleep(2000);
                    }).ContinueWith(t =>
                    {
                        this.DialogResult = DialogResult.OK;
                        this.Close();
                    }, TaskScheduler.Default);
                }
                catch (Exception ex) {
                    lblMsg.Text = string.Empty;
                    Toast.Show(this, ex.Message, Toast.LENGTH_LONG, true);
                }
            }
            else
            {
                Toast.Show(this, "Printer IP and Password cannot be blank", Toast.LENGTH_LONG, true);
            }
        }
    }
}
