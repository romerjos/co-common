package pe.gob.hospitalcayetano.cocommon.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@Slf4j
public final class InformacionRequestUtilitario {

	private static List<String> listaNegraIp;

	private InformacionRequestUtilitario() {}

	@Value("${lista.negra.ip:#{T(java.util.Collections).emptyList()}}")
	public void setListaNegraIp(List<String> listaNIp) {
		listaNegraIp = listaNIp;
	}

	public static String obtenerRequestIP(HttpServletRequest request) {
		try {
			String xfHeader = request.getHeader("X-Forwarded-For");

			log.info("xfHeader (IP): {}", xfHeader);

			if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
				String remoteAddr = request.getRemoteAddr();

				log.info("RemoteAddr (IP): {}", remoteAddr);

				return remoteAddr;
			}

			return xfHeader.split(",")[0];
		} catch (Exception e) {
			log.info("Error: {}", e.getMessage());
			return "";
		}
	}

	public static boolean validarIpEnListaNegra(String ipRequest) {
		if (listaNegraIp != null && !listaNegraIp.isEmpty()) {
			log.info("listaNegraIp: {}", listaNegraIp);
			boolean ipRequestDentroListaNegra = listaNegraIp.contains(ipRequest);
			log.info("ipRequestDentroListaNegra: {}", ipRequestDentroListaNegra);
			return ipRequestDentroListaNegra;
		} else {
			return false;
		}
	}

}
