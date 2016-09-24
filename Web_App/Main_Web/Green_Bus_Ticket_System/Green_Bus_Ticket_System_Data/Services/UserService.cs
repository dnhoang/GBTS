using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Repositories;
using Green_Bus_Ticket_System_Data.UnitOfWork;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Green_Bus_Ticket_System_Data.Services
{
    public interface IUserService : IEntityService<User>
    {
        User GetUser(int id);
        User GetUserByPhone(string phone);
    }

    public class UserService : EntityService<User>, IUserService
    {
        IUnitOfWork _unitOfWork;
        IUserRepository _repository;

        public UserService(IUnitOfWork unitOfWork, IUserRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public User GetUser(int id)
        {
            return _repository.FindBy(u => u.UserId == id).FirstOrDefault();
        }
        public User GetUserByPhone(string phone)
        {
            return _repository.FindBy(u => u.PhoneNumber.Equals(phone)).FirstOrDefault();
        }
    }
}
