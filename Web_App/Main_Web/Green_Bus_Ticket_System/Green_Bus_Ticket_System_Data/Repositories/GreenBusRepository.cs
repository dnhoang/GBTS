using Green_Bus_Ticket_System_Data.Model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Data.Repositories
{
    public interface IUserRepository : IGenericRepository<User> { }
    public class GreenBusRepository : GenericRepository<User>, IUserRepository
    {
        public GreenBusRepository(GreenBusEntities context) : base(context) { }
    }

    public interface IRoleRepository : IGenericRepository<Role> { }
    public class RoleRepository : GenericRepository<Role>, IRoleRepository
    {
        public RoleRepository(GreenBusEntities context) : base(context) { }
    }

    public interface ITicketRepository : IGenericRepository<Ticket> { }
    public class TicketRepository : GenericRepository<Ticket>, ITicketRepository
    {
        public TicketRepository(GreenBusEntities context) : base(context) { }
    }

    public interface ITicketTypeRepository : IGenericRepository<TicketType> { }
    public class TicketTypeRepository : GenericRepository<TicketType>, ITicketTypeRepository
    {
        public TicketTypeRepository(GreenBusEntities context) : base(context) { }
    }

    public interface IPromotionRepository : IGenericRepository<Promotion> { }
    public class PromotionRepository : GenericRepository<Promotion>, IPromotionRepository
    {
        public PromotionRepository(GreenBusEntities context) : base(context) { }
    }

    public interface IPaymentTransactionRepository : IGenericRepository<PaymentTransaction> { }
    public class PaymentTransactionRepository : GenericRepository<PaymentTransaction>, IPaymentTransactionRepository
    {
        public PaymentTransactionRepository(GreenBusEntities context) : base(context) { }
    }

    public interface ICreditPlanRepository : IGenericRepository<CreditPlan> { }
    public class CreditPlanRepository : GenericRepository<CreditPlan>, ICreditPlanRepository
    {
        public CreditPlanRepository(GreenBusEntities context) : base(context) { }
    }
    public interface ICardRepository : IGenericRepository<Card> { }
    public class CardRepository : GenericRepository<Card>, ICardRepository
    {
        public CardRepository(GreenBusEntities context) : base(context) { }
    }

    public interface IBusRouteRepository : IGenericRepository<BusRoute> { }
    public class BusRouteRepository : GenericRepository<BusRoute>, IBusRouteRepository
    {
        public BusRouteRepository(GreenBusEntities context) : base(context) { }
    }

    public interface IScratchCardRepository : IGenericRepository<ScratchCard> { }
    public class ScratchCardRepository : GenericRepository<ScratchCard>, IScratchCardRepository
    {
        public ScratchCardRepository(GreenBusEntities context) : base(context) { }
    }
}
