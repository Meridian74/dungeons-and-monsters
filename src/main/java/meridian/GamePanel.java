/**
 * @author Meridian
 * @since  2023.
 */
package meridian;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;

/**
 * SCREEN SETTINGS
 */
public class GamePanel extends JPanel {

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


   /**
    * CONSTRUCTOR
    */
   public GamePanel() {
      this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
      this.setBackground(Color.black);
      this.setDoubleBuffered(true);

   }

}