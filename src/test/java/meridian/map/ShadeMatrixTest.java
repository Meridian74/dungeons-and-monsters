/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShadeMatrixTest {

   @Test
   void visibleMatrixInitData() {
      ShadeMatrix vm = new ShadeMatrix();
      int[][] coords = {{2, 1}};
      List<ShadowCasterCell> preCalculatedData = vm.initData(coords);
      List<CellDarkener> modifiedCells = preCalculatedData.get(0).getModifiedCells();

      assertEquals(27, modifiedCells.size());
//      System.out.println("size: " + modifiedCells.size());
//      for (CellDarkener m : modifiedCells) {
//         System.out.println("x: " + m.getX() + ", y: " + m.getY() + ", value: " + m.getValue());
//      }

   }

}