package com.hedgecoon.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.data.CompositeItemVO;
import com.uwsoft.editor.renderer.data.SpriteAnimationVO;
import com.uwsoft.editor.renderer.resources.IResourceRetriever;
import com.uwsoft.editor.renderer.scene2d.CompositeActor;

import java.util.ArrayList;

/**
 * Created by Vasily on 18.09.2016.
 */
public class CustomCompositeActor extends CompositeActor {
    public enum States{
        start,
        stop
    }

    public States state;

    public CustomCompositeActor(CompositeItemVO vo, IResourceRetriever ir) {
        super(vo, ir);
        state = States.stop;
    }

    @Override
    protected void build(CompositeItemVO vo, BuiltItemHandler itemHandler, boolean isRoot) {
        buildSpriteAnimations(vo.composite.sSpriteAnimations, itemHandler);
        super.build(vo, itemHandler, isRoot);
    }

    protected void buildSpriteAnimations(ArrayList<SpriteAnimationVO> animations, BuiltItemHandler itemHandler) {
        for (int i = 0; i < animations.size(); i++) {
            Array<TextureAtlas.AtlasRegion> region = ir.getSpriteAnimation(animations.get(i).animationName).getRegions();
            Animation animation = new Animation(1f / animations.get(i).fps, region);
            AnimatedImage actor = new AnimatedImage(animation,animations.get(i).playMode);
            processMain(actor, animations.get(i));
            addActor(actor);

            itemHandler.onItemBuild(actor);
        }
    }
}
