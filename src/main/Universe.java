package main;

import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * MainApplication.java
 * Spawns in "stars" that move from the top of the screen to the bottom in the background
 * January 21st 2020
 * @author Subodh Neupane, Muntasir Munem, Herman Tran
 */
public class Universe{
	private static final Random RAND = new Random();
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	Canvas canvas = new Canvas(WIDTH, HEIGHT);
	private GraphicsContext gc = canvas.getGraphicsContext2D();
	int posX,posY;
	private int a,b,c,d,e;
	private double clear;
	boolean pause = false;

	/**
	 * Sets the gc with the stars in a random X position with a random size and a random opacity.
	 * @param gc
	 */
	public Universe(GraphicsContext gc){
		// Random X position for the stars to spawn in.
		if(pause == false) {
			posX = RAND.nextInt(WIDTH);
			posY = 0;
			// Random size for the stars.
			a = RAND.nextInt(5)+1;
			b = RAND.nextInt(5)+1;
			// Random opacity for the stars.
			c = RAND.nextInt(100)+150;
			d = RAND.nextInt(100)+150;
			e = RAND.nextInt(100)+150;
			clear = RAND.nextFloat();
			this.gc=gc;
		}
	}
	/**
	 * This method draws the the different stars in the background
	 * @param gc is the graphics context used to draw the image
	 */
	public void draw() {
		if(pause == false) {
			// Used to create stars with different varieties of brightness.
			if(clear > 0.8)
				clear -= 0.01;
			if(clear < 0.1)
				clear += 0.01;
			// Fills the oval with a random opacity for the star.
			gc.setFill(Color.rgb(c, d, e, clear));
			// Draws the oval with a random X position and the random size.
			gc.fillOval(posX, posY, b, a);
			// Position of the stars increase as they go from top of the screen to the bottom.
			posY+=20;
		}
	}

	int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}
}