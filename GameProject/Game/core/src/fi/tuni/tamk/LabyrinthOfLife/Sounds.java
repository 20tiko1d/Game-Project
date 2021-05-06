package fi.tuni.tamk.LabyrinthOfLife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Class which contains all of the sounds and music.
 *
 * @author Artur Haavisto
 */
public final class Sounds {

    // Background music
    public Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds and Music/gamemusic.wav"));

    // Activate object
    public Sound activationSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/object_pickup.wav"));

    // Switch object
    public Sound switchSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/pair_switch.wav"));

    // Connecting objects
    public Sound connectingSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/right_pair.wav"));

    // Wrong validation
    public Sound wrongValidationSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/wrong_pair.wav"));

    // Level completed
    public Music levelCompletedMusic = Gdx.audio.newMusic(Gdx.files.internal("Sounds and Music/level_complete.wav"));

    // Button click sound
    public Sound buttonPressSound = Gdx.audio.newSound(Gdx.files.internal("Sounds and Music/button_click.wav"));
}
