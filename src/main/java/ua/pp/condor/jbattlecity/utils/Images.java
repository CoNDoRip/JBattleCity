package ua.pp.condor.jbattlecity.utils;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Images {
	
	public static final String STAGE_1 = "/img/stage1.gif";
	
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
