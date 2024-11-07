package sn.odc.flutter.Services.Impl;

import sn.odc.flutter.Datas.Repository.Interfaces.BaseInterface;
import sn.odc.flutter.Datas.listeners.impl.SoftDeletable;
import sn.odc.flutter.Services.BaseService;

import java.util.List;
import java.util.Optional;

public abstract class BaseServiceImpl<T extends SoftDeletable, ID> implements BaseService<T, ID> {

    protected final BaseInterface<T, ID> repository;

    protected BaseServiceImpl(BaseInterface<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findByIdAndDeletedFalse(id);
    }

    @Override
    public List<T> findAll() {
        return repository.findByDeletedFalse();
    }

    @Override
    public T update(ID id, T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        Optional<T> entityOpt = repository.findByIdAndDeletedFalse(id);
        if (entityOpt.isPresent()) {
            T entity = entityOpt.get();
            entity.setDeleted(true);
            repository.save(entity);
        }
    }


}