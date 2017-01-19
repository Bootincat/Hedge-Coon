package com.hedgecoon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Vasily on 30.05.2016.
 */
public class WorldCreator {
    public static World curWorld;
    public static Body hedge;
    static Body bonus;
    public static Vector2 collision = new Vector2();
    public static Fixture curFixture;
    public static Body[] segments;
    public static RevoluteJoint[] joints;
    public static RopeJoint[] dJoints;
    public static ObstaclesSegments obstacleCreator;
    public static float deltaRange;
    public static ArrayList<Body> topSpikes1;
    public static ArrayList<Body> topSpikes2;
    public static ArrayList<Body> botSpikes1;
    public static ArrayList<Body> botSpikes2;
    public static ArrayList<Body> roof;
    public static boolean clicked;
    public static final Vector2 nullVec = new Vector2(0, 0);
    public static int curRoofElement;
    public static boolean evenManage = false;
    //cocoones
    private static float lastCocoonX;
    private static float lastBonusX;
    static int curLineSegment;

    static int topCount;
    static int botCount;

    //glider
    static Body curGliderObstacle;
    static Body lastGliderObstacle;

    public static RayCastCallback rayCastCallback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

            if (fixture.isSensor()) {
                return -1;
            }
            collision = point.cpy();
            curFixture = fixture;

            return fraction;
        }
    };

    private static RayCastCallback gliderRayCast = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.isSensor()) {
                return -1;
            }
            CustomData data = (CustomData) fixture.getBody().getUserData();
            if (data != null) {
                if (data.scale != 0) {
                    curGliderObstacle = fixture.getBody();
                }
            }

            return fraction;
        }
    };

    public WorldCreator() {
        deltaRange = 0;
        //cocoon spikes
        lastCocoonX = 0;
        lastBonusX = 0;
        curLineSegment = 0;

        curWorld = new World(VarSource.gravity, true);
        obstacleCreator = new ObstaclesSegments();
        joints = null;
        dJoints = null;
        topSpikes1 = new ArrayList<Body>(VarSource.obsPerScreen);
        topSpikes2 = new ArrayList<Body>(VarSource.obsPerScreen);
        botSpikes1 = new ArrayList<Body>(VarSource.obsPerScreen);
        botSpikes2 = new ArrayList<Body>(VarSource.obsPerScreen);
        curWorld.setContactListener(new MyContactListener());
        curRoofElement = 0;
        topCount = 0;
        botCount = 0;
        clicked = false;
    }

    void initBodies() {
        //start ground place
        startPoint();

        //player
        createHedge();

        //bonus body
        createBonus();

        initRoof(3);

        initRopeSegments();

        createObstacles();
    }

    private void createHedge() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(VarSource.wUnitMax.x / 2, VarSource.wUnitMax.y / 2);// + VarSource.hedgeRadius);
        hedge = curWorld.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(VarSource.hedgeRadius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = VarSource.hedgeDensity;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixtureDef.shape = circle;
        fixtureDef.filter.categoryBits = VarSource.CATEGORY_HEDGE;
        hedge.createFixture(fixtureDef);
        circle.dispose();
    }

    private void createBonus() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(-VarSource.wUnitMax.x / 2, VarSource.wUnitMax.y / 2);
        bonus = curWorld.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(VarSource.hedgeRadius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.isSensor = true;
        fixtureDef.shape = circle;


        bonus.createFixture(fixtureDef);

        bonus.setUserData(new CustomData());

        circle.dispose();
    }

    private void startPoint() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(VarSource.startTombPos);
        Body body = curWorld.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();

        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-27, 0);
        vertices[1] = new Vector2(-16, VarSource.wUnitMax.y / 2 - VarSource.hedgeRadius);
        vertices[2] = new Vector2(16, VarSource.wUnitMax.y / 2 - VarSource.hedgeRadius);
        vertices[3] = new Vector2(27, 0);

        shape.set(vertices);


        body.createFixture(shape, 0);
    }

    //int this function we create bodies for the rope in our first itteration,
// later we will jst move them,
// that will optimize our game as well
    public void initRopeSegments() {
        segments = new Body[VarSource.maxSegmentCount];

        float height = VarSource.ropeSegmentSize;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.active = false;

        CircleShape circle = new CircleShape();
        circle.setRadius(height);
        FixtureDef segmentFixtrure = new FixtureDef();
        segmentFixtrure.shape = circle;
        segmentFixtrure.restitution = 1;
        segmentFixtrure.density = VarSource.ropeSegmentDensity;
        segmentFixtrure.friction = 0f;

        for (int i = 0; i < segments.length; i++) {
            bodyDef.position.set(-10, 50);
            bodyDef.active = false;
            segments[i] = curWorld.createBody(bodyDef);
            segments[i].createFixture(segmentFixtrure);
        }
        circle.dispose();
    }

    public static void moveRopeSegments(int length, Vector2 distance, Vector2 startPosition) {
        Vector2 localDirection = new Vector2();
        for (int i = 0; i < length; i++) {
            localDirection = localDirection.add(distance);

            segments[i].setActive(!segments[i].isActive());
            segments[i].setTransform(startPosition.cpy().add(localDirection), segments[i].getAngle());
        }
    }

    public static void createRope(int length, Vector2 direction, Vector2 distance, Vector2 velocity) {

        hedge.setTransform(hedge.getPosition(), 0f);
        //hedge.setLinearVelocity(velocity);

        joints = new RevoluteJoint[length + 1];
        dJoints = new RopeJoint[length + 1];

        float height = VarSource.ropeSegmentSize;

        moveRopeSegments(length, distance, hedge.getPosition());

        //revolute joint
        RevoluteJointDef rJointDef = new RevoluteJointDef();
        //rope joint
        RopeJointDef dJointDef = new RopeJointDef();

        for (int i = 0; i < joints.length; i++) {

            rJointDef.localAnchorA.set(0, -height / 2);
            rJointDef.localAnchorB.set(0, height / 2);
            rJointDef.collideConnected = true;

            dJointDef.maxLength = (joints.length - i);
            dJointDef.collideConnected = true;

            if (i == 0) {
                rJointDef.localAnchorA.set(1.06f, 1.06f);// square root of radius divided by 2
                rJointDef.localAnchorB.set(0, height / 2);
                rJointDef.bodyA = hedge;
                rJointDef.bodyB = segments[i];


                dJointDef.localAnchorA.set(0, -height / 2);
                dJointDef.localAnchorB.set(curFixture.getBody().getPosition().sub(collision.cpy()).scl(-1));
                dJointDef.bodyA = segments[i];
                dJointDef.bodyB = curFixture.getBody();

                joints[i] = (RevoluteJoint) curWorld.createJoint(rJointDef);
                dJoints[i] = (RopeJoint) curWorld.createJoint(dJointDef);

            } else if (i == joints.length - 1) {
                rJointDef.localAnchorA.set(0, -height / 2);
                rJointDef.localAnchorB.set(curFixture.getBody().getPosition().sub(collision.cpy()).scl(-1));
                rJointDef.bodyA = segments[i - 1];
                rJointDef.bodyB = curFixture.getBody();

                dJointDef.localAnchorA.set(0, -height / 2);
                dJointDef.localAnchorB.set(curFixture.getBody().getPosition().sub(collision.cpy()).scl(-1));
                dJointDef.bodyA = segments[i - 1];
                dJointDef.bodyB = curFixture.getBody();

                joints[i] = (RevoluteJoint) curWorld.createJoint(rJointDef);
                dJoints[i] = (RopeJoint) curWorld.createJoint(dJointDef);
            } else {
                rJointDef.bodyA = segments[i - 1];
                rJointDef.bodyB = segments[i];

                dJointDef.localAnchorA.set(0, -height / 2);
                dJointDef.localAnchorB.set(curFixture.getBody().getPosition().sub(collision.cpy()).scl(-1));
                dJointDef.bodyA = segments[i];
                dJointDef.bodyB = curFixture.getBody();

                joints[i] = (RevoluteJoint) curWorld.createJoint(rJointDef);
                dJoints[i] = (RopeJoint) curWorld.createJoint(dJointDef);
            }
        }
    }

    public static void createRayCast(Vector2 p2) {
        clicked = true;
        curFixture = null;
        curWorld.rayCast(rayCastCallback, hedge.getPosition(), p2);

        Vector2 ropeVector = collision.cpy().sub(hedge.getPosition());
        float ropeLength = ropeVector.len() / VarSource.ropeSegmentSize;
        int roundLength = MathUtils.round(ropeLength / 7);

        if (curFixture != null) {
            Vector2 veloInstance = hedge.getLinearVelocity().cpy();
            createRope(roundLength, ropeVector.cpy().nor(), ropeVector.scl(1f / roundLength), veloInstance);
            for (int i = 0; i < roundLength; i++) {
                segments[i].setLinearVelocity(hedge.getLinearVelocity().scl(0.5f));
            }
            //hedge.setLinearVelocity(hedge.getLinearVelocity().add(veloInstance));
            /*if (UIstage.power >= 5)
                UIstage.power -= 5;*/
        }
    }


    public static void destroyRope() {

        int length = 0;

        //destroy all joints from the world
        if (joints != null && !clicked) {
            for (Joint j : joints) {
                CustomData data = (CustomData) j.getUserData();
                if (data != null && data.toDelete) {
                    curWorld.destroyJoint(j);
                    joints = null;
                    length += 1;
                }
            }
        }

        if (dJoints != null && !clicked) {
            for (Joint j : dJoints) {
                CustomData data = (CustomData) j.getUserData();
                if (data != null && data.toDelete) {
                    curWorld.destroyJoint(j);
                    dJoints = null;
                }
            }
        }

        if (!clicked && length != 0) {
            moveRopeSegments(length - 1, nullVec, nullVec);
            clicked = false;
        }

    }

    public static void setToDelete() {
        if (joints != null) {
            for (Joint j : joints) {
                CustomData data = new CustomData();
                data.toDelete = true;
                j.setUserData(data);
            }
        }
        if (dJoints != null) {
            for (Joint j : dJoints) {
                CustomData data = new CustomData();
                data.toDelete = true;
                j.setUserData(data);
            }
        }

        clicked = false;
    }

    public void initRoof(int count) {
        roof = new ArrayList<Body>(3);

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(0, 0, VarSource.wUnitMax.x, 0);

        BodyDef edgeDef = new BodyDef();
        edgeDef.type = BodyDef.BodyType.StaticBody;

        for (int i = 0; i < count; i++) {
            edgeDef.position.set(i * VarSource.wUnitMax.x, VarSource.wUnitMax.y);

            Body bodyEdge = curWorld.createBody(edgeDef);
            bodyEdge.createFixture(edgeShape, 0);
            roof.add(bodyEdge);
        }
    }

    public static void manageRoof() {
        switch (curRoofElement) {
            case 0:
                roof.get(curRoofElement).setTransform(roof.get(2).getPosition().x + VarSource.wUnitMax.x, VarSource.wUnitMax.y, roof.get(curRoofElement).getAngle());
                curRoofElement += 1;
                break;
            case 1:
                roof.get(curRoofElement).setTransform(roof.get(0).getPosition().x + VarSource.wUnitMax.x, VarSource.wUnitMax.y, roof.get(curRoofElement).getAngle());
                curRoofElement += 1;
                break;
            case 2:
                roof.get(curRoofElement).setTransform(roof.get(1).getPosition().x + VarSource.wUnitMax.x, VarSource.wUnitMax.y, roof.get(curRoofElement).getAngle());
                curRoofElement = 0;
                break;
        }
    }

    public static void createObstacles() {
        //obstacles
        topSpikes1.addAll(obstacleCreator.createSpikes(hedge.getPosition(), -1));
        topSpikes2.addAll(obstacleCreator.createSpikes(hedge.getPosition(), -1));
        botSpikes1.addAll(obstacleCreator.createSpikes(hedge.getPosition(), 1));
        botSpikes2.addAll(obstacleCreator.createSpikes(hedge.getPosition(), 1));
    }


    private void manageObstaclesArr(ArrayList<Body> top, ArrayList<Body> bot) {
        int indexPlacement = 0;
        int arrayIndex;
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < VarSource.obsPerScreen; i++) {
            /**random choice of obstacle side**/
            if (topCount == VarSource.maxObsInRaw) {
            indexPlacement = 2;
            } else if (botCount== VarSource.maxObsInRaw){
                indexPlacement = 1;
            }
            else {
                indexPlacement = MathUtils.random(1, 2);
            }

            switch (indexPlacement) {
                //top side spikes
                case 1:
                    topCount++;
                    botCount = 0;

                    do {
                        arrayIndex = MathUtils.random(0, top.size() - 1);
                    } while (!set.add(arrayIndex));

                    CustomData topData = (CustomData) top.get(arrayIndex).getUserData();

                    top.get(arrayIndex).setTransform(hedge.getPosition().x + VarSource.wUnitMax.x / 2
                                    + (VarSource.wUnitMax.x / VarSource.obsPerScreen) * i, VarSource.wUnitMax.y,
                            top.get(arrayIndex).getAngle());
                    //cocoon spawn
                    // spawn cocoon or just spike?
                    //checking for spawn distance accordingly cocoon spawn frequency
                    if (top.get(arrayIndex).getPosition().x - lastCocoonX >= VarSource.cocoonFreq[VarSource.difficult - 1] * VarSource.wUnitMax.x
                            && VarSource.difficult > VarSource.cocoonStartSpawnDiff
                            && MathUtils.random(0, 1) == 1) {

                        BGstage.assignToBody(new Vector2(top.get(arrayIndex).getPosition().x -
                                        VarSource.spikeWidth, top.get(arrayIndex).getPosition().y),
                                BGstage.cocoonesMark, new Vector2(1, topData.scale));
                        lastCocoonX = top.get(arrayIndex).getPosition().x;
                        topData.cocoon = true;
                        topData.web = false;
                        top.get(arrayIndex).setUserData(topData);
                    } else {
                        topData.cocoon = false;
                        topData.web = false;
                        top.get(arrayIndex).setUserData(topData);
                        BGstage.assignToBody(new Vector2(top.get(arrayIndex).getPosition().x -
                                        VarSource.spikeWidth, top.get(arrayIndex).getPosition().y),
                                BGstage.spikesMark, new Vector2(1, topData.scale));
                    }
                    break;

                //bottom side spikes
                case 2:
                    topCount = 0;
                    botCount++;
                    do {
                        arrayIndex = MathUtils.random(0, bot.size() - 1);
                    } while (!set.add(arrayIndex * (-1)));
                    CustomData botData = (CustomData) bot.get(arrayIndex).getUserData();
                    bot.get(arrayIndex).setTransform(hedge.getPosition().x + VarSource.wUnitMax.x / 2
                                    + (VarSource.wUnitMax.x / VarSource.obsPerScreen) * i, 0,
                            bot.get(arrayIndex).getAngle());

                    //graphic assign
                    if (bot.get(arrayIndex).getPosition().x - lastCocoonX >= VarSource.cocoonFreq[VarSource.difficult - 1] * VarSource.wUnitMax.x
                            && VarSource.difficult > VarSource.cocoonStartSpawnDiff
                            && MathUtils.random(0, 1) == 1) {

                        BGstage.assignToBody(new Vector2(bot.get(arrayIndex).getPosition().x -
                                        VarSource.spikeWidth, bot.get(arrayIndex).getPosition().y),
                                BGstage.webMark, new Vector2(1, botData.scale));
                        lastCocoonX = bot.get(arrayIndex).getPosition().x;
                        botData.web = true;
                        botData.cocoon = false;
                        bot.get(arrayIndex).setUserData(botData);

                    } else {
                        botData.cocoon = false;
                        botData.web = false;
                        bot.get(arrayIndex).setUserData(botData);

                        BGstage.assignToBody(new Vector2(bot.get(arrayIndex).getPosition().x -
                                        VarSource.spikeWidth, bot.get(arrayIndex).getPosition().y),
                                BGstage.spikesMark, new Vector2(1, botData.scale));
                    }

                    break;

            }
        }

    }


     private void rangeCheck() {
        float currentDelta;//current difference between hedge and deltaRange

        if (deltaRange == 0) {
            deltaRange = hedge.getPosition().x;
        }

        currentDelta = hedge.getPosition().x - deltaRange;

        manageBonus();

        if (currentDelta >= VarSource.wUnitMax.x) {
            if (!evenManage) {
                manageObstaclesArr(topSpikes1, botSpikes1);
            } else manageObstaclesArr(topSpikes2, botSpikes2);
            manageRoof();
            evenManage = !evenManage;
            deltaRange = hedge.getPosition().x;
        }

        gliderMoving();

    }

    private void heightCheck() {
        if (hedge.getPosition().y <= VarSource.hedgeRadius) {
            Main.state = Main.State.LOST;
        }
    }

    private void setDifficult() {

        if (VarSource.difficult < VarSource.maxDifficult) {  //5 is max difficult

            VarSource.obsPerScreen = VarSource.difficult - 1;

            //checking for range to upper difficult
            if (hedge.getPosition().x >= VarSource.dif1Range[VarSource.difficult - 1] - VarSource.wUnitMax.x / 2) {
                VarSource.difficult += 1;
                curLineSegment = 0;
            }
        }
    }

    private static void manageBonus() {
        //checking for frequency
        if (hedge.getPosition().x + VarSource.wUnitMax.x / 2 - lastBonusX >= VarSource.bonusFrequence) {

            CustomData data = (CustomData) bonus.getUserData();
            data.collected = false;
            //what type of a bonus?
            int[] arr = VarSource.bonusSpawnChance.clone();
            switch (PlayerStage.transportModifer) {
                case clock:
                    arr[1] = 0;
                    break;
                case glider:
                    arr[0] = 0;
                    break;
            }

            int keyTypeIndex = UsefullMethods.randomSpawnChance(arr);

            for (int i = 0; i < VarSource.bonusKeys.length; i++) {
                if (i == keyTypeIndex) {
                    data.bonusTypes.put(VarSource.bonusKeys[keyTypeIndex], true);
                } else {
                    data.bonusTypes.put(VarSource.bonusKeys[i], false);
                }
            }
            bonus.setUserData(data);

            bonus.setTransform(hedge.getPosition().x + VarSource.wUnitMax.x / 2, VarSource.wUnitMax.y / 2, 0f);
            lastBonusX = hedge.getPosition().x + VarSource.wUnitMax.x / 2;

        }

    }

    static void activateGlider() {
        curWorld.clearForces();
        hedge.setGravityScale(0);
        hedge.setUserData(new Vector2(hedge.getPosition().x, VarSource.wUnitMax.y / 2));
        setToDelete();
    }

    static void deactivateGlider() {
        curWorld.clearForces();
        hedge.setGravityScale(1);
        hedge.setFixedRotation(false);
    }


    private static void gliderMoving() {
        if (PlayerStage.transportModifer == PlayerStage.TransportModifer.glider) {
            /**checking for hedge location**/
            Vector2 transPos = (Vector2) hedge.getUserData();
            if (transPos != null) {
                hedge.setTransform(transPos, 0);
                hedge.setUserData(null);
            }

            if (hedge.getLinearVelocity() != VarSource.gliderSpeed) {
                hedge.setFixedRotation(true);
                hedge.setGravityScale(0);
                hedge.setLinearVelocity(VarSource.gliderSpeed);
            }

        }
    }


    void act(){
        curWorld.step(Gdx.graphics.getDeltaTime() * VarSource.timeSpeed, 1, MathUtils.round(5 * VarSource.timeSpeed));
        destroyRope();//destroying old joints
        setDifficult();//manage difficulty
        rangeCheck();
        heightCheck();//falling check
    }


}
