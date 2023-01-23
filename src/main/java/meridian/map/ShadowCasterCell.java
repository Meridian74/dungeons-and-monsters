package meridian.map;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ShadowCasterCell {

   private int x;
   private int y;
   private List<CellDarkener> modifiedCells = new ArrayList<>();

   public ShadowCasterCell(int x, int y) {
      this.x = x;
      this.y = y;
   }

}
