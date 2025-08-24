package com.gestion.equipos.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para Thymeleaf templates
 */
public class ThymeleafUtils {
    
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    
    /**
     * Convierte LocalDate al formato ISO requerido por input type="date"
     * @param date fecha a convertir
     * @return string en formato yyyy-MM-dd o cadena vacía si es null
     */
    public static String formatDateForInput(LocalDate date) {
        return date != null ? date.format(ISO_DATE_FORMATTER) : "";
    }
    
    /**
     * Convierte LocalDateTime al formato ISO requerido por input type="datetime-local"
     * @param dateTime fecha/hora a convertir
     * @return string en formato yyyy-MM-ddTHH:mm o cadena vacía si es null
     */
    public static String formatDateTimeForInput(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(ISO_DATETIME_FORMATTER) : "";
    }
}