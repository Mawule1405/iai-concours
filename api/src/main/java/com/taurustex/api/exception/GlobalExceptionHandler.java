package com.taurustex.api.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  // 1. Ressource non trouvée (404)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), "Resource not found");
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  // 2. Validation des champs (400) - Harmonisé avec ApiError
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Échec de la validation des données",
            errors
    );
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  // 3. Accès refusé / RBAC (403)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException ex) {
    ApiError apiError = new ApiError(
            HttpStatus.FORBIDDEN.value(),
            "Permissions insuffisantes pour effectuer cette action.",
            ex.getMessage()
    );
    return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
  }

  // 4. Authentification échouée (401)
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
    ApiError apiError = new ApiError(
            HttpStatus.UNAUTHORIZED.value(),
            "Identifiant ou mot de passe incorrect.",
            null
    );
    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  // 4. bis. Gestion des statuts de compte (401 au lieu de 500)
  @ExceptionHandler(AccountStatusException.class)
  public ResponseEntity<ApiError> handleAccountStatusException(AccountStatusException ex) {
    String message = "L'accès à ce compte est restreint.";

    if (ex instanceof DisabledException) {
      message = "Ce compte utilisateur est désactivé.";
    } else if (ex instanceof LockedException) {
      message = "Ce compte utilisateur est verrouillé.";
    }

    ApiError apiError = new ApiError(
            HttpStatus.UNAUTHORIZED.value(),
            message,
            ex.getMessage()
    );
    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  // 5. Conflit métier (409) - Ex: Chevauchement d'emploi du temps
  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<ApiError> handleDataConflictException(DataConflictException ex) {
    ApiError apiError = new ApiError(HttpStatus.CONFLICT.value(), ex.getMessage(), "Conflict detected");
    return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
  }

  // 6. Entrée invalide / Mauvaise requête (400)
  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<ApiError> handleInvalidInputException(InvalidInputException ex) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> handleConflict(IllegalStateException ex) {
    // Utilise la même structure pour toutes tes erreurs
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex) {
    // Utilise la même structure pour toutes tes erreurs
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  // 7. Erreur générique serveur (500)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGlobalException(Exception ex) {
    ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Une erreur technique est survenue.",
            ex.getLocalizedMessage()
    );
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}