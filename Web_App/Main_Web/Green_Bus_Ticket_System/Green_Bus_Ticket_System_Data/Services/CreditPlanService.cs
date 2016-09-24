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
    public interface ICreditPlanService : IEntityService<CreditPlan>
    {
        CreditPlan GetCreditPlan(int id);
    }

    public class CreditPlanService : EntityService<CreditPlan>, ICreditPlanService
    {
        IUnitOfWork _unitOfWork;
        ICreditPlanRepository _repository;

        public CreditPlanService(IUnitOfWork unitOfWork, ICreditPlanRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public CreditPlan GetCreditPlan(int id)
        {
            return _repository.FindBy(obj => obj.Id == id).FirstOrDefault();
        }
    }
}
