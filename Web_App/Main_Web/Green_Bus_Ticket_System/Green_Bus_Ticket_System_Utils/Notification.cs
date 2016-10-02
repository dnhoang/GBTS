using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public class Notification
    {
        public string notificationCode { get; set; }
        public string message { get; set; }
        public Notification()
        {

        }
        public Notification(string notificationCode, string message)
        {
            this.notificationCode = notificationCode;
            this.message = message;
        }
    }
}
