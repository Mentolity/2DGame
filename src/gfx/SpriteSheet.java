package gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
	public String path;
	public int width;
	public int height;
	
	public int[] pixels;
	
	public SpriteSheet(String path){
		BufferedImage image = null; //bufers the image with the buffered image class
		//loads the image
		try {
			image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//the image can sometimes be null and returns just in case the catch above fails
		if(image == null){
			return;
		}
		 //intiallizes the class variables for this instance
		this.path = path;
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		//sets the pixel variables
		pixels = image.getRGB(0, 0, width, height, null, 0, width);
		for(int i=0;i<pixels.length;i++){
			//removes alpha channel
			pixels[i] = (pixels[i] & 0xff)/64;
		}
		for(int i = 0; i<8; i++){
			System.out.println(pixels[i]);
		}
	}
}
