package sn.odc.flutter.Web.Controller.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Services.UserService;
import sn.odc.flutter.Web.Controller.UserController;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;

import java.util.Base64;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserControllerImpl extends BaseControllerImpl<User, User> implements UserController {

    private final UserService userService;
    @Autowired
    private HttpServletRequest request;

    public UserControllerImpl(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @Override
    protected User convertToResponseDTO(User entity) {
        return entity;
    }

    @GetMapping("/profile")
    @Override
    public ResponseEntity<GenericResponse<User>> getUserProfile() {
        String authHeader = request.getHeader("Authorization");
        System.out.println("getProfile" + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(new GenericResponse(null, "Token non fourni"));
        }

        // Extraire le token sans le prefix "Bearer "
        String token = authHeader.substring(7);
        System.out.println("Token: " + token);

        try {
            // 2. Extraire et décoder le token
            String token2 = authHeader.substring(7);
            String[] chunks = token.split("\\.");

            // 3. Decoder le payload (deuxième partie du token)
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            // 4. Parser le JSON du payload
            ObjectMapper mapper = new ObjectMapper();
            JsonNode claims = mapper.readTree(payload);

            // 5. Extraire les informations
            String username = claims.get("sub").asText();  // sub est le claim standard pour username
            System.out.println("username : " + username);

            Optional<User> compte = userService.getUserProfile(username);
            if (compte == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new GenericResponse(null, "Compte non trouvé"));
            }

            return ResponseEntity.ok(new GenericResponse( compte,  "Profil recupere avec succes"));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new GenericResponse(null, "Token expiré"));
        }
    }
}