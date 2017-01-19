package com.hedgecoon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Created by Vasily on 02.09.2016.
 */
public class UIInputProcessor implements InputProcessor {
    final int start_button_width_part = 5;
    final int start_button_height_part = 5;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE) {
            byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

            Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            PixmapIO.writePNG(Gdx.files.external("mypixmap.png"), pixmap);
            pixmap.dispose();
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        /**start/pause button*/
        if (coordinatesCheck(VarSource.startButtonBox, screenX, screenY)) {
            SoundEffects.playSound(SoundEffects.onclick,false);
            switch (Main.state) {
                case RUN:
                    Main.state = Main.State.PAUSE;
                    break;
                case PAUSE:
                    Main.state = Main.State.RUN;
                    break;
                case MENU:
                    PlayerStage.changeState(PlayerStage.PlayerState.start);
                    PlayerStage.hedgeActor.state = CustomCompositeActor.States.start;
                    Main.state = Main.State.RUN;
                    /**main theme music reset**/
                    SoundEffects.mainThemeReset();
                    break;
                case TOPSCORES:
                    Main.stateAfterRestart = Main.State.MENU;
                    Main.state = Main.State.RESTART;
                    /**main theme music reset**/
                    SoundEffects.mainThemeReset();

                    break;
            }
            return true;
        }
        /**restart/next button**/
        else if (coordinatesCheck(VarSource.restartAndNexButtonBox, screenX, screenY)) {

            switch (Main.state) {

                case EARNED:
                    SoundEffects.playSound(SoundEffects.onclick,false);
                    Main.state = Main.State.TOPSCORES;
                    break;

                case TOPSCORES:
                    SoundEffects.playSound(SoundEffects.onclick,false);
                    Main.stateAfterRestart = Main.State.RUN;
                    Main.state = Main.State.RESTART;
                    break;
            }

            return true;

        }
        /**upgrades store button**/
        else if (coordinatesCheck(VarSource.upgradeStoreButtonBox, screenX, screenY) && Main.state.equals(Main.State.MENU)) {
            SoundEffects.playSound(SoundEffects.onclick,false);
            UIstage.windowIndex = UIstage.isItTouched(UIstage.windowIndex, VarSource.upgradeWindowIndex);
            UIstage.update = true;
            UIstage.upgradespageIndex = 0;
            return true;
        }
        /**upgrades next button**/
        else if (coordinatesCheck(VarSource.upgradeNextButtonBox, screenX, screenY)&& Main.state.equals(Main.State.MENU)) {
            SoundEffects.playSound(SoundEffects.onclick,false);
            switch (UIstage.windowIndex) {
                case VarSource.upgradeWindowIndex:
                    if (UIstage.upgradespageIndex < VarSource.upgradeKeys.length - 1) {
                        UIstage.update = true;
                        UIstage.upgradespageIndex++;
                    } else {
                        UIstage.upgradespageIndex = 0;
                        UIstage.update = true;
                    }
                    break;
                case VarSource.helpWindowIndex:
                    if (UIstage.helpPageIndex < VarSource.helpKeys.length - 1) {
                        UIstage.update = true;
                        UIstage.helpPageIndex++;
                    } else {
                        UIstage.helpPageIndex = 0;
                        UIstage.update = true;
                    }
                    break;
            }
            return true;
        }
        /**upgrades prev button**/
        else if (coordinatesCheck(VarSource.upgradePrevButtonBox, screenX, screenY)&& Main.state.equals(Main.State.MENU)) {
            SoundEffects.playSound(SoundEffects.onclick,false);
            switch (UIstage.windowIndex) {
                /**upgrades**/
                case VarSource.upgradeWindowIndex:
                    if (UIstage.upgradespageIndex > 0) {
                        UIstage.update = true;
                        UIstage.upgradespageIndex--;
                    } else {
                        UIstage.update = true;
                        UIstage.upgradespageIndex = VarSource.upgradeKeys.length - 1;
                    }
                    break;
                /**help**/
                case VarSource.helpWindowIndex:
                    if (UIstage.helpPageIndex > 0) {
                        UIstage.update = true;
                        UIstage.helpPageIndex--;
                    } else {
                        UIstage.update = true;
                        UIstage.helpPageIndex = VarSource.helpKeys.length - 1;
                    }
                    break;
            }
        }
        /**upgrades buy button**/
        else if (coordinatesCheck(VarSource.upgradeBuyButtonBox, screenX, screenY)&& Main.state.equals(Main.State.MENU)) {
            if (UIstage.windowIndex == VarSource.upgradeWindowIndex) {
                UIstage.update = true;
                UIstage.buyButtonClicked = true;
            }
            return true;
        }
        /**options panel**/
        else if (coordinatesCheck(VarSource.optionsPanelBox, screenX, screenY)&& Main.state.equals(Main.State.MENU)) {

            UIstage.optionsPanelClicked = true;
            for (int i = 0; i < VarSource.optionsBoxes.length; i++) {

                if (coordinatesCheck(VarSource.optionsBoxes[i], screenX)) {
                    UIstage.update = true;
                    UIstage.optionsBoxindex = i;
                    break;
                }

            }
            return true;
        }
        else {
            if (Main.state.equals(Main.State.MENU)){
                UIstage.hideAllMenus();
            }
        }
        return false;
    }

    static boolean coordinatesCheck(Vector2[] pos, int screenX, int screenY) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float extendBox = 0.05f;
        return (screenX >= width * (pos[0].x-extendBox) && screenX < width * (pos[1].x+extendBox) && screenY > height * (pos[1].y-extendBox) && screenY < height * (pos[0].y+extendBox));

    }

    private boolean coordinatesCheck(Vector2 pos, int screenX) {
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        return (screenX >= width * pos.x && screenX < width * pos.y); //y is x actualy

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
