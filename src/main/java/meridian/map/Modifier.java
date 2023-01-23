package meridian.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Modifier {

   int x;
   int y;
   double value;

   public Modifier(int x, int y, double value) {
      this.x = x;
      this.y = y;
      this.value = value;
   }

}