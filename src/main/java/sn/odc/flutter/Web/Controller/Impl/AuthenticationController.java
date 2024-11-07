package sn.odc.flutter.Web.Controller.Impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Services.Impl.AuthenticationService;
import sn.odc.flutter.Services.Impl.JwtService;
import sn.odc.flutter.Web.Dtos.request.RegisterDTO;
import sn.odc.flutter.Web.Dtos.request.LoginUserDto;
import sn.odc.flutter.Web.Dtos.response.LoginResponse;

@RequestMapping("/auth")
@RestController
@Tag(name = "Auth ", description = "API pour gérer les users")

public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterDTO registerUserDto) {
        User registeredCompte = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredCompte);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);

        return ResponseEntity.ok(loginResponse);
    }
}