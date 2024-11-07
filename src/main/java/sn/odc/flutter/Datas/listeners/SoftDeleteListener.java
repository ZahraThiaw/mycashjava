package sn.odc.flutter.Datas.listeners;

import jakarta.persistence.PreRemove;
import sn.odc.flutter.Datas.listeners.impl.SoftDeletable;

public class SoftDeleteListener {
    @PreRemove
    public void preRemove(SoftDeletable softDeletable) {
        softDeletable.setDeleted(true);
    }
}
