package com.gestion.equipos.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manejador global de excepciones para proporcionar mensajes claros al usuario
 * en lugar de stack traces técnicos de base de datos o Java.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja violaciones de integridad de base de datos SOLO para API REST
     * Los controllers web deben manejar sus propias excepciones con try-catch
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Error de integridad de datos en {}", request.getRequestURI(), ex);

        // SOLO manejar requests de API
        if (!request.getRequestURI().startsWith("/api/")) {
            // Lanzar de nuevo para que el controller lo maneje
            throw ex;
        }

        String userMessage = extractUserFriendlyMessage(ex);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(userMessage));
    }

    /**
     * Maneja errores de validación de campos (@Valid en DTOs)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Error de validación de campos", ex);

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errors);
    }

    /**
     * Maneja entidades no encontradas
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        log.error("Entidad no encontrada", ex);

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("El recurso solicitado no fue encontrado"));
    }

    /**
     * Maneja IllegalArgumentException SOLO para API REST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.error("Argumento inválido en {}", request.getRequestURI(), ex);

        // SOLO manejar requests de API
        if (!request.getRequestURI().startsWith("/api/")) {
            throw ex;
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }

    // REMOVIDO: Exception handler genérico que interceptaba TODAS las excepciones
    // Los controllers web deben manejar sus propias excepciones con try-catch

    /**
     * Extrae mensajes amigables de excepciones de PostgreSQL
     * Este método es público estático para que los controllers puedan usarlo
     */
    public static String extractUserFriendlyMessage(DataIntegrityViolationException ex) {
        String message = ex.getMessage();
        String rootCauseMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : message;

        // Patrón para detectar violación de unique constraint (con DOTALL para manejar saltos de línea)
        Pattern uniquePattern = Pattern.compile("duplicate key value violates unique constraint \"(\\w+)\".*Detail: Key \\((\\w+)\\)=\\(([^)]*)\\)", Pattern.DOTALL);
        Matcher uniqueMatcher = uniquePattern.matcher(rootCauseMessage);

        if (uniqueMatcher.find()) {
            String constraintName = uniqueMatcher.group(1);
            String columnName = uniqueMatcher.group(2);
            String value = uniqueMatcher.group(3);

            return buildUniqueConstraintMessage(constraintName, columnName, value);
        }

        // Patrón para detectar violación de foreign key
        Pattern foreignKeyPattern = Pattern.compile("violates foreign key constraint \"(\\w+)\"");
        Matcher foreignKeyMatcher = foreignKeyPattern.matcher(rootCauseMessage);

        if (foreignKeyMatcher.find()) {
            return "No se puede realizar la operación porque existen registros relacionados";
        }

        // Patrón para detectar violación de NOT NULL
        Pattern notNullPattern = Pattern.compile("null value in column \"(\\w+)\" (?:of relation \"(\\w+)\" )?violates not-null constraint");
        Matcher notNullMatcher = notNullPattern.matcher(rootCauseMessage);

        if (notNullMatcher.find()) {
            String columnName = notNullMatcher.group(1);
            return String.format("El campo '%s' es obligatorio y no puede estar vacío", translateColumnName(columnName));
        }

        // Si no coincide con ningún patrón, mensaje genérico
        return "Error de integridad de datos. Verifique que la información sea correcta y no esté duplicada.";
    }

    /**
     * Construye mensajes específicos para violaciones de unique constraint
     */
    private static String buildUniqueConstraintMessage(String constraintName, String columnName, String value) {
        // Casos específicos conocidos
        if (constraintName.contains("electronico_imei")) {
            if (value == null || value.isEmpty() || value.isBlank()) {
                return "Ya existe un equipo electrónico sin IMEI registrado. Por favor ingrese un IMEI único o deje el campo completamente vacío.";
            }
            return String.format("Ya existe un equipo electrónico con el IMEI '%s'. Por favor ingrese un IMEI diferente.", value);
        }

        if (constraintName.contains("vehiculo_placa")) {
            return String.format("Ya existe un vehículo con la placa '%s'. Las placas deben ser únicas.", value);
        }

        if (constraintName.contains("vehiculo_vin")) {
            return String.format("Ya existe un vehículo con el VIN '%s'. Los VIN deben ser únicos.", value);
        }

        if (constraintName.contains("usuario") && columnName.contains("codigo")) {
            return String.format("Ya existe un usuario con el código '%s'. Por favor use un código diferente.", value);
        }

        if (constraintName.contains("usuario") && columnName.contains("usuario")) {
            return String.format("El nombre de usuario '%s' ya está en uso. Por favor elija otro nombre de usuario.", value);
        }

        if (constraintName.contains("equipo_identificador")) {
            return String.format("Ya existe un equipo con el identificador '%s'.", value);
        }

        // Mensaje genérico para otros casos
        String fieldName = translateColumnName(columnName);
        if (value != null && !value.isEmpty()) {
            return String.format("Ya existe un registro con %s '%s'. Este campo debe ser único.", fieldName, value);
        } else {
            return String.format("Ya existe un registro con %s vacío.", fieldName);
        }
    }

    /**
     * Traduce nombres de columnas técnicos a nombres amigables en español
     */
    private static String translateColumnName(String columnName) {
        return switch (columnName.toLowerCase()) {
            case "imei" -> "IMEI";
            case "placa" -> "placa";
            case "vin" -> "VIN";
            case "codigo" -> "código";
            case "usuario" -> "nombre de usuario";
            case "identificador" -> "identificador";
            case "serie" -> "número de serie";
            case "nombre" -> "nombre";
            case "no_motor" -> "número de motor";
            case "sistema_operativo" -> "sistema operativo";
            case "primer_nombre" -> "primer nombre";
            case "primer_apellido" -> "primer apellido";
            default -> columnName.replace("_", " ");
        };
    }

    /**
     * Clase interna para respuestas de error estructuradas
     */
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}