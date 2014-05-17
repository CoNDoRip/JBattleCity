package ua.pp.condor.jbattlecity;

import org.apache.log4j.Logger;

public class JBattleCityServer {

    private static final Logger logger = Logger.getLogger(JBattleCityServer.class);

    public static void main(String[] args) {
        logger.debug("Server started");
        if (args.length != 1) {
            logger.error("Run again as: java JBattleCityServer <port_number>");
        }
    }

}
