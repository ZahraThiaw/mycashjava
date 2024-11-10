package sn.odc.flutter.Web.Dtos.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal montant;
    private BigDecimal frais;
    private BigDecimal montantTotal;
    private String type;
    private Date date;
    private UserResponseDTO expediteur;
    private UserResponseDTO destinataire;
    private String status;
}