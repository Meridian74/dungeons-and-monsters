/**
 * @author Meridian
 * @since  2023.
 */
package meridian.tile;


import meridian.main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;



public class TileManager {

   private static final String DUNGEON_SET_01_FILE = "/tiles/dungeon_basic_set_01.png";
   private static final String DUNGEON_SET_01_JSON_CONFIG = "file:src/main/resources/tiles/dungeon_basic_set_01.json";

   private GamePanel gp;
   private Tile[] tiles;


   public TileManager(GamePanel gp) {
      this.gp = gp;
      this.tiles = loadTileImages();
   }


   public void draw(Graphics2D g2) {
      int x = 0;
      int y = 0;
      for (Tile tile : tiles) {
         g2.drawImage(
               tile.getImage(),
               x, y,
               tile.getImage().getWidth() * GamePanel.SCALE,
               tile.getImage().getHeight() * GamePanel.SCALE,
               null
         );
         x = x + tile.getImage().getWidth() * GamePanel.SCALE;
         if (x >= GamePanel.SCREEN_WIDTH) {
            x = 0;
            y = y + tile.getImage().getHeight() * GamePanel.SCALE;
         }
      }

   }


   private Tile[] loadTileImages() {
      List<TileConfig> tileConfigs = getTileConfigsFromJSON();
      Tile[] result = new Tile[tileConfigs.size()];

      BufferedImage image = null;
      try {
         image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(DUNGEON_SET_01_FILE)));
         for (TileConfig tf : tileConfigs) {
            Tile tile = new Tile();
            tile.setId(tf.getId());
            tile.setCollision(tf.isCollision());
            tile.setDescription(tf.getDescription());
            tile.setBlockFieldOfVision(tf.getTransparency());

            BufferedImage tileImage = image.getSubimage(
                  GamePanel.ORIGINAL_TILE_SIZE * tf.getX(),
                  GamePanel.ORIGINAL_TILE_SIZE * tf.getY(),
                  tf.getXSize(), tf.getYSize()
            );
            tile.setImage(tileImage);

            int index = tile.getId();
            result[index] = tile;
         }
      } catch (IOException | RasterFormatException e) {
         System.err.println("Can not read data of the dungeon's tile pictures! " + e);
         System.exit(-1);
      }

      return result;
   }

   private List<TileConfig> getTileConfigsFromJSON() {
      List<TileConfig> result = Collections.emptyList();
      try {
         URL file = new URL(DUNGEON_SET_01_JSON_CONFIG);
         ObjectMapper objectMapper = new ObjectMapper();
         result = objectMapper.readValue(file, new TypeReference<>() {
         });
      }
      catch (IOException e) {
         System.err.println("Can not read dungeon's tile config file: " + e);
         System.exit(-1);
      }
      return result;
   }

}