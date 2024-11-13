package sn.odc.flutter.Web.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import sn.odc.flutter.Datas.Entity.ScheduledTransfer;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;
import sn.odc.flutter.Web.Dtos.response.ScheduledTransferResponseDTO;
import sn.odc.flutter.Web.Dtos.response.TransactionResponseDTO;

import java.util.List;

public interface ScheduledTransferController extends BaseController<ScheduledTransfer, ScheduledTransferResponseDTO> {
    ResponseEntity<GenericResponse<ScheduledTransferResponseDTO>> planifierTransfert(
            Authentication authentication,
            ScheduledTransferRequestDTO request
    );

    ResponseEntity<GenericResponse<Boolean>> annulerPlanification(
            Authentication authentication,
            Long transactionId
    );

    ResponseEntity<GenericResponse<List<ScheduledTransferResponseDTO>>> getScheduledTransfers(Authentication authentication);
}

