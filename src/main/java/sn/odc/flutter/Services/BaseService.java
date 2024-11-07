package sn.odc.flutter.Services;

import sn.odc.flutter.Datas.listeners.impl.SoftDeletable;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends SoftDeletable, ID> {

    T create(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    T update(ID id, T entity);

    void delete(ID id);
}