package sn.odc.flutter.utils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CardGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CardGenerator.class);

    public static BufferedImage generateCard(BufferedImage qrCode, String logoSvgPath) throws IOException, TranscoderException {
        logger.info("Début de la génération de la carte");

        // Conversion du logo SVG en BufferedImage
        BufferedImage logo = convertSvgToPng(logoSvgPath);

        // Vérification des dimensions de la carte
        int cardWidth = 600;
        int cardHeight = 400;
        BufferedImage cardImage = new BufferedImage(cardWidth, cardHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = cardImage.createGraphics();

        // Appliquer des rendus de qualité
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fond blanc
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, cardWidth, cardHeight);

        // Bordure
        graphics.setColor(new Color(230, 230, 230));
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRect(5, 5, cardWidth - 10, cardHeight - 10);

        // QR code
        int qrWidth = 200;
        int qrHeight = 200;
        int qrX = 50;
        int qrY = 100;
        graphics.drawImage(qrCode, qrX, qrY, qrWidth, qrHeight, null);

        // Logo
        int logoWidth = 100;
        int logoHeight = 50;
        int logoX = cardWidth - logoWidth - 50;
        int logoY = 50;
        graphics.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);

        // Titre
        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        graphics.setColor(Color.BLACK);
        graphics.drawString("Carte d'Utilisateur", cardWidth / 2 - 100, 40);

        // Ligne décorative
        graphics.setColor(new Color(0, 102, 204));
        graphics.setStroke(new BasicStroke(3));
        graphics.drawLine(50, 60, cardWidth - 50, 60);

        graphics.dispose();
        logger.info("Génération de la carte terminée avec succès");
        return cardImage;
    }

    private static BufferedImage convertSvgToPng(String svgPath) throws IOException, TranscoderException {
        logger.info("Conversion du fichier SVG en PNG");

        File svgFile = new File(svgPath);
        if (!svgFile.exists()) {
            throw new IOException("Le fichier SVG n'existe pas: " + svgPath);
        }

        // Conversion du SVG en PNG temporaire
        PNGTranscoder transcoder = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));
        File tempPngFile = File.createTempFile("temp_logo", ".png");
        try (FileOutputStream pngOutputStream = new FileOutputStream(tempPngFile)) {
            TranscoderOutput output = new TranscoderOutput(pngOutputStream);
            transcoder.transcode(input, output);
        }

        // Charger le PNG converti dans BufferedImage
        return ImageIO.read(tempPngFile);
    }
}
