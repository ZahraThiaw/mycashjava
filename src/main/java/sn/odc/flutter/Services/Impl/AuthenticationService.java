package sn.odc.flutter.Services.Impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Web.Dtos.request.RegisterDTO;
import sn.odc.flutter.Web.Dtos.request.LoginUserDto;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterDTO input) {
        // Vérifier si le téléphone existe déjà
        if (userRepository.findCompteByTelephone(input.getTelephone()).isPresent()) {
            throw new RuntimeException("Téléphone déjà existant");
        }

        User user = new User();
        user.setEmail(input.getEmail());
        user.setTelephone(input.getTelephone());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setNom(input.getNom());
        user.setPrenom(input.getPrenom());
        user.setType(input.getType());
        user.setStatut(input.getStatut());

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        try {
            // Vérifier si le numéro de téléphone existe
            User user = userRepository.findCompteByTelephone(input.getTelephone())
                    .orElseThrow(() -> new BadCredentialsException("Téléphone ou mot de passe invalide"));

            // Vérifier si le mot de passe correspond
            if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Téléphone ou mot de passe invalide");
            }

            // Authentifier (vérifiez que AuthenticationManager est configuré pour utiliser le téléphone)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getTelephone(),
                            input.getPassword()
                    )
            );

            return user;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Échec d'authentification : " + e.getMessage());
        }
    }
}