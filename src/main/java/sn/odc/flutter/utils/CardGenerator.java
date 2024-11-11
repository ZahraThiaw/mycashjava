package sn.odc.flutter.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.geom.RoundRectangle2D;

public class CardGenerator {
    private static final Logger logger = LoggerFactory.getLogger(CardGenerator.class);

    // Constantes pour la personnalisation de la carte
    private static final int CARD_WIDTH = 600;
    private static final int CARD_HEIGHT = 400;
    private static final int CORNER_RADIUS = 20;
    private static final float GRADIENT_OPACITY = 0.85f;

    public static BufferedImage generateCard(BufferedImage qrCode, String logoPngPath) throws IOException {
        logger.info("Début de la génération de la carte avec le nouveau design");

        // Chargement du logo
        BufferedImage logo = ImageIO.read(new File(logoPngPath));

        // Extraction de la couleur dominante du logo
        Color dominantColor = extractDominantColor(logo);
        Color secondaryColor = getDarkerColor(dominantColor, 0.7f);

        // Création de l'image de la carte
        BufferedImage cardImage = new BufferedImage(CARD_WIDTH, CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = cardImage.createGraphics();

        // Configuration de la qualité du rendu
        configureGraphicsQuality(graphics);

        // Dessiner le fond avec gradient
        drawBackground(graphics, dominantColor, secondaryColor);

        // Ajouter des éléments décoratifs
        drawDecorations(graphics, dominantColor);

        // Placer le QR code avec un fond blanc et une ombre
        drawCenteredQRCode(graphics, qrCode);

        // Placer le logo
        drawLogo(graphics, logo);

        // Ajouter le texte
        drawText(graphics);

        graphics.dispose();
        logger.info("Génération de la carte terminée avec le nouveau design");
        return cardImage;
    }

    private static void configureGraphicsQuality(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private static void drawBackground(Graphics2D graphics, Color primaryColor, Color secondaryColor) {
        // Créer un gradient pour le fond
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(),
                (int)(255 * GRADIENT_OPACITY)),
                CARD_WIDTH, CARD_HEIGHT, new Color(secondaryColor.getRed(), secondaryColor.getGreen(),
                secondaryColor.getBlue(), (int)(255 * GRADIENT_OPACITY))
        );

        // Dessiner le fond arrondi
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, CARD_WIDTH, CARD_HEIGHT,
                CORNER_RADIUS, CORNER_RADIUS);
        graphics.setPaint(gradient);
        graphics.fill(roundedRectangle);
    }

    private static void drawDecorations(Graphics2D graphics, Color baseColor) {
        // Motif géométrique en arrière-plan
        graphics.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 30));
        for (int i = 0; i < CARD_WIDTH; i += 40) {
            graphics.drawLine(i, 0, i + 100, CARD_HEIGHT);
        }

        // Cercles décoratifs
        graphics.setColor(new Color(255, 255, 255, 30));
        graphics.fillOval(-50, -50, 200, 200);
        graphics.fillOval(CARD_WIDTH - 100, CARD_HEIGHT - 100, 150, 150);
    }

    private static void drawCenteredQRCode(Graphics2D graphics, BufferedImage qrCode) {
        int qrWidth = 180;
        int qrHeight = 180;
        int qrX = (CARD_WIDTH - qrWidth) / 2;
        int qrY = (CARD_HEIGHT - qrHeight) / 2;

        // Fond blanc pour le QR code
        graphics.setColor(Color.WHITE);
        graphics.fillRoundRect(qrX - 10, qrY - 10, qrWidth + 20, qrHeight + 20, 10, 10);

        // Dessiner le QR code
        graphics.drawImage(qrCode, qrX, qrY, qrWidth, qrHeight, null);
    }

    private static void drawLogo(Graphics2D graphics, BufferedImage logo) {
        int logoWidth = 120;
        int logoHeight = 60;
        int logoX = CARD_WIDTH - logoWidth - 50;
        int logoY = 40;

        graphics.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);
    }

    private static void drawText(Graphics2D graphics) {
        // Configuration de la police
        Font titleFont = new Font("Arial", Font.BOLD, 28);
        Font subtitleFont = new Font("Arial", Font.PLAIN, 16);

        // Dessiner le titre
        graphics.setColor(Color.WHITE);
        graphics.setFont(titleFont);
        graphics.drawString("MoneyFlow", CARD_WIDTH / 2 - 120, 50);

        // Sous-titre
        graphics.setFont(subtitleFont);
        graphics.setColor(new Color(240, 240, 240));
        graphics.drawString("Scanner le QR code", CARD_WIDTH / 2 - 75, CARD_HEIGHT - 40);
    }

    private static Color extractDominantColor(BufferedImage image) {
        long sumR = 0, sumG = 0, sumB = 0;
        int totalPixels = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color pixelColor = new Color(image.getRGB(x, y), true);
                if (pixelColor.getAlpha() > 128) {  // Ignorer les pixels transparents
                    sumR += pixelColor.getRed();
                    sumG += pixelColor.getGreen();
                    sumB += pixelColor.getBlue();
                    totalPixels++;
                }
            }
        }

        if (totalPixels == 0) return new Color(0, 102, 204); // Couleur par défaut

        return new Color(
                (int)(sumR / totalPixels),
                (int)(sumG / totalPixels),
                (int)(sumB / totalPixels)
        );
    }

    private static Color getDarkerColor(Color color, float factor) {
        return new Color(
                Math.max((int)(color.getRed() * factor), 0),
                Math.max((int)(color.getGreen() * factor), 0),
                Math.max((int)(color.getBlue() * factor), 0)
        );
    }
}