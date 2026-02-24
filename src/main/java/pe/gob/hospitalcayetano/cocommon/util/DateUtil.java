package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;


@Slf4j
public final class DateUtil {

    private static final ZoneId ZONE_LIMA = ZoneId.of("America/Lima");
    private static final Locale LOCALE_ES = Locale.of("es", "PE");


    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }



    public static LocalDate parseToLocalDate(String date, String pattern) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            log.error("Error parsing date: {}", date, e);
            return null;
        }
    }

    public static LocalDateTime parseToLocalDateTime(String date, String pattern) {
        try {
            return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException e) {
            log.error("Error parsing datetime: {}", date, e);
            return null;
        }
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE_LIMA);
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE_LIMA);
    }

    public static String nowFormatted(String pattern) {
        return format(now(), pattern);
    }

    public static int obtenerDia() {
        return today().getDayOfMonth();
    }

    public static int obtenerDia(String fechaString, String formato) {
        return parseToLocalDate(fechaString, formato).getDayOfMonth();
    }

    public static int obtenerAnio() {
        return today().getYear();
    }

    public static int obtenerAnio(String fechaString, String formato) {
        return parseToLocalDate(fechaString, formato).getYear();
    }

    public static String obtenerNombreMes(String fechaString, String formato) {
        LocalDate fecha = parseToLocalDate(fechaString, formato);
        return fecha.getMonth().getDisplayName(TextStyle.FULL, LOCALE_ES);
    }

    public static String obtenerNombreMes() {
        return today().getMonth().getDisplayName(TextStyle.FULL, LOCALE_ES);
    }

    public static String getPeriodoActual() {
        return String.valueOf(today().getYear());
    }

    public static LocalDate parseMultipleFormats(String fechaRequest, String... formatos) {
        if (fechaRequest == null || fechaRequest.isBlank()) {
            return null;
        }

        for (String formato : formatos) {
            try {
                return LocalDate.parse(fechaRequest, DateTimeFormatter.ofPattern(formato));
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }
}