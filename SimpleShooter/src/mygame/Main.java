package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    Player player;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    //Run initialization methods on start
    @Override
    public void simpleInitApp() {
        initPlayer();
        setUpKeys();
    }

    //Initialize the player
    private void initPlayer() {
        player = new Player();
        rootNode.attachChild(player.getNode());
        player.getNode().setLocalTranslation(0, 10, 0); // Initial position of the player
    }

    //Sets up the keys for player movement
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(player, "Left", "Right", "Jump");
    }

    //Updates the player
    @Override
    public void simpleUpdate(float tpf) {
        player.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    //Player class
    class Player implements ActionListener {
        Node playerNode;
        Vector3f walkDirection = new Vector3f();
        boolean left, right, jump;
        boolean onGround = false; // Indicates if the player is on the ground

        public Player() {
            playerNode = new Node();
        }

        //Getter for the player's node
        public Node getNode() {
            return playerNode;
        }

        //Method to update the player's position based on input and gravity
        public void update(float tpf) {
            // Apply gravity
            if (!onGround) {
                walkDirection.y -= 30 * tpf;
            }

            // Update player's position
            playerNode.move(walkDirection.mult(tpf));
        }

        //Method to handle player input
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

        //Update walk direction based on input
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