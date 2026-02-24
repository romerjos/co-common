package pe.gob.hospitalcayetano.cocommon.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingRepositoryAspect {

    @Around("execution(* pe.gob.hospitalcayetano..repository.impl..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();

        MethodSignature valorMetodo = (MethodSignature) joinPoint.getSignature();
        String nombreMetodo = valorMetodo.getDeclaringType().getSimpleName() + "." + valorMetodo.getName();

        log.info("Iniciando método: {}", nombreMetodo);

        Object[] parametros = joinPoint.getArgs();
        String[] nombreParametros = valorMetodo.getParameterNames();

        if (nombreParametros != null && parametros.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < parametros.length; i++) {
                sb.append(nombreParametros[i]).append("=").append(parametros[i]);
                if (i < parametros.length - 1) sb.append(", ");
            }

            log.info("Parámetros: {}", sb);
        }

        Object metodoOriginal;

        try {
            metodoOriginal = joinPoint.proceed();
            long duracion = System.currentTimeMillis() - inicio;
            log.info("Finalizando {} método en {} ms", nombreMetodo, duracion);
        } catch (Throwable t) {
            log.error("Error en {}: {}", nombreMetodo, t.getMessage(), t);
            throw t;
        }

        return metodoOriginal;
    }
}
