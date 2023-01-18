/**
 * @author Meridian
 * @since  2023.
 */
package meridian.main;

import meridian.entity.Player;

import javax.swing.JPanel;
import java.awt.*;


// Screen settings.
public class GamePanel extends JPanel implements Runnable {

   // Defining 16x16 pixels size tile.
   public static final int ORIGINAL_TILE_SIZE = 16;

   // Pixel size multiplier.
   public static final int SCALE = 3;

   // Increasing the tile size with multiplier (48x48 pixels tile).
   public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

   // Define the screen size (768x576 pixels).
   public static final int MAX_SCREEN_COL = 16;
   public static final int MAX_SCREEN_ROW = 12;
   public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
   public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

   // FPS - Screen frame per second.
   public static final int FPS = 60;
   public static final long DRAW_INTERVAL = 1000000000 / FPS;

   // Handle keypressing.
   KeyHandler keyHandler = new KeyHandler();

   // Game thread.
   Thread gameThread;

   // Add a Player.
   Player player = new Player(this, keyHandler);


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

      player.update();

   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      player.draw(g2);

      g2.dispose();

   }

}