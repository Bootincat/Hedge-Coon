package com.hedgecoon.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Vasily on 11.08.2016.
 */
public class MyContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        if (contact.getFixtureB().getFilterData().categoryBits == VarSource.CATEGORY_HEDGE && contact.getFixtureA().getFilterData().categoryBits == VarSource.CATEGORY_SPIKES) {
            /**sound effect**/
            SoundEffects.playSound(SoundEffects.obsHit,false);

            Main.state = Main.State.LOST;
        }
        if (contact.getFixtureB().isSensor() && contact.getFixtureA().getFilterData().categoryBits == VarSource.CATEGORY_HEDGE ) {
            CustomData data = (CustomData) contact.getFixtureB().getBody().getUserData();
            data.collected = true;

            /**sound effect**/
            SoundEffects.playSound(SoundEffects.collect,false);

            for (String s : VarSource.bonusKeys) {
                if (data.bonusTypes.get(s)) {
                    //clock
                    if (s.equals(VarSource.bonusKeys[0])) {
                        //if glider is not active
                        if (PlayerStage.transportModifer!= PlayerStage.TransportModifer.glider) {
                            UIstage.clockCount += 1;
                            UIstage.timeOn(UIstage.clockTask);
                            VarSource.timeSpeed = 0.5f;
                        }

                        //TODO: else if glider is active, then bonuses will be destroyed
                    }
                    //glider
                    else {
                        if (s.equals(VarSource.bonusKeys[1])) {
                            /** we can't take glider till we are in slowmo**/
                            if (VarSource.timeSpeed==1) {
                                WorldCreator.activateGlider();
                                UsefullMethods.changeLayersVisibility(PlayerStage.hedgeActor,"glider","");
                                UIstage.timeOn(UIstage.gliderTask);
                            }
                        }
                        else {
                            //smoke
                            if (s.equals(VarSource.bonusKeys[2])){
                                UIstage.smokeCount +=1;
                            }
                            else {
                                //kunai
                                if (s.equals(VarSource.bonusKeys[3])){
                                    UIstage.kunaiCount +=1;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
