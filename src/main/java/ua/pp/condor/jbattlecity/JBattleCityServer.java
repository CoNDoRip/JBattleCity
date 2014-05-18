package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class JBattleCityServer {

    private static final Logger logger = Logger.getLogger(JBattleCityServer.class);

    public static void main(String[] args) {
        if (args.length != 1) {
            logger.error("Run again as: java JBattleCityServer <port_number>");
            System.exit(1);
        }
        int portNumber = 0;
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            logger.error("Can not parse port number", e);
            System.exit(1);
        }

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            logger.debug("successfully binded to port " + portNumber);
            while (true) {
                Socket firstPlayer = serverSocket.accept();
                logger.info("First player connected from " + firstPlayer.getInetAddress().getHostAddress());
                Socket secondPlayer = serverSocket.accept();
                logger.info("Second player connected from " + secondPlayer.getInetAddress().getHostAddress());
                new GameJob(firstPlayer, secondPlayer).start();
            }
        } catch (IOException e) {
            logger.error("Can not open a ServerSocket", e);
        }
    }

}
