package com.hedgecoon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vasily on 30.05.2016.
 */
public class VarSource {

    //world
    public static final Vector2 gravity = new Vector2(0, -60f);
    public static final Vector2 wUnitMax = new Vector2(100, 60);

    //player
    public static final float hedgeDensity = 1.5f;
    public static final float sightlength = 60;
    public static final float hedgeRadius = 1.5f;

    //rope
    public static final float ropeSegmentSize = 0.5f;
    public static final float ropeSegmentDensity = 5;
    public static final int maxSegmentCount = 30;

    //obstacles
    public static int obsPerScreen = 1;
    public static int maxObsPerScreen = 5;
    public static final float spikeWidth = 2f;
    public static final float defSpikeHeight = 28f;//29.25f;
    public static final short CATEGORY_HEDGE = 0x0002;
    public static final short CATEGORY_SPIKES = 0x0004;
    public static final short CATEGORY_ROPE = 0x0008;
    static final short MASK_SPIKES = CATEGORY_HEDGE | CATEGORY_ROPE;
    static final Vector2 startTombPos = new Vector2(37, 0);
    static final int maxObsInRaw = 3;

    //screen settings
    public static int viewportWidth;
    public static int viewportHeight;
    public static float aspectRatio;
    static float resMultiplier;

    //preferences
    static final String prefSettings = "hc-settings";
    static final String bestScoreKey = "bestScore";
    static final String[] topScores = {"top1", "top2", "top3", "top4", "top5"};
    static final String lastBestScore = "lastBestScore";
    static final String coonCoins = "coonCoins";
    static final String curLevel = "curLevel";
    static final String eng = "en";
    static final String rus = "ru";
    static final String language = "language";
    static final String soundOn = "soundOn";
    static final String firstRun = "firstRun";
    static final int adsShowFreq = 2;

    static final int scoreToCoins = 100;


    //difficult
    static int difficult = 1;
    static int cocoonStartSpawnDiff = 1;
    static int maxDifficult = 6;

    //static final float[] dif1Range = {500,1000,1500,2000,4000};
    private static final float defDiffStep = 100;
    static final float[] dif1Range = {defDiffStep, defDiffStep * 15, defDiffStep * 24, defDiffStep * 30, defDiffStep * 36, defDiffStep * 45};
    static final float[] cocoonFreq = {0, 3, 2, 1, 1, 1, 1};

    //speed
    static float fastSpeed = 60;
    static float mediumSpeed = 40;

    //bonus
    static final float bonusFrequence = 400;
    static final String[] bonusKeys = {"clock", "glider", "smoke", "kunai"};
    static final int[] bonusSpawnChance = {30, 20, 80, 80};
    static final int defClockDuration = 5;
    static final int defGliderDuration = 5;

    //slow time
    static float timeSpeed = 1;

    //glider
    static final Vector2 gliderSpeed = new Vector2(100, 0);

    //screen layout
    static final Vector2[] startButtonBox = {new Vector2(0.03f, 0.97f), new Vector2(0.12f, 0.82f)};
    //static Vector2[] upgradePrevButtonBox = {new Vector2(0.29f, 0.59f), new Vector2(0.37f, 0.45f)};
    static Vector2[] upgradePrevButtonBox;
    //static Vector2[] upgradeNextButtonBox = {new Vector2(0.63f, 0.59f), new Vector2(0.71f, 0.45f)};
    static Vector2[] upgradeNextButtonBox;
    static final Vector2[] upgradeStoreButtonBox = {new Vector2(0.838f, 0.30f), new Vector2(0.98f, 0.18f)};
    static final Vector2[] smokeBox = {new Vector2(0.01f, 0.40f), new Vector2(0.98f, 0.28f)};
    static final Vector2[] kunaiBox = {new Vector2(0.01f, 0.50f), new Vector2(0.98f, 0.38f)};
    //static Vector2[] upgradeBuyButtonBox = {new Vector2(0.43f, 0.72f), new Vector2(0.56f, 0.65f)};
    static Vector2[] upgradeBuyButtonBox;
    static final Vector2[] upgradePrevBox = {new Vector2(0,0.36f),new Vector2(0.2f,0.63f)};
    static final Vector2[] upgradeNextBox = {new Vector2(0.8f,0.36f), new Vector2(1,0.63f)};
    static final Vector2[] upgradeBuyBox = {new Vector2(0.35f,0), new Vector2(0.64f,0.1f)};


    static int bannerHeight;

    static final Vector2[] restartAndNexButtonBox = {new Vector2(0.46f, 0.97f), new Vector2(0.54f, 0.82f)};
    static Vector2[] optionsPanelBox = new Vector2[2];
   /* private static Vector2 optionsShowHide = new Vector2(0.575f,0.66f);
    private static Vector2 optionsHighScores = new Vector2(0.661f,0.745f);
    private static final Vector2 optionsSound = new Vector2(0.746f,0.83f);
    private static final Vector2 optionsLanguage = new Vector2(0.831f,0.915f);
    private static final Vector2 optionsHelp = new Vector2(0.916f,1f); */
    //static final Vector2[] optionsBoxes = {optionsShowHide,optionsHighScores,optionsSound,optionsLanguage,optionsHelp};
    static Vector2[] optionsBoxes = new Vector2[5];

    static final int upgradeWindowIndex = 0;
    static final int highScoresWindowIndex = 1;
    static final int languageWindowIndex = 2;
    static final int helpWindowIndex = 2;

    //help
    static final String[] helpKeys = {"0", "1", "2", "3", "4", "5", "6"};

    //upgrades
    static final String[] upgradeKeys = {"clocks", "glider", "smoke", "kunai"};
    static final String[] upgradeStoreStaticLayouts = {"text","header","cost", "buy", "maxLevel", "curLevel", "prev", "next", "Default"};
    static final String[] helpStaticLayouts = {"text","header","prev", "next", "Default"};
    static final int[] upgradeMaxLvl = {5, 5, 15, 15};
    static final int[] upgradeCosts = {1000, 2000, 1000, 1000};

    //bundle
    static I18NBundle myBundle;

    //font
    static BitmapFont shadowFont;
    static final String FONT_PATH = "freetypefonts/Shadow.ttf";

    //resolutions handle
    static int[] resWidths = {480,800,1024,1280,1920,2400};
    static int[] resHeights = {320,480,600,720,1200,1600};
}
