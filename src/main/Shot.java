package main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Shot.java
 * Creates a bullet that the player shoots
 * Updates the y value of the shot and checks for collision between the shot and the debris
 * January 21st 2020
 * @author Subodh Neupane, Muntasir Munem, Herman Tran
 */
public class Shot {
	static final Image SHOT_IMG = new Image("shot.png");
	public boolean toRemove;
	int posX,posY,speed = 10;
	static final int SIZE = 30;
	MainApplication ast = new MainApplication();

	/**
	 * Sets the position of the shots
	 * @param posX is the x position of the shot
	 * @param posY is the y position of the shot
	 */
	public Shot(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	/**
	 * This method updates the y position of the shot by incrementing the position by the speed variable
	 */
	public void update(){
		posY += speed;
	}

	/**
	 * This method draws the the shot image
	 * @param gc is the graphics context used to draw the image
	 */
	public void draw(GraphicsContext gc) {
		gc.drawImage(SHOT_IMG,posX, posY, SIZE, SIZE);

	}

	/**
	 * Checks the distance between the shot and the debris and returns the distance
	 * @param Player
	 * @return distance is the distance between the player and the debris
	 */
	public boolean collide(Player Player) {
		int distance = distance(this.posX+SIZE/2,this.posY+SIZE/2,Player.posX+Player.size/2,Player.posY+Player.size/2);
		return distance < Player.size/2+SIZE/2;
	}

	/**
	 * This method returns the distance between the position of the shot and the position of the debris
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return distance
	 */
	int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}
}