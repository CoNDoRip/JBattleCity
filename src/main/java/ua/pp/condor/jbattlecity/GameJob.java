package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.net.Socket;

public class GameJob extends Thread {

    private static final Logger logger = Logger.getLogger(GameJob.class);

    private final Socket firstPlayer;
    private final Socket secondPlayer;

    public GameJob(Socket firstPlayer, Socket secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    @Override
    public void run() {
        logger.debug("started with id = " + getId());
        //TODO Auto-generated method stub
    }

}
