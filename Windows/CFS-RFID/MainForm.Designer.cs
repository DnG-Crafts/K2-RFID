namespace CFS_RFID
{
    partial class MainForm
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
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.btnWrite = new System.Windows.Forms.Button();
            this.btnRead = new System.Windows.Forms.Button();
            this.btnColor = new System.Windows.Forms.Button();
            this.lblTagId = new System.Windows.Forms.Label();
            this.materialWeight = new System.Windows.Forms.ComboBox();
            this.materialName = new System.Windows.Forms.ComboBox();
            this.lblUid = new System.Windows.Forms.Label();
            this.printerModel = new System.Windows.Forms.ComboBox();
            this.vendorName = new System.Windows.Forms.ComboBox();
            this.lblConnect = new System.Windows.Forms.Label();
            this.btnDel = new System.Windows.Forms.Button();
            this.btnEdit = new System.Windows.Forms.Button();
            this.btnAdd = new System.Windows.Forms.Button();
            this.btnMenu = new System.Windows.Forms.Button();
            this.imgEnc = new System.Windows.Forms.PictureBox();
            this.sidebarPanel = new System.Windows.Forms.Panel();
            this.flowMenu = new System.Windows.Forms.FlowLayoutPanel();
            this.sidebarTimer = new System.Windows.Forms.Timer(this.components);
            this.lblAdd = new System.Windows.Forms.Label();
            this.lblAutoWrite = new System.Windows.Forms.Label();
            this.chkAutoWrite = new SwitchCheckBox();
            this.lblAutoRead = new System.Windows.Forms.Label();
            this.chkAutoRead = new SwitchCheckBox();
            this.btnFrame = new System.Windows.Forms.Panel();
            ((System.ComponentModel.ISupportInitialize)(this.imgEnc)).BeginInit();
            this.sidebarPanel.SuspendLayout();
            this.btnFrame.SuspendLayout();
            this.SuspendLayout();
            // 
            // btnWrite
            // 
            this.btnWrite.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnWrite.FlatAppearance.BorderSize = 0;
            this.btnWrite.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnWrite.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnWrite.ForeColor = System.Drawing.Color.White;
            this.btnWrite.Location = new System.Drawing.Point(199, 121);
            this.btnWrite.Name = "btnWrite";
            this.btnWrite.Size = new System.Drawing.Size(140, 47);
            this.btnWrite.TabIndex = 23;
            this.btnWrite.Text = "Write Tag";
            this.btnWrite.UseVisualStyleBackColor = false;
            this.btnWrite.Click += new System.EventHandler(this.BtnWrite_Click);
            // 
            // btnRead
            // 
            this.btnRead.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnRead.FlatAppearance.BorderSize = 0;
            this.btnRead.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnRead.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnRead.ForeColor = System.Drawing.Color.White;
            this.btnRead.Location = new System.Drawing.Point(16, 121);
            this.btnRead.Name = "btnRead";
            this.btnRead.Size = new System.Drawing.Size(140, 47);
            this.btnRead.TabIndex = 22;
            this.btnRead.Text = "Read Tag";
            this.btnRead.UseVisualStyleBackColor = false;
            this.btnRead.Click += new System.EventHandler(this.BtnRead_Click);
            // 
            // btnColor
            // 
            this.btnColor.BackColor = System.Drawing.SystemColors.Control;
            this.btnColor.FlatAppearance.BorderSize = 0;
            this.btnColor.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnColor.Location = new System.Drawing.Point(69, 320);
            this.btnColor.Name = "btnColor";
            this.btnColor.Size = new System.Drawing.Size(259, 40);
            this.btnColor.TabIndex = 25;
            this.btnColor.UseVisualStyleBackColor = false;
            this.btnColor.Click += new System.EventHandler(this.BtnColor_Click);
            // 
            // lblTagId
            // 
            this.lblTagId.AutoSize = true;
            this.lblTagId.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblTagId.Location = new System.Drawing.Point(29, 61);
            this.lblTagId.Name = "lblTagId";
            this.lblTagId.Size = new System.Drawing.Size(68, 20);
            this.lblTagId.TabIndex = 28;
            this.lblTagId.Text = "Tag ID:";
            this.lblTagId.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.lblTagId.Visible = false;
            // 
            // materialWeight
            // 
            this.materialWeight.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.materialWeight.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.materialWeight.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.materialWeight.FormattingEnabled = true;
            this.materialWeight.Items.AddRange(new object[] {
            "1 KG",
            "750 G",
            "600 G",
            "500 G",
            "250 G"});
            this.materialWeight.Location = new System.Drawing.Point(69, 259);
            this.materialWeight.Name = "materialWeight";
            this.materialWeight.Size = new System.Drawing.Size(259, 37);
            this.materialWeight.TabIndex = 26;
            this.materialWeight.SelectedIndexChanged += new System.EventHandler(this.MaterialWeight_SelectedIndexChanged);
            // 
            // materialName
            // 
            this.materialName.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.materialName.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.materialName.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.materialName.FormattingEnabled = true;
            this.materialName.Location = new System.Drawing.Point(69, 200);
            this.materialName.Name = "materialName";
            this.materialName.Size = new System.Drawing.Size(259, 37);
            this.materialName.TabIndex = 24;
            this.materialName.SelectedIndexChanged += new System.EventHandler(this.MaterialName_SelectedIndexChanged);
            // 
            // lblUid
            // 
            this.lblUid.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblUid.Location = new System.Drawing.Point(137, 62);
            this.lblUid.Name = "lblUid";
            this.lblUid.Size = new System.Drawing.Size(154, 20);
            this.lblUid.TabIndex = 21;
            this.lblUid.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // printerModel
            // 
            this.printerModel.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.printerModel.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.printerModel.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.printerModel.FormattingEnabled = true;
            this.printerModel.Location = new System.Drawing.Point(69, 85);
            this.printerModel.Name = "printerModel";
            this.printerModel.Size = new System.Drawing.Size(259, 37);
            this.printerModel.TabIndex = 34;
            this.printerModel.SelectedIndexChanged += new System.EventHandler(this.PrinterModel_SelectedIndexChanged);
            this.printerModel.SelectionChangeCommitted += new System.EventHandler(this.PrinterModel_SelectionChangeCommitted);
            // 
            // vendorName
            // 
            this.vendorName.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.vendorName.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.vendorName.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.vendorName.FormattingEnabled = true;
            this.vendorName.Location = new System.Drawing.Point(69, 142);
            this.vendorName.Name = "vendorName";
            this.vendorName.Size = new System.Drawing.Size(259, 37);
            this.vendorName.TabIndex = 35;
            this.vendorName.SelectedIndexChanged += new System.EventHandler(this.VendorName_SelectedIndexChanged);
            // 
            // lblConnect
            // 
            this.lblConnect.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblConnect.ForeColor = System.Drawing.Color.IndianRed;
            this.lblConnect.Location = new System.Drawing.Point(36, 375);
            this.lblConnect.Name = "lblConnect";
            this.lblConnect.Size = new System.Drawing.Size(317, 65);
            this.lblConnect.TabIndex = 36;
            this.lblConnect.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            this.lblConnect.Click += new System.EventHandler(this.LblConnect_Click);
            // 
            // btnDel
            // 
            this.btnDel.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnDel.FlatAppearance.BorderSize = 0;
            this.btnDel.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnDel.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnDel.Image = global::CFS_RFID.Properties.Resources.delete;
            this.btnDel.Location = new System.Drawing.Point(139, 17);
            this.btnDel.Name = "btnDel";
            this.btnDel.Size = new System.Drawing.Size(48, 48);
            this.btnDel.TabIndex = 33;
            this.btnDel.UseVisualStyleBackColor = false;
            this.btnDel.Click += new System.EventHandler(this.BtnDel_Click);
            this.btnDel.MouseLeave += new System.EventHandler(this.BtnDel_MouseLeave);
            // 
            // btnEdit
            // 
            this.btnEdit.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnEdit.FlatAppearance.BorderSize = 0;
            this.btnEdit.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnEdit.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnEdit.Image = global::CFS_RFID.Properties.Resources.edit;
            this.btnEdit.Location = new System.Drawing.Point(216, 17);
            this.btnEdit.Name = "btnEdit";
            this.btnEdit.Size = new System.Drawing.Size(48, 48);
            this.btnEdit.TabIndex = 32;
            this.btnEdit.UseVisualStyleBackColor = false;
            this.btnEdit.Click += new System.EventHandler(this.BtnEdit_Click);
            this.btnEdit.MouseLeave += new System.EventHandler(this.BtnEdit_MouseLeave);
            // 
            // btnAdd
            // 
            this.btnAdd.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnAdd.FlatAppearance.BorderSize = 0;
            this.btnAdd.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnAdd.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnAdd.Image = global::CFS_RFID.Properties.Resources.add;
            this.btnAdd.Location = new System.Drawing.Point(293, 17);
            this.btnAdd.Name = "btnAdd";
            this.btnAdd.Size = new System.Drawing.Size(48, 48);
            this.btnAdd.TabIndex = 31;
            this.btnAdd.UseVisualStyleBackColor = false;
            this.btnAdd.Click += new System.EventHandler(this.BtnAdd_Click);
            this.btnAdd.MouseLeave += new System.EventHandler(this.BtnAdd_MouseLeave);
            // 
            // btnMenu
            // 
            this.btnMenu.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnMenu.FlatAppearance.BorderSize = 0;
            this.btnMenu.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnMenu.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.btnMenu.Image = global::CFS_RFID.Properties.Resources.menu;
            this.btnMenu.Location = new System.Drawing.Point(19, 16);
            this.btnMenu.Name = "btnMenu";
            this.btnMenu.Size = new System.Drawing.Size(48, 48);
            this.btnMenu.TabIndex = 41;
            this.btnMenu.UseVisualStyleBackColor = false;
            this.btnMenu.Click += new System.EventHandler(this.BtnMenu_Click);
            this.btnMenu.MouseLeave += new System.EventHandler(this.BtnMenu_MouseLeave);
            // 
            // imgEnc
            // 
            this.imgEnc.BackgroundImageLayout = System.Windows.Forms.ImageLayout.Center;
            this.imgEnc.Image = global::CFS_RFID.Properties.Resources._lock;
            this.imgEnc.Location = new System.Drawing.Point(103, 57);
            this.imgEnc.Name = "imgEnc";
            this.imgEnc.Size = new System.Drawing.Size(24, 24);
            this.imgEnc.SizeMode = System.Windows.Forms.PictureBoxSizeMode.StretchImage;
            this.imgEnc.TabIndex = 45;
            this.imgEnc.TabStop = false;
            this.imgEnc.Visible = false;
            this.imgEnc.Click += new System.EventHandler(this.ImgEnc_Click);
            this.imgEnc.MouseLeave += new System.EventHandler(this.ImgEnc_MouseLeave);
            // 
            // sidebarPanel
            // 
            this.sidebarPanel.BackColor = System.Drawing.Color.White;
            this.sidebarPanel.Controls.Add(this.flowMenu);
            this.sidebarPanel.Dock = System.Windows.Forms.DockStyle.Left;
            this.sidebarPanel.Location = new System.Drawing.Point(0, 0);
            this.sidebarPanel.Name = "sidebarPanel";
            this.sidebarPanel.Size = new System.Drawing.Size(27, 657);
            this.sidebarPanel.TabIndex = 49;
            // 
            // flowMenu
            // 
            this.flowMenu.FlowDirection = System.Windows.Forms.FlowDirection.TopDown;
            this.flowMenu.Location = new System.Drawing.Point(12, 12);
            this.flowMenu.Name = "flowMenu";
            this.flowMenu.Size = new System.Drawing.Size(296, 633);
            this.flowMenu.TabIndex = 49;
            this.flowMenu.WrapContents = false;
            // 
            // sidebarTimer
            // 
            this.sidebarTimer.Interval = 10;
            this.sidebarTimer.Tick += new System.EventHandler(this.sidebarTimer_Tick);
            // 
            // lblAdd
            // 
            this.lblAdd.AutoSize = true;
            this.lblAdd.Cursor = System.Windows.Forms.Cursors.Hand;
            this.lblAdd.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, ((System.Drawing.FontStyle)((System.Drawing.FontStyle.Bold | System.Drawing.FontStyle.Underline))), System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAdd.ForeColor = System.Drawing.Color.RoyalBlue;
            this.lblAdd.Location = new System.Drawing.Point(83, 9);
            this.lblAdd.Name = "lblAdd";
            this.lblAdd.Size = new System.Drawing.Size(178, 25);
            this.lblAdd.TabIndex = 50;
            this.lblAdd.Text = "Add to Spoolman";
            this.lblAdd.Click += new System.EventHandler(this.LblAdd_Click);
            // 
            // lblAutoWrite
            // 
            this.lblAutoWrite.AutoSize = true;
            this.lblAutoWrite.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAutoWrite.Location = new System.Drawing.Point(49, 431);
            this.lblAutoWrite.Name = "lblAutoWrite";
            this.lblAutoWrite.Size = new System.Drawing.Size(199, 20);
            this.lblAutoWrite.TabIndex = 54;
            this.lblAutoWrite.Text = "Auto write tag on scan?";
            this.lblAutoWrite.Visible = false;
            // 
            // chkAutoWrite
            // 
            this.chkAutoWrite.BorderThickness = 1;
            this.chkAutoWrite.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.chkAutoWrite.Location = new System.Drawing.Point(281, 426);
            this.chkAutoWrite.MaximumSize = new System.Drawing.Size(100, 50);
            this.chkAutoWrite.MinimumSize = new System.Drawing.Size(30, 15);
            this.chkAutoWrite.Name = "chkAutoWrite";
            this.chkAutoWrite.Size = new System.Drawing.Size(60, 31);
            this.chkAutoWrite.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkAutoWrite.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkAutoWrite.TabIndex = 53;
            this.chkAutoWrite.Text = "Auto read on tag scan?";
            this.chkAutoWrite.ThumbColor = System.Drawing.Color.White;
            this.chkAutoWrite.UseVisualStyleBackColor = true;
            this.chkAutoWrite.Visible = false;
            // 
            // lblAutoRead
            // 
            this.lblAutoRead.AutoSize = true;
            this.lblAutoRead.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAutoRead.Location = new System.Drawing.Point(49, 380);
            this.lblAutoRead.Name = "lblAutoRead";
            this.lblAutoRead.Size = new System.Drawing.Size(197, 20);
            this.lblAutoRead.TabIndex = 52;
            this.lblAutoRead.Text = "Auto read tag on scan?";
            this.lblAutoRead.Visible = false;
            // 
            // chkAutoRead
            // 
            this.chkAutoRead.BorderThickness = 1;
            this.chkAutoRead.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.chkAutoRead.Location = new System.Drawing.Point(281, 375);
            this.chkAutoRead.MaximumSize = new System.Drawing.Size(100, 50);
            this.chkAutoRead.MinimumSize = new System.Drawing.Size(30, 15);
            this.chkAutoRead.Name = "chkAutoRead";
            this.chkAutoRead.Size = new System.Drawing.Size(60, 31);
            this.chkAutoRead.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkAutoRead.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkAutoRead.TabIndex = 51;
            this.chkAutoRead.Text = "Auto read on tag scan?";
            this.chkAutoRead.ThumbColor = System.Drawing.Color.White;
            this.chkAutoRead.UseVisualStyleBackColor = true;
            this.chkAutoRead.Visible = false;
            // 
            // btnFrame
            // 
            this.btnFrame.Controls.Add(this.btnWrite);
            this.btnFrame.Controls.Add(this.lblUid);
            this.btnFrame.Controls.Add(this.lblTagId);
            this.btnFrame.Controls.Add(this.btnRead);
            this.btnFrame.Controls.Add(this.imgEnc);
            this.btnFrame.Controls.Add(this.lblAdd);
            this.btnFrame.Location = new System.Drawing.Point(13, 469);
            this.btnFrame.Name = "btnFrame";
            this.btnFrame.Size = new System.Drawing.Size(352, 182);
            this.btnFrame.TabIndex = 55;
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(244)))), ((int)(((byte)(244)))), ((int)(((byte)(244)))));
            this.ClientSize = new System.Drawing.Size(383, 657);
            this.Controls.Add(this.sidebarPanel);
            this.Controls.Add(this.btnFrame);
            this.Controls.Add(this.lblAutoWrite);
            this.Controls.Add(this.chkAutoWrite);
            this.Controls.Add(this.lblAutoRead);
            this.Controls.Add(this.chkAutoRead);
            this.Controls.Add(this.btnMenu);
            this.Controls.Add(this.vendorName);
            this.Controls.Add(this.printerModel);
            this.Controls.Add(this.btnDel);
            this.Controls.Add(this.btnEdit);
            this.Controls.Add(this.btnAdd);
            this.Controls.Add(this.btnColor);
            this.Controls.Add(this.materialWeight);
            this.Controls.Add(this.materialName);
            this.Controls.Add(this.lblConnect);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "CFS RFID";
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.MainForm_FormClosed);
            this.Load += new System.EventHandler(this.MainForm_Load);
            this.LocationChanged += new System.EventHandler(this.MainForm_LocationChanged);
            this.Click += new System.EventHandler(this.MainForm_Click);
            this.MouseMove += new System.Windows.Forms.MouseEventHandler(this.MainForm_MouseMove);
            ((System.ComponentModel.ISupportInitialize)(this.imgEnc)).EndInit();
            this.sidebarPanel.ResumeLayout(false);
            this.btnFrame.ResumeLayout(false);
            this.btnFrame.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion
        private System.Windows.Forms.Button btnWrite;
        private System.Windows.Forms.Button btnRead;
        private System.Windows.Forms.Button btnDel;
        private System.Windows.Forms.Button btnEdit;
        private System.Windows.Forms.Button btnAdd;
        private System.Windows.Forms.Button btnColor;
        private System.Windows.Forms.Label lblTagId;
        private System.Windows.Forms.ComboBox materialWeight;
        private System.Windows.Forms.ComboBox materialName;
        private System.Windows.Forms.Label lblUid;
        private System.Windows.Forms.ComboBox printerModel;
        private System.Windows.Forms.ComboBox vendorName;
        private System.Windows.Forms.Label lblConnect;
        private System.Windows.Forms.Button btnMenu;
        private System.Windows.Forms.PictureBox imgEnc;
        private System.Windows.Forms.Panel sidebarPanel;
        private System.Windows.Forms.FlowLayoutPanel flowMenu;
        private System.Windows.Forms.Timer sidebarTimer;
        private System.Windows.Forms.Label lblAdd;
        private System.Windows.Forms.Label lblAutoWrite;
        private SwitchCheckBox chkAutoWrite;
        private System.Windows.Forms.Label lblAutoRead;
        private SwitchCheckBox chkAutoRead;
        private System.Windows.Forms.Panel btnFrame;
    }
}

