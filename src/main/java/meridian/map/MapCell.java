/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import lombok.Getter;
import lombok.Setter;
import meridian.tile.Tile;

@Getter
@Setter
public class MapCell {

   // Tile grafix
   private Tile tile;

   float currentOpacity;
   float visibleOpacity;

   // -- bottom Decoration element (fountain bottom part)
   // -- Gateway in direction to another map (-->id, x, y)
   // -- Item here...
   // -- Monster here...
   // -- upper Decoration here...(doorway)


}