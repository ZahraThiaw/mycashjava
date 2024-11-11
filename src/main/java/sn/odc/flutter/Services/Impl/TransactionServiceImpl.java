package sn.odc.flutter.Services.Impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.odc.flutter.Datas.Entity.Transaction;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.TransactionRepository;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Web.Dtos.request.ScheduleTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.TransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.MultiTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.TransactionResponseDTO;
import sn.odc.flutter.Services.TransactionService;
import sn.odc.flutter.Web.Dtos.response.UserResponseDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class TransactionServiceImpl extends BaseServiceImpl<Transaction, Long> implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private static final BigDecimal TAUX_FRAIS = new BigDecimal("0.01"); // 1%

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        super(transactionRepository);
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    private BigDecimal calculerFrais(BigDecimal montant) {
        return montant.multiply(TAUX_FRAIS).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculerMontantTotal(BigDecimal montant) {
        return montant.add(calculerFrais(montant));
    }

    @Override
    @Transactional
    public TransactionResponseDTO effectuerTransfert(Long expediteurId, TransferRequestDTO request) {
        User expediteur = userRepository.findByIdAndDeletedFalse(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        User destinataire = userRepository.findByTelephoneAndDeletedFalse(request.getDestinataire())
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        BigDecimal frais = calculerFrais(request.getMontant());
        BigDecimal montantTotal = calculerMontantTotal(request.getMontant());

        // Vérification du solde suffisant (montant + frais)
        if (expediteur.getSolde().compareTo(montantTotal) < 0) {
            throw new RuntimeException("Solde insuffisant pour couvrir le montant et les frais");
        }

        Transaction transaction = new Transaction();
        transaction.setMontant(request.getMontant());
        transaction.setFrais(frais);
        transaction.setMontantTotal(montantTotal);
        transaction.setType(Transaction.TypeTransaction.TRANSFERT);
        transaction.setDate(new Date());
        transaction.setExpUser(expediteur);
        transaction.setDestinataireUser(destinataire);

        // Mise à jour des soldes
        expediteur.setSolde(expediteur.getSolde().subtract(montantTotal));
        destinataire.setSolde(destinataire.getSolde().add(request.getMontant()));

        userRepository.save(expediteur);
        userRepository.save(destinataire);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return convertToDTO(savedTransaction);
    }

    @Override
    @Transactional
    public boolean annulerTransfert(Long transactionId, Long userId) {
        Optional<Transaction> transactionOpt = transactionRepository.findByIdAndDeletedFalse(transactionId);

        if (transactionOpt.isEmpty()) {
            throw new RuntimeException("Transaction non trouvée");
        }

        Transaction transaction = transactionOpt.get();

        // Vérifier si l'utilisateur est l'expéditeur
        if (!transaction.getExpUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette transaction");
        }

        // Vérifier si moins de 30 minutes se sont écoulées
        Instant transactionTime = transaction.getDate().toInstant();
        Instant now = Instant.now();
        Duration duration = Duration.between(transactionTime, now);

        if (duration.toMinutes() > 30) {
            throw new RuntimeException("Le délai d'annulation de 30 minutes est dépassé");
        }

        // Annuler le transfert (rembourser montant + frais)
        User expediteur = transaction.getExpUser();
        User destinataire = transaction.getDestinataireUser();

        expediteur.setSolde(expediteur.getSolde().add(transaction.getMontantTotal()));
        destinataire.setSolde(destinataire.getSolde().subtract(transaction.getMontant()));

        transaction.setDeleted(true);

        userRepository.save(expediteur);
        userRepository.save(destinataire);
        transactionRepository.save(transaction);

        return true;
    }

    @Override
    @Transactional
    public List<TransactionResponseDTO> effectuerTransfertMultiple(Long expediteurId, MultiTransferRequestDTO request) {
        User expediteur = userRepository.findByIdAndDeletedFalse(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        BigDecimal fraisParTransfert = calculerFrais(request.getMontant());
        BigDecimal montantTotalParTransfert = calculerMontantTotal(request.getMontant());

        // Calculer le montant total nécessaire (montant + frais pour chaque transfert)
        BigDecimal montantTotalNecessaire = montantTotalParTransfert
                .multiply(new BigDecimal(request.getDestinataires().size()));

        // Vérifier si le solde est suffisant
        if (expediteur.getSolde().compareTo(montantTotalNecessaire) < 0) {
            throw new RuntimeException("Solde insuffisant pour effectuer tous les transferts et payer les frais");
        }

        List<TransactionResponseDTO> transactions = new ArrayList<>();

        for (String destinataire : request.getDestinataires()) {
            TransferRequestDTO singleTransfer = new TransferRequestDTO();
            singleTransfer.setDestinataire(destinataire);
            singleTransfer.setMontant(request.getMontant());

            transactions.add(effectuerTransfert(expediteurId, singleTransfer));
        }

        return transactions;
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAllByOrderByDateDesc();
        return transactions.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByUser(Long userId) {
        List<Transaction> transactions = transactionRepository.findByExpUser_IdOrDestinataireUser_IdOrderByDateDesc(userId, userId);
        return transactions.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public TransactionResponseDTO convertToDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setMontant(transaction.getMontant());
        dto.setFrais(transaction.getFrais());
        dto.setMontantTotal(transaction.getMontantTotal());
        dto.setType(transaction.getType().toString());
        dto.setDate(transaction.getDate());

        if (transaction.getExpUser() != null) {
            UserResponseDTO expDTO = convertUserToDTO(transaction.getExpUser());
            dto.setExpediteur(expDTO);
        }
        if (transaction.getDestinataireUser() != null) {
            UserResponseDTO destDTO = convertUserToDTO(transaction.getDestinataireUser());
            dto.setDestinataire(destDTO);
        }

        // Indiquer si la transaction est annulée ou non
        dto.setStatus(transaction.isDeleted() ? "Annulée" : "Active");

        return dto;
    }

    private UserResponseDTO convertUserToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setTelephone(user.getTelephone());
        dto.setEmail(user.getEmail());
        dto.setSolde(user.getSolde());
        dto.setRole(user.getType().name());
        return dto;
    }


    @Override
    @Transactional
    public TransactionResponseDTO planifierTransfert(Long expediteurId, ScheduleTransferRequestDTO request) {
        User expediteur = userRepository.findByIdAndDeletedFalse(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        User destinataire = userRepository.findByTelephoneAndDeletedFalse(request.getDestinataire())
                .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));

        // Créer la transaction planifiée
        Transaction planification = new Transaction();
        planification.setMontant(request.getMontant());
        planification.setFrais(calculerFrais(request.getMontant()));
        planification.setMontantTotal(calculerMontantTotal(request.getMontant()));
        planification.setType(Transaction.TypeTransaction.TRANSFERT);
        planification.setDate(new Date());
        planification.setExpUser(expediteur);
        planification.setDestinataireUser(destinataire);

        // Paramètres de planification
        planification.setSchedulePeriod(request.getPeriod());
        planification.setNextExecutionDate(calculateNextExecutionDate(new Date(), request.getPeriod()));
        planification.setScheduleActive(true);

        Transaction savedPlanification = transactionRepository.save(planification);
        return convertToDTO(savedPlanification);
    }

    @Transactional
    protected void executeScheduledTransfer(Transaction planification) {
        try {
            // Créer une requête de transfert basée sur la planification
            TransferRequestDTO request = new TransferRequestDTO();
            request.setDestinataire(planification.getDestinataireUser().getTelephone());
            request.setMontant(planification.getMontant());

            // Effectuer le transfert
            TransactionResponseDTO executed = effectuerTransfert(
                    planification.getExpUser().getId(),
                    request
            );

            // Mettre à jour la date de prochaine exécution de la planification
            planification.setNextExecutionDate(
                    calculateNextExecutionDate(new Date(), planification.getSchedulePeriod())
            );
            transactionRepository.save(planification);
        } catch (Exception e) {
            // Log l'erreur
            e.printStackTrace();
            throw e;
        }
    }

    @Scheduled(fixedRate = 60000) // Vérifie toutes les minutes
    @Transactional
    public void executeScheduledTransfers() {
        Date now = new Date();
        List<Transaction> scheduledTransfers = transactionRepository
                .findByScheduleActiveTrueAndNextExecutionDateBefore(now);

        for (Transaction planification : scheduledTransfers) {
            try {
                executeScheduledTransfer(planification);
            } catch (Exception e) {
                // Log l'erreur mais continue avec les autres transferts
                e.printStackTrace();
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getScheduledTransfers(Long userId) {
        List<Transaction> scheduledTransfers = transactionRepository
                .findByExpUser_IdAndScheduleActiveTrueOrderByDateDesc(userId);
        return scheduledTransfers.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public boolean annulerPlanification(Long transactionId, Long userId) {
        Transaction planification = transactionRepository.findByIdAndDeletedFalse(transactionId)
                .orElseThrow(() -> new RuntimeException("Planification non trouvée"));

        if (!planification.getExpUser().getId().equals(userId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette planification");
        }

        if (!planification.isScheduledTransfer() || !planification.isScheduleActive()) {
            throw new RuntimeException("Cette transaction n'est pas une planification active");
        }

        planification.setScheduleActive(false);
        transactionRepository.save(planification);
        return true;
    }

    private Date calculateNextExecutionDate(Date fromDate, Transaction.SchedulePeriod period) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        switch (period) {
            case DAILY:
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, 1);
                break;
            default:
                return null;
        }

        return calendar.getTime();
    }
}