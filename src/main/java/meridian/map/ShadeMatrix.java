/**
 * @author Meridian
 * @since 2023.
 */
package meridian.map;


import meridian.entity.Entity;
import meridian.main.GameParam;

import lombok.Getter;

import java.util.List;
import java.util.ArrayList;


@Getter
public class ShadeMatrix {

   private static final int WIDTH = GameParam.MAX_SCREEN_COL / 2 + 1;
   private static final int HEIGHT = GameParam.MAX_SCREEN_ROW / 2 + 1;

   // Arrays of relative X & Y coords from the Entity
   private static final int[][] RELATIVE_COORDS = {
         // 1st ring
         {1, 0}, {1, 1},
         // 2nd ring
         {2, 0}, {2, 1}, {2, 2},
         // 3rd ring
         {3, 0}, {3, 1}, {3, 2}, {3, 3},
         // 4th ring
         {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4},
         // 5th ring
         {5, 0}, {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5},
         // 6th ring
         {6, 0}, {6, 1}, {6, 2}, {6, 3}, {6, 4}, {6, 5}, {6, 6},
         // 7th ring
         {7, 0}, {7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 6}, {7, 7},
         // 8th ring - truncated with screen height
         {8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5}, {8, 6}, {8, 7},
         // 9th ring - truncated with screen height
         {9, 0}, {9, 1}, {9, 2}, {9, 3}, {9, 4}, {9, 5}, {9, 6}, {9, 7},
   };

   // Stored pre-calculated cell visibility modifiers with relative coords from Entity.
   private final List<ViewPosition> viewPositions;

   // Basic constructor
   public ShadeMatrix() {
      this.viewPositions = initData(RELATIVE_COORDS);
   }

   // Constructor for testing one or more given coords.
   public ShadeMatrix(int[][] coords) {
      this.viewPositions = initData(coords);
   }

   public void updateMapCellsVisibility(MapCell[][] cells, Entity entity) {
      int entityX = entity.getWorldCol();
      int entityY = entity.getWorldRow();
      int viewedCellRow;
      int viewedCellCol;

      for (ViewPosition vp : viewPositions) {
         viewedCellRow = entityY + vp.getY();
         viewedCellCol = entityX + vp.getX();

         if (viewedCellRow < 0 || viewedCellRow > cells.length - 1 ||
               viewedCellCol < 0 || viewedCellCol > cells[viewedCellRow].length - 1) {

            // Because of it is outside of World Map then get next...!
            continue;
         }

         float cellTransparency = cells[viewedCellRow][viewedCellCol].getTile().getTileTransparency();

         // If it is not completely transparent, this cell will cover the cells behind it.
         if (cellTransparency > 0.0001f) {
            setCoveredMapCells(cells, entityX, entityY, vp, cellTransparency);
         }

      }

   }

   private void setCoveredMapCells(MapCell[][] cells, int entityX, int entityY, ViewPosition vp, float blockerCellTransparency) {
      MapCell cell;
      int coveredCellRow;
      int coveredCellCol;
      for (CellDarkener currentDarkener : vp.getCellDarkeners()) {
         coveredCellRow = entityY + currentDarkener.getY();
         coveredCellCol = entityX + currentDarkener.getX();

         if (coveredCellRow < 0 || coveredCellRow > cells.length - 1 ||
               coveredCellCol < 0 || coveredCellCol > cells[coveredCellRow].length - 1) {

            // Because of it is outside of World Map then get next...!
            continue;
         }

         cell = cells[coveredCellRow][coveredCellCol];
         float opacity = cell.getVisibleOpacity();
         if (opacity > 0.0001f) {

            // Calculate opacity value according to the viewer point.
            cell.setVisibleOpacity(opacity - currentDarkener.getValue() * blockerCellTransparency);
            if (cell.getVisibleOpacity() < 0.0f) {
               cell.setVisibleOpacity(0.0f);
            }

         }

      }

   }

   // Precalculating data by examining shading vectors.
   private List<ViewPosition> initData(int[][] coords) {
      List<ViewPosition> result = new ArrayList<>();

      for (int[] xyCoordinate : coords) {
         int xx = xyCoordinate[0];
         int yy = xyCoordinate[1];
         float deltaY1 = (yy - 0.5f) / xx;
         float deltaY2 = (yy + 0.5f) / xx;

         ViewPosition viewPosition = new ViewPosition(xx, yy);
         findCoveredCells(xx, yy, deltaY1, deltaY2, viewPosition);

         // extends data if it is situated on the X axis
         if (yy == 0) {
            findMoreCoveredCellByHorizontalAxis(viewPosition);
         }
         // extends date if it is X/Y diagonal
         if (xx == yy) {
            findMoreCoveredCellsByXYDiagonal(viewPosition);
         }

         // Save result in this position with list of fully or partially covered cells.
         result.add(viewPosition);

         // Copy and add mirrored position's result - obtaining a __QUARTER__ of the whole map.
         if (xx != yy) {
            // Save mirrored new position with shaded cell list into result.
            ViewPosition mirroredXYPosition = new ViewPosition(yy, xx);
            List<CellDarkener> mirroredDarkeners = mirrorDarkenersOnXY(viewPosition, 1);
            mirroredXYPosition.getCellDarkeners().addAll(mirroredDarkeners);

            result.add(mirroredXYPosition);
         }

      }

      // Mirror result HORIZONTALLY - obtaining a __HALF__ of the whole map.
      List<ViewPosition> mirroredX = copyAndMirrorHorizontally(result);
      result.addAll(mirroredX);

      // Mirror result VERTICALLY - obtaining a __FULL__ of the whole map.
      List<ViewPosition> mirroredY = copyAndMirrorVertically(result);
      result.addAll(mirroredY);

      return result;
   }

   private void findCoveredCells(int xx, int yy, float deltaY1, float deltaY2, ViewPosition viewPosition) {
      // Search shaded map cells - in XY triangle.
      for (int row = 0; row <= HEIGHT; row++) {
         for (int col = row; col <= WIDTH; col++) {

            // This is a fully visible cells - get next.
            if (isFullyVisibleCells(xx, yy, col, row))
               continue;

            updateViewPositionCellDarkeners(viewPosition, deltaY1, deltaY2, col, row);
         }
      }

   }

   private void findMoreCoveredCellsByXYDiagonal(ViewPosition viewPosition) {
      List<CellDarkener> additionalDarkeners = mirrorDarkenersOnXY(viewPosition, 0);
      // Update list if not empty (because the screen shape --> width > height).
      if (!additionalDarkeners.isEmpty()) {
         viewPosition.getCellDarkeners().addAll(additionalDarkeners);
      }

   }

   private void findMoreCoveredCellByHorizontalAxis(ViewPosition viewPosition) {
      List<CellDarkener> additionalDarkeners = mirrorDarkenersOnY(viewPosition);
      // Update list if not empty (because the screen shape --> width > height).
      if (!additionalDarkeners.isEmpty()) {
         viewPosition.getCellDarkeners().addAll(additionalDarkeners);
      }

   }

   private boolean isFullyVisibleCells(int xx, int yy, int col, int row) {
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
      } else {
         result = false;
      }
      return result;
   }

   private void updateViewPositionCellDarkeners(ViewPosition viewPosition, float deltaY1, float deltaY2, int col, int row) {
      // Upper line of light ray.
      float lightY1 = (row - 0.5f);
      // Bottom line of light ray.
      float lightY2 = (row + 0.5f);

      // Upper line of shadow ray.
      float shadowY1 = col * deltaY1;
      // Bottom line of shadow ray.
      float shadowY2 = col * deltaY2;

      // Fully shaded cell.
      if (lightY1 >= shadowY1 && lightY2 <= shadowY2) {
         viewPosition.getCellDarkeners().add(new CellDarkener(col, row, 1.0f));
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
         viewPosition.getCellDarkeners().add(new CellDarkener(col, row, portion));
      }

   }

   private List<CellDarkener> mirrorDarkenersOnY(ViewPosition casterCell) {
      List<CellDarkener> darkeners = casterCell.getCellDarkeners();
      List<CellDarkener> additionalDarkeners = new ArrayList<>();
      for (CellDarkener cd : darkeners) {
         int x = cd.getX();
         int y = cd.getY();
         if (y != 0 && x <= WIDTH) { // Because the screen wider than tall

            // FYI: Swap Y with -Y!
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
   private List<CellDarkener> mirrorDarkenersOnXY(ViewPosition casterCell, int offset) {
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

   private List<ViewPosition> copyAndMirrorVertically(List<ViewPosition> result) {
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

   private List<ViewPosition> copyAndMirrorHorizontally(List<ViewPosition> originalResult) {
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