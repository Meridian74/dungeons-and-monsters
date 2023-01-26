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
public class ShadowCastingPosition {

   private int x;
   private int y;
   private List<DarkenedCell> darkenedCells = new ArrayList<>();

   public ShadowCastingPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

}
