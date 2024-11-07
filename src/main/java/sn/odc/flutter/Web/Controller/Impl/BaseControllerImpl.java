package sn.odc.flutter.Web.Controller.Impl;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.odc.flutter.Datas.listeners.impl.SoftDeletable;
import sn.odc.flutter.Services.BaseService;
import sn.odc.flutter.Web.Dtos.response.GenericResponse;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur de base implémentant les opérations CRUD standard
 * @param <T> Type de l'entité
 * @param <ID> Type de l'identifiant
 */
@RestController
public abstract class BaseControllerImpl<T extends SoftDeletable, ID> {

    protected final BaseService<T, ID> service;

    protected BaseControllerImpl(BaseService<T, ID> service) {
        this.service = service;
    }


    @Operation(summary = "Créer une nouvelle entité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Entité créée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la création")
    })
    @PostMapping
    public ResponseEntity<GenericResponse<T>> create(@RequestBody T entity) {
        try {
            T createdEntity = service.create(entity);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new GenericResponse<>(createdEntity, "Entité créée avec succès"));
        } catch (Exception e) {
            return handleException("Erreur lors de la création", e);
        }
    }

    @Operation(summary = "Récupérer une entité par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entité trouvée"),
            @ApiResponse(responseCode = "404", description = "Entité non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la recherche")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<T>> findById(@PathVariable ID id) {
        try {
            Optional<T> entity = service.findById(id);
            return entity.map(t -> ResponseEntity.ok(new GenericResponse<>(
                    t,
                    "Entité trouvée avec succès"
            ))).orElseGet(() -> ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>(String.format("Entité non trouvée avec l'ID: %s", id))));
        } catch (Exception e) {
            return handleException("Erreur lors de la recherche", e);
        }
    }

    @Operation(summary = "Récupérer toutes les entités")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération")
    })
    @GetMapping
    public ResponseEntity<GenericResponse<List<T>>> readAll() {
        try {
            List<T> entities = service.findAll();
            String message = entities.isEmpty() ?
                    "Aucune entité trouvée" :
                    "Liste récupérée avec succès";

            return ResponseEntity.ok(new GenericResponse<>(entities, message));
        } catch (Exception e) {
            return handleException("Erreur lors de la récupération de la liste", e);
        }
    }

    @Operation(summary = "Mettre à jour une entité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mise à jour réussie"),
            @ApiResponse(responseCode = "404", description = "Entité non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la mise à jour")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<T>> update(@PathVariable ID id, @RequestBody T entity) {
        try {
            if (!service.findById(id).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new GenericResponse<>(String.format("Entité non trouvée avec l'ID: %s", id)));
            }

            T updatedEntity = service.update(id, entity);
            return ResponseEntity.ok(new GenericResponse<>(
                    updatedEntity,
                    "Mise à jour effectuée avec succès"
            ));
        } catch (Exception e) {
            return handleException("Erreur lors de la mise à jour", e);
        }
    }

    @Operation(summary = "Supprimer une entité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppression réussie"),
            @ApiResponse(responseCode = "404", description = "Entité non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la suppression")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> delete(@PathVariable ID id) {
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

    /**
     * Méthode utilitaire pour gérer les exceptions de manière cohérente
     */
    private <R> ResponseEntity<GenericResponse<R>> handleException(String message, Exception e) {
        GenericResponse<R> errorResponse = new GenericResponse<>(message);
        errorResponse.addError(e.getMessage());
        errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}