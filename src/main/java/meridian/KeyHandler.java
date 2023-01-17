/**
 * @author Meridian
 * @since  2023.
 */
package meridian;

import lombok.Getter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


@Getter
public class KeyHandler implements KeyListener {

   private boolean leftPressed;
   private boolean rightPressed;
   private boolean upPressed;
   private boolean downPressed;


   @Override
   public void keyTyped(KeyEvent e) {


   }

   @Override
   public void keyPressed(KeyEvent e) {
      int code = e.getKeyCode();
      if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A || code == KeyEvent.VK_KP_LEFT) {
         this.leftPressed = true;
      }

      if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D || code == KeyEvent.VK_KP_RIGHT) {
            this.rightPressed =true;
      }

      if (code == KeyEvent.VK_UP || code ==  KeyEvent.VK_W || code == KeyEvent.VK_KP_UP) {
         this.upPressed = true;
      }

      if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S || code == KeyEvent.VK_KP_DOWN) {
         this.downPressed = true;
      }

   }

   @Override
   public void keyReleased(KeyEvent e) {
      int code = e.getKeyCode();
      if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A || code == KeyEvent.VK_KP_LEFT) {
         this.leftPressed = false;
      }

      if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D || code == KeyEvent.VK_KP_RIGHT) {
         this.rightPressed = false;
      }

      if (code == KeyEvent.VK_UP || code ==  KeyEvent.VK_W || code == KeyEvent.VK_KP_UP) {
         this.upPressed = false;
      }

      if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S || code == KeyEvent.VK_KP_DOWN) {
         this.downPressed = false;
      }

   }

}