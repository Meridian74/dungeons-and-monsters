/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Map {

   private int id;
   private String mapFileName;
   private String tileSetName;
   private int sizeX;
   private int sizeY;

}
