package sn.odc.flutter.Datas.listeners;



import jakarta.persistence.PrePersist;
import sn.odc.flutter.Datas.listeners.impl.CodeGeneratable;

import java.time.Instant;

public class CodeGeneratorListener {

    @PrePersist
    public void generateCode(Object entity) {
        if (entity instanceof CodeGeneratable) {
            CodeGeneratable generatable = (CodeGeneratable) entity;
            if (generatable.getCode() == null || generatable.getCode().isEmpty()) {
                long timestamp = Instant.now().getEpochSecond(); // Obtenir le timestamp en secondes
                generatable.setCode("REF" + timestamp);
            }
        }
    }
}
