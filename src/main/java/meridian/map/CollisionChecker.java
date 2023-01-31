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


   /**
    * Since the movement consists of small partial positions and it is possible to go in
    * two main directions at the same time (in the case of diagonal movement), several cells
    * must be checked to see if they block free movement or not... if the cell is solid in
    * one of the main directions (vertically or horizontally), then it is false returns
    * with a value that prevents the entity from moving further in that direction. If a cell
    * is traversable, it returns true, and the entity moves in that direction.
    *
    * @param entity a player or a monster.
    * @param direction main direction of displacement under investigation.
    * @return boolean 'true': can move -- 'false': cannot move in that direction.
    */
   public boolean canMove(Entity entity, Direction direction) {

      int entityPosX = entity.getWorldCol();
      int entityPosY = entity.getWorldRow();
      int shiftX = entity.getShiftX();
      int shiftY = entity.getShiftY();

      int nextPosX = entityPosX;
      int nextPosY = entityPosY;
      int neighbourPosX = entityPosX;
      int neighbourPosY = entityPosY;

      switch (direction) {
         case UP -> {
            if (shiftY >= 0) {
               nextPosY--;
               neighbourPosY--;
            }

            if (nextPosY < mapManager.getWorldTop()) {
               return false;
            }

            if (shiftX < 0) {
               neighbourPosX++;
            }
            else if (shiftX > 0) {
               neighbourPosX--;
            }
         }
         case DOWN -> {
            if (shiftY <= 0) {
               nextPosY++;
               neighbourPosY++;
            }

            if (nextPosY >= mapManager.getWorldBottom()) {
               return false;
            }

            if (shiftX < 0) {
               neighbourPosX++;
            }
            else if (shiftX > 0) {
               neighbourPosX--;
            }
         }
         case LEFT -> {
            if (shiftX >= 0) {
               nextPosX--;
               neighbourPosX--;
            }

            if (nextPosX < mapManager.getWorldLeft()) {
               return false;
            }

            if (shiftY < 0) {
               neighbourPosY++;
            }
            else if (shiftY > 0) {
               neighbourPosY--;
            }
         }
         case RIGHT -> {
            if (shiftX <= 0) {
               nextPosX++;
               neighbourPosX++;
            }
            if (nextPosX >= mapManager.getWorldRight()) {
               return false;
            }

            if (shiftY < 0) {
               neighbourPosY++;
            }
            else if (shiftY > 0) {
               neighbourPosY--;
            }
         }
      }

      // Check in the direction of main move
      boolean result = isCellTraverseable(nextPosX, nextPosY);

      // Check in the direction diagonally as well.
      if (result) {
         result = isCellTraverseable(neighbourPosX, neighbourPosY);
      }

      return result;
   }

   private boolean isCellTraverseable(int nextPosX, int nextPosY) {
      MapCell[][] cells = this.mapManager.getCells();
      Tile tile = cells[nextPosY][nextPosX].getTile();
      return !tile.isSolid();
   }

}