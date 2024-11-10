package sn.odc.flutter.Datas.Repository.Interfaces;

import org.springframework.stereotype.Repository;
import sn.odc.flutter.Datas.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseInterface<User,Long> {
   Optional<User> findCompteByTelephone(String telephone);
   Optional<User> findCompteByEmail(String email);
   Optional<User> findByTelephoneAndDeletedFalse(String telephone);
}
