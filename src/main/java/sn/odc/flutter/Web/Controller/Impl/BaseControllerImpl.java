package sn.odc.flutter.Web.Controller.Impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.odc.flutter.Datas.listeners.impl.SoftDeletable;
import sn.odc.flutter.Services.BaseService;
import sn.odc.flutter.Web.Controller.BaseController;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;

public abstract class BaseControllerImpl<T extends SoftDeletable, R> implements BaseController<T, R> {

    protected final BaseService<T, Long> service;

    protected BaseControllerImpl(BaseService<T, Long> service) {
        this.service = service;
    }

    @Override
    @PostMapping
    public ResponseEntity<GenericResponse<R>> create(@RequestBody T entity) {
        try {
            T createdEntity = service.create(entity);
            R response = convertToResponseDTO(createdEntity);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new GenericResponse<>(response, "Entité créée avec succès"));
        } catch (Exception e) {
            return handleException("Erreur lors de la création", e);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<R>> getById(@PathVariable Long id) {
        try {
            T entity = service.findById(id)
                    .orElseThrow(() -> new RuntimeException("Entité non trouvée"));
            R response = convertToResponseDTO(entity);
            return ResponseEntity.ok(new GenericResponse<>(
                    response,
                    "Entité trouvée avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors de la recherche", e);
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<R>> update(@PathVariable Long id, @RequestBody T entity) {
        try {
            if (!service.findById(id).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>(String.format("Entité non trouvée avec l'ID: %s", id)));
            }

            T updatedEntity = service.update(id, entity);
            R response = convertToResponseDTO(updatedEntity);
            return ResponseEntity.ok(new GenericResponse<>(
                    response,
                    "Mise à jour effectuée avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors de la mise à jour", e);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> delete(@PathVariable Long id) {
        try {
            if (!service.findById(id).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>(String.format("Entité non trouvée avec l'ID: %s", id)));
            }

            service.delete(id);
            return ResponseEntity.ok(new GenericResponse<>(null, "Suppression effectuée avec succès"));
        } catch (Exception e) {
            return handleException("Erreur lors de la suppression", e);
        }
    }

    // Méthode abstraite pour convertir l'entité en DTO de réponse
    protected abstract R convertToResponseDTO(T entity);

    protected <E> ResponseEntity<GenericResponse<E>> handleException(String message, Exception e) {
        GenericResponse<E> errorResponse = new GenericResponse<>(message);
        errorResponse.addError(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
