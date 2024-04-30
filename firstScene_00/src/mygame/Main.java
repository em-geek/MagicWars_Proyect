package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 * 
 * Se desarrollará la práctica del libro jMe beginners Guide de Ruth Kusterer. En esta práctica se 
 * desarrollara un juego simple de defender la torre. En este tipo de juego el usuario 
 * debera evitar que los oponentes (NPC) lleguen a la torre. Este juego es similar a 
 * Plantas contra zombis.
 * El demo se jugara en una perspectiva de tercera persona; lo que significa que el jugador 
 * observaria hacia el terreno desde arriba y hara click alas tores para asignar municiones 
 * de acuerdo al presupuesto que tenga el jugador.
 * 
 * 1: Modifica los settings para especificar una imagen propia del juego y el nombre de 
 * este sera "My Tower Defense Demo"
 * 2: Mantener activo la iteraccion con la "flyCam" es decir no modificar por ahora "settings.useInput()"
 * 3: Crear una caja plana de 33-WU para que sea el piso(floor)
 * 4: Crear un "playerNode", un "towerNode", un "creepNode", y adjuntarlos al "rootNode".
 * 5: Crear las geometrias; una caja acostada amarilla representando la base del jugador, 
 * una caja alta verde que representara una torre, y una cubo pequeño negro que representa un creep.
 * 6: Adjunta las geometrias al playerNode, al towerNode, y al creepNode respectivamente. 
 * 7: Posiciona la gemetria de la base en el origen.Esto permitira orientares.
 * 8: Posiciona una tore a la izquierda y otra a la derecha en frente de la base, y algunos pequeños 
 * cubos creep a lo lejos a lo largo del eje z
 * 9: Agrega el codigo que desactiva el objeto "StatsView"
 * @author cboyain
 */
public class Main extends SimpleApplication {
    public static final Quaternion PITCH090 = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(1,0,0));
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true); //Creamos el objeto para controlar las especificaciones
        settings.setTitle("My Tower Defense Demo"); //Cambiamos el nombre de la ventana 
        //Integramos una imagen personal a la pantalla de inicio
        settings.setSettingsDialogImage("Interface/towerSplash.png");
        //modificar la resolucion 
        //useInput establece si deseamos reaccionar a las entradas del mouse o teclado
        //settings.useInput(false);
        Main app = new Main();
        app.setSettings(settings);//Aplicamos las especificaciones a la app
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //Para controlar si se oculta la informacion de los objetos, mallas y sombras
        //setDisplayFps(false);
        //setDisplayStatView(false);
        
        //El objeto flayCma esta instanciado por defecto, al extender SimpleApplication
        flyCam.setMoveSpeed(1.8f); //Determinamos que la camara se desplace a una mayor velocidad
        
        //Camibaremso la ubiccion y rotacion de la camara para dar la perspectiva que requiere el juego
        cam.setLocation(new Vector3f(0, 40, 15)); 
        cam.setRotation(PITCH090);
        
        Node playerNode = new Node("player_node"), toweNode = new Node("tower_node"), creepNode = new Node("creep_node");
        
        //Se define la caja de color naranja que sera el piso
        // Recuerda tecla Q Mueve hacia arriba la camara
        // tecla Z mueve hacia abajo la camara
        Box floor_mesh = new Box(20, 0.5f, 33);
        Geometry floor_geom = new Geometry("floor", floor_mesh );
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.setColor("Color", ColorRGBA.Orange); 
        floor_geom.setMaterial(floor_mat);
        //Utilizaremos las dimensiones de la malla del piso para definir la posicion de unos elementos
        floor_geom.setLocalTranslation(0, 0, floor_mesh.zExtent-(floor_mesh.zExtent/10));
        
        // Se define las dimensiones de la base, en funcion de las dimensiones del piso
        Box base_mesh = new Box(floor_mesh.zExtent/3,floor_mesh.zExtent/6,1);
        Geometry base_geom = new Geometry("base", base_mesh);
        Material base_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        base_mat.setColor("Color", ColorRGBA.Yellow);
        base_geom.setMaterial(base_mat);
        base_geom.setLocalTranslation(0, base_mesh.yExtent, 0);
        
        Box tower_mesh = new Box(1,(base_mesh.yExtent/4)*3,1);
        Geometry tower_geom = new Geometry("tower01", tower_mesh);
        Material tower_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        tower_mat.setColor("Color", ColorRGBA.Green);
        tower_geom.setMaterial(tower_mat);
        tower_geom.setLocalTranslation(new Vector3f(base_mesh.getXExtent()+2, tower_mesh.yExtent, 4*tower_mesh.zExtent));
        
        Geometry tower02_geom = new Geometry("tower00", tower_mesh);
        tower02_geom.setMaterial(tower_mat);
        tower02_geom.setLocalTranslation(new Vector3f(-(base_mesh.getXExtent()+2), tower_mesh.yExtent, 4*tower_mesh.zExtent));
        
        Box creep_mesh = new Box(1,1,1);
        Geometry creep_geom = new Geometry("creep", creep_mesh);
        Material creep_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        creep_mat.setColor("Color", ColorRGBA.Gray);
        creep_geom.setMaterial(creep_mat);
        creep_geom.setLocalTranslation(-2, creep_mesh.yExtent, ((floor_mesh.zExtent*2)/10)*8);
        
        Geometry creep02_geom = new Geometry("creep02", creep_mesh);
        creep02_geom.setMaterial(creep_mat);
        //El componente z de la traslacion se debe 
        creep02_geom.setLocalTranslation(2, creep_mesh.yExtent, ((floor_mesh.zExtent*2)/10)*8);

        playerNode.attachChild(base_geom);
        toweNode.attachChild(tower_geom);
        toweNode.attachChild(tower02_geom);
        creepNode.attachChild(creep_geom);
        creepNode.attachChild(creep02_geom);
        
        rootNode.attachChild(floor_geom);
        rootNode.attachChild(playerNode);
        rootNode.attachChild(toweNode);
        rootNode.attachChild(creepNode);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
