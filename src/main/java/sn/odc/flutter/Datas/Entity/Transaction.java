package sn.odc.flutter.Datas.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(precision = 10, scale = 2)
    private BigDecimal frais;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantTotal;

    public enum TypeTransaction {
        TRANSFERT,
        PAIEMENT,
        DEPOT,
        RETRAIT,
    }
    @Enumerated(EnumType.STRING)
    private TypeTransaction type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "agent")
    private User agentUser;

    @ManyToOne
    @JoinColumn(name = "destinataire")
    private User destinataireUser;

    @ManyToOne
    @JoinColumn(name = "exp")
    private User expUser;

    @ManyToOne
    @JoinColumn(name = "distibuteur")
    private User distributeurUser;

    public enum SchedulePeriod {
        NONE,    // Pour les transferts normaux
        DAILY,
        WEEKLY,
        MONTHLY
    }

    private Long parentTransactionId;  // Pour lier les transferts planifiés à leur transaction parent

    // Méthode utilitaire pour vérifier si c'est un transfert planifié
    public boolean isScheduledTransfer() {
        return schedulePeriod != SchedulePeriod.NONE;
    }

    @Column(name = "is_schedule_active")
    private boolean isScheduleActive = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_execution_date")
    private Date nextExecutionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_period")
    private SchedulePeriod schedulePeriod = SchedulePeriod.NONE;


}

