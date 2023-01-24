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
      int[][] coords = { {3, 1} };
      List<ShadowCastingPosition> preCalculatedData = vm.initData(coords);
      List<DarkenedCell> modifiedCells = preCalculatedData.get(0).getDarkenedCells();

//      System.out.println("size: " + modifiedCells.size());
//      for (DarkenedCell m : modifiedCells) {
//         System.out.println("x: " + m.getX() + ", y: " + m.getY() + ", value: " + m.getValue());
//      }

      assertEquals(17, modifiedCells.size());
   }

   @Test
   void visibleMatrixInitAllData() {
      ShadeMatrix vm = new ShadeMatrix();
      List<ShadowCastingPosition> preCalculatedData = vm.initData(vm.getObjectSurroundCoords());

      assertEquals(416, preCalculatedData.size());
   }

}