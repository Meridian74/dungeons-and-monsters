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

   private int x;
   private int y;
   private double value;

   public CellDarkener(int x, int y, double value) {
      this.x = x;
      this.y = y;
      this.value = value;
   }

}