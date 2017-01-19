package com.hedgecoon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

import java.util.HashSet;

/**
 * Created by Vasily on 03.06.2016.
 */
public class UsefullMethods {
    public static final char x = 'x';
    public static final char y = 'y';
    public static float pixToWorldUnits(char ch, float screenPix){
        float touchV = 0;
        switch (ch){
            case x:
                touchV = screenPix / (Gdx.graphics.getWidth() / VarSource.wUnitMax.x);
                break;
            case y:
                touchV = (Gdx.graphics.getHeight() - screenPix) / (Gdx.graphics.getHeight() / VarSource.wUnitMax.y);
                break;
        }
        return touchV;
    }

    static float wUnitToPix(char ch, float wUnit){
        float screenPos = 0;
        switch (ch){
            case x:
                screenPos = wUnit* (VarSource.viewportWidth/ VarSource.wUnitMax.x);
                break;
            case y:
                screenPos = (wUnit*(VarSource.viewportHeight/ VarSource.wUnitMax.y));
                break;
        }
        return Math.round(screenPos);
    }

    static void changeLayersVisibility(CompositeActor actor, String layerName, String except){
        for (LayerItemVO layer : actor.getVo().composite.layers){
            if (layer.layerName.equals(layerName) || (layer.layerName.equals(except) && except.length()!=0)){
                actor.setLayerVisibility(layer.layerName,true);
            }
            else {
                actor.setLayerVisibility(layer.layerName,false);
            }
        }
    }

    static void changeLayersVisibility(CompositeActor actor, String layerName, HashSet<String> set){
        for (LayerItemVO layer : actor.getVo().composite.layers){
            if (layer.layerName.equals(layerName) || (set.contains(layer.layerName) && set.size()!=0)){
                actor.setLayerVisibility(layer.layerName,true);
            }
            else {
                actor.setLayerVisibility(layer.layerName,false);
            }
        }
    }

    static int indexCountUpdate(int i, int arraySize){
        if (i>=arraySize){
            i = 0;
        }
        else {
            i +=1;
        }

        return i;
    }

    static int randomSpawnChance(int[] items){
        int range = 0;
        for (int i = 0; i < items.length; i++)
            range += items[i];

        int rand = MathUtils.random(range);
        int top = 0;
        int index = 0;

        for (int i = 0; i < items.length; i++) {
            top += items[i];
            if (rand < top){
                index = i;
                break;
            }
        }

        return index;
    }
}
