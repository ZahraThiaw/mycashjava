package sn.odc.flutter.Services.Impl;

import org.springframework.stereotype.Service;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Services.UserService;
import sn.odc.flutter.Web.Dtos.request.RegisterDTO;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {
    private final UserRepository compteRepository;

    public UserServiceImpl(UserRepository compteRepository) {
        super(compteRepository);
        this.compteRepository = compteRepository;
    }

    @Override
    public User createCompte(RegisterDTO dto) {
        User compte = new User();


        return compteRepository.save(compte);
    }
}