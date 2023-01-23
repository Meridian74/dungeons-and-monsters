/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VisibleMatrix {

   // Arrays of x, y coords
   private final int[][] objectSurroundCoords = {

         // first circle
         {1, 0}, {1, 1},
         {0, 1},
         // second circle
         {2, 0}, {2, 1}, {2, 2},
         {1, 2}, {0, 2},
         // third circle
         {3, 0}, {3, 1}, {3, 2}, {3, 3},
         {2, 3}, {1, 3}, {0, 3},
         // 4th circle
         {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4},
         {3, 4}, {2, 4}, {1, 4}, {0, 4},
         // 5th circle
         {5, 0}, {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5},
         {4, 5}, {3, 5}, {2, 5}, {1, 5}, {0, 5},
         // 6th circle
         {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}, {6, 6},
         {5, 6}, {4, 6}, {3, 6}, {2, 6}, {1, 6}, {0, 6},
         // 7h part
         {7, 0}, {7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 6},
         // 8th part
         {8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 6},
   };


   private List<VisibilityBlockerCell> preCalculatedData;

   public VisibleMatrix() {
      this.initData();
   }

   private void initData() {
      this.preCalculatedData = new ArrayList<>();

      for (int[] coords : objectSurroundCoords) {
         int xx = coords[0];
         int yy = coords[1];
         double deltaY1 = yy - 0.5 / xx;
         double deltaY2 = yy + 0.5 / xx;
         VisibilityBlockerCell vbc = new VisibilityBlockerCell(xx, yy);

         // Check map cells.
         for (int row = 0; row < 8; row++) {
            for (int col = row; col < 10; col++) {

               // Fully visible cells.
               if (fullyVisibleCells(xx, yy, row, col))
                  continue;

               // Inline the viewer cells.
               if (cellInlineInCamera(vbc, deltaY1, row, col))
                  continue;

               addShadedCell(vbc, deltaY1, deltaY2, row, col);
            }
         }

         // TODO: mirroring data all square

      }

   }

   private static boolean fullyVisibleCells(int xx, int yy, int row, int col) {
      boolean result;
      // This is the camera cell.
      if (row == 0 && col == 0) {
         result = true;
      }
      // This is the shadow cell.
      else if (row == yy && col == xx) {
         result = true;
      }
      // Cell is not situated in the direction of the shadow
      else if (xx < col) {
         result = true;
      }
      else {
         result = false;
      }
      return result;
   }

   private static boolean cellInlineInCamera(VisibilityBlockerCell vbc, double deltaY1, int row, int col) {
      if (deltaY1 < 0) {
         vbc.getModifiedCells().add(new Modifier(col, row, 1.0));
         return true;
      }
      return false;
   }



   private static void addShadedCell(VisibilityBlockerCell vbc, double deltaY1, double deltaY2, int row, int col) {
      // Upper shadow line.
      double lineY1 = col * deltaY1;
      // Bottom shadow line.
      double lineY2 = col * deltaY2;

      // Fully shaded cell.
      if(lineY1 < row - 0.5 && lineY2 > row + 0.5) {
         vbc.getModifiedCells().add(new Modifier(col, row, 1.0));
         return;
      }

      // Partially shaded cell.
      double portion = 1.0;
      boolean portionIsModified = false;
      if (lineY1 > row - 0.5 && lineY1 < row + 0.5) {
         portion = portion - (lineY1 - (row - 0.5));
         portionIsModified = true;
      }
      if (lineY2 < row + 0.5 && lineY2 > row - 0.5) {
         portion = portion - ((row + 0.5) - lineY2);
         portionIsModified = true;
      }
      if (portionIsModified) {
         vbc.getModifiedCells().add(new Modifier(col, row, portion));
      }

   }

}