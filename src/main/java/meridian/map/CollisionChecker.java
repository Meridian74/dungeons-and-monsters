/**
 * @author Meridian
 * @since 2023.
 */
package meridian.map;


import meridian.entity.Direction;
import meridian.entity.Entity;
import meridian.tile.Tile;

public class CollisionChecker {

   MapManager mapManager;

   public CollisionChecker(MapManager mapManager) {
      this.mapManager = mapManager;
   }


   public boolean canMove(Entity entity, Direction direction) {
      boolean result = true;

      int entityPosX = entity.getWorldCol();
      int entityPosY = entity.getWorldRow();
      int shiftX = entity.getShiftX();
      int shiftY = entity.getShiftY();
      int nextPosX = entityPosX;
      int nextAfterPosX = entityPosX;
      int nextPosY = entityPosY;
      int nextAfterPosY = entityPosY;


      MapCell[][] cells = mapManager.getCells();

      switch (direction) {
         case UP -> {
            if (shiftY >= 0) {
               nextPosY--;
            }

            if (shiftX > 0 && shiftY >= 0) {
               nextAfterPosX--;
            }
            else if (shiftX < 0 && shiftY >= 0) {
               nextAfterPosX++;
            }

            if (nextPosY < mapManager.getWorldTop()) {
               result = false;
            }
         }
         case DOWN -> {
            if (shiftX <= 0) {
               nextPosY++;
            }

            if (shiftX > 0 && shiftY <= 0) {
               nextAfterPosX--;
            }
            else if (shiftX < 0 && shiftY <= 0) {
               nextAfterPosX++;
            }

            if (nextPosY >= mapManager.getWorldBottom()) {
               result = false;
            }
         }
         case LEFT -> {
            if (shiftX >= 0) {
               nextPosX--;
            }

            if (shiftY > 0 && shiftX >= 0) {
               nextAfterPosY--;
            }
            else if (shiftY < 0 && shiftX >= 0) {
               nextAfterPosY++;
            }

            if (nextPosX < mapManager.getWorldLeft()) {
               result = false;
            }
         }
         case RIGHT -> {
            if (shiftX <= 0) {
               nextPosX++;
               nextAfterPosX++;
            }

            if (shiftY > 0 && shiftX <= 0) {
               nextAfterPosY--;
            }
            else if (shiftY < 0 && shiftX <= 0) {
               nextAfterPosY++;
            }

            if (nextPosX >= mapManager.getWorldRight()) {
               result = false;
            }
         }
      }


      if (result) {
         Tile tile = cells[nextPosY][nextPosX].getTile();
         if (tile.isSolid()) {
            result = false;
         }
      }
      if (result) {
         Tile tile = cells[nextAfterPosY][nextAfterPosX].getTile();
         if (tile.isSolid()) {
            result = false;
         }
      }


      return result;
   }

}