using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Collections.Specialized;
using System.Net;
using System.IO;
using System.Globalization;
using System.Text;


namespace Green_Bus_Ticket_System_Utils
{
    public class PayPal
    {
        public static PayPalRedirect ExpressCheckout(PayPalOrder order)
        {
            NameValueCollection values = new NameValueCollection();
            values["METHOD"] = "SetExpressCheckout";
            values["PAYMENTREQUEST_0_AMT"] = order.Amount.ToString();
            values["RETURNURL"] = PayPalSettings.ReturnUrl;
            values["CANCELURL"] = PayPalSettings.CancelUrl;
            values["PAYMENTACTION"] = "SALE";
            values["CURRENCYCODE"] = "USD";
            values["BUTTONSOURCE"] = "PP-ECWizard";
            values["USER"] = PayPalSettings.Username;
            values["PWD"] = PayPalSettings.Password;
            values["SIGNATURE"] = PayPalSettings.Signature;
            values["VERSION"] = "93";
            values["BRANDNAME"] = "Green Bus Ticket System";
            values["L_PAYMENTREQUEST_0_NAME0"] = order.CreditPlanName;
            values["PAYMENTREQUEST_0_CUSTOM"] = order.CreditPlanId.ToString();
            
            values["L_PAYMENTREQUEST_0_AMT0"] = order.Amount.ToString();
            values["L_PAYMENTREQUEST_0_QTY0"] = "1";

            values = Submit(values);

            string ack = values["ACK"].ToLower();

            if (ack == "success" || ack == "successwithwarning")
            {
                HttpContext.Current.Session["CurrentCardId"] = order.CardId;
                return new PayPalRedirect
                {
                    Token = values["TOKEN"],
                    Url = String.Format("https://{0}/cgi-bin/webscr?cmd=_express-checkout&token={1}", PayPalSettings.CgiDomain, values["TOKEN"])
                };
            }
            else
            {
                throw new Exception(values["L_LONGMESSAGE0"]);
            }
        }

        private static NameValueCollection Submit(NameValueCollection values)
        {
            string data = String.Join("&", values.Cast<string>().Select(key => String.Format("{0}={1}", key, values[key])));
            ServicePointManager.Expect100Continue = true;
            ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls | SecurityProtocolType.Tls11 | SecurityProtocolType.Tls12 | SecurityProtocolType.Ssl3;
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format("https://{0}/nvp", PayPalSettings.ApiDomain));
            request.Method = "POST";
            request.KeepAlive = false;

            byte[] byteArray = Encoding.UTF8.GetBytes(data);
            request.ContentType = "application/x-www-form-urlencoded";
            request.ContentLength = byteArray.Length;

            Stream dataStream = request.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();

            using (StreamReader reader = new StreamReader(request.GetResponse().GetResponseStream()))
            {
                return HttpUtility.ParseQueryString(reader.ReadToEnd());
            }
        }

        public static bool GetCheckout(string token, ref string amount, ref string creditPlanId)
        {
            NameValueCollection values = new NameValueCollection();
            values["METHOD"] = "GetExpressCheckoutDetails";
            values["TOKEN"] = token;
            values["USER"] = PayPalSettings.Username;
            values["PWD"] = PayPalSettings.Password;
            values["SIGNATURE"] = PayPalSettings.Signature;
            values["VERSION"] = "93";
            values = Submit(values);

            string ack = values["ACK"].ToLower();

            if (ack == "success" || ack == "successwithwarning")
            {
                amount = values["PAYMENTREQUEST_0_AMT"];
                creditPlanId = values["PAYMENTREQUEST_0_CUSTOM"];
                return true;
            }
            else
            {
                return false;
            }
        }
        public static bool DoCheckoutPayment(string token, string PayerID, ref string creditPlanId, ref string transctionId)
        {
            if (HttpContext.Current.Session["CurrentSectionPayment"] == null)
            {
                HttpContext.Current.Session["CurrentCardId"] = null;
                return false;
            }

            string amount = "";
            string creditPlan = "";

            if (!GetCheckout(token, ref amount, ref creditPlan))
            {
                HttpContext.Current.Session["CurrentCardId"] = null;
                return false;
            }

            NameValueCollection values = new NameValueCollection();
            values["METHOD"] = "DoExpressCheckoutPayment";
            values["TOKEN"] = token;
            values["PAYERID"] = PayerID;
            values["PAYMENTREQUEST_0_AMT"] = amount;
            values["PAYMENTREQUEST_0_CURRENCYCODE"] = "USD";
            values["PAYMENTREQUEST_0_PAYMENTACTION"] = "Sale";
            values["USER"] = PayPalSettings.Username;
            values["PWD"] = PayPalSettings.Password;
            values["SIGNATURE"] = PayPalSettings.Signature;
            values["VERSION"] = "93";
            values = Submit(values);

            string ack = values["ACK"].ToLower();

            if (ack == "success" || ack == "successwithwarning")
            {
                creditPlanId = creditPlan;
                transctionId = values["PAYMENTINFO_0_TRANSACTIONID"].ToString();
                HttpContext.Current.Session["CurrentSectionPayment"] = null;
                return true;
            }
            else
            {
                HttpContext.Current.Session["CurrentCardId"] = null;
                return false;
            }
        }
    }

    public class PayPalOrder
    {
        public decimal? Amount { get; set; }
        public int CreditPlanId { get; set; }
        public string CreditPlanName { get; set; }
        public string CardId { get; set; }
    }

    public class PayPalRedirect
    {
        public string Url { get; set; }
        public string Token { get; set; }
    }
}
