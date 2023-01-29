package main;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Player.java
 * Creates and draws the player as well as any explosion animation for the player
 * Checks for the collision between the player and debris
 * January 21st 2020
 * @author Subodh Neupane, Muntasir Munem, Herman Tran
 */
public class Player{
	static final Image EXPLOSION_IMG = new Image("explosion1.png");
	static final int EXPLOSIONS_W = 128;
	static final int EXPLOSIONS_H = 128;
	static final int EXPLOSIONS_COL = 3;
	static final int EXPLOSIONS_ROW = 3;
	static final int EXPLOSIONS_STEP = 15;


	int posX,posY,size;
	boolean exploding,destroyed;
	Image img;
	int explosionStep = 0;
	/**
	 * This method sets the players' position and sets the players' image
	 * @param posX
	 * @param posY
	 * @param size
	 * @param image
	 */
	public Player(int posX,int posY, int size, Image image) {
		this.posX= posX;
		this.posY= posY;
		this.size= size;
		img = image;
	}

	/**
	 * Returns the shot that the player shoots along with its position.
	 * @return Shot- The laser that is shot by the player
	 */
	public Shot shoot() {
		return new Shot(posX+size/2-Shot.SIZE/2, posY+Shot.SIZE);
	}

	/**
	 * This method goes through each explosion image to create an animation of an explosion
	 */
	public void update() {
		//Increases the variable to get the next explosion image
		if (exploding) {
			explosionStep++;
		}
		destroyed = explosionStep > EXPLOSIONS_STEP;
	}

	/**
	 * Draws the picture of the explosion if the player is exploding or draws picture of the player/character if not exploding.
	 * @param gc - Graphics context used to draw the image
	 */
	public void draw(GraphicsContext gc) {
		// Checks if the player is exploding
		if(exploding) {
			gc.drawImage(EXPLOSION_IMG, explosionStep %EXPLOSIONS_COL*EXPLOSIONS_W,
					(explosionStep/EXPLOSIONS_ROW)*EXPLOSIONS_H+1,EXPLOSIONS_W,EXPLOSIONS_H,posX,posY,size,size);
		}
		else {
			gc.drawImage(img, posX, posY,size,size);
		}
	}

	/**
	 * Checks the distance between the shot and the debris and returns the distance
	 * @param Player
	 * @return distance is the distance between the shot and the debris
	 */
	public boolean collide(Player other) {
		int dis = distance(this.posX+size/2,this.posY+size/2,other.posX+other.size/2,other.posY+other.size/2);
		return dis < other.size/2+this.size/2;
	}

	/**
	 * This method returns the distance between the position of the player and the position of the debris
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return distance
	 */
	public int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	/**
	 * This method explodes the player
	 * Sets the explosion image to the -1 so the explosion shows the full animation
	 */
	public void explode() {
		exploding = true;
		explosionStep = -1;
	}
}