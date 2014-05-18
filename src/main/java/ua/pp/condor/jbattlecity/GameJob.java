package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class GameJob extends Thread {

    private static final Logger logger = Logger.getLogger(GameJob.class);

    private final Socket firstPlayer;
    private final Socket secondPlayer;

    private InputStream fIn;
    private BufferedOutputStream fOut;

    private InputStream sIn;
    private BufferedOutputStream sOut;

    public GameJob(Socket firstPlayer, Socket secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;

        try {
            fIn = firstPlayer.getInputStream();
            fOut = new BufferedOutputStream(firstPlayer.getOutputStream());
            sIn = secondPlayer.getInputStream();
            sOut = new BufferedOutputStream(secondPlayer.getOutputStream());
        } catch (IOException e) {
            logger.error("Can not get input of output stream of one of players", e);
            interrupt();
        }
    }

    @Override
    public void run() {
        logger.debug("started with id = " + getId());
        //TODO Auto-generated method stub
        try {
            sendToBoth(Protocol.START_GAME);
            for (;;) {
                if (firstPlayer.isClosed() || secondPlayer.isClosed()) {
                    interrupt();
                }
            }
        } catch (IOException e) {
            logger.error("Problems with socket", e);
        }
    }

    private void sendToBoth(byte code) throws IOException {
        fOut.write(code);
        fOut.flush();
        sOut.write(code);
        sOut.flush();
    }

    @Override
    protected void finalize() throws Throwable {
        fIn.close();
        fOut.close();
        sIn.close();
        sOut.close();
        firstPlayer.close();
        secondPlayer.close();
        super.finalize();
    }

}
