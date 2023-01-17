/**
 * @author Meridian
 * @since  2023.
 */
package meridian;

import javax.swing.JPanel;
import java.awt.*;


// Screen settings.

public class GamePanel extends JPanel implements Runnable {

   // Defining 16x16 pixels size tile.
   static final int ORIGINAL_TILE_SIZE = 16;

   // Pixel size multiplier.
   static final int SCALE = 3;

   // Increasing the tile size with multiplier (48x48 pixels tile).
   static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

   // Define the screen size (768x576 pixels).
   static final int MAX_SCREEN_COL = 16;
   static final int MAX_SCREEN_ROW = 12;
   static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
   static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

   // FPS - Screen frame per second.
   static final int FPS = 60;
   static final long DRAW_INTERVAL = 1000000000 / FPS;

   // Handle keypressing.
   KeyHandler keyHandler = new KeyHandler();

   // Game thread.
   Thread gameThread;

   // Player default position.
   private int playerPosX = 100;
   private int playerPosY = 100;
   private final int playerMovementSpeed = SCALE;


   // Constructor.
   public GamePanel() {
      this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
      this.setBackground(Color.black);
      this.setDoubleBuffered(true);
      this.addKeyListener(this.keyHandler);
      this.setFocusable(true);

   }


   /**
    * ---- Methods ----
    */

   public void startGameThread() {
      this.gameThread = new Thread(this);
      this.gameThread.start();

   }

   @Override
   public void run() {
      long nextDrawTime = System.nanoTime() + DRAW_INTERVAL;

      // Screen drawing cycle (loop)
      while(this.gameThread != null) {
         this.update();
         this.repaint();

         // Calculate remaining time in milliseconds.
         long timeToNextScreenReDraw = (nextDrawTime - System.nanoTime()) / 1000000;

         // Waiting for next drawing time.
         try {
            if (timeToNextScreenReDraw > 0) {
               Thread.sleep(timeToNextScreenReDraw);
            }
         } catch (InterruptedException e) {
            throw new RuntimeException(e);

         }

         // Calculate end of the next drawing cycle.
         nextDrawTime = nextDrawTime + DRAW_INTERVAL;
      }

   }

   public void update() {

      // Update player's horizontal position.
      if (keyHandler.isLeftPressed()) {
         playerPosX -= playerMovementSpeed;
         if (playerPosX < 0) {
            playerPosX = 0;
         }
      }
      else if (keyHandler.isRightPressed()) {
         playerPosX += playerMovementSpeed;
         if (playerPosX > SCREEN_WIDTH - TILE_SIZE) {
            playerPosX = SCREEN_WIDTH - TILE_SIZE;
         }
      }


      // Update player's vertical position.
      if (keyHandler.isUpPressed()) {
         playerPosY -= playerMovementSpeed;
         if (playerPosY < 0) {
            playerPosY = 0;
         }
      }
      else if (keyHandler.isDownPressed()) {
         playerPosY += playerMovementSpeed;
         if (playerPosY > SCREEN_HEIGHT - TILE_SIZE) {
            playerPosY = SCREEN_HEIGHT - TILE_SIZE;
         }
      }

   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor(Color.white);
      g2.fillRect(playerPosX, playerPosY, TILE_SIZE, TILE_SIZE);
      g2.dispose();

   }

}