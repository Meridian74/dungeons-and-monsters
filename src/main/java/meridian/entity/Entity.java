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
   private Direction direction;

   // which pictures from current row
   private int animationIndex;

   // variables movement types row index offset (for example: normal move (0) and attack move (4))
   private int animationRowOffset;

   // screen frame counter
   private int screenFrameCounter;

}