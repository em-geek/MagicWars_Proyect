package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;

public class Main extends SimpleApplication {

    Player player;
    BulletAppState fisica;
    ArrayList<Enemy> enemies;
    int maxEnemies = 5; // Máximo número de enemigos en el juego
    boolean isGameOver = false;
    private Timer gameOverTimer;
    
    //Vida de las balas
    private float projectileLifetime = 3.0f;
    
    // Variables para el cooldown entre disparos
    private float shootCooldown = 0.5f; // Tiempo de enfriamiento entre disparos en segundos
    private float timeSinceLastShot = 0f; // Tiempo transcurrido desde el último disparo

    // Variables para el límite de balas en pantalla
    private int maxProjectiles = 3; // Máximo número de balas en pantalla
    private int projectilesOnScreen = 0; // Número actual de balas en pantalla
    
    //Bitmaps para disparos
    private BitmapText cooldownText;
    private BitmapText projectilesText;
    
    //Audios
    private AudioNode fire;
    private AudioNode combustion;
    
    
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
        // Inicializar los textos de la interfaz de usuario
        cooldownText = new BitmapText(guiFont, false);
        cooldownText.setSize(guiFont.getCharSet().getRenderedSize());
        cooldownText.setColor(ColorRGBA.White);
        cooldownText.setText("Cooldown: " + shootCooldown); // Mostrar el tiempo de cooldown inicial
        cooldownText.setLocalTranslation(10, settings.getHeight() - 10, 0);
        guiNode.attachChild(cooldownText);

        projectilesText = new BitmapText(guiFont, false);
        projectilesText.setSize(guiFont.getCharSet().getRenderedSize());
        projectilesText.setColor(ColorRGBA.White);
        projectilesText.setText("Projectiles: " + projectilesOnScreen); // Mostrar el número inicial de balas
        projectilesText.setLocalTranslation(10, settings.getHeight() - 30, 0);
        guiNode.attachChild(projectilesText);
        
        //Inicializar sonidos
        AssetManager assetManager = this.assetManager;
        fire = new AudioNode(assetManager, "Sounds/fire.ogg", AudioData.DataType.Buffer);
        fire.setVolume(0.5f);
        combustion = new AudioNode(assetManager, "Sounds/combustion.ogg", AudioData.DataType.Buffer);
        combustion.setVolume(0.5f);

        
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
        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Left", "Right", "Forward", "Backward", "Jump", "GameOver", "Shoot");
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
        timeSinceLastShot += tpf;
        player.update(tpf);
        updateEnemies(tpf);
        
        // Actualizar el tiempo de cooldown restante
        if (timeSinceLastShot < shootCooldown) {
            cooldownText.setText("Cooldown: " + String.format("%.1f", shootCooldown - timeSinceLastShot));
        } else {
            cooldownText.setText("Cooldown: Ready");
        }

        // Actualizar el número de balas en pantalla
        projectilesText.setText("Projectiles: " + (3 - projectilesOnScreen));
        
        if (!isGameOver) {
            player.update(tpf);
            // Actualizar la posición de la cámara para seguir al jugador
            Vector3f playerPos = player.getNode().getWorldTranslation();
            Vector3f camOffset = new Vector3f(0, 2, 5); // Offset de la cámara detrás y encima del jugador
            cam.setLocation(player.getNode().getWorldTranslation().add(0, 1.8f, 0)); // Altura de los ojos del jugador
        } else {
            // Incrementa el temporizador cuando se muestra la pantalla de Game Over
            gameOverTimer.update();
            if (gameOverTimer.getTimeInSeconds() >= 30.0f) {
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
            } else if (name.equals("Shoot") && isPressed) {
                player.shoot(); // Llamar al método de disparo
            } else {
                player.onAction(name, isPressed, tpf);
            }
        }
    };

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
             // Load the ghost model
            model = (Node) assetManager.loadModel("Models/fantasma/fantasma.j3o");

            // Set the position of the model
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
            Vector3f camDir = cam.getDirection().clone().multLocal(0.2f);
            Vector3f camLeft = cam.getLeft().clone().multLocal(0.2f);

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
        
        public void shoot() {
            // Verificar el cooldown
            if (timeSinceLastShot < shootCooldown) {
                return; // Todavía en cooldown, no se puede disparar
            }
            
            // Verificar el límite de balas en pantalla
            if (projectilesOnScreen >= maxProjectiles) {
                return; // Límite alcanzado, no se puede disparar más balas
            }
            
            
            // Crear un nodo para contener la geometría del proyectil y el emisor de partículas
            Node projectileNode = new Node("ProjectileNode");

            // Crear una esfera como proyectil
            Sphere sphere = new Sphere(16, 16, 0.2f);
            Geometry projectileGeom = new Geometry("Projectile", sphere);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Green); // Color del proyectil
            projectileGeom.setMaterial(mat);

            // Posicionar el proyectil en la posición del jugador
            projectileGeom.setLocalTranslation(playerNode.getWorldTranslation().add(0, 1.8f, 0));

            // Añadir un control de cuerpo rígido al proyectil
            RigidBodyControl projectileControl = new RigidBodyControl(1f);
            projectileGeom.addControl(projectileControl);
            fisica.getPhysicsSpace().add(projectileControl);

            // Aplicar una fuerza para lanzar el proyectil
            Vector3f direction = cam.getDirection().clone().mult(25); // Dirección y velocidad del disparo
            projectileControl.setLinearVelocity(direction);

            // Añadir la geometría del proyectil al nodo del proyectil
            projectileNode.attachChild(projectileGeom);

            // Crear y configurar el emisor de partículas
            ParticleEmitter fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
            Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
            //fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
            fireEffect.setMaterial(fireMat);
            fireEffect.setImagesX(2); 
            fireEffect.setImagesY(2); // 2x2 texture animation
            fireEffect.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));   // red
            fireEffect.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f)); // yellow
            fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
            fireEffect.setStartSize(0.6f);
            fireEffect.setEndSize(0.1f);
            fireEffect.setGravity(0f, 0f, 0f);
            fireEffect.setLowLife(0.5f);
            fireEffect.setHighLife(3f);
            fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);

            // Añadir el emisor de partículas al nodo del proyectil
            projectileNode.attachChild(fireEffect);

            // Añadir el nodo del proyectil al nodo raíz
            rootNode.attachChild(projectileNode);

            // Mantener una referencia a la hora de creación de la bala
            final float startTime = getTimer().getTimeInSeconds(); // <-- Se declara aquí

            // Reiniciar el tiempo desde el último disparo
            timeSinceLastShot = 0f;
            
            //Añade un proyectil a la pantalla
            projectilesOnScreen = projectilesOnScreen + 1;
            fire.play();
            
            // Agregar un controlador de actualización para ajustar continuamente la posición del emisor de partículas
            projectileNode.addControl(new AbstractControl() {  
                protected void controlUpdate(float tpf) {
                    combustion.play();
                    // Obtener la posición actual del proyectil
                    Vector3f projectilePos = projectileGeom.getWorldTranslation();
                    // Establecer la posición del emisor de partículas para que coincida con la posición del proyectil
                    fireEffect.setLocalTranslation(projectilePos);
                    
                    // Calcular el tiempo transcurrido desde la creación de la bala
                    float currentTime = getTimer().getTimeInSeconds();
                    float elapsedTime = currentTime - startTime;

                    // Verificar si la bala ha excedido su tiempo de vida
                    if (elapsedTime >= projectileLifetime) {
                        // Eliminar la bala del juego
                        rootNode.detachChild(projectileNode);
                        fisica.getPhysicsSpace().remove(projectileControl);
                        // Deshabilitar este controlador de actualización
                        projectileNode.removeControl(this);
                        projectilesOnScreen = projectilesOnScreen - 1;
                        combustion.stop();
                    }
                    
                }

                protected void controlRender(RenderManager rm, ViewPort vp) {
                    // No se utiliza para este propósito
                }
            });
            
            
            
        }

    }
}


