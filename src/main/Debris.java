package main;

import javafx.scene.image.Image;

/**
 * Debris.java
 * Sets the position of each debris in the game according to the speed of the debris
 * Destroys the debris if the debris reaches the end of the screen
 * January 21st 2020
 * @author Subodh Neupane, Muntasir Munem, Herman Tran
 */
public class Debris extends Player {
	private int speed = 0;

	/**
	 * A constructor from the superclass that collects the position X and Y, size and image.
	 * It then sets the speed using the equation.
	 * @param posX
	 * @param posY
	 * @param size
	 * @param image
	 * @param speed
	 */
	public Debris(int posX, int posY, int size, Image image, int speed) {
		super(posX, posY, size, image);
		this.speed = speed;
	}

	/**
	 * An update method that changes the Y position of the debris and also checks whether the debris is off the screen.  
	 */
	public void update() {
		super.update();
		// Asteroid's  Y position is decreased alongside the speed for it to go from bottom of the screen to the top.
		if (!exploding && !destroyed) {
			posY-=speed;
		}
		// Debris is destroyed when the position passes 0 for new debris to spawn.
		if (posY == 0) {
			destroyed = true;
		}
	}
	/**
	 * A setter for the speed of the debris
	 * @param speed -
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	/**
	 * A getter for the speed of the debris
	 */
	public int getSpeed() {
		return speed;
	}

}