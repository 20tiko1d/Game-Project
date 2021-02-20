package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


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


    public Texture [][] createMap(int size, int preferredLength, World world, float oneWidth,
                                  float windowWidth, float windowHeight, int arraySize,
                                  int numOfPairs, LevelScreen levelScreen) {
        this.size = size;
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
        return map;
    }

    public void disposeAll() {
        generatingMap = null;
        path1 = null;
        path2 = null;
        randomPath = null;
        middle = null;
    }

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

    public void checkNetwork(int row, int column) {
        if(!(generatingMap[row][column][0] == 0 &&  generatingMap[row][column][1] == 0 &&
                generatingMap[row][column][2] == 0 && generatingMap[row][column][3] == 0)) {
            pathDone = true;
        }
    }

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

    public void putTextures() {
        Texture imgFloor1 = new Texture("floors/floor1.png");
        Texture imgFloor2 = new Texture("floors/floor2.png");
        Texture imgWall = new Texture("walls/wall3.png");

        map = new Texture[(size + 24) * 4 + 1][(size + 12) * 4 + 1];

        for(int row = 0; row < map.length; row++) {
            for(int column = 0; column < map[row].length; column++) {
                if(row >= 48 && row <= 48 + size * 4 && column == 24) {
                    column += size * 4 - 1;
                } else {
                    map[row][column] = imgFloor2;
                }
            }
        }
        int [][] collisionArray = new int[(size + 24) * 4 + 1][(size + 12) * 4 + 1];
        float mapX = windowWidth / 2 - (path1[0][1] * 4 + 26) * oneWidth - oneWidth / 2;
        float mapY = windowHeight / 2 + (path1[0][0] * 4 + 47) * oneWidth + oneWidth / 2;

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

    public void createRandomPairs(int numOfPairs, int arraySize) {
        int [][] pairs = new int[numOfPairs][5];
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

        for(int i = 0; i < numOfPairs; i++) {
            int randomX1 = MathUtils.random(0, generatingMap.length - 1);
            int randomY1 = MathUtils.random(0, generatingMap.length - 1);
            int randomX2 = MathUtils.random(0, generatingMap.length - 1);
            int randomY2 = MathUtils.random(0, generatingMap.length - 1);

            if(Math.abs(randomX1 - randomX2) + Math.abs(randomY1 - randomY2) <= size / 3 ||
            !checkPairLocations(randomX1, randomY1, randomX2, randomY2, pairs)) {
                i--;
            } else {
                pairs[i][1] = randomX1;
                pairs[i][2] = randomY1;
                pairs[i][3] = randomX2;
                pairs[i][4] = randomY2;
            }
        }
        for(int i = 0; i < numOfPairs; i++) {
            pairs[i][1] = pairs[i][1] * 4 + 50;
            pairs[i][2] = pairs[i][2] * 4 + 26;
            pairs[i][3] = pairs[i][3] * 4 + 50;
            pairs[i][4] = pairs[i][4] * 4 + 26;
        }
        levelScreen.setRandomPairs(pairs);
    }

    public boolean checkPairLocations(int x1, int y1, int x2, int y2, int[][] pairs) {
        for(int i = 0; i < 4; i++) {
            if(x1 == middle[i][1] && y1 == middle[i][0] || x2 == middle[i][1] && y2 == middle[i][0]) {
                return false;
            }
        }
        if(x1 == path2[0][1] && y1 == path2[0][0] || x2 == path2[0][1] && y2 == path2[0][0]
           || x1 == path1[0][1] && y1 == path1[0][0] || x2 == path1[0][1] && y2 == path1[0][0]
           || x1 == path1[1][1] && y1 == path1[1][0] || x2 == path1[1][1] && y2 == path1[1][0]) {
            return false;
        }
        for(int i = 0; i < pairs.length; i++) {
            int y3 = pairs[i][1];
            int x3 = pairs[i][2];
            int y4 = pairs[i][3];
            int x4 = pairs[i][4];
            if(x3 == 0 && y3 == 0 && x4 == 0 && y4 == 0) {
                continue;
            }
            if(x1 == x3 && y1 == y3 || x1 == x4 && y1 == y4 || x2 == x3 && y2 == y3 || x2 == x4 && y2 == y4) {
                return false;
            }
        }
        return true;
    }
}
