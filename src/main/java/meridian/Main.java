/**
 * @author Meridian
 * @since  2023.
 */
package meridian;

import javax.swing.*;


/**
 * main program
 */
public class Main {
   public static void main(String[] args) {

      JFrame window = new JFrame();
      window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      window.setResizable(false);
      window.setTitle("Dungeons and Monsters");

      GamePanel gamePanel = new GamePanel();
      window.add(gamePanel);

      window.pack();

      window.setLocationRelativeTo(null);
      window.setVisible(true);

   }

}