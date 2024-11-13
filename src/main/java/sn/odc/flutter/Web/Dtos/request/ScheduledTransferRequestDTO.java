package sn.odc.flutter.Web.Dtos.request;

import lombok.Data;
import sn.odc.flutter.Datas.Entity.ScheduledTransfer;

import java.math.BigDecimal;
import java.sql.Time;

@Data
public class ScheduledTransferRequestDTO {
    private String destinataire;
    private BigDecimal montant;
    private ScheduledTransfer.SchedulePeriod period = ScheduledTransfer.SchedulePeriod.DAILY;
    private Time executionTime;
}
