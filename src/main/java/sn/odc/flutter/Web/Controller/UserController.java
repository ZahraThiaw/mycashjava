package sn.odc.flutter.Web.Controller;

import org.springframework.http.ResponseEntity;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;

public interface UserController extends BaseController<User, User> {
    ResponseEntity<GenericResponse<User>> getUserProfile();
}