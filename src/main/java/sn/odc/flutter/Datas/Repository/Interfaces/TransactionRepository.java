package sn.odc.flutter.Datas.Repository.Interfaces;

import sn.odc.flutter.Datas.Entity.Transaction;
import java.util.List;

public interface TransactionRepository extends BaseInterface<Transaction, Long> {;
    List<Transaction> findAllByOrderByDateDesc();
    List<Transaction> findByExpUser_IdOrDestinataireUser_IdOrderByDateDesc(Long expUserId, Long destUserId);
}
