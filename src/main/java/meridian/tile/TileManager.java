/**
 * @author Meridian
 * @since  2023.
 */
package meridian.tile;

import meridian.main.GameParam;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class TileManager {

   private Tile[] tiles;


   public void loadTiles(String tilesFileName) {
      List<TileConfig> tileConfigs = getTileConfigsFromJSON("/tiles/" + tilesFileName + ".json");
      this.tiles = new Tile[tileConfigs.size()];

      BufferedImage image;
      try {
         image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/" + tilesFileName + ".png")));
         for (TileConfig tc : tileConfigs) {
            Tile tile = new Tile();
            tile.setId(tc.getId());
            tile.setCollision(tc.isCollision());
            tile.setDescription(tc.getDescription());
            tile.setBlockFieldOfVision(tc.getShadeFactor());

            BufferedImage tileImage = image.getSubimage(
                  GameParam.ORIGINAL_TILE_SIZE * tc.getX(),
                  GameParam.ORIGINAL_TILE_SIZE * tc.getY(),
                  tc.getSizeX(), tc.getSizeY()
            );
            tile.setImage(tileImage);
            int index = tile.getId();

            this.tiles[index] = tile;
         }
      } catch (IOException | RasterFormatException e) {
         throw new IllegalStateException("Can not read dungeon's tile config file: " + e);
      }

   }

   public Tile getTileByIndex(int index) {
      if (index >= 0 && index < tiles.length) {
         return tiles[index];
      }
      throw new IllegalStateException(("Wrong tile index data (it must be between 0 and " +
            (tiles.length - 1) + "): " + index));
   }

   private List<TileConfig> getTileConfigsFromJSON(String tileConfigFileName) {
      List<TileConfig> result;
      try (InputStream inputStream = getClass().getResourceAsStream(tileConfigFileName)) {
         assert inputStream != null;
         try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
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
      }
      catch (IOException e) {
         throw new IllegalStateException("Can not read dungeon's tile config file: " + e);
      }

      return result;
   }

}