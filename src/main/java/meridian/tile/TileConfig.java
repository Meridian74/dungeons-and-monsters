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
   private int sizeX;
   private int sizeY;
   private boolean collision;
   private float shadeFactor;
   private String description;

}