package ge.ai.domino.console.transfer.dto.domino;

import ge.ai.domino.domain.domino.Tile;

import java.util.HashMap;
import java.util.Map;

public class TileDTO {

    private int x;

    private int y;

    private boolean played;

    private double me;

    private double him;

    private double bazaar;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public double getMe() {
        return me;
    }

    public void setMe(double me) {
        this.me = me;
    }

    public double getHim() {
        return him;
    }

    public void setHim(double him) {
        this.him = him;
    }

    public double getBazaar() {
        return bazaar;
    }

    public void setBazaar(double bazaar) {
        this.bazaar = bazaar;
    }

    public static TileDTO toTileDTO(Tile tile) {
        TileDTO dto = new TileDTO();
        dto.setX(tile.getX());
        dto.setY(tile.getY());
        dto.setPlayed(tile.isPlayed());
        dto.setMe(tile.getMe());
        dto.setHim(tile.getHim());
        dto.setBazaar(tile.getBazaar());
        return dto;
    }

    public static Tile toTile(TileDTO dto) {
        Tile tile = new Tile();
        tile.setX(dto.getX());
        tile.setY(dto.getY());
        tile.setPlayed(dto.isPlayed());
        tile.setMe(dto.getMe());
        tile.setHim(dto.getHim());
        tile.setBazaar(dto.getBazaar());
        return tile;
    }

    public static Map<String, TileDTO> toTileDTOMap(Map<String, Tile> tiles) {
        Map<String, TileDTO> dtoMap = new HashMap<>();
        for (String key : tiles.keySet()) {
            dtoMap.put(key, toTileDTO(tiles.get(key)));
        }
        return dtoMap;
    }

    public static Map<String, Tile> toTileMap(Map<String, TileDTO> dtoMap) {
        Map<String, Tile> tileMap = new HashMap<>();
        for (String key : dtoMap.keySet()) {
            tileMap.put(key, toTile(dtoMap.get(key)));
        }
        return tileMap;
    }
}
