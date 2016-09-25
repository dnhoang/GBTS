using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;
using Twilio;
using log4net;
using System.Reflection;
using log4net.Config;

namespace Green_Bus_Ticket_System_Utils
{
    public static class SMSMessage
    {
        private static readonly ILog log = LogManager.GetLogger("UtilLog");
        private static string accountSid = ConfigurationManager.AppSettings["SMSAccountSid"];
        private static string authToken = ConfigurationManager.AppSettings["SMSAuthToken"];
        private static string serverNumber = ConfigurationManager.AppSettings["SMSServerNumber"];
        
        public static void SendSMS(string receiver, string body)
        {

            // Find your Account Sid and Auth Token at twilio.com/user/account 
            var twilio = new TwilioRestClient(accountSid, authToken);

            var message = twilio.SendMessage(serverNumber, receiver, body);

            if (message.RestException != null)
            {
                var error = message.RestException.Message;
                log.Error(error);
            }

        }
    }
}
