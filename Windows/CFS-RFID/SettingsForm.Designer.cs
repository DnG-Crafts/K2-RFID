namespace CFS_RFID
{
    partial class SettingsForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SettingsForm));
            this.lblAutoWrite = new System.Windows.Forms.Label();
            this.chkAutoWrite = new SwitchCheckBox();
            this.lblAutoRead = new System.Windows.Forms.Label();
            this.chkAutoRead = new SwitchCheckBox();
            this.chkEnableSm = new SwitchCheckBox();
            this.lblEnableSm = new System.Windows.Forms.Label();
            this.txtHost = new System.Windows.Forms.TextBox();
            this.txtPort = new System.Windows.Forms.TextBox();
            this.lblHost = new System.Windows.Forms.Label();
            this.lblPort = new System.Windows.Forms.Label();
            this.btnClose = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // lblAutoWrite
            // 
            this.lblAutoWrite.AutoSize = true;
            this.lblAutoWrite.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAutoWrite.Location = new System.Drawing.Point(81, 88);
            this.lblAutoWrite.Name = "lblAutoWrite";
            this.lblAutoWrite.Size = new System.Drawing.Size(199, 20);
            this.lblAutoWrite.TabIndex = 48;
            this.lblAutoWrite.Text = "Auto write tag on scan?";
            // 
            // chkAutoWrite
            // 
            this.chkAutoWrite.BorderThickness = 1;
            this.chkAutoWrite.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.chkAutoWrite.Location = new System.Drawing.Point(313, 83);
            this.chkAutoWrite.MaximumSize = new System.Drawing.Size(100, 50);
            this.chkAutoWrite.MinimumSize = new System.Drawing.Size(30, 15);
            this.chkAutoWrite.Name = "chkAutoWrite";
            this.chkAutoWrite.Size = new System.Drawing.Size(60, 31);
            this.chkAutoWrite.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkAutoWrite.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkAutoWrite.TabIndex = 47;
            this.chkAutoWrite.Text = "Auto read on tag scan?";
            this.chkAutoWrite.ThumbColor = System.Drawing.Color.White;
            this.chkAutoWrite.UseVisualStyleBackColor = true;
            // 
            // lblAutoRead
            // 
            this.lblAutoRead.AutoSize = true;
            this.lblAutoRead.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAutoRead.Location = new System.Drawing.Point(81, 37);
            this.lblAutoRead.Name = "lblAutoRead";
            this.lblAutoRead.Size = new System.Drawing.Size(197, 20);
            this.lblAutoRead.TabIndex = 46;
            this.lblAutoRead.Text = "Auto read tag on scan?";
            // 
            // chkAutoRead
            // 
            this.chkAutoRead.BorderThickness = 1;
            this.chkAutoRead.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.chkAutoRead.Location = new System.Drawing.Point(313, 32);
            this.chkAutoRead.MaximumSize = new System.Drawing.Size(100, 50);
            this.chkAutoRead.MinimumSize = new System.Drawing.Size(30, 15);
            this.chkAutoRead.Name = "chkAutoRead";
            this.chkAutoRead.Size = new System.Drawing.Size(60, 31);
            this.chkAutoRead.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkAutoRead.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkAutoRead.TabIndex = 45;
            this.chkAutoRead.Text = "Auto read on tag scan?";
            this.chkAutoRead.ThumbColor = System.Drawing.Color.White;
            this.chkAutoRead.UseVisualStyleBackColor = true;
            // 
            // chkEnableSm
            // 
            this.chkEnableSm.BorderThickness = 1;
            this.chkEnableSm.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.chkEnableSm.Location = new System.Drawing.Point(313, 203);
            this.chkEnableSm.MaximumSize = new System.Drawing.Size(100, 50);
            this.chkEnableSm.MinimumSize = new System.Drawing.Size(30, 15);
            this.chkEnableSm.Name = "chkEnableSm";
            this.chkEnableSm.Size = new System.Drawing.Size(60, 31);
            this.chkEnableSm.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkEnableSm.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkEnableSm.TabIndex = 49;
            this.chkEnableSm.Text = "Auto read on tag scan?";
            this.chkEnableSm.ThumbColor = System.Drawing.Color.White;
            this.chkEnableSm.UseVisualStyleBackColor = true;
            // 
            // lblEnableSm
            // 
            this.lblEnableSm.AutoSize = true;
            this.lblEnableSm.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblEnableSm.Location = new System.Drawing.Point(29, 208);
            this.lblEnableSm.Name = "lblEnableSm";
            this.lblEnableSm.Size = new System.Drawing.Size(249, 20);
            this.lblEnableSm.TabIndex = 50;
            this.lblEnableSm.Text = "Enable spoolman functionality";
            // 
            // txtHost
            // 
            this.txtHost.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtHost.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtHost.Location = new System.Drawing.Point(189, 274);
            this.txtHost.MaxLength = 255;
            this.txtHost.Name = "txtHost";
            this.txtHost.Size = new System.Drawing.Size(184, 30);
            this.txtHost.TabIndex = 51;
            this.txtHost.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.txtHost.WordWrap = false;
            // 
            // txtPort
            // 
            this.txtPort.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtPort.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtPort.Location = new System.Drawing.Point(189, 320);
            this.txtPort.MaxLength = 5;
            this.txtPort.Name = "txtPort";
            this.txtPort.Size = new System.Drawing.Size(184, 30);
            this.txtPort.TabIndex = 52;
            this.txtPort.TextAlign = System.Windows.Forms.HorizontalAlignment.Center;
            this.txtPort.WordWrap = false;
            // 
            // lblHost
            // 
            this.lblHost.AutoSize = true;
            this.lblHost.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblHost.Location = new System.Drawing.Point(29, 274);
            this.lblHost.Name = "lblHost";
            this.lblHost.Size = new System.Drawing.Size(149, 25);
            this.lblHost.TabIndex = 53;
            this.lblHost.Text = "Host Address:";
            // 
            // lblPort
            // 
            this.lblPort.AutoSize = true;
            this.lblPort.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPort.Location = new System.Drawing.Point(29, 322);
            this.lblPort.Name = "lblPort";
            this.lblPort.Size = new System.Drawing.Size(58, 25);
            this.lblPort.TabIndex = 54;
            this.lblPort.Text = "Port:";
            // 
            // btnClose
            // 
            this.btnClose.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnClose.FlatAppearance.BorderSize = 0;
            this.btnClose.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnClose.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnClose.ForeColor = System.Drawing.Color.White;
            this.btnClose.Location = new System.Drawing.Point(247, 422);
            this.btnClose.Name = "btnClose";
            this.btnClose.Size = new System.Drawing.Size(126, 47);
            this.btnClose.TabIndex = 55;
            this.btnClose.Text = "Close";
            this.btnClose.UseVisualStyleBackColor = false;
            this.btnClose.Click += new System.EventHandler(this.BtnClose_Click);
            // 
            // SettingsForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(409, 496);
            this.Controls.Add(this.txtHost);
            this.Controls.Add(this.btnClose);
            this.Controls.Add(this.lblPort);
            this.Controls.Add(this.lblHost);
            this.Controls.Add(this.txtPort);
            this.Controls.Add(this.lblEnableSm);
            this.Controls.Add(this.chkEnableSm);
            this.Controls.Add(this.lblAutoWrite);
            this.Controls.Add(this.chkAutoWrite);
            this.Controls.Add(this.lblAutoRead);
            this.Controls.Add(this.chkAutoRead);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "SettingsForm";
            this.ShowInTaskbar = false;
            this.Text = "Settings";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.SettingsForm_FormClosing);
            this.Load += new System.EventHandler(this.SettingsForm_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label lblAutoWrite;
        private SwitchCheckBox chkAutoWrite;
        private System.Windows.Forms.Label lblAutoRead;
        private SwitchCheckBox chkAutoRead;
        private SwitchCheckBox chkEnableSm;
        private System.Windows.Forms.Label lblEnableSm;
        private System.Windows.Forms.TextBox txtHost;
        private System.Windows.Forms.TextBox txtPort;
        private System.Windows.Forms.Label lblHost;
        private System.Windows.Forms.Label lblPort;
        private System.Windows.Forms.Button btnClose;
    }
}