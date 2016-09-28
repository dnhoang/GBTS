using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public class JsonResponse<T>
    {
        public bool success { get; set; }
        public string message { get; set; }
        public IEnumerable<T> data { get; set; }

        public JsonResponse()
        {

        }

        public JsonResponse(bool success, string message, IEnumerable<T> data)
        {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }
}
