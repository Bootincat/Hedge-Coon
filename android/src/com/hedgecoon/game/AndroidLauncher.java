package com.hedgecoon.game;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.hedgecoon.game.AdsHandler;
import com.hedgecoon.game.Main;


public class AndroidLauncher extends AndroidApplication implements AdsHandler {

    private final int SHOW_ADS = 1;
    private final int HIDE_ADS = 0;

    protected AdView adView;

    /**admob handler**/
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_ADS:
                    adView.setVisibility(View.VISIBLE);
                    break;
                case HIDE_ADS:
                    adView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));//admob initialize

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

        /*****main layout**/
        RelativeLayout layout = new RelativeLayout(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);


        /**game view**/
        View gameView = initializeForView(new Main(this), config);

        ViewGroup.LayoutParams gameParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layout.addView(gameView, gameParams);

        /***admob view**/
        adView = new AdView(this);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Main.state = Main.State.PAUSE;//pausing game iterations
            }
        });

        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(getString(R.string.banner_id));

        AdRequest adRequest = new AdRequest.Builder().build();//request new ad from server
        adView.loadAd(adRequest);

        /**adView setting up**/
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adView.setVisibility(View.GONE);

        layout.addView(adView, adParams);

        setContentView(layout);//display main layout
    }

    /**ads visible handling**/
    @Override
    public void showAds(boolean show) {
        handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
    }
}
