package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputReader extends Thread {

    private static final Logger logger = Logger.getLogger(InputReader.class);

    private final int playerId;
    private final BufferedInputStream in;
    private final OutputStream out;

    public InputReader(int playerId, BufferedInputStream in, OutputStream out) {
        this.playerId = playerId;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        logger.debug("started with id = " + playerId);
        int count;
        byte[] buff = new byte[Protocol.BUF_SIZE];
        try {
            while ((count = in.read(buff)) >= 0) {
                logger.debug("Readed " + count + " bytes from "
                        + (playerId == 1 ? "first" : "second") + " player");
                out.write(buff);
                NetworkUtils.bzero(buff);

            }
            logger.debug("stoped with id = " + playerId);
            interrupt();
        } catch (IOException e) {
            logger.error("Player " + playerId + ": " + e);
        } catch (Exception e) {
            logger.error("Player " + playerId + ": " + e);
        }
    }

}
