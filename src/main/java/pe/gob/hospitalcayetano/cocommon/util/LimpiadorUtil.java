package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public final class LimpiadorUtil {

    private LimpiadorUtil() {
    }

    public static <T> List<T> cleanAttributesList(List<T> objs) {
        try {
            if (objs == null) {
                return new ArrayList<>();
            }

            for (T item : objs) {
                Field[] atributos = item.getClass().getDeclaredFields();

                cleanAttributes(item, atributos);

                if (item.getClass().getSuperclass() != null&& item.getClass().getSuperclass() != Object.class) {
                    cleanAttributesSuperclasse(item, item.getClass().getSuperclass());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return objs;
    }

    public static <T> T cleanAttributesObject(T obj) {
        try {
            if (obj == null) {
                return null;
            }

            Field[] atributos = obj.getClass().getDeclaredFields();

            cleanAttributes(obj, atributos);

            if (obj.getClass().getSuperclass() != null&& obj.getClass().getSuperclass() != Object.class) {
                cleanAttributesSuperclasse(obj, obj.getClass().getSuperclass());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return obj;
    }

    private static <T> void cleanAttributesSuperclasse(T obj, Class classT) throws Exception {
        Field[] atributos = classT.getDeclaredFields();

        cleanAttributes(obj, atributos);

        if (classT.getSuperclass() != null && classT.getSuperclass() != Object.class) {
            cleanAttributesSuperclasse(obj, classT.getSuperclass());
        }
    }

    private static <T> void cleanAttributes(T obj, Field[] atributos) throws Exception {
        try {
            for (Field field : atributos) {
                field.setAccessible(true);

                if (field.getType().equals(int.class) && field.get(obj) == null) {
                    field.set(obj, 0);
                } else if (field.getType().equals(String.class) && field.get(obj) == null) {
                    field.set(obj, "");
                } else if (field.getType().equals(Integer.class) && field.get(obj) == null) {
                    field.set(obj, 0);
                } else if (field.getType().equals(Double.class) && field.get(obj) == null) {
                    field.set(obj, 0.0);
                } else if (field.getType().equals(Date.class) && field.get(obj) == null) {
                    field.set(obj, DateUtil.getDateTimeZone(DateUtil.DATETIME_FORMAT));
                } else if (field.getType().equals(List.class) && field.get(obj) == null) {
                    field.set(obj, new ArrayList<>());
                } else if (field.getType().isArray() && field.get(obj) == null) {
                    field.set(obj, Array.newInstance(field.getType().getComponentType(), 0));
                } else if (field.getType().equals(Long.class) && field.get(obj) == null) {
                    field.set(obj, 0L);
                } else if (field.getType().equals(BigInteger.class) && field.get(obj) == null) {
                    field.set(obj, BigInteger.valueOf(0));
                } else if (field.getType().equals(BigDecimal.class) && field.get(obj) == null) {
                    field.set(obj, BigDecimal.valueOf(0.0));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

}
