package pe.gob.hospitalcayetano.cocommon.servletloggin;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CacheBodyServletInputStream extends ServletInputStream {

    private final InputStream cacheBodyInputStream;

    public CacheBodyServletInputStream(byte[] cacheBody) {
        this.cacheBodyInputStream = new ByteArrayInputStream(cacheBody);
    }

    /**
     * Indicates whether InputStream has more data to read or not.
     *
     * @return true when zero bytes available to read
     */
    @Override
    public boolean isFinished() {
        try {
            return cacheBodyInputStream.available() == 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Indicates whether InputStream is ready for reading or not.
     * Since we've already copied InputStream in a byte array, we'll return true to indicate that it's always available.
     *
     * @return true
     */
    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
        return cacheBodyInputStream.read();
    }
}
