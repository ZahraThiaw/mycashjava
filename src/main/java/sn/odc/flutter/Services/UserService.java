package sn.odc.flutter.Services;

import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Web.Dtos.request.RegisterDTO;

public interface UserService extends BaseService<User,Long> {
    // Add your custom methods here
     public User createCompte(RegisterDTO dto);
}
