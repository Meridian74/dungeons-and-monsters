/**
 * @author Meridian
 * @since  2023.
 */
package meridian.entity;

import lombok.Getter;
import meridian.main.GamePanel;
import meridian.main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


@Getter
public class Player extends Entity {

   private static final int AREA_LEFT_EDGE = 0;
   private static final int AREA_UP_EDGE = 0;

   private static final int AREA_RIGHT_EDGE = GamePanel.SCREEN_WIDTH - GamePanel.TILE_SIZE;
   private static final int AREA_DOWN_EDGE = GamePanel.SCREEN_HEIGHT - GamePanel.TILE_SIZE;

   // controlling the animation speed
   private static final int ANIM_SPEED = 10;

   private final GamePanel gp;
   private final KeyHandler keyH;

   private boolean activeMoveLeft;
   private boolean activeMoveRight;
   private boolean activeMoveUp;
   private boolean activeMoveDown;


   // Constructor.
   public Player(GamePanel gp, KeyHandler keyH) {
      this.gp = gp;
      this.keyH = keyH;
      this.init();
   }


   public void init() {
      setPosX(100);
      setPosY(100);
      setSpeed(GamePanel.SCALE);

      try {
         // get tile set image from file
         BufferedImage tileSet = ImageIO.read(getClass().getResourceAsStream("/players/player-set-börg.png"));
         setImages(new BufferedImage[8][]);

         // get images of the normal movement phases - each row contains 5 (size: 16x16px) pics
         for (int row = 0; row < 4; row++) {

            BufferedImage[] pics = new BufferedImage[5];

            for (int current = 0; current < 5; current++) {
               pics[current] = tileSet.getSubimage(
                     GamePanel.ORIGINAL_TILE_SIZE * current,
                     GamePanel.ORIGINAL_TILE_SIZE * row,
                     GamePanel.ORIGINAL_TILE_SIZE, GamePanel.ORIGINAL_TILE_SIZE
               );

            }
            getImages()[row] = pics;

         }
         // get images of attack movement phases - each row contains 6 (size: 16x16px) images
         for (int row = 4; row < 8; row++) {

            BufferedImage[] pics = new BufferedImage[6];
            for (int current = 0; current < 6; current++) {
               pics[current] = tileSet.getSubimage(
                     GamePanel.ORIGINAL_TILE_SIZE * current,
                     GamePanel.ORIGINAL_TILE_SIZE * row,
                     GamePanel.ORIGINAL_TILE_SIZE, GamePanel.ORIGINAL_TILE_SIZE
               );

            }
            getImages()[row] = pics;

         }
         
      }
      catch (IOException e) {
         throw new IllegalStateException("Cannot load player' graphics" + e);
      }

      setDirection(Direction.DOWN);
      setAnimationIndex(0);
      setScreenFrameCounter(0);
      setAnimationRowOffset(0);

   }

   public void update() {

      // Update player's vertical position.
      if (keyH.isUpPressed()) {

         setDirection(Direction.UP);

         // Turn ON active movement direction
         activeMoveDown = false;
         activeMoveUp = true;

         setPosY(getPosY() - getSpeed());
         if (getPosY() < AREA_UP_EDGE) {
            setPosY(AREA_UP_EDGE);

            // Turn OFF active movement direction
            activeMoveUp = false;
         }

      }
      else if (keyH.isDownPressed()) {

         setDirection(Direction.DOWN);

         // Turn ON active movement direction
         activeMoveUp = false;
         activeMoveDown = true;

         setPosY(getPosY() + getSpeed());
         if (getPosY() > AREA_DOWN_EDGE) {
            setPosY(AREA_DOWN_EDGE);

            // Turn OFF active movement direction
            activeMoveDown = false;
         }

      }

      // If reached a TILE edge, turn OFF movement
      if ((!keyH.isUpPressed() || !keyH.isDownPressed()) &&
            getPosY() % GamePanel.TILE_SIZE == 0) {
         activeMoveUp = false;
         activeMoveDown = false;
      }

      // Doing continous vertical movement by tile size accurating.
      if (!keyH.isUpPressed() && this.activeMoveUp &&
            getPosY() % GamePanel.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getPosY() % GamePanel.TILE_SIZE < GamePanel.SCALE) {
            setPosY(getPosY() - getPosY() % GamePanel.SCALE);
            activeMoveUp = false;
         }
         else {
            setPosY(getPosY() - getSpeed());
         }
      }
      else if (!keyH.isDownPressed() && this.activeMoveDown &&
            getPosY() % GamePanel.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getPosY() % GamePanel.TILE_SIZE < GamePanel.SCALE) {
            setPosY(getPosY() + GamePanel.SCALE - getPosY() % GamePanel.TILE_SIZE);
            activeMoveDown = false;
         }
         else {
            setPosY(getPosY() + getSpeed());
         }

      }


      // Update player's horizontal position.
      if (keyH.isLeftPressed()) {

         setDirection(Direction.LEFT);

         // Turn ON active movement direction
         activeMoveRight = false;
         activeMoveLeft = true;

         setPosX(getPosX() - getSpeed());
         if (getPosX() < AREA_LEFT_EDGE) {
            setPosX(AREA_LEFT_EDGE);

            // Turn OFF active movement direction
            activeMoveLeft = false;
         }

      }
      else if (keyH.isRightPressed()) {

         setDirection(Direction.RIGHT);

         // Turn ON active movement direction
         activeMoveLeft = false;
         activeMoveRight = true;

         setPosX(getPosX() + getSpeed());
         if (getPosX() > AREA_RIGHT_EDGE) {
            setPosX(AREA_RIGHT_EDGE);

            // Turn OFF active movement direction
            activeMoveRight = false;
         }

      }

      // If reached a TILE edge, turn OFF movement
      if ((!keyH.isLeftPressed() || !keyH.isRightPressed()) &&
            getPosX() % GamePanel.TILE_SIZE == 0) {
         activeMoveLeft = false;
         activeMoveRight = false;
      }

      // Doing continous horizontal movement by tile size accurating.
      if (!keyH.isLeftPressed() && this.activeMoveLeft &&
            getPosX() % GamePanel.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getPosX() % GamePanel.TILE_SIZE < GamePanel.SCALE) {
            setPosX(getPosX() - getPosX() % GamePanel.SCALE);
            activeMoveLeft = false;
         }
         else {
            setPosX(getPosX() - getSpeed());
         }
      }
      else if (!keyH.isRightPressed() && this.activeMoveRight &&
            getPosX() % GamePanel.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getPosX() % GamePanel.TILE_SIZE < GamePanel.SCALE) {
            setPosX(getPosX() + GamePanel.SCALE - getPosX() % GamePanel.TILE_SIZE);
            activeMoveRight = false;
         }
         else {
            setPosX(getPosX() + getSpeed());
         }

      }

   }

   public void draw(Graphics2D g2) {

      BufferedImage image = null;
      int currentAnimRowLength = 0;
      int animIndex = getAnimationIndex();
      int rowOffSet = getAnimationRowOffset();
      int frameCounter = getScreenFrameCounter();

      switch (getDirection()) {
         case DOWN -> {
            image = getImages()[rowOffSet][animIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }
         case UP -> {
            image = getImages()[rowOffSet + 1][animIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }
         case LEFT -> {
            image = getImages()[rowOffSet + 2][animIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }
         case RIGHT -> {
            image = getImages()[rowOffSet + 3][animIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }

      }


      // show Player's character
      g2.drawImage(image, getPosX(), getPosY(), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, null);


      // control of animation phase changes -- 60 fps screen is too fast for anim speed
      frameCounter++;
      if (frameCounter > ANIM_SPEED) {

         // step next frame of animation row if moving is active
         if (activeMoveLeft || activeMoveRight || activeMoveUp || activeMoveDown) {
            setAnimationIndex(getAnimationIndex() + 1);
            if (getAnimationIndex() > currentAnimRowLength - 1) {

               // because the first frame of animation row is a standing state
               setAnimationIndex(1);
            }
         }
         else {
            setAnimationIndex(0);
            // optional part... if the Player stopped, the character turns to face you
            setDirection(Direction.DOWN);
         }

         frameCounter = 0;
      }

      setScreenFrameCounter(frameCounter);

   }

}