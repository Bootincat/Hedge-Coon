package com.hedgecoon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Vasily on 04.01.2017.
 */

public class SoundEffects {

    /**
     * main theme
     **/
    static Music mainTheme;
    static boolean soundOn;
    static Sound show = Gdx.audio.newSound(Gdx.files.internal("audio/show.mp3"));
    static Sound hide = Gdx.audio.newSound(Gdx.files.internal("audio/hide.mp3"));
    static Sound onclick = Gdx.audio.newSound(Gdx.files.internal("audio/onclick.mp3"));
    static Sound buy = Gdx.audio.newSound(Gdx.files.internal("audio/buy.mp3"));
    static Sound hook = Gdx.audio.newSound(Gdx.files.internal("audio/hook.mp3"));
    static Sound smoke = Gdx.audio.newSound(Gdx.files.internal("audio/smoke.mp3"));
    static Sound eggs = Gdx.audio.newSound(Gdx.files.internal("audio/eggs.mp3"));
    static Sound bats = Gdx.audio.newSound(Gdx.files.internal("audio/bats.mp3"));
    static Sound webCrash = Gdx.audio.newSound(Gdx.files.internal("audio/webCrash.mp3"));
    static Sound kunaiHit = Gdx.audio.newSound(Gdx.files.internal("audio/kunaiHit.mp3"));
    static Sound kunai = Gdx.audio.newSound(Gdx.files.internal("audio/kunai.mp3"));
    static Sound earned = Gdx.audio.newSound(Gdx.files.internal("audio/earned.mp3"));
    static Sound clockStart = Gdx.audio.newSound(Gdx.files.internal("audio/clockStart.mp3"));
    static Sound clockEnd = Gdx.audio.newSound(Gdx.files.internal("audio/clockEnd.mp3"));
    static Sound glider = Gdx.audio.newSound(Gdx.files.internal("audio/glider.mp3"));
    static Sound obsHit = Gdx.audio.newSound(Gdx.files.internal("audio/obsHit.mp3"));
    static Sound speedUp = Gdx.audio.newSound(Gdx.files.internal("audio/speedUp.mp3"));
    static Sound spider = Gdx.audio.newSound(Gdx.files.internal("audio/spider.mp3"));
    static Sound collect = Gdx.audio.newSound(Gdx.files.internal("audio/collect.mp3"));
    static Sound cannot = Gdx.audio.newSound(Gdx.files.internal("audio/cannot.mp3"));


    public SoundEffects() {
        createMainTheme();
    }


    /**
     * create an instance of main theme music
     **/
    public void createMainTheme() {
        mainTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/hc_main_theme.mp3"));
        mainTheme.setLooping(true);
        mainTheme.setVolume(0.7f);
        mainTheme.play();
    }


    /**
     * playing a sound effect
     **/
    static void playSound(Sound sound, boolean loop) {
        if (soundOn) {
            long id = sound.play(0.5f);
            sound.setLooping(id, loop);
        }
    }


    /**
     * main theme music control
     **/
    public void mainThemeHandle() {

        if (Main.state.equals(Main.State.RUN) || Main.state.equals(Main.State.MENU)) {
            if (soundOn && !mainTheme.isPlaying()) {
                mainTheme.setLooping(true);
                mainTheme.setVolume(0.7f);
                mainTheme.play();

            } else if (!soundOn && mainTheme.isPlaying()) {
                mainTheme.stop();
            }
        }
    }

    /**
     * stopping main theme music when change states
     **/
    static void mainThemeReset() {
        if (soundOn && mainTheme.isPlaying()) {
            mainTheme.stop();
        }
    }

    /**
     * checking for sound settings
     **/
    public void soundSettings() {
        if (Gdx.app.getPreferences(VarSource.prefSettings).getBoolean(VarSource.soundOn, true)) {
            soundOn = true;
        } else soundOn = false;
    }

    public void act() {
        soundSettings();
        mainThemeHandle();
    }
}
