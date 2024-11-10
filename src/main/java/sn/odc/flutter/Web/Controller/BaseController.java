package sn.odc.flutter.Web.Controller;

import org.springframework.http.ResponseEntity;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;

public interface BaseController<T, R> {
    ResponseEntity<GenericResponse<R>> create(T entity);
    ResponseEntity<GenericResponse<R>> update(Long id, T entity);
    ResponseEntity<GenericResponse<R>> getById(Long id);
    ResponseEntity<GenericResponse<Void>> delete(Long id);
}
