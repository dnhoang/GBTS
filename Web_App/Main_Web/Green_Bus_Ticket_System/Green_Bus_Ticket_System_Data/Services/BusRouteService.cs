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
    public interface IBusRouteService : IEntityService<BusRoute>
    {
        BusRoute GetBusRoute(int id);
    }

    public class BusRouteService : EntityService<BusRoute>, IBusRouteService
    {
        IUnitOfWork _unitOfWork;
        IBusRouteRepository _repository;

        public BusRouteService(IUnitOfWork unitOfWork, IBusRouteRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public BusRoute GetBusRoute(int id)
        {
            return _repository.FindBy(obj => obj.Id == id).FirstOrDefault();
        }
    }
}
