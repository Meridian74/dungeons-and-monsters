/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ViewPosition {

   // Relative to the player's position.
   private int x;
   private int y;

   // List of the all shaded cells.
   private List<CellDarkener> cellDarkeners = new ArrayList<>();

   public ViewPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

}
