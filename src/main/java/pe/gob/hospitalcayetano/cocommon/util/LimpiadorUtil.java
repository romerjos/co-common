package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
public final class LimpiadorUtil {

    private LimpiadorUtil() {}

    public static <T> List<T> cleanAttributesList(List<T> objs) {
        if (objs == null) {
            return List.of();
        }

        objs.forEach(LimpiadorUtil::cleanAttributesObject);
        return objs;
    }

    public static <T> T cleanAttributesObject(T obj) {
        if (obj == null) {
            return null;
        }

        Class<?> currentClass = obj.getClass();

        while (currentClass != null && currentClass != Object.class) {
            cleanFields(obj, currentClass);
            currentClass = currentClass.getSuperclass();
        }

        return obj;
    }

    private static <T> void cleanFields(T obj, Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object value = field.get(obj);

                if (value != null) {
                    continue;
                }

                Class<?> type = field.getType();

                if (type == String.class) {
                    field.set(obj, "");
                } else if (type == Integer.class) {
                    field.set(obj, 0);
                } else if (type == Long.class) {
                    field.set(obj, 0L);
                } else if (type == Double.class) {
                    field.set(obj, 0.0);
                } else if (type == BigInteger.class) {
                    field.set(obj, BigInteger.ZERO);
                } else if (type == BigDecimal.class) {
                    field.set(obj, BigDecimal.ZERO);
                } else if (type == LocalDateTime.class) {
                    field.set(obj, LocalDateTime.now());
                } else if (Collection.class.isAssignableFrom(type)) {
                    field.set(obj, new ArrayList<>());
                } else if (type.isArray()) {
                    field.set(obj, Array.newInstance(type.getComponentType(), 0));
                }

            } catch (IllegalAccessException e) {
                log.warn("No se pudo limpiar campo {} en {}", field.getName(), clazz.getName(), e);
            }
        }
    }
}