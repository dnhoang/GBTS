using Green_Bus_Ticket_System_Utils;
using Microsoft.WindowsAzure.Storage;
using Microsoft.WindowsAzure.Storage.Queue;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;
using log4net;
using System.Net;
using System.IO;

namespace AutoHandler
{
    class Program
    {
        static string key = ConfigurationManager.AppSettings["FireBaseKey"];
        static string senderId = ConfigurationManager.AppSettings["FireBaseSender"];

        private static readonly ILog log = LogManager.GetLogger("AutoLog");
        static void Main(string[] args)
        {
            MainAsync().Wait();
        }

        static async Task MainAsync()
        {
            //var task1 = SendNotification();
            //var task2 = SendNotificationx();

            //await Task.WhenAll(task1, task2);

            await SendBalanceNotification();

        }

        static async Task SendBalanceNotification()
        {
            string storageConn = ConfigurationManager.AppSettings["StorageConnection"];

            while (true)
            {
                CloudStorageAccount account = CloudStorageAccount.Parse(storageConn);

                CloudQueueClient client = account.CreateCloudQueueClient();
                CloudQueue queue = client.GetQueueReference("gbtscardbalance");
                queue.CreateIfNotExists();
                queue.FetchAttributes();

                if (queue.ApproximateMessageCount > 0)
                {
                    CloudQueueMessage message = queue.GetMessage();
                    if (message != null)
                    {
                        string token = message.AsString;

                        string title = "Green Bus";
                        string notification = "Thẻ của bạn sắp hết tiền, vui lòng nạp thêm.";

                        SendToFireBase(token, title, notification);

                        Console.WriteLine("Send message to " + token);
                        queue.DeleteMessage(message);
                    }
                }
                else
                {
                    Console.WriteLine("No message...");
                }
                await Task.Delay(2000);
            }
        }

        static void SendToFireBase(string token, string title, string message)
        {
            WebRequest tRequest;
            tRequest = WebRequest.Create("https://fcm.googleapis.com/fcm/send");
            tRequest.Method = "POST";
            tRequest.UseDefaultCredentials = true;

            tRequest.PreAuthenticate = true;

            tRequest.Credentials = CredentialCache.DefaultNetworkCredentials;

            tRequest.ContentType = "application/json";
            tRequest.Headers.Add(string.Format("Authorization: key={0}", key));
            tRequest.Headers.Add(string.Format("Sender: id={0}", senderId));


            string RegArr = token;

            string postData = "{ \"registration_ids\": [ \"" + RegArr + "\" ],\"data\": {\"message\": \"" + message + "\",\"body\": \"" + message + "\",\"title\": \"" + title + "\",\"collapse_key\":\"" + message + "\"}}";

            Byte[] byteArray = Encoding.UTF8.GetBytes(postData);
            tRequest.ContentLength = byteArray.Length;

            Stream dataStream = tRequest.GetRequestStream();
            dataStream.Write(byteArray, 0, byteArray.Length);
            dataStream.Close();

            WebResponse tResponse = tRequest.GetResponse();

            dataStream = tResponse.GetResponseStream();

            StreamReader tReader = new StreamReader(dataStream);

            String sResponseFromServer = tReader.ReadToEnd();

            log.Info(sResponseFromServer); //Lấy thông báo kết quả từ FCM server.
            tReader.Close();
            dataStream.Close();
            tResponse.Close();
        }

        
    }
}
