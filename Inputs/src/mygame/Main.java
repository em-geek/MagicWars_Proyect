package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    private final static Trigger TRIGGER_COLOR = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    
    private final static Trigger TRIGGER_COLOR2 = new KeyTrigger(KeyInput.KEY_C);
    
    
    private final static String MAPPING_COLOR = "Toggle Color";
    private final static String MAPPING_ROTATE = "Rotate";

    private Geometry box01_geom;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addMapping(MAPPING_COLOR, TRIGGER_COLOR);
        inputManager.addMapping(MAPPING_COLOR, TRIGGER_COLOR2);
        

        inputManager.addListener(actionListener, new String[]{MAPPING_COLOR});
        inputManager.addListener(analogListener, new String[]{MAPPING_ROTATE});
        
        Box blue01 = new Box(1, 1, 1);
        box01_geom = new Geometry("box blue", blue01);
        Material box01_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box01_mat.setColor("Color", ColorRGBA.Blue);
        box01_geom.setMaterial(box01_mat);

        rootNode.attachChild(box01_geom);
    }
    
    private ActionListener actionListener = new ActionListener(){
        public void onAction(String name, boolean isPressed, float tpf){
            System.out.println("You triggered: "+name);
            if (name.equals(MAPPING_COLOR) && !isPressed){
                box01_geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
            }
        }
    };

    private AnalogListener analogListener = new AnalogListener(){
        public void onAnalog(String name, float intensity, float tpf){
            if(name.equals(MAPPING_ROTATE)){
                box01_geom.rotate(0, intensity, 0);
                System.out.println("You triggered: "+name+" , intensidad: " + intensity);
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}


