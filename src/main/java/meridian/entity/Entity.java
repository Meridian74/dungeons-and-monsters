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

   // Position on the world map by tilesize.
   private int worldCol;
   private int worldRow;
   private int shiftY;
   private int shiftX;

   // Movement speed in pixels.
   private int speed;

   // Storing all pictures of the animation phases
   private BufferedImage[][] images;

   // State of the movement direction
   private Direction direction;

   // Which pictures from current row
   private int animationPhaseIndex;

   // Variables movement types row index offset (for example: normal move (0) and attack move (4))
   private int animationRowOffset;

   // Screen frame counter
   private int currentDrawedFrame;

}