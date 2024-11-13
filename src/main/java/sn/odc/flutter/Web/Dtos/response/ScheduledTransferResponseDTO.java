package sn.odc.flutter.Web.Dtos.response;

import lombok.Data;
import sn.odc.flutter.Datas.Entity.ScheduledTransfer;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

// ScheduledTransferResponseDTO.java
@Data
public class ScheduledTransferResponseDTO {
    private Long id;
    private BigDecimal montant;
    private BigDecimal frais;
    private BigDecimal montantTotal;
    private ScheduledTransfer.TypeTransaction type;
    private Date date;
    private UserResponseDTO expediteur;
    private UserResponseDTO destinataire;
    private boolean isScheduleActive;
    private Date nextExecutionDate;
    private Time executionTime;
    private ScheduledTransfer.SchedulePeriod schedulePeriod;
}