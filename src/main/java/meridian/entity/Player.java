/**
 * @author Meridian
 * @since  2023.
 */
package meridian.entity;

import lombok.Getter;
import meridian.main.GamePanel;
import meridian.main.KeyHandler;

import java.awt.*;


@Getter
public class Player extends Entity {

   private static final int AREA_LEFT_EDGE = 0;
   private static final int AREA_UP_EDGE = 0;

   private static final int AREA_RIGHT_EDGE = GamePanel.SCREEN_WIDTH - GamePanel.TILE_SIZE;
   private static final int AREA_DOWN_EDGE = GamePanel.SCREEN_HEIGHT - GamePanel.TILE_SIZE;

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
      this.setDefaultValues();
   }


   public void setDefaultValues() {
      setPosX(100);
      setPosY(100);
      setSpeed(GamePanel.SCALE);

   }

   public void update() {

      // Update player's horizontal position.
      if (keyH.isLeftPressed()) {

         // Turn ON active movement direction
         this.activeMoveRight = false;
         this.activeMoveLeft = true;

         setPosX(getPosX() - getSpeed());
         if (getPosX() < AREA_LEFT_EDGE) {
            setPosX(AREA_LEFT_EDGE);

            // Turn OFF active movement direction
            this.activeMoveLeft = false;
         }

      }
      else if (keyH.isRightPressed()) {

         // Turn ON active movement direction
         this.activeMoveLeft = false;
         this.activeMoveRight = true;

         setPosX(getPosX() + getSpeed());
         if (getPosX() > AREA_RIGHT_EDGE) {
            setPosX(AREA_RIGHT_EDGE);

            // Turn OFF active movement direction
            this.activeMoveRight = false;
         }

      }

      // If reached a TILE edge, turn OFF movement
      if (getPosX() % GamePanel.TILE_SIZE == 0) {
         this.activeMoveLeft = false;
         this.activeMoveRight = false;
      }

      // Doing continous horizontal movement by tile size accurating.
      if (!keyH.isLeftPressed() && this.activeMoveLeft &&
            getPosX() % GamePanel.TILE_SIZE != 0) {

         // Include fitting of the X coordinate to the Tile's grid with pixel's SCALE.
         if (getPosX() % GamePanel.TILE_SIZE < GamePanel.SCALE) {
            setPosX(getPosX() - getPosX() % GamePanel.SCALE);
            this.activeMoveLeft = false;
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
            this.activeMoveRight = false;
         }
         else {
            setPosX(getPosX() + getSpeed());
         }

      }



      // Update player's vertical position.
      if (keyH.isUpPressed()) {

         // Turn ON active movement direction
         this.activeMoveDown = false;
         this.activeMoveUp = true;

         setPosY(getPosY() - getSpeed());
         if (getPosY() < AREA_UP_EDGE) {
            setPosY(AREA_UP_EDGE);

            // Turn OFF active movement direction
            this.activeMoveUp = false;
         }

      }
      else if (keyH.isDownPressed()) {

         // Turn ON active movement direction
         this.activeMoveUp = false;
         this.activeMoveDown = true;

         setPosY(getPosY() + getSpeed());
         if (getPosY() > AREA_DOWN_EDGE) {
            setPosY(AREA_DOWN_EDGE);

            // Turn OFF active movement direction
            this.activeMoveDown = false;
         }

      }

      // If reached a TILE edge, turn OFF movement
      if (getPosY() % GamePanel.TILE_SIZE == 0) {
         this.activeMoveUp = false;
         this.activeMoveDown = false;
      }

      // Doing continous vertical movement by tile size accurating.
      if (!keyH.isUpPressed() && this.activeMoveUp &&
            getPosY() % GamePanel.TILE_SIZE != 0) {

         // Include fitting of the Y coordinate to the Tile's grid with pixel's SCALE.
         if (getPosY() % GamePanel.TILE_SIZE < GamePanel.SCALE) {
            setPosY(getPosY() - getPosY() % GamePanel.SCALE);
            this.activeMoveUp = false;
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
            this.activeMoveDown = false;
         }
         else {
            setPosY(getPosY() + getSpeed());
         }

      }

   }

   public void draw(Graphics2D g2) {
      g2.setColor(Color.white);
      g2.fillRect(getPosX(), getPosY(), GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

   }

}