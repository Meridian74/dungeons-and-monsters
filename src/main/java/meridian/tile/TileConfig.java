/**
 * @author Meridian
 * @since  2023.
 */
package meridian.tile;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TileConfig {

   private int id;
   private int x;
   private int y;
   private int xSize;
   private int ySize;
   private boolean collision;
   private double transparency;
   private String description;

}