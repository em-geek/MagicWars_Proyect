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
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;

public class Main extends SimpleApplication {

    Player player;
    Spatial castle;
    private BulletAppState bulletAppState;
    private static final float GRAVITY = 30f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Inicializar el motor de física
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        
        // Cargar el modelo del castillo
        assetManager.registerLocator("assets/Models/castillo/", FileLocator.class);
        castle = assetManager.loadModel("castillo.j3o");
        rootNode.attachChild(castle);
        
        // Configurar la física del castillo
        CollisionShape castleShape = CollisionShapeFactory.createMeshShape(castle);
        RigidBodyControl castlePhysics = new RigidBodyControl(castleShape, 1); // Ajustar la masa según sea necesario
        castle.addControl(castlePhysics);
        bulletAppState.getPhysicsSpace().add(castlePhysics);
        
        // Agregar iluminación
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f)); // Dirección de la luz (puede ajustarse)
        sun.setColor(ColorRGBA.White); // Color de la luz
        rootNode.addLight(sun);

        // Cambiar el color de fondo del ViewPort a blanco
        viewPort.setBackgroundColor(ColorRGBA.Gray);

        // Inicializar al jugador
        initPlayer();
        setUpKeys();
        
        // Configurar la cámara para seguir al jugador
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y); // Orientar la cámara hacia adelante
        
        // Crear un terreno procedimental
        TerrainQuad terrain = new TerrainQuad("myTerrain", 65, 257, null);
        TerrainLodControl lodControl = new TerrainLodControl(terrain, cam);
        terrain.addControl(lodControl);
        terrain.setLocalTranslation(0, -100, 0); // Ajustar la posición del terreno

        // Configurar el material del terreno
        Material matTerrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matTerrain.setBoolean("useTriPlanarMapping", false);
        matTerrain.setFloat("Shininess", 0.0f);

        // Asignar el material al terreno
        terrain.setMaterial(matTerrain);

        // Agregar el terreno a la escena
        rootNode.attachChild(terrain);
    }

    // Configurar las teclas para el movimiento del jugador
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(player, "Left", "Right", "Jump");
    }
    
    private void initPlayer() {
        player = new Player();
        rootNode.attachChild(player.getNode());
        
        Vector3f castlePosition = castle.getWorldTranslation();
        
        // Agregar física al jugador
        CapsuleCollisionShape playerShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
        CharacterControl playerPhysics = new CharacterControl(playerShape, 0.05f);
        player.getNode().addControl(playerPhysics);
        bulletAppState.getPhysicsSpace().add(playerPhysics);
        
        // Posicionar al jugador encima del castillo
        player.getNode().setLocalTranslation(castlePosition.x, castlePosition.y + 10, castlePosition.z);
    }

    @Override
    public void simpleUpdate(float tpf) {
        checkCollisionWithCastle();
        player.update(tpf);

        // Actualizar la posición de la cámara para seguir al jugador
        Vector3f playerPos = player.getNode().getWorldTranslation();
        Vector3f camOffset = new Vector3f(0, 2, 5); // Offset de la cámara detrás y encima del jugador
        cam.setLocation(playerPos.add(camOffset)); // Establecer la posición de la cámara
    }

    // Verificar colisión con el castillo
    private void checkCollisionWithCastle() {
        if (player != null && castle != null) {
            if (player.getNode().getWorldBound().intersects(castle.getWorldBound())) {
                // Si hay una colisión, mover al jugador hacia atrás para evitar la penetración
                player.getNode().setLocalTranslation(player.getPreviousPosition());
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Código de renderización
    }

    // Clase del jugador
    class Player implements ActionListener {
        Node playerNode;
        Vector3f walkDirection = new Vector3f();
        boolean left, right, jump;
        boolean onGround = false; // Indica si el jugador está en el suelo
        Vector3f previousPosition = new Vector3f(); // Almacena la posición anterior para la detección de colisiones

        public Player() {
            playerNode = new Node();
        }

        // Getter para el nodo del jugador
        public Node getNode() {
            return playerNode;
        }

        // Getter para la posición anterior
        public Vector3f getPreviousPosition() {
            return previousPosition;
        }

        // Método para actualizar la posición del jugador basado en la entrada y la gravedad
        public void update(float tpf) {
            previousPosition.set(playerNode.getLocalTranslation()); // Actualizar la posición anterior

            // Aplicar la gravedad si el jugador no está en el suelo
            if (!onGround) {
                walkDirection.y -= GRAVITY * tpf; // Ajustar la velocidad en y según la gravedad
            }

            // Actualizar la posición del jugador
            playerNode.move(walkDirection.mult(tpf));
        }

        // Método para manejar la entrada del jugador
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
                        walkDirection.y = 15; // Fuerza de salto
                        onGround = false; // El jugador ya no está en el suelo
                    }
                    break;
            }
            updateWalkDirection();
        }

        // Actualizar la dirección del movimiento basado en la entrada
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
