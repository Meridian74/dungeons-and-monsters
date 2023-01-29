/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;


import meridian.entity.Player;
import meridian.main.GameParam;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


@Getter
public class ShadeMatrix {

   private static final int WIDTH = GameParam.MAX_SCREEN_COL / 2 + 1;
   private static final int HEIGHT = GameParam.MAX_SCREEN_ROW / 2 + 1;

   // Arrays of x, y coords
   private static final int[][] OBJECT_SURROUND_COORDS = {
         // first circle
         {1, 0}, {1, 1},
//         {0, 1},
         // second circle
         {2, 0}, {2, 1}, {2, 2},
//         {1, 2}, {0, 2},
         // third circle
         {3, 0}, {3, 1}, {3, 2}, {3, 3},
//         {2, 3}, {1, 3}, {0, 3},
         // 4th circle
         {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4},
//         {3, 4}, {2, 4}, {1, 4}, {0, 4},
         // 5th circle
         {5, 0}, {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5},
//         {4, 5}, {3, 5}, {2, 5}, {1, 5}, {0, 5},
         // 6th circle
         {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}, {6, 6},
//         {5, 6}, {4, 6}, {3, 6}, {2, 6}, {1, 6}, {0, 6},
         // 7h part
         {7, 0}, {7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 6}, {7, 7},
//         {6, 7}, {5, 7}, {4, 7}, {3, 7}, {2, 7}, {1, 7}, {0, 7},

         // 8th part - left/right side of screen
         {8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 6}, {8, 7},
         // 9th part
         {9, 0}, {9, 1}, {9, 2}, {9, 3}, {9, 4}, {9, 5}, {9, 6}, {9, 7},
   };

   // PreCalculated cell visibility modifiers data
   private final List<ViewPosition> viewPositions;

   public ShadeMatrix() {
      this.viewPositions = initData(OBJECT_SURROUND_COORDS);
   }

   public ShadeMatrix(int[][] coords) {
      this.viewPositions = initData(coords);
   }

   public void updateMapCellsVisibility(MapCell[][] cells, Player player) {
      int playerX = player.getWorldCol();
      int playerY = player.getWorldRow();
      int viewedCellRow;
      int viewedCellCol;
      int coveredCellRow;
      int coveredCellCol;
      MapCell cell;

      for (ViewPosition vp : viewPositions) {
         viewedCellRow = playerY + vp.getY();
         if (viewedCellRow < 0 || viewedCellRow > cells.length - 1)
            continue;

         viewedCellCol = playerX + vp.getX();
         if (viewedCellCol < 0 || viewedCellCol > cells[viewedCellRow].length - 1)
            continue;

         float visibilityBlocker = cells[viewedCellRow][viewedCellCol].getTile().getBlockFieldOfVision();
         // If the cell obscures the cells behind it
         if (visibilityBlocker > 0.0001f) {
            for (CellDarkener currentDarkener : vp.getCellDarkeners()) {
               coveredCellRow = playerY + currentDarkener.getY();
               if (coveredCellRow < 0 || coveredCellRow > cells.length - 1)
                  continue;

               coveredCellCol = playerX + currentDarkener.getX();
               if (coveredCellCol < 0 || coveredCellCol > cells[coveredCellRow].length - 1)
                  continue;

               cell = cells[coveredCellRow][coveredCellCol];
               float opacity = cell.getVisibleOpacity();
               if (opacity < 0.0001f)
                  continue;

               // Calculate opacity value according to the viewer point.
               cell.setVisibleOpacity(opacity - currentDarkener.getValue() * visibilityBlocker);
               if (cell.getVisibleOpacity() < 0.000f) {
                  cell.setVisibleOpacity(0.0f);
               }

            }

         }

      }

   }

   private List<ViewPosition> initData(int[][] coords) {
      List<ViewPosition> result = new ArrayList<>();

      for (int[] xyCoordinate : coords) {
         int xx = xyCoordinate[0];
         int yy = xyCoordinate[1];
         float deltaY1 = (yy - 0.5f) / xx;
         float deltaY2 = (yy + 0.5f) / xx;
         ViewPosition castingPosition = new ViewPosition(xx, yy);

         // Search shaded map cells - in XY triangle.
         for (int row = 0; row <= HEIGHT; row++) {
            for (int col = row; col <= WIDTH; col++) {
               // Fully visible cells.
               if (isFullyVisibleCells(xx, yy, col, row))
                  continue;

               findShadedCell(castingPosition, deltaY1, deltaY2, col, row);
            }
         }

         // range data X diagonal
         if (yy == 0) {
            List<CellDarkener> additionalDarkeners = mirrorDarkenersOnY(castingPosition);
            castingPosition.getCellDarkeners().addAll(additionalDarkeners);
         }

         // range date if it is X/Y diagonal
         if (xx == yy) {
            List<CellDarkener> additionalDarkeners = mirrorDarkenersOnXY(castingPosition, 0);
            // Update list if not empty (because the screen shape --> width > height).
            if (!additionalDarkeners.isEmpty()) {
               castingPosition.getCellDarkeners().addAll(additionalDarkeners);
            }
         }

         // Save position with shaded cell list into result.
         result.add(castingPosition);

         // Copy and add mirrored position's result
         if (xx != yy) {
            // Save mirrored new position with shaded cell list into result.
            ViewPosition mirroredXYPosition = new ViewPosition(yy, xx);
            List<CellDarkener> mirroredDarkeners = mirrorDarkenersOnXY(castingPosition, 1);
            mirroredXYPosition.getCellDarkeners().addAll(mirroredDarkeners);

            result.add(mirroredXYPosition);
         }

      }

      // Extend result with HORIZONTALLY mirrored positions.
      List<ViewPosition> mirroredX = copyAndMirrorHorizontally(result);
      result.addAll(mirroredX);

      // Extend result with VERTICALLY mirrored positions.
      List<ViewPosition> mirroredY = copyAndMirrorVertically(result);
      result.addAll(mirroredY);

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

   private static void findShadedCell(ViewPosition vbc, float deltaY1, float deltaY2, int col, int row) {
      // Upper line of light ray.
      float lightY1 = (row - 0.5f);
      // Bottom line of light ray.
      float lightY2 = (row + 0.5f);

      // Upper line of shadow ray.
      float shadowY1 = col * deltaY1;
      // Bottom line of shadow ray.
      float shadowY2 = col * deltaY2;

      // Fully shaded cell.
      if(lightY1 >= shadowY1 && lightY2 <= shadowY2) {
         vbc.getCellDarkeners().add(new CellDarkener(col, row, 1.0f));

         return;
      }

      // Partially shaded cell.
      float portion = 1.0f;
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
         vbc.getCellDarkeners().add(new CellDarkener(col, row, portion));

      }

   }

   private static List<CellDarkener> mirrorDarkenersOnY(ViewPosition casterCell) {
      List<CellDarkener> darkeners = casterCell.getCellDarkeners();
      List<CellDarkener> additionalDarkeners = new ArrayList<>();
      for (CellDarkener cd : darkeners) {
         int x = cd.getX();
         int y = cd.getY();
         if (y != 0 && x <= HEIGHT + 2) { // Because the screen wider than tall

            // FYI: Swap Y with -y!
            CellDarkener newCd = new CellDarkener(x, -y, cd.getValue());

            additionalDarkeners.add(newCd);
         }
      }
      return additionalDarkeners;
   }

   /**
    * @param casterCell data that need to mirror
    * @param offset 0: diagonal, 1: non-diagonal mirroring
    * @return ranged cell visibility data list
    */
   private static List<CellDarkener> mirrorDarkenersOnXY(ViewPosition casterCell, int offset) {
      List<CellDarkener> darkeners = casterCell.getCellDarkeners();
      List<CellDarkener> additionalDarkeners = new ArrayList<>();
      for (CellDarkener cd : darkeners) {
         int x = cd.getX();
         int y = cd.getY();
         if (x != y && x < HEIGHT + offset) { // Because the screen wider than tall

            // FYI: Swap x and y!
            CellDarkener newCd = new CellDarkener(y, x, cd.getValue());

            additionalDarkeners.add(newCd);
         }
      }
      return additionalDarkeners;
   }

   private static List<ViewPosition> copyAndMirrorVertically(List<ViewPosition> result) {
      // copy and mirror result VERTICALLY and add to result
      List<ViewPosition> additionalResult = new ArrayList<>();
      for (ViewPosition scp : result) {

         // Except data in the horizontal mirroring angle!
         if (scp.getY() != 0) {
            ViewPosition mirroredSCP = new ViewPosition(scp.getX(), -(scp.getY())); // mirrored Y position
            List<CellDarkener> darkeners = scp.getCellDarkeners();
            for (CellDarkener cellDarkener : darkeners) {
               int x = cellDarkener.getX();
               int y = cellDarkener.getY();
               CellDarkener mirroredDC = new CellDarkener(x, -y, cellDarkener.getValue());

               mirroredSCP.getCellDarkeners().add(mirroredDC);
            }

            additionalResult.add(mirroredSCP);
         }

      }

      return additionalResult;
   }

   private static List<ViewPosition> copyAndMirrorHorizontally(List<ViewPosition> originalResult) {
      // copy and mirror result HORIZONTALLY and add to result
      List<ViewPosition> additionalResult = new ArrayList<>();
      for (ViewPosition scp : originalResult) {

         // Except data in the vertical mirroring angle!
         if (scp.getX() != 0) {
            // Create new with mirrored X position
            ViewPosition mirroredSCP = new ViewPosition(-(scp.getX()), scp.getY());

            List<CellDarkener> darkeners = scp.getCellDarkeners();
            for (CellDarkener cellDarkener : darkeners) {
               int x = cellDarkener.getX();
               int y = cellDarkener.getY();
               CellDarkener mirroredDC = new CellDarkener(-x, y, cellDarkener.getValue());

               mirroredSCP.getCellDarkeners().add(mirroredDC);
            }

            additionalResult.add(mirroredSCP);
         }

      }

      return additionalResult;
   }

}