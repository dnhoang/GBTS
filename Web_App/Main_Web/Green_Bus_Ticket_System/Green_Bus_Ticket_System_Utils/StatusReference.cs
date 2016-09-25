using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Utils
{
    public static class StatusReference
    {
        public enum CardStatus:int
        {
            NON_ACTIVATED = 0,
            ACTIVATED = 1,
            BLOCKED = 2
        };

        public enum RoleID : int
        {
            ADMIN = 0,
            MANAGER = 1,
            STAFF = 2,
            PASSENGER = 3
        };

        public enum UserStatus : int
        {
            DEACTIVATED = 0,
            ACTIVATED = 1,
        };
    }
}
