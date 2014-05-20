package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
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
        byte[] buf = new byte[Protocol.BUF_SIZE];
        try {
            while ((count = in.read(buf)) >= 0) {
                logger.debug("Readed " + count + " bytes from "
                        + (playerId == 1 ? "first" : "second") + " player: "
                        + buf[0] + "-" + buf[1] + "-" + buf[2] + "-" + buf[3]);
                out.write(buf);
                NetworkUtils.bzero(buf);

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
