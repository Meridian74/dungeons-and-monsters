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
   public static final int[][] OBJECT_SURROUND = {
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

      for (int[] coords : OBJECT_SURROUND) {
         int xx = coords[0];
         int yy = coords[1];
         VisibilityBlockerCell vbc = new VisibilityBlockerCell(xx, yy);
         double deltaY1 = yy - 0.5 / xx;
         double deltaY2 = yy + 0.5 / xx;

         for (int row = 0; row < 8; row++) {
            for (int col = row; col < 10; col++) {
               // continue if it's centrum cell
               if (row == 0 && col == 0) {
                  continue;
               }
               // Continue if it's the visibility bokcker cell
               if (row == yy && col == xx) {
                  continue;
               }
               // continue if cell is situated left from object
               if (xx < col) {
                  continue;
               }

               // cell inline the viewer
               if (deltaY1 < 0) {
                  vbc.getModifiedCells().add(new Modifier(col, row, 1.0));
                  continue;
               }

               double lineY1 = col * deltaY1;
               double lineY2 = col * deltaY2;
               if(lineY1 < row - 0.5 && lineY2 > row + 0.5) {
                  vbc.getModifiedCells().add(new Modifier(col, row, 1.0));
                  continue;
               }
               double portion = 1.0;
               boolean modified = false;
               if (lineY1 > row - 0.5 && lineY1 < row + 0.5) {
                  portion = portion - (lineY1 - (row - 0.5));
                  modified = true;
               }
               if (lineY2 < row + 0.5 && lineY2 > row - 0.5) {
                  portion = portion - (row + 0.5) - lineY2);
                  modified = true;
               }
               if (modified) {
                  vbc.getModifiedCells().add(new Modifier(col, row, portion));
               }

            }

         }

         // TODO: mirroring data all square

      }

   }
}