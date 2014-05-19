package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputReader extends Thread {

    private static final Logger logger = Logger.getLogger(InputReader.class);

    private final BufferedInputStream in;
    private final int playerId;

    public InputReader(BufferedInputStream in, int playerId) {
        this.in = in;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        logger.debug("started with id = " + playerId);
        int count;
        byte[] buff = new byte[10];
        try {
            while ((count = in.read(buff)) >= 0) {
                logger.debug("Readed " + count + " bytes from "
                        + (playerId == 1 ? "first" : "second") + " player");
            }
            logger.debug("stoped with id = " + playerId);
            interrupt();
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
