package com.hedgecoon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.ProjectInfoVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

/**
 * Created by Vasily on 28.07.2016.
 */
public class UIstage extends Stage {

    private Label curScoreLabel;
    private CompositeActor coonCoinsActor;
    private Label bestScoreLabel;
    private CompositeActor smokeActor;
    private CompositeActor kunaiActor;
    private CompositeActor highScoresActor;
    private CompositeActor earnedActor;
    private CompositeActor upgradeButton;
    private CompositeActor upgradesStoreActor;
    private CompositeActor helpActor;
    private CompositeActor optionsPanel;
    private ArrayList<CompositeActor> windowActorsArray;
    static boolean upgradesVisibility;
    static int helpPageIndex;
    static int lastHelpPageIndex;
    static boolean buyButtonClicked;
    static int windowIndex;
    static int lastWindowIndex;
    static int optionsBoxindex;
    static boolean optionsPanelClicked;
    static int upgradespageIndex;
    private static int lastUpgradesPageIndex;
    private Main.State lastState;
    private static CompositeActor countDownActor;
    static int curScore;
    private int localCurScore;
    private int coonCoinsShowed;
    private int max;
    Actor powerCount;
    public static int power;
    private static CompositeActor pauseActor;
    private static CompositeActor restartButton;
    private CompositeActor nextButton;
    private ProjectInfoVO projectInfo;
    private IResourceRetriever ir;
    static Timer.Task clockTask;
    static Timer.Task gliderTask;
    private static SimpleDateFormat sdf;
    private int storedCoins;
    private ArrayList<CompositeActor> menuInterfaceElements;
    static boolean update;

    static int smokeCount;
    static int kunaiCount;
    static int clockCount;
    static int gliderCount;

    private boolean earnedSoundIsPlaying;
    private boolean optionsShowed;

    private CompositeActor logoActor;
    private CompositeActor gameByActor;


    public UIstage(IResourceRetriever ir) {
        //super(new StretchViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        super(new ExtendViewport(VarSource.viewportWidth, VarSource.viewportHeight));
        //super(new ScreenViewport());
        //super(new FillViewport(VarSource.viewportWidth,VarSource.viewportHeight));
        float h = Gdx.graphics.getHeight();
        float w = Gdx.graphics.getWidth();

        Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);
        smokeCount = preferences.getInteger(VarSource.upgradeKeys[2], 0);
        kunaiCount = preferences.getInteger(VarSource.upgradeKeys[3], 0);

        max = 0;
        curScore = 0;
        coonCoinsShowed = 0;
        storedCoins = 0;
        localCurScore = 0;

        upgradesVisibility = false;
        buyButtonClicked = false;

        optionsBoxindex = 0;
        optionsPanelClicked = false;

        upgradespageIndex = -1;
        lastUpgradesPageIndex = -1;

        helpPageIndex = -1;
        lastHelpPageIndex = -1;

        windowIndex = -1;
        lastWindowIndex = -1;
        windowActorsArray = new ArrayList<CompositeActor>(2);

        menuInterfaceElements = new ArrayList<CompositeActor>();

        update = true;

        sdf = new SimpleDateFormat("mm:ss", Locale.UK);

        clockTask = new Timer.Task() {
            @Override
            public void run() {
                countDownActor.setVisible(false);
                PlayerStage.transportModifer = PlayerStage.TransportModifer.nomod;
                VarSource.timeSpeed = 1;

                /**sound effect**/
                SoundEffects.playSound(SoundEffects.clockEnd, false);
            }
        };

        gliderTask = new Timer.Task() {
            @Override
            public void run() {
                UsefullMethods.changeLayersVisibility(PlayerStage.hedgeActor, "run", "");
                countDownActor.setVisible(false);
                PlayerStage.transportModifer = PlayerStage.TransportModifer.nomod;
                WorldCreator.deactivateGlider();
            }
        };


        this.ir = ir;
        projectInfo = ir.getProjectVO();

        //score
        CompositeItemVO scoreData = projectInfo.libraryItems.get("score");
        CompositeActor score = new CompositeActor(scoreData, ir);

        addActor(score);

        curScoreLabel = (Label) score.getItemsByLayer("Default").get(0);
        curScoreLabel.setText("0");

        score.setX((getWidth() * 0.01f));
        score.setY(getHeight() * (1 - VarSource.upgradeStoreButtonBox[1].y));

        /**logo**/
        CompositeItemVO logoData = projectInfo.libraryItems.get("logo");
        logoActor = new CompositeActor(logoData, ir);
        logoActor.setX(getWidth() / 2 - logoActor.getWidth() / 2);
        logoActor.setY(getHeight() * (1 - 0.48f));
        logoActor.setUserObject(new Vector2(logoActor.getX(),logoActor.getY()));
        addActor(logoActor);
        menuInterfaceElements.add(logoActor);
        /**created by**/
        CompositeItemVO gameByData = projectInfo.libraryItems.get("gameBy");
        gameByActor = new CompositeActor(gameByData, ir);
        gameByActor.setX(getWidth() / 2 - gameByActor.getWidth() / 2);
        gameByActor.setY(getHeight() * (1 - VarSource.restartAndNexButtonBox[0].y));
        gameByActor.setUserObject(new Vector2(gameByActor.getX(), gameByActor.getY()));
        addActor(gameByActor);
        menuInterfaceElements.add(gameByActor);

        //buttons data
        CompositeItemVO buttonData = projectInfo.libraryItems.get("button");

        //pause button
        pauseActor = new CompositeActor(buttonData, ir);

        pauseActor.setX((getWidth() / 15) - pauseActor.getWidth() / 2);
        pauseActor.setY(pauseActor.getHeight() / 10);
        UsefullMethods.changeLayersVisibility(pauseActor, "resume", "Default");


        addActor(pauseActor);

        //smoke
        CompositeItemVO smokeData = projectInfo.libraryItems.get("smokeCount");

        smokeActor = new CompositeActor(smokeData, ir);

        smokeActor.setX((getWidth() * 0.01f));
        smokeActor.setY(getHeight() * (1 - VarSource.smokeBox[1].y));

        addActor(smokeActor);

        //kunai
        CompositeItemVO kunaiData = projectInfo.libraryItems.get("kunaiCount");

        kunaiActor = new CompositeActor(kunaiData, ir);

        kunaiActor.setX((getWidth() * 0.01f));
        kunaiActor.setY(getHeight() * (1 - VarSource.kunaiBox[1].y));

        addActor(kunaiActor);

        //timer actor
        CompositeItemVO timerData = projectInfo.libraryItems.get("timer");
        countDownActor = new CompositeActor(timerData, ir);

        countDownActor.setX((getWidth() / 2) - countDownActor.getWidth() / 2);
        countDownActor.setY(pauseActor.getY() + pauseActor.getHeight() / 2);

        countDownActor.setVisible(false);

        addActor(countDownActor);

        //restart button
        CompositeItemVO restartData = projectInfo.libraryItems.get("restartButton");

        restartButton = new CompositeActor(restartData, ir);

        restartButton.setX(getWidth() / 2 - restartButton.getWidth() / 2);
        restartButton.setY(-restartButton.getHeight());
        restartButton.setVisible(false);

        MoveToAction restartAction = new MoveToAction();
        restartAction.setPosition(getWidth() / 2 - restartButton.getWidth() / 2, pauseActor.getY());
        restartAction.setDuration(0.3f);
        restartButton.addAction(restartAction);

        //earned score actor
        CompositeItemVO earnedData = projectInfo.libraryItems.get("earned");
        earnedActor = new CompositeActor(earnedData, ir);
        earnedActor.setVisible(false);
        earnedActor.setX(getWidth());
        earnedActor.setY(getHeight() / 2 - earnedActor.getHeight() / 2);
        ((Label) earnedActor.getItemsByLayer("text").first()).setFontScale(2);
        /**for playing sound just once**/
        earnedSoundIsPlaying = false;

        //next button
        CompositeItemVO nextData = projectInfo.libraryItems.get("nextButton");
        nextButton = new CompositeActor(nextData, ir);
        nextButton.setX(getWidth() / 2 - nextButton.getWidth() / 2);
        nextButton.setY(-nextButton.getHeight());
        nextButton.setVisible(false);

        MoveToAction nextAction = new MoveToAction();
        nextAction.setPosition(getWidth() / 2 - nextButton.getWidth() / 2, pauseActor.getY());
        nextAction.setDuration(0.3f);
        nextButton.addAction(nextAction);


        //coon coins actor for main menu
        CompositeItemVO coonCoinsData = projectInfo.libraryItems.get("coonCoins");
        coonCoinsActor = new CompositeActor(coonCoinsData, ir);

        //coonCoinsActor.setX(getWidth() * VarSource.upgradeStoreButtonBox[0].x);
        coonCoinsActor.setX(getWidth() * 0.99f);
        coonCoinsActor.setY(getHeight() * (1 - VarSource.upgradeStoreButtonBox[1].y));
        /**user data**/
        coonCoinsActor.setUserObject(new Vector2(coonCoinsActor.getX(), coonCoinsActor.getY()));

        addActor(coonCoinsActor);
        menuInterfaceElements.add(coonCoinsActor);

        //upgrade button
        CompositeItemVO upgrData = projectInfo.libraryItems.get("upgradeButton");
        upgradeButton = new CompositeActor(upgrData, ir);

        //upgradeButton.setX(getWidth() * VarSource.upgradeStoreButtonBox[0].x);
        upgradeButton.setX(getWidth() * 0.99f);
        upgradeButton.setY(getHeight() * (1 - VarSource.upgradeStoreButtonBox[0].y));
        /**user data**/
        upgradeButton.setUserObject(new Vector2(upgradeButton.getX(), upgradeButton.getY()));

        addActor(upgradeButton);
        menuInterfaceElements.add(upgradeButton);

        //upgrades store actor
        CompositeItemVO upgrStoreData = projectInfo.libraryItems.get("upgrades");
        upgradesStoreActor = new CompositeActor(upgrStoreData, ir);

        upgradesStoreActor.setX(-upgradesStoreActor.getWidth());
        upgradesStoreActor.setY(getHeight() / 2 - upgradesStoreActor.getHeight() / 2);

        upgradesStoreActor.setVisible(false);
        /**user data**/
        upgradesStoreActor.setUserObject(new Vector2(upgradesStoreActor.getX(), upgradesStoreActor.getY()));

        addActor(upgradesStoreActor);
        menuInterfaceElements.add(upgradesStoreActor);

        VarSource.upgradeBuyButtonBox = settingUpTouchBoxes(VarSource.upgradeBuyBox, upgradesStoreActor.getWidth() * 2, upgradesStoreActor.getHeight());
        VarSource.upgradePrevButtonBox = settingUpTouchBoxes(VarSource.upgradePrevBox, upgradesStoreActor.getWidth() * 2, upgradesStoreActor.getHeight());
        VarSource.upgradeNextButtonBox = settingUpTouchBoxes(VarSource.upgradeNextBox, upgradesStoreActor.getWidth() * 2, upgradesStoreActor.getHeight());

        //options panel
        CompositeItemVO optionsData = projectInfo.libraryItems.get("optionsPanel");
        optionsPanel = new CompositeActor(optionsData, ir);
        optionsPanel.setX(getWidth());
        optionsPanel.setY(getHeight() * (1 - VarSource.startButtonBox[0].y));
        /**user data**/
        optionsPanel.setUserObject(new Vector2(optionsPanel.getX(), optionsPanel.getY()));
        /**update language indicator**/
        String curLang = Gdx.app.getPreferences(VarSource.prefSettings).getString(VarSource.language, VarSource.eng);
        if (curLang.equals(VarSource.eng)) {
            optionsPanel.setLayerVisibility("languageRu", false);
            optionsPanel.setLayerVisibility("languageEn", true);
        } else {
            optionsPanel.setLayerVisibility("languageRu", true);
            optionsPanel.setLayerVisibility("languageEn", false);
        }

        /**pref setting of sound**/
        optionsPanel.setLayerVisibility("soundon", Gdx.app.getPreferences(VarSource.prefSettings).getBoolean(VarSource.soundOn, true));

        optionsShowed = false;

        addActor(optionsPanel);
        menuInterfaceElements.add(optionsPanel);

        settingUpOptionsTouchBoxes(optionsPanel.getWidth() * 1.25f, optionsPanel.getHeight());

        //high scores table actor
        CompositeItemVO highScoresData = projectInfo.libraryItems.get("highscores");
        highScoresActor = new CompositeActor(highScoresData, ir);

        highScoresActor.setX(getWidth() / 2);
        highScoresActor.setY(getHeight());
        highScoresActor.setLayerVisibility("last", false);
        /**user data**/
        highScoresActor.setUserObject(new Vector2(highScoresActor.getX(), highScoresActor.getY()));

        highScoresActor.setVisible(false);
        addActor(highScoresActor);
        menuInterfaceElements.add(highScoresActor);

        /**help actor**/
        CompositeItemVO helpData = projectInfo.libraryItems.get("help");
        helpActor = new CompositeActor(helpData, ir);
        helpActor.setX(getWidth() + helpActor.getWidth());
        helpActor.setY(getHeight() * (1 - VarSource.upgradeBuyButtonBox[0].y));
        helpActor.setVisible(false);
        helpActor.setUserObject(new Vector2(helpActor.getX(), helpActor.getY()));

        addActor(helpActor);
        menuInterfaceElements.add(helpActor);

        windowActorsArray.add(upgradesStoreActor);
        windowActorsArray.add(highScoresActor);
        windowActorsArray.add(helpActor);

    /*    Label labelTest = new Label("ываыва",new Label.LabelStyle(VarSource.shadowFont,Color.WHITE));
        labelTest.setPosition(getWidth()/2,getHeight()/2);
        addActor(labelTest);*/
    }

    /**
     * this method is for setting up touch boxes of options panel and upgrade store
     **/
    private Vector2[] settingUpTouchBoxes(Vector2[] box, float width, float height) {
        Vector2[] resultArr = new Vector2[2];
        float startX = (getWidth() / 2 - width / 2) / getWidth();
        float startY = (getHeight() / 2 + height / 2) / getHeight();

        for (int i = 0; i < box.length; i++) {
            float x = (box[i].x * width) / getWidth();
            float y = (box[i].y * height) / getHeight();
            Vector2 vector = new Vector2(startX + x, startY - y);
            resultArr[i] = vector;
        }

        return resultArr;
    }

    private void settingUpOptionsTouchBoxes(float width, float height) {
        float startX = (getWidth() - width) / getWidth();

        VarSource.optionsPanelBox[0] = new Vector2(startX, VarSource.startButtonBox[0].y);
        VarSource.optionsPanelBox[1] = new Vector2(1, VarSource.startButtonBox[1].y);

        float boxRange = 0.2f;

        for (int i = 0; i < 5; i++) {
            float x1 = (boxRange * i * width) / getWidth();
            float x2 = (boxRange * (i + 1) * width) / getWidth();
            Vector2 vector = new Vector2(startX + x1, startX + x2);
            VarSource.optionsBoxes[i] = vector;
        }
    }


    @Override
    public void act() {
        switch (Main.state) {

            case RUN:
                if (curScore < MathUtils.round(WorldCreator.hedge.getPosition().x)) {
                    curScore = MathUtils.round(WorldCreator.hedge.getPosition().x - 50);
                    curScoreLabel.setText(unitTranslate(curScore));
                    //curScoreLabel.setText(Float.toString(MathUtils.round(WorldCreator.hedge.getLinearVelocity().x)));
                    //curScoreLabel.setText(Float.toString(Gdx.graphics.getFramesPerSecond()));
                    countDownUpdate();
                }
                break;
            case EARNED:

                showEarnedScore();
                break;

            case TOPSCORES:
                if (!highScoresActor.isVisible()) {
                    earnedActor.setVisible(false);
                    nextButton.setVisible(false);
                    countDownActor.setVisible(false);
                    showHighScores();
                }
                break;
            case MENU:
                coonCoinsUpdate();
                optionspanelOnClick();
                showHideWindows();
                break;
        }
        setButton();
        //upgradesVisibleManage();
        updateBonusCount();
        interfaceVisibility();


        super.act();
    }

    private void coonCoinsUpdate() {
        ((Label) coonCoinsActor.getItemsByLayer("text").first()).setText(Integer.toString(Gdx.app.getPreferences(VarSource.prefSettings).getInteger(VarSource.coonCoins)));
    }


    private void showEarnedScore() {
        //отображаем актёра через action
        if (!earnedActor.isVisible()) {
            ((Label) earnedActor.getItemsByLayer("header").first()).setText(VarSource.myBundle.format("earnedHeader"));
            MoveToAction action = new MoveToAction();
            action.setPosition(getWidth() / 2 - earnedActor.getWidth() / 2, getHeight() / 2 - earnedActor.getHeight() / 2);
            action.setDuration(0.3f);
            earnedActor.setVisible(true);
            earnedActor.addAction(action);
            storedCoins = curScore / VarSource.scoreToCoins;
            localCurScore = curScore; //потому что далее мы будем его менять, но значение curScore нам еще пригодится
            addActor(earnedActor);
        } else {

            if (Math.round(earnedActor.getX()) == Math.round(getWidth() / 2 - earnedActor.getWidth() / 2)) {
                if (coonCoinsShowed < storedCoins) {
                    /**sound effect**/
                    if (!earnedSoundIsPlaying) {
                        SoundEffects.playSound(SoundEffects.earned, false);
                        earnedSoundIsPlaying = true;
                    }

                    if (localCurScore > VarSource.scoreToCoins) {
                        localCurScore -= VarSource.scoreToCoins;
                    }

                    coonCoinsShowed++;
                    ((Label) earnedActor.getItemsByLayer("text").first()).setText(Integer.toString(coonCoinsShowed));
                    curScoreLabel.setText(Integer.toString(localCurScore));
                } else {
                    /**sound effect**/
                    SoundEffects.earned.stop();

                    localCurScore = 0;
                    curScoreLabel.setText(Integer.toString(localCurScore));
                    if (!nextButton.isVisible()) {
                        nextButton.setVisible(true);
                        addActor(nextButton); //TODO: иначе выезжает кнопка некст для перехода на следующий экран
                    }
                }
            }
        }
    }

    static void hideAllMenus(){
        update = true;
        upgradesVisibility = false;

        windowIndex = -1;

        optionsPanelClicked = true;
        optionsBoxindex = 0;
    }

    private String unitTranslate(int unit) {

        //get biggest value
        if (max < unit) {
            max = unit;
        }

        return Integer.toString(max);
    }

    private void interfaceVisibility() {
        if (Main.state.equals(Main.State.MENU)) {
            /**if we have to update somethings**/
            if (update) {
                upgradesUpdate();
                highScoresMenuUpdate();
                helpUpdate();
                update = false;
            }
        }
        setMenuInterfaceVisibility();
    }

    /**
     * в меню показываем, в RUN скрываем и возвращаем на место
     **/
    private void setMenuInterfaceVisibility() {
        for (CompositeActor actor : menuInterfaceElements) {
            switch (Main.state) {
                case MENU:
                    if (actor.getX() > getWidth() - actor.getWidth() && actor.getX() <= getWidth() && actor.getY() > actor.getHeight() && actor.getY() < getHeight()) {
                        actor.setVisible(false);
                    }
                    break;
                case RUN:
                    if (actor.isVisible()) {
                        actor.setVisible(false);
                        Vector2 pos = (Vector2) actor.getUserObject();
                        actor.setX(pos.x);
                        actor.setY(pos.y);
                    }
                    break;
            }
        }
    }

    private void setButton() {
        if (Main.state != lastState) {
            switch (Main.state) {
                case PAUSE:
                    UsefullMethods.changeLayersVisibility(pauseActor, "resume", "Default");
                    break;
                case RUN:
                    UsefullMethods.changeLayersVisibility(pauseActor, "pause", "Default");
                    break;
                case TOPSCORES:
                    if (!pauseActor.isVisible()) {
                        pauseActor.setVisible(true);
                    }
                    UsefullMethods.changeLayersVisibility(pauseActor, "home", "Default");
                    restartButton.setVisible(true);
                    addActor(restartButton);
                    break;
                case EARNED:
                    if (pauseActor.isVisible()) {
                        pauseActor.setVisible(false);
                    }
                case MENU:

                    UsefullMethods.changeLayersVisibility(pauseActor, "resume", "Default");
                    break;
            }
            lastState = Main.state;
        }
    }


    private void upgradesUpdate() {
        /**else if already showed, we are checking for touch event**/
        if (windowIndex != VarSource.upgradeWindowIndex) {
            UsefullMethods.changeLayersVisibility(upgradeButton, "Default", "");
        } else {
            UsefullMethods.changeLayersVisibility(upgradeButton, "touched", "");
            buyButtonOnClick();
            manageUpgradesPages();
            //TODO: а если магазинчик видимый, то обновляем видимость слоев в соответствии с текущим индексом
        }
    }

    private void helpUpdate() {
        if (windowIndex != VarSource.helpWindowIndex) {
            if (optionsPanel.getVo().composite.layers.get(optionsPanel.getLayerIndex("helpTouched")).isVisible) {
                optionsPanel.setLayerVisibility("helpTouched", false);
            }
        } else {
            if (!optionsPanel.getVo().composite.layers.get(optionsPanel.getLayerIndex("helpTouched")).isVisible) {
                optionsPanel.setLayerVisibility("helpTouched", true);
            }
            manageHelpPages();
        }
    }

    private void manageHelpPages() {
        if (lastHelpPageIndex != helpPageIndex) {

            arrowScaleOnClick(lastHelpPageIndex, helpPageIndex, VarSource.helpKeys.length - 1, helpActor);

            UsefullMethods.changeLayersVisibility(helpActor, Integer.toString(helpPageIndex), new HashSet<String>(Arrays.asList(VarSource.helpStaticLayouts)));
            ((Label) helpActor.getItemsByLayer("text").first()).setText(VarSource.myBundle.format("help" + helpPageIndex));
            ((Label) helpActor.getItemsByLayer("header").first()).setText(VarSource.myBundle.format("helpHeader"));
            lastHelpPageIndex = helpPageIndex;
        }
    }

    private void buyButtonOnClick() {
        if (buyButtonClicked) {
            Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);
            int coonCoins = preferences.getInteger(VarSource.coonCoins, 0);
            int curLevel = preferences.getInteger(VarSource.upgradeKeys[upgradespageIndex], 0);
            int maxLevel = VarSource.upgradeMaxLvl[upgradespageIndex];
            /**if we not already on max lvl upgrade**//**and we have got enough coon coins**/
            if (curLevel < VarSource.upgradeMaxLvl[upgradespageIndex] && coonCoins >= VarSource.upgradeCosts[upgradespageIndex]) {
                /**sound effect**/
                SoundEffects.playSound(SoundEffects.buy, false);

                curLevel++;
                coonCoins -= VarSource.upgradeCosts[upgradespageIndex];
                preferences.putInteger(VarSource.upgradeKeys[upgradespageIndex], curLevel);
                preferences.putInteger(VarSource.coonCoins, coonCoins);
                preferences.flush();

                costAndLevelsUpdate();


                //TODO: сразу обновить количество изначальных бонусов или их продолжительность
                switch (upgradespageIndex) {
                    /**smoke**/
                    case 2:
                        smokeCount++;
                        break;
                    /**kunai**/
                    case 3:
                        kunaiCount++;
                        break;
                }
            } else {
                /**wrong sound effect**/
                SoundEffects.playSound(SoundEffects.cannot, false);
            }
            buyButtonClicked = false;
        }
    }

    private void costAndLevelsUpdate() {
        Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);
        int curLevel = preferences.getInteger(VarSource.upgradeKeys[upgradespageIndex], 0);
        int maxLevel = VarSource.upgradeMaxLvl[upgradespageIndex];

        if (curLevel < maxLevel) {
            ((Label) upgradesStoreActor.getItemsByLayer("cost").first()).setText(Integer.toString(VarSource.upgradeCosts[upgradespageIndex]));
        } else {
            ((Label) upgradesStoreActor.getItemsByLayer("cost").first()).setText("MAX");
        }

        ((Label) upgradesStoreActor.getItemsByLayer("curLevel").first()).setText("Lvl:" + Integer.toString(curLevel));
        ((Label) upgradesStoreActor.getItemsByLayer("maxLevel").first()).setText("Max:" + Integer.toString(maxLevel));
    }

    private void manageUpgradesPages() {
        if (lastUpgradesPageIndex != upgradespageIndex) {
            /**on touch scaling event**/
            arrowScaleOnClick(lastUpgradesPageIndex, upgradespageIndex, VarSource.upgradeKeys.length - 1, upgradesStoreActor);

            ((Label) upgradesStoreActor.getItemsByLayer("text").first()).setText(VarSource.myBundle.format("upgrades" + upgradespageIndex));
            ((Label) upgradesStoreActor.getItemsByLayer("header").first()).setText(VarSource.myBundle.format("upgradesHeader"));
            UsefullMethods.changeLayersVisibility(upgradesStoreActor, VarSource.upgradeKeys[upgradespageIndex], new HashSet<String>(Arrays.asList(VarSource.upgradeStoreStaticLayouts)));
            /**setting up cost label**/
            costAndLevelsUpdate();

            lastUpgradesPageIndex = upgradespageIndex;
        }
    }

    private void showHideWindows() {
        if (windowIndex != lastWindowIndex && Main.state == Main.State.MENU) {
            /**show**/
            if (windowIndex != -1) {
                /**show**/
                SoundEffects.playSound(SoundEffects.show, false);

                MoveToAction showAction = new MoveToAction();
                showAction.setPosition(getWidth() / 2, getHeight() * (1 - VarSource.upgradeBuyButtonBox[0].y));
                showAction.setDuration(0.3f);
                windowActorsArray.get(windowIndex).addAction(showAction);
                windowActorsArray.get(windowIndex).setVisible(true);
            }
            /**hide**/
            if (lastWindowIndex != -1) {
                /**sound effect**/
                SoundEffects.playSound(SoundEffects.hide, false);

                Vector2 position = (Vector2) windowActorsArray.get(lastWindowIndex).getUserObject();

                MoveToAction hideAction = new MoveToAction();
                hideAction.setPosition(position.x, position.y);
                hideAction.setDuration(0.3f);

                windowActorsArray.get(lastWindowIndex).addAction(hideAction);

            }
            lastWindowIndex = windowIndex;

        }
    }

    private void optionspanelOnClick() {
        if (optionsPanelClicked) {
            switch (optionsBoxindex) {
                /**hide button**/
                case 0:
                    if (optionsShowed) {
                        /**sound effect**/
                        SoundEffects.playSound(SoundEffects.hide, false);
                        /**hide action**/
                        MoveToAction hideAction = new MoveToAction();
                        hideAction.setPosition(getWidth(), getHeight() * (1 - VarSource.optionsPanelBox[0].y));
                        hideAction.setDuration(0.3f);
                        optionsPanel.addAction(hideAction);
                        optionsBoxindex = -1;
                        optionsShowed = false;
                    }
                    break;
                /**high scores button**/
                case 1:
                    if (optionsShowed) {
                        SoundEffects.playSound(SoundEffects.onclick, false);
                        windowIndex = isItTouched(windowIndex, VarSource.highScoresWindowIndex);
                    }
                    break;
                case 2:
                    if (optionsShowed) {
                        SoundEffects.playSound(SoundEffects.onclick, false);
                        soundStateChange();
                        optionsBoxindex = -1;
                    }
                    break;
                /**language change button**/
                case 3:
                    if (optionsShowed) {
                        SoundEffects.playSound(SoundEffects.onclick, false);
                        languageChange();
                        lastHelpPageIndex = -1;
                        lastUpgradesPageIndex = -1;
                        update = true;
                    }
                    break;
                /**show or help button**/
                case 4:
                    /**if panel showed, then we show help**/
                    if (optionsPanel.getX() == getWidth() - optionsPanel.getWidth() && optionsShowed) {
                        windowIndex = isItTouched(windowIndex, VarSource.helpWindowIndex);
                        helpPageIndex = 0;
                        SoundEffects.playSound(SoundEffects.onclick, false);
                    }
                    /**else if panel hided, we must just show it up**/
                    else if (optionsPanel.getX() == getWidth() && !optionsShowed) {
                        /**sound effect**/
                        SoundEffects.playSound(SoundEffects.show, false);
                        /**show action**/
                        MoveToAction showAction = new MoveToAction();
                        //showAction.setPosition(getWidth() * VarSource.optionsPanelBox[0].x, getHeight() * (1 - VarSource.optionsPanelBox[0].y));
                        showAction.setPosition(getWidth() - optionsPanel.getWidth(), getHeight() * (1 - VarSource.optionsPanelBox[0].y));
                        showAction.setDuration(0.3f);
                        optionsPanel.addAction(showAction);

                        optionsShowed = true;
                    }
                    optionsBoxindex = -1;
                    break;
            }
            optionsPanelClicked = false;
        }
    }

    private void languageChange() {
        Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);
        String curLang = preferences.getString(VarSource.language, VarSource.eng);

        /**if current language is english**/
        if (curLang.equals(VarSource.eng)) {
            preferences.putString(VarSource.language, VarSource.rus);
            optionsPanel.setLayerVisibility("languageRu", true);
            optionsPanel.setLayerVisibility("languageEn", false);
        } else {
            preferences.putString(VarSource.language, VarSource.eng);
            optionsPanel.setLayerVisibility("languageRu", false);
            optionsPanel.setLayerVisibility("languageEn", true);
        }
        preferences.flush();

        /**refresh bundle**/
        Main.bundleHandle();
    }

    /**
     * turn sound on/off
     **/
    private void soundStateChange() {
        SoundEffects.soundOn = !SoundEffects.soundOn;
        Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);
        preferences.putBoolean(VarSource.soundOn, SoundEffects.soundOn);
        preferences.flush();

        optionsPanel.setLayerVisibility("soundon", SoundEffects.soundOn);
    }

    private void highScoresMenuUpdate() {
        if (windowIndex != VarSource.highScoresWindowIndex) {
            if (optionsPanel.getVo().composite.layers.get(optionsPanel.getLayerIndex("hsTouched")).isVisible) {
                optionsPanel.setLayerVisibility("hsTouched", false);
            }
        } else {
            if (!optionsPanel.getVo().composite.layers.get(optionsPanel.getLayerIndex("hsTouched")).isVisible) {
                optionsPanel.setLayerVisibility("hsTouched", true);
            }
            highScoresUpdate();
        }
    }

    private void highScoresUpdate() {
        Label label;
        ((Label) highScoresActor.getItemsByLayer("header").first()).setText(VarSource.myBundle.format("highscoresHeader"));
        //fill labels
        for (int i = 0; i < 5; i++) { //5 because we show just top5 records
            label = (Label) highScoresActor.getItem(VarSource.topScores[i]);

            int scr = Gdx.app.getPreferences(VarSource.prefSettings).getInteger(VarSource.topScores[i], 0);
            label.setText(Integer.toString(scr));
            if (scr == Gdx.app.getPreferences(VarSource.prefSettings).getInteger(VarSource.lastBestScore, 0) && scr == curScore) {
                highScoresActor.setLayerVisibility("last", true);
                Actor last = highScoresActor.getItemsByLayer("last").first();
                last.setY(label.getY() + label.getHeight() / 2 - last.getHeight() / 2);
            }
        }
    }

    //show table with high scores and set action for it
    private void showHighScores() {
        highScoresUpdate();

        //setting up action for our table
        MoveToAction action = new MoveToAction();
        action.setPosition(getWidth() / 2, getHeight() / 4);
        action.setDuration(0.3f);

        highScoresActor.addAction(action);
        highScoresActor.setVisible(true);
    }

    private void updateBonusCount() {
        ((Label) smokeActor.getItemsByLayer("text").first()).setText(Integer.toString(smokeCount));
        ((Label) kunaiActor.getItemsByLayer("text").first()).setText(Integer.toString(kunaiCount));
    }

    static void timeOn(Timer.Task task) {
        float seconds = 0;
        int additiveDur = 0;

        /**glider task on**/
        if (task == gliderTask) {
            /**sound effect**/
            SoundEffects.playSound(SoundEffects.glider, false);

            PlayerStage.transportModifer = PlayerStage.TransportModifer.glider;
            additiveDur = Gdx.app.getPreferences(VarSource.prefSettings).getInteger(VarSource.upgradeKeys[1]);
        }
        /**else clock task on**/
        else {
            if (task == clockTask) {
                /**sound effect**/
                SoundEffects.playSound(SoundEffects.clockStart, false);

                PlayerStage.transportModifer = PlayerStage.TransportModifer.clock;
                additiveDur = Gdx.app.getPreferences(VarSource.prefSettings).getInteger(VarSource.upgradeKeys[0]);
            }
        }
        countDownActor.setVisible(true);

        /**if task already sheduled, we just continue it**/
        synchronized (task) {
            if (task.isScheduled()) {
                seconds = ((task.getExecuteTimeMillis() - TimeUtils.nanoTime() / 1000000) / 1000) % 60;
                Timer.instance().clear();
            }
        }

        Timer.schedule(task, VarSource.defClockDuration + additiveDur + seconds);

    }

    private static void countDownUpdate() {

        if (clockTask.isScheduled()) {
            ((Label) countDownActor.getItemsByLayer("Default").first()).setText(sdf.format(new Date(clockTask.getExecuteTimeMillis() - TimeUtils.nanoTime() / 1000000)));
        } else if (gliderTask.isScheduled()) {
            ((Label) countDownActor.getItemsByLayer("Default").first()).setText(sdf.format(new Date(gliderTask.getExecuteTimeMillis() - TimeUtils.nanoTime() / 1000000)));
        }
    }


    static int isItTouched(int a, int b) {
        if (a == b) {
            return -1;
        } else return b;
    }


    /**
     * scale event on touch ewent
     **/
    private void scaleButtonsEvent(Actor actor) {

        ScaleToAction scaleUpAction = new ScaleToAction();
        scaleUpAction.setScale(1.2f);
        scaleUpAction.setDuration(0.05f);

        ScaleToAction scaleDownAction = new ScaleToAction();
        scaleDownAction.setScale(1f);
        scaleDownAction.setDuration(0.05f);

        SequenceAction sequenceAction = new SequenceAction(scaleUpAction, scaleDownAction);

        actor.addAction(sequenceAction);
    }

    private void arrowScaleOnClick(int lastIndex, int index, int maxIndex, CompositeActor actor) {
        if (lastIndex != -1) {
            if (lastIndex < index) {
                if ((lastIndex == 0 && index == maxIndex)) {
                    scaleButtonsEvent(actor.getItem("prevArrow"));
                } else {
                    scaleButtonsEvent(actor.getItem("nextArrow"));
                }
            } else {
                if ((lastIndex == maxIndex && index == 0)) {
                    scaleButtonsEvent(actor.getItem("nextArrow"));
                } else {
                    scaleButtonsEvent(actor.getItem("prevArrow"));
                }
            }
        }
    }

   /* private static int timeFormat(long millis){
       int days = (int) task.getExecuteTimeMillis()/1000*60*60*24;
    }*/
}
