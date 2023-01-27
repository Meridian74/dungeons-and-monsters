/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import meridian.main.GameParam;
import meridian.tile.Tile;
import meridian.tile.TileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Getter
public class MapManager {


   private static final String MAP_LIST_FILE = "/maps/map_list.json";

   private final TileManager tileManager;

   private int currentMapId;
   private String currentTileSet;
   private int mapWidth;
   private int mapHeight;

   private int worldTop = 0;
   private int worldLeft = 0;
   private int worldRight = 0;
   private int worldBottom = 0;

   // list of loadable maps data
   List<Map> maps;

   // Storing all information of World Map in MapCells
   MapCell[][] cells;

   public MapManager(TileManager tm) {
      this.tileManager = tm;
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

            // create new Map Cells array by row/col.
            this.cells = new MapCell[map.getSizeY()][map.getSizeX()];

            // define World Map edges
            this.worldLeft = 0;
            this.worldTop = 0;
            this.worldRight = this.mapWidth * GameParam.TILE_SIZE;
            this.worldBottom = this.mapHeight * GameParam.TILE_SIZE;

            break;
         }
      }
   }


   public void loadMapData(String mapName) {
      try (InputStream inputStream = getClass().getResourceAsStream(mapName + ".map")) {
         assert inputStream != null;
         try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
              BufferedReader reader = new BufferedReader(streamReader)) {

            int row = 0;
            String line;
            while ((line = reader.readLine()) != null) {

               String[] currentRowData = line.split(" ");

               int currentCol = 0;
               while (currentCol < currentRowData.length && currentCol < cells[0].length) {

                  int tileIndex = Integer.parseInt(currentRowData[currentCol]);
                  Tile tile = tileManager.getTileByIndex(tileIndex);

                  MapCell cell = new MapCell();
                  cell.setTile(tile);
                  cell.setCurrentOpacity(1.0 - tile.getBlockFieldOfVision());

                  cells[row][currentCol] = cell;

                  currentCol++;
               }

               row++;
            }

         }

      }
      catch (IOException e) {
         throw new IllegalStateException("Cannot initialize World Map Cells from file: " + e);
      }

   }

}