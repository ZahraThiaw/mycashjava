package sn.odc.flutter.Datas.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

@Entity
@Data
@Table(name = "scheduled_transfers")
public class ScheduledTransfer extends BaseEntity {

    @Column(precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(precision = 10, scale = 2)
    private BigDecimal frais;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal;

    public enum TypeTransaction {
        TRANSFERT
    }
    @Enumerated(EnumType.STRING)
    private TypeTransaction type = TypeTransaction.TRANSFERT;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "destinataire")
    private User destinataireUser;

    @ManyToOne
    @JoinColumn(name = "exp")
    private User expUser;

    public enum SchedulePeriod {
        DAILY,
        WEEKLY,
        MONTHLY
    }

    @Column(name = "is_schedule_active")
    private boolean isScheduleActive = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_execution_date")
    private Date nextExecutionDate;

    @Temporal(TemporalType.TIME)
    @Column(name = "execution_time")
    private Time executionTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_period")
    private SchedulePeriod schedulePeriod = SchedulePeriod.DAILY;

    @Column(name = "schedule_active") // Assurez-vous que le nom de la colonne correspond à celui de votre base de données
    private Boolean scheduleActive ;
}