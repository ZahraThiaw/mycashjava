package sn.odc.flutter.Web.Dtos.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class TransferRequestDTO {
    private String destinataire; // Numéro de téléphone ou identifiant
    private BigDecimal montant;
}
