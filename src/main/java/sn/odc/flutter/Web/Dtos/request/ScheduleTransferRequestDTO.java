package sn.odc.flutter.Web.Dtos.request;

import lombok.Data;
import sn.odc.flutter.Datas.Entity.Transaction;

import java.math.BigDecimal;

@Data
public class ScheduleTransferRequestDTO {
    private String destinataire;
    private BigDecimal montant;
    private Transaction.SchedulePeriod period = Transaction.SchedulePeriod.DAILY;  // DAILY, WEEKLY, MONTHLY
}
