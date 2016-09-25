using System.Web.Mvc;

namespace Green_Bus_Ticket_System.Areas.Passenger
{
    public class PassengerAreaRegistration : AreaRegistration 
    {
        public override string AreaName 
        {
            get 
            {
                return "Passenger";
            }
        }

        public override void RegisterArea(AreaRegistrationContext context) 
        {
            context.MapRoute(
                "Passenger_default",
                "Passenger/{controller}/{action}/{id}",
                new { action = "Index", id = UrlParameter.Optional }
            );
        }
    }
}