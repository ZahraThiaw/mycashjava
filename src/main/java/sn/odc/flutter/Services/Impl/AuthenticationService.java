package sn.odc.flutter.Services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Web.Dtos.request.RegisterDTO;
import sn.odc.flutter.Web.Dtos.request.LoginUserDto;
import org.springframework.security.authentication.BadCredentialsException;
import sn.odc.flutter.utils.QRCodeGenerator;
import sn.odc.flutter.utils.CardGenerator;
import sn.odc.flutter.utils.EmailService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.batik.transcoder.TranscoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(RegisterDTO input) {
        logger.info("Début de l'inscription pour l'utilisateur: {}", input.getTelephone());

        try {
            String baseDir = "/home/fatimata/Documents/Java/flutter/src/main/resources/images";
            createDirectoryIfNotExists(baseDir + "/qrcodes");
            createDirectoryIfNotExists(baseDir + "/cartes");

            if (userRepository.findCompteByTelephone(input.getTelephone()).isPresent()) {
                logger.error("Téléphone {} déjà existant", input.getTelephone());
                throw new RuntimeException("Téléphone déjà existant");
            }

            User user = createUser(input);

            logger.info("Génération du QR code");
            String qrData = input.getTelephone() + " " + input.getNom() + " " + input.getPrenom();
            BufferedImage qrCode = QRCodeGenerator.generateQRCode(qrData, 250, 250);
            if (qrCode == null) {
                throw new RuntimeException("Échec de la génération du QR code");
            }

            String qrCodePath = baseDir + "/qrcodes/" + user.getTelephone() + "_qrcode.png";
            File qrCodeFile = new File(qrCodePath);
            logger.info("Sauvegarde du QR code: {}", qrCodePath);
            ImageIO.write(qrCode, "PNG", qrCodeFile);

            String logoPath = baseDir + "/logos/logo.svg";
            try {
                BufferedImage cardImage = CardGenerator.generateCard(qrCode, logoPath);
                if (cardImage == null) {
                    throw new RuntimeException("Échec de la génération de la carte");
                }

                String cardPath = baseDir + "/cartes/" + user.getTelephone() + "_carte.jpg";
                File cardFile = new File(cardPath);
                logger.info("Sauvegarde de la carte: {}", cardPath);
                ImageIO.write(cardImage, "JPEG", cardFile);

                user.setQrcode(qrCodePath);
                userRepository.save(user);

                emailService.sendEmailWithAttachment(
                        user.getEmail(),
                        "Votre carte d'utilisateur",
                        "Veuillez trouver ci-joint votre carte.",
                        cardFile
                );

            } catch (TranscoderException e) {
                logger.error("Erreur lors de la conversion du logo SVG", e);
                throw new RuntimeException("Erreur lors de la conversion du logo SVG", e);
            }

            logger.info("Inscription terminée avec succès");
            return user;

        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription: ", e);
            throw new RuntimeException("Erreur lors de l'inscription de l'utilisateur: " + e.getMessage(), e);
        }
    }

    private void createDirectoryIfNotExists(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            logger.info("Création du répertoire: {}", path);
            if (!directory.mkdirs()) {
                logger.error("Impossible de créer le répertoire: {}", path);
                throw new RuntimeException("Impossible de créer le répertoire: " + path);
            }
        }
    }

    private User createUser(RegisterDTO input) {
        User user = new User();
        user.setEmail(input.getEmail());
        user.setTelephone(input.getTelephone());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setNom(input.getNom());
        user.setPrenom(input.getPrenom());
        user.setType(User.TypeCompte.CLIENT);
        user.setStatut(User.Statut.ACTIF);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        try {
            User user = userRepository.findCompteByTelephone(input.getTelephone())
                    .orElseThrow(() -> new BadCredentialsException("Téléphone ou mot de passe invalide"));

            if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Téléphone ou mot de passe invalide");
            }

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
