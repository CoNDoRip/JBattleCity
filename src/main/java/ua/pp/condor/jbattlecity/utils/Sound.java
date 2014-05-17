package ua.pp.condor.jbattlecity.utils;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

public final class Sound {

    private static final String GAME_START = "sound/gamestart.au";
    private static final String BACKGROUND = "sound/background.au";
    private static final String BRICK = "sound/brick.au";
    private static final String FIRE = "sound/fire.au";
    private static final String EXPLOSION = "sound/explosion.au";
    private static final String GAME_OVER = "sound/gameover.au";

    private static AudioClip gameStart;
    private static AudioClip background;
    private static AudioClip brick;
    private static AudioClip fire;
    private static AudioClip explosion;
    private static AudioClip gameOver;

    private Sound() {
    }

    public static void load(Applet applet) {
        URL codeBase = applet.getCodeBase();
        gameStart = applet.getAudioClip(codeBase, GAME_START);
        background = applet.getAudioClip(codeBase, BACKGROUND);
        brick = applet.getAudioClip(codeBase, BRICK);
        fire = applet.getAudioClip(codeBase, FIRE);
        explosion = applet.getAudioClip(codeBase, EXPLOSION);
        gameOver = applet.getAudioClip(codeBase, GAME_OVER);
    }

    public static AudioClip getGameStart() {
        return gameStart;
    }

    public static AudioClip getBackground() {
        return background;
    }

    public static AudioClip getBrick() {
        return brick;
    }

    public static AudioClip getFire() {
        return fire;
    }

    public static AudioClip getExplosion() {
        return explosion;
    }

    public static AudioClip getGameOver() {
        return gameOver;
    }

}
