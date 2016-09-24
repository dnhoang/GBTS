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
    public interface IPromotionService : IEntityService<Promotion>
    {
        Promotion GetPromotion(int id);
    }

    public class PromotionService : EntityService<Promotion>, IPromotionService
    {
        IUnitOfWork _unitOfWork;
        IPromotionRepository _repository;

        public PromotionService(IUnitOfWork unitOfWork, IPromotionRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public Promotion GetPromotion(int id)
        {
            return _repository.FindBy(obj => obj.Id == id).FirstOrDefault();
        }
    }
}
