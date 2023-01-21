/**
 * @author Meridian
 * @since  2023.
 */
package meridian.tile;


import meridian.main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


public class TileManager {

   private static final String DUNGEON_SET_01_JSON_CONFIG = "/tiles/dungeon_basic_set_01.json";
   private static final String DUNGEON_SET_01_FILE = "/tiles/dungeon_basic_set_01.png";
   private static final String DUNGEON_TEST_MAP = "/maps/test_map.map";

   private GamePanel gp;
   private Tile[] tiles;

   private int[][] mapTileIDs = new int[GamePanel.MAX_SCREEN_ROW][GamePanel.MAX_SCREEN_COL];


   public TileManager(GamePanel gp) {
      this.gp = gp;
      this.tiles = loadTileImages();
      this.loadMapData();
   }

   public void loadMapData() {
      try (InputStream inputStream = getClass().getResourceAsStream(DUNGEON_TEST_MAP);
           InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
           BufferedReader reader = new BufferedReader(streamReader)) {

         int row = 0;
         String line;
         while ((line = reader.readLine()) != null) {

            String[] numbers = line.split(" ");

            int col = 0;
            while (col < GamePanel.MAX_SCREEN_COL) {
               int num = Integer.parseInt(numbers[col]);

               mapTileIDs[row][col] = num;
               col++;
            }

            row++;
         }

      } catch (NumberFormatException | IOException ioe) {
         throw new IllegalStateException("Can not read file", ioe);
      }

   }

   public void draw(Graphics2D g2) {
      for (int row = 0; row < GamePanel.MAX_SCREEN_ROW; row++) {
         for (int col = 0; col < GamePanel.MAX_SCREEN_COL; col++) {
            int tileIndex = mapTileIDs[row][col];
            Tile tile = tiles[tileIndex];

            g2.drawImage(
                  tile.getImage(),
                  col * GamePanel.TILE_SIZE,
                  row * GamePanel.TILE_SIZE,
                  tile.getImage().getWidth() * GamePanel.SCALE,
                  tile.getImage().getHeight() * GamePanel.SCALE,
                  null
            );

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
      try (InputStream inputStream = getClass().getResourceAsStream(DUNGEON_SET_01_JSON_CONFIG);
           InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
           BufferedReader reader = new BufferedReader(streamReader)) {

         StringBuilder sb = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            sb.append(line);
         }

         String json = sb.toString();
         ObjectMapper objectMapper = new ObjectMapper();
         result = objectMapper.readValue(json, new TypeReference<>(){});
      }
      catch (IOException e) {
         System.err.println("Can not read dungeon's tile config file: " + e);
         System.exit(-1);
      }

      return result;
   }

}