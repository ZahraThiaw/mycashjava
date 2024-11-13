package sn.odc.flutter.Datas.Repository.Interfaces;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sn.odc.flutter.Datas.Entity.Transaction;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends BaseInterface<Transaction, Long> {
    List<Transaction> findAllByOrderByDateDesc();

    List<Transaction> findByExpUser_IdOrDestinataireUser_IdOrderByDateDesc(Long expUserId, Long destUserId);

}