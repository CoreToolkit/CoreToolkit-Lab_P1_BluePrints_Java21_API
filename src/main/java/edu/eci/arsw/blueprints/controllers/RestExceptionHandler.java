package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.exceptions.BadRequestException;
import edu.eci.arsw.blueprints.model.ApiResponse;
import edu.eci.arsw.blueprints.persistence.BlueprintAlreadyExistsException;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Manejo centralizado de errores para devolver ApiResponse uniforme en validaciones.
 * No cubre excepciones de negocio ya gestionadas en el controlador.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        return error(HttpStatus.BAD_REQUEST, fieldErrors(ex.getBindingResult()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(v -> v.getPropertyPath().toString(), LinkedHashMap::new,
                        Collectors.mapping(v -> v.getMessage() == null ? "invalid" : v.getMessage(), Collectors.toList())))
                .entrySet().stream()
                .map(e -> e.getKey() + ": " + String.join(", ", e.getValue()))
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, message.isBlank() ? "validation error" : message);
    }

    @ExceptionHandler({BlueprintNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(Exception ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BlueprintAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(BlueprintAlreadyExistsException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, BlueprintPersistenceException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        String message = ex instanceof HttpMessageNotReadableException ? "invalid request body" : ex.getMessage();
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "internal server error");
    }

    private ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(status.value(), message == null ? status.getReasonPhrase() : message, null));
    }

    private String fieldErrors(BindingResult bindingResult) {
        String msg = bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField, LinkedHashMap::new,
                        Collectors.mapping(fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                                Collectors.toList())))
                .entrySet().stream()
                .map(e -> e.getKey() + ": " + String.join(", ", e.getValue()))
                .collect(Collectors.joining("; "));
        return msg.isBlank() ? "validation error" : msg;
    }
}
