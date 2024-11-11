package sn.odc.flutter.Services;

import sn.odc.flutter.Datas.Entity.Transaction;
import sn.odc.flutter.Web.Dtos.request.ScheduleTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.TransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.MultiTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.TransactionResponseDTO;
import java.util.List;

public interface TransactionService extends BaseService<Transaction, Long> {
    TransactionResponseDTO effectuerTransfert(Long expediteurId, TransferRequestDTO request);
    boolean annulerTransfert(Long transactionId, Long userId);
    List<TransactionResponseDTO> effectuerTransfertMultiple(Long expediteurId, MultiTransferRequestDTO request);
    List<TransactionResponseDTO> getAllTransactions();
    List<TransactionResponseDTO> getTransactionsByUser(Long userId);
    TransactionResponseDTO convertToDTO(Transaction transaction);
    TransactionResponseDTO planifierTransfert(Long expediteurId, ScheduleTransferRequestDTO request);
    boolean annulerPlanification(Long transactionId, Long userId);
    List<TransactionResponseDTO> getScheduledTransfers(Long userId);
}