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
    public interface IOfferSubscriptionService : IEntityService<OfferSubscription>
    {
        OfferSubscription GetOfferSubscription(int id);
        OfferSubscription GetOfferSubscriptionByCode(string code);

    }

    public class OfferSubscriptionService : EntityService<OfferSubscription>, IOfferSubscriptionService
    {
        IUnitOfWork _unitOfWork;
        IOfferSubscriptionRepository _repository;

        public OfferSubscriptionService(IUnitOfWork unitOfWork, IOfferSubscriptionRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public OfferSubscription GetOfferSubscription(int id)
        {
            return _repository.FindBy(u => u.Id == id).FirstOrDefault();
        }

        public OfferSubscription GetOfferSubscriptionByCode(string code)
        {
            return _repository.FindBy(u => u.Code.ToLower().Equals(code.ToLower())).FirstOrDefault();
        }
    }
}
