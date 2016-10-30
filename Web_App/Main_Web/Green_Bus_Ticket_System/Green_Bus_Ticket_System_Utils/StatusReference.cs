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
            ADMIN = 1,
            MANAGER = 2,
            STAFF = 3,
            PASSENGER = 4
        };

        public enum UserStatus : int
        {
            DEACTIVATED = 0,
            ACTIVATED = 1,
        };

        public enum TicketTypeStatus : int
        {
            DEACTIVATED = 0,
            ACTIVATED = 1,
        };

        public enum CreditPlansStatus : int
        {
            DEACTIVATED = 0,
            ACTIVATED = 1,
        };

        public enum ScratchCardStatus : int
        {
            USED = 0,
            AVAILABLE = 1,
        };

        public enum PromotionStatus : int
        {
            SENDING = 0,
            SENT = 1,
            CANCELLED = 2,
        };
    }
}
