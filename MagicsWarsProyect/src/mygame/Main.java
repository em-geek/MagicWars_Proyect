package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Main extends SimpleApplication {

    Player player;
    Spatial castle;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // Cargar el modelo del castillo
        assetManager.registerLocator("assets/Models/castillo/", FileLocator.class);
        castle = assetManager.loadModel("castillo.j3o");
        rootNode.attachChild(castle);
        
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
    }

    // Configurar las teclas para el movimiento del jugador
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(player, "Left", "Right", "Forward", "Backward", "Jump");
    }
    
    private void initPlayer() {
        player = new Player();
        rootNode.attachChild(player.getNode());
        
        Vector3f castlePosition = castle.getWorldTranslation();
        
        // Posicionar al jugador encima del castillo
        player.getNode().setLocalTranslation(castlePosition.x, castlePosition.y + 10, castlePosition.z);
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);

        // Actualizar la posición de la cámara para seguir al jugador
        Vector3f playerPos = player.getNode().getWorldTranslation();
        Vector3f camOffset = new Vector3f(0, 2, 5); // Offset de la cámara detrás y encima del jugador
        cam.setLocation(playerPos.add(camOffset)); // Establecer la posición de la cámara
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Código de renderización
    }

    // Clase del jugador
    class Player implements ActionListener {
        Node playerNode;
        Vector3f walkDirection = new Vector3f();
        boolean left, right, forward, backward, jump;

        public Player() {
            playerNode = new Node();
        }

        // Getter para el nodo del jugador
        public Node getNode() {
            return playerNode;
        }

        // Método para actualizar la posición del jugador basado en la entrada
        public void update(float tpf) {
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
                case "Forward":
                    forward = isPressed;
                    break;
                case "Backward":
                    backward = isPressed;
                    break;
                case "Jump":
                    jump = isPressed;
                    break;
            }
            updateWalkDirection();
        }

        // Actualizar la dirección del movimiento basado en la entrada
        private void updateWalkDirection() {
            // Obtener la dirección actual de la cámara en el plano XY
            Vector3f camDir = cam.getDirection().multLocal(1, 0, 1).normalizeLocal();
            Vector3f camLeft = cam.getLeft().multLocal(1, 0, 1).normalizeLocal();

            // Resetear la dirección de movimiento
            walkDirection.set(0, 0, 0);

            // Ajustar la dirección del movimiento basado en la entrada y la orientación de la cámara
            if (left) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (forward) {
                walkDirection.addLocal(camDir);
            }
            if (backward) {
                walkDirection.addLocal(camDir.negate());
            }
            if (jump) {
                // Implementa la lógica de salto aquí si es necesario
            }

            // Normalizar la dirección del movimiento para mantener una velocidad constante
            if (!walkDirection.equals(Vector3f.ZERO)) {
                walkDirection.normalizeLocal().multLocal(5); // Ajustar la velocidad de movimiento aquí
            }
        }
    }
}
