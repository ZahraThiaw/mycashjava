package sn.odc.flutter.Datas.Repository.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseInterface<T,ID> extends JpaRepository<T, ID> {
    List<T> findByDeletedFalse();
    Optional<T> findByIdAndDeletedFalse(ID id);
}
