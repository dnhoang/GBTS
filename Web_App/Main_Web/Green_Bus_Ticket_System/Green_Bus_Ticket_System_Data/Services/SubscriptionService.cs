using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Repositories;
using Green_Bus_Ticket_System_Data.UnitOfWork;
using Green_Bus_Ticket_System_Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Data.Services
{
    public interface ISubscriptionService : IEntityService<Subscription>
    {
        Subscription GetOfferSubscription(int id);
        Subscription GetOfferSubscriptionByCode(string code);

    }

    public class SubscriptionService : EntityService<Subscription>, ISubscriptionService
    {
        IUnitOfWork _unitOfWork;
        ISubscriptionRepository _repository;

        public SubscriptionService(IUnitOfWork unitOfWork, ISubscriptionRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public Subscription GetOfferSubscription(int id)
        {
            return _repository.FindBy(u => u.Id == id).FirstOrDefault();
        }

        public Subscription GetOfferSubscriptionByCode(string code)
        {
            return _repository.FindBy(u => u.Code.ToLower().Equals(code.ToLower())).FirstOrDefault();
        }
    }
}
