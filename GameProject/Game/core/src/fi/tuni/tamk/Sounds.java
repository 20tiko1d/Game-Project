package fi.tuni.tamk;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public final class Sounds {

    // Background music
    public static Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds and Music/gamemusic.wav"));

    // Activate object
    public static Sound activationSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/object_pickup.wav"));

    // Switch object
    public static Sound switchSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/pair_switch.wav"));

    // Connecting objects
    public static Sound connectingSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/right_pair.wav"));

    // Wrong validation
    public static Sound wrongValidationSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/wrong_pair.wav"));

    // Level completed
    public static Music levelCompletedMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds and Music/level_complete.wav"));

    // Character walk
    public static Sound walkSound;

    // Character sprint
    public static Sound sprintSound;

    // Button click sound
    public static Sound buttonPressSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/button_click2.wav"));

    private Sounds() {}
}
