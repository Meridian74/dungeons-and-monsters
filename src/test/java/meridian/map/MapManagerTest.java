/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class MapManagerTest {

   @Test
   void mapConfigLoadTest() {

      MapManager mm = new MapManager(null);

      int numOfMaps = mm.getMaps().size();
      assertEquals(2, numOfMaps);

      String secondMapName = mm.getMaps().get(1).getMapFileName();
      assertEquals("test_map2", secondMapName);

      int firstMapSizeX = mm.getMaps().get(0).getSizeX();
      assertEquals(19, firstMapSizeX);

      int secondMapSizeY = mm.getMaps().get(1).getSizeY();
      assertEquals(45, secondMapSizeY);

   }

}