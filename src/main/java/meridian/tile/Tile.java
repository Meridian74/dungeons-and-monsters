/**
 * @author Meridian
 * @since  2023.
 */
package meridian.tile;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;


@Getter
@Setter
public class Tile {

   // ID
   private int id;

   // description
   private String description;

   // graphic appearance
   private BufferedImage image;

   // walkable (false: yes, true: block walking - def: walkable
   private boolean collision = false;

   /* factor affecting the visibility of the neighboring field in a percentage
    * way from the direction of the field of view
    * 0.0 = 100% transparent, 1.0 = 100% block visible of neighborg cell - def: 100% transparent
    */
   private float tileTransparency = 0.0f;

}