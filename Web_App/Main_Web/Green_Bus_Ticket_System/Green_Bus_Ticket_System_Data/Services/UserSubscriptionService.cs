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
    public interface IUserSubscriptionService : IEntityService<UserSubscription>
    {
        UserSubscription GetUserSubscription(int id);
        UserSubscription GetUserSubscriptionByPhone(string phone);
    }

    public class UserSubscriptionService : EntityService<UserSubscription>, IUserSubscriptionService
    {
        IUnitOfWork _unitOfWork;
        IUserSubscriptionRepository _repository;

        public UserSubscriptionService(IUnitOfWork unitOfWork, IUserSubscriptionRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public UserSubscription GetUserSubscription(int id)
        {
            return _repository.FindBy(u => u.Id == id).FirstOrDefault();
        }

        public UserSubscription GetUserSubscriptionByPhone(string phone)
        {
            return _repository.FindBy(u => u.User.PhoneNumber.Equals(phone)).FirstOrDefault();
        }
    
    }

}
