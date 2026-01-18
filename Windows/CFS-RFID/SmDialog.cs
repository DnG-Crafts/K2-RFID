using System;
using System.Drawing;
using System.Windows.Forms;

namespace CFS_RFID
{
    public partial class SmDialog : Form
    {
        [System.Runtime.InteropServices.DllImport("user32.dll", CharSet = System.Runtime.InteropServices.CharSet.Auto)]
        private static extern Int32 SendMessage(IntPtr hWnd, int msg, int wParam, [System.Runtime.InteropServices.MarshalAs(System.Runtime.InteropServices.UnmanagedType.LPWStr)] string lParam);
        private const int EM_SETCUEBANNER = 0x1501;
        private readonly TextBox TxtColorName;
        public string ColorNameResult { get; private set; }

        public SmDialog(string vendor, string type, string colorHex, int weight)
        {
            this.Text = "Add Spool to Spoolman";
            this.FormBorderStyle = FormBorderStyle.FixedDialog;
            this.Width = 280;
            this.Height = 295;
            this.StartPosition = FormStartPosition.CenterParent;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            BackColor = ColorTranslator.FromHtml("#F4F4F4");
            AddLabel("Vendor:", vendor, 20);
            AddLabel("Type:", type, 45);
            AddLabel("Color:", colorHex, 70);
            AddLabel("Weight:", weight + "g", 95);
            Panel colorBox = new Panel()
            {
                Left = 140,
                Top = 70,
                Width = 32,
                Height = 16,
                BorderStyle = BorderStyle.FixedSingle
            };
            try { colorBox.BackColor = ColorTranslator.FromHtml("#" + colorHex); }
            catch { colorBox.BackColor = Color.Gray; }
            this.Controls.Add(colorBox);
            Label lblInput = new Label() { 
                Text = "Enter Color Name:", 
                Left = 20, 
                Top = 145, 
                Width = 250, 
                Height = 16
                
            };
            this.Controls.Add(lblInput);
            TxtColorName = new TextBox() { 
                Left = 20, 
                Top = 165, 
                Width = 220, 
                Height = 30,
                TextAlign = HorizontalAlignment.Center,
                BorderStyle = BorderStyle.FixedSingle
            };
            this.Controls.Add(TxtColorName);
            ColorMatcher matcher = new ColorMatcher();
            string matchedColor = matcher.FindNearestColor(colorHex);
            if (TxtColorName.IsHandleCreated)
            {
                SendMessage(TxtColorName.Handle, EM_SETCUEBANNER, 1, matchedColor);
            }
            else
            {
                TxtColorName.HandleCreated += (s, e) => {
                    SendMessage(TxtColorName.Handle, EM_SETCUEBANNER, 1, matchedColor);
                };
            }
            Button btnOk = new Button()
            {
                Text = "Submit",
                Left = 165,
                Top = 210,
                Width = 75,
                Height = 30,
                AutoSize = false,
                Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0))),
                FlatStyle = FlatStyle.Flat,
                FlatAppearance = { BorderSize = 0 },
                ForeColor = Color.White,
                UseVisualStyleBackColor = false,
                BackColor = ColorTranslator.FromHtml("#1976D2"),
                DialogResult = DialogResult.OK
            };
            btnOk.Click += (s, e) => {
                if (TxtColorName.Text == "")
                {
                    TxtColorName.Text = matchedColor;
                }
                this.ColorNameResult = TxtColorName.Text; 
            };
            this.Controls.Add(btnOk);
            Button btnCancel = new Button()
            {
                Text = "Cancel",
                Left = 70,
                Top = 210,
                Width = 75,
                Height = 30,
                AutoSize = false,
                Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0))),
                FlatStyle = FlatStyle.Flat,
                FlatAppearance = { BorderSize = 0 },
                ForeColor = Color.White,
                BackColor = ColorTranslator.FromHtml("#1976D2"),
                DialogResult = DialogResult.Cancel 
            };
            this.Controls.Add(btnCancel);
            btnOk.NotifyDefault(false);
        }

        private void AddLabel(string header, string value, int top)
        {
            Label lblHeader = new Label()
            {
                Text = header,
                Left = 20,
                Top = top,
                AutoSize = true,
                Font = new Font(this.Font, FontStyle.Bold)
            };
            this.Controls.Add(lblHeader);
            Label lblValue = new Label()
            {
                Text = value,
                Left = 90, 
                Top = top,
                AutoSize = true,
                Font = new Font(this.Font, FontStyle.Regular)
            };
            this.Controls.Add(lblValue);
        }

    }
}
