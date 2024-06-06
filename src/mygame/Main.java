package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
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
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.Random;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;

public class Main extends SimpleApplication {

    Player player;
    BulletAppState fisica;
    ArrayList<Enemy> enemies;
    int maxEnemies = 5; // Máximo número de enemigos en el juego

    
    Player player;
    BulletAppState fisica;
    boolean isGameOver = false;
    private Timer gameOverTimer;
    
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Magics Wars");
        settings.setSettingsDialogImage("Interface/Pantalla_Magics_Wars.jpg");
        settings.setResolution(1280, 720); // Establece la resolución deseada
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp(){
        gameOverTimer = getTimer();
        rootNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/sky2.png", SkyFactory.EnvMapType.EquirectMap));

        // Agregacion de fisica
        fisica = new BulletAppState();
        stateManager.attach(fisica);
        fisica.setDebugEnabled(true);

        // Configurar la cámara
        cam.setLocation(new Vector3f(0, 1.8f, 0)); // Altura de los ojos del jugador
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y); // Orientar la cámara hacia adelante

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
        Box groundMesh = new Box(200, 0.5f, 200); // Tamaño del suelo (ajústalo según tu necesidad)
        Geometry groundGeom = new Geometry("Ground", groundMesh);
        groundGeom.setMaterial(groundMat);

        // Posicionar el suelo debajo de la cámara
        groundGeom.setLocalTranslation(0, -0.5f, 0);

        // Adjuntar el suelo al rootNode
        rootNode.attachChild(groundGeom);

        Spatial suelo = groundGeom;
        CollisionShape colisionSuelo = CollisionShapeFactory.createBoxShape(suelo);

        // Cuerpo Rigido
        RigidBodyControl cuerpoRigidoSuelo = new RigidBodyControl(colisionSuelo, 0.0f);
        suelo.addControl(cuerpoRigidoSuelo);
        fisica.getPhysicsSpace().add(cuerpoRigidoSuelo);

        // Inicializar al jugador
        initPlayer(fisica);
        setUpKeys();

        // Inicializar enemigos
        initEnemies();
    }

    // Configurar las teclas para el movimiento del jugador
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("GameOver", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addListener(actionListener, "Left", "Right", "Forward", "Backward", "Jump", "GameOver");
    }

    private void initPlayer(BulletAppState fisica) {
        player = new Player();
        rootNode.attachChild(player.getNode());
        fisica.getPhysicsSpace().add(player.characterControl);
    }

    private void initEnemies() {
        enemies = new ArrayList<>();
        for (int i = 0; i < maxEnemies; i++) {
            createEnemy();
        }
    }

    private void createEnemy() {
        Random random = new Random();
        float x, z;

        // Decide en cuál borde generar el enemigo
        int edge = random.nextInt(4); // 0 = norte, 1 = sur, 2 = este, 3 = oeste

        switch (edge) {
            case 0: // Norte (z máximo)
                x = random.nextFloat() * 3000 - 100; // Aleatorio en el rango del ancho de la plataforma
                z = 200; // Orilla norte
                break;
            case 1: // Sur (z mínimo)
                x = random.nextFloat() * 3000 - 100; // Aleatorio en el rango del ancho de la plataforma
                z = 200; // Orilla sur
                break;
            case 2: // Este (x máximo)
                x = 200; // Orilla este
                z = random.nextFloat() * 3000 - 100; // Aleatorio en el rango de la longitud de la plataforma
                break;
            case 3: // Oeste (x mínimo)
                x = 200; // Orilla oeste
                z = random.nextFloat() * 3000 - 100; // Aleatorio en el rango de la longitud de la plataforma
                break;
            default:
                x = 0;
                z = 0;
                break;
        }

        Enemy e = new Enemy(x, z);
        enemies.add(e);
        rootNode.attachChild(e);
    }

    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);
        updateEnemies(tpf);

        // Actualizar la posición de la cámara para seguir al jugador
        cam.setLocation(player.getNode().getWorldTranslation().add(0, 1.8f, 0)); // Altura de los ojos del jugador
    }

        
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
        if (!isGameOver) {
            player.update(tpf);
            // Actualizar la posición de la cámara para seguir al jugador
            Vector3f playerPos = player.getNode().getWorldTranslation();
            Vector3f camOffset = new Vector3f(0, 2, 5); // Offset de la cámara detrás y encima del jugador
            cam.setLocation(playerPos.add(camOffset)); // Establecer la posición de la cámara
        } else {
            // Incrementa el temporizador cuando se muestra la pantalla de Game Over
            gameOverTimer.update();
            if (gameOverTimer.getTimeInSeconds() >= 20.0f) {
                // Cierra la aplicación después de 3 segundos
                stop();
            }
        }
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        // Código de renderización
    }

    private void gameOver() {
        isGameOver = true;  // Actualiza el estado del juego
        
        BitmapText gameOverText = new BitmapText(guiFont, false);
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 4);
        gameOverText.setColor(ColorRGBA.Red); // Color del texto
        gameOverText.setText("¡Game Over!"); // Texto a mostrar
        gameOverText.setLocalTranslation(settings.getWidth() / 2f - gameOverText.getLineWidth() / 2f, settings.getHeight() / 2f, 0); // Posición del texto en la pantalla
        guiNode.attachChild(gameOverText);
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("GameOver") && isPressed) {
                gameOver();
            } else {
                player.onAction(name, isPressed, tpf);
            }
        }
    };
    
    // Clase del jugador
    class Player implements ActionListener {
        Node playerNode;
        Spatial playerModel;
        Vector3f walkDirection = new Vector3f();
        boolean left, right, forward, backward, jump;

    private void updateEnemies(float tpf) {
        Vector3f playerLocation = player.getNode().getLocalTranslation();

        // Si hay menos enemigos de los necesarios, crear nuevos
        if (enemies.size() < maxEnemies) {
            createEnemy();
        }

        for (int i = 0; i < enemies.size(); i++) {
            // Mover los enemigos hacia el jugador
            Enemy e = enemies.get(i);

            if (e.health <= 0) {
                enemies.remove(e);
                e.model.removeFromParent();
            }

            Vector3f enemyLocation = e.model.getLocalTranslation();
            Vector3f playerDirection = playerLocation.subtract(enemyLocation);
            e.model.move(playerDirection.mult(tpf).mult(.25f));

            // Si el enemigo está cerca, atacar al jugador
            float distance = enemyLocation.distance(playerLocation);
            if (distance < 5) {
                e.attack();
            }

            // Si e es amarillo o cian, hacer rojo
            if (e.isYellow) {
                e.endYellow();
            } else if (e.isCyan) {
                e.endCyan();
            }
        }
    }

    // Clase del enemigo
    class Enemy extends Node {

        long lastAttack = System.currentTimeMillis(); // Time of last attack
        long lastHit = System.currentTimeMillis();
        Node model;
        boolean isYellow = false;
        boolean isCyan = false;
        int health = 100; // Health of the enemy

        public Enemy(float x, float z) {
            Box b = new Box(1, 1, 1);
            Geometry geom = new Geometry("Enemy", b);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Red);
            geom.setMaterial(mat);

            model = new Node("EnemyNode");
            model.attachChild(geom);

            model.setLocalTranslation(x, 1, z);
            this.attachChild(model);
        }

        public void attack() {
            long now = System.currentTimeMillis();
            if (now - lastAttack >= 1000) { // Check if 1 second has passed since last attack
                player.health -= 10; // Reduce player health
                player.updateHealthColor();
                lastAttack = now;
            }
        }

        public void hit() {
            long now = System.currentTimeMillis();
            if (now - lastHit >= 1000) {
                this.health -= 10;
                updateHealthColor();
                lastHit = now;
            }
        }

        public void updateHealthColor() {
            Geometry geom = (Geometry) model.getChild("Enemy");
            Material mat = geom.getMaterial();

            if (health < 100 && health >= 60) {
                mat.setColor("Color", ColorRGBA.Yellow);
                isYellow = true;
            } else if (health < 60 && health >= 20) {
                mat.setColor("Color", ColorRGBA.Cyan);
                isYellow = false;
                isCyan = true;
            } else if (health < 20) {
                mat.setColor("Color", ColorRGBA.Red);
                isCyan = false;
            }
        }

        public void endYellow() {
            long now = System.currentTimeMillis();
            if (now - lastHit >= 2000) {
                Geometry geom = (Geometry) model.getChild("Enemy");
                Material mat = geom.getMaterial();
                mat.setColor("Color", ColorRGBA.Red);
                isYellow = false;
            }
        }

        public void endCyan() {
            long now = System.currentTimeMillis();
            if (now - lastHit >= 4000) {
                Geometry geom = (Geometry) model.getChild("Enemy");
                Material mat = geom.getMaterial();
                mat.setColor("Color", ColorRGBA.Red);
                isCyan = false;
            }
        }
    }

    // Clase del jugador
    class Player extends Node implements ActionListener {

        int health = 100;
        Node playerNode;
        CharacterControl characterControl;
        Vector3f walkDirection = new Vector3f();
        boolean left = false, right = false, up = false, down = false;

        public Player() {
            Box b = new Box(0.5f, 1.8f, 0.5f); // Tamanho do jugador
            Geometry geom = new Geometry("Player", b);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            geom.setMaterial(mat);

            playerNode = new Node("PlayerNode");
            playerNode.attachChild(geom);
            this.attachChild(playerNode);

            // Crear la forma de colisión para el jugador
            CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
            characterControl = new CharacterControl(capsuleShape, 0.5f);
            playerNode.addControl(characterControl);

            characterControl.setJumpSpeed(20);
            characterControl.setFallSpeed(30);
            characterControl.setGravity(30);
            characterControl.setPhysicsLocation(new Vector3f(0, 5, 0)); // Posición inicial del jugador
        }

        public Node getNode() {
            return playerNode;
        }

        public void updateHealthColor() {
            Geometry geom = (Geometry) playerNode.getChild("Player");
            Material mat = geom.getMaterial();

            if (health < 100 && health >= 60) {
                mat.setColor("Color", ColorRGBA.Yellow);
            } else if (health < 60 && health >= 20) {
                mat.setColor("Color", ColorRGBA.Cyan);
            } else if (health < 20) {
                mat.setColor("Color", ColorRGBA.Red);
            }
        }

        public void onAction(String binding, boolean isPressed, float tpf) {
            if (binding.equals("Left")) {
                left = isPressed;
            } else if (binding.equals("Right")) {
                right = isPressed;
            } else if (binding.equals("Forward")) {
                up = isPressed;
            } else if (binding.equals("Backward")) {
                down = isPressed;
            } else if (binding.equals("Jump")) {
                characterControl.jump();
            }
        }

        public void update(float tpf) {
            // Calcular la dirección de movimiento basada en la dirección de la cámara
            Vector3f camDir = cam.getDirection().clone().multLocal(0.1f);
            Vector3f camLeft = cam.getLeft().clone().multLocal(0.1f);

            walkDirection.set(0, 0, 0);
            if (left) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                walkDirection.addLocal(camDir);
            }
            if (down) {
                walkDirection.addLocal(camDir.negate());
            }

            characterControl.setWalkDirection(walkDirection);
        }
    }
}