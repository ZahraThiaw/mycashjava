package sn.odc.flutter.Datas.Repository.Interfaces;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.odc.flutter.Datas.Entity.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends BaseInterface<Transaction, Long> {
    List<Transaction> findAllByOrderByDateDesc();

    List<Transaction> findByExpUser_IdOrDestinataireUser_IdOrderByDateDesc(Long expUserId, Long destUserId);

    // Corrected query to match your entity field name
    @Query("SELECT t FROM Transaction t WHERE t.isScheduleActive = true AND t.nextExecutionDate < :date")
    List<Transaction> findByScheduleActiveTrueAndNextExecutionDateBefore(@Param("date") Date date);

    @Query("SELECT t FROM Transaction t WHERE t.expUser.id = :userId AND t.isScheduleActive = true ORDER BY t.date DESC")
    List<Transaction> findByExpUser_IdAndScheduleActiveTrueOrderByDateDesc(@Param("userId") Long userId);
}