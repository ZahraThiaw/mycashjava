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
    private Long id;
    private User.TypeCompte type = User.TypeCompte.CLIENT;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String password;
    private User.Statut statut = User.Statut.ACTIF;
}