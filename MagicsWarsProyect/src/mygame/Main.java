package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    Player player;
    Spatial castle;
    private static final float GRAVITY = 30f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    // Run initialization methods on start
    @Override
    public void simpleInitApp() {
        // Load the castle model
        assetManager.registerLocator("assets/Models/castillo/", FileLocator.class);
        castle = assetManager.loadModel("castillo.j3o");
        rootNode.attachChild(castle);
        
        // Agregar iluminación
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f)); // Dirección de la luz (puede ajustarse)
        sun.setColor(ColorRGBA.White); // Color de la luz
        rootNode.addLight(sun);

        // Cambia el color de fondo del ViewPort a blanco
        viewPort.setBackgroundColor(com.jme3.math.ColorRGBA.Gray);

        // Initialize the player
        initPlayer();
        setUpKeys();
        
        // Configura la cámara para que siga al jugador
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y); // Orienta la cámara hacia adelante
        
        // Crear un terreno procedimental
    TerrainQuad terrain = new TerrainQuad("myTerrain", 65, 257, null);
    TerrainLodControl lodControl = new TerrainLodControl(terrain, cam);
    terrain.addControl(lodControl);
    terrain.setLocalTranslation(0, -100, 0); // Ajusta la posición del terreno

    // Configurar el material del terreno
    Material matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
    matTerrain.setBoolean("useTriPlanarMapping", false);
    matTerrain.setFloat("Shininess", 0.0f);


    // Asignar el material al terreno
    terrain.setMaterial(matTerrain);

    // Agregar el terreno a la escena
    rootNode.attachChild(terrain);

    // Inicializar el jugador y configurar las teclas
    initPlayer();
    setUpKeys();
    }

    // Sets up the keys for player movement
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(player, "Left", "Right", "Jump");
    }
    
    private void initPlayer() {
        player = new Player();
        rootNode.attachChild(player.getNode());
        player.getNode().setLocalTranslation(0, 10, 0); // Posición inicial del jugador
        
        
    }

    // Updates the player
    @Override
    public void simpleUpdate(float tpf) {
        checkCollisionWithCastle();
        
        player.update(tpf);

    // Actualiza la posición y orientación de la cámara para seguir al jugador
    Vector3f playerPos = player.getNode().getWorldTranslation();
    Vector3f camOffset = new Vector3f(0, 2, 5); // Offset de la cámara detrás y encima del jugador
    cam.setLocation(playerPos.add(camOffset)); // Establece la posición de la cámara

    
    }

    // Check collision with the castle
    private void checkCollisionWithCastle() {
        if (player != null && castle != null) {
            if (player.getNode().getWorldBound().intersects(castle.getWorldBound())) {
                // If there is a collision, move the player back to avoid penetration
                player.getNode().setLocalTranslation(player.getPreviousPosition());
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // TODO: add render code
    }

    // Player class
    class Player implements ActionListener {
        Node playerNode;
        Vector3f walkDirection = new Vector3f();
        boolean left, right, jump;
        boolean onGround = false; // Indicates if the player is on the ground
        Vector3f previousPosition = new Vector3f(); // Store previous position for collision checking

        public Player() {
            playerNode = new Node();
        }

        // Getter for the player's node
        public Node getNode() {
            return playerNode;
        }

        // Getter for previous position
        public Vector3f getPreviousPosition() {
            return previousPosition;
        }

        // Method to update the player's position based on input and gravity
        public void update(float tpf) {
            previousPosition.set(playerNode.getLocalTranslation()); // Actualiza la posición anterior

            // Aplica la gravedad si el jugador no está en el suelo
            if (!onGround) {
                walkDirection.y -= GRAVITY * tpf; // Ajusta la velocidad en y según la gravedad
            }

            // Actualiza la posición del jugador
            playerNode.move(walkDirection.mult(tpf));
        }

        // Method to handle player input
        @Override
        public void onAction(String binding, boolean isPressed, float tpf) {
            switch (binding) {
                case "Left":
                    left = isPressed;
                    break;
                case "Right":
                    right = isPressed;
                    break;
                case "Jump":
                    if (isPressed && onGround) {
                        walkDirection.y = 15; // Jumping strength
                        onGround = false; // Player is no longer on the ground
                    }
                    break;
            }
            updateWalkDirection();
        }

        // Update walk direction based on input
        private void updateWalkDirection() {
            walkDirection.set(0, 0, 0);
            if (left) {
                walkDirection.addLocal(-5, 0, 0);
            }
            if (right) {
                walkDirection.addLocal(5, 0, 0);
            }
        }
    }
}