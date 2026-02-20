package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@Slf4j
public final class DateUtil {

    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_YYYY_MM_DD = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FECHA_HORA = "dd/MM/yyyy hh.mm a";
    public static final String DATETIME_FORMAT_DD_MM_YYYY_HH_MM_A = "dd/MM/yyyy hh:mm a";

    private DateUtil() {
    }

    public static String getStringFromDate(final Date date, final String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(final String date, final String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.parse(date);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String getStringFromString(final String date, final String formatFrom, final String formatTo) {
        Date dateFrom = getDateFromString(date, formatFrom);
        return getStringFromDate(dateFrom, formatTo);
    }

    public static String getActualString(final String formato) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formato);
        return simpleDateFormat.format(getDateTimeZone(formato));
    }

    public static Date getActualDate(final String formato) {
        return getDateTimeZone(formato);
    }

    public static Date getDateTimeZone(final String formato) {
        try {
            Instant instant = Instant.now();
            ZoneId zoneId = ZoneId.of("America/Lima");
            ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formato);
            DateFormat dateFormat = new SimpleDateFormat(formato);
            String fechaString = zonedDateTime.format(dateTimeFormatter);

            return dateFormat.parse(fechaString);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String  getPeriodoActual(String formato) {
        Date dateInTimeZone = getDateTimeZone(formato);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateInTimeZone);
        return Integer.toString(calendar.get(Calendar.YEAR));
    }

    public static int obtenerDia() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(new Date());
        return calendario.get(Calendar.DAY_OF_MONTH);
    }

    public static int obtenerDia(String fechaString, String formato) {
        LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern(formato));
        return fecha.getDayOfMonth();
    }
    public static int obtenerAnio() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(new Date());
        return calendario.get(Calendar.YEAR);
    }

    public static int obtenerAnio(String fechaString, String formato) {
        LocalDate date = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern(formato));
        return date.getYear();
    }

    public static String obtenerNombreMes(String fechaString, String formato) {
        LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ofPattern(formato));
        int valorMes = fecha.getMonthValue();
        String[] nombresMeses = new DateFormatSymbols(new Locale("es")).getMonths();
        return nombresMeses[valorMes - 1];
    }

    public static String obtenerNombreMes() {
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(new Date());
        int valorMes = calendario.get(Calendar.MONTH);
        String[] nombresMeses = new DateFormatSymbols(new Locale("es")).getMonths();
        return nombresMeses[valorMes];
    }

    public static Date obtenerFecha(String fechaRequest, String ...formatos) {
        if (fechaRequest == null || fechaRequest.isEmpty()) {
            return null;
        }

        Date fechaResultado = null;

        for (String formato : formatos) {
            fechaResultado = getDateFromString(fechaRequest, formato);

            if (fechaResultado != null) {
                return fechaResultado;
            }
        }

        return fechaResultado;
    }

}
