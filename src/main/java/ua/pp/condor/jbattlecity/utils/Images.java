package ua.pp.condor.jbattlecity.utils;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {
	
	public static final String STAGE_1 = "/img/stage1.gif";
	
	public static final String YOU = "/img/you.gif";
	public static final String YOU_UP = "/img/you_up.gif";
	public static final String YOU_DOWN = "/img/you_down.gif";
	public static final String YOU_LEFT = "/img/you_left.gif";
	public static final String YOU_RIGHT = "/img/you_right.gif";
	
	public static Image getImage(String name) {
		Image image = null;
		try {
			image = ImageIO.read(Images.class.getResourceAsStream(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

}
