/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DarkenedCell {

   private int x;
   private int y;
   private double value;

   public DarkenedCell(int x, int y, double value) {
      this.x = x;
      this.y = y;
      this.value = value;
   }

}