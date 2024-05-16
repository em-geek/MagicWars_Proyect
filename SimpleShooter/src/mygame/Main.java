package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    InteractionManager     im;
    Node                   scene;
    Node                   cameraNode;
    ArrayList<Enemy>       enemies;
    Player                 player;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    //Run initialization methods on start
    @Override
    public void simpleInitApp() {
        this.viewPort.setBackgroundColor(ColorRGBA.LightGray);
        im = new InteractionManager();
        setEdgeFilter();
        initEnemies();
        initCamera();
        initScene();
        initPlayer();
        initCrossHairs();
    }
    
    //Create an edge filter for visual effect
    private void setEdgeFilter() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        CartoonEdgeFilter toon = new CartoonEdgeFilter();
        toon.setEdgeWidth(2);
        fpp.addFilter(toon);
        this.getViewPort().addProcessor(fpp);
        
    }
    
    //Initializes the camera
    private void initCamera() {
        cameraNode = new Node();
        rootNode.attachChild(cameraNode);
        cameraNode.setLocalTranslation(10,2,10);
        this.getFlyByCamera().setMoveSpeed(0);    
    }
    
    // A centred plus sign to help the player aim.
    protected void initCrossHairs() {
      guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
      BitmapText ch = new BitmapText(guiFont, false);
      ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
      ch.setText("+"); // crosshairs
      ch.setLocalTranslation( // center
        settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
      guiNode.attachChild(ch);
    }
    
    //Initializes the scene
    private void initScene() {
        scene       = new Node();
        Quad floor  = new Quad(100,100);
        Geometry fg = new Geometry("Floor", floor);
        Material fm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        fm.setColor("Color", ColorRGBA.Green);
        fg.setMaterial(fm);
        fg.rotate(-FastMath.HALF_PI,0,0);
        scene.attachChild(fg);
        rootNode.attachChild(scene);
        fg.setLocalTranslation(-50, -2, 25);
    }

    //Initialize the player
    private void initPlayer() {
        player = new Player();
    }    
    
    //Player class
    class Player {
        long    lastAttack = System.currentTimeMillis();
        int     health     = 5; //Starting health
        boolean gunFlash   = false; //Boolean to detect whether gun is flashing
        Node gun; //Model for gun
        
        public Player() {
            createGun();
            cameraNode.attachChild(gun);
            gun.setLocalTranslation(1f, 1.5f, 2.5f);
        }
        //Creates the players gun model
        private void createGun() {
            gun           = new Node("Gun");
            Box      b    = new Box(.25f, .25f, 1.25f);
            Geometry bg   = new Geometry("Box", b);
            Material bm   = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bm.setColor("Color", ColorRGBA.Gray);
            bg.setMaterial(bm);
            gun.attachChild(bg);
        }
        //Player attacks
        public void attack() {
            //Check attack cool down of 500 milliseconds
            if (System.currentTimeMillis() - lastAttack  > 500) {
                //Set last attack to now
                lastAttack = System.currentTimeMillis();
                gunFlash   = true;
                
                //Turn the gun to orange when it's shot
                ((Geometry)gun.getChild("Box")).getMaterial().setColor("Color", ColorRGBA.Orange);
                
                //Create collision results
                CollisionResults results = new CollisionResults();
                Ray              ray     = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);
                
                //If collision check for enemy
                if (results.size() != 0) {
                    Node hit = results.getClosestCollision().getGeometry().getParent();
                    //If enemy lower his health and print it
                    if (hit.getName() == null) return; //Return if null
                    if (hit.getName().contains("Enemy")) {
                        Enemy e = (Enemy) hit.getParent(); //Get the enemy from the map
                        e.health--; //Remove one health
                        e.makeCyan(); // Make the enemy head green when hit
                        System.out.println("Enemy Hit: " + e.health); //Print the enemies health
                    }
                }
            }
        }
        
        //End the gun's orange flash
        public void endGunFlash() {
            if (System.currentTimeMillis() - lastAttack  > 200) {
                gunFlash = false;
                ((Geometry)gun.getChild("Box")).getMaterial().setColor("Color", ColorRGBA.Gray);
            }
        }
        
    }
    
    int enemyCounter = 0;    
    
    //Initializes the enemies
    private void initEnemies() {
        enemies = new ArrayList<>();
        createEnemy();
    }    
    
    private void createEnemy() {
        Enemy e = new Enemy();
        enemies.add(e);
        rootNode.attachChild(e);    
    }
    
    //Enemy class
    class Enemy extends Node {
    
        long lastAttack = System.currentTimeMillis(); //Time of last attack
        long lastHit    = System.currentTimeMillis(); //Time of last hit taken
        int  health = 5;
        Node model;
        
        boolean isYellow = false; //Enemy flashes yellow when it attacks
        boolean isCyan  = false; //Enemy flashes green when it is hit
        
        //Enemy Constructor
        public Enemy() {
            createModel();
            enemyCounter++;
        }
        
        //Creates the enemies model
        private void createModel() {
            model         = new Node("Enemy"+enemyCounter);
            Box b         = new Box(.5f, 2, .5f);
            Geometry bg   = new Geometry("Box", b);
            Material bm   = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Sphere sphere = new Sphere(32, 32, 1.5f, false, false);
            Geometry sg   = new Geometry("Sphere", sphere);
            Material sm   = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            bm.setColor("Color", ColorRGBA.Blue);
            sm.setColor("Color", ColorRGBA.Red);
            bg.setMaterial(bm);    
            sg.setMaterial(sm);
            model.attachChild(bg);
            model.attachChild(sg);
            sg.setLocalTranslation(0,2.5f,0);
            
            attachChild(model);
        }        
        
        //Enemy attack method
        public void attack() {
        
            //If cooldown of 500 milliseconds attack player and print health
            if (System.currentTimeMillis() - lastAttack > 500) {
                lastAttack = System.currentTimeMillis();
                isYellow   = true;
                ((Geometry)model.getChild("Sphere")).getMaterial().setColor("Color", ColorRGBA.Yellow);
                player.health--;
                System.out.println("Attacking Player Health: " + player.health);
            }
            
        }
        
        //After 200 milliseconds return the yellow head to red
        public void endYellow() {
            if (System.currentTimeMillis() - lastAttack > 200) {
                isYellow = false;
                ((Geometry)model.getChild("Sphere")).getMaterial().setColor("Color", ColorRGBA.Red);
            }
        }
        
        public void endCyan() {
            if (System.currentTimeMillis() - lastHit > 200) {
                isCyan = false;
                ((Geometry)model.getChild("Sphere")).getMaterial().setColor("Color", ColorRGBA.Red);
            }        
        }
        
        //Runs when the enemy is attacked
        public void makeCyan() {
            lastHit = System.currentTimeMillis();
            isCyan = true;
            ((Geometry)model.getChild("Sphere")).getMaterial().setColor("Color", ColorRGBA.Cyan);
        }
        
    }
    
    //Class that handles player's input
    class InteractionManager implements ActionListener {
    
        boolean up=false, left=false, right=false,down=false,click=false;
        
        public InteractionManager() {
            setUpKeys();
        }
        
        //Sets up the keys
        private void setUpKeys() {
            inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
            inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
            inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
            inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
            inputManager.addMapping("Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
            inputManager.addListener(this, "Up");
            inputManager.addListener(this, "Down");
            inputManager.addListener(this, "Left");
            inputManager.addListener(this, "Right");
            inputManager.addListener(this, "Click");
        }

        //Runs when an action is performed
        @Override
        public void onAction(String binding, boolean isPressed, float tpf) {

            switch (binding) {
                case "Up":
                    up = isPressed;
                    break;
                case "Down":
                    down = isPressed;
                    break;
                case "Left":
                    left = isPressed;
                    break;
                case "Right":
                    right = isPressed;
                    break;
                case "Click":
                    click = isPressed;
                    break;
                default:
                    break;
            }

        }
        
    }
    
    //Moves the camera around
    private void moveCamera(float tpf) {
        //Place the camera at the node
        this.getCamera().setLocation(cameraNode.getLocalTranslation().multLocal(1,0,1).add(0, 2, 0));
        cameraNode.lookAt(cam.getDirection().mult(999999), new Vector3f(0,1,0)); //Makes the gun point
        if (im.up) {
            cameraNode.move(getCamera().getDirection().mult(tpf).mult(10));
        }
        else if (im.down) {
            cameraNode.move(getCamera().getDirection().negate().mult(tpf).mult(10));
        }
        if (im.right) {
            cameraNode.move(getCamera().getLeft().negate().mult(tpf).mult(10));
        }
        else if (im.left) {
            cameraNode.move(getCamera().getLeft().mult(tpf).mult(10));
        }
    }
    
    //Updates the Enemies
    private void updateEnemies(float tpf) {
        Vector3f playerLocation = cameraNode.getLocalTranslation();
        
        //If no enemies make one
        if (enemies.size() < 5) {
            createEnemy();
        }
        
        for (int i = 0; i < enemies.size(); i++) {

            //Move the enemies toward the player
            Enemy    e =  enemies.get(i);
            
            if (e.health <= 0) {
                enemies.remove(e);
                e.model.removeFromParent();
            }
            
            Vector3f enemyLocation   = e.model.getLocalTranslation();
            Vector3f playerDirection = playerLocation.subtract(enemyLocation);
            e.model.move(playerDirection.mult(tpf).mult(.25f));
            
            //If the enemy is close then attack the player
            float distance = enemyLocation.distance(playerLocation);
            if (distance < 5) {
                e.attack();
            }
            
            //If e is yellow or cyan run make red
            if (e.isYellow) {
                e.endYellow();
            }
            else if (e.isCyan) {
                e.endCyan();
            }
            
        }
        
    }
    
    //Updates the player
    private void updatePlayer() {
        //If click is pressed run the player attack method
        if (im.click) {
            player.attack();
        }
        //If players health goes to or below 0 print death and close
        if (player.health <=0) {
            System.out.println("DEATH");
            this.stop();
        }
        //If player's gun is flashing check to end the gun flash
        if (player.gunFlash) {
            player.endGunFlash();
        }
    }
    
    //Run the update methods
    @Override
    public void simpleUpdate(float tpf) {
        moveCamera(tpf);
        updateEnemies(tpf);
        updatePlayer();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
