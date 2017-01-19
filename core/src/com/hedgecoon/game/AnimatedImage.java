package com.hedgecoon.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Vasily on 23.09.2016.
 */

public class AnimatedImage extends Image {
    protected Animation animation = null;
    private float stateTime = 0;
    private boolean looping;
    private float maxState;

    public AnimatedImage(Animation animation, int playmode) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
        maxState = animation.getAnimationDuration();

        switch (playmode) {
            case 6:
                looping = false;
                break;
            case 2:
                looping = true;
                break;
        }
    }

    @Override
    public void act(float delta) {
        //not looping
        if (!looping) {
            if (((CustomCompositeActor) getParent()).state == CustomCompositeActor.States.start) {
                ((TextureRegionDrawable) getDrawable()).setRegion(animation.getKeyFrame(stateTime += delta));
                super.act(delta);
            }

            if (((CustomCompositeActor) getParent()).state == CustomCompositeActor.States.stop) {
                stateTime = 0;
                ((TextureRegionDrawable) getDrawable()).setRegion(animation.getKeyFrame(0));
            }

            if (getParent()== PlayerStage.hedgeActor && this.animation.isAnimationFinished(stateTime) && PlayerStage.state!= PlayerStage.PlayerState.run) {
                PlayerStage.changeState(PlayerStage.PlayerState.run);
            }
        }
        //looping
        else {
            ((TextureRegionDrawable) getDrawable()).setRegion(animation.getKeyFrame(stateTime += delta, looping));
        }

    }
}
