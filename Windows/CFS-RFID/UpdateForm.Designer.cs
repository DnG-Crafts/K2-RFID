﻿namespace CFS_RFID
{
    partial class UpdateForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(UpdateForm));
            this.btnCancel = new System.Windows.Forms.Button();
            this.btnUpdate = new System.Windows.Forms.Button();
            this.btnCheck = new System.Windows.Forms.Button();
            this.lblPass = new System.Windows.Forms.Label();
            this.lblIP = new System.Windows.Forms.Label();
            this.txtPass = new System.Windows.Forms.TextBox();
            this.txtIP = new System.Windows.Forms.TextBox();
            this.lblMsg = new System.Windows.Forms.Label();
            this.lblPrinter = new System.Windows.Forms.Label();
            this.lblIns = new System.Windows.Forms.Label();
            this.lblAvl = new System.Windows.Forms.Label();
            this.rtbDesc = new System.Windows.Forms.RichTextBox();
            this.lblCver = new System.Windows.Forms.Label();
            this.lblNver = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // btnCancel
            // 
            this.btnCancel.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnCancel.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnCancel.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnCancel.ForeColor = System.Drawing.Color.White;
            this.btnCancel.Location = new System.Drawing.Point(39, 563);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(126, 47);
            this.btnCancel.TabIndex = 10;
            this.btnCancel.Text = "Cancel";
            this.btnCancel.UseVisualStyleBackColor = false;
            this.btnCancel.Click += new System.EventHandler(this.BtnCancel_Click);
            // 
            // btnUpdate
            // 
            this.btnUpdate.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnUpdate.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnUpdate.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnUpdate.ForeColor = System.Drawing.Color.White;
            this.btnUpdate.Location = new System.Drawing.Point(265, 563);
            this.btnUpdate.Name = "btnUpdate";
            this.btnUpdate.Size = new System.Drawing.Size(126, 47);
            this.btnUpdate.TabIndex = 9;
            this.btnUpdate.Text = "Update";
            this.btnUpdate.UseVisualStyleBackColor = false;
            this.btnUpdate.Visible = false;
            this.btnUpdate.Click += new System.EventHandler(this.BtnUpdate_Click);
            // 
            // btnCheck
            // 
            this.btnCheck.BackColor = System.Drawing.Color.RoyalBlue;
            this.btnCheck.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnCheck.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.btnCheck.ForeColor = System.Drawing.Color.White;
            this.btnCheck.Location = new System.Drawing.Point(265, 563);
            this.btnCheck.Name = "btnCheck";
            this.btnCheck.Size = new System.Drawing.Size(126, 47);
            this.btnCheck.TabIndex = 11;
            this.btnCheck.Text = "Check";
            this.btnCheck.UseVisualStyleBackColor = false;
            this.btnCheck.Click += new System.EventHandler(this.BtnCheck_Click);
            // 
            // lblPass
            // 
            this.lblPass.AutoSize = true;
            this.lblPass.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPass.Location = new System.Drawing.Point(34, 236);
            this.lblPass.Name = "lblPass";
            this.lblPass.Size = new System.Drawing.Size(113, 25);
            this.lblPass.TabIndex = 15;
            this.lblPass.Text = "Password:";
            // 
            // lblIP
            // 
            this.lblIP.AutoSize = true;
            this.lblIP.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblIP.Location = new System.Drawing.Point(34, 176);
            this.lblIP.Name = "lblIP";
            this.lblIP.Size = new System.Drawing.Size(114, 25);
            this.lblIP.TabIndex = 14;
            this.lblIP.Text = "Printer IP: ";
            // 
            // txtPass
            // 
            this.txtPass.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtPass.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtPass.Location = new System.Drawing.Point(177, 234);
            this.txtPass.Name = "txtPass";
            this.txtPass.Size = new System.Drawing.Size(184, 30);
            this.txtPass.TabIndex = 13;
            // 
            // txtIP
            // 
            this.txtIP.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.txtIP.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.txtIP.Location = new System.Drawing.Point(177, 174);
            this.txtIP.Name = "txtIP";
            this.txtIP.Size = new System.Drawing.Size(184, 30);
            this.txtIP.TabIndex = 12;
            // 
            // lblMsg
            // 
            this.lblMsg.Font = new System.Drawing.Font("Microsoft Sans Serif", 10F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblMsg.Location = new System.Drawing.Point(39, 491);
            this.lblMsg.Name = "lblMsg";
            this.lblMsg.Size = new System.Drawing.Size(352, 41);
            this.lblMsg.TabIndex = 31;
            this.lblMsg.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // lblPrinter
            // 
            this.lblPrinter.Font = new System.Drawing.Font("Microsoft Sans Serif", 11F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblPrinter.Location = new System.Drawing.Point(34, 9);
            this.lblPrinter.Name = "lblPrinter";
            this.lblPrinter.Size = new System.Drawing.Size(357, 40);
            this.lblPrinter.TabIndex = 32;
            this.lblPrinter.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // lblIns
            // 
            this.lblIns.AutoSize = true;
            this.lblIns.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblIns.Location = new System.Drawing.Point(81, 69);
            this.lblIns.Name = "lblIns";
            this.lblIns.Size = new System.Drawing.Size(149, 20);
            this.lblIns.TabIndex = 33;
            this.lblIns.Text = "Installed Version:";
            this.lblIns.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblAvl
            // 
            this.lblAvl.AutoSize = true;
            this.lblAvl.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAvl.Location = new System.Drawing.Point(81, 105);
            this.lblAvl.Name = "lblAvl";
            this.lblAvl.Size = new System.Drawing.Size(152, 20);
            this.lblAvl.TabIndex = 34;
            this.lblAvl.Text = "Avaliable Version:";
            this.lblAvl.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.lblAvl.Visible = false;
            // 
            // rtbDesc
            // 
            this.rtbDesc.BackColor = System.Drawing.SystemColors.Control;
            this.rtbDesc.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.rtbDesc.Cursor = System.Windows.Forms.Cursors.Arrow;
            this.rtbDesc.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.rtbDesc.Location = new System.Drawing.Point(39, 320);
            this.rtbDesc.Name = "rtbDesc";
            this.rtbDesc.ReadOnly = true;
            this.rtbDesc.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.None;
            this.rtbDesc.ShortcutsEnabled = false;
            this.rtbDesc.Size = new System.Drawing.Size(352, 137);
            this.rtbDesc.TabIndex = 35;
            this.rtbDesc.TabStop = false;
            this.rtbDesc.Text = "";
            // 
            // lblCver
            // 
            this.lblCver.AutoSize = true;
            this.lblCver.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblCver.Location = new System.Drawing.Point(236, 69);
            this.lblCver.Name = "lblCver";
            this.lblCver.Size = new System.Drawing.Size(109, 20);
            this.lblCver.TabIndex = 36;
            this.lblCver.Text = "                    ";
            // 
            // lblNver
            // 
            this.lblNver.AutoSize = true;
            this.lblNver.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblNver.Location = new System.Drawing.Point(236, 105);
            this.lblNver.Name = "lblNver";
            this.lblNver.Size = new System.Drawing.Size(109, 20);
            this.lblNver.TabIndex = 37;
            this.lblNver.Text = "                    ";
            this.lblNver.Visible = false;
            // 
            // UpdateForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(9F, 20F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(434, 640);
            this.Controls.Add(this.lblNver);
            this.Controls.Add(this.lblCver);
            this.Controls.Add(this.rtbDesc);
            this.Controls.Add(this.lblAvl);
            this.Controls.Add(this.lblIns);
            this.Controls.Add(this.lblPrinter);
            this.Controls.Add(this.lblMsg);
            this.Controls.Add(this.lblPass);
            this.Controls.Add(this.lblIP);
            this.Controls.Add(this.txtPass);
            this.Controls.Add(this.txtIP);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.btnUpdate);
            this.Controls.Add(this.btnCheck);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "UpdateForm";
            this.ShowInTaskbar = false;
            this.Text = "Update";
            this.Load += new System.EventHandler(this.UpdateForm_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.Button btnUpdate;
        private System.Windows.Forms.Button btnCheck;
        private System.Windows.Forms.Label lblPass;
        private System.Windows.Forms.Label lblIP;
        private System.Windows.Forms.TextBox txtPass;
        private System.Windows.Forms.TextBox txtIP;
        private System.Windows.Forms.Label lblMsg;
        private System.Windows.Forms.Label lblPrinter;
        private System.Windows.Forms.Label lblIns;
        private System.Windows.Forms.Label lblAvl;
        private System.Windows.Forms.RichTextBox rtbDesc;
        private System.Windows.Forms.Label lblCver;
        private System.Windows.Forms.Label lblNver;
    }
}