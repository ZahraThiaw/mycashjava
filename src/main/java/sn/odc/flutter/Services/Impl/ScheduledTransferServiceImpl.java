package sn.odc.flutter.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.odc.flutter.Datas.Entity.ScheduledTransfer;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.ScheduledTransferRepository;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Exceptions.ResourceNotFoundException;
import sn.odc.flutter.Exceptions.UnauthorizedException;
import sn.odc.flutter.Services.ScheduledTransferService;
import sn.odc.flutter.Services.TransactionService;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.TransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.ScheduledTransferResponseDTO;
import sn.odc.flutter.Web.Dtos.response.UserResponseDTO;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j@Service
public class ScheduledTransferServiceImpl extends BaseServiceImpl<ScheduledTransfer, Long> implements ScheduledTransferService {

    private final ScheduledTransferRepository scheduledTransferRepository;
    private final UserRepository userRepository;
    private final TransactionService transferService;

    // Constructor for dependency injection
    public ScheduledTransferServiceImpl(
            ScheduledTransferRepository scheduledTransferRepository,
            UserRepository userRepository,
            TransactionService transferService) {
        super(scheduledTransferRepository); // Pass repository to BaseServiceImpl
        this.scheduledTransferRepository = scheduledTransferRepository;
        this.userRepository = userRepository;
        this.transferService = transferService;
    }

    @Override
    @Transactional
    public ScheduledTransferResponseDTO planifierTransfert(Long expediteurId, ScheduledTransferRequestDTO request) {
        User expediteur = userRepository.findByIdAndDeletedFalse(expediteurId)
                .orElseThrow(() -> new ResourceNotFoundException("Expéditeur non trouvé"));

        User destinataire = userRepository.findByTelephoneAndDeletedFalse(request.getDestinataire())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire non trouvé"));

        validateTransferAmount(request.getMontant());

        ScheduledTransfer planification = createScheduledTransfer(expediteur, destinataire, request);
        return convertToDTO(scheduledTransferRepository.save(planification));
    }

    private ScheduledTransfer createScheduledTransfer(User expediteur, User destinataire, ScheduledTransferRequestDTO request) {
        ScheduledTransfer planification = new ScheduledTransfer();
        planification.setMontant(request.getMontant());
        planification.setFrais(calculerFrais(request.getMontant()));
        planification.setMontantTotal(request.getMontant().add(calculerFrais(request.getMontant())));
        planification.setType(ScheduledTransfer.TypeTransaction.TRANSFERT);
        planification.setDate(new Date());
        planification.setExpUser(expediteur);
        planification.setDestinataireUser(destinataire);
        planification.setExecutionTime(request.getExecutionTime());
        planification.setSchedulePeriod(request.getPeriod());
        planification.setNextExecutionDate(calculateNextExecutionDate(new Date(), request.getPeriod()));
        planification.setScheduleActive(true);
        return planification;
    }

    @Override
    @Transactional
    public boolean annulerPlanification(Long transferId, Long userId) {
        ScheduledTransfer planification = scheduledTransferRepository.findByIdAndDeletedFalse(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Planification non trouvée"));

        if (!planification.getExpUser().getId().equals(userId)) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à annuler cette planification");
        }

        if (!planification.isScheduleActive()) {
            throw new IllegalStateException("Cette planification est déjà inactive");
        }

        planification.setScheduleActive(false);
        scheduledTransferRepository.save(planification);
        return true;
    }

    @Override
    public List<ScheduledTransferResponseDTO> getScheduledTransfers(Long userId) {
        return scheduledTransferRepository
                .findByExpUser_IdAndScheduleActiveTrueOrderByDateDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void executeScheduledTransfers() {
        Date now = new Date();
        List<ScheduledTransfer> scheduledTransfers = scheduledTransferRepository
                .findByScheduleActiveTrueAndNextExecutionDateBefore(now);

        for (ScheduledTransfer planification : scheduledTransfers) {
            try {
                if (isExecutionTimeReached(planification, now)) {
                    executeScheduledTransfer(planification);
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'exécution du transfert planifié " + planification.getId(), e);
            }
        }
    }

    private boolean isExecutionTimeReached(ScheduledTransfer planification, Date now) {
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);

        Calendar execCal = Calendar.getInstance();
        execCal.setTime(planification.getExecutionTime());

        return nowCal.get(Calendar.HOUR_OF_DAY) >= execCal.get(Calendar.HOUR_OF_DAY) &&
                nowCal.get(Calendar.MINUTE) >= execCal.get(Calendar.MINUTE);
    }


    @Transactional
    public void executeScheduledTransfer(ScheduledTransfer planification) {
        try {
            TransferRequestDTO request = new TransferRequestDTO();
            request.setDestinataire(planification.getDestinataireUser().getTelephone());
            request.setMontant(planification.getMontant());

            transferService.effectuerTransfert(planification.getExpUser().getId(), request);

            updateNextExecutionDate(planification);
        } catch (Exception e) {
            log.error("Échec de l'exécution du transfert planifié " + planification.getId(), e);
            throw new RuntimeException("Échec de l'exécution du transfert planifié", e);
        }
    }

    private void updateNextExecutionDate(ScheduledTransfer planification) {
        planification.setNextExecutionDate(
                calculateNextExecutionDate(new Date(), planification.getSchedulePeriod())
        );
        scheduledTransferRepository.save(planification);
    }

    private Date calculateNextExecutionDate(Date fromDate, ScheduledTransfer.SchedulePeriod period) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        switch (period) {
            case DAILY -> calendar.add(Calendar.DAY_OF_MONTH, 1);
            case WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1);
            case MONTHLY -> calendar.add(Calendar.MONTH, 1);
            default -> throw new IllegalArgumentException("Période non supportée: " + period);
        }

        return calendar.getTime();
    }

    @Override
    public ScheduledTransferResponseDTO convertToDTO(ScheduledTransfer scheduledTransfer) {
        if (scheduledTransfer == null) {
            return null;
        }

        ScheduledTransferResponseDTO dto = new ScheduledTransferResponseDTO();
        dto.setId(scheduledTransfer.getId());
        dto.setMontant(scheduledTransfer.getMontant());
        dto.setFrais(scheduledTransfer.getFrais());
        dto.setMontantTotal(scheduledTransfer.getMontantTotal());
        dto.setType(scheduledTransfer.getType());
        dto.setDate(scheduledTransfer.getDate());
        dto.setExpediteur(convertUserToDTO(scheduledTransfer.getExpUser()));
        dto.setDestinataire(convertUserToDTO(scheduledTransfer.getDestinataireUser()));
        dto.setScheduleActive(scheduledTransfer.isScheduleActive());
        dto.setSchedulePeriod(scheduledTransfer.getSchedulePeriod());
        dto.setExecutionTime(scheduledTransfer.getExecutionTime());
        dto.setNextExecutionDate(scheduledTransfer.getNextExecutionDate());

        return dto;
    }

    private UserResponseDTO convertUserToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setTelephone(user.getTelephone());
        return dto;
    }

    private BigDecimal calculerFrais(BigDecimal montant) {
        return montant.multiply(new BigDecimal("0.02"));
    }

    private void validateTransferAmount(BigDecimal montant) {
        if (montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }
    }
}
