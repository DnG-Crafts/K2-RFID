using System;
using System.Drawing;
using System.Windows.Forms;

namespace CFS_RFID
{
    public partial class SettingsForm : Form
    {

        public SettingsForm()
        {
            InitializeComponent();
        }

        private void BtnClose_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void SettingsForm_Load(object sender, EventArgs e)
        {
            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            btnClose.BackColor = ColorTranslator.FromHtml("#1976D2");
            chkAutoRead.Checked = Settings.GetSetting("AutoRead", false);
            chkAutoWrite.Checked = Settings.GetSetting("AutoWrite", false);
            chkEnableSm.Checked = Settings.GetSetting("EnableSm", false);
            txtHost.Text = Settings.GetSetting("SmHost", String.Empty); ;
            txtPort.Text = Settings.GetSetting("SmPort", 7912).ToString();
            txtHost.Enabled = chkEnableSm.Checked;
            txtPort.Enabled = chkEnableSm.Checked;
            chkAutoRead.CheckedChanged += ChkAutoRead_CheckedChanged;
            chkAutoWrite.CheckedChanged += ChkAutoWrite_CheckedChanged;
            chkEnableSm.CheckedChanged += ChkEnableSm_CheckedChanged;
        }

        private void ChkAutoRead_CheckedChanged(object sender, EventArgs e)
        {
            Settings.SaveSetting("AutoRead", chkAutoRead.Checked);
            if (chkAutoRead.Checked)
            {
                chkAutoWrite.Checked = false;
            }
        }

        private void ChkAutoWrite_CheckedChanged(object sender, EventArgs e)
        {
            Settings.SaveSetting("AutoWrite", chkAutoWrite.Checked);
            if (chkAutoWrite.Checked)
            {
                chkAutoRead.Checked = false;
            }
        }

        private void ChkEnableSm_CheckedChanged(object sender, EventArgs e)
        {
            Settings.SaveSetting("EnableSm", chkEnableSm.Checked);
            txtHost.Enabled = chkEnableSm.Checked;
            txtPort.Enabled = chkEnableSm.Checked;
        }

        private void SettingsForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            Settings.SaveSetting("SmHost", txtHost.Text);
            Settings.SaveSetting("SmPort", txtPort.Text);
        }

    }
}
