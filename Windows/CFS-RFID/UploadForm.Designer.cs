namespace CFS_RFID
{
    partial class UploadForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(UploadForm));
            this.txtIP = new System.Windows.Forms.TextBox();
            this.txtPass = new System.Windows.Forms.TextBox();
            this.lblIP = new System.Windows.Forms.Label();
            this.lblPass = new System.Windows.Forms.Label();
            this.btnCancel = new System.Windows.Forms.Button();
            this.btnUpload = new System.Windows.Forms.Button();
            this.lblPrinter = new System.Windows.Forms.Label();
            this.rtbDesc = new System.Windows.Forms.RichTextBox();
            this.lblMsg = new System.Windows.Forms.Label();
            this.lblPrevent = new System.Windows.Forms.Label();
            this.lblReboot = new System.Windows.Forms.Label();
            this.lblReset = new System.Windows.Forms.Label();
            this.chkResetApp = new SwitchCheckBox();
            this.chkReset = new SwitchCheckBox();
            this.chkReboot = new SwitchCheckBox();
            this.chkPrevent = new SwitchCheckBox();
            this.SuspendLayout();
            // 
            // txtIP
            // 
            this.txtIP.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtIP.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtIP.Location = new System.Drawing.Point(201, 99);
            this.txtIP.Name = "txtIP";
            this.txtIP.Size = new System.Drawing.Size(184, 30);
            this.txtIP.TabIndex = 0;
            // 
            // txtPass
            // 
            this.txtPass.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtPass.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtPass.Location = new System.Drawing.Point(201, 159);
            this.txtPass.Name = "txtPass";
            this.txtPass.Size = new System.Drawing.Size(184, 30);
            this.txtPass.TabIndex = 1;
            // 
            // lblIP
            // 
            this.lblIP.AutoSize = true;
            this.lblIP.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblIP.Location = new System.Drawing.Point(54, 101);
            this.lblIP.Name = "lblIP";
            this.lblIP.Size = new System.Drawing.Size(114, 25);
            this.lblIP.TabIndex = 2;
            this.lblIP.Text = "Printer IP: ";
            // 
            // lblPass
            // 
            this.lblPass.AutoSize = true;
            this.lblPass.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPass.Location = new System.Drawing.Point(54, 164);
            this.lblPass.Name = "lblPass";
            this.lblPass.Size = new System.Drawing.Size(113, 25);
            this.lblPass.TabIndex = 3;
            this.lblPass.Text = "Password:";
            // 
            // btnCancel
            // 
            this.btnCancel.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnCancel.FlatAppearance.BorderSize = 0;
            this.btnCancel.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnCancel.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnCancel.ForeColor = System.Drawing.Color.White;
            this.btnCancel.Location = new System.Drawing.Point(42, 627);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(126, 47);
            this.btnCancel.TabIndex = 8;
            this.btnCancel.Text = "Cancel";
            this.btnCancel.UseVisualStyleBackColor = false;
            this.btnCancel.Click += new System.EventHandler(this.BtnCancel_Click);
            // 
            // btnUpload
            // 
            this.btnUpload.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnUpload.FlatAppearance.BorderSize = 0;
            this.btnUpload.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnUpload.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnUpload.ForeColor = System.Drawing.Color.White;
            this.btnUpload.Location = new System.Drawing.Point(268, 627);
            this.btnUpload.Name = "btnUpload";
            this.btnUpload.Size = new System.Drawing.Size(126, 47);
            this.btnUpload.TabIndex = 7;
            this.btnUpload.Text = "Upload";
            this.btnUpload.UseVisualStyleBackColor = false;
            this.btnUpload.Click += new System.EventHandler(this.BtnUpload_Click);
            // 
            // lblPrinter
            // 
            this.lblPrinter.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPrinter.Location = new System.Drawing.Point(47, 28);
            this.lblPrinter.Name = "lblPrinter";
            this.lblPrinter.Size = new System.Drawing.Size(347, 43);
            this.lblPrinter.TabIndex = 9;
            this.lblPrinter.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // rtbDesc
            // 
            this.rtbDesc.BackColor = System.Drawing.SystemColors.Control;
            this.rtbDesc.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.rtbDesc.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.rtbDesc.Location = new System.Drawing.Point(42, 385);
            this.rtbDesc.Name = "rtbDesc";
            this.rtbDesc.ReadOnly = true;
            this.rtbDesc.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.None;
            this.rtbDesc.ShortcutsEnabled = false;
            this.rtbDesc.Size = new System.Drawing.Size(352, 137);
            this.rtbDesc.TabIndex = 11;
            this.rtbDesc.TabStop = false;
            this.rtbDesc.Text = "";
            // 
            // lblMsg
            // 
            this.lblMsg.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblMsg.Location = new System.Drawing.Point(42, 537);
            this.lblMsg.Name = "lblMsg";
            this.lblMsg.Size = new System.Drawing.Size(352, 82);
            this.lblMsg.TabIndex = 30;
            this.lblMsg.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // lblPrevent
            // 
            this.lblPrevent.AutoSize = true;
            this.lblPrevent.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPrevent.Location = new System.Drawing.Point(67, 241);
            this.lblPrevent.Name = "lblPrevent";
            this.lblPrevent.Size = new System.Drawing.Size(180, 20);
            this.lblPrevent.TabIndex = 34;
            this.lblPrevent.Text = "Prevent DB updates?";
            // 
            // lblReboot
            // 
            this.lblReboot.AutoSize = true;
            this.lblReboot.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblReboot.Location = new System.Drawing.Point(67, 285);
            this.lblReboot.Name = "lblReboot";
            this.lblReboot.Size = new System.Drawing.Size(135, 20);
            this.lblReboot.TabIndex = 35;
            this.lblReboot.Text = "Reboot printer?";
            // 
            // lblReset
            // 
            this.lblReset.AutoSize = true;
            this.lblReset.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblReset.Location = new System.Drawing.Point(67, 330);
            this.lblReset.Name = "lblReset";
            this.lblReset.Size = new System.Drawing.Size(204, 20);
            this.lblReset.TabIndex = 36;
            this.lblReset.Text = "Reset printer database?";
            // 
            // chkResetApp
            // 
            this.chkResetApp.BorderThickness = 1;
            this.chkResetApp.Location = new System.Drawing.Point(310, 281);
            this.chkResetApp.MinimumSize = new System.Drawing.Size(40, 20);
            this.chkResetApp.Name = "chkResetApp";
            this.chkResetApp.Size = new System.Drawing.Size(60, 31);
            this.chkResetApp.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkResetApp.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkResetApp.TabIndex = 37;
            this.chkResetApp.Text = "switchCheckBox1";
            this.chkResetApp.ThumbColor = System.Drawing.Color.White;
            this.chkResetApp.UseVisualStyleBackColor = true;
            this.chkResetApp.Visible = false;
            // 
            // chkReset
            // 
            this.chkReset.BorderThickness = 1;
            this.chkReset.Location = new System.Drawing.Point(310, 326);
            this.chkReset.MinimumSize = new System.Drawing.Size(40, 20);
            this.chkReset.Name = "chkReset";
            this.chkReset.Size = new System.Drawing.Size(60, 31);
            this.chkReset.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkReset.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkReset.TabIndex = 33;
            this.chkReset.Text = "switchCheckBox3";
            this.chkReset.ThumbColor = System.Drawing.Color.White;
            this.chkReset.UseVisualStyleBackColor = true;
            this.chkReset.CheckStateChanged += new System.EventHandler(this.ChkReset_CheckStateChanged);
            // 
            // chkReboot
            // 
            this.chkReboot.BorderThickness = 1;
            this.chkReboot.Location = new System.Drawing.Point(310, 281);
            this.chkReboot.MinimumSize = new System.Drawing.Size(40, 20);
            this.chkReboot.Name = "chkReboot";
            this.chkReboot.Size = new System.Drawing.Size(60, 31);
            this.chkReboot.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkReboot.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkReboot.TabIndex = 32;
            this.chkReboot.Text = "switchCheckBox2";
            this.chkReboot.ThumbColor = System.Drawing.Color.White;
            this.chkReboot.UseVisualStyleBackColor = true;
            this.chkReboot.CheckStateChanged += new System.EventHandler(this.ChkReboot_CheckStateChanged);
            // 
            // chkPrevent
            // 
            this.chkPrevent.BorderThickness = 1;
            this.chkPrevent.Location = new System.Drawing.Point(310, 237);
            this.chkPrevent.MinimumSize = new System.Drawing.Size(40, 20);
            this.chkPrevent.Name = "chkPrevent";
            this.chkPrevent.Size = new System.Drawing.Size(60, 31);
            this.chkPrevent.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkPrevent.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkPrevent.TabIndex = 31;
            this.chkPrevent.Text = "switchCheckBox1";
            this.chkPrevent.ThumbColor = System.Drawing.Color.White;
            this.chkPrevent.UseVisualStyleBackColor = true;
            this.chkPrevent.CheckStateChanged += new System.EventHandler(this.ChkPrevent_CheckStateChanged);
            // 
            // UploadForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.ClientSize = new System.Drawing.Size(434, 709);
            this.Controls.Add(this.chkResetApp);
            this.Controls.Add(this.lblReset);
            this.Controls.Add(this.lblReboot);
            this.Controls.Add(this.lblPrevent);
            this.Controls.Add(this.chkReset);
            this.Controls.Add(this.chkReboot);
            this.Controls.Add(this.chkPrevent);
            this.Controls.Add(this.lblMsg);
            this.Controls.Add(this.rtbDesc);
            this.Controls.Add(this.lblPrinter);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.btnUpload);
            this.Controls.Add(this.lblPass);
            this.Controls.Add(this.lblIP);
            this.Controls.Add(this.txtPass);
            this.Controls.Add(this.txtIP);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "UploadForm";
            this.ShowInTaskbar = false;
            this.Text = "Upload";
            this.Load += new System.EventHandler(this.UploadForm_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox txtIP;
        private System.Windows.Forms.TextBox txtPass;
        private System.Windows.Forms.Label lblIP;
        private System.Windows.Forms.Label lblPass;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.Button btnUpload;
        private System.Windows.Forms.Label lblPrinter;
        private System.Windows.Forms.RichTextBox rtbDesc;
        private System.Windows.Forms.Label lblMsg;
        private SwitchCheckBox chkPrevent;
        private SwitchCheckBox chkReboot;
        private SwitchCheckBox chkReset;
        private System.Windows.Forms.Label lblPrevent;
        private System.Windows.Forms.Label lblReboot;
        private System.Windows.Forms.Label lblReset;
        private SwitchCheckBox chkResetApp;
    }
}