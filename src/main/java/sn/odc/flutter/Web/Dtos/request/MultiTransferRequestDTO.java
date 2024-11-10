package sn.odc.flutter.Web.Dtos.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class MultiTransferRequestDTO {
    private List<String> destinataires; // Liste des numéros ou identifiants
    private BigDecimal montant;
}