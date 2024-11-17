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
import sn.odc.flutter.Services.TransactionService;
import sn.odc.flutter.Services.UserService;
import sn.odc.flutter.Web.Controller.UserController;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;
import sn.odc.flutter.Web.Dtos.response.TransactionResponseDTO;
import sn.odc.flutter.Web.Dtos.response.UserProfileDTO;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequestMapping("/users")
@RestController
public class UserControllerImpl extends BaseControllerImpl<User, User> implements UserController {

    private final UserService userService;
    private final TransactionService transactionService;

    @Autowired
    private HttpServletRequest request;

    public UserControllerImpl(UserService userService, TransactionService transactionService) {
        super(userService);
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @Override
    protected User convertToResponseDTO(User entity) {
        return entity;
    }

    @GetMapping("/profile")
    @Override
    public ResponseEntity<GenericResponse<UserProfileDTO>> getUserProfile() {
        String authHeader = request.getHeader("Authorization");
        System.out.println("getProfile" + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(new GenericResponse<>(null, "Token non fourni"));
        }

        try {
            // Extraire le token sans le prefix "Bearer "
            String token = authHeader.substring(7);
            String[] chunks = token.split("\\.");

            // Decoder le payload
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            // Parser le JSON du payload
            ObjectMapper mapper = new ObjectMapper();
            JsonNode claims = mapper.readTree(payload);

            String username = claims.get("sub").asText();
            System.out.println("username : " + username);

            Optional<User> userOptional = userService.getUserProfile(username);
            if (userOptional.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new GenericResponse<>(null, "Compte non trouvé"));
            }

            User user = userOptional.get();
            // Récupérer les transactions de l'utilisateur
            List<TransactionResponseDTO> transactions = transactionService.getTransactionsByUser(user.getId());

            // Créer l'objet de réponse combiné
            UserProfileDTO profileDTO = new UserProfileDTO(user, transactions);

            return ResponseEntity.ok(new GenericResponse<>(profileDTO, "Profil récupéré avec succès"));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new GenericResponse<>(null, "Token expiré"));
        }
    }
}