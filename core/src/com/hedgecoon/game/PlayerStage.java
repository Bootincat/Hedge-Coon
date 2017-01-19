package com.hedgecoon.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

import java.util.ArrayList;

/**
 * Created by Vasily on 26.08.2016.
 */
public class PlayerStage extends Stage {
    private CompositeItemVO ropeData;
    public static CustomCompositeActor hedgeActor;
    private static ArrayList<CustomCompositeActor> smokeAnimActors;
    private static int curSmokeIndex;
    private static CompositeActor bonusActor;
    private CustomCompositeActor airStrip;
    //spider
    private CompositeItemVO spiderData;
    private Fixture lastFixture;
    private ArrayList<CompositeActor> spiders;
    private IResourceRetriever ir;
    private ArrayList<CompositeActor> ropeActors;
    private float centerX;
    private float maxCurX;
    public static PlayerState state;
    public static SpeedState speedState;
    public static TransportModifer transportModifer;

    private class BonusData {
        float x;
        boolean disabled;

        public BonusData(float x, boolean disabled) {
            this.x = x;
            this.disabled = disabled;
        }
    }


    public enum PlayerState {
        menu,
        start,
        run
    }

    public enum SpeedState {
        slow,
        medium,
        fast
    }

    public enum TransportModifer {
        nomod,
        glider,
        clock
    }

    public PlayerStage(IResourceRetriever ir) {
        //super(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        //super(new StretchViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new ExtendViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new FitViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        super(new FillViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new ScreenViewport());
        this.ir = ir;

        maxCurX = 0;
        curSmokeIndex = 0;
        centerX = VarSource.wUnitMax.x / 2;

        //transport modifer
        transportModifer = TransportModifer.nomod; //TODO: должно брать значение из преференций

        ProjectInfoVO projectInfoVO = ir.getProjectVO();
        ropeData = projectInfoVO.libraryItems.get("rope");

        //player actor
        CompositeItemVO hedgeData = projectInfoVO.libraryItems.get("hedge");
        hedgeActor = new CustomCompositeActor(hedgeData, ir);
        hedgeActor.setOrigin(0, 0);

        //bonus actor
        CompositeItemVO bonusData = projectInfoVO.libraryItems.get("bonus");
        bonusActor = new CompositeActor(bonusData, ir);
        bonusActor.setVisible(false);
        addActor(bonusActor);

        CompositeItemVO airStripData = projectInfoVO.libraryItems.get("airstrip");
        airStrip = new CustomCompositeActor(airStripData, ir);
        addActor(airStrip);

        UsefullMethods.changeLayersVisibility(hedgeActor, "run", "");
        addActor(hedgeActor);

        //smoke animation actor
        smokeAnimActors = new ArrayList<CustomCompositeActor>();
        for (int i = 0; i < 5; i++) {
            CompositeItemVO smokeData = projectInfoVO.libraryItems.get("smokeAnim");
            CustomCompositeActor smokeAnimActor = new CustomCompositeActor(smokeData, ir);
            smokeAnimActor.setUserObject(null);
            smokeAnimActor.state = CustomCompositeActor.States.stop;
            smokeAnimActor.setVisible(false);
            smokeAnimActors.add(smokeAnimActor);
            addActor(smokeAnimActor);
        }

        //spider actor data vo
        spiderData = projectInfoVO.libraryItems.get("spider");

        //array of a spiders
        spiders = new ArrayList<CompositeActor>();

        //last joined fixture
        lastFixture = null;


        changeState(PlayerState.menu);

    }

    @Override
    public void act(float delta) {
        spiderCheck();
        manageSpiders();
        cached();
        speedStateUpdate();
        super.act(delta);
    }

    @Override
    public void draw() {

        if (WorldCreator.hedge != null) {
            drawRope();
            drawPlayer();
            speedEffectupdate();
            bonusUpdate();
            smokeUpdate();
        }
        super.draw();
    }

    private void drawPlayer() {

        float x = centerX + (WorldCreator.hedge.getPosition().x - maxCurX);
        float y = WorldCreator.hedge.getPosition().y;
        hedgeActor.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x));// - (hedgeActor.getVo().width/2));
        hedgeActor.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, y));// - (hedgeActor.getVo().height/ 2));
        hedgeActor.setRotation(MathUtils.radiansToDegrees * WorldCreator.hedge.getAngle());

    }

    private void drawRope() {

        float hedgeX = WorldCreator.hedge.getPosition().x;
        if (maxCurX < hedgeX) {
            maxCurX = hedgeX;
        }

        //if we create rope
        if (WorldCreator.joints != null && ropeActors == null) {

            ropeActors = new ArrayList<CompositeActor>();

            for (Joint j : WorldCreator.joints) {
                CompositeActor ropeActor = new CompositeActor(ropeData, ir);

                ropeActor.setOrigin(ropeActor.getWidth() / 2, 0);

                addActor(ropeActor);
                ropeActors.add(ropeActor);
            }


        }


        //if we just render rope
        if (WorldCreator.joints != null && ropeActors != null) {
            for (int i = 0; i < WorldCreator.joints.length; i++) {

                float length;
                float angle;
                float scale;
                float x;
                float y;

                //first joint with player
                if (i == 0) {

                    x = centerX + (WorldCreator.joints[i].getAnchorA().x - maxCurX);
                    y = WorldCreator.joints[i].getAnchorA().y;

                    length = (WorldCreator.joints[i].getBodyB().getPosition().cpy().sub(WorldCreator.joints[i].getAnchorA().cpy())).len();
                    angle = WorldCreator.joints[i].getBodyB().getPosition().cpy().sub(WorldCreator.joints[i].getAnchorA().cpy()).angle();

                } else if (i == WorldCreator.joints.length - 1) {

                    x = centerX + (WorldCreator.joints[i].getBodyA().getPosition().x - maxCurX);
                    y = WorldCreator.joints[i].getBodyA().getPosition().y;
                    length = WorldCreator.joints[i].getAnchorB().cpy().sub(WorldCreator.joints[i].getBodyA().getPosition().cpy()).len();
                    angle = WorldCreator.joints[i].getAnchorB().cpy().sub(WorldCreator.joints[i].getBodyA().getPosition().cpy()).angle();

                } else {

                    x = centerX + (WorldCreator.joints[i].getBodyA().getPosition().x - maxCurX);
                    y = WorldCreator.joints[i].getBodyA().getPosition().y;
                    length = WorldCreator.joints[i].getBodyB().getPosition().cpy().sub(WorldCreator.joints[i].getBodyA().getPosition().cpy()).len();
                    angle = WorldCreator.joints[i].getBodyB().getPosition().cpy().sub(WorldCreator.joints[i].getBodyA().getPosition().cpy()).angle();
                }
                scale = UsefullMethods.wUnitToPix(UsefullMethods.x, length) / ropeActors.get(i).getHeight();

                ropeActors.get(i).setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x));
                ropeActors.get(i).setY(UsefullMethods.wUnitToPix(UsefullMethods.y, y));

                ropeActors.get(i).getItemsByLayer("Default").first().setScaleY(scale); /**MAY CAUSE SOME ISSUES, WATCH OUT LATER**/
                ropeActors.get(i).setRotation(-90 + angle);

            }
        }

        if (WorldCreator.joints == null && ropeActors != null) {
            for (CompositeActor a : ropeActors) {
                a.remove();
            }
            ropeActors = null;
        }


    }

    public static void changeState(PlayerState stateName) {
        UsefullMethods.changeLayersVisibility(hedgeActor, stateName.name(), "");
        state = stateName;
    }

    private void spiderCheck() {
        if (VarSource.difficult > VarSource.cocoonStartSpawnDiff && WorldCreator.curFixture != null && WorldCreator.clicked && lastFixture != WorldCreator.curFixture) {
            CustomData data = (CustomData) WorldCreator.curFixture.getBody().getUserData();
            if (data != null) {
                if (data.cocoon) {
                    //float x = centerX + (WorldCreator.curFixture.getBody().getPosition().x - maxCurX) - 2f;
                    spiders.add(createSpider(WorldCreator.curFixture.getBody().getPosition().x));

                    //spiderActor.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x, getWidth()));
                    lastFixture = WorldCreator.curFixture;
                }
            }
        }
    }

    private CompositeActor createSpider(float x) {

        CompositeActor spiderActor = new CustomCompositeActor(spiderData, ir);
        spiderActor.setLayerVisibility("static", true);
        spiderActor.setLayerVisibility("anim", false);
        //spiderActor.setPosition(0, getHeight());
        spiderActor.setPosition(x, getHeight() / 2);

        MoveByAction action = new MoveByAction();
        action.setDuration(0.7f);
        action.setAmountY(-getHeight());

        spiderActor.addAction(action);
        spiderActor.setUserObject(new BonusData(x, false));

        spiderActor.setY(getHeight());
        addActor(spiderActor);

        /**sound effect**/
        SoundEffects.playSound(SoundEffects.spider, false);

        return spiderActor;
    }

    private void manageSpiders() {
        if (spiders.size() > 0) {
            for (int i = 0; i < spiders.size(); i++) {
                float x = centerX + (((BonusData) spiders.get(i).getUserObject()).x - maxCurX);
                spiders.get(i).setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x));

                if (spiders.get(i).getX() < -spiders.get(i).getWidth()) {
                    spiders.get(i).remove();
                    spiders.remove(i);
                }
            }
        }
    }

    private void cached() {
        for (CompositeActor spdr : spiders) {
            //if hedge intersect with spider
            if (Math.abs(spdr.getX() - hedgeActor.getX()) <= spdr.getWidth() / 2 && hedgeActor.getY() >= spdr.getY()) {

                BonusData data = (BonusData) spdr.getUserObject();
                if (!data.disabled) {
                    //have i got an explosive
                    if (UIstage.smokeCount > 0 && spdr.getActions().size != 0) {
                        //smoke
                        smokeAnimActors.get(curSmokeIndex).state = CustomCompositeActor.States.start;
                        smokeAnimActors.get(curSmokeIndex).setUserObject(spdr);
                        smokeAnimActors.get(curSmokeIndex).setVisible(true);

                        curSmokeIndex = UsefullMethods.indexCountUpdate(curSmokeIndex, smokeAnimActors.size());

                        data.disabled = true;
                        spdr.setUserObject(data);

                        spdr.removeAction(spdr.getActions().first());
                        UIstage.smokeCount -= 1;

                        /**sound effect**/
                        SoundEffects.playSound(SoundEffects.smoke, false);

                    } else {
                        if (Main.state.equals(Main.State.RUN)) {
                            hedgeActor.setVisible(false);
                            spdr.setLayerVisibility("static", false);
                            spdr.setLayerVisibility("anim", true);
                            Main.state = Main.State.LOST;
                        }
                    }
                }
            }
        }
    }

    //move and rotate air strips animation, if player moving fast
    private void speedEffectupdate() {
        if (speedState != SpeedState.slow) {
            //airStrip.setRotation(WorldCreator.hedge.getLinearVelocity().angle());

           /* float x = centerX + (WorldCreator.hedge.getPosition().x - maxCurX) - 1.8f;
            float y = WorldCreator.hedge.getPosition().y;
            airStrip.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x+VarSource.hedgeRadius, getWidth())-airStrip.getWidth());
            airStrip.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, y, getHeight()));*/

            /*airStrip.setX(hedgeActor.getX() + hedgeActor.getVo().width / 2 - airStrip.getWidth() * 0.75f);
            airStrip.setY(hedgeActor.getY() + hedgeActor.getVo().height / 2);*/

            airStrip.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, centerX + (WorldCreator.hedge.getPosition().x - maxCurX)));
            airStrip.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, WorldCreator.hedge.getPosition().y));
        }
    }

    //checking and updating speed state of a player
    private void speedStateUpdate() {
        float velo = WorldCreator.hedge.getLinearVelocity().x;
        if (velo >= VarSource.fastSpeed) {
            if (!airStrip.isVisible()) {
                airStrip.setVisible(true);
            }
            //if glider is active we don't need to showing "fast" animation
            if (PlayerStage.transportModifer != TransportModifer.glider) {
                //is layer visible? checking out that state is already updated
                if (!airStrip.getVo().composite.layers.get(airStrip.getLayerIndex("fast")).isVisible) {
                    speedState = SpeedState.fast;
                    UsefullMethods.changeLayersVisibility(airStrip, "fast", "medium");

                    /**sound effect**/
                    SoundEffects.playSound(SoundEffects.speedUp, false);
                }
            } else {
                UsefullMethods.changeLayersVisibility(airStrip, "medium", "");
                speedState = SpeedState.fast;
            }


        }

        if (velo < VarSource.fastSpeed && velo >= VarSource.mediumSpeed) {
            if (!airStrip.isVisible()) {
                airStrip.setVisible(true);
            }

            speedState = SpeedState.medium;
            UsefullMethods.changeLayersVisibility(airStrip, "medium", "");


        }

        if (velo < VarSource.mediumSpeed) {
            speedState = SpeedState.slow;

            if (airStrip.isVisible()) {
                airStrip.setVisible(false);
            }
        }
    }

    private void bonusUpdate() {
        //if bonus on the screen
        if (Math.abs(WorldCreator.hedge.getPosition().x - WorldCreator.bonus.getPosition().x) <= VarSource.wUnitMax.x / 2) {
            CustomData data = (CustomData) WorldCreator.bonus.getUserData();
            //setting visibility
            if (!data.collected) {
                if (!bonusActor.isVisible()) {

                    for (String s : VarSource.bonusKeys) {
                        //if one of types is chosen
                        if (data.bonusTypes.get(s)) {
                            UsefullMethods.changeLayersVisibility(bonusActor, s, "");
                        }
                    }

                    bonusActor.setVisible(true);
                }
            } else {
                bonusActor.setVisible(false);
            }

            //setting position on the screen
            float x = centerX + (WorldCreator.bonus.getPosition().x - maxCurX);
            float y = WorldCreator.bonus.getPosition().y;

            bonusActor.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x));
            bonusActor.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, y));


        } else {
            //if not on the screen then make it not visible
            if (bonusActor.isVisible()) {
                bonusActor.setVisible(false);
            }
        }
    }

    private void smokeUpdate() {
        for (CustomCompositeActor smokeAnimActor : smokeAnimActors) {

            CustomCompositeActor spdr = (CustomCompositeActor) smokeAnimActor.getUserObject();

            if (spdr != null) {
                if (smokeAnimActor.getX() >= -smokeAnimActor.getWidth()) {
                    smokeAnimActor.setX(spdr.getX());
                    smokeAnimActor.setY(spdr.getY());
                } else {
                    smokeAnimActor.state = CustomCompositeActor.States.stop;
                    smokeAnimActor.setVisible(false);
                    smokeAnimActor.setUserObject(null);
                }
            }

           /* if (spdr != null) {
                smokeAnimActor.setX(spdr.getX());
                smokeAnimActor.setY(spdr.getY());
            } else {
                if (smokeAnimActor.isVisible()) {
                    smokeAnimActor.state = CustomCompositeActor.States.stop;
                    smokeAnimActor.setVisible(false);
                }
            }*/
        }
    }

}
