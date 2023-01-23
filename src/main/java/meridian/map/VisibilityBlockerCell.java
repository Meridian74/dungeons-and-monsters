package meridian.map;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class VisibilityBlockerCell {

   private int x;
   private int y;
   private List<Modifier> modifiedCells = new ArrayList<>();

   public VisibilityBlockerCell(int x, int y) {
      this.x = x;
      this.y = y;
   }

}
