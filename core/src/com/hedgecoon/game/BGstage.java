package com.hedgecoon.game;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Vasily on 23.08.2016.
 */

public class BGstage extends Stage {

    float deltaX;
    float bgIndentX;
    float bgIndentY;
    float mgIndentX;
    float mgIndentY;
    static float maxCurX;
    static float centerX;
    static int curSpikeIndex;
    static int curWebIndex;
    private int lastWebIndex;
    ArrayList<Actor> bgActors;
    ArrayList<Actor> mgActors;
    static ArrayList<CompositeActor> spikes;
    static ArrayList<CustomCompositeActor> webs;
    private static CompositeActor kunaiActor;
    private CustomCompositeActor startPoint;
    private CustomCompositeActor bats;
    private RepeatAction batsAction;

    static ProjectInfoVO projectInfo;
    static IResourceRetriever ir;

    static final int spikesMark = 1;
    static final int cocoonesMark = 2;
    static final int webMark = 3;

    private static class BonusData {
        Vector2 position;
        boolean crushed;

        public BonusData(Vector2 position, boolean crushed) {
            this.crushed = crushed;
            this.position = position;
        }
    }

    public BGstage(IResourceRetriever ir) {
        //super(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        //super(new StretchViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new ExtendViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new FitViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        super(new FillViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        //super(new ScreenViewport());

        bgActors = new ArrayList<Actor>(2);
        mgActors = new ArrayList<Actor>(2);
        spikes = new ArrayList<CompositeActor>(10);
        webs = new ArrayList<CustomCompositeActor>(3);
        deltaX = 0;

        bgIndentX = 0;
        bgIndentY = getHeight() / 5;

        maxCurX = 0;
        centerX = VarSource.wUnitMax.x / 2;

        ProjectInfoVO projectInfo = ir.getProjectVO();

        /*this.projectInfo = projectInfo;
        this.ir = ir;*/


        //background1
        CompositeItemVO bg1Data = projectInfo.libraryItems.get("BG1");
        CustomCompositeActor bg1Actor = new CustomCompositeActor(bg1Data, ir);

        addActor(bg1Actor);
        bg1Actor.setX(bgIndentX);
        bg1Actor.setY(getHeight() / 2 - bg1Actor.getHeight() / 2);

        bgActors.add(bg1Actor);

        //background2
        CompositeItemVO bg2Data = projectInfo.libraryItems.get("BG2");
        CustomCompositeActor bg2Actor = new CustomCompositeActor(bg2Data, ir);

        addActor(bg2Actor);
        bg2Actor.setX(bg1Actor.getX() + bg1Actor.getWidth());
        bg2Actor.setY(getHeight() / 2 - bg2Actor.getHeight() / 2);

        bgActors.add(bg2Actor);

        /**middle ground**/

        createMiddleGroupes(create_Mini_MG_actos(projectInfo, ir), 0);
        createMiddleGroupes(create_Mini_MG_actos(projectInfo, ir), 1);

        //ceiling
        /*CompositeItemVO ceilingData = projectInfo.libraryItems.get("ceiling");
        CompositeActor ceiling = new CompositeActor(ceilingData, ir);

        addActor(ceiling);
        ceiling.setX(0);
        ceiling.setY(getHeight() - ceiling.getHeight());
        ceiling.setScale(40, 1);*/

        //obstacles
        //spike array
        curSpikeIndex = 0;
        /**bats actor**/
        CompositeItemVO batsData = projectInfo.libraryItems.get("bats");
        bats = new CustomCompositeActor(batsData, ir);

        bats.setVisible(false);

        /**bats action**/

        VisibleAction showAction = new VisibleAction();
        showAction.setVisible(true);

        MoveToAction resetPosAction = new MoveToAction();
        resetPosAction.setPosition(getWidth() / 2, 0);

        MoveToAction moveToAction = new MoveToAction();
        moveToAction.setPosition(getWidth(), getHeight());
        moveToAction.setDuration(3);

        RunnableAction soundAction = new RunnableAction();
        soundAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                SoundEffects.playSound(SoundEffects.bats, false);
            }
        });

        VisibleAction hideAction = new VisibleAction();
        hideAction.setVisible(false);

        DelayAction delayAction = new DelayAction(20);

        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(soundAction);
        sequenceAction.addAction(showAction);
        sequenceAction.addAction(resetPosAction);
        sequenceAction.addAction(moveToAction);
        sequenceAction.addAction(hideAction);
        sequenceAction.addAction(delayAction);

        batsAction = new RepeatAction();
        batsAction.setAction(sequenceAction);
        batsAction.setCount(-1);
        bats.addAction(batsAction);
        /************/

        addActor(bats);
        //loading data from overlap project
        CompositeItemVO spikeData = projectInfo.libraryItems.get("spikes");

        for (int i = 0; i < 9; i++) {

            CompositeActor spike = new CustomCompositeActor(spikeData, ir);
            spike.setX(-spike.getWidth());
            spike.setY(0);

            spike.setUserObject(null);

            addActor(spike);
            spikes.add(spike);
        }

        //web array
        curWebIndex = 0;

        lastWebIndex = 0;

        CompositeItemVO webData = projectInfo.libraryItems.get("webcrush");

        for (int i = 0; i < 8; i++) {
            CustomCompositeActor web = new CustomCompositeActor(webData, ir);

            web.setX(-web.getWidth()); //move it out of screen
            web.setY(0);

            web.setUserObject(null);

            addActor(web);
            webs.add(web);
        }

        //kunai actor
        CompositeItemVO kunaiData = projectInfo.libraryItems.get("kunai");
        kunaiActor = new CompositeActor(kunaiData, ir);
        kunaiActor.setVisible(false);
        kunaiActor.setUserObject(null);

        addActor(kunaiActor);

        //start point
        CompositeItemVO startPointData = projectInfo.libraryItems.get("startpoint");
        startPoint = new CustomCompositeActor(startPointData, ir);
        addActor(startPoint);

        /**sound effect**/
        RunnableAction runnableEggsAction = new RunnableAction();
        runnableEggsAction.setRunnable(new Runnable() {
            @Override
            public void run() {
                SoundEffects.playSound(SoundEffects.eggs, false);
            }
        });

        DelayAction delayEggsAction = new DelayAction(2f);

        SequenceAction sequenceEggsAction = new SequenceAction(runnableEggsAction, delayEggsAction);

        RepeatAction repeatEggsAction = new RepeatAction();
        repeatEggsAction.setAction(sequenceEggsAction);
        repeatEggsAction.setCount(-1);

        startPoint.addAction(repeatEggsAction);


    }

    private void createMiddleGroupes(ArrayList<CompositeActor> actorsArray, int i) {
        Group mgActor = new Group();
        mgActor.setWidth(getWidth());
        mgActor.setHeight(getHeight());

        mgActor.setX(getWidth() * i);
        mgActor.setY(0);

        for (CompositeActor actor : actorsArray) {
            mgActor.addActor(actor);
        }

        addActor(mgActor);

        mgActors.add(mgActor);
    }

    private ArrayList<CompositeActor> create_Mini_MG_actos(ProjectInfoVO projectInfo, IResourceRetriever ir) {
        ArrayList<CompositeActor> mgMiniActors = new ArrayList<CompositeActor>(5);

        for (int i = 0; i < 5; i++) {
            CompositeItemVO mgData = projectInfo.libraryItems.get("MG" + Integer.toString(i + 1));
            CompositeActor actor = new CompositeActor(mgData, ir);
            actor.setX(getWidth() / 10 + i * (getWidth() / 5));
            actor.setY(getHeight() / 2 - actor.getHeight() / 2);
            mgMiniActors.add(actor);
        }
        return mgMiniActors;
    }


    private void batsAnimation() {

    }

    @Override
    public void act(float delta) {
        if (!Main.firstitter) {
            float x = WorldCreator.hedge.getPosition().x;
            repeatDecorations(bgActors, 0);
            repeatDecorations(mgActors, 0);
            if (x - deltaX >= 1) {
                moveDecorations(bgActors, 0.5f);
                moveDecorations(mgActors, 1);
                deltaX = x;
            }
            if (maxCurX < x) {
                maxCurX = x;
            }

            manageSpikes();
            manageWebs();
            manageKunai();
            startPointManage();
        }
        super.act(delta);
    }

    private void startPointManage() {
        if (startPoint.getX() <= -startPoint.getWidth()) {
            startPoint.remove();
        } else {
            startPoint.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, centerX + (VarSource.startTombPos.x - maxCurX)));
            startPoint.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, VarSource.startTombPos.y));
        }
    }

    private void repeatDecorations(ArrayList<Actor> actors, float indent) {
        if (actors.get(1).getX() < 0) {
            Collections.reverse(actors);
            actors.get(1).setX(actors.get(0).getWidth() + indent);
        }
    }

    private void moveDecorations(ArrayList<Actor> actors, float step) {
        for (Actor a : actors) {
            a.setX(a.getX() - step);
        }
    }

    static void assignToBody(Vector2 position, int mark, Vector2 scale) {
        switch (mark) {
            case spikesMark:
                //get actor
                CompositeActor actorSpike = spikes.get(curSpikeIndex);
                //set position
                actorSpike.setUserObject(position);
                //set scale
                actorSpike.setScale(scale.x, scale.y);

                //set layer visibility
                UsefullMethods.changeLayersVisibility(actorSpike, "Default", "");

                break;
            case cocoonesMark:
                //get actor
                CompositeActor actorCocoon = spikes.get(curSpikeIndex);
                //set position
                actorCocoon.setUserObject(position);
                //set scale
                actorCocoon.setScale(scale.x, scale.y);

                //set layer visibility
                UsefullMethods.changeLayersVisibility(actorCocoon, "cocoon", "Default");

                break;
            case webMark:
                //get actor
                CompositeActor actorWeb = spikes.get(curSpikeIndex);
                //set position
                actorWeb.setUserObject(position);
                //set scale
                actorWeb.setScale(scale.x, scale.y);

                CustomCompositeActor web = webs.get(curWebIndex);
                //set position
                web.setUserObject(new BonusData(new Vector2(position.x, position.y + VarSource.defSpikeHeight * scale.y), false));
                //set scale
                web.setScale(scale.x, (2 - Math.abs(scale.y)));

                //set layer visibility
                UsefullMethods.changeLayersVisibility(actorWeb, "Default", "");
                break;
        }
        //update array index
        if (curSpikeIndex < spikes.size() - 1) {
            curSpikeIndex += 1;
        } else curSpikeIndex = 0;

        if (curWebIndex < webs.size() - 1) {
            curWebIndex += 1;
        } else curWebIndex = 0;
    }

    private void manageSpikes() {
        for (CompositeActor actor : spikes) {
            if (actor.getUserObject() != null) {
                Vector2 position = (Vector2) actor.getUserObject();
                float x = centerX + (position.x - maxCurX) + VarSource.spikeWidth;
                actor.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x));
                actor.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, position.y));

                if (actor.getX() < -actor.getWidth()) {
                    UsefullMethods.changeLayersVisibility(actor, "Default", "");
                }
            }
        }
    }

    private void manageWebs() {
        for (int i = 0; i < webs.size(); i++) {
            CustomCompositeActor actor = webs.get(i);
            if (actor.getUserObject() != null) {
                BonusData data = (BonusData) actor.getUserObject();
                Vector2 position = data.position;
                float x = centerX + (position.x - maxCurX) + VarSource.spikeWidth;
                actor.setX(UsefullMethods.wUnitToPix(UsefullMethods.x, x));
                actor.setY(UsefullMethods.wUnitToPix(UsefullMethods.y, position.y));
            }

            if (actor.getX() < -actor.getWidth()) {
                actor.state = CustomCompositeActor.States.stop;
                actor.setUserObject(null);
            }

            if (actor.getX() >= PlayerStage.hedgeActor.getX()) {
                BonusData data = (BonusData) actor.getUserObject();
                //if web isn't already crushed
                if (!data.crushed) {
                    //if hedge overlap web or coming closer to it
                    if (actor.getX() - PlayerStage.hedgeActor.getX() <= getWidth() / VarSource.obsPerScreen
                            && PlayerStage.hedgeActor.getY() >= actor.getY()) {
                        //if we have got a kunai
                        if (UIstage.kunaiCount > 0 && !PlayerStage.transportModifer.equals(PlayerStage.TransportModifer.glider)) {
                            //but in glider mod we can't use kunai
                            if (!PlayerStage.transportModifer.equals(PlayerStage.TransportModifer.glider)) {

                                kunaiActor.setPosition(PlayerStage.hedgeActor.getX(), PlayerStage.hedgeActor.getY());
                                MoveByAction action = new MoveByAction();
                                action.setAmount(getWidth() / VarSource.obsPerScreen, 0);
                                action.setDuration(0.3f);
                                kunaiActor.addAction(action);
                                kunaiActor.setVisible(true);
                                data.crushed = true;
                                actor.setUserObject(data);
                                kunaiActor.setUserObject(actor);

                                UIstage.kunaiCount -= 1;

                                /**sound effect**/
                                SoundEffects.playSound(SoundEffects.kunai, false);
                            }
                        } else { /**if we haven't got an any kunais**/
                            if (data.position.x - WorldCreator.hedge.getPosition().x <= VarSource.hedgeRadius) {
                                if (PlayerStage.speedState.equals(PlayerStage.SpeedState.fast) || PlayerStage.transportModifer.equals(PlayerStage.TransportModifer.glider)) {


                                    actor.state = CustomCompositeActor.States.start;
                                    data.crushed = true;
                                    actor.setUserObject(data);

                                    /**sound effect**/
                                    SoundEffects.playSound(SoundEffects.webCrash, false);

                                    if (PlayerStage.transportModifer != PlayerStage.TransportModifer.glider) {
                                        WorldCreator.hedge.setLinearVelocity(WorldCreator.hedge.getLinearVelocity().cpy().scl(0.75f));
                                        //Gdx.app.log("web","holy shit");
                                    }
                                } else {
                                    if (Main.state.equals(Main.State.RUN)) {
                                        /**sound effect**/
                                        SoundEffects.playSound(SoundEffects.obsHit, false);

                                        Main.state = Main.State.LOST;
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }
    }

    private void manageKunai() {
        if (kunaiActor.getUserObject() != null) {
            CustomCompositeActor a = (CustomCompositeActor) kunaiActor.getUserObject();
            if (kunaiActor.getX() >= a.getX()) {
                kunaiActor.setVisible(false);
                if (kunaiActor.getActions().size > 0){
                    kunaiActor.removeAction(kunaiActor.getActions().first());
                }
                kunaiActor.setUserObject(null);
                a.state = CustomCompositeActor.States.start;

                /**sound effect**/
                SoundEffects.playSound(SoundEffects.kunaiHit, false);
                SoundEffects.playSound(SoundEffects.webCrash, false);
            }

        }
    }
}
