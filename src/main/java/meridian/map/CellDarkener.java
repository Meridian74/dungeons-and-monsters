/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellDarkener {

   // Relative to the player's position
   private int x;
   private int y;

   // Cell opacity modifier.
   private float value;

   public CellDarkener(int x, int y, float value) {
      this.x = x;
      this.y = y;
      this.value = value;
   }

}