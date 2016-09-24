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
    public interface IPaymentTransactionService : IEntityService<PaymentTransaction>
    {
        PaymentTransaction GetPaymentTransaction(int id);
    }

    public class PaymentTransactionService : EntityService<PaymentTransaction>, IPaymentTransactionService
    {
        IUnitOfWork _unitOfWork;
        IPaymentTransactionRepository _repository;

        public PaymentTransactionService(IUnitOfWork unitOfWork, IPaymentTransactionRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public PaymentTransaction GetPaymentTransaction(int id)
        {
            return _repository.FindBy(obj => obj.Id == id).FirstOrDefault();
        }
    }
}
