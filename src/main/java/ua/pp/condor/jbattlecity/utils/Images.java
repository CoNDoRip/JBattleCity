package ua.pp.condor.jbattlecity.utils;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {
	
	private static final String STAGE_1 = "/img/stage_1.gif";
	
	private static final String YOU_UP    = "/img/you_up.gif";
	private static final String YOU_RIGHT = "/img/you_right.gif";
	private static final String YOU_DOWN  = "/img/you_down.gif";
	private static final String YOU_LEFT  = "/img/you_left.gif";
	
	private static final String ENEMY_UP    = "/img/enemy_up.gif";
	private static final String ENEMY_RIGHT = "/img/enemy_right.gif";
	private static final String ENEMY_DOWN  = "/img/enemy_down.gif";
	private static final String ENEMY_LEFT  = "/img/enemy_left.gif";
	
	private static final String PROJECTILE  = "/img/projectile.gif";
	public static final int PROJECTILE_SIZE  = 3;
	
	private static final String GAME_OVER  = "/img/game_over.gif";

	private static Image stage1;
	
	private static Image youUp;
	private static Image youRight;
	private static Image youDown;
	private static Image youLeft;
	
	private static Image enemyUp;
	private static Image enemyRight;
	private static Image enemyDown;
	private static Image enemyLeft;
	
	private static Image projectile;
	
	private static Image gameOver;
	
	public static Image getImage(String name) {
		Image image = null;
		try {
			image = ImageIO.read(Images.class.getResourceAsStream(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static Image getStage1() {
		if (stage1 == null)
			stage1 = getImage(STAGE_1);
		return stage1;
	}
	
	public static Image getYouUp() {
		if (youUp == null)
			youUp = getImage(YOU_UP);
		return youUp;
	}
	
	public static Image getYouRight() {
		if (youRight == null)
			youRight = getImage(YOU_RIGHT);
		return youRight;
	}
	
	public static Image getYouDown() {
		if (youDown == null)
			youDown = getImage(YOU_DOWN);
		return youDown;
	}
	
	public static Image getYouLeft() {
		if (youLeft == null)
			youLeft = getImage(YOU_LEFT);
		return youLeft;
	}

	public static Image getEnemyUp() {
		if (enemyUp == null)
			enemyUp = getImage(ENEMY_UP);
		return enemyUp;
	}

	public static Image getEnemyRight() {
		if (enemyRight == null)
			enemyRight = getImage(ENEMY_RIGHT);
		return enemyRight;
	}

	public static Image getEnemyDown() {
		if (enemyDown == null)
			enemyDown = getImage(ENEMY_DOWN);
		return enemyDown;
	}

	public static Image getEnemyLeft() {
		if (enemyLeft == null)
			enemyLeft = getImage(ENEMY_LEFT);
		return enemyLeft;
	}

	public static Image getProjectile() {
		if (projectile == null)
			projectile = getImage(PROJECTILE);
		return projectile;
	}

	public static Image getGameOver() {
		if (gameOver == null)
			gameOver = getImage(GAME_OVER);
		return gameOver;
	}

}
