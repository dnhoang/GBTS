using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public static class CommonUtils
    {
        public static string GetCurrentRate()
        {
            try
            {
                string endPoint = @"http://free.currencyconverterapi.com/api/v3/convert";
                var client = new RestClient(endPoint);
                var json = client.MakeRequest("?q=USD_VND");

                return json;
            }
            catch(Exception e)
            {
                return null;
            }
            
        }
        public static string VietnamingPhone(string globalPhone)
        {
            if (globalPhone.Contains("+84"))
                return globalPhone.Replace("+84", "0");

            return globalPhone;
        }
        public static string GlobalingingPhone(string vietnamPhone)
        {
            if (vietnamPhone.StartsWith("0"))
                return "+84" + vietnamPhone.Substring(1);

            return vietnamPhone;
        }

        public static string HashPassword(string input)
        {
            MD5 md5Hash = MD5.Create();
            // Convert the input string to a byte array and compute the hash.
            byte[] data = md5Hash.ComputeHash(Encoding.UTF8.GetBytes(input));

            // Create a new Stringbuilder to collect the bytes
            // and create a string.
            StringBuilder sBuilder = new StringBuilder();

            // Loop through each byte of the hashed data 
            // and format each one as a hexadecimal string.
            for (int i = 0; i < data.Length; i++)
            {
                sBuilder.Append(data[i].ToString("x2"));
            }

            // Return the hexadecimal string.
            return sBuilder.ToString();
        }

        public static string GeneratePassword(int length)
        {
            const string valid = "123456789012345678901234567890123456789012345678901234567890";
            StringBuilder res = new StringBuilder();
            Random rnd = new Random();
            while (0 < length--)
            {
                res.Append(valid[rnd.Next(valid.Length)]);
            }
            return res.ToString();
        }

        public static string GeneratePre(int length)
        {
            const string valid = "QƯERTYUIOPASDFGHJKLZXCVBNM";
            StringBuilder res = new StringBuilder();
            Random rnd = new Random();
            while (0 < length--)
            {
                res.Append(valid[rnd.Next(valid.Length)]);
            }
            return res.ToString();
        }


    }
}
