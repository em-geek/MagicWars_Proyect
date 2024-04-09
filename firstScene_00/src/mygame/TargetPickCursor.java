/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * TargetPickCursor - Es la evolucion del programa TargetPickCenter
 * 
 * Se complementara la practica con "pick a brick"
 * - Definiendo una mira y detectando coliciones, se identificara los objetos seleccionados
 * 
 * Se hace un segndo complemento con "pick a brick" (usando el mouse)
 * - En lugar de hacer una mira para identificar los objetos sobre los cuales se hace click, se 
 * hara visible el mouse en la escena para dar cuenta donde podriamos hacer click.
 * - Se removera la mira.
 * - Aplicar ray casting con el cursor del mouse
 * 
 * @author boyolu
 */
public class TargetPickCursor extends SimpleApplication {
    // Constantes triggers que representan los clicks de la barra y el mouse
    // Los triggers son los objetos que representan las entradas fisicas de los cliks o joystick
    private final static Trigger TRIGGER_COLOR = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    
    //Ya que una accion puede ser activada por mas de un input, se puede agregar otro Trigger
    private final static Trigger TRIGGER_COLOR2 = new KeyTrigger(KeyInput.KEY_C);
    
    //definimos las constantes que nos ayudaran a identificar las acciones por los triggers
    // Recuerda: Una accion puede tener mas de un trigger que la activa
    private final static String MAPPING_COLOR = "Toggle Color";
    private final static String MAPPING_ROTATE = "Rotate";
    
    //Aqui se declara la geometria para que los listener tengan puedan tener accesos al objeto 
    // al momento que lo mandan llamara en onAction y onAnalog
    private Geometry box01_geom;
    
    //Se define y hace estatico la malla que se podra replicar
    public static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    
    public static void main (String[] args){
        TargetPickCursor app = new TargetPickCursor();
        app.start();
                
    }
    /**
     * myBox regresa un geometry de una caja listo para agregarse a un nodo. 
     * @param name String del Nombre para identificar la geometry en el scene graph
     * @param loc Vector3f que indica la posicion dentro del nodo que se adguntara
     * @param color ColorRGBA que sera asingado al material de la caja
     * @return Geometry de una caja con los parametros especificados
     */
    private Geometry myBox(String name, Vector3f loc, ColorRGBA color){
        Geometry geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }
    
    
    @Override
    public void simpleInitApp(){
        //para hacer uso de los triggers y mapping se deben registrar en el inputManager
        //** Se utiliz el Rotate mapping para la mira del mouse en la escena
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addMapping(MAPPING_COLOR, TRIGGER_COLOR);
        //Para utilizar el segundo trigger se registra al inputManager
        inputManager.addMapping(MAPPING_COLOR, TRIGGER_COLOR2);
        
        // Para poder activar los mapping debemos estar escuchando para detectar el input
        inputManager.addListener(actionListener, new String[]{MAPPING_COLOR});
        inputManager.addListener(analogListener, new String[]{MAPPING_ROTATE});
        
        // El cursor del mouse esta escondido por defecto, por lo cual para hacerlo aparecer
        // se requiere la siguientes lineas
        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        
        Box blue01 = new Box(1,1,1);
        //Se modifico la posicion en donde se definicion del objeto box01_geom
        box01_geom = new Geometry("box blue", blue01);
        Material box01_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        box01_mat.setColor("Color", ColorRGBA.Blue);
        box01_geom.setMaterial(box01_mat);
        
        //rootNode.attachChild(box01_geom);
        
                
        // Utilizando el metodo y la malla definida de forma estatica, se hacen y adjuntan 
        // varias cajas a la escena
        rootNode.attachChild(this.myBox("Red Cube", new Vector3f(0, 1.5f, 0), ColorRGBA.Red));
        rootNode.attachChild(this.myBox("Blue Cube", new Vector3f(0, -1.5f, 0), ColorRGBA.Blue));
        rootNode.attachChild(this.myBox("Yellow Cube", new Vector3f(2.5f, 1.5f, 0), ColorRGBA.Yellow));
        rootNode.attachChild(this.myBox("Green Cube", new Vector3f(-2.5f, -1.5f, 0), ColorRGBA.Green));
    }    
    
    
    // Declaramos dos de los principales listeners que utiliza el motor de videojuego
    // Analiza cuál es la diferencia entre los dos tipos de listeners
    private final ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf){
                // En este método es donde se definira las condicones que dictaran la acción 
                System.out.println("you triggered : "+name);
                // Se evalua si es que se ha recibido el input
                // !isPressed evalua el input para activar cuando se libere el trigger, es 
                // decir, cuando se suelta el boton del mouse
                if (name.equals(MAPPING_COLOR)&& !isPressed){
                    box01_geom.getMaterial().setColor("Color", ColorRGBA.randomColor());
                }
            }
    };
    //Utilizamos el listener analogico ya que la accion de rotacion sera una accion continua.
    private final AnalogListener analogListener = new AnalogListener(){
        @Override
        public void onAnalog(String name, float intensity, float tpf){
            // creamos una lista vacia de resultado para las colisiones
            CollisionResults results = new CollisionResults();
            // Al hacer uso del mouse, se requiere de la posicion 2D de éste
            Vector2f click2d = inputManager.getCursorPosition();
            // Convertimos el vector2D en uno 3D para definir el origen del ray, ya que 
            // un ray requiere vector 3D
            Vector3f click3d = cam.getWorldCoordinates(click2d, 0f);
        }
    };
    
    @Override
    public void simpleUpdate(float tpf){
        
    }
    
}
