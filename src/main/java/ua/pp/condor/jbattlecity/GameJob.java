package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class GameJob extends Thread {

    private static final Logger logger = Logger.getLogger(GameJob.class);

    private final Socket firstPlayer;
    private final Socket secondPlayer;

    private InputStream fIn;
    private OutputStream fOut;
    private InputReader fReader;

    private InputStream sIn;
    private OutputStream sOut;
    private InputReader sReader;

    public GameJob(Socket firstPlayer, Socket secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;

        try {
            fIn = firstPlayer.getInputStream();
            fOut = firstPlayer.getOutputStream();
            fReader = new InputReader(fIn, 1);

            sIn = secondPlayer.getInputStream();
            sOut = secondPlayer.getOutputStream();
            sReader = new InputReader(sIn, 2);
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
            //Send START_GAME code with playerId to both players
            byte[] buf = new byte[2];
            buf[0] = Protocol.START_GAME;
            buf[1] = 1;
            fOut.write(buf);
            buf[1] = 2;
            sOut.write(buf);

            fReader.start();
            sReader.start();
        } catch (IOException e) {
            logger.error("Problems with socket", e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        firstPlayer.close();
        secondPlayer.close();
        super.finalize();
    }

}
