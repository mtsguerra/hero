package arena;

import model.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArenaLoader {
    private final int width;
    private final int height;
    private final List<Wall> walls = new ArrayList<>();
    private final boolean[][] wallGrid;
    private final List<Coin> coins = new ArrayList<>();
    private Door door;
    private final List<Monster> monsters = new ArrayList<>();
    private Hero hero;

    public ArenaLoader(String resourcePath) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IOException("map not found: " + resourcePath);
        }

        // preferred over char[][], for it will be easier to access and final
        // (just initiate the arena)
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line = reader.readLine();
            while (line!= null) {
                lines.add(line);
                line = reader.readLine();
            }
        }

        this.height = lines.size();
        this.width = lines.isEmpty() ? 0 : lines.get(0).length();
        this.wallGrid = new boolean[width][height];

        parseLevel(lines);
    }

    /**
     * Parse the level from the given lines.
     *
     * 1. Start by finding the hero position (necessary for TrackingMonster),
     * in case there is no 'H' in the map, define a default position.
     * 2. Populate all elements (walls, monsters, coins [for now]).
     *
     * @param lines the lines to parse
     */
    private void parseLevel(List<String> lines) {
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == 'H') {
                    this.hero = new Hero(x, y);
                    break;
                }
            }
            if (this.hero != null) break;
        }

        if (this.hero == null) {
            this.hero = new Hero(1, 1);
        }

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                switch (c) {
                    case '#' -> {
                        walls.add(new Wall(x, y));
                        wallGrid[x][y] = true;
                    }
                    case 'O' -> coins.add(new Coin(x, y));
                    case 'D' -> this.door = new Door(x, y);
                    case 'R' -> monsters.add(new RandomMonster(x, y));
                    case 'P' -> monsters.add(new PatrolMonster(x, y));
                    case 'T' -> monsters.add(new TrackingMonster(x, y, hero));
                    default -> {}
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public Hero getHero() {
        return hero;
    }
    public List<Wall> getWalls() {
        return walls;
    }
    public boolean[][] getWallGrid() {
        return wallGrid;
    }
    public List<Coin> getCoins() {
        return coins;
    }
    public List<Monster> getMonsters() {
        return monsters;
    }
    public Door getDoor() {
        return door;
    }
}