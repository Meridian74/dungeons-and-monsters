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
//      int[][] coords = { {3, 1} };
      int[][] coords = { {2, 1} };
      ShadeMatrix vm = new ShadeMatrix(coords);
      List<ViewPosition> viewedPositions = vm.getViewPositions();

      System.out.println("viewed cell: " + viewedPositions.size());
      for (ViewPosition vp : viewedPositions) {
         System.out.println("Viewed position cell X: " + vp.getX() + ", Y: " + vp.getY());
         System.out.println("----------------------");
         System.out.println("--> Cell Darkeners <--");
         for (CellDarkener cd : vp.getCellDarkeners()) {
            System.out.println("cdX: " + cd.getX() + ", cdY: " + cd.getY() + ", cdValue: " + cd.getValue());
         }
         System.out.println("======================");
      }

//      List<CellDarkener> modifiedCells = viewedPositions.get(0).getCellDarkeners();
//      assertEquals(21, modifiedCells.size());
   }

   @Test
   void visibleMatrixInitAllData() {
      ShadeMatrix vm = new ShadeMatrix();
      List<ViewPosition> preCalculatedData = vm.getViewPositions();

      assertEquals(540, preCalculatedData.size());
   }

}