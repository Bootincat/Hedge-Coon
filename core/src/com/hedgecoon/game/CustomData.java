package com.hedgecoon.game;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasily on 17.06.2016.
 */
public class CustomData {
    boolean toDelete;
    float scale;
    boolean cocoon;
    boolean web;
    boolean collected;

    Map<String,Boolean> bonusTypes;


    public CustomData() {
        toDelete = false;
        scale = 0;
        cocoon = false;
        web = false;

        //bonus
        bonusTypes = new HashMap<String, Boolean>(VarSource.bonusKeys.length);

        for (String s : VarSource.bonusKeys){
            bonusTypes.put(s, false);
        }

    }
}
