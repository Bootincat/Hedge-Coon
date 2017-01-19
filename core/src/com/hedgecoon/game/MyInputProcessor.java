package com.hedgecoon.game;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Vasily on 31.05.2016.
 */
public class MyInputProcessor implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {

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

        if (!UIInputProcessor.coordinatesCheck(VarSource.startButtonBox, screenX, screenY) && Main.state == Main.State.RUN && PlayerStage.transportModifer!= PlayerStage.TransportModifer.glider) {
            if (WorldCreator.dJoints==null && WorldCreator.joints==null) {
                Vector2 touchV = new Vector2(VarSource.sightlength, VarSource.sightlength);
                Vector2 position = WorldCreator.hedge.getPosition().cpy();
                //вычисляем вектор, через который будем строить веревку и нормализуем его для последующих вычислений
                Vector2 ropeDirection = position.add(touchV);
                WorldCreator.createRayCast(ropeDirection);

                /**sound effect**/
                SoundEffects.playSound(SoundEffects.hook,false);
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (!UIInputProcessor.coordinatesCheck(VarSource.startButtonBox, screenX, screenY) && Main.state == Main.State.RUN && PlayerStage.transportModifer!= PlayerStage.TransportModifer.glider) {
                WorldCreator.setToDelete();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean touchDragged ( int screenX, int screenY, int pointer){
            return false;
        }

        @Override
        public boolean mouseMoved ( int screenX, int screenY){
            return false;
        }

        @Override
        public boolean scrolled ( int amount){
            return false;
        }
    }
