import java.util.ArrayList;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//utilities class
interface Utils {

  //defines the size of the board
  int BOARD_SIZE = 15;

  //defines the number of colors cells can be up to 7
  int NUM_COLORS = 6;

  //defines the size of a cell
  int CELL_SIZE = 50;

  //represents the dimensions of the board
  int BOARD_DIMS = CELL_SIZE * BOARD_SIZE;

  int NUM_MOVES = (int) (Utils.BOARD_SIZE * Utils.NUM_COLORS * 0.7);

  //chooses a random color from the given number of colors up to 7
  static Color randColor(int numColors) {
    int num = new Random().nextInt(numColors);
    if (num == 0) {
      return Color.decode("#0000b3");
    }
    else if (num == 1) {
      return Color.decode("#02b3b9");
    }
    else if (num == 2) {
      return  Color.decode("#0Fb35a");
    }
    else if (num == 3) {
      return Color.decode("#ffd500");
    }
    else if (num == 4) {
      return Color.decode("#ffc0cb");
    }
    else if (num == 5) {
      return Color.decode("#b30Fb3");
    }
    else {
      return Color.PINK;
    }
  }

  //converts the units of this position from pixels to x and y indices
  static Posn screen2BoardCoord(Posn pos, int cellSize) {
    int xIdx = (int) ((double)pos.x / (double)cellSize);
    int yIdx = (int) ((double)pos.y / (double)cellSize);
    return new Posn(xIdx, yIdx);
  }
}


//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  //constructs a cell with no adjacent cells
  Cell(int x, int y, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  //EFFECT: draws this cell onto the game
  void drawCell(WorldScene bg) {
    WorldImage cellIMG = 
        new RectangleImage(Utils.CELL_SIZE, Utils.CELL_SIZE, OutlineMode.SOLID, this.color);
    bg.placeImageXY(cellIMG, this.x, this.y);
  }

  //EFFECT: modifies the fields of this and the other cell to make this cell below the other
  void setBelow(Cell c) {
    c.bottom = this;
    this.top = c;
  }

  //EFFECT: modifies the fields of this and the other cell to make this cell right of the other
  void setRight(Cell c) {
    this.left = c;
    c.right = this;
  }

  //gets the color of this cell
  public Color getColor() {
    return this.color;
  }

  //determines if this cell is flooded
  public boolean isFlooded() {
    return this.flooded;
  }


}

//represents the world of the FloodIt game
class FloodItWorld extends World {
  //All the cells of the game
  ArrayList<ArrayList<Cell>> board;
  ArrayList<Cell> worklist;
  Cell clicked;
  int clickCounter;

  //constructor where a board is passed in
  FloodItWorld(ArrayList<ArrayList<Cell>> board) {
    this.board = board;
    this.worklist = new ArrayList<Cell>();
    this.clicked = null;
    this.clickCounter = 0;

    for (int x = 0; x < Utils.BOARD_SIZE ; x++) {
      ArrayList<Cell> column = board.get(x);

      for (int y = 0; y < Utils.BOARD_SIZE ; y++) {
        if (y > 0) {
          column.get(y).setBelow(column.get(y - 1));
        }
        if (x > 0) {
          column.get(y).setRight(board.get(x - 1).get(y));
        }
      }
    }

  }

  //constructor which generates cells depending on the board size
  FloodItWorld() {
    this.board = new ArrayList<ArrayList<Cell>>();
    this.worklist = new ArrayList<Cell>();
    this.clicked = null;
    this.clickCounter = 0;

    for (int x = 0; x < Utils.BOARD_SIZE ; x++) {

      ArrayList<Cell> column = new ArrayList<>();

      for (int y = 0; y < Utils.BOARD_SIZE ; y++) {

        column.add(new Cell(Utils.CELL_SIZE / 2 + x * Utils.CELL_SIZE,
            Utils.CELL_SIZE / 2 + y * Utils.CELL_SIZE, Utils.randColor(Utils.NUM_COLORS), false));

        if (y > 0) {
          column.get(y).setBelow(column.get(y - 1));
        }

        if (x > 0) {
          column.get(y).setRight(board.get(x - 1).get(y));
        }
      }
      board.add(column);
    }

  }

  //draws all the cells onto the game
  public WorldScene makeScene() {

    WorldScene bg = new WorldScene(Utils.BOARD_DIMS, Utils.BOARD_DIMS + 500);

    for (int x = 0; x < Utils.BOARD_SIZE; x++) {

      ArrayList<Cell> column = this.board.get(x);

      for (int y = 0; y < Utils.BOARD_SIZE; y++) {
        column.get(y).drawCell(bg);
      }
    }

    TextImage text = 
        new TextImage("Remaining Moves: " + Integer.toString(Utils.NUM_MOVES - this.clickCounter),
            25, Color.BLACK);
    bg.placeImageXY(text, Utils.BOARD_DIMS / 2, Utils.BOARD_DIMS + 50);


    return bg;
  }

  //EFFECT: initiates the flood by adding the top left cell to the worklist, changes clicked
  //to the cell that was clicked, and increaes the clickCounter by 1
  public void onMouseClicked(Posn pos) {
    if (gameWon()) {
      this.endOfWorld("You Won!");

    }
    if (this.clickCounter >= Utils.NUM_MOVES) {
      this.endOfWorld("You Lost");
    }

    Posn boardP = Utils.screen2BoardCoord(pos, Utils.CELL_SIZE);

    Cell topLeft = board.get(0).get(0);
    this.clicked = board.get(boardP.x).get(boardP.y);

    System.out.print("Clicked: " + pos.x + " " + pos.y);
    System.out.println(" -->: " + boardP.x + " " + boardP.y + " [" + clicked.color + "]");

    for (int x = 0; x < Utils.BOARD_SIZE; x++) {

      ArrayList<Cell> column = this.board.get(x);

      for (int y = 0; y < Utils.BOARD_SIZE; y++) {
        column.get(y).flooded = false;
      }
    }

    this.worklist.add(topLeft);
    this.clickCounter = this.clickCounter + 1;
    System.out.print("worklist size: " + worklist.size());

  }

  //creates the last scene with a message which says whether the game was won or lost
  public WorldScene lastScene(String msg) {
    WorldScene s = this.makeScene();
    s.placeImageXY(
        new TextImage(msg, 50, Color.BLACK), Utils.BOARD_DIMS / 2, Utils.BOARD_DIMS  / 2);
    return s;
  }

  //EFFECT: floods the next cell in the worklist 
  //and adds adjacent cells to the next which can be flooded to the worklist
  public void onTick() { 

    if (this.worklist.size() > 0) {
      Cell c = this.worklist.get(0);
      Cell l = c.left;
      Cell t = c.top;
      Cell r = c.right;
      Cell b = c.bottom;

      if (l != null) {
        if (!l.isFlooded() && !this.worklist.contains(l) && l.getColor().equals(c.color)) {
          this.worklist.add(l);
        }
      }
      if (t != null) {
        if (!t.isFlooded() && !this.worklist.contains(t) && t.getColor().equals(c.color)) {
          this.worklist.add(t);
        }
      }
      if (r != null) {
        if (!r.isFlooded() && !this.worklist.contains(r) && r.getColor().equals(c.color)) {
          this.worklist.add(r);
        }
      }
      if (b != null) {
        if (!b.isFlooded() && !this.worklist.contains(b) && b.getColor().equals(c.color)) {
          this.worklist.add(b);
        }
      }

      c.flooded = true;
      c.color = this.clicked.color;

      this.worklist.remove(0);
    }
  }
  
  //EFFECT: Resets the board if the button r is pressed.
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      this.board = new FloodItWorld().board;
      this.clickCounter = 0;
    }
  }

  //returns true if all the cells on the board are flooded
  public boolean gameWon() {
    boolean won = true;

    for (int x = 0; x < Utils.BOARD_SIZE; x++) {
      ArrayList<Cell> column = this.board.get(x);
      for (int y = 0; y < Utils.BOARD_SIZE; y++) {
        won = column.get(y).flooded && won;
      }
    }
    return won;
  }
}


class ExamplesCells {

  //represents a sample 4x4 board
  ArrayList<ArrayList<Cell>> board;
  ArrayList<Cell> col0; 
  Cell cell00; 
  Cell cell01;
  Cell cell02;
  Cell cell03;
  ArrayList<Cell> col1;
  Cell cell10;
  Cell cell11;
  Cell cell12;
  Cell cell13; 
  ArrayList<Cell> col2;
  Cell cell20;
  Cell cell21;
  Cell cell22;
  Cell cell23;
  ArrayList<Cell> col3 ;
  Cell cell30;
  Cell cell31;
  Cell cell32;
  Cell cell33;



  void initData() {

    //sample 4x4 board
    board = new ArrayList<ArrayList<Cell
        >>();

    col0 = new ArrayList<Cell>();
    cell00 = new Cell(Utils.CELL_SIZE / 2 + 0 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 0 * Utils.CELL_SIZE,Color.RED,false);
    cell01 = new Cell(Utils.CELL_SIZE / 2 + 0 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 1 * Utils.CELL_SIZE,Color.BLUE,false);
    cell02 = new Cell(Utils.CELL_SIZE / 2 + 0 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 2 * Utils.CELL_SIZE,Color.GREEN,false);
    cell03 = new Cell(Utils.CELL_SIZE / 2 + 0 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 3 * Utils.CELL_SIZE,Color.PINK,false);
    col1 = new ArrayList<Cell>();
    cell10 = new Cell(Utils.CELL_SIZE / 2 + 1 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 0 * Utils.CELL_SIZE,Color.RED,false);
    cell11 = new Cell(Utils.CELL_SIZE / 2 + 1 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 1 * Utils.CELL_SIZE,Color.BLUE,false);
    cell12 = new Cell(Utils.CELL_SIZE / 2 + 1 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 2 * Utils.CELL_SIZE,Color.GREEN,false);
    cell13 = new Cell(Utils.CELL_SIZE / 2 + 1 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 3 * Utils.CELL_SIZE,Color.PINK,false);
    col2 = new ArrayList<Cell>();
    cell20 = new Cell(Utils.CELL_SIZE / 2 + 2 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 0 * Utils.CELL_SIZE,Color.RED,false);
    cell21 = new Cell(Utils.CELL_SIZE / 2 + 2 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 1 * Utils.CELL_SIZE,Color.BLUE,false);
    cell22 = new Cell(Utils.CELL_SIZE / 2 + 2 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 2 * Utils.CELL_SIZE,Color.GREEN,false);
    cell23 = new Cell(Utils.CELL_SIZE / 2 + 2 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 3 * Utils.CELL_SIZE,Color.PINK,false);
    col3 = new ArrayList<Cell>();
    cell30 = new Cell(Utils.CELL_SIZE / 2 + 3 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 0 * Utils.CELL_SIZE,Color.RED,false);
    cell31 = new Cell(Utils.CELL_SIZE / 2 + 3 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 1 * Utils.CELL_SIZE,Color.BLUE,false);
    cell32 = new Cell(Utils.CELL_SIZE / 2 + 3 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 2 * Utils.CELL_SIZE,Color.GREEN,false);
    cell33 = new Cell(Utils.CELL_SIZE / 2 + 3 * Utils.CELL_SIZE,Utils.CELL_SIZE 
        / 2 + 3 * Utils.CELL_SIZE,Color.PINK,false);

    col0.add(cell00);
    col0.add(cell01);
    col0.add(cell02);
    col0.add(cell03);

    col1.add(cell10);
    col1.add(cell11);
    col1.add(cell12);
    col1.add(cell13);

    col2.add(cell20);
    col2.add(cell21);
    col2.add(cell22);
    col2.add(cell23);

    col3.add(cell30);
    col3.add(cell31);
    col3.add(cell32);
    col3.add(cell33);

    board.add(col0);
    board.add(col1);
    board.add(col2);
    board.add(col3);

  }

  void testFloodIt(Tester t) {
    initData();
    //FloodItWorld w = new FloodItWorld(board);
    //w.bigBang(Utils.BOARD_DIMS, Utils.BOARD_DIMS, 0.05);
  }

  void testFloodIt2(Tester t) {
    FloodItWorld w = new FloodItWorld();
    System.out.print(w.board.get(0).get(0).right);
    w.bigBang(Utils.BOARD_DIMS, Utils.BOARD_DIMS + 100, 0.01);
  }


  void drawCell(Tester t) {
    initData();
    WorldScene bg = new WorldScene(Utils.BOARD_DIMS, Utils.BOARD_DIMS);
    t.checkExpect(bg, new WorldScene(Utils.BOARD_DIMS, Utils.BOARD_DIMS));
    cell00.drawCell(bg);
    //t.checkExpect(bg, new WorldScene(Utils.BOARD_DIMS, Utils.BOARD_DIMS).placeImageXY(
    //new RectangleImage(Utils.CELL_SIZE, Utils.CELL_SIZE, OutlineMode.SOLID, Color.RED), 5, 5));

  }

  void testSetBelow(Tester t) {
    initData();
    t.checkExpect(cell00.bottom, null);
    t.checkExpect(cell32.top, null);
    cell32.setBelow(cell00);
    t.checkExpect(cell00.bottom, cell32);
    t.checkExpect(cell32.top, cell00);    

  }

  void testSetRight(Tester t) {
    initData();
    t.checkExpect(cell00.right, null);
    t.checkExpect(cell01.left, null);
    cell01.setRight(cell00);
    t.checkExpect(cell00.right, cell01);
    t.checkExpect(cell01.left, cell00);
  }

  void testIsFlooded(Tester t) {
    initData();
    t.checkExpect(cell00.isFlooded(), false);
    this.cell00.flooded = true;
    t.checkExpect(cell00.isFlooded(), true);
  }

  void testGetColor(Tester t) {
    initData();
    t.checkExpect(cell01.getColor(), Color.BLUE);
    t.checkExpect(cell02.getColor(), Color.GREEN);
    t.checkExpect(cell03.getColor(), Color.PINK);
  }
  
  //FloodItWorld methods were tested using the pre-built board above in the
  //FloodIt test that was commented out

}