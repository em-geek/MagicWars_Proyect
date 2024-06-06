package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.Random;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;

public class Main extends SimpleApplication {
    //Testeo de rama
        Player player;
        BulletAppState fisica;
    
    public static void main(String[] args) {
        
        
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp(){
        rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/sky2.png", SkyFactory.EnvMapType.EquirectMap));
            
        //Agregacion de fisica
        fisica = new BulletAppState();
        stateManager.attach(fisica);
        fisica.setDebugEnabled(true);
        
        // Configurar la cámara
        cam.setLocation(new Vector3f(0, 10, 30));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        // Añadir iluminación
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);

        // Genera un material simple
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture brickTexture = assetManager.loadTexture("Textures/brick.png");
        mat.setTexture("ColorMap", brickTexture);

        // Crear un nodo para contener las cajas
        Node cajasNode = new Node("CajasNode");
        rootNode.attachChild(cajasNode);
        
        // Genera y posiciona los cubos
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            Box b = new Box(1, 1, 1);  // Crea un cubo de tamaño 1x1x1
            Geometry geom = new Geometry("Box" + i, b); // Cambia el nombre de la geometrí
            geom.setMaterial(mat);
                CollisionShape colisionCaja = CollisionShapeFactory.createBoxShape(geom);
                RigidBodyControl cuerpoRigidoCaja = new RigidBodyControl(colisionCaja, 1.0f);
                geom.addControl(cuerpoRigidoCaja);  // Attach control to each box geometry
                fisica.getPhysicsSpace().add(cuerpoRigidoCaja);
            // Posiciona el cubo en una posición aleatoria
            float x = rand.nextFloat() * 50 - 25;
            float y = rand.nextFloat() * 10;
            float z = rand.nextFloat() * 50 - 25;
            geom.setLocalTranslation(new Vector3f(x, y, z));

            cajasNode.attachChild(geom);
        }
        
         // Crear el material para el suelo
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture groundTexture = assetManager.loadTexture("Textures/brick.png"); // Ajusta la textura según lo que necesites
        groundMat.setTexture("ColorMap", groundTexture);

        // Crear el suelo como un Quad grande
        Box groundMesh = new Box(200,0.5f, 200); // Tamaño del suelo (ajústalo según tu necesidad)
        Geometry groundGeom = new Geometry("Ground", groundMesh);
        groundGeom.setMaterial(groundMat);
        

        // Posicionar el suelo debajo de la cámara
        groundGeom.setLocalTranslation(-60, -100, 60);

        // Adjuntar el suelo al rootNode
        rootNode.attachChild(groundGeom);
        
        
        // Configurar la cámara para seguir al jugador
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y); // Orientar la cámara hacia adelante
        

        
        
        ////COLISIONES CAJAS
        //Colisiones
        /*Spatial cajas = cajasNode;
        CollisionShape colisionCajas = CollisionShapeFactory.createBoxShape(cajas);
        
        //Cuerpo Rigido
        RigidBodyControl cuerpoRigidoCaja = new RigidBodyControl(colisionCajas, 1.0f);
        cajas.addControl(cuerpoRigidoCaja);
        fisica.getPhysicsSpace().add(cuerpoRigidoCaja);
        */
        
        ////COLISIONES SUELO
        //Colisiones
        Spatial suelo = groundGeom;
        CollisionShape colisionSuelo = CollisionShapeFactory.createBoxShape(suelo);
        
        //Cuerpo Rigido
        RigidBodyControl cuerpoRigidoSuelo = new RigidBodyControl(colisionSuelo, 0.0f);
        suelo.addControl(cuerpoRigidoSuelo);
        fisica.getPhysicsSpace().add(cuerpoRigidoSuelo);
        
        // Inicializar al jugador
        initPlayer(fisica);
        setUpKeys();
        
        
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
    
    private void initPlayer(BulletAppState fisica) {
        player = new Player();
        rootNode.attachChild(player.getNode());
        
        ////COLISIONES SUELO
        //Colisiones
        Spatial personaje = player.getNode();
        CollisionShape colisionPersonaje = CollisionShapeFactory.createBoxShape(personaje);
        
        //Cuerpo Rigido
        RigidBodyControl cuerpoRigidoPersonaje = new RigidBodyControl(colisionPersonaje, 1.0f);
        cuerpoRigidoPersonaje.setGravity(new Vector3f(0, -9.81f, 0)); // Sets gravity to -9.81 on the Y-axis
        personaje.addControl(cuerpoRigidoPersonaje);
        fisica.getPhysicsSpace().add(cuerpoRigidoPersonaje);
        
        
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
        Spatial playerModel;
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
            // Actualizar la dirección de movimiento basada en la orientación de la cámara
            updateWalkDirection(tpf);
            
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
        }

        // Actualizar la dirección del movimiento basado en la entrada y la orientación de la cámara
        private void updateWalkDirection(float tpf) {
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