using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;
using System.Globalization;

namespace Green_Bus_Ticket_System_Utils
{
    public class PayPalSettings
    {
        public static string ApiDomain
        {
            get
            {
                return Setting<bool>("PayPal:Sandbox") ? "api-3t.sandbox.paypal.com" : "api-3t.paypal.com";
            }
        }

        public static string CgiDomain
        {
            get
            {
                return Setting<bool>("PayPal:Sandbox") ? "www.sandbox.paypal.com" : "www.paypal.com";
            }
        }

        public static string Signature
        {
            get
            {
                return Setting<string>("PayPal:Signature");
            }
        }

        public static string Username
        {
            get
            {
                return Setting<string>("PayPal:Username");
            }
        }

        public static string Password
        {
            get
            {
                return Setting<string>("PayPal:Password");
            }
        }

        public static string ReturnUrl
        {
            get
            {
                return Setting<string>("PayPal:ReturnUrl");
            }
        }

        public static string CancelUrl
        {
            get
            {
                return Setting<string>("PayPal:CancelUrl");
            }
        }

        public static string MerchantAccountID
        {
            get
            {
                return Setting<string>("PayPal:MerchantAccountID");
            }
        }

        public static string Environment
        {
            get
            {
                return Setting<string>("PayPal:Environment");
            }
        }

        private static T Setting<T>(string name)
        {
            string value = ConfigurationManager.AppSettings[name];
            if (value == null)
            {
                throw new Exception(String.Format("Could not find setting '{0}',", name));
            }
            return (T)Convert.ChangeType(value, typeof(T), CultureInfo.InvariantCulture);
        }
    }
}
