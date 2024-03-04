package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    //Rotaciones al sol
    public Spatial solS = null;
    public Spatial mercurioS = null;
    public Spatial venusS = null;
    public Spatial tierraS = null;
    
    //Rotaciones Unicas
    public Spatial mercurioU = null;
    public Spatial venusU = null;
    public Spatial tierraU = null;
    
    //Rotaciones lunares
    public Spatial lunaTierra = null;
    
    //Rotaciones lunares unicas
    public Spatial lunaU = null;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //Creacion del sol
        Node solNodo = new Node("solNodo");
        Sphere sol = new Sphere(30, 30, 1);
        Geometry solGeo = new Geometry("Sphere", sol);

        Texture solTexture = assetManager.loadTexture("Textures/sol.jpg");

        // Crear un nuevo material y establecer la textura
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", solTexture);

        // Asignar el material a tu geometría
        solGeo.setMaterial(mat);
        solNodo.attachChild(solGeo);
        rootNode.attachChild(solNodo);
        
        
        
        //Creacion de Mercurio
        Node mercurioNodo = new Node("mercurioNodo");
        Node mercurioNodoR = new Node("mercurioNodoR");
        Sphere mercurio = new Sphere(30, 30, 1);
        Geometry mercurioGeo = new Geometry("mercurioGeo", mercurio);

        Texture mercurioTexture = assetManager.loadTexture("Textures/mercurio.jpg");

        // Crear un nuevo material y establecer la textura
        Material matMercurio = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matMercurio.setTexture("ColorMap", mercurioTexture);

        // Asignar el material a tu geometría
        mercurioGeo.setMaterial(matMercurio);
        mercurioGeo.move(0, 0, 4);
        
        mercurioNodoR.attachChild(mercurioGeo);
        mercurioNodo.attachChild(mercurioNodoR);
        rootNode.attachChild(mercurioNodo);
        
        
        
        //Creacion de Venus
        Node venusNodo = new Node("venusNodo");
        Node venusNodoR = new Node("venusNodoR");
        Sphere venus = new Sphere(30, 30, 1);
        Geometry venusGeo = new Geometry("venusGeo", venus);

        Texture venusTexture = assetManager.loadTexture("Textures/venus.jpg");

        // Crear un nuevo material y establecer la textura
        Material matVenus = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matVenus.setTexture("ColorMap", venusTexture);

        // Asignar el material a tu geometría
        venusGeo.setMaterial(matVenus);
        venusGeo.move(1, 0, -7);
        
        venusNodoR.attachChild(venusGeo);
        venusNodo.attachChild(venusNodoR);
        rootNode.attachChild(venusNodo);
        
        
        
        //Creacion de Tierra
        Node tierraNodo = new Node("tierraNodo");
        Node tierraNodoR = new Node("tierraNodoR");
        Sphere tierra = new Sphere(30, 30, 1);
        Geometry tierraGeo = new Geometry("tierraGeo", tierra);

        Texture tierraTexture = assetManager.loadTexture("Textures/tierra.jpg");

        // Crear un nuevo material y establecer la textura
        Material matTierra = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTierra.setTexture("ColorMap", tierraTexture);

        // Asignar el material a tu geometría
        tierraGeo.setMaterial(matTierra);
        tierraGeo.move(0, 0, 0);
       
        
        Node lunaNodo = new Node("lunaNodo");
        tierraNodoR.move(3, 0, -10);
        Sphere luna = new Sphere(30, 30, 0.1f);
        Geometry lunaT = new Geometry("lunaT", luna);
        
        Texture lunaTexture = assetManager.loadTexture("Textures/luna.jpg");

        // Crear un nuevo material y establecer la textura
        Material matLuna = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matLuna.setTexture("ColorMap", lunaTexture);

        // Asignar el material a tu geometría
        lunaT.setMaterial(matLuna);
        lunaT.move(0, 0, 2);
        
        rootNode.attachChild(tierraNodo);
        tierraNodo.attachChild(tierraNodoR);
        tierraNodoR.attachChild(tierraGeo);
        tierraNodoR.attachChild(lunaNodo);
        lunaNodo.attachChild(lunaT);
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        //Rotaciones al sol
        mercurioS = rootNode.getChild("mercurioNodo");
        solS = rootNode.getChild("solNodo");
        venusS = rootNode.getChild("venusNodo");
        tierraS = rootNode.getChild("tierraNodo");  
        
        mercurioS.rotate(0, tpf/4, 0);
        solS.rotate(0, tpf, 0);
        venusS.rotate(0, tpf/8, 0);
        tierraS.rotate(0, tpf/3, 0);
        
        //Rotaciones unicas
        //Mercurio
        Node mercurioParent = (Node) rootNode.getChild("mercurioNodo"); // Convertir a Node
        if (mercurioParent != null) {
            mercurioU = mercurioParent.getChild("mercurioGeo");
            if (mercurioU != null) {
                mercurioU.rotate(0, tpf, 0);
            }
        }
        
        //Venus
        Node venusParent = (Node) rootNode.getChild("venusNodo"); // Convertir a Node
        if (venusParent != null) {
            venusU = venusParent.getChild("venusGeo");
            if (venusU != null) {
                venusU.rotate(0, tpf/8, 0);
            }
        }
        
        //Tierra
        Node tierraParent = (Node) rootNode.getChild("tierraNodo"); // Convertir a Node
        if (tierraParent != null) {
            tierraU = tierraParent.getChild("tierraGeo");
            if (tierraU != null) {
                tierraU.rotate(0, tpf/2, 0); // Rotación en sentido contrario a la de la tierra
                Node lunaParent = (Node) tierraParent.getChild("tierraNodoR");
                if (lunaParent != null){
                    //Rotacion lunar a la tierra
                    lunaTierra = lunaParent.getChild("lunaNodo");
                    lunaTierra.rotate(0, -tpf, 0);
                    //Rotacion lunar unica
                    lunaU = lunaParent.getChild("lunaT");
                    lunaU.rotate(0, -tpf, 0);
                }
           }
        }
      
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
