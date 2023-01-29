package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * MainApplication.java
 * Runs the Free Fall game
 * This game includes a player on the top of the screen falling, dodging and shooting asteroids
 * The player has three lives, once the player loses all three lives, the game is over
 * The score increases as the player shoots the asteroid
 * January 21st 2020
 * @author Subodh Neupane, Muntasir Munem, Herman Tran
 */
public class MainApplication extends Application {

	private static final Random RAND = new Random();
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 600;
	private static final int PLAYER_SIZE = 100;
	private static final int AST_SIZE = 80;
	static final Image PLAYER_IMG = new Image("alien.png");
	static final Image SCARED_PLR = new Image("scared.png");
	static final Image BACKGROUND_IMG = new Image("bg.gif", WIDTH, HEIGHT, false, false);
	static final Image PAUSED = new Image("pause.jpg",WIDTH,HEIGHT,false,false);
	static final Image GO = new Image("GO.jpg",WIDTH,HEIGHT,false,false);
	static Image LIVES_IMG = new Image ("heart.png",40,40,false,false);
	static Image LIVES2_IMG = new Image ("heart.png",40,40,false,false);
	static Image LIVES3_IMG = new Image ("heart.png",40,40,false,false);
	static final int EXPLOSIONS_W = 128;
	static final int EXPLOSIONS_H = 128;
	static final int EXPLOSIONS_COL = 3;
	static final int EXPLOSIONS_ROW = 3;
	static final int EXPLOSIONS_STEP = 15;
	static final Image IMAGES[] = {
			new Image ("asteroid1.png"),
			new Image ("asteroid2.png"),
			new Image ("debris2.png"),
			new Image ("debris3.png"),
			new Image ("debris4.png"),
			new Image ("debris5.png"),
			new Image ("debris6.png"),
			new Image ("debris7.png"),
			new Image ("debris8.png")
	};
	private int lives = 3;
	boolean dead = false;
	boolean gameOver = false;
	private GraphicsContext gc;
	Player player;
	List<Shot>shots;
	List<Universe>univ;
	List<Debris>asteroids;
	public int score = 0;
	boolean pause;
	//Sets speed for the timeline of the game
	Timeline timeline = new Timeline(new KeyFrame(Duration.millis(30), e -> run(gc)));
	int speed = 2;
	int ammo = 15;
	Scene scene;

	public static void main(String[]args) {
		launch();
	}

	/**
	 *This method runs the game, adds a console for the game adds any sound effects,
	 *adds any buttons and sets each scene
	 *Starts the playable game, shows instructions, exits the console
	 */
	public void start(Stage primaryStage) throws Exception {
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		gc = canvas.getGraphicsContext2D();
		primaryStage.setScene(new Scene(new StackPane(canvas)));
		primaryStage.setTitle("Free Fall");

		//Creates a background image for the title screen
		//Image is set to the width and height of the canvas
		InputStream is = Files.newInputStream(Paths.get("src/b.gif"));

		//Sets image for the menu
		Image menuScreen = new Image(is);
		is.close();
		ImageView iv = new ImageView(menuScreen);
		iv.setFitWidth(WIDTH);
		iv.setFitHeight(HEIGHT);
		StackPane pane = new StackPane();

		//Plays background music for the menu
		File newFile = new File ("src/backMusic.wav");
		Media newSound = null;
		try {
			newSound = new Media (newFile.toURI().toURL().toString());
		}
		catch (MalformedURLException g) {
			g.printStackTrace();
		}
		MediaPlayer backMusic = new MediaPlayer(newSound);
		//Plays music until backMusic.stop is called
		backMusic.setCycleCount(MediaPlayer.INDEFINITE);
		backMusic.play();

		//Button created called start
		//Repositioned to the center
		Button start = new Button("Start");
		start.setTranslateY(-20);
		start.setPrefSize(500, 50);
		//The button style is edited to be transparent
		//Button has a transparent background with white text with a font size of 45
		start.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 45px");
		//When the mouse enters the button, the button's text color changes to gray and font increases to 60
		//This is for a pop up effect
		start.setOnMouseEntered(e->{
			start.setStyle("-fx-background-color:transparent; -fx-text-fill: gray; -fx-font-size: 60px");
			//Plays sound when the mouse enters the button
			//Not looped to make an effect
			File file = new File ("src/button.wav");
			Media sound=null;
			try {
				sound = new Media (file.toURI().toURL().toString());
			}
			catch (MalformedURLException g) {
				g.printStackTrace();
			}
			MediaPlayer music = new MediaPlayer(sound);
			music.play();
		});
		//When the mouse leaves the button, the text color changes back to white and font changes back to 45
		start.setOnMouseExited(e ->{
			start.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 45px");
		});
		//When the button is clicked, the code executes
		//Game begins
		start.setOnMouseClicked(e -> {
			//Stops the menu music
			backMusic.stop();
			//Plays in game music
			File file1 = new File ("src/musicM.wav");
			Media soundM = null;
			try {
				soundM = new Media (file1.toURI().toURL().toString());
			}
			catch (MalformedURLException g) {
				g.printStackTrace();
			}
			MediaPlayer musicM = new MediaPlayer(soundM);
			//Plays the in game music until stopped
			musicM.setCycleCount(MediaPlayer.INDEFINITE);
			musicM.play();

			//The timeline begins and runs infinitely
			timeline.setCycleCount(Timeline.INDEFINITE);
			timeline.play();
			//Used for enabling keyboard use
			canvas.setFocusTraversable(true);
			//Checks which key is pressed
			canvas.setOnKeyPressed(f -> {
				//When the key 'P' is pressed, the pause boolean is set to true
				//This pauses the game
				if(f.getCode() == KeyCode.P) {
					pause = true;
				}
				//If the boolean pause is false, the game is playing
				//The player is able to move and shoot
				if(pause == false) {
					//Checks if the player is within the parameters of the stage
					//The player can move and shoot if they are inside the parameters
					if(player.posX < WIDTH-100 && player.posX > 0 && player.posY < HEIGHT && player.posY > 0) {
						//If the left arrow key or the 'A' key is pressed the player moves left
						if (f.getCode() == KeyCode.LEFT || f.getCode() == KeyCode.A) {
							left();
						}
						//If the right arrow key or the 'D' key is pressed the player moves left
						else if (f.getCode() == KeyCode.RIGHT || f.getCode() == KeyCode.D) {
							right();
						}
						//If the 'P' key is pressed, the boolean pause is set to true, this pauses the game
						else if(f.getCode() == KeyCode.P) {
							pause = true;
						}
					}
					//If the player is not within the parameters of the stage, the player is "bounced" back onto the stage
					else {
						//If the player is on the far right of the stage, the player is bounced to the left
						if(player.posX >= WIDTH-100) {
							bounceLeft();
						}
						//If the player is on the far left of the stage, the player is bounced to the right
						else if(player.posX <= 0) {
							bounceRight();
						}
					}
				}
				//If the boolean pause is true the following code is executed
				else {
					//If the enter key is pressed, the boolean pause is set to false
					//This will resume the game
					if(f.getCode() == KeyCode.ENTER) {
						pause = false;
					}
				}
			});
			//Check which key was pressed then released
			canvas.setOnKeyReleased (f -> {
				//The following code executes when the game is not paused
				if(pause == false) {
					//Lets the player shoot if the ammo is greater than 0
					if(ammo > 0 && gameOver == false) {
						//Shoots if the player presses and releases space
						if (f.getCode() == KeyCode.SPACE) {
							//Player shoots and animation is shown
							shots.add(player.shoot());
							//For every shot, an ammo is reduced
							ammo--;
							//Plays a shot sound effect each time the player shoots
							File file = new File ("src/shot.wav");
							Media sound=null;
							try {
								sound = new Media (file.toURI().toURL().toString());
							}
							catch (MalformedURLException g) {
								g.printStackTrace();
							}
							MediaPlayer music = new MediaPlayer(sound);
							music.play();
						}
					}
					//Plays a sound effect if the player does not have any ammo
					else if(ammo == 0 && gameOver == false){
						//Plays the sound if player presses and releases space
						if(f.getCode() == KeyCode.SPACE) {
							File file = new File ("src/empty.wav");
							Media sound=null;
							try {
								sound = new Media (file.toURI().toURL().toString());
							}
							catch (MalformedURLException g) {
								g.printStackTrace();
							}
							MediaPlayer music = new MediaPlayer(sound);
							music.play();
						}
					}
					//If the game is over a new screen is shown
					//Sends scores to the text file
					if(gameOver) {
						//Stops the in game music
						musicM.stop();

						//If the user presses and releases the enter key, the game restarts
						//The game is set to exactly how it is supposed to be in the beginning
						if(f.getCode() == KeyCode.ENTER) {
							gameOver = false;
							score = 0;
							ammo = 15;
							dead = false;
							lives = 3;
							setup();
							musicM.play();
						}
						//If the user enters the backspace key, the screen is sent to the main menu
						if(f.getCode() == KeyCode.BACK_SPACE) {
							//If the game is started, a new game will be created
							gameOver = false;
							//The scene is set to the menu
							primaryStage.setScene(scene);
							//The background music is set to play again
							backMusic.play();
						}
					}
					//If the ammo is not full, the code is executed
					if (ammo < 15) {
						//If the R key is pressed then released, reloads the ammo
						if(f.getCode() == KeyCode.R) {
							//Reloads ammo
							ammo = 15;
							//A reloading sound effect is played
							File file = new File ("src/reload.wav");
							Media sound=null;
							try {
								sound = new Media (file.toURI().toURL().toString());
							}
							catch (MalformedURLException g) {
								g.printStackTrace();
							}
							MediaPlayer music = new MediaPlayer(sound);
							music.play();
						}
					}
				}

				//If the game is paused, and the user presses and releases enter, the game is resumed
				if(pause) {
					//If the user presses backspace, the user is sent to the main menu
					if(f.getCode() == KeyCode.BACK_SPACE) {
						//Does not bring the user to paused screen when game begins again
						pause = false;
						primaryStage.setScene(scene);
					}
					//If the user presses enter, the game is resumed
					if(f.getCode() == KeyCode.ENTER) {
						pause = false;
					}
				}

				//If the escape key is pressed, the boolean gameOver is set to true
				//This sends the player to the game over screen
				if(f.getCode() == KeyCode.ESCAPE) {
					gameOver = true;
				}
			});

			//Goes to the setup method which creates each thing in the game
			setup();
			//Shows the stage with the game
			primaryStage.setScene(new Scene(new StackPane(canvas)));
			primaryStage.setTitle("Space Invaders");
			primaryStage.show();
		});

		//A new button called instructions is created
		Button instructions = new Button("Instructions");
		//The position and size is edited to stay centered
		instructions.setTranslateY(60);
		instructions.setPrefSize(400, 50);
		//The style is changed to make a transparent background with white text and 45 font size
		instructions.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 45px");
		//When the mouse enter the button, an effect is added to the button
		instructions.setOnMouseEntered(e->{
			//The button style changes to gray text, 60 font
			instructions.setStyle("-fx-background-color:transparent; -fx-text-fill: gray; -fx-font-size: 60px");
			//A sound effect is played when the mouse enters the button
			File file = new File ("src/button.wav");
			Media sound=null;
			try {
				sound = new Media (file.toURI().toURL().toString());
			}
			catch (MalformedURLException g) {
				g.printStackTrace();
			}
			MediaPlayer music = new MediaPlayer(sound);
			music.play();
		});
		//When the mouse exits the button, the style is changed back to the original style
		instructions.setOnMouseExited(e ->{
			instructions.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 45px");
		});
		//When the mouse clicks on the instructions button, an instructions page is shown
		instructions.setOnMouseClicked(e -> {
			//Tries to show the instruction screen image
			try {
				//Adds the instructions image, fits the image to the correct width and height of the stage
				Image ins = new Image(new FileInputStream("src/instructions.png"));
				ImageView imv = new ImageView(ins);
				imv.setFitHeight(HEIGHT);
				imv.setFitWidth(WIDTH);
				StackPane pane1 = new StackPane();
				//Creates a new button inside of the instructions screen called back
				//Positions and sizes the button to center itself
				Button back = new Button("Back");
				back.setTranslateX(425);
				back.setTranslateY(-260);
				//Sets the style of the button to transparent, white text, 50 font size
				back.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 50px");
				//Adds an effect when the mouse enters the button
				back.setOnMouseEntered(f -> {
					//Button style is changed to gray text, 70 font size
					back.setStyle("-fx-background-color:transparent; -fx-text-fill: gray; -fx-font-size: 70px");
					//Adds a sound effect when mouse enters the button
					File file = new File ("src/button.wav");
					Media sound=null;
					try {
						sound = new Media (file.toURI().toURL().toString());
					}
					catch (MalformedURLException g) {
						g.printStackTrace();
					}
					MediaPlayer music = new MediaPlayer(sound);
					music.play();
				});
				//When the mouse exits the button, the style is changed to what it was originally
				back.setOnMouseExited(f -> {
					back.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 50px");
				});
				//When the button is clicked, the scene is changed to the menu screen
				back.setOnMouseClicked(f -> {
					primaryStage.setScene(scene);
				});

				//Adds the image and the back button to the scene and sets the scene
				pane1.getChildren().addAll(imv, back);
				Scene insScene = new Scene(pane1);
				primaryStage.setScene(insScene);
			}
			//Catches an error if the image cannot be found
			catch (FileNotFoundException ex) {
				System.out.println("Cant find");
			}

		});


		//Creates a new button called exit
		//Positions and sizes it to center itself
		Button exit = new Button("Exit");
		exit.setTranslateY(150);
		exit.setPrefSize(300, 50);
		//The style is set to a transparent background, white text, and 45 font size
		exit.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 45px");
		//When the mouse enters the button the style is changed to gray text and 60 font size
		//A sound effect is added when the mouse enters the button
		exit.setOnMouseEntered(e->{
			exit.setStyle("-fx-background-color:transparent; -fx-text-fill: gray; -fx-font-size: 60px");
			File file = new File ("src/button.wav");
			Media sound=null;
			try {
				sound = new Media (file.toURI().toURL().toString());
			}
			catch (MalformedURLException g) {
				g.printStackTrace();
			}
			MediaPlayer music = new MediaPlayer(sound);
			music.play();
		});
		//When the mouse exits the button, the style is changed back to its original style
		exit.setOnMouseExited(e ->{
			exit.setStyle("-fx-background-color:transparent; -fx-text-fill: white; -fx-font-size: 45px");
		});
		//When the button is clicked, the program terminates
		exit.setOnMouseClicked(e -> System.exit(0));
		//adds the background image as well as all three buttons to the scene
		//Shows the stage
		pane.getChildren().addAll(iv, start, instructions, exit);
		scene = new Scene(pane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}


	/**
	 * This method moves the player position 40 pixels to the left
	 */
	private void left() {
		player.posX -= 40;
	}
	/**
	 * This method moves the player position 40 pixels to the right
	 */
	private void right() {
		player.posX += 40;
	}
	/**
	 * This method moves the player into the frame (near the right side of the screen)
	 * of the game when player attempts to leave the frame
	 */
	private void bounceLeft() {
		player.posX = WIDTH - 110;
	}
	/**
	 * This method moves the player into the frame (near the left side of the screen)
	 * of the game when player attempts to leave the frame
	 */
	private void bounceRight() {
		player.posX = 10;
	}
	/**
	 * This method is sets every element of the game up
	 * This includes:
	 * The universe (stars that move in the background)
	 * Shots (the shots that the player shoot)
	 * Asteroids (the debris)
	 * The player
	 */
	private void setup() {
		//Everything is set for a new game, score is set to 0
		univ = new ArrayList<>();
		shots = new ArrayList<>();
		asteroids= new ArrayList<>();
		player = new Player(350, 15, PLAYER_SIZE, PLAYER_IMG);
		score = 0;
		//These are the images of the number of lives that the player has set to 3 lives
		LIVES_IMG = new Image ("heart.png",40,40,false,false);
		LIVES2_IMG = new Image ("heart.png",40,40,false,false);
		LIVES3_IMG = new Image ("heart.png",40,40,false,false);
		//Adds 5 new debris to the scene and chooses 5 random images
		IntStream.range(0, 7).mapToObj(i -> this.newDebris()).forEach(asteroids::add);
	}

	/**
	 * This method adds/removes images as well as texts to the screen
	 * Updates and draws game pieces such as the player, the shot and debris
	 * Checks for any collisions and destroys the item if collided
	 * Changes the speed at certain scores with an equation as well as with if statements
	 * @param gc is the graphics context used to draw, add texts, etc.
	 */
	private void run(GraphicsContext gc) {
		//This is the background image in game
		gc.drawImage(BACKGROUND_IMG,0,0);

		//Adds the score, ammo, and the number of lives left for the player
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(Font.font(20));
		gc.setFill(Color.WHITE);
		gc.fillText("Score: " + score, 60,  20);
		gc.fillText("Ammo: " +ammo, 60, 42 );
		gc.drawImage(LIVES_IMG, 175, 0);
		gc.drawImage(LIVES2_IMG, 225, 0);
		gc.drawImage(LIVES3_IMG, 275, 0);

		//If the game is paused, a paused screen image is shown
		if(pause) {
			gc.drawImage(PAUSED, 0, 0);
			return;
		}
		//If the player dies (collides with an asteroid), a life is lost
		//Player images as well as lives images are changed
		if(dead) {
			lives--;
			//When the user dies, all asteroids explode in the screen
			for (Debris ast :asteroids) {
				ast.explode();
			}

			ammo = 15;
			//This is the player image if the score is less than 20
			if (score < 20) {
				player = new Player(350, 15, PLAYER_SIZE, PLAYER_IMG);
			}
			//If the score is greater or equal to 20, the player image is changed
			else {
				player = new Player(350, 15, PLAYER_SIZE, SCARED_PLR);
			}

			//If there are two lives left, one life image is removed
			if (lives==2) {
				LIVES3_IMG=null;
			}
			//If there is 1 life left, another life image is removed
			if (lives==1) {
				LIVES2_IMG=null;
			}
			//If there are 0 lives left, another life image is removed and the game is over
			if (lives==0) {
				LIVES_IMG=null;
				gameOver=true;
			}
		}
		//If the game is over, a game over image as well as the players' score is shown
		if(gameOver) {
			gc.drawImage(GO, 0, 0);
			gc.setFont(Font.font(35));
			gc.setFill(Color.WHITE);
			gc.fillText("Score: " + score, WIDTH/2, 550, 500);
			return;
		}
		//Sets the universe as each spot in the univ ArrayList and draws the universe
		for(Universe u: univ) {
			u.draw();
		}
		//Checks if the player is exploding
		player.update();
		//Draws explosion if the player is exploding
		//If the player is not exploding, the player is drawn
		player.draw(gc);
		//Checks each asteroid, changes the position of the asteroid and draws the asteroid as well
		//Explodes the player if the asteroid and the player collide
		for(Debris ast: asteroids) {
			//Changes the position of the asteroids and destroys the debris if they move off screen
			ast.update();
			//Draws the player and checks if its exploding
			ast.draw(gc);
			//If the player collides with the debris and the player is not exploding the following code will execute
			if(player.collide(ast) && !player.exploding) {
				//Player will explode as well as the debris
				player.explode();
				ast.exploding = true;
				//An exploding sound effect is added and played
				File file = new File ("src/pBoom.wav");
				Media sound=null;
				try {
					sound = new Media (file.toURI().toURL().toString());
				}
				catch (MalformedURLException g) {
					g.printStackTrace();
				}
				MediaPlayer music = new MediaPlayer(sound);
				music.play();
			}
		}
		//Checks each shot to see when it reaches the end of the screen and removes the shot if it
		//reaches the end of the screen
		for (int i = shots.size() - 1; i >=0 ; i--) {
			Shot shot = shots.get(i);
			if(shot.posY < 0 || shot.toRemove)  {
				shots.remove(i);
				continue;
			}
			//Changes (updates) the position of the shot
			shot.update();
			//Draws the shot
			shot.draw(gc);
			//Goes through each debris to check if it is colliding with the shot, if so then the score increases
			for (Debris ast :asteroids) {
				//If the shots collide with the debris, score is incremented and asteroid explodes
				if(shot.collide(ast) && !ast.exploding) {
					score++;
					ast.explode();
					shot.toRemove = true;

					//The speed of the debris is changed through this equation
					speed = (score/5) + 2;
					//The speed is changed at certain points to make the game more challenging
					if(speed == 7) {
						speed = 8;
					}
					else if(speed == 9) {
						speed = 10;
					}
					else if(speed == 11) {
						speed = 12;
					}
					//The max speed is set to 15
					else if(speed >= 13) {
						speed = 15;
					}
					//The speed is set
					ast.setSpeed(speed);
					//An explosion sound effect is added and played when shots collide with the debris
					File file = new File ("src/boom.wav");
					Media sound=null;
					try {
						sound = new Media (file.toURI().toURL().toString());
					}
					catch (MalformedURLException g) {
						g.printStackTrace();
					}
					MediaPlayer music = new MediaPlayer(sound);
					music.play();
				}
			}
		}

		//Checks each asteroid to see if it is destroyed
		//If destroyed a new set of debris is added to the game
		for (int i = asteroids.size() - 1; i >= 0; i--){
			if(asteroids.get(i).destroyed)  {
				asteroids.set(i, newDebris());
			}
		}

		//Dead is changed depending on whether the player is killed or not
		dead = player.destroyed;
		//Creates new stars in the background
		if(RAND.nextInt(10) > 2) {
			univ.add(new Universe(gc));
		}
		//Removes the stars in the background as they reach the end of the screen
		for (int i = 0; i < univ.size(); i++) {
			if(univ.get(i).posY > HEIGHT)
				univ.remove(i);
		}
	}

	/**
	 * This method creates new sets of debris and adds it to the game
	 * @return Debris returns a new randomized image of a debris and adds it to the game
	 */
	public Debris newDebris() {
		//New asteroid images are added to the game
		if(score == 0) {
			return new Debris(50 + RAND.nextInt(WIDTH - 100), HEIGHT, AST_SIZE, IMAGES[RAND.nextInt(IMAGES.length)], 2);
		}
		return new Debris(50 + RAND.nextInt(WIDTH - 100), HEIGHT, AST_SIZE, IMAGES[RAND.nextInt(IMAGES.length)], speed);
	}
}