package sn.odc.flutter.Web.Controller.Impl;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sn.odc.flutter.Datas.Entity.ScheduledTransfer;
import sn.odc.flutter.Datas.Entity.User;
import sn.odc.flutter.Services.ScheduledTransferService;
import sn.odc.flutter.Web.Controller.ScheduledTransferController;
import sn.odc.flutter.Web.Dtos.request.ScheduledTransferRequestDTO;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;
import sn.odc.flutter.Web.Dtos.response.ScheduledTransferResponseDTO;

import java.util.List;


@RestController
@RequestMapping("/schelduled")
@Tag(name = "Planification Transferts", description = "API de gestion des planifications de transferts")
public class ScheduledTransferControllerImpl extends BaseControllerImpl<ScheduledTransfer, ScheduledTransferResponseDTO>
        implements ScheduledTransferController {

    private final ScheduledTransferService scheduledTransferService;

    @Autowired
    public ScheduledTransferControllerImpl(ScheduledTransferService scheduledTransferService) {
        super(scheduledTransferService);  // Pass the service to the base class constructor
        this.scheduledTransferService = scheduledTransferService;
    }

    @Override
    @PostMapping("/scheduled-transfers")
    public ResponseEntity<GenericResponse<ScheduledTransferResponseDTO>> planifierTransfert(
            Authentication authentication,
            @Valid @RequestBody ScheduledTransferRequestDTO request) {
        User user = (User) authentication.getPrincipal();
        ScheduledTransferResponseDTO planification = scheduledTransferService.planifierTransfert(user.getId(), request);
        return ResponseEntity.ok(new GenericResponse<>(
                planification,
                "Transfert planifié avec succès"
        ));
    }

    @Override
    @PostMapping("/scheduled-transfers/{transactionId}/cancel")
    public ResponseEntity<GenericResponse<Boolean>> annulerPlanification(
            Authentication authentication,
            @PathVariable Long transactionId) {
        User user = (User) authentication.getPrincipal();
        boolean result = scheduledTransferService.annulerPlanification(transactionId, user.getId());
        return ResponseEntity.ok(new GenericResponse<>(
                result,
                "Planification annulée avec succès"
        ));
    }

    @Override
    @GetMapping("/scheduled-transfers/user")
    public ResponseEntity<GenericResponse<List<ScheduledTransferResponseDTO>>> getScheduledTransfers(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ScheduledTransferResponseDTO> planifications = scheduledTransferService.getScheduledTransfers(user.getId());
        return ResponseEntity.ok(new GenericResponse<>(
                planifications,
                "Liste des transferts planifiés récupérée avec succès"
        ));
    }

    @Override
    protected ScheduledTransferResponseDTO convertToResponseDTO(ScheduledTransfer entity) {
        return scheduledTransferService.convertToDTO(entity);
    }
}
