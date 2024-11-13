package sn.odc.flutter.Datas.Repository.Interfaces;

import sn.odc.flutter.Datas.Entity.ScheduledTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

// Extend BaseInterface along with JpaRepository
public interface ScheduledTransferRepository extends JpaRepository<ScheduledTransfer, Long>, BaseInterface<ScheduledTransfer, Long> {
    Optional<ScheduledTransfer> findByIdAndDeletedFalse(Long id);
    List<ScheduledTransfer> findByDeletedFalse();
    List<ScheduledTransfer> findByScheduleActiveTrueAndNextExecutionDateBefore(Date date);
    List<ScheduledTransfer> findByExpUser_IdAndScheduleActiveTrueOrderByDateDesc(Long userId);
}
