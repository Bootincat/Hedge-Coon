package com.hedgecoon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.uwsoft.editor.renderer.resources.FontSizePair;
import com.uwsoft.editor.renderer.resources.ResourceManager;

import java.io.File;

/**
 * Created by Vasily on 18.09.2016.
 */
public class CustomResourceManager extends ResourceManager {

    @Override
    public void loadSpriteAnimations() {
        spriteAnimNamesToLoad.add("paws");
        spriteAnimNamesToLoad.add("webhedge");
        spriteAnimNamesToLoad.add("blinking");
        spriteAnimNamesToLoad.add("eyes");
        spriteAnimNamesToLoad.add("webcrush");
        spriteAnimNamesToLoad.add("airstrip");
        spriteAnimNamesToLoad.add("smoke");
        spriteAnimNamesToLoad.add("eggs");
        spriteAnimNamesToLoad.add("bats");
        VarSource.resMultiplier = resMultiplier;
        super.loadSpriteAnimations();
    }

    @Override
    public void loadFont(FontSizePair pair) {
        String FONT_CHARS = "";
        for( int i = 32; i < 127; i++ ) FONT_CHARS += (char)i; // цифры и весь английский
        for( int i = 1024; i < 1104; i++ ) FONT_CHARS += (char)i; // русские

        FileHandle fontFile;
        fontFile = Gdx.files.internal(fontsPath + File.separator + pair.fontName + ".ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.size = Math.round(pair.fontSize * resMultiplier);
        parameter.characters = FONT_CHARS;

        BitmapFont font = generator.generateFont(parameter);
        bitmapFonts.put(pair, font);
    }
}
