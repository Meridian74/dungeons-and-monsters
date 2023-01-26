/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Getter
public class MapManager {

   private static final String MAP_LIST_FILE = "/maps/map_list.json";

   private int currentMapId;
   private String currentTileSet;
   private int mapWidth;
   private int mapHeight;

   // list of loadable maps data
   List<Map> maps;

   MapCell[][] cells;

   public MapManager() {
      this.maps = this.init();
   }

   private List<Map> init() {
      try (InputStream inputStream = getClass().getResourceAsStream(MAP_LIST_FILE)) {
         assert inputStream != null;
         try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
              BufferedReader reader = new BufferedReader(streamReader)) {

            StringBuilder sb = new StringBuilder();

            // Read all lines of the text file.
            String line;
            while ((line = reader.readLine()) != null) {
               sb.append(line);
            }

            String json = sb.toString();
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(json, new TypeReference<>(){});
         }
      }
      catch (IOException e) {
         throw new IllegalStateException("Can not read maps' config-json file: " + e);
      }

   }

   public void loadMapById(int mapId) {
      for (Map map : this.maps) {
         if (map.getId() == mapId) {
            loadMapData("/maps/" + map.getMapFileName());
            this.currentMapId = map.getId();
            this.currentTileSet = map.getTileSetName();
            this.mapWidth = map.getSizeX();
            this.mapHeight = map.getSizeY();
            break;
         }
      }
   }


   public void loadMapData(String mapName) {
      System.out.println("Should load map data (MapCells) here... coming soon!");
      System.out.println("Map name: " + mapName);
   }
}