package sn.odc.flutter.Web.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import sn.odc.flutter.Datas.Entity.Transaction;
import sn.odc.flutter.Web.Dtos.request.MultiTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.request.TransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;
import sn.odc.flutter.Web.Dtos.response.TransactionResponseDTO;

import java.util.List;

public interface TransactionController extends BaseController<Transaction, TransactionResponseDTO> {
    ResponseEntity<GenericResponse<TransactionResponseDTO>> effectuerTransfert(
            Authentication authentication,
            TransferRequestDTO request
    );

    ResponseEntity<GenericResponse<List<TransactionResponseDTO>>> effectuerTransfertMultiple(
            Authentication authentication,
            MultiTransferRequestDTO request
    );

    ResponseEntity<GenericResponse<Boolean>> annulerTransfert(
            Authentication authentication,
            Long transactionId
    );

    ResponseEntity<GenericResponse<List<TransactionResponseDTO>>> getAllTransactions();

    ResponseEntity<GenericResponse<List<TransactionResponseDTO>>> getTransactionsForUser(Authentication authentication);

}