package sn.odc.flutter.Web.Dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.odc.flutter.Datas.Entity.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String password;
}