/**
 * @author Meridian
 * @since  2023.
 */
package meridian.main;

import meridian.entity.Player;
import meridian.map.MapManager;
import meridian.tile.TileManager;

import javax.swing.JPanel;
import java.awt.*;


// Screen settings.
public class GamePanel extends JPanel implements Runnable {

   // Handle keypressing.
   private KeyHandler keyHandler = new KeyHandler();

   // Map Manager;
   private MapManager mapManager = new MapManager();

   // Tile Manager
   private TileManager tileManager = new TileManager();

   // Add a Player.
   private Player player = new Player(this, keyHandler);

   // Game thread.
   Thread gameThread;


   // Constructor.
   public GamePanel() {
      this.setPreferredSize(new Dimension(GameParam.SCREEN_WIDTH, GameParam.SCREEN_HEIGHT));
      this.setBackground(GameParam.DEFAULT_BACKGROUND);
      this.setDoubleBuffered(true);
      this.addKeyListener(this.keyHandler);
      this.setFocusable(true);


      // basic start - without menu, load, etc
      this.mapManager.loadMapById(1);
      this.tileManager.loadTileImages("/tiles/" + mapManager.getCurrentTileSet());
      // place player on the WORLD map
      this.player.setWorldPosX(10 * GameParam.TILE_SIZE);
      this.player.setWorldPosX(10 * GameParam.TILE_SIZE);

   }


   /**
    * ---- Methods ----
    */

   public void startGameThread() {
      gameThread = new Thread(this);
      gameThread.start();

   }

   @Override
   public void run() {
      long nextDrawTime = System.nanoTime() + GameParam.DRAW_INTERVAL;

      // Screen drawing cycle (loop)
      while(gameThread != null) {


         // player update
         update();
         repaint();

         // Calculate remaining time in milliseconds.
         long timeToNextScreenReDraw = (nextDrawTime - System.nanoTime()) / 1000000;

         // Waiting for next drawing time.
         try {
            if (timeToNextScreenReDraw > 0) {
               Thread.sleep(timeToNextScreenReDraw);
            }
         } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Program running interrupted: " + e);
         }

         // Calculate end of the next drawing cycle.
         nextDrawTime = nextDrawTime + GameParam.DRAW_INTERVAL;
      }

   }

   public void update() {

      player.update();

   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      // TODO: get cell on the screen by Player position - doing this the MapManager
      // TODO: draw screen floor and wall
      // TODO: draw items
      // TODO: draw monsters

      // draw player
      player.draw(g2);

      // TODO: draw secondary walls&decorations (door, torch, others...)

      g2.dispose();

   }

}