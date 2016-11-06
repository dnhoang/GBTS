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
    public interface ITokenService : IEntityService<Token>
    {
        Token GetToken(string key);
    }

    public class TokenService : EntityService<Token>, ITokenService
    {
        IUnitOfWork _unitOfWork;
        ITokenRepository _repository;

        public TokenService(IUnitOfWork unitOfWork, ITokenRepository repository) : base(unitOfWork, repository)
        {
            _unitOfWork = unitOfWork;
            _repository = repository;
        }

        public Token GetToken(string key)
        {
            return _repository.FindBy(obj => obj.TheKey.Equals(key)).FirstOrDefault();
        }
    }
}
