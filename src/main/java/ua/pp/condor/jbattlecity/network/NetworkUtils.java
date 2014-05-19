package ua.pp.condor.jbattlecity.network;

public final class NetworkUtils {

    private NetworkUtils() {}

    public static void bzero(byte[] buf) {
        bzero(buf, 0);
    }

    public static void bzero(byte[] buf, int from) {
        for (int i = from; i < buf.length; i++) {
            buf[i] = 0;
        }
    }

}
