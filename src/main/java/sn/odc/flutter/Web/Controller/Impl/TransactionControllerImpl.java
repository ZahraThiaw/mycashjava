package sn.odc.flutter.Web.Controller.Impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.odc.flutter.Datas.Entity.Transaction;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Services.TransactionService;
import sn.odc.flutter.Web.Controller.TransactionController;
import sn.odc.flutter.Web.Dtos.request.MultiTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.TransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;
import sn.odc.flutter.Web.Dtos.response.TransactionResponseDTO;

import java.util.List;

@RestController
//@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "API de gestion des transactions")
public class TransactionControllerImpl
        extends BaseControllerImpl<Transaction, TransactionResponseDTO>
        implements TransactionController {

    private final TransactionService transactionService;

    public TransactionControllerImpl(TransactionService transactionService) {
        super(transactionService);
        this.transactionService = transactionService;
    }

    @Override
    protected TransactionResponseDTO convertToResponseDTO(Transaction entity) {
        // Implémentez la conversion de Transaction vers TransactionResponseDTO
        return transactionService.convertToDTO(entity);
    }

    @Override
    @PostMapping("/transfert")
    public ResponseEntity<GenericResponse<TransactionResponseDTO>> effectuerTransfert(
            Authentication authentication,
            @RequestBody TransferRequestDTO request) {
        try {
            User user = (User) authentication.getPrincipal();
            TransactionResponseDTO transaction = transactionService.effectuerTransfert(user.getId(), request);
            return ResponseEntity.ok(new GenericResponse<>(
                    transaction,
                    "Transfert effectué avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors du transfert", e);
        }
    }

    @Override
    @PostMapping("/transfert-multiple")
    public ResponseEntity<GenericResponse<List<TransactionResponseDTO>>> effectuerTransfertMultiple(
            Authentication authentication,
            @RequestBody MultiTransferRequestDTO request) {
        try {
            User user = (User) authentication.getPrincipal();
            List<TransactionResponseDTO> transactions = transactionService.effectuerTransfertMultiple(user.getId(), request);
            return ResponseEntity.ok(new GenericResponse<>(
                    transactions,
                    "Transferts multiples effectués avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors des transferts multiples", e);
        }
    }

    @Override
    @PostMapping("/{transactionId}/annuler")
    public ResponseEntity<GenericResponse<Boolean>> annulerTransfert(
            Authentication authentication,
            @PathVariable Long transactionId) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean result = transactionService.annulerTransfert(transactionId, user.getId());
            return ResponseEntity.ok(new GenericResponse<>(
                    result,
                    "Transfert annulé avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors de l'annulation du transfert", e);
        }
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<TransactionResponseDTO>>> getAllTransactions() {
        try {
            List<TransactionResponseDTO> transactions = transactionService.getAllTransactions();
            return ResponseEntity.ok(new GenericResponse<>(
                    transactions,
                    "Liste des transactions récupérée avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors de la récupération des transactions", e);
        }
    }

    @Override
    @GetMapping("/user/transactions")
    public ResponseEntity<GenericResponse<List<TransactionResponseDTO>>> getTransactionsForUser(
            Authentication authentication) {
        try {
            // Récupérer l'utilisateur connecté
            User user = (User) authentication.getPrincipal();

            // Appeler le service pour obtenir les transactions de l'utilisateur
            List<TransactionResponseDTO> transactions = transactionService.getTransactionsByUser(user.getId());

            return ResponseEntity.ok(new GenericResponse<>(
                    transactions,
                    "Liste des transactions de l'utilisateur récupérée avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors de la récupération des transactions de l'utilisateur", e);
        }
    }
}