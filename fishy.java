import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

//represents a fish in the game Fishy!
interface IFish {
  //WorldImage drawFish(); // you comment this out

  //determines if this fish is at the right boundary of the screen
  boolean fishAtRightEdge(); 

  //determines if this fish is at the left boundary of the screen
  boolean fishAtLeftEdge();

  int PLAYER_SPEED = 30;

}

//abstract class for IFish
abstract class AFish implements IFish {
  int x;
  int y;
  int size;
  Color color;
  boolean dirRight;

  //constructor
  AFish(int x, int y, int size, Color color, boolean dirRight) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.color = color;
    this.dirRight = dirRight;
  }

  /* FIELDS
   *  this.x ... int
   *  this.y ... int
   *  this.size ... int
   *  this.color ... Color
   *  this.dirRight ... boolean
   * 
   * METHODS
   *  drawFish() ... WorldImage
   *  fishAtRightEdge() ... boolean
   *  fishAtLeftEdge() ... boolean
   */

  //draws this fish
  WorldImage drawFish() { //ben
    if (this.dirRight == true) {
      return  new OverlayOffsetImage(
          new CircleImage(this.size, "solid" , this.color), - this.size,0,
          new RotateImage(new EquilateralTriangleImage(this.size + this.size / 2, 
              OutlineMode.SOLID, this.color), 330));
    }
    else {
      return new OverlayOffsetImage( new CircleImage(this.size,"solid", this.color),this.size,0,
          new RotateImage(new EquilateralTriangleImage(this.size + this.size / 2,
              OutlineMode.SOLID, this.color), 30));
    }



  }

  //determines if this fish is at the right boundary of the screen
  public boolean fishAtRightEdge() { // has to be named public
    return this.x > 1000 && this.dirRight == true;
  }

  //determines if this fish is at the left boundary of the screen
  public boolean fishAtLeftEdge() { // has to be named public
    return this.x < 0 && this.dirRight == false;
  }

  public int randomInt(int n) {
    return -n + (new Random().nextInt(2 * n + 1));
  }
}

//represents the fish controlled by the player
class Player extends AFish {

  //constructor
  Player(int x, int y, int size, Color color, boolean dirRight) {
    super(x, y, size, color, dirRight);
  }

  /* FIELDS
   *  this.x ... int
   *  this.y ... int
   *  this.size ... int
   *  this.color ... Color
   * 
   * METHODS
   *  movePlayer(Key) ... Player
   *  loopRight() ... Player
   *  isBigger(BgFish) ... boolean
   *  distBetween(BgFish) ... int
   *  collision(BgFish) ... boolean
   *  eaten(BgFish) ... 
   *  eatBgFish(BgFish) ... Player
   *  growPlayer(ILoBgFish) ... Player 
   */

  //moves this player using the arrow keys
  public Player movePlayer(String key) { // ben
    if (key.equals("right")) {
      return new Player(this.x + PLAYER_SPEED, this.y, this.size, this.color, true);
    } else if (key.equals("left")) {
      return new Player(this.x - PLAYER_SPEED, this.y, this.size, this.color, false);
    } else if (key.equals("up")) {
      return new Player(this.x, this.y - PLAYER_SPEED, this.size, this.color, this.dirRight);
    } else if (key.equals("down")) {
      return new Player(this.x, this.y + PLAYER_SPEED, this.size, this.color, this.dirRight);
    }
    else 
      return this;
  }

  //loops this player around to the right edge if it exits on the left boundary of the screen
  public Player loopRight() {
    if (this.fishAtLeftEdge()) {
      return new Player(1000, this.y, this.size, this.color, this.dirRight);
    }
    else {
      return this;
    }
  }

  //loops this player around to the left edge if it exits on the right boundary of the screen
  public Player loopLeft() {
    if (this.fishAtRightEdge()) {
      return new Player(0, this.y, this.size, this.color, this.dirRight);
    }
    else {
      return this;
    }
  }
  public int randomInt(int n) {
    return -n + (new Random().nextInt(2 * n + 1));
  }

  //determines if this player is bigger than that background fish
  public boolean isBigger(BgFish bgf) {
    return this.size > bgf.size;
  }

  //determines the distnace between this player and that background fish
  public int distBetween(BgFish bgf) {
    return (int) Math.floor(Math.sqrt (Math.pow((this.x - bgf.x), 2) + Math.pow((this.y - bgf.y), 2)));
  }

  //determines if there is a collision between this player and that background fish
  public boolean collision(BgFish bgf) {
    return this.size > this.distBetween(bgf)
        || bgf.size > this.distBetween(bgf);
  }

  //determines if this player was eaten by that background fish
  public boolean eaten(BgFish bgf) {
    return this.collision(bgf) && !this.isBigger(bgf);
  }

  //determines if this player eats that background fish
  public boolean eatBgFish(BgFish bgf) {
    return this.collision(bgf) && this.isBigger(bgf);
  }

  //increases the size of this player when it eats a background fish
  public Player growPlayer(ILoBgFish lobgf) {
    return lobgf.growPlayerHelp(this);
  }
}

//represents a fish in the background of the game
class BgFish extends AFish {
  int speed;

  //constructor
  BgFish(int x, int y, int size, Color color, int speed, boolean dirRight) {
    super(x, y, size, color, dirRight);
    this.speed = speed;
  }

  /* FIELDS
   *  this.x ... int
   *  this.y ... int
   *  this.size ... int
   *  this.color ... Color
   *  this.speed ... int
   * 
   * METHODS
   *  WorldImage drawFish()
   *  WorldImage moveBgFish
   */

  //moves a single background fish across the screen
  public BgFish moveBgFish() {
    return new BgFish(this.x + this.speed, this.y, this.size, this.color, this.speed, this.dirRight);
  }


}

//represents the set of all fish currently in the game
interface ILoBgFish  {

  //draws all the fish in this list of background fish onto the game
  WorldScene drawAllBgFish(WorldScene below);

  //moves this entire set of backfround fish across the screen
  ILoBgFish moveAllBgFish();

  //removes all fish that have reached the edge of the screen from this set of fish
  ILoBgFish remove();

  //determines if the player has been eaten by any fish in this list of background fish
  boolean eatenByAny(Player p);

  //increases the size of that player if it eats any of the fish in this list of background fish
  Player growPlayerHelp(Player p);

  //removes any fish that have been eaten by that player from this list of background fish
  ILoBgFish removeEaten(Player p, ILoBgFish acc);

  //adds a new fish to this list of background fish based on a random number
  ILoBgFish randomSpawnTime();

  //randomizes the attributes of fish that will be added to this list of background fish
  ILoBgFish randomSpawn(int x);

  //determines if the player is bigger than all the fish in this list of background fish
  boolean biggerThanAll(Player p);

}

//represents an empty set of fish
class MtLoFish implements ILoBgFish {

  /* FIELDS
   * 
   * METHODS
   *  drawAllBgFish() ... WorldScene
   *  moveAllBgFish() ... ILoBgFish
   *  remove() ... ILoBgFish
   *  eatenByAny(Player) ... boolean
   *  growPlayerHelp(Player) ... Player
   *  removeEaten(Player, ILoBgFish) ... ILoBgFish
   *  randomSpawnTime() ... ConsLoFish
   *  randomSpawn(int) ... int
   *  biggerThanAll(Player) ... boolean
   */

  //draws all the fish in this empty list of background fish onto the game
  public WorldScene drawAllBgFish(WorldScene below) {
    return below;
  }

  //moves this empty set of backfround fish across the screen
  public ILoBgFish moveAllBgFish() {
    return this;
  }

  //removes all fish that have reached the edge of the screen from this empty set of fish
  public ILoBgFish remove() {
    return this;
  }

  //determines if the player has been eaten by any fish in this empty list of background fish
  public boolean eatenByAny(Player p) {
    return false;
  }

  //increases the size of that player if it eats any of the fish in this empty list of background fish
  public Player growPlayerHelp(Player p) {
    return p;
  }

  //removes any fish that have been eaten by that player from this empty list of background fish
  public ILoBgFish removeEaten(Player p, ILoBgFish acc) {
    return acc;
  }

  //adds a new fish to this empty list of background fish based on a random number
  public ILoBgFish randomSpawnTime() {
    return this;
  }

  //randomizes the attributes of fish that will be added to this empty list of background fish
  public ILoBgFish randomSpawn(int x) {
    return this;
  }

  //determines if the player is bigger than all the fish in this empty list of background fish
  public boolean biggerThanAll(Player p) {
    return true;
  }
}

//represents a non-empty set of fish
class ConsLoFish implements ILoBgFish {
  BgFish first;
  ILoBgFish rest;

  //constructor
  ConsLoFish(BgFish first, ILoBgFish rest) {
    this.first = first;
    this.rest = rest;
  }

  /* FIELDS
   *  this.first ... Fish
   *  this.rest ... ILoFish
   * 
   * METHODS
   *  drawAllBgFish() ... WorldScene
   *  moveAllBgFish() ... ILoBgFish
   *  remove() ... ILoBgFish
   *  eatenByAny(Player) ... boolean
   *  growPlayerHelp(Player) ... Player
   *  removeEaten(Player, ILoBgFish) ... ILoBgFish
   *  randomSpawnTime() ... ConsLoFish
   *  randomSpawn(int) ... int
   *  biggerThanAll(Player) ... boolean
   * 
   * METHODS FOR FIELDS
   *  this.first.drawFish() ... WorldImage
   *  this.first.fishAtRightEdge() ... boolean
   *  this.first.fishAtLeftEdge() ... boolean
   *  this.first.movePlayer(Key) ... Player
   *  this.first.loopRight() ... Player
   *  this.first.isBigger(BgFish) ... boolean
   *  this.first.distBetween(BgFish) ... int
   *  this.first.collision(BgFish) ... boolean
   *  this.first.eaten(BgFish) ... 
   *  this.first.eatBgFish(BgFish) ... Player
   *  this.first.growPlayer(ILoBgFish) ... Player 
   * 
   *  this.rest.drawAllBgFish() ... WorldScene
   *  this.rest.moveAllBgFish() ... ILoBgFish
   *  this.rest.remove() ... ILoBgFish
   *  this.rest.eatenByAny(Player) ... boolean
   *  this.rest.growPlayerHelp(Player) ... Player
   *  this.rest.removeEaten(Player, ILoBgFish) ... ILoBgFish
   *  this.rest.randomSpawnTime() ... ConsLoFish
   *  this.rest.randomSpawn(int) ... int
   *  this.rest.biggerThanAll(Player) ... boolean
   * 
   */

  //draws all the fish in this non-empty list of background fish onto the game
  public WorldScene drawAllBgFish(WorldScene below) {
    return this.rest.drawAllBgFish(below.placeImageXY(this.first.drawFish(),this.first.x,this.first.y));
  }

  //moves this non-empty set of backfround fish across the screen
  public ILoBgFish moveAllBgFish() {
    return new ConsLoFish(this.first.moveBgFish(), this.rest.moveAllBgFish());
  }

  //removes all fish that have reached the edge of the screen from this non-empty set of fish
  public ILoBgFish remove() {
    if (this.first.fishAtRightEdge() || this.first.fishAtLeftEdge()) {
      return this.rest.remove();
    }
    else {
      return new ConsLoFish(this.first, this.rest.remove());
    }
  }

  //determines if the player has been eaten by any fish in this non-empty list of background fish
  public boolean eatenByAny(Player p) {
    return p.eaten(this.first) || this.rest.eatenByAny(p);
  }

  //increases the size of that player if it eats any of the fish in this non-empty list of background fish
  public Player growPlayerHelp(Player p) {
    if (p.eatBgFish(this.first)) {
      return new Player(p.x, p.y, p.size + (int) (Math.floor(this.first.size / 5)), p.color, p.dirRight);
    }
    else {
      return this.rest.growPlayerHelp(p);
    }
  }

  //removes any fish that have been eaten by that player from this non-empty list of background fish
  public ILoBgFish removeEaten(Player p, ILoBgFish acc) {
    if (p.eatBgFish(this.first)) {
      return this.rest.removeEaten(p, acc);
    }
    else {
      return this.rest.removeEaten(p, new ConsLoFish(this.first, acc));
    }
  }

  //adds a new fish to this non-empty list of background fish based on a random number
  public ConsLoFish randomSpawnTime() {
    if (new Random().nextInt(100) > 90) {
      return this.randomSpawn(500); 
    }
    else 
      return this;
  }

  //randomizes the attributes of fish that will be added to this non-empty list of background fish
  public ConsLoFish randomSpawn(int x) {
    return new ConsLoFish(randomHelper(x),this);
  }

  //randomizes the side and direction of the fish that will be added to this non-empty list of bg fish
  public BgFish randomHelper(int x) {
    if (new Random().nextInt(2 * x + 1 ) % 2 == 0) {
      return new BgFish(0,randomY(x),randomSize(x),
          new Color(randomY(x+10)%255,randomY(x) % 255,randomY(x + 10) % 255),randomSpeed(x),true);
    }
    else {
      return new BgFish(1000,randomY(x),randomSize(x), 
          new Color(randomY( x + 10) % 255,randomY(x) % 255,randomY(x + 10)%255), - randomSpeed(x),false);
    }   
  }

  //randomizes the y value of the fish that will be added to this non-empty list of bg fish
  public int randomY(int x) {
    return new Random().nextInt(2 * x + 1);
  }

  //randomizes the speed of the fish that will be added to this non-empty list of bg fish
  public int randomSpeed(int x) {
    return (1 + (new Random().nextInt(15)));
  }

  //randomizes the size of the fish that will be added to this non-empty list of bg fish
  public int randomSize(int x) {
    return (1 + new Random().nextInt(100));
  }

  //determines if the player is bigger than all the fish in this non-empty list of background fish
  public boolean biggerThanAll(Player p) {
    return p.isBigger(this.first) && this.rest.biggerThanAll(p);
  }
}

//represents the world of the Fishy! game
class FishyGame extends World {
  int width = 1000;
  int height = 1000;
  Player p;
  ILoBgFish bgf;

  //constructor
  FishyGame(Player p, ILoBgFish bgf) {
    super();
    this.p = p;
    this.bgf = bgf;
  }

  //On tick detemines if the game has ended or handes bg fish movement and removal, 
  //and checks if the player has eaten any bg fish
  public World onTick() {
    if (this.bgf.eatenByAny(p)) {
      return this.endOfWorld("You Were Eaten");
    }
    if (this.bgf.biggerThanAll(p)) {
      return this.endOfWorld("You Win!");
    } 
    else {
      return new FishyGame(this.p.growPlayer(this.bgf),
          this.bgf.remove().moveAllBgFish().randomSpawnTime().removeEaten(this.p,new MtLoFish()));
    }
  }

  //Moves the player around the screen using the arrow keys
  public World onKeyEvent(String key) {
    return new FishyGame(this.p.movePlayer(key).loopLeft().loopRight(),this.bgf);
  }

  //represents the background of the scren
  public WorldImage background = new RectangleImage(this.width,
      this.height, OutlineMode.SOLID, Color.decode("#496ac1"));

  //draws the player, bg fish, and the game's background onto the game
  public WorldScene makeScene() {//ben
    return 
        this.bgf.drawAllBgFish(this.getEmptyScene()
            .placeImageXY(this.background, this.width / 2, this.height / 2))
        .placeImageXY(this.p.drawFish() , this.p.x, this.p.y);
  }

  // produce the last image of this world by adding text to the image 
  public WorldScene lastScene(String s) {
    return this.makeScene().placeImageXY(new TextImage(s, Color.red), width / 2,
        height / 2);
  }

}

//examples and tests
class ExamplesFishy {

  int PLAYER_SPEED = 30;
  //examples of data for the Fish class:
  //(int x, int y, int size, Color color)
  Player p1 = new Player(100, 100, 40, Color.RED, true);
  Player p1left = new Player(100 - PLAYER_SPEED, 100, 40, Color.RED, false);
  Player p1right = new Player(100 + PLAYER_SPEED, 100, 40, Color.RED, true);
  Player p1up = new Player(100, 100 - PLAYER_SPEED, 40, Color.RED, true);
  Player p1down = new Player(100, 100 + PLAYER_SPEED, 40, Color.RED, true);

  Player p1OBRight = new Player(1005, 100, 50, Color.RED, true);
  Player p1OBLeft = new Player(-5, 100, 50, Color.RED, false);

  Player p2 = new Player(250, 700, 30, Color.CYAN, true);

  Player pBig = new Player(10, 7, 80, Color.WHITE, true);

  //examples of data for the bgFish Class
  //(int x, int y, int size, Color color, int speed, boolean dirRight)
  BgFish bgf1 = new BgFish(0, 100, 20, Color.BLUE, 10, true); // fish starting on left, moving right
  BgFish bgf2 = new BgFish(1000, 250, 50, Color.PINK, -10, false); // fish starting on right, moving left
  BgFish bgf3 = new BgFish(0, 500, 50, Color.GREEN, 15, true);

  BgFish bgfOBRight = new BgFish(1001, 500, 40, Color.GREEN, 10, true);
  BgFish bgfOBLeft = new BgFish(-10, 500, 30, Color.GREEN, 10, false);

  BgFish bgfEatP2 = new BgFish(245, 710, 45, Color.GRAY, 15, false);
  BgFish bgfEatenByP2 = new BgFish(255, 695, 29, Color.BLACK, 8, true);

  //examples of data for the ILoBgFish Class
  //(int x, int y, int size, Color color, int speed, boolean dirRight)
  ILoBgFish LbgF1 = new MtLoFish();
  ILoBgFish LbgF2 = new ConsLoFish(bgf3, new MtLoFish());
  ILoBgFish LbgF3 = new ConsLoFish(bgf2, LbgF2);
  ILoBgFish LbgF4 = new ConsLoFish(bgf1, LbgF3);
  ILoBgFish LbgFGrow = new ConsLoFish(bgfEatenByP2, this.LbgF4);
  ILoBgFish LbgFRemove = new ConsLoFish(this.bgfOBRight, this.LbgF4);
  ILoBgFish LbgFEatPlayer = new ConsLoFish(this.bgfEatP2, this.LbgF4);
  ILoBgFish LbgFEaten = new ConsLoFish(this.bgfEatenByP2, this.LbgF4);


  // examples of data for the FishyGame class
  FishyGame FG1s = new FishyGame(this.p1, this.LbgF1);
  FishyGame FG1w = new FishyGame(this.p1, this.LbgF4);
  FishyGame FG1leftw = new FishyGame(this.p1left, this.LbgF4);
  FishyGame FG1rightw = new FishyGame(this.p1right, this.LbgF4);
  FishyGame FG1upw = new FishyGame(this.p1up, this.LbgF4);
  FishyGame FG1downw = new FishyGame(this.p1down, this.LbgF4);

  boolean testFishyGame(Tester t) {
    // run the game
    //int x, int y, int size, Color color
    FishyGame w = new FishyGame(this.p1,LbgF4);
    return w.bigBang(1000, 1000, .15);
  }

  boolean testFishAtRightEdge(Tester t) {
    return t.checkExpect(this.p1.fishAtRightEdge(), false)
        && t.checkExpect(this.p1down.fishAtRightEdge(), false)
        && t.checkExpect(this.p1OBRight.fishAtRightEdge(), true)
        && t.checkExpect(this.bgfOBRight.fishAtRightEdge(), true);
  }

  boolean testFishAtLeftEdge(Tester t) {
    return t.checkExpect(this.p1.fishAtLeftEdge(), false)
        && t.checkExpect(this.p1up.fishAtLeftEdge(), false)
        && t.checkExpect(this.p1OBLeft.fishAtLeftEdge(), true)
        && t.checkExpect(this.bgfOBLeft.fishAtLeftEdge(), true);
  }

  boolean testMovePlayer(Tester t) {
    return t.checkExpect(this.p1.movePlayer("left"), this.p1left)
        && t.checkExpect(this.p1.movePlayer("right"), this.p1right)
        && t.checkExpect(this.p1.movePlayer("up"), this.p1up)
        && t.checkExpect(this.p1.movePlayer("down"), this.p1down)
        && t.checkExpect(this.p1.movePlayer("q"), this.p1);

  }

  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.FG1w.onKeyEvent("left"), this.FG1leftw)
        && t.checkExpect(this.FG1w.onKeyEvent("right"), this.FG1rightw)
        && t.checkExpect(this.FG1w.onKeyEvent("down"), this.FG1downw)
        && t.checkExpect(this.FG1w.onKeyEvent("up"), this.FG1upw)
        && t.checkExpect(this.FG1w.onKeyEvent("q"), this.FG1w); 
  }

  boolean testDrawFish(Tester t) {
    return t.checkExpect(this.p1.drawFish(), new OverlayOffsetImage(
        new CircleImage(40, "solid", Color.RED), -40, 0,
        new RotateImage(new EquilateralTriangleImage(40 + 40 / 2, 
            OutlineMode.SOLID, Color.RED), 330)))
        && t.checkExpect(this.bgf2.drawFish(), new OverlayOffsetImage(
            new CircleImage(50,"solid", Color.PINK),50,0,
            new RotateImage(new EquilateralTriangleImage(50+50/2, 
                OutlineMode.SOLID, Color.PINK), 30)));
  }

  boolean testLoopRight(Tester t) {
    return t.checkExpect(this.p1.loopRight(), this.p1)
        && t.checkExpect(new Player(-1, 467, 30, Color.GREEN, false).loopRight(), 
            new Player(1000, 467, 30, Color.GREEN, false));

  }

  boolean testLoopLeft(Tester t) {
    return t.checkExpect(this.p1.loopLeft(), this.p1)
        && t.checkExpect(new Player(1001, 690, 32, Color.BLUE, true).loopLeft(), 
            new Player(0, 690, 32, Color.BLUE, true));
  }

  boolean testIsBigger(Tester t) {
    return t.checkExpect(this.p1.isBigger(bgf1), true)
        && t.checkExpect(this.p1down.isBigger(bgf2), false);
  }

  boolean testDistBetween(Tester t) {
    return t.checkExpect(this.p1.distBetween(this.bgf1), 100)
        && t.checkExpect(this.p1.distBetween(this.bgf2), 912)
        && t.checkExpect(this.p1down.distBetween(this.bgfOBLeft), 395);
  }

  boolean testCollision(Tester t) {
    return t.checkExpect(this.p1.collision(this.bgf1), false)
        && t.checkExpect(this.p2.collision(this.bgfEatP2), true)
        && t.checkExpect(this.p2.collision(this.bgfEatenByP2), true);
  }

  boolean testEaten(Tester t) {
    return t.checkExpect(this.p1.eaten(bgf1), false)
        && t.checkExpect(this.p2.eaten(bgfEatP2), true)
        && t.checkExpect(this.p2.eaten(bgfEatenByP2), false);
  }

  boolean testEatBgFish(Tester t) {
    return t.checkExpect(this.p1.eatBgFish(bgf2), false)
        && t.checkExpect(this.p2.eatBgFish(bgfEatP2), false)
        && t.checkExpect(this.p2.eatBgFish(bgfEatenByP2), true);
  }

  boolean testGrowPlayer(Tester t) {
    return t.checkExpect(this.p1.growPlayer(this.LbgF4), this.p1)
        && t.checkExpect(this.p2.growPlayer(this.LbgFGrow), new Player(250, 700, 35, Color.CYAN, true));
  }

  boolean testGrowPlayerHelp(Tester t) {
    return t.checkExpect(this.LbgF4.growPlayerHelp(this.p1), this.p1)
        && t.checkExpect(this.LbgFGrow.growPlayerHelp(this.p2), new Player(250, 700, 35, Color.CYAN, true));
  }

  boolean testMoveBgFish(Tester t) {
    return t.checkExpect(this.bgf1.moveBgFish(), new BgFish(10, 100, 20, Color.BLUE, 10, true))
        && t.checkExpect(this.bgf2.moveBgFish(), new BgFish(990, 250, 50, Color.PINK, -10, false))
        && t.checkExpect(this.bgf3.moveBgFish(), new BgFish(15, 500, 50, Color.GREEN, 15, true));
  }

  boolean testOnTick1(Tester t) {
    boolean result = true;
    for (int i = 0; i < 20; i++) {
      FishyGame bwf = (FishyGame) this.FG1w.onTick();
      result = result && t.checkRange(bwf.p.x,100, 105);
    }
    return result;
  }
/*
  boolean testmakeScene(Tester t) {
    return t.checkExpect(FG1s.makeScene(), new FishyGame(this.p1, this.LbgF1).makeScene());
  }

  boolean testDrawAllBgFish(Tester t) {
    return t.checkExpect(this.LbgF1.drawAllBgFish(new WorldScene(1000, 1000)), new WorldScene(1000, 1000))
        && t.checkExpect(this.LbgF2.drawAllBgFish(new WorldScene(1000, 1000)),
            new WorldScene(1000, 1000).placeImageXY(this.bgf3.drawFish(),0,500));
  }

  boolean testMoveAllBgFish(Tester t) {
    return t.checkExpect(this.LbgF1.moveAllBgFish(), this.LbgF1)
        && t.checkExpect(this.LbgF2.moveAllBgFish(), new ConsLoFish(bgf3.moveBgFish(), this.LbgF1));
  }

  boolean testRemove(Tester t) {
    return t.checkExpect(this.LbgF4.remove(), this.LbgF4)
        && t.checkExpect(this.LbgFRemove.remove(), this.LbgF4)
        && t.checkExpect(this.LbgF1.remove(), this.LbgF1);
  }

  boolean testEatenByAny(Tester t) {
    return t.checkExpect(this.LbgF1.eatenByAny(this.p1), false)
        && t.checkExpect(this.LbgFEatPlayer.eatenByAny(this.p2), false)
        && t.checkExpect(this.LbgFEaten.eatenByAny(this.p2), true);
  }

  boolean testRemoveEaten(Tester t) {
    return t.checkExpect(this.LbgF1.removeEaten(this.p1, this.LbgF4), this.LbgF4)
        && t.checkExpect(this.LbgFEaten.removeEaten(this.p2, this.LbgF4), this.LbgF4);
  }

  boolean testRandomHeight(Tester t) {
    return t.checkOneOf("test randomHeight", this.p1.randomInt(5),-5,-4,-3, -2, -1,
        0, 1, 2, 3, 4, 5)
        && t.checkNoneOf("test randomHeight", this.p1.randomInt(5), -8,
            -6, 7, 9);
  }

  boolean testBiggerThanAll(Tester t) {
    return t.checkExpect(this.LbgF1.biggerThanAll(this.p1), true)
        && t.checkExpect(this.LbgF4.biggerThanAll(this.p2), false)
        && t.checkExpect(this.LbgF4.biggerThanAll(this.pBig), true);
  }

  boolean testRandomSpeed(Tester t) {
    return t.checkOneOf("test randomSpeed", this.p1.randomInt(10), -10,-9,-8,-7,-6,-5,-4,-3, -2, -1,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        && t.checkNoneOf("test randomSpeed", this.p1.randomInt(3), -5,
            -4, 4, 5);
            */
  //}


}
