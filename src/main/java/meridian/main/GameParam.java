/**
 * @author Meridian
 * @since  2023.
 */
package meridian.main;

import java.awt.*;

public class GameParam {

   // Defining 16x16 pixels size tile.
   public static final int ORIGINAL_TILE_SIZE = 16;

   // Pixel size multiplier.
   public static final int SCALE = 3;

   // Increasing the tile size with multiplier (48x48 pixels tile).
   public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;

   // Define the screen size (768x576 pixels).
   public static final int MAX_SCREEN_COL = 19;
   public static final int MAX_SCREEN_ROW = 15;
   public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
   public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;

   public static final Color DEFAULT_BACKGROUND = Color.black;

   // FPS - Screen frame per second.
   public static final int FPS = 60;
   public static final long DRAW_INTERVAL = 1000000000 / FPS;

}
