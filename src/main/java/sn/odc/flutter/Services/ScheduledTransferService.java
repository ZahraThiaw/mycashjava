package sn.odc.flutter.Services;
import sn.odc.flutter.Datas.Entity.ScheduledTransfer;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.ScheduledTransferResponseDTO;

import java.util.List;

public interface ScheduledTransferService extends BaseService<ScheduledTransfer, Long>  {

    ScheduledTransferResponseDTO planifierTransfert(Long userId, ScheduledTransferRequestDTO request);

    boolean annulerPlanification(Long transactionId, Long userId);

    List<ScheduledTransferResponseDTO> getScheduledTransfers(Long userId);

    ScheduledTransferResponseDTO convertToDTO(ScheduledTransfer entity);
}
