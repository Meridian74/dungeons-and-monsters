/**
 * @author Meridian
 * @since  2023.
 */
package meridian.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;


@Getter
@Setter
public class Entity {

   private int posX;
   private int posY;
   private int speed;

   // storing all pictures of the animation phases
   private BufferedImage[][] images;

   // state of the movement direction
   private String direction;

}