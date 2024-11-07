package sn.odc.flutter.Web.Controller.Interfaces;


import org.springframework.http.ResponseEntity;

public interface BaseController<T, DTO, ID> {
        ResponseEntity<T> create(DTO dto);
        ResponseEntity<T> update(ID id, DTO dto);
        ResponseEntity<T> getById(ID id);
        ResponseEntity<Void> delete(ID id);
        // autres méthodes de base...
    }

