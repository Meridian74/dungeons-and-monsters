/**
 * @author Meridian
 * @since  2023.
 */
package meridian.entity;

import lombok.Getter;
import meridian.main.GamePanel;
import meridian.main.GameParam;
import meridian.main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


@Getter
public class Player extends Entity {

   private static final int AREA_LEFT_EDGE = 0;
   private static final int AREA_UP_EDGE = 0;

   private static final int AREA_RIGHT_EDGE = GameParam.SCREEN_WIDTH - GameParam.TILE_SIZE;
   private static final int AREA_DOWN_EDGE = GameParam.SCREEN_HEIGHT - GameParam.TILE_SIZE;

   // Player pictures location on the displayed screen
   private static final int DRAWING_POSITION_X = GameParam.SCREEN_WIDTH / 2 - GameParam.TILE_SIZE / 2;
   private static final int DRAWING_POSITION_Y = GameParam.SCREEN_HEIGHT / 2 - GameParam.TILE_SIZE / 2;

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
      setSpeed(GameParam.SCALE);
      this.init();
   }


   public void init() {
      try {
         // get tile set image from file
         BufferedImage tileSet = ImageIO.read(getClass().getResourceAsStream("/players/player-set-börg.png"));
         setImages(new BufferedImage[8][]);

         // Get images of the normal movement phases - each row contains 5 (size: 16x16px) pics.
         for (int row = 0; row < 4; row++) {

            BufferedImage[] pics = new BufferedImage[5];

            for (int current = 0; current < 5; current++) {
               pics[current] = tileSet.getSubimage(
                     GameParam.ORIGINAL_TILE_SIZE * current,
                     GameParam.ORIGINAL_TILE_SIZE * row,
                     GameParam.ORIGINAL_TILE_SIZE, GameParam.ORIGINAL_TILE_SIZE
               );

            }
            getImages()[row] = pics;

         }
         // Get images of attack movement phases - each row contains 6 (size: 16x16px) images.
         for (int row = 4; row < 8; row++) {

            BufferedImage[] pics = new BufferedImage[6];
            for (int current = 0; current < 6; current++) {
               pics[current] = tileSet.getSubimage(
                     GameParam.ORIGINAL_TILE_SIZE * current,
                     GameParam.ORIGINAL_TILE_SIZE * row,
                     GameParam.ORIGINAL_TILE_SIZE, GameParam.ORIGINAL_TILE_SIZE
               );

            }
            getImages()[row] = pics;

         }
         
      }
      catch (IOException e) {
         throw new IllegalStateException("Cannot load player' graphics" + e);
      }

      setDirection(Direction.DOWN);
      setAnimationPhaseIndex(0);
      setCurrentDrawedFrame(0);
      setAnimationRowOffset(0);

   }

   public void update() {

      // Update player's vertical position.
      if (keyH.isUpPressed()) {

         setDirection(Direction.UP);

         // Turn ON active movement direction
         activeMoveDown = false;
         activeMoveUp = true;

         setWorldPosY(getWorldPosY() - getSpeed());
         if (getWorldPosY() < AREA_UP_EDGE) {
            setWorldPosY(AREA_UP_EDGE);

            // Turn OFF active movement direction
            activeMoveUp = false;
         }

      }
      else if (keyH.isDownPressed()) {

         setDirection(Direction.DOWN);

         // Turn ON active movement direction
         activeMoveUp = false;
         activeMoveDown = true;

         setWorldPosY(getWorldPosY() + getSpeed());
         if (getWorldPosY() > AREA_DOWN_EDGE) {
            setWorldPosY(AREA_DOWN_EDGE);

            // Turn OFF active movement direction
            activeMoveDown = false;
         }

      }

      // If reached a TILE edge, turn OFF movement
      if ((!keyH.isUpPressed() || !keyH.isDownPressed()) &&
            getWorldPosY() % GameParam.TILE_SIZE == 0) {
         activeMoveUp = false;
         activeMoveDown = false;
      }

      // Doing continous vertical movement by tile size accurating.
      if (!keyH.isUpPressed() && this.activeMoveUp &&
            getWorldPosY() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosY() % GameParam.TILE_SIZE < GameParam.SCALE) {
            setWorldPosY(getWorldPosY() - getWorldPosY() % GameParam.SCALE);
            activeMoveUp = false;
         }
         else {
            setWorldPosY(getWorldPosY() - getSpeed());
         }
      }
      else if (!keyH.isDownPressed() && this.activeMoveDown &&
            getWorldPosY() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosY() % GameParam.TILE_SIZE < GameParam.SCALE) {
            setWorldPosY(getWorldPosY() + GameParam.SCALE - getWorldPosY() % GameParam.TILE_SIZE);
            activeMoveDown = false;
         }
         else {
            setWorldPosY(getWorldPosY() + getSpeed());
         }

      }


      // Update player's horizontal position.
      if (keyH.isLeftPressed()) {

         setDirection(Direction.LEFT);

         // Turn ON active movement direction
         activeMoveRight = false;
         activeMoveLeft = true;

         setWorldPosX(getWorldPosX() - getSpeed());
         if (getWorldPosX() < AREA_LEFT_EDGE) {
            setWorldPosX(AREA_LEFT_EDGE);

            // Turn OFF active movement direction
            activeMoveLeft = false;
         }

      }
      else if (keyH.isRightPressed()) {

         setDirection(Direction.RIGHT);

         // Turn ON active movement direction
         activeMoveLeft = false;
         activeMoveRight = true;

         setWorldPosX(getWorldPosX() + getSpeed());
         if (getWorldPosX() > AREA_RIGHT_EDGE) {
            setWorldPosX(AREA_RIGHT_EDGE);

            // Turn OFF active movement direction
            activeMoveRight = false;
         }

      }

      // If reached a TILE edge, turn OFF movement
      if ((!keyH.isLeftPressed() || !keyH.isRightPressed()) &&
            getWorldPosX() % GameParam.TILE_SIZE == 0) {
         activeMoveLeft = false;
         activeMoveRight = false;
      }

      // Doing continous horizontal movement by tile size accurating.
      if (!keyH.isLeftPressed() && this.activeMoveLeft &&
            getWorldPosX() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosX() % GameParam.TILE_SIZE < GameParam.SCALE) {
            setWorldPosX(getWorldPosX() - getWorldPosX() % GameParam.SCALE);
            activeMoveLeft = false;
         }
         else {
            setWorldPosX(getWorldPosX() - getSpeed());
         }
      }
      else if (!keyH.isRightPressed() && this.activeMoveRight &&
            getWorldPosX() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosX() % GameParam.TILE_SIZE < GameParam.SCALE) {
            setWorldPosX(getWorldPosX() + GameParam.SCALE - getWorldPosX() % GameParam.TILE_SIZE);
            activeMoveRight = false;
         }
         else {
            setWorldPosX(getWorldPosX() + getSpeed());
         }

      }

   }

   public void draw(Graphics2D g2) {

      BufferedImage image = null;
      int currentAnimRowLength = 0;
      int animPhaseIndex = getAnimationPhaseIndex();
      int rowOffSet = getAnimationRowOffset();
      int drawedFrame = getCurrentDrawedFrame();

      switch (getDirection()) {
         case DOWN -> {
            image = getImages()[rowOffSet][animPhaseIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }
         case UP -> {
            image = getImages()[rowOffSet + 1][animPhaseIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }
         case LEFT -> {
            image = getImages()[rowOffSet + 2][animPhaseIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }
         case RIGHT -> {
            image = getImages()[rowOffSet + 3][animPhaseIndex];
            currentAnimRowLength = getImages()[rowOffSet].length;
         }

      }


      // show Player's character
      g2.drawImage(image, DRAWING_POSITION_X, DRAWING_POSITION_Y,
            GameParam.TILE_SIZE, GameParam.TILE_SIZE, null);


      // control of animation phase changes -- 60 fps screen is too fast for anim speed
      drawedFrame++;
      if (drawedFrame > ANIM_SPEED) {

         // step next frame of animation row if moving is active
         if (activeMoveLeft || activeMoveRight || activeMoveUp || activeMoveDown) {
            setAnimationPhaseIndex(getAnimationPhaseIndex() + 1);
            if (getAnimationPhaseIndex() > currentAnimRowLength - 1) {

               // because the first frame of animation row is a standing state
               setAnimationPhaseIndex(1);
            }
         }
         else {
            setAnimationPhaseIndex(0);
            // optional part... if the Player stopped, the character turns to face you
            setDirection(Direction.DOWN);
         }

         drawedFrame = 0;
      }

      setCurrentDrawedFrame(drawedFrame);

   }

}