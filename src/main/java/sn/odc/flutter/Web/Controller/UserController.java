// UserController.java
package sn.odc.flutter.Web.Controller;

import org.springframework.http.ResponseEntity;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;
import sn.odc.flutter.Web.Dtos.response.UserProfileDTO;

public interface UserController extends BaseController<User, User> {
    ResponseEntity<GenericResponse<UserProfileDTO>> getUserProfile();
}