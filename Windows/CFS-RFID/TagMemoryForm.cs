using System;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Drawing;

namespace CFS_RFID
{
    public partial class TagMemoryForm : Form
    {
        private Reader reader;

        public TagMemoryForm(Reader reader)
        {
            InitializeComponent();
            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            btnRead.BackColor = ColorTranslator.FromHtml("#1976D2");
            btnClose.BackColor = ColorTranslator.FromHtml("#1976D2");
            flowLayoutPanel1.AutoScroll = false;
            flowLayoutPanel1.VerticalScroll.Visible = true;
            flowLayoutPanel1.VerticalScroll.Enabled = true;
            flowLayoutPanel1.AutoScroll = true;
            this.reader = reader;
            this.Load += (s, e) => ReadTagMemory();
        }

        public void UpdateReader(Reader reader)
        {
            this.reader = reader;
        }

        private void ReadTagMemory()
        {
            bool encrypted = false;
            if (this.reader == null)
            {
                Toast.Show(this, "Error reading tag", Toast.LENGTH_SHORT, true);
                return;
            }
            this.Invoke((MethodInvoker)delegate
            {
                flowLayoutPanel1.Controls.Clear();
            });
            Task.Run(() =>
            {
                try
                {
                    int sectorCount = 16;
                    if (this.reader.Authentication6byte(4, 96, 1) || this.reader.Authentication10byte(4, 96, 1))
                    {
                        encrypted = true;
                    }
                    for (int s = 0; s < sectorCount; s++)
                    {
                        int firstBlock = s * 4;
                        bool auth;
                        if (s == 1 && encrypted)
                        {
                            auth = this.reader.Authentication6byte((byte)firstBlock, 96, 1);
                        }
                        else
                        {
                            auth = this.reader.Authentication6byte((byte)firstBlock, 96, 0);
                        }

                        if (auth)
                        {
                            for (int b = 0; b < 4; b++)
                            {
                                int currentBlock = firstBlock + b;
                                if (this.IsDisposed) return;
                                byte[] data = reader.ReadBinaryBlocks((byte)currentBlock, 16);
                                if (data != null)
                                {
                                    string hexString = BitConverter.ToString(data).Replace("-", " ").Trim();
                                    string definition = GetMifareBlockDefinition(s, b, 4).Trim();
                                    string blockTitle = $"Block {currentBlock} | {definition}".Trim();
                                    Image icon = GetIconForBlock(s, b);
                                    this.Invoke((MethodInvoker)delegate
                                    {
                                        flowLayoutPanel1.SuspendLayout();
                                        TagBlockCard card = new TagBlockCard();
                                        card.SetData(blockTitle, hexString, icon);
                                        card.Height = 60;
                                        card.Width = flowLayoutPanel1.DisplayRectangle.Width - 10;
                                        card.Margin = new Padding(5, 2, 5, 2);
                                        flowLayoutPanel1.Controls.Add(card);
                                        flowLayoutPanel1.ResumeLayout();
                                    });
                                }
                            }
                        }
                        else
                        {
                            this.Invoke((MethodInvoker)delegate
                            {
                                TagBlockCard errorCard = new TagBlockCard();
                                errorCard.SetData($"Sector {s} | FAILED AUTHENTICATION", "Key Required", Properties.Resources.failed);
                                errorCard.Height = 60;
                                errorCard.Width = flowLayoutPanel1.DisplayRectangle.Width - 10;
                                errorCard.Margin = new Padding(5, 2, 5, 2);
                                flowLayoutPanel1.Controls.Add(errorCard);
                            });
                        }
                    }
                    this.Invoke((MethodInvoker)delegate {
                        foreach (Control c in flowLayoutPanel1.Controls)
                        {
                            c.Width = flowLayoutPanel1.ClientSize.Width - 10;
                        }
                    });
                }
                catch
                {
                    this.Invoke((MethodInvoker)delegate
                    {
                        Toast.Show(this, "Error reading tag", Toast.LENGTH_SHORT, true);
                    });
                }
            });
        }

        private Image GetIconForBlock(int sector, int blockInSector)
        {
            if (sector == 0 && blockInSector == 0)
            {
                return Properties.Resources.locked;
            }
            if (blockInSector == 3)
            {
                return Properties.Resources._internal;
            }
            return Properties.Resources.writable;
        }

        public string GetMifareBlockDefinition(int sector, int blockInSector, int totalBlocks)
        {
            if (sector == 0 && blockInSector == 0) return "MANUFACTURER (UID)";
            if (blockInSector == totalBlocks - 1) return "Keys A/B + Access Bits";
            return "USER DATA";
        }

        private void BtnRead_Click(object sender, EventArgs e)
        {
            ReadTagMemory();
        }

        private void BtnClose_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }
    }
}
