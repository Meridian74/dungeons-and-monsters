/**
 * @author Meridian
 * @since 2023.
 */
package meridian.entity;

import lombok.Getter;
import meridian.main.GameParam;
import meridian.main.KeyHandler;
import meridian.map.MapManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;


@Getter
public class Player extends Entity {

   // Player pictures location on the displayed screen
   private static final int DRAWING_POSITION_X = GameParam.SCREEN_WIDTH / 2 - GameParam.TILE_SIZE / 2;
   private static final int DRAWING_POSITION_Y = GameParam.SCREEN_HEIGHT / 2 - GameParam.TILE_SIZE / 2;

   // controlling the animation speed
   private static final int ANIM_SPEED = 10;

   private final KeyHandler keyH;
   private final MapManager mapManager;

   private boolean activeMoveLeft;
   private boolean activeMoveRight;
   private boolean activeMoveUp;
   private boolean activeMoveDown;


   // Constructor.
   public Player(KeyHandler kh, MapManager mm) {
      this.keyH = kh;
      this.mapManager = mm;
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

      } catch (NullPointerException | IOException e) {
         throw new IllegalStateException("Cannot load player' graphics" + e);
      }

      // Set movement (1 * 3 pixel)
      setSpeed(GameParam.PIXEL_SCALE);

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

      // Update player's HORIZONTAL position.
      checkMovingHorizontally();
      checkTileBoundaryHorizontally();

      // Calculate World Map Tile position.
      updateWorldMapPostion();

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
      float opacity = 1.0f;
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
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
         } else {
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

         // Checking the boundary of the World Map.
         if (getWorldRow() == mapManager.getWorldTop()) {
            activeMoveUp = false;
         }
         else {
            // Increase value of Y shift for map scrolling
            int newValue = getShiftY() + getSpeed();
            setShiftY(newValue);
         }

      }
      else if (keyH.isDownPressed()) {
         // Turn ON active movement direction
         activeMoveUp = false;
         activeMoveDown = true;
         setDirection(Direction.DOWN);

         // Checking the boundary of the World Map.
         if (getWorldRow() == mapManager.getWorldBottom()) {
            activeMoveDown = false;

         }
         else {
            // Decrease value of Y shift for map scrolling
            int newValue = getShiftY() - getSpeed();
            setShiftY(newValue);
         }

      }

      // Doing continuous vertical movement.
      if (!keyH.isUpPressed() && this.activeMoveUp && getShiftY() != 0) {
         int newValue = getShiftY() + getSpeed();
         setShiftY(newValue);
      }
      else if (!keyH.isDownPressed() && this.activeMoveDown && getShiftY() != 0) {
         int newValue = getShiftY() - getSpeed();
         setShiftY(newValue);
      }

   }

   private void checkTileBoundaryVertically() {
      // If reached a TILE edge and not pressing the key THEN turn OFF automatic movement.
      if ((!keyH.isUpPressed() || !keyH.isDownPressed()) && getShiftY() == 0) {
         activeMoveUp = false;
         activeMoveDown = false;
      }

   }

   private void checkMovingHorizontally() {

      if (keyH.isLeftPressed()) {
         // Turn ON active movement direction
         activeMoveRight = false;
         activeMoveLeft = true;
         setDirection(Direction.LEFT);

         // Checking the boundary of the World Map.
         if (getWorldCol() == mapManager.getWorldLeft()) {
            activeMoveLeft = false;
         }
         else {
            // Increase value of X shift for map scrolling
            int newValue = getShiftX() + getSpeed();
            setShiftX(newValue);
         }

      }
      else if (keyH.isRightPressed()) {
         // Turn ON active movement direction
         activeMoveLeft = false;
         activeMoveRight = true;
         setDirection(Direction.RIGHT);

         // Checking the boundary of the World Map.
         if (getWorldCol() == mapManager.getWorldRight()) {
            activeMoveRight = false;

         }
         else {
            // Decrease value of X shift for map scrolling
            int newValue = getShiftX() - getSpeed();
            setShiftX(newValue);
         }

      }

      // Doing continuous horizontal movement.
      if (!keyH.isLeftPressed() && this.activeMoveLeft && getShiftX() != 0) {
         int newValue = getShiftX() + getSpeed();
         setShiftX(newValue);
      }
      else if (!keyH.isRightPressed() && this.activeMoveRight && getShiftX() != 0) {
         int newValue = getShiftX() - getSpeed();
         setShiftX(newValue);
      }

   }

   private void checkTileBoundaryHorizontally() {
      // If reached a TILE edge, turn OFF movement
      if ((!keyH.isLeftPressed() || !keyH.isRightPressed()) && getShiftX() == 0) {
         activeMoveLeft = false;
         activeMoveRight = false;
      }

   }

   private void updateWorldMapPostion() {
      // Update Y coord
      int shift = getShiftY();
      if (shift >= GameParam.TILE_SIZE) {
         int position = getWorldRow();
         position--;
         setWorldRow(position);
         setShiftY(shift - GameParam.TILE_SIZE);
      }
      else if (shift < 0) {
         int position = getWorldRow();
         position++;
         setWorldRow(position);
         setShiftY(shift + GameParam.TILE_SIZE);
      }

      // Update X coord
      shift = getShiftX();
      if (shift >= GameParam.TILE_SIZE) {
         int position = getWorldCol();
         position--;
         setWorldCol(position);
         setShiftX(shift - GameParam.TILE_SIZE);
      }
      else if (shift < 0) {
         int position = getWorldCol();
         position++;
         setWorldCol(position);
         setShiftX(shift + GameParam.TILE_SIZE);
      }

   }

}