package fi.tuni.tamk;

/**
 * HighScoreEntry needs to have variables for all the attributes from the
 * HighScore server.In the demo case, we need variables String name and
 * int score.
 *
 * The variable names need to match the json keys.
 * Also each of the attribute needs to have a getter and setter for json parsing.
 */
public class HighScoreEntry {
    private String name;
    private int score;
    private int id;
    private int map_id;

    // We need this no argument constructor for the json parsing!
    public HighScoreEntry() {
    }

    public HighScoreEntry(String name, int score, int id, int map_id) {
        this.name = name;
        this.score = score;
        this.id = id;
        this.map_id = map_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMap_id() {
        return this.map_id;
    }

    public void setMap_id(int map_id) {
        this.map_id = map_id;
    }
}