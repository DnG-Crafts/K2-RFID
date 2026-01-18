using System.Drawing;
using System.Windows.Forms;

namespace CFS_RFID
{
    public partial class TagBlockCard : UserControl
    {
        public TagBlockCard()
        {
            InitializeComponent();
        }

        public void SetData(string title, string hex, Image icon)
        {
            lblTitle.Text = title;
            lblHex.Text = hex;
            picIcon.Image = icon;
            this.BorderStyle = BorderStyle.FixedSingle;
        }
    }
}
