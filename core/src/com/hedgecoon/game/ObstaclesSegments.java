package com.hedgecoon.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import java.util.ArrayList;

/**
 * Created by Vasily on 23.06.2016.
 */
/*
this class is for dynamical creating obstacles
*/
public class ObstaclesSegments {
    final int obstTypes = 3;
    int[] positionsX;
    float midleX, midleY;
    final float indentX = 5f;
    final float indentY = 10f;
    final float lowerSpikeY = 15f;
    final float higherSpikeY = 30f;
    final float minScale = 0.6f;

    public ObstaclesSegments() {
        midleX = VarSource.wUnitMax.x / 2;
        midleY = VarSource.wUnitMax.y / 2;
    }

   /* creating a new obstacles with randomly position and size*/
   //allignmen equals -1 or 1 that means top or bottom placement
    public ArrayList<Body> createSpikes(Vector2 hedgePos, int allignment) {
        //final array with bodyDefs for creating
        ArrayList<Body> bodies = new ArrayList<Body>();

        for (int i = 0; i < VarSource.maxObsPerScreen; i++) {
            float scale = (minScale + (i * 0.1f)) * allignment;
            //physic body
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(10,0);
            //bodyDef.position.x = hedgePos.x + midleX + (VarSource.wUnitMax.x / VarSource.obsPerScreen) * i;

            Body body = WorldCreator.curWorld.createBody(bodyDef);
            body.createFixture(createSpikeFixture(scale));
            CustomData data = new CustomData();
            data.scale = scale;
            body.setUserData(data);

            bodies.add(body);
        }

        return bodies;
    }

    public FixtureDef createSpikeFixture(float scale) { // this method is for creating triangle with randomly height

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.restitution = 0;
        fixtureDef.friction = 0;
        fixtureDef.density = 1;

        //setting shape
        Vector2[] vertices = new Vector2[3]; // 3 cause we make triangle
        vertices[0] = new Vector2(-VarSource.spikeWidth, 0);
        vertices[1] = new Vector2(0, VarSource.defSpikeHeight * scale);
        vertices[2] = new Vector2(VarSource.spikeWidth, 0);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);


        fixtureDef.shape = polygonShape;



        fixtureDef.filter.categoryBits = VarSource.CATEGORY_SPIKES;
        //fixtureDef.filter.maskBits = VarSource.MASK_SPIKES;

        return fixtureDef;
    }
/*
    public ArrayList<Body> createCliff(float x) {
        int amount = MathUtils.random(0, 2);
        ArrayList<Body> bodies = new ArrayList<Body>();

        float margin = 0;
        float prevObsX = 0; // previous obstacle position + it's width
        float middlePoint = midleX / amount;

        for (int i = 0; i < amount; i++) {

            float widthScale = MathUtils.random(0.5f, 1f);
            float halfCurWidth = (VarSource.defCliffWidth * widthScale) / 2;

            //body definition
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            float randomX = MathUtils.random(x + midleX + 2 * (middlePoint * i) + middlePoint - (middlePoint - halfCurWidth), x + midleX + 2 * (middlePoint * i) + middlePoint + (middlePoint - halfCurWidth));
            bodyDef.position.set(randomX, VarSource.wUnitMax.y - halfCurWidth / 2);
            Body body = WorldCreator.curWorld.createBody(bodyDef);

            //fixture definition

            prevObsX = body.getPosition().x + halfCurWidth * 2;
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 0;
            fixtureDef.restitution = 0;
            fixtureDef.friction = 0;

            Vector2[] vertices = new Vector2[4];
            vertices[0] = new Vector2(-halfCurWidth, halfCurWidth / 2); //height equals half of a width
            vertices[1] = new Vector2(-(halfCurWidth / 2), 0);
            vertices[2] = new Vector2((halfCurWidth / 2), 0);
            vertices[3] = new Vector2(halfCurWidth, halfCurWidth / 2);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(vertices);

            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            bodies.add(body);

            //WorldCreator.cliffsEntity.add(gCreator.createGraphic("cliffs",new Vector2(widthScale,widthScale/2),bodyDef.position.x - halfCurWidth,bodyDef.position.y));

            polygonShape.dispose();
        }

        return bodies;
    }*/

  /*  public Body createBonus(float x) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        int i = MathUtils.random(0, 4);
        float startPoint = x + midleX;

        float randomX = MathUtils.random(
                startPoint + i * (VarSource.wUnitMax.x / VarSource.obsPerScreen) + spikeWidth * 4,
                startPoint + (i + 1) * (VarSource.wUnitMax.x / VarSource.obsPerScreen) - spikeWidth * 4);

        float randomY = MathUtils.random(lowerSpikeY + 12, VarSource.wUnitMax.y - 2 - VarSource.defCliffWidth / 2);  // 2 is margin

        bodyDef.position.set(randomX, randomY);
        Body body = WorldCreator.curWorld.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0;
        fixtureDef.density = 0;
        fixtureDef.restitution = 0;
        fixtureDef.shape = circle;

        body.createFixture(fixtureDef).setSensor(true);

        circle.dispose();

        return body;
    }*/
}
