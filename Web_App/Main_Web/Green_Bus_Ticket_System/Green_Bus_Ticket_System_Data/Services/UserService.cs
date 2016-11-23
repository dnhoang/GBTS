using Green_Bus_Ticket_System_Data.Model;
using Green_Bus_Ticket_System_Data.Repositories;
using Green_Bus_Ticket_System_Data.UnitOfWork;
using Green_Bus_Ticket_System_Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Configuration;

namespace Green_Bus_Ticket_System_Data.Services
{
    public interface IUserService : IEntityService<User>
    {
        User GetUser(int id);
        bool IsUserExist(string phone);
        User GetUserByPhone(string phone);
        User AddUser(string phone, string name, string password, int roleId);

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

        public bool IsUserExist(string phone)
        {
            User user = GetUserByPhone(phone);
            return user != null;
        }
        public User GetUserByPhone(string phone)
        {
            return _repository.FindBy(u => u.PhoneNumber.Equals(phone)).FirstOrDefault();
        }

        public User AddUser(string phone, string name, string password, int roleId)
        {
            User user = new User();
            user.PhoneNumber = phone;
            user.Fullname = name;
            user.Status = (int)StatusReference.UserStatus.ACTIVATED;
            user.RoleId = roleId;
            user.Password = CommonUtils.HashPassword(password);
            user.MinBalance = Int32.Parse(ConfigurationManager.AppSettings["AlertBalance"]);
            _repository.Add(user);
            return user;
        }
    }
}
