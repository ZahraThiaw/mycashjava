package sn.odc.flutter.Services.Impl;

import org.springframework.stereotype.Service;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Services.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, JwtService jwtService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public Optional <User> getUserProfile(String telephone ) {
        return userRepository.findByTelephoneAndDeletedFalse(telephone);
    }
}