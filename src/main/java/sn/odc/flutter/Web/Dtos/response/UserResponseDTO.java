package sn.odc.flutter.Web.Dtos.response;

import lombok.Data;
import sn.odc.flutter.Datas.Entity.User;

import java.math.BigDecimal;

@Data
public class UserResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private BigDecimal solde;
    private String role;
    private User.TypeCompte type;
    private User.Statut statut;
    private String qrcode;
}
