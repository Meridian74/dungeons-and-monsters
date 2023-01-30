/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;


import meridian.entity.Direction;
import meridian.entity.Entity;

public class CollisionChecker {

   MapManager mapManager;

   public CollisionChecker(MapManager mapManager) {
      this.mapManager = mapManager;
   }


   public boolean checkCollision(Entity entity, Direction direction) {

      return false;
   }

}