package sn.odc.flutter.Datas.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import sn.odc.flutter.Datas.listeners.impl.SoftDeletable;
import java.time.LocalDateTime;

@MappedSuperclass

public abstract class BaseEntity implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @CreationTimestamp
    @Getter
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Getter
    private LocalDateTime updatedAt;

    @Setter
    @Getter
    private boolean deleted = false;
}
