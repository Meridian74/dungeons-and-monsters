/**
 * @author Meridian
 * @since 2023.
 */
package meridian.entity;

import lombok.Getter;
import meridian.main.GameParam;
import meridian.main.KeyHandler;
import meridian.map.CollisionChecker;

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
   private final CollisionChecker collisionChecker;

   private boolean activeMoveLeft;
   private boolean activeMoveRight;
   private boolean activeMoveUp;
   private boolean activeMoveDown;

   private int lightCircle = 3;


   // Constructor.
   public Player(KeyHandler kh, CollisionChecker cc) {
      this.keyH = kh;
      this.collisionChecker = cc;
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
      updateWorldMapYPosition();

      // Update player's HORIZONTAL position.
      checkMovingHorizontally();
      checkTileBoundaryHorizontally();
      updateWorldMapXPosition();

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
         setDirection(Direction.UP);

         if (collisionChecker.canMove(this, Direction.UP)) {
            // Increase value of Y shift for map scrolling
            int newValue = getShiftY() + getSpeed();
            setShiftY(newValue);
            activeMoveUp = true;
         }
         else {
            activeMoveUp = false;
         }

      }
      else if (keyH.isDownPressed()) {
         // Turn ON active movement direction
         activeMoveUp = false;
         setDirection(Direction.DOWN);

         if (collisionChecker.canMove(this, Direction.DOWN)) {
            // Decrease value of Y shift for map scrolling
            int newValue = getShiftY() - getSpeed();
            setShiftY(newValue);
            activeMoveDown = true;
         }
         else {
            activeMoveDown = false;
         }

      }

      // Doing continuous vertical movement.
      if (!keyH.isUpPressed() && this.activeMoveUp && getShiftY() != 0 && getShiftY() != GameParam.TILE_SIZE) {
         int newValue = getShiftY() + getSpeed();
         setShiftY(newValue);
      }
      else if (!keyH.isDownPressed() && this.activeMoveDown && getShiftY() != 0 && getShiftY() != GameParam.TILE_SIZE) {
         int newValue = getShiftY() - getSpeed();
         setShiftY(newValue);
      }

   }

   private void checkTileBoundaryVertically() {
      // If reached a TILE edge and not pressing the key THEN turn OFF automatic movement.
      if ((!keyH.isUpPressed() || !keyH.isDownPressed()) &&
            (getShiftY() == 0 || getShiftY() == GameParam.TILE_SIZE)) {

         activeMoveUp = false;
         activeMoveDown = false;
      }

   }

   private void checkMovingHorizontally() {

      if (keyH.isLeftPressed()) {
         // Turn ON active movement direction
         activeMoveRight = false;
         setDirection(Direction.LEFT);

         if (collisionChecker.canMove(this, Direction.LEFT)) {
            // Increase value of X shift for map scrolling
            int newValue = getShiftX() + getSpeed();
            setShiftX(newValue);
            activeMoveLeft = true;
         }
         else {
            activeMoveLeft = false;
         }

      }
      else if (keyH.isRightPressed()) {
         // Turn ON active movement direction
         activeMoveLeft = false;
         setDirection(Direction.RIGHT);

         // Checking the boundary of the World Map.
         if (collisionChecker.canMove(this, Direction.RIGHT)) {
            // Decrease value of X shift for map scrolling
            int newValue = getShiftX() - getSpeed();
            setShiftX(newValue);
            activeMoveRight = true;
         }
         else {
            activeMoveRight = false;
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
      if ((!keyH.isLeftPressed() || !keyH.isRightPressed()) &&
            (getShiftX() == 0 || getShiftX() == GameParam.TILE_SIZE)) {

         activeMoveLeft = false;
         activeMoveRight = false;
      }

   }

   private void updateWorldMapYPosition() {
      // Update Y coord
      int shift = getShiftY();

      // Change next position if player reached half of next tile.
      if (shift <= -(GameParam.TILE_SIZE / 2)) {
         setShiftY(shift + (GameParam.TILE_SIZE));
         int position = getWorldRow();
         setWorldRow(++position);
      }
      else if (shift >= (GameParam.TILE_SIZE / 2)) {
         setShiftY(shift - (GameParam.TILE_SIZE));
         int position = getWorldRow();
         setWorldRow(--position);
      }

   }

   private void updateWorldMapXPosition() {
      // Update Y coord
      int shift = getShiftX();

      // Change next position if player reached half of next tile.
      if (shift <= -(GameParam.TILE_SIZE / 2)) {
         setShiftX(shift + GameParam.TILE_SIZE);
         int position = getWorldCol();
         setWorldCol(++position);
      }
      else if (shift >= (GameParam.TILE_SIZE / 2)) {
         setShiftX(shift - (GameParam.TILE_SIZE));
         int position = getWorldCol();
         setWorldCol(--position);
      }

   }

}