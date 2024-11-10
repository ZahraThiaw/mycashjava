package sn.odc.flutter.Datas.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return type != null ?
                List.of(new SimpleGrantedAuthority(type.name())) :
                List.of();
    }

    @Override
    public String getUsername() {
        return telephone;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum Statut {
        ACTIF,
        INACTIF,
        BLOQUE
    }

    public enum TypeCompte {
        CLIENT,
        ADMIN,
        AGENT,
        DISTRIBUTEUR;
    }

    @Enumerated(EnumType.STRING)
    private TypeCompte type = TypeCompte.CLIENT;

    private String nom;

    private String prenom;

    @Column(unique = true, nullable = false)
    private String telephone;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(precision = 10, scale = 2)
    private BigDecimal solde = BigDecimal.ZERO;

    private String qrcode;

    @Enumerated(EnumType.STRING)
    private Statut statut = Statut.ACTIF;

    private int plafonnd = 500000;
}