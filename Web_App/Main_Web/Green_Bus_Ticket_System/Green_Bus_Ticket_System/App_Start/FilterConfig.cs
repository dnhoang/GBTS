using System.Web;
using System.Web.Mvc;

namespace Green_Bus_Ticket_System
{
    public class FilterConfig
    {
        public static void RegisterGlobalFilters(GlobalFilterCollection filters)
        {
            filters.Add(new HandleErrorAttribute());
        }
    }
}
