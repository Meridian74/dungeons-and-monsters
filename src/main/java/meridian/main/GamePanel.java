/**
 * @author Meridian
 * @since  2023.
 */
package meridian.main;

import meridian.entity.Player;
import meridian.map.CollisionChecker;
import meridian.map.MapManager;
import meridian.map.ShadeMatrix;
import meridian.sound.SoundManager;
import meridian.tile.TileManager;

import javax.swing.JPanel;
import java.awt.*;


// Screen settings.
public class GamePanel extends JPanel implements Runnable {

   // Handle key pressing.
   private KeyHandler keyHandler = new KeyHandler();

   // Tile graphics.
   private TileManager tileManager = new TileManager();

   // Opacity (shade) calculator
   private ShadeMatrix shadeMatrix = new ShadeMatrix();

   // World Map graphics.
   private MapManager mapManager = new MapManager(this.tileManager, this.shadeMatrix);

   // Collision handler.
   private CollisionChecker collisionChecker = new CollisionChecker(this.mapManager);

   // Sounds
   private SoundManager soundManager = new SoundManager();

   // Add a Player.
   private Player player = new Player(keyHandler, this.collisionChecker);

   // Game thread.
   private Thread gameThread;


   // Constructor.
   public GamePanel() {
      this.setPreferredSize(new Dimension(GameParam.SCREEN_WIDTH, GameParam.SCREEN_HEIGHT));
      this.setBackground(GameParam.DEFAULT_BACKGROUND);
      this.setDoubleBuffered(true);
      this.addKeyListener(this.keyHandler);
      this.setFocusable(true);

      // basic start - without menu, load, etc
      this.mapManager.loadMapById(1);

      // place player on the WORLD map
      this.player.setWorldCol(4);
      this.player.setWorldRow(3);
      this.player.setShiftX(0);
      this.player.setShiftY(0);

      // play start songs
      this.soundManager.setPlayOfSoundFileByKeyname("start");
      this.soundManager.play();

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
      mapManager.updateLights(player);

   }

   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      mapManager.drawMap(g2, player);

      // TODO: draw items
      // TODO: draw monsters

      // draw player
      player.draw(g2);

      // TODO: draw secondary walls & decorations (door, torch, others...)

      g2.dispose();

   }

}