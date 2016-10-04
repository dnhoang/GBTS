using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(Green_Bus_Ticket_System.Startup))]
namespace Green_Bus_Ticket_System
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
            app.MapSignalR();
        }
    }
}
