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
import java.util.Objects;


@Getter
public class Player extends Entity {

   private static final int WORD_LEFT_EDGE = 0;
   private static final int WORLD_TOP_EDGE = 0;

   private static final int WORLD_RIGHT_EDGE = GameParam.SCREEN_WIDTH - GameParam.TILE_SIZE;
   private static final int WORLD_BOTTOM_EDGE = GameParam.SCREEN_HEIGHT - GameParam.TILE_SIZE;

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
      this.init();
   }


   public void init() {

      try {
         // Get tile set image from file.
         BufferedImage tileSet = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/players/player-set-börg.png")));
         setImages(new BufferedImage[8][]);

         // Get images of the normal movement phases - each row contains 5 (size: 16*16px) pics.
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
         // Get images of attack movement phases - each row contains 6 (size: 16*16px) images.
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
      catch (NullPointerException | IOException e) {
         throw new IllegalStateException("Cannot load player' graphics" + e);
      }

      // Set movement
      setSpeed(GameParam.SCALE);

      // Set first appearance
      setDirection(Direction.DOWN);
      setAnimationPhaseIndex(0);
      setCurrentDrawedFrame(0);
      setAnimationRowOffset(0);

   }

   public void update() {

      // Update player's VERTICAL position.
      checkMovingVertically();
      checkTileBoundaryVertically();
      automaticMovementVertically();

      // Update player's HORIZONTAL position.
      checkMovingHorizontally();
      checkTileBoundaryHorizontally();
      automaticMovementHorizontally();

      // Calculate World Map Tile position.
      calculateWorldMapTilePostion();

   }

   public void draw(Graphics2D g2) {

      BufferedImage image = null;
      int currentAnimRowLength = 0;
      int animPhaseIndex = getAnimationPhaseIndex();
      int rowOffSet = getAnimationRowOffset();
      int drawedFrame = getCurrentDrawedFrame();

      // Get current image of Player's character.
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

   private void checkMovingVertically() {

      if (keyH.isUpPressed()) {
         // Turn ON active movement direction
         activeMoveDown = false;
         activeMoveUp = true;
         setDirection(Direction.UP);

         int newPosition = getWorldPosY() - getSpeed();
         setWorldPosY(newPosition);

         // Checking the boundary of the World Map.
         if (getWorldPosY() < WORLD_TOP_EDGE) {
            // Turn OFF active movement direction and roll back position.
            activeMoveUp = false;
            setWorldPosY(WORLD_TOP_EDGE);
         }

      }
      else if (keyH.isDownPressed()) {
         // Turn ON active movement direction
         activeMoveUp = false;
         activeMoveDown = true;
         setDirection(Direction.DOWN);

         int newPosition = getWorldPosY() + getSpeed();
         setWorldPosY(newPosition);

         // Checking the boundary of the World Map.
         if (getWorldPosY() > WORLD_BOTTOM_EDGE) {
            // Turn OFF active movement direction and roll back position.
            activeMoveDown = false;
            setWorldPosY(WORLD_BOTTOM_EDGE);
         }

      }

   }

   private void checkTileBoundaryVertically() {
      // If reached a TILE edge, turn OFF automatic movement.
      if ((!keyH.isUpPressed() || !keyH.isDownPressed()) &&
            getWorldPosY() % GameParam.TILE_SIZE == 0) {

         activeMoveUp = false;
         activeMoveDown = false;
      }

   }

   private void automaticMovementVertically() {
      // Doing continuous vertical movement by tile size accurate.
      if (!keyH.isUpPressed() && this.activeMoveUp &&
            getWorldPosY() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosY() % GameParam.TILE_SIZE < getSpeed()) {
            int newPosition = getWorldPosY() - getWorldPosY() % GameParam.SCALE;
            setWorldPosY(newPosition);
            activeMoveUp = false;
         }
         else {
            int newPosition = getWorldPosY() - getSpeed();
            setWorldPosY(newPosition);
         }
      }
      else if (!keyH.isDownPressed() && this.activeMoveDown &&
            getWorldPosY() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosY() % GameParam.TILE_SIZE < getSpeed()) {
            int newPosition = getWorldPosY() + getWorldPosY() % GameParam.SCALE;
            setWorldPosY(newPosition);
            activeMoveDown = false;
         }
         else {
            int newPosition = getWorldPosY() + getSpeed();
            setWorldPosY(newPosition);
         }

      }

   }

   private void checkMovingHorizontally() {

      if (keyH.isLeftPressed()) {
         // Turn ON active movement direction
         activeMoveRight = false;
         activeMoveLeft = true;
         setDirection(Direction.LEFT);

         int newPosition = getWorldPosX() - getSpeed();
         setWorldPosX(newPosition);

         // Checking the boundary of the World Map.
         if (getWorldPosX() < WORD_LEFT_EDGE) {
            // Turn OFF active movement direction and roll back position.
            activeMoveLeft = false;
            setWorldPosX(WORD_LEFT_EDGE);
         }

      }
      else if (keyH.isRightPressed()) {
         // Turn ON active movement direction
         activeMoveLeft = false;
         activeMoveRight = true;
         setDirection(Direction.RIGHT);

         int newPosition = getWorldPosX() + getSpeed();
         setWorldPosX(newPosition);

         // Checking the boundary of the World Map.
         if (getWorldPosX() > WORLD_RIGHT_EDGE) {
            // Turn OFF active movement direction
            activeMoveRight = false;
            setWorldPosX(WORLD_RIGHT_EDGE);
         }

      }

   }

   private void checkTileBoundaryHorizontally() {
      // If reached a TILE edge, turn OFF movement
      if ((!keyH.isLeftPressed() || !keyH.isRightPressed()) &&
            getWorldPosX() % GameParam.TILE_SIZE == 0) {

         activeMoveLeft = false;
         activeMoveRight = false;
      }

   }

   private void automaticMovementHorizontally() {
      // Doing continuous horizontal movement by tile size accurate.
      if (!keyH.isLeftPressed() && this.activeMoveLeft &&
            getWorldPosX() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosX() % GameParam.TILE_SIZE < getSpeed()) {
            int newPosition = getWorldPosX() - getWorldPosX() % GameParam.SCALE;
            setWorldPosX(newPosition);
            activeMoveLeft = false;
         }
         else {
            int newPosition = getWorldPosX() - getSpeed();
            setWorldPosX(newPosition);
         }
      }
      else if (!keyH.isRightPressed() && this.activeMoveRight &&
            getWorldPosX() % GameParam.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getWorldPosX() % GameParam.TILE_SIZE < getSpeed()) {
            int newPosition = getWorldPosX() + GameParam.SCALE - getWorldPosX() % GameParam.TILE_SIZE;
            setWorldPosX(newPosition);
            activeMoveRight = false;
         }
         else {
            int newPosition = getWorldPosX() + getSpeed();
            setWorldPosX(newPosition);
         }

      }

   }

   private void calculateWorldMapTilePostion() {
      if (getWorldPosY() % GameParam.TILE_SIZE < getSpeed()) {
         int newPosition = getWorldPosY() / GameParam.TILE_SIZE;
         setWorldRow(newPosition);
      }
      if (getWorldPosX() % GameParam.TILE_SIZE < getSpeed()) {
         int newPosition = getWorldPosX() / GameParam.TILE_SIZE;
         setWorldCol(newPosition);
      }

   }

}