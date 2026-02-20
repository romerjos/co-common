package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public abstract class UtilDate {
    static final SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
    static final SimpleDateFormat ddMMyyyy_HHmmss = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    static final SimpleDateFormat HHmmss = new SimpleDateFormat("HH:mm:ss");
    static final SimpleDateFormat HHmm = new SimpleDateFormat("HH:mm");

    public static String mes(String numeroMes) {
        String mes = "";
        mes = "01".equals(numeroMes) ? "Enero" : mes;
        mes = "02".equals(numeroMes) ? "Febrero" : mes;
        mes = "03".equals(numeroMes) ? "Marzo" : mes;
        mes = "04".equals(numeroMes) ? "Abril" : mes;
        mes = "05".equals(numeroMes) ? "Mayo" : mes;
        mes = "06".equals(numeroMes) ? "Junio" : mes;
        mes = "07".equals(numeroMes) ? "Julio" : mes;
        mes = "08".equals(numeroMes) ? "Agosto" : mes;
        mes = "09".equals(numeroMes) ? "Septiembre" : mes;
        mes = "10".equals(numeroMes) ? "Octubre" : mes;
        mes = "11".equals(numeroMes) ? "Noviembre" : mes;
        mes = "12".equals(numeroMes) ? "Diciembre" : mes;
        return mes;
    }

    public static String diaES(DayOfWeek fechaHoy) {
        switch (fechaHoy) {
            case MONDAY:
                return "Lunes";
            case TUESDAY:
                return "Martes";
            case WEDNESDAY:
                return "Miercoles";
            case THURSDAY:
                return "Jueves";
            case FRIDAY:
                return "Viernes";
            case SATURDAY:
                return "Sabado";
            case SUNDAY:
                return "Domingo";
            default:
                return "";
        }
    }

    public static Boolean esPasado(Date fecha) {
        return new Date().getTime() - fecha.getTime() > 0;
    }

    public static Boolean esFuturo(Date fecha) {
        try {
            return new Date().getTime() - fecha.getTime() < 0;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public static Integer cantidadDias(Date fechaInicio, Date fechaFin) {

        return fechaInicio == null || fechaFin == null ? null : (int) ((fechaFin.getTime() - fechaInicio.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int porcentajeTranscurrido(Date fechaInicio, Date fechaFin) {
        Integer porcentaje = 100 * cantidadDias(fechaInicio, new Date()) / UtilDate.cantidadDias(fechaInicio, fechaFin);
        porcentaje = porcentaje < 0 ? 0 : porcentaje;
        porcentaje = porcentaje > 100 ? 100 : porcentaje;
        return porcentaje;
    }

    public static Integer porcentajeTranscurrido(Long cantidadDias, Date fechaFin) {
        try {
            Date fechaInicio = restarDias(fechaFin, cantidadDias);
            return porcentajeTranscurrido(fechaInicio, fechaFin);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public static Date sumarDias(Date fechaInicio, Long cantidadDias) {
        long tiempo = fechaInicio.getTime() + (cantidadDias * 24L * 60L * 60L * 1000L);
        Date fechaFutura = new Date();
        fechaFutura.setTime(tiempo);
        return fechaFutura;
    }

    public static Date sumarMeses(Date fechaInicio, Integer cantidadMeses) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(fechaInicio);
        calendar.add(Calendar.MONTH, cantidadMeses);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date restarDias(Date fechaFin, Long cantidadDias) {
        long tiempo = fechaFin.getTime() - (cantidadDias * 24L * 60L * 60L * 1000L);
        Date fechaFutura = new Date();
        fechaFutura.setTime(tiempo);
        return fechaFutura;
    }

    public static String restarDias(Date fechaFin, Long cantidadDias, String formato) {
        return new SimpleDateFormat(formato).format(restarDias(fechaFin, cantidadDias));
    }

    public static String sumarDias(Date fechaInicio, Long cantidadDias, String formato) {
        return new SimpleDateFormat(formato).format(sumarDias(fechaInicio, cantidadDias));
    }

    public static Date sumarSegundos(Date fechaInicio, Long cantidadSegundos) {
        long tiempo = fechaInicio.getTime() + (cantidadSegundos * 1000L);
        Date fechaFutura = new Date();
        fechaFutura.setTime(tiempo);
        return fechaFutura;
    }

    public static String formato(String clave, String formatoOrigen, String formatoDestino) {
        Date date;
        try {
            date = new SimpleDateFormat(formatoOrigen).parse(clave);
            if (date != null) {
                return new SimpleDateFormat(formatoDestino).format(date);
            }
        } catch (Exception e1) {
            log.error(e1.getMessage(), e1);
            return clave;
        }
        return clave;
    }

    public static boolean fechaEnRangoFecha(String fecha, String fechaDesde, String fechaHasta, String formato) {
        boolean esValido = false;

        try {
            Date fechaTest = new SimpleDateFormat(formato).parse(fecha);
            Date desde = new SimpleDateFormat(formato).parse(fechaDesde);
            Date hasta = new SimpleDateFormat(formato).parse(fechaHasta);
            if (fechaTest != null && desde != null && hasta != null) {
                esValido = (fechaTest.equals(desde) || fechaTest.after(desde)) && (fechaTest.equals(hasta) || fechaTest.before(hasta));
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        return esValido;
    }

    public static String fechaYHoraActual() {
        // AAAA-MM-DDTHH:MM:SS en tiempo local
        return Instant.now()
                .atZone(ZoneId.systemDefault())
                .toString()
                .substring(0, 19);
    }

    public static String fechaActual() {
        return fechaYHoraActual().split("T")[0];
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date stringToDate(String date, String formato) {
        DateFormat format = new SimpleDateFormat(formato);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String fechaCompletaString() {
        return Integer.toString(LocalDateTime.now().getYear()) +
                LocalDateTime.now().getMonthValue() +
                LocalDateTime.now().getDayOfMonth() +
                LocalDateTime.now().getHour() +
                LocalDateTime.now().getMinute();
    }

    public static String obtenerStringDiaMesAnio(Date fecha) {
        return ddMMyyyy.format(fecha);
    }

    public static String obtenerStringDiaMesAnioHHmmss(Date fecha) {
        return ddMMyyyy_HHmmss.format(fecha);
    }

    public static String obtenerStringHoraMinSegundo(Date fecha) {
        return HHmmss.format(fecha);
    }

    public static String obtenerStringHoraMinuto(Date fecha) {
        return HHmm.format(fecha);
    }

    public static Date obtenerUltimoDiaMes(Date fecha) {
        LocalDate convertedDate = LocalDate.parse(ddMMyyyy.format(fecha),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));

        return Date.from(convertedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date obtenerPrimerDiaMes(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date sumarRestarDiaAnio(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();
    }

    public static Date obtenerPrimerDiaMes() {
        Calendar c = Calendar.getInstance(); // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();

    }

    public static Date obtenerUltimoDiaMes() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

        return cal.getTime();
    }

    public static String obtenerEdad(String lsFechnac) {
        Date ldFechnac = new Date();
        SimpleDateFormat lsFormato = new SimpleDateFormat("dd/MM/yyyy");
        String lsDatehoy = lsFormato.format(ldFechnac);
        String[] fDate_1 = lsFechnac.split("/");
        String[] fDate_2 = lsDatehoy.split("/");
        String liDateano = Integer.toString(Integer.parseInt(fDate_2[2]) - Integer.parseInt(fDate_1[2]));
        int liDatemes = Integer.parseInt(fDate_2[1]) - Integer.parseInt(fDate_1[1]);
        if (liDatemes < 0)
            liDateano = Integer.toString(Integer.parseInt(liDateano) - 1);
        else if (liDatemes == 0) {
            int liDatedia = Integer.parseInt(fDate_2[0]) - Integer.parseInt(fDate_1[0]);
            if (liDatedia > 0)
                liDateano = Integer.toString(Integer.parseInt(liDateano) - 1);
        }
        return liDateano;
    }

    public static List<String> rangoAnios(String anioInicio, String anioFin) {
        int desde = Integer.parseInt(anioInicio);
        int hasta = Integer.parseInt(anioFin);
        List<String> list = new ArrayList<>();
        while (desde <= hasta) {
            list.add(String.valueOf(desde++));
        }
        return list;
    }
}
