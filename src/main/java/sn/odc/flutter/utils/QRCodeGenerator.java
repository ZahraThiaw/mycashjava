package sn.odc.flutter.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {

    public static BufferedImage generateQRCode(String data, int width, int height) {
        try {
            Map<EncodeHintType, Object> hintMap = new HashMap<>();
            hintMap.put(EncodeHintType.MARGIN, 1);  // Ajuster la marge du QR code

            // Créer une matrice de QR code
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height, hintMap);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (bitMatrix.get(i, j)) {
                        image.setRGB(i, j, Color.BLACK.getRGB());
                    } else {
                        image.setRGB(i, j, Color.WHITE.getRGB());
                    }
                }
            }

            return image;
        } catch (WriterException e) {
            // Gérer l'exception, par exemple en la loguant
            System.err.println("Erreur lors de la génération du QR code : " + e.getMessage());
            return null;  // Retourner null ou gérer autrement
        }
    }

}
