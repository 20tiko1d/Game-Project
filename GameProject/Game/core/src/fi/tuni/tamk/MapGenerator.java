package fi.tuni.tamk;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * The class produces a random labyrinth.
 *
 * The labyrinth has a small square in the center. There is only one way out of the labyrinth.
 */
public class MapGenerator {

    private Texture[][] map;
    private int [][][] generatingMap;
    private int preferredLength;

    private int[][] middle;
    private int[][] path1; // 1st is start of path1 and the last is entry to the middle.
    private int[][] path2; // 1st is exit and the last is entry in the middle
    private int[][] randomPath;
    private int randomCounter = 0;
    private boolean pathDone;

    private int size;

    private int exitRow;
    private int exitColumn;

    private int path1Min;

    private boolean pathClear = true;
    private boolean pathDead = false;

    private boolean firstPath;

    private Main main;
    private World world;

    private float oneWidth;
    private float windowWidth;
    private float windowHeight;

    private GameScreen gameScreen;
    private LevelScreen levelScreen;

    public MapGenerator(Main main, GameScreen gameScreen) {
        this.main = main;
        this.gameScreen = gameScreen;
    }

    /**
     * The method controls all of the stages to create a random labyrinth.
     *
     * @param size: Length of the labyrinth sides.
     * @param preferredLength: Length of the route from the start to the center square.
     * @param world: Contains all of the collision boxes.
     * @param oneWidth: Width of the one square on the gaming device.
     * @param windowWidth: Width of the virtual world in meters.
     * @param windowHeight: Height of the virtual world in meters.
     * @param numOfPairs: Number of pairs.
     * @param levelScreen: LevelScreen object for information transfer.
     * @return Returns created map in texture-array form.
     */
    public void createMap(int size, int preferredLength, World world, float oneWidth,
                                  float windowWidth, float windowHeight,
                                  int numOfPairs, LevelScreen levelScreen) {
        this.size = size;
        int arraySize = FileReader.getPairElements().size;
        middle = new int[4][2];
        generatingMap = new int [size][size][4];
        this.preferredLength = preferredLength;
        this.world = world;
        this.oneWidth = oneWidth;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.levelScreen = levelScreen;

        createMiddle();
        createPath(true);
        createPath(false);
        createGeneratingMap1();
        createRandom();
        putTextures();
        pathDone = false;
        gameScreen.setStart(path1[0][1] * 4 + 27, path1[0][0] * 4 + 50);
        createRandomPairs(numOfPairs, arraySize);
        disposeAll();
        gameScreen.setMap(map);
    }

    public void disposeAll() {
        generatingMap = null;
        path1 = null;
        path2 = null;
        randomPath = null;
        middle = null;
    }

    /**
     * Method creates the center square.
     */
    public void createMiddle() {
        int x = size / 2 - 1;
        int y = size / 2 - 1;
        if(size % 2 != 0) {
            x += MathUtils.random(0, 1);
            y += MathUtils.random(0, 1);
        }
        for(int i = 0; i < middle.length; i++) {
            if(i == 1 || i == 3) { x++;}
            if(i == 2) {
                x--;
                y++;
            }
            middle[i][0] = y;
            middle[i][1] = x;
        }
    }

    /**
     * Method creates the path from the start to the center and from the center to the exit.
     *
     * @param firstPath: Tells which one of the paths have to be made.
     */
    public void createPath(boolean firstPath) {
        this.firstPath = firstPath;
        int[][] path;
        int startRow;
        int startColumn;
        int middlePoint;
        if(firstPath) {
            startRow = size - 1;
            startColumn = MathUtils.random(2, size - 3);
            middlePoint = MathUtils.random(2, 3);

        } else {
            createExit();
            middlePoint = MathUtils.random(0, 1);
            startRow = exitRow;
            startColumn = exitColumn;
        }
        int distance = Math.abs(startRow - middle[middlePoint][0]) +
                Math.abs(startColumn - middle[middlePoint][1]);

        int pathLength = preferredLength;
        if(distance % 2 != 0) {
            pathLength++;
        }
        path = new int[pathLength][2];
        path[0][0] = startRow;
        path[0][1] = startColumn;
        if(firstPath) {
            path[1][0] = startRow - 1;
            path[1][1] = startColumn;
        }

        path[pathLength - 1][0] = middle[middlePoint][0];
        path[pathLength - 1][1] = middle[middlePoint][1];


        if(MathUtils.random(0, 1) == 0) {
            int plus = 1;
            if(middlePoint == 2 || middlePoint == 0) {
                plus = -1;
            }
            path[pathLength - 2][0] = middle[middlePoint][0];
            path[pathLength - 2][1] = middle[middlePoint][1] + plus;
        } else {
            int plus = 1;
            if(!firstPath) {
                plus = - 1;
            }
            path[pathLength - 2][0] = middle[middlePoint][0] + plus;
            path[pathLength - 2][1] = middle[middlePoint][1];
        }
        path1Min = size / 2 -1;

        while(true) {
            path = clearPath(path);
            int length = 0;
            if(firstPath) {
                length = 1;
            }
            for(int i = length; i <= path.length - 4 && !pathDead; i++) {

                int[] direction = {1, 1, 1, 1};
                int endRow = path[path.length - 2][0];
                int endColumn = path[path.length - 2][1];
                boolean shortest = checkLength(path[i][0], endRow,
                        path[i][1], endColumn, path.length - 2 - i);

                if(shortest) {

                    if(endRow - path[i][0] < 0) {
                        direction[3] = 0;
                    }
                    else if(endRow - path[i][0] > 0) {
                        direction[1] = 0;
                    }
                    if(endColumn - path[i][1] < 0) {
                        direction[2] = 0;
                    }
                    else if(endColumn - path[i][1] > 0) {
                        direction[0] = 0;
                    }
                    if(endRow == path[i][0]) {
                        direction[0] = 0;
                        direction[2] = 0;
                    }
                    else if(endColumn == path[i][1]) {
                        direction[1] = 0;
                        direction[3] = 0;
                    }
                }
                while(!pathDead) {
                    int random = randomIndex(direction);
                    if(random == -1) {break;}
                    path = makePath(path, path[i][0], path[i][1], i + 1, random);
                    if(!pathClear) {
                        direction[random] = 0;
                        pathClear = true;
                    } else {
                        break;
                    }
                }
            }
            int endLength = path.length;
            boolean clearCheck = checkLength(path[endLength - 2][0], path[endLength - 3][0],
                    path[endLength - 2][1], path[endLength - 3][1], 1);

            if(pathDead || !clearCheck) {
                pathDead = false;
            } else {
                if(firstPath) {
                    path1 = path;
                } else {
                    path2 = path;
                }
                break;
            }
        }
    }

    public int randomIndex(int[] direction) {
        int counter = 0;
        for(int i : direction) {
            if(i == 1) {
                counter++;
            }
        }
        if(counter == 0) {
            pathDead = true;
            return -1;
        }
        int[] random = new int[counter];
        int j = 0;
        for(int i = 0; i < direction.length; i++) {
            if(direction[i] == 1) {
                random[j] = i;
                j++;
            }
        }
        return random[MathUtils.random(0, random.length - 1)];
    }

    /**
     * Method creates an opening through the walls.
     *
     * It removes the wall inside it's own cell and also breaks the wall of the another cell.
     * @param tempPath: Not finished main path.
     * @param row: Row of the newest cell under construction.
     * @param column: Column of the newest cell under construction.
     * @param index: The next index after this current one.
     * @param randomIndex: Random direction between 0 - 3, which tells which direction to break.
     * @return Returns the updated path, if the current move was accepted.
     */
    public int[][] makePath(int[][] tempPath, int row, int column, int index, int randomIndex) {
        switch (randomIndex) {
            case 0:
                column--;
                break;
            case 1:
                row--;
                break;
            case 2:
                column++;
                break;
            default:
                row++;
        }
        boolean check = checkTemp(tempPath, row, column);
        if(check) {
            tempPath[index][0] = row;
            tempPath[index][1] = column;
        } else {
            pathClear = false;
        }
        return tempPath;
    }

    /**
     * Method checks if the next move is under bounds.
     *
     * @param tempPath: Path in progress.
     * @param row: Row of the current working cell.
     * @param column: Column of the current working cell.
     * @return Returns the info if the move was in bounds.
     */
    public boolean checkTemp(int[][] tempPath, int row, int column) {
        try {
            for(int i = 0; i < path1.length; i++) {
                if(path1[i][0] == row && path1[i][1] == column) {
                    return false;
                }
            }
        } catch (Exception e) {

        }


        for(int i = 0; i < middle.length; i++) {
            if(middle[i][0] == row && middle[i][1] == column) {
                return false;
            }
        }

        for(int i = 0; i < tempPath.length; i++) {
            if(tempPath[i][0] == row && tempPath[i][1] == column) {
                return false;
            }
        }
        if(row < 0 || column > size - 1 || column < 0 || row > size - 1) {
            return false;
        }
        if(firstPath && row <= path1Min) {
            return false;
        }
        return true;
    }

    /**
     * Method clears the scrubbed path, but keeps the main things.
     *
     * @param tempPath: Path that failed to make it through.
     * @return Returns cleared path.
     */
    public int[][] clearPath(int[][] tempPath) {
        int j = 1;
        if(firstPath) {
            j = 2;
        }
        for(int i = j; i <= tempPath.length - 3; i++) {
            tempPath[i][0] = 0;
            tempPath[i][1] = 0;
        }
        return tempPath;
    }

    public boolean checkLength(int x1, int x2, int y1, int y2, int length) {
        return (Math.abs(x2 - x1) + Math.abs(y2 - y1) == length);
    }

    /**
     * Method creates the hole for the exit.
     */
    public void createExit() {
        int random = MathUtils.random(0, 2 * size - 3);
        if(random <= size / 2 - 2) {
            exitRow = size / 2 - 1 - random;
            exitColumn = 0;
            return;
        }
        random -= size / 2 - 1;
        if(random <= size - 1) {
            exitRow = 0;
            exitColumn = random;
            return;
        }
        random -= size;
        exitRow = random;
        exitColumn = size - 1;
    }

    /**
     * Method creates a random path which tries to connect to the main network.
     */
    public void createRandom() {
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(generatingMap[i][j][0] == 0 &&  generatingMap[i][j][1] == 0 &&
                        generatingMap[i][j][2] == 0 && generatingMap[i][j][3] == 0) {
                    pathDone = false;
                    createRandomOne(i, j);
                    for(int k = 1; k < randomPath.length; k++) {
                        if (k > randomCounter) {
                            break;
                        }
                        createHole(randomPath[k][0], randomPath[k][1], randomPath[k - 1][0],
                                randomPath[k - 1][1]);
                    }
                }
            }
        }
    }

    /**
     * Method takes the random path one step forward.
     *
     * @param row: Row of the latest cell.
     * @param column: Column of the latest cell.
     */
    public void createRandomOne(int row, int column) {
        while(true) {
            randomPath = new int[size][2];
            randomPath[0][0] = row;
            randomPath[0][1] = column;
            randomCounter = 0;

            for(int i = 1; i < size - 1 && !pathDead; i++) {
                int[] direction = {1, 1, 1, 1};

                while(!pathDead) {
                    int random = randomIndex(direction);
                    if(random == -1) {break;}
                    makeRandomPath(randomPath[i - 1][0], randomPath[i - 1][1], i, random);
                    if(!pathClear) {
                        direction[random] = 0;
                        pathClear = true;
                    } else {
                        randomCounter++;
                        break;
                    }
                }
                if(pathDone) {
                    pathDead = false;
                    return;
                }
            }
            pathDead = false;
        }
    }

    /**
     * Method merges two cells or networks together.
     *
     * @param row: Row of the previous cell.
     * @param column: Column of the previous cell.
     * @param index: Index of the current path.
     * @param random: Random direction (0-3).
     */
    public void makeRandomPath(int row, int column, int index, int random) {
        switch (random) {
            case 0:
                column--;
                break;
            case 1:
                row--;
                break;
            case 2:
                column++;
                break;
            default:
                row++;
        }
        if(row < 0 || row > size - 1 || column < 0 || column > size - 1 ||
                (row == exitRow && column == exitColumn) || (row == path1[0][0] && column == path1[0][1])) {
            pathClear = false;
            return;
        }
        for(int i = 0; i < randomPath.length; i++) {
            if(randomPath[i][0] == row && randomPath[i][1] == column) {
                pathClear = false;
                return;
            }
        }

        checkNetwork(row, column);
        randomPath[index][0] = row;
        randomPath[index][1] = column;
    }

    /**
     * Method checks if the random path has found it's way to the main network.
     *
     * @param row: Current row.
     * @param column: Current column.
     */
    public void checkNetwork(int row, int column) {
        if(!(generatingMap[row][column][0] == 0 &&  generatingMap[row][column][1] == 0 &&
                generatingMap[row][column][2] == 0 && generatingMap[row][column][3] == 0)) {
            pathDone = true;
        }
    }

    /**
     * Method commits the results of the middle square and the paths.
     */
    public void createGeneratingMap1() {
        generatingMap[path1[0][0]][path1[0][1]][3] = 1;
        int index1 = 2;
        if(path2[0][0] == 0) {
            index1 = 1;
        }
        else if(path2[0][1] == 0) {
            index1 = 0;
        }
        generatingMap[path2[0][0]][path2[0][1]][index1] = 1;

        generatingMap[middle[0][0]][middle[0][1]][2] = 1;
        generatingMap[middle[0][0]][middle[0][1]][3] = 1;
        generatingMap[middle[1][0]][middle[1][1]][0] = 1;
        generatingMap[middle[1][0]][middle[1][1]][3] = 1;
        generatingMap[middle[2][0]][middle[2][1]][1] = 1;
        generatingMap[middle[2][0]][middle[2][1]][2] = 1;
        generatingMap[middle[3][0]][middle[3][1]][0] = 1;
        generatingMap[middle[3][0]][middle[3][1]][1] = 1;

        for(int ii = 1; ii < path1.length; ii++) {
            createHole(path1[ii][0], path1[ii][1], path1[ii - 1][0], path1[ii - 1][1]);
        }
        for(int i = 1; i < path2.length; i++) {
            createHole(path2[i][0], path2[i][1], path2[i - 1][0], path2[i - 1][1]);
        }
    }

    /**
     * Method creates a pathway between two cells.
     *
     * @param row1:
     * @param column1:
     * @param row2:
     * @param column2: (self planetary)
     */
    public void createHole(int row1, int column1, int row2, int column2) {
        boolean isRow = (column1 == column2);
        int first;
        int second;
        if(isRow) {
            if(row1 - row2 < 0) {
                first = 3;
                second = 1;
            } else {
                first = 1;
                second = 3;
            }
        } else {
            if(column1 - column2 < 0) {
                first = 2;
                second = 0;
            } else {
                first = 0;
                second = 2;
            }
        }
        generatingMap[row1][column1][first] = 1;
        generatingMap[row2][column2][second] = 1;
    }

    /**
     * Method scales map up, inserts textures and creates collision boxes.
     */
    public void putTextures() {
        Texture imgFloor1 = new Texture("floors/floor1.png");
        Texture imgFloor2 = new Texture("floors/floor2.png");
        Texture imgWall = new Texture("walls/wall3.png");

        map = new Texture[(size + 24) * 4 + 1][(size + 12) * 4 + 1];

        // Sets textures to the surroundings of the labyrinth.
        for(int row = 0; row < map.length; row++) {
            for(int column = 0; column < map[row].length; column++) {
                if(row >= 48 && row <= 48 + size * 4 && column == 24) {
                    column += size * 4 - 1;
                } else {
                    map[row][column] = imgFloor2;
                }
            }
        }
        // This makes sure that the collision map and visual map will match.
        int [][] collisionArray = new int[(size + 24) * 4 + 1][(size + 12) * 4 + 1];
        float mapX = windowWidth / 2 - (path1[0][1] * 4 + 26) * oneWidth - oneWidth / 2;
        float mapY = windowHeight / 2 + (path1[0][0] * 4 + 47) * oneWidth + oneWidth / 2;

        // Converts the randomly generated map to the larger scale and also inserts textures.
        for(int row = 0; row < generatingMap.length; row++) {
            for(int column = 0; column < generatingMap[row].length; column++) {
                for(int row2 = row * 4; row2 <= (row + 1) * 4; row2++) {
                    for(int column2 = column * 4; column2 <= (column + 1) * 4; column2++) {
                        if((generatingMap[row][column][0] == 0 && column2 == column * 4) ||
                           (generatingMap[row][column][1] == 0 && row2 == row * 4) ||
                           (generatingMap[row][column][2] == 0 && column2 == (column + 1) * 4) ||
                           (generatingMap[row][column][3] == 0 && row2 == (row + 1) * 4) &&
                           map[row2 + 48][column2 + 24] == null) {

                            map[row2 + 48][column2 + 24] = imgWall;
                        } else {
                            if(map[row2 + 48][column2 + 24] == null) {
                                map[row2 + 48][column2 + 24] = imgFloor1;
                            }
                        }
                    }
                }

                // Creates the collision boxes for the walls.
                if(generatingMap[row][column][0] == 0 && collisionArray[row * 4 + 50][column * 4 + 24] == 0) {
                    createGround(mapX + (column * 4 + 24.5f) * oneWidth, mapY - (row * 4 + 50.5f) * oneWidth, oneWidth * 0.5f, oneWidth * 2.5f);
                    collisionArray[row * 4 + 50][column * 4 + 24] = 1;
                }
                if(generatingMap[row][column][1] == 0 && collisionArray[row * 4 + 48][column * 4 + 26] == 0) {
                    createGround(mapX + (column * 4 + 26.5f) * oneWidth, mapY - (row * 4 + 48.5f) * oneWidth, oneWidth * 2.5f, oneWidth * 0.5f);
                    collisionArray[row * 4 + 48][column * 4 + 26] = 1;
                }
                if(generatingMap[row][column][2] == 0 && collisionArray[row * 4 + 50][column * 4 + 28] == 0) {
                    createGround(mapX + (column * 4 + 28.5f) * oneWidth, mapY - (row * 4 + 50.5f) * oneWidth, oneWidth * 0.5f, oneWidth * 2.5f);
                    collisionArray[row * 4 + 50][column * 4 + 28] = 1;
                }
                if(generatingMap[row][column][3] == 0 && collisionArray[row * 4 + 52][column * 4 + 26] == 0) {
                    createGround(mapX + (column * 4 + 26.5f) * oneWidth, mapY - (row * 4 + 52.5f) * oneWidth, oneWidth * 2.5f, oneWidth * 0.5f);
                    collisionArray[row * 4 + 52][column * 4 + 26] = 1;
                }
            }
        }
        collisionArray = null;
    }

    public void createGround(float x, float y, float width, float height) {
        Body groundBody = world.createBody(getGroundBodyDef(x, y));
        groundBody.createFixture(getGroundShape(width, height), 0.0f);
    }

    public BodyDef getGroundBodyDef(float x, float y) {
        BodyDef myBodyDef = new BodyDef();
        myBodyDef.type = BodyDef.BodyType.StaticBody;
        myBodyDef.position.set(x, y);
        return myBodyDef;
    }


    public PolygonShape getGroundShape(float width, float height) {
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(width, height);
        return groundBox;
    }

    /**
     * Method gives random locations for the pairs.
     *
     * @param numOfPairs: Number of pairs in current game.
     * @param arraySize: Amount of sentences to choose from.
     */
    public void createRandomPairs(int numOfPairs, int arraySize) {
        int [][] pairs = new int[numOfPairs][5];

        // Selects random index to the sentences array.
        for(int i = 0; i < numOfPairs; i++) {

            boolean clear = true;
            int random = MathUtils.random(1, arraySize);
            for(int j = 0; j < numOfPairs; j++) {
                if(random == pairs[j][0]) {
                    clear = false;
                }
            }
            if(!clear) {
                i--;
            } else {
                pairs[i][0] = random;
            }
        }

        for(int i = 0; i < numOfPairs; i++) {
            pairs[i][0]--;
        }

        // Generates random locations for the pairs.
        for(int i = 0; i < numOfPairs; i++) {
            int randomRow1 = MathUtils.random(0, generatingMap.length - 1);
            int randomCol1 = MathUtils.random(0, generatingMap.length - 1);
            int randomRow2 = MathUtils.random(0, generatingMap.length - 1);
            int randomCol2 = MathUtils.random(0, generatingMap.length - 1);

            if(Math.abs(randomRow1 - randomRow2) + Math.abs(randomCol1 - randomCol2) <= size / 3 ||
            !checkPairLocations(randomRow1, randomCol1, randomRow2, randomCol2, pairs)) {
                i--;
            } else {
                pairs[i][1] = randomRow1;
                pairs[i][2] = randomCol1;
                pairs[i][3] = randomRow2;
                pairs[i][4] = randomCol2;
            }
        }
        for(int i = 0; i < numOfPairs; i++) {
            pairs[i][1] = pairs[i][1] * 4 + 50;
            pairs[i][2] = pairs[i][2] * 4 + 26;
            pairs[i][3] = pairs[i][3] * 4 + 50;
            pairs[i][4] = pairs[i][4] * 4 + 26;
        }
        gameScreen.setRandomPairs(pairs);
    }

    /**
     * Method checks if the randomly chosen locations are in bounds.
     *
     * @param R1: Row of the pair 1.
     * @param C1: Column of the pair 1.
     * @param R2: Row of the pair 2.
     * @param C2: Column of the pair 2.
     * @param pairs: Coordinates of the chosen pairs.
     * @return: Returns true, if the given coordinates are ok, and false if not.
     */
    public boolean checkPairLocations(int R1, int C1, int R2, int C2, int[][] pairs) {
        // Check middle.
        for(int i = 0; i < 4; i++) {
            if((R1 == middle[i][0] && C1 == middle[i][1]) || (R2 == middle[i][0] && C2 == middle[i][1])) {
                return false;
            }
        }
        // Check start and exit.
        if((R1 == path2[0][0] && C1 == path2[0][1]) || (R2 == path2[0][0] && C2 == path2[0][1])
           || (R1 == path1[0][0] && C1 == path1[0][1]) || (R2 == path1[0][0] && C2 == path1[0][1])
           || (R1 == path1[1][0] && C1 == path1[1][1]) || (R2 == path1[1][0] && C2 == path1[1][1])) {
            return false;
        }
        // Check each other.
        for(int i = 0; i < pairs.length; i++) {
            int R3 = pairs[i][1];
            int C3 = pairs[i][2];
            int R4 = pairs[i][3];
            int C4 = pairs[i][4];
            if(R3 == 0 && C3 == 0 && R4 == 0 && C4 == 0) {
                continue;
            }
            if((R1 == R3 && C1 == C3) || (R1 == R4 && C1 == C4) || (R2 == R3 && C2 == C3) || (R2 == R4 && C2 == C4)) {
                return false;
            }
        }
        return true;
    }
}
