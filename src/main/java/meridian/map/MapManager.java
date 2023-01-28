/**
 * @author Meridian
 * @since  2023.
 */
package meridian.map;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import meridian.entity.Player;
import meridian.main.GameParam;
import meridian.tile.Tile;
import meridian.tile.TileManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Getter
public class MapManager {


   private static final String MAP_LIST_FILE = "/maps/map_list.json";
   private static final int VOID_CELL_ID = 9999;

   private final TileManager tileManager;

   private int currentMapId;
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
            this.mapWidth = map.getSizeX();
            this.mapHeight = map.getSizeY();
            this.currentMapId = map.getId();

            // Load Map Tile set images
            this.tileManager.loadTiles(map.getTileSetName());

            // Create new Map Cells array by row/col and load all map information.
            this.cells = new MapCell[map.getSizeY()][map.getSizeX()];
            loadMapData("/maps/" + map.getMapFileName());

            // Define World Map edges.
            this.worldLeft = 0;
            this.worldTop = 0;
            this.worldRight = this.mapWidth;
            this.worldBottom = this.mapHeight;

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
                  Tile tile;
                  if (tileIndex != VOID_CELL_ID) {
                     tile = tileManager.getTileByIndex(tileIndex);
                  }
                  else { // void cell...!
                     tile = new Tile();
                     tile.setId(VOID_CELL_ID);
                  }

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

   public void updateLights() {
      // TODO set: cell opacity
   }

   public void drawMap(Graphics2D g2, Player player) {
      BufferedImage image;
      int drawX;
      int drawY;

      int startRow = player.getWorldRow() - GameParam.MAX_SCREEN_ROW / 2 - 1;
      int startCol = player.getWorldCol() - GameParam.MAX_SCREEN_COL / 2 - 1;
      int endRow = startRow + GameParam.MAX_SCREEN_ROW + 1;
      int endCol = startCol + GameParam.MAX_SCREEN_COL + 1;

      for (int row = startRow; row <= endRow && row < cells.length; row++) {
         if (row < 0) continue;
         drawY = (row - startRow - 1) * GameParam.TILE_SIZE + player.getShiftY();

         for (int col = startCol; col <= endCol && col < cells[row].length; col++) {
            if (col < 0) continue;

            Tile tile = cells[row][col].getTile();
            if (tile.getId() != VOID_CELL_ID) {
               drawX = (col - startCol - 1) * GameParam.TILE_SIZE + player.getShiftX();

               // Drawing current tile.
               image = tile.getImage();
               g2.drawImage(image, drawX, drawY, GameParam.TILE_SIZE, GameParam.TILE_SIZE, null);
            }

         }

      }

   }

}