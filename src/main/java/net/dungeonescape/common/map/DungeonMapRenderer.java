package net.dungeonescape.common.map;

import net.dungeonescape.DungeonEscapeMain;
import net.dungeonescape.DungeonGame;
import net.dungeonescape.common.DungeonPlayer;
import net.dungeonescape.dungeon.RoomLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.bukkit.util.BoundingBox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;

public class DungeonMapRenderer extends MapRenderer {
    private final DungeonEscapeMain plugin;
    private boolean rendered = false;
    private int completed = 0;
    private boolean selfKey = true;
    private boolean enemyKey = true;
    private boolean selfRoomCompleted = false;
    private boolean enemyRoomCompleted = false;
    private final HashMap<String, Image> imageCache = new HashMap<>();

    public DungeonMapRenderer(DungeonEscapeMain plugin) {
        super(true);
        this.plugin = plugin;
        imageCache.put("background", getImageFile("background.png"));
        imageCache.put("block", getImageFile("block.png"));
        imageCache.put("checkmark", getImageFile("checkmark.png"));
    }

    private Image getImageFile(String image) {
        try {
            return ImageIO.read(new File(plugin.getDataFolder().getPath() + "/images/" + image));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Image getImageURL(String image) {
        try {
            return ImageIO.read(new URL(image));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Image getImage(String image, boolean url) {
        if (imageCache.containsKey(image)) return imageCache.get(image);
        else if (url) return getImageURL(image);
        else return getImageFile(image + ".png");
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        DungeonGame game = plugin.getGameManager().getGame(player);
        if (game == null) return;

        DungeonPlayer dungeonPlayer = game.getDungeonPlayer(player);
        DungeonPlayer enemy = game.getEnemy(dungeonPlayer);
        RoomLocation dungeonRoom = dungeonPlayer.getRoomLocation(dungeonPlayer.getDungeonRoom());
        RoomLocation enemyRoom = enemy.getRoomLocation(enemy.getDungeonRoom());
        if (dungeonRoom == null || enemyRoom == null) return;

        MapCursorCollection cursor = new MapCursorCollection();
        Location playerLocation = player.getLocation().subtract(dungeonPlayer.getLocation());
        cursor.addCursor(new MapCursor((byte) -playerLocation.getBlockX(), (byte) (108-playerLocation.getBlockZ()), (byte) 8, MapCursor.Type.SMALL_WHITE_CIRCLE, true));
        canvas.setCursors(cursor);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!rendered) {
                drawBase(dungeonPlayer, canvas, player, enemy);
                drawChecks(dungeonPlayer, canvas);
                rendered = true;
            }

            if (dungeonPlayer.getRoomsCompleted() > completed) {
                completed++;
                drawChecks(dungeonPlayer, canvas);
            }

            boolean selfCompleted = dungeonRoom.isCompleted();
            boolean enemyCompleted = enemyRoom.isCompleted();
            if (dungeonPlayer.hasKey() != selfKey || enemy.hasKey() != enemyKey || selfCompleted != selfRoomCompleted || enemyCompleted != enemyRoomCompleted) {
                selfKey = dungeonPlayer.hasKey();
                enemyKey = enemy.hasKey();
                selfRoomCompleted = selfCompleted;
                enemyRoomCompleted = enemyCompleted;
                drawImage(canvas, 63, 63, getImage("background", false), 128, 128, false);
                if (!selfKey) drawImage(canvas, 16, 21, getImage("block", false), 16, 16, true);
                if (!enemyKey) drawImage(canvas, 110, 21, getImage("block", false), 16, 16, true);
                if (selfRoomCompleted) drawImage(canvas, 16, 103, getImage("checkmark", false), 16, 16, true);
                if (enemyRoomCompleted) drawImage(canvas, 110, 103, getImage("checkmark", false), 16, 16, true);
            }
        });
    }

    private void drawChecks(DungeonPlayer dungeonPlayer, MapCanvas canvas) {
        dungeonPlayer.getRoomLocations().forEach(roomLocation -> {
            Location location = roomLocation.getLocation().subtract(dungeonPlayer.getLocation());
            if (roomLocation.isCompleted()) drawImage(canvas, -(location.getBlockX()/2)+64, 117-(location.getBlockZ()/2), getImage("checkmark", false), 6, 6, true);
        });
    }

    private void drawBase(DungeonPlayer dungeonPlayer, MapCanvas canvas, Player player, DungeonPlayer enemy) {
        drawImage(canvas, 63, 63, getImage("background", false), 128, 128, false);
        drawImage(canvas, 16, 63, getImage("https://cravatar.eu/helmavatar/" + player.getName(), true), 16, 16, true);
        drawImage(canvas, 110, 63, getImage("https://cravatar.eu/helmavatar/" + enemy.getPlayer().getName(), true), 16, 16, true);

        for (int x = 34; x < 94; x++) {
            for (int z = 0; z < 128; z++) {
                canvas.setPixelColor(x, z, new Color(112, 112, 180));
            }
        }

        dungeonPlayer.getRoomLocations().forEach(roomLocation -> {
            Location location = roomLocation.getLocation().subtract(dungeonPlayer.getLocation());
            BoundingBox boundingBox = roomLocation.getDungeonRoom().getBoundingBox();

            roomLocation.getDungeonRoom().getDoors().forEach(doorLocation -> {
                int locX = location.clone().add(doorLocation.getVector()).getBlockX() / 2;
                int locZ = location.clone().add(doorLocation.getVector()).getBlockZ() / 2;
                for (int x = locX - 1; x < locX + 1; x++) {
                    for (int z = locZ - 1; z < locZ + 1; z++) {
                        canvas.setPixelColor(-x + 64, 117 - z, new Color(79, 79, 79));
                    }
                }
            });

            int minX = (location.getBlockX() + (int) boundingBox.getMinX()) / 2;
            int maxX = (location.getBlockX() + (int) boundingBox.getMaxX()) / 2;
            int minZ = (location.getBlockZ() + (int) boundingBox.getMinZ()) / 2;
            int maxZ = (location.getBlockZ() + (int) boundingBox.getMaxZ()) / 2;

            for (int x = minX; x < maxX; x++) {
                for (int z = minZ; z < maxZ; z++) {
                    if (x != minX && z != minZ) {
                        canvas.setPixelColor(-x + 64, 117 - z, new Color(106, 76, 54));
                    }
                }
            }
        });
    }

    private static void drawImage(MapCanvas canvas, int posX, int posZ, Image unsizedImage, int scaleX, int scaleZ, boolean drawWhite) {
        try {
            Image image = unsizedImage.getScaledInstance(scaleX, scaleZ, Image.SCALE_SMOOTH);
            byte[] bytes = MapPalette.imageToBytes(image);
            int width = image.getWidth(null);
            int height = image.getHeight(null);

            for (int x = 0; x < width; ++x) {
                for (int z = 0; z < height; ++z) {
                    byte color = bytes[z * width + x];
                    if (color != 0 && (drawWhite || color != 34)) {
                        canvas.setPixel((posX - (width / 2) + 1) + x, (posZ - (height / 2) + 1) + z, color);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
