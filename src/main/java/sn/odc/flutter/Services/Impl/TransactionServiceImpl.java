package sn.odc.flutter.Services.Impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.odc.flutter.Datas.Entity.Transaction;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Datas.Repository.Interfaces.TransactionRepository;
import sn.odc.flutter.Datas.Repository.Interfaces.UserRepository;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
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
}