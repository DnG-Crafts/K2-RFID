namespace CFS_RFID
{
    partial class FilamentForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FilamentForm));
            this.paramList = new System.Windows.Forms.ListView();
            this.btnSave = new System.Windows.Forms.Button();
            this.cboType = new System.Windows.Forms.ComboBox();
            this.cboBrand = new System.Windows.Forms.ComboBox();
            this.label6 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.lblID = new System.Windows.Forms.Label();
            this.txtMaxTemp = new System.Windows.Forms.TextBox();
            this.txtMinTemp = new System.Windows.Forms.TextBox();
            this.txtName = new System.Windows.Forms.TextBox();
            this.txtId = new System.Windows.Forms.TextBox();
            this.btnCancel = new System.Windows.Forms.Button();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.basePage = new System.Windows.Forms.TabPage();
            this.rtbDesc = new System.Windows.Forms.RichTextBox();
            this.label8 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.chkSupport = new SwitchCheckBox();
            this.chkSoluble = new SwitchCheckBox();
            this.paramPage = new System.Windows.Forms.TabPage();
            this.lblFilament = new System.Windows.Forms.Label();
            this.tabControl1.SuspendLayout();
            this.basePage.SuspendLayout();
            this.paramPage.SuspendLayout();
            this.SuspendLayout();
            // 
            // paramList
            // 
            this.paramList.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.paramList.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.paramList.HideSelection = false;
            this.paramList.Location = new System.Drawing.Point(6, 6);
            this.paramList.Name = "paramList";
            this.paramList.Size = new System.Drawing.Size(1112, 712);
            this.paramList.TabIndex = 0;
            this.paramList.UseCompatibleStateImageBehavior = false;
            this.paramList.AfterLabelEdit += new System.Windows.Forms.LabelEditEventHandler(this.ParamList_AfterLabelEdit);
            this.paramList.MouseClick += new System.Windows.Forms.MouseEventHandler(this.ParamList_MouseClick);
            // 
            // btnSave
            // 
            this.btnSave.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnSave.FlatAppearance.BorderSize = 0;
            this.btnSave.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnSave.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnSave.ForeColor = System.Drawing.Color.White;
            this.btnSave.Location = new System.Drawing.Point(1014, 785);
            this.btnSave.Name = "btnSave";
            this.btnSave.Size = new System.Drawing.Size(126, 47);
            this.btnSave.TabIndex = 2;
            this.btnSave.Text = "Save";
            this.btnSave.UseVisualStyleBackColor = false;
            this.btnSave.Click += new System.EventHandler(this.BtnSave_Click);
            // 
            // cboType
            // 
            this.cboType.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.cboType.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.cboType.FormattingEnabled = true;
            this.cboType.Location = new System.Drawing.Point(214, 188);
            this.cboType.Name = "cboType";
            this.cboType.Size = new System.Drawing.Size(175, 33);
            this.cboType.TabIndex = 15;
            // 
            // cboBrand
            // 
            this.cboBrand.FlatStyle = System.Windows.Forms.FlatStyle.System;
            this.cboBrand.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.cboBrand.FormattingEnabled = true;
            this.cboBrand.Location = new System.Drawing.Point(214, 99);
            this.cboBrand.Name = "cboBrand";
            this.cboBrand.Size = new System.Drawing.Size(175, 33);
            this.cboBrand.TabIndex = 14;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label6.Location = new System.Drawing.Point(74, 282);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(114, 25);
            this.label6.TabIndex = 13;
            this.label6.Text = "Max Temp";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label5.Location = new System.Drawing.Point(74, 239);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(108, 25);
            this.label5.TabIndex = 12;
            this.label5.Text = "Min Temp";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label4.Location = new System.Drawing.Point(74, 196);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(61, 25);
            this.label4.TabIndex = 11;
            this.label4.Text = "Type";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label3.Location = new System.Drawing.Point(74, 151);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(68, 25);
            this.label3.TabIndex = 10;
            this.label3.Text = "Name";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label2.Location = new System.Drawing.Point(74, 107);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(69, 25);
            this.label2.TabIndex = 9;
            this.label2.Text = "Brand";
            // 
            // lblID
            // 
            this.lblID.AutoSize = true;
            this.lblID.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblID.Location = new System.Drawing.Point(74, 59);
            this.lblID.Name = "lblID";
            this.lblID.Size = new System.Drawing.Size(33, 25);
            this.lblID.TabIndex = 8;
            this.lblID.Text = "ID";
            // 
            // txtMaxTemp
            // 
            this.txtMaxTemp.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtMaxTemp.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtMaxTemp.Location = new System.Drawing.Point(214, 277);
            this.txtMaxTemp.MaxLength = 3;
            this.txtMaxTemp.Name = "txtMaxTemp";
            this.txtMaxTemp.Size = new System.Drawing.Size(175, 30);
            this.txtMaxTemp.TabIndex = 5;
            this.txtMaxTemp.KeyDown += new System.Windows.Forms.KeyEventHandler(this.TxtMaxTemp_KeyDown);
            this.txtMaxTemp.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.TxtMaxTemp_KeyPress);
            // 
            // txtMinTemp
            // 
            this.txtMinTemp.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtMinTemp.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtMinTemp.Location = new System.Drawing.Point(214, 235);
            this.txtMinTemp.MaxLength = 3;
            this.txtMinTemp.Name = "txtMinTemp";
            this.txtMinTemp.Size = new System.Drawing.Size(175, 30);
            this.txtMinTemp.TabIndex = 4;
            this.txtMinTemp.KeyDown += new System.Windows.Forms.KeyEventHandler(this.TxtMinTemp_KeyDown);
            this.txtMinTemp.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.TxtMinTemp_KeyPress);
            // 
            // txtName
            // 
            this.txtName.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtName.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtName.Location = new System.Drawing.Point(214, 146);
            this.txtName.Name = "txtName";
            this.txtName.Size = new System.Drawing.Size(175, 30);
            this.txtName.TabIndex = 1;
            // 
            // txtId
            // 
            this.txtId.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtId.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtId.Location = new System.Drawing.Point(214, 54);
            this.txtId.MaxLength = 5;
            this.txtId.Name = "txtId";
            this.txtId.Size = new System.Drawing.Size(175, 30);
            this.txtId.TabIndex = 0;
            this.txtId.KeyDown += new System.Windows.Forms.KeyEventHandler(this.TxtId_KeyDown);
            this.txtId.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.TxtId_KeyPress);
            // 
            // btnCancel
            // 
            this.btnCancel.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnCancel.FlatAppearance.BorderSize = 0;
            this.btnCancel.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnCancel.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnCancel.ForeColor = System.Drawing.Color.White;
            this.btnCancel.Location = new System.Drawing.Point(831, 785);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(126, 47);
            this.btnCancel.TabIndex = 4;
            this.btnCancel.Text = "Cancel";
            this.btnCancel.UseVisualStyleBackColor = false;
            this.btnCancel.Click += new System.EventHandler(this.BtnCancel_Click);
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.basePage);
            this.tabControl1.Controls.Add(this.paramPage);
            this.tabControl1.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.tabControl1.Location = new System.Drawing.Point(12, 12);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(1132, 757);
            this.tabControl1.TabIndex = 5;
            // 
            // basePage
            // 
            this.basePage.BackColor = System.Drawing.SystemColors.Control;
            this.basePage.Controls.Add(this.rtbDesc);
            this.basePage.Controls.Add(this.label8);
            this.basePage.Controls.Add(this.label7);
            this.basePage.Controls.Add(this.chkSupport);
            this.basePage.Controls.Add(this.chkSoluble);
            this.basePage.Controls.Add(this.cboType);
            this.basePage.Controls.Add(this.txtMaxTemp);
            this.basePage.Controls.Add(this.cboBrand);
            this.basePage.Controls.Add(this.txtId);
            this.basePage.Controls.Add(this.label6);
            this.basePage.Controls.Add(this.txtName);
            this.basePage.Controls.Add(this.label5);
            this.basePage.Controls.Add(this.txtMinTemp);
            this.basePage.Controls.Add(this.label4);
            this.basePage.Controls.Add(this.label3);
            this.basePage.Controls.Add(this.label2);
            this.basePage.Controls.Add(this.lblID);
            this.basePage.Location = new System.Drawing.Point(4, 34);
            this.basePage.Name = "basePage";
            this.basePage.Padding = new System.Windows.Forms.Padding(3);
            this.basePage.Size = new System.Drawing.Size(1124, 719);
            this.basePage.TabIndex = 0;
            this.basePage.Text = "Base";
            // 
            // rtbDesc
            // 
            this.rtbDesc.BackColor = System.Drawing.SystemColors.Control;
            this.rtbDesc.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.rtbDesc.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.rtbDesc.Location = new System.Drawing.Point(506, 151);
            this.rtbDesc.Name = "rtbDesc";
            this.rtbDesc.ReadOnly = true;
            this.rtbDesc.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.None;
            this.rtbDesc.ShortcutsEnabled = false;
            this.rtbDesc.Size = new System.Drawing.Size(523, 360);
            this.rtbDesc.TabIndex = 21;
            this.rtbDesc.TabStop = false;
            this.rtbDesc.Text = "";
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label8.Location = new System.Drawing.Point(75, 404);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(172, 20);
            this.label8.TabIndex = 20;
            this.label8.Text = "Filament is support?";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label7.Location = new System.Drawing.Point(75, 355);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(168, 20);
            this.label7.TabIndex = 19;
            this.label7.Text = "Filament is soluble?";
            // 
            // chkSupport
            // 
            this.chkSupport.BorderThickness = 1;
            this.chkSupport.Location = new System.Drawing.Point(329, 398);
            this.chkSupport.MinimumSize = new System.Drawing.Size(40, 20);
            this.chkSupport.Name = "chkSupport";
            this.chkSupport.Size = new System.Drawing.Size(60, 31);
            this.chkSupport.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkSupport.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkSupport.TabIndex = 18;
            this.chkSupport.Text = "switchCheckBox2";
            this.chkSupport.ThumbColor = System.Drawing.Color.White;
            this.chkSupport.UseVisualStyleBackColor = true;
            // 
            // chkSoluble
            // 
            this.chkSoluble.BorderThickness = 1;
            this.chkSoluble.Location = new System.Drawing.Point(329, 349);
            this.chkSoluble.MinimumSize = new System.Drawing.Size(40, 20);
            this.chkSoluble.Name = "chkSoluble";
            this.chkSoluble.Size = new System.Drawing.Size(60, 31);
            this.chkSoluble.SwitchOffColor = System.Drawing.Color.LightGray;
            this.chkSoluble.SwitchOnColor = System.Drawing.Color.FromArgb(((int)(((byte)(25)))), ((int)(((byte)(118)))), ((int)(((byte)(210)))));
            this.chkSoluble.TabIndex = 17;
            this.chkSoluble.Text = "switchCheckBox1";
            this.chkSoluble.ThumbColor = System.Drawing.Color.White;
            this.chkSoluble.UseVisualStyleBackColor = true;
            // 
            // paramPage
            // 
            this.paramPage.BackColor = System.Drawing.SystemColors.Control;
            this.paramPage.Controls.Add(this.paramList);
            this.paramPage.Location = new System.Drawing.Point(4, 34);
            this.paramPage.Name = "paramPage";
            this.paramPage.Padding = new System.Windows.Forms.Padding(3);
            this.paramPage.Size = new System.Drawing.Size(1124, 719);
            this.paramPage.TabIndex = 1;
            this.paramPage.Text = "Param";
            // 
            // lblFilament
            // 
            this.lblFilament.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblFilament.Location = new System.Drawing.Point(23, 790);
            this.lblFilament.Name = "lblFilament";
            this.lblFilament.Size = new System.Drawing.Size(782, 42);
            this.lblFilament.TabIndex = 6;
            // 
            // FilamentForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1159, 847);
            this.Controls.Add(this.lblFilament);
            this.Controls.Add(this.tabControl1);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.btnSave);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "FilamentForm";
            this.ShowInTaskbar = false;
            this.Text = "Filament";
            this.Load += new System.EventHandler(this.FilamentForm_Load);
            this.tabControl1.ResumeLayout(false);
            this.basePage.ResumeLayout(false);
            this.basePage.PerformLayout();
            this.paramPage.ResumeLayout(false);
            this.ResumeLayout(false);

        }




        #endregion

        private System.Windows.Forms.ListView paramList;
        private System.Windows.Forms.Button btnSave;
        private System.Windows.Forms.TextBox txtMaxTemp;
        private System.Windows.Forms.TextBox txtMinTemp;
        private System.Windows.Forms.TextBox txtName;
        private System.Windows.Forms.TextBox txtId;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblID;
        private System.Windows.Forms.ComboBox cboBrand;
        private System.Windows.Forms.ComboBox cboType;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage basePage;
        private System.Windows.Forms.TabPage paramPage;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label label7;
        private SwitchCheckBox chkSupport;
        private SwitchCheckBox chkSoluble;
        private System.Windows.Forms.RichTextBox rtbDesc;
        private System.Windows.Forms.Label lblFilament;
    }
}