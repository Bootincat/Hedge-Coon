package com.hedgecoon.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Main extends ApplicationAdapter {

    private WorldCreator worldCreator;
    private IResourceRetriever ir;
    static boolean firstitter;
    private boolean restarted;
    private boolean scoreUpdated;

    /**countdown handling when game is paused**/
    private boolean paused;
    private long pausedTime;
    private boolean resumed;

    /**stages instances**/
    private UIstage uIstage;
    private BGstage bGstage;
    private PlayerStage pStage;

    /**main camera**/
    private OrthographicCamera cam;
    private Vector2 cameraPos;
    private float maxCurrentX;//max current player X position in world units

    private SoundEffects soundEffects;

    /**viewport**/
    private Viewport viewport;
    private int vpUpdtW;//viewport update width
    private int vpUpdtH;//viewport update height

    /**admob**/
    private static AdsHandler adsHandler;
    private static boolean showAds;

    /**game states**/
    public enum State {
        PAUSE,
        RUN,
        MENU,
        LOST,
        EARNED,
        TOPSCORES,
        RESTART,
    }

    public static State state;
    static State stateAfterRestart;

    public Main(AdsHandler adsHandler) {
        Main.adsHandler = adsHandler;
    }

    @Override
    public void create() {
        maxCurrentX = 0;
        pausedTime = 0;
        resumed = false;
        paused = false;

        /**input processing**/
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new MyInputProcessor());
        multiplexer.addProcessor(new UIInputProcessor());
        Gdx.input.setInputProcessor(multiplexer);

        /**loading assets**/
        CustomResourceManager rm = new CustomResourceManager();
        rm.packResolutionName = resolution_choice();
        rm.initAllResources();
        SceneLoader sl = new SceneLoader(rm);
        sl.loadScene("MainScene"); // loading scene from overlap2d

        /**main viewport setting up**/
        viewport = new FillViewport(100, 60);

        //vpUpdtW = Gdx.graphics.getWidth();
        //vpUpdtH = Gdx.graphics.getHeight();

        vpUpdtW = VarSource.viewportWidth;
        vpUpdtH = VarSource.viewportHeight;

        cam = (OrthographicCamera) viewport.getCamera();

        worldCreator = new WorldCreator();//world handler class

        ir = sl.getRm();//overlap2d's  resource manager

        cameraPos = new Vector2();//camera position, will be defined later

        uIstage = new UIstage(ir);//GUI stage instance

        bGstage = new BGstage(ir);//background stage instance

        pStage = new PlayerStage(ir);//player stage instance

        firstitter = true;//is it first iteration? Yes

        restarted = false;//is it restarted now?

        scoreUpdated = false;//score table updated?

        state = State.MENU;//first game state is MENU of course
        stateAfterRestart = null;//cause it is first iteration

        bundleHandle();//handle bundle with locale user languages

        soundEffects = new SoundEffects();  //create  an instance of sound effects class

        showAds = false;
    }

    @Override
    public void render() {

        switch (state) {
            case RUN:
                /**countdown management**/
                if (!resumed) {
                    resume();
                }

                if (restarted) {
                    worldCreator.initBodies();
                    restarted = false;
                    scoreUpdated = false;
                }

                defaultDraw();//media iteration

                worldCreator.act();//world iteration

                camSet();//setting up camera
                break;
            case PAUSE:
                defaultDraw();
                /**countdown management**/
                if (!paused) {
                    pause();
                }
                break;
            case MENU:
                /**reset all stuff**/
                if (restarted) {
                    worldCreator.initBodies();
                    restarted = false;
                    scoreUpdated = false;
                }

                if (firstitter) {
                    worldCreator.initBodies();
                    firstitter = false;
                }
                defaultDraw();
                break;

            case LOST:
                defaultDraw();
                state = State.EARNED;
                break;

            case EARNED:
                if (!scoreUpdated) {
                    setBestScore();//update best scores table in preferences
                    coonCoinsUpdate();//game currency
                    scoreUpdated = true;//is score updated?
                }
                defaultDraw();
                break;

            case TOPSCORES:
                defaultDraw();
                break;

            case RESTART:
                reset();//reset all stuff

                /**next state handling**/
                if (stateAfterRestart != null) {
                    switch (stateAfterRestart) {
                        case MENU:
                            state = State.MENU;
                            break;
                        case RUN:
                            PlayerStage.changeState(PlayerStage.PlayerState.start);
                            PlayerStage.hedgeActor.state = CustomCompositeActor.States.start;
                            state = State.RUN;
                            break;
                    }
                    /**ads control**/
                    showAds = !showAds;
                    adsHandler.showAds(showAds);
                    stateAfterRestart = null;
                }

                break;
        }
    }

    private void reset() {
        VarSource.difficult = 1;

        VarSource.timeSpeed = 1;

        Timer.instance().clear();//timer instance reset

        worldCreator = new WorldCreator();//world handler class

        cameraPos = new Vector2();//camera position reset

        maxCurrentX = 0;//max player X position reset

        uIstage = new UIstage(ir);//GUI stage instance reset

        bGstage = new BGstage(ir);//background stage instance reset

        pStage = new PlayerStage(ir);//player stage instance reset

        restarted = true;//is it restarted now?


    }

    private void defaultDraw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /**background stage update**/
        bGstage.getViewport().apply();
        bGstage.act(Gdx.graphics.getDeltaTime() * VarSource.timeSpeed);
        bGstage.draw();

        /**player stage update**/
        pStage.getViewport().apply();
        pStage.act(Gdx.graphics.getDeltaTime() * VarSource.timeSpeed);
        pStage.draw();

        /**GUI stage update**/
        uIstage.getViewport().apply();
        uIstage.act();
        uIstage.draw();

        /**main camera viewport update**/
        viewport.apply();
        viewport.update(vpUpdtW, vpUpdtH);
        camSet();

        /**sound effects update**/
        soundEffects.act();
    }

    private void camSet() {
        cameraPos.x = WorldCreator.hedge.getPosition().x;//player position in world units
        if (maxCurrentX < cameraPos.x) {
            maxCurrentX = cameraPos.x;//max current X position update
        }
        cameraPos.x = maxCurrentX;
        cameraPos.y = cam.viewportHeight / 2;//centered camera

        /**update camera position**/
        cam.position.set(cameraPos, 0);
        cam.update();
    }

    /**
     * this method update best score table in preferences and sorte values
     **/
    private void setBestScore() {
        Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);//game preferences

        ArrayList<Integer> scores = new ArrayList<Integer>(5);//scores array

        for (int i = 0; i < 5; i++) {
            scores.add(preferences.getInteger(VarSource.topScores[i], 0));//filling array with scores
        }

        Collections.sort(scores);
        Collections.reverse(scores);

        /**if score is can be in top scores table**/
        if (UIstage.curScore > scores.get(scores.size() - 1)) {

            preferences.putInteger(VarSource.lastBestScore, UIstage.curScore);

            /**find closet value in table**/
            for (int i = 0; i < scores.size(); i++) {
                if (UIstage.curScore > scores.get(i)) {

                    /**creating sub list of an array**/
                    ArrayList<Integer> subList = new ArrayList<Integer>(scores.subList(i, scores.size()));

                    scores.set(i, UIstage.curScore);


                    if (subList.size() != 0) {
                        /**delete values that bigger then our current best score**/
                        scores.removeAll(subList);
                        /**shifting table's values**/
                        scores.addAll(subList);

                        /**trim array to correct size**/
                        while (scores.size() > 5) {
                            scores.remove(scores.size() - 1);
                        }
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            preferences.putInteger(VarSource.topScores[i], scores.get(i));
        }

        preferences.flush();//update preferences
    }

    /**
     * this method update game currency value
     **/
    private void coonCoinsUpdate() {
        Preferences preferences = Gdx.app.getPreferences(VarSource.prefSettings);//get preferences
        int coonCoins = preferences.getInteger(VarSource.coonCoins, 0);
        coonCoins += UIstage.curScore / VarSource.scoreToCoins;//calculating new value of coon-coins
        preferences.putInteger(VarSource.coonCoins, coonCoins);
        preferences.flush();//update preferences

    }

    @Override
    public void pause() {
        /**stopping countdown and save current time in nano seconds**/
        if (!paused) {
            resumed = false;
            paused = true;
            pausedTime = TimeUtils.nanoTime();
            Timer.instance().stop();
        }
        super.pause();
    }

    @Override
    public void resume() {
        if (!resumed) {
            /**resuming countdown and adding pause delay**/
            Timer.instance().delay((TimeUtils.nanoTime() / 1000000) - (pausedTime / 1000000));
            Timer.instance().start();
            pausedTime = 0;
            resumed = true;
            paused = false;
        }
        super.resume();
    }

    /**
     * set the viewport size before loading main resources
     **/
    private void viewportSettingUp(int w, int h) {
        VarSource.viewportWidth = w;
        VarSource.viewportHeight = h;
    }

    /**
     * language handling
     **/
    static void bundleHandle() {
        FileHandle baseFileHandle = Gdx.files.internal("MyBundle");
        String lang = Gdx.app.getPreferences(VarSource.prefSettings).getString(VarSource.language, VarSource.eng);
        Locale locale = new Locale(lang);
        VarSource.myBundle = I18NBundle.createBundle(baseFileHandle, locale);
    }


    /**
     * in this method we are choosing closest resolution to loading asset packs
     **/
    private String resolution_choice() {

        int dotsCount = Gdx.graphics.getWidth() * (Gdx.graphics.getHeight());

        int top = 0;
        /**find closets one**/
        for (int i = 0; i < VarSource.resWidths.length - 1; i++) {
            if (dotsCount < VarSource.resWidths[i] * VarSource.resHeights[i]) {

                break;
            } else {
                top = i;
            }
        }

        viewportSettingUp(VarSource.resWidths[top], VarSource.resHeights[top]);//setting up viewports sizes

        return Integer.toString(VarSource.resWidths[top]) + "x" + Integer.toString(VarSource.resHeights[top]);//return pack name

    }


}
