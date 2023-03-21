package com.cairns.rich.aoc._2020;

import com.cairns.rich.aoc.Loader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

// TODO: this could definitely be done better
class Day20 extends Base2020 {
  private static final int[][] monsterDeltaRowAndCols = {
      { 0, 0 },
      { 1, -18 }, { 1, -13 }, { 1, -12 }, { 1, -7 }, { 1, -6 }, { 1, -1 }, { 1, 0 }, { 1, 1 },
      { 2, -17 }, { 2, -14 }, { 2, -11 }, { 2, -8 }, { 2, -5 }, { 2, -2 }
  };
  private static final List<Consumer<boolean[][]>> transformOperations = List.of(
      Day20::rotate,
      Day20::rotate,
      Day20::rotate,
      Day20::vertFlip,
      Day20::rotate,
      Day20::rotate,
      Day20::rotate,
      Day20::rotate
  );

  @Override
  protected Object part1(Loader loader) {
    return fail("Why does part 2 fail if this is implemented :-(  something is dependent on itr order.  static state?");
  }

  @Override
  protected Object part2(Loader loader) {
    Map<Long, Tile> tiles = parseTiles(loader.ml());
    Set<Connection> cornerConnections = findCornerConnections(tiles);
    System.out.println(productOfCorners(cornerConnections));

    boolean[][] picture = buildPicture(tiles, cornerConnections);
    int numMonsters = getNumMonsters(picture);
    return countAll(picture) - numMonsters * monsterDeltaRowAndCols.length;
  }

  private int getNumMonsters(boolean[][] picture) {
    List<Long> monsterHeadIndexes = findMonsterHeadIndexes(picture);
    for (int i = 0; monsterHeadIndexes.isEmpty(); ++i) {
      transformOperations.get(i).accept(picture);
      monsterHeadIndexes = findMonsterHeadIndexes(picture);
    }
    return monsterHeadIndexes.size();
  }

  private int countAll(boolean[][] picture) {
    int total = 0;
    for (int row = 0; row < picture.length; ++row) {
      for (int col = 0; col < picture[0].length; ++col) {
        if (picture[row][col]) {
          ++total;
        }
      }
    }
    return total;
  }

  private List<Long> findMonsterHeadIndexes(boolean[][] picture) {
    List<Long> monsterHeads = new ArrayList<>();
    for (int row = 0; row < picture.length - 2; ++row) {
      for (int col = 18; col < picture[row].length - 1; ++col) {
        if (doesMonsterStartHere(picture, row, col)) {
          monsterHeads.add(((long) row) + (((long) col) << 32));
        }
      }
    }
    return monsterHeads;
  }

  private boolean doesMonsterStartHere(boolean[][] picture, int row, int col) {
    for (int[] monsterDeltaRowCol : monsterDeltaRowAndCols) {
      if (!picture[row + monsterDeltaRowCol[0]][col + monsterDeltaRowCol[1]]) {
        return false;
      }
    }
    return true;
  }

  private boolean[][] buildPicture(
      Map<Long, Tile> tiles,
      Set<Connection> cornerConnections
  ) {
    int dimension = (int) Math.sqrt(tiles.size());
    List<List<Connection>> grid = new ArrayList<>();
    addRow(grid, tiles, cornerConnections.iterator().next());
    addRow(grid, tiles, findSecondRowLeadConnection(tiles, grid.get(0).get(0)));
    for (int i = 2; i < dimension; ++i) {
      addRow(grid, tiles, findNextRowLeadConnection(grid, tiles));
    }
    orient(grid);

    int pictureDim = dimension * (grid.get(0).get(0).tile.cells.length - 2);
    boolean[][] picture = new boolean[pictureDim][pictureDim];
    for (int gridRow = 0; gridRow < grid.size(); ++gridRow) {
      for (int gridCol = 0; gridCol < grid.get(0).size(); ++gridCol) {
        Tile tile = grid.get(gridRow).get(gridCol).tile;
        for (int tileRow = 1; tileRow < tile.cells.length - 1; ++tileRow) {
          for (int tileCol = 1; tileCol < tile.cells[0].length - 1; ++tileCol) {
            picture[gridRow * 8 + (tileRow - 1)][gridCol * 8 + (tileCol - 1)] = tile.cells[tileRow][tileCol];
          }
        }
      }
    }
    return picture;
  }

  private void orient(List<List<Connection>> grid) {
    for (int row = 0; row < grid.size(); ++row) {
      for (int col = 0; col < grid.get(row).size(); ++col) {
        Connection currentConnection = grid.get(row).get(col);
        int rotations = (currentConnection.connectedToPreviousByIndex + 3) % 4;
        for (int i = 0; i < rotations; ++i) {
          rotate(currentConnection.tile.cells);
        }
        String topBorder = currentConnection.tile.rowToString(0);
        if (row == 0) {
          if (1 != Tile.edgeToId.get(topBorder).size()) {
            vertFlip(currentConnection.tile.cells);
          }
        }
        else if (!topBorder.equals(grid.get(row - 1).get(col).tile.rowToString(9))) {
          vertFlip(currentConnection.tile.cells);
        }
      }
    }
  }

  private void addRow(
      List<List<Connection>> grid,
      Map<Long, Tile> tiles,
      Connection leadConnection
  ) {
    List<Connection> row = new ArrayList<>();
    row.add(leadConnection);
    while (true) {
      Connection next = findConnection(tiles, row.get(row.size() - 1));
      if (next == null) {
        break;
      }
      row.add(next);
    }
    grid.add(row);
  }

  private Connection findSecondRowLeadConnection(Map<Long, Tile> tiles, Connection cornerConnection) {
    int upperEdgeIndex = findIndexOfEdgeBorder(cornerConnection.tile, cornerConnection.connectedToPreviousByIndex);
    Connection downwardCornerConnection = new Connection(cornerConnection.tile, upperEdgeIndex);
    Connection downwardConnectionSecondRowLead = findConnection(tiles, downwardCornerConnection);
    return new Connection(
        downwardConnectionSecondRowLead.tile,
        findIndexOfEdgeBorder(downwardConnectionSecondRowLead.tile, -1)
    );
  }

  private Connection findNextRowLeadConnection(List<List<Connection>> grid, Map<Long, Tile> tiles) {
    Tile oneAbove = grid.get(grid.size() - 1).get(0).tile;
    Connection downwardConnection = new Connection(
        oneAbove,
        findIndexOfSharedBorder(oneAbove, grid.get(grid.size() - 2).get(0).tile)
    );
    Connection rowLeadDownwardConnection = findConnection(tiles, downwardConnection);
    return new Connection(
        rowLeadDownwardConnection.tile,
        findIndexOfEdgeBorder(rowLeadDownwardConnection.tile, -1)
    );
  }

  private int findIndexOfEdgeBorder(Tile tile, int avoidIndex) {
    for (int i = 0; i < 4; ++i) {
      if (i != avoidIndex) {
        String border = tile.borders.get(i).iterator().next();
        if (Tile.edgeToId.get(border).size() == 1) {
          return i;
        }
      }
    }
    throw fail();
  }

  private int findIndexOfSharedBorder(Tile left, Tile right) {
    for (int i = 0; i < 4; ++i) {
      String edge = left.borders.get(i).iterator().next();
      for (Set<String> rightBorder : right.borders) {
        if (rightBorder.contains(edge)) {
          return i;
        }
      }
    }
    throw fail(left + " - " + right);
  }

  private Connection findConnection(Map<Long, Tile> tiles, Connection connectTo) {
    int connectingEdgeIndex = (connectTo.connectedToPreviousByIndex + 2) % 4;
    String connectingEdge = connectTo.tile.borders.get(connectingEdgeIndex).iterator().next();
    for (Tile tile : tiles.values()) {
      if (tile != connectTo.tile) {
        for (int i = 0; i < 4; ++i) {
          if (tile.borders.get(i).contains(connectingEdge)) {
            return new Connection(tile, i);
          }
        }
      }
    }
    return null;
  }

  private long productOfCorners(Set<Connection> cornerConnections) {
    return cornerConnections.stream().mapToLong((cc) -> cc.tile.id).reduce(1, Math::multiplyExact);
  }

  private Set<Connection> findCornerConnections(Map<Long, Tile> tiles) {
    Set<Tile> cornerTiles = new HashSet<>();
    Set<Connection> cornerConnections = new HashSet<>();
    Multimap<Long, String> idsToUnmatchedBorder = HashMultimap.create();
    for (String edge : Tile.edgeToId.keySet()) {
      if (Tile.edgeToId.get(edge).size() == 1) {
        idsToUnmatchedBorder.put(Tile.edgeToId.get(edge).iterator().next(), edge);
      }
    }
    for (long id : idsToUnmatchedBorder.keySet()) {
      if (idsToUnmatchedBorder.get(id).size() == 4) {
        Tile cornerTile = tiles.get(id);
        String edge = idsToUnmatchedBorder.get(id).iterator().next();
        cornerTiles.add(cornerTile);
        cornerConnections.add(new Connection(cornerTile, findIndexOfEdge(cornerTile, edge)));
      }
    }
    return cornerConnections;
  }

  private int findIndexOfEdge(Tile tile, String edge) {
    for (int i = 0; i < 4; ++i) {
      if (tile.borders.get(i).contains(edge)) {
        return i;
      }
    }
    throw new RuntimeException(edge + " !C " + tile.borders);
  }

  private Map<Long, Tile> parseTiles(List<String> lines) {
    Map<Long, Tile> tilesById = new HashMap<>();
    while (!lines.isEmpty()) {
      int blankIndex = lines.indexOf("");
      Tile tile = new Tile(lines.subList(0, blankIndex));
      tilesById.put(tile.id, tile);
      lines = lines.subList(blankIndex + 1, lines.size());
    }
    return tilesById;
  }

  private static void rotate(boolean[][] grid) {
    boolean[][] save = new boolean[grid.length][grid[0].length];
    for (int i = 0; i < save.length; ++i) {
      for (int j = 0; j < save[0].length; ++j) {
        save[i][j] = grid[i][j];
      }
    }
    for (int i = 0; i < save.length; ++i) {
      for (int j = 0; j < save[0].length; ++j) {
        grid[j][grid.length - i - 1] = save[i][j];
      }
    }
  }

  private static void vertFlip(boolean[][] grid) {
    for (int col = 0; col < grid[0].length; ++col) {
      for (int row = 0; row < grid.length / 2; ++row) {
        boolean save = grid[row][col];
        grid[row][col] = grid[grid.length - row - 1][col];
        grid[grid.length - row - 1][col] = save;
      }
    }
  }

  private static class Connection {
    private final Tile tile;
    private final int connectedToPreviousByIndex;

    private Connection(Tile tile, int connectedToPreviousByIndex) {
      this.tile = tile;
      this.connectedToPreviousByIndex = connectedToPreviousByIndex;
    }

    @Override
    public String toString() {
      return "{"
           + tile.id + ", "
           + connectedToPreviousByIndex
           + "}";
    }
  }

  private static class Tile implements HasId<Long> {
    private static Multimap<String, Long> edgeToId = HashMultimap.create(); // any other type of map and it breaks

    private final long id;
    private final List<String> rows;
    private final List<Set<String>> borders = new ArrayList<>();
    private final boolean[][] cells;

    private Tile(List<String> lines) {
      this.id = parseId(lines.get(0));
      this.rows = lines.subList(1, lines.size());
      addBorders(rows.get(0));                // top
      addBorders(column(0));                  // left
      addBorders(rows.get(rows.size() - 1));  // bottom
      addBorders(column(rows.size() - 1));    // right
      borders.stream().flatMap(Set::stream).forEach((border) -> edgeToId.put(border, id));
      this.cells = buildCells();
    }

    @Override
    public Long getId() {
      return id;
    }

    private long parseId(String line) {
      return Long.parseLong(line.substring(line.indexOf(' ') + 1, line.length() - 1));
    }

    private void addBorders(String border) {
      borders.add(Set.of(border, reverse(border)));
    }

    private String column(int index) {
      return rows.stream().map((row) -> Character.toString(row.charAt(index))).collect(Collectors.joining());
    }

    private String reverse(String line) {
      return (new StringBuilder(line)).reverse().toString();
    }

    private boolean[][] buildCells() {
      boolean[][] cells = new boolean[10][10];
      for (int row = 0; row < rows.size(); ++row) {
        for (int col = 0; col < rows.get(0).length(); ++col) {
          cells[row][col] = rows.get(row).charAt(col) == '#';
        }
      }
      return cells;
    }

    private String rowToString(int index) {
      StringBuilder str = new StringBuilder();
      for (int col = 0; col < cells[index].length; ++col) {
        str.append((cells[index][col]) ? '#' : '.');
      }
      return str.toString();
    }
  }
}
