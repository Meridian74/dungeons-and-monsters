/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import meridian.main.GamePanel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


@Getter
public class ShadeMatrix {

   static final int WIDTH = GamePanel.MAX_SCREEN_COL / 2;
   static final int HEIGHT = GamePanel.MAX_SCREEN_ROW / 2;

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

   // PreCalculated cell visibility modifiers data
   private final List<ShadowCasterCell> preCalculatedData;

   public ShadeMatrix() {
      this.preCalculatedData = initData(this.objectSurroundCoords);
   }

   public List<ShadowCasterCell> initData(int[][] coords) {
      List<ShadowCasterCell> result = new ArrayList<>();

      for (int[] xyCoordinate : coords) {
         int xx = xyCoordinate[0];
         int yy = xyCoordinate[1];
         double deltaY1 = (yy - 0.5) / xx;
         double deltaY2 = (yy + 0.5) / xx;
         ShadowCasterCell vbc = new ShadowCasterCell(xx, yy);

         // Check map cells.
         for (int row = 0; row <= HEIGHT; row++) {
            for (int col = row; col <= WIDTH; col++) {
               // Fully visible cells.
               if (isFullyVisibleCells(xx, yy, col, row))
                  continue;

               addShadedCell(vbc, deltaY1, deltaY2, col, row);
            }
         }
         result.add(vbc);

         // TODO: mirroring data all square

      }

      return result;
   }

   private static boolean isFullyVisibleCells(int xx, int yy, int col, int row) {
      boolean result;
      // This is the camera cell.
      if (row == 0 && col == 0) {
         result = true;
      }
      // This is the shadow caster cell.
      else if (row == yy && col == xx) {
         result = true;
      }
      // Cell is not situated in the direction of the shadow
      else if (col < xx) {
         result = true;
      }
      else {
         result = false;
      }
      return result;
   }

   private static void addShadedCell(ShadowCasterCell vbc, double deltaY1, double deltaY2, int col, int row) {
      // Upper line of light ray.
      double lightY1 = (row - 0.5);
      // Bottom line of light ray.
      double lightY2 = (row + 0.5);

      // Upper line of shadow ray.
      double shadowY1 = col * deltaY1;
      // Bottom line of shadow ray.
      double shadowY2 = col * deltaY2;

      // Fully shaded cell.
      if(lightY1 >= shadowY1 && lightY2 <= shadowY2) {
         vbc.getModifiedCells().add(new CellDarkener(col, row, 1.0));
         return;
      }

      // Partially shaded cell.
      double portion = 1.0;
      boolean portionIsModified = false;
      if (lightY1 > shadowY1 && lightY1 < shadowY2) {
         portion = portion - (1 - (shadowY2 - lightY1));
         portionIsModified = true;
      }
      if (lightY2 < shadowY2 && lightY2 > shadowY1) {
         portion = portion - (1 - (lightY2 - shadowY1));
         portionIsModified = true;
      }
      if (portionIsModified) {
         vbc.getModifiedCells().add(new CellDarkener(col, row, portion));
      }

   }

}