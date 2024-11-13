package sn.odc.flutter.Services;

import sn.odc.flutter.Datas.Entity.User;

import java.util.Optional;

public interface UserService extends BaseService<User, Long> {
    Optional<User> getUserProfile(String telephone);
}