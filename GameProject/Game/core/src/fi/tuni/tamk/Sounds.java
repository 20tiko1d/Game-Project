package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public final class Sounds {

    // Background music
    public static Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds and Music/gamemusic.wav"));

    // Activate object
    public static Sound activationSound;

    // Switch object
    public static Sound switchSound;

    // Connecting objects
    public static Sound connectingSound;

    // Wrong validation
    public static Sound wrongValidationSound;

    // Level completed
    public static Music levelCompletedMusic;

    // Character walk
    public static Sound walkSound;

    // Character sprint
    public static Sound sprintSound;

    // Button click sound
    public static Sound buttonPressSound;

    private Sounds() {}
}
