package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
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
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class UserInput extends SimpleApplication {
    //Rotaciones al sol
    public Spatial solS = null;
    public Spatial mercurioS = null;
    public Spatial venusS = null;
    public Spatial tierraS = null;
    public Spatial marteS = null;
    public Spatial jupiterS = null;
    public Spatial saturnoS = null;
    
    //Rotaciones Unicas
    public Spatial mercurioU = null;
    public Spatial venusU = null;
    public Spatial tierraU = null;
    public Spatial marteU = null;
    public Spatial jupiterU = null;
    public Spatial saturnoU = null;
    
    //Rotaciones lunares
    public Spatial lunaTierra = null;
    public Spatial lunasMarte = null;
    public Spatial lunasJupiter = null;
    public Spatial lunasSaturno = null;
    
    //Rotaciones lunares unicas
    public Spatial lunaU = null;
    public Spatial deimos = null;
    public Spatial fobos = null;
    public Spatial ganimedes = null;
    public Spatial europa = null;
    public Spatial calisto = null;
    public Spatial tetis = null;
    public Spatial mimis = null;
    public Spatial encetado = null;

    public static void main(String[] args) {
        UserInput app = new UserInput();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Node estrellasNodo = new Node("estrellasNodo");

        // Generar estrellas
        int cantidadEstrellas = 100; // ajusta según sea necesario
        for (int i = 0; i < cantidadEstrellas; i++) {
            // Generar una posición aleatoria en el espacio tridimensional
            float x = FastMath.nextRandomFloat() * 100 - 50; // ajusta según sea necesario
            float y = FastMath.nextRandomFloat() * 100 - 50; // ajusta según sea necesario
            float z = FastMath.nextRandomFloat() * 100 - 50; // ajusta según sea necesario

            // Crear una geometría de punto (estrella)
            Box estrella = new Box(0.1f, 0.1f, 0.1f); // Tamaño del punto (ajusta según sea necesario)
            Geometry estrellaGeom = new Geometry("estrella_" + i, estrella);

            // Ubicar la estrella en la posición aleatoria generada
            estrellaGeom.setLocalTranslation(x, y, z);

            // Crear un material para la estrella (punto blanco)
            Material matEstrella = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            matEstrella.setColor("Color", ColorRGBA.White); // Estrellas blancas
            estrellaGeom.setMaterial(matEstrella);

            // Agregar la geometría de la estrella al nodo de estrellas
            estrellasNodo.attachChild(estrellaGeom);
        }

        // Agregar el nodo de estrellas al rootNode
        rootNode.attachChild(estrellasNodo);
        
        cam.setLocation(new Vector3f(0f, 20f, 40f));
        
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
        lunaT.move(0, 0, 1.5f);
        
        rootNode.attachChild(tierraNodo);
        tierraNodo.attachChild(tierraNodoR);
        tierraNodoR.attachChild(tierraGeo);
        tierraNodoR.attachChild(lunaNodo);
        lunaNodo.attachChild(lunaT);
        
        
        
        //Creacion de Marte
        Node marteNodo = new Node("marteNodo");
        Node marteNodoR = new Node("marteNodoR");
        Sphere marte = new Sphere(30, 30, 1);
        Geometry marteGeo = new Geometry("marteGeo", marte);

        Texture marteTexture = assetManager.loadTexture("Textures/marte.jpg");

        // Crear un nuevo material y establecer la textura
        Material matMarte = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matMarte.setTexture("ColorMap", marteTexture);

        // Asignar el material a tu geometría
        marteGeo.setMaterial(matMarte);
        marteGeo.move(0, 0, 0);
       
        
        Node lunasMarte = new Node("lunasMarte");
        Sphere deimos = new Sphere(30, 30, 0.1f);
        Geometry lunaDeimos = new Geometry("lunaDeimos", deimos);

        // Asignar el material a tu geometría
        lunaDeimos.setMaterial(matLuna);
        lunaDeimos.move(0, 0, 2);

        Sphere fobos = new Sphere(30, 30, 0.1f);
        Geometry lunaFobos = new Geometry("lunaFobos", fobos);

        // Asignar el material a tu geometría
        lunaFobos.setMaterial(matLuna);
        lunaFobos.move(1, 0, 2);
        
        marteNodoR.move(3, 0, -16);
        
        rootNode.attachChild(marteNodo);
        marteNodo.attachChild(marteNodoR);
        marteNodoR.attachChild(marteGeo);
        marteNodoR.attachChild(lunasMarte);
        lunasMarte.attachChild(lunaDeimos);
        lunasMarte.attachChild(lunaFobos);
         
        
        
        //Creacion de Jupiter
        Node jupiterNodo = new Node("jupiterNodo");
        Node jupiterNodoR = new Node("jupiterNodoR");
        Sphere jupiter = new Sphere(30, 30, 1);
        Geometry jupiterGeo = new Geometry("jupiterGeo", jupiter);

        Texture jupiterTexture = assetManager.loadTexture("Textures/jupiter.jpg");

        // Crear un nuevo material y establecer la textura
        Material matJupiter = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matJupiter.setTexture("ColorMap", jupiterTexture);

        // Asignar el material a tu geometría
        jupiterGeo.setMaterial(matJupiter);
        jupiterGeo.move(0, 0, 0);
       
        
        Node lunasJupiter = new Node("lunasJupiter");
        Sphere ganimedes = new Sphere(30, 30, 0.1f);
        Geometry lunaGanimedes = new Geometry("lunaGanimedes", ganimedes);

        // Asignar el material a tu geometría
        lunaGanimedes.setMaterial(matLuna);
        lunaGanimedes.move(0, 0, 2);

        Sphere europa = new Sphere(30, 30, 0.1f);
        Geometry lunaEuropa = new Geometry("lunaEuropa", europa);

        // Asignar el material a tu geometría
        lunaEuropa.setMaterial(matLuna);
        lunaEuropa.move(1, 0, 2);
        
        Sphere calisto = new Sphere(30, 30, 0.1f);
        Geometry lunaCalisto = new Geometry("lunaCalisto", calisto);

        // Asignar el material a tu geometría
        lunaCalisto.setMaterial(matLuna);
        lunaCalisto.move(3, 1, 2);
        
        jupiterNodoR.move(3, 0, -19);
        
        rootNode.attachChild(jupiterNodo);
        jupiterNodo.attachChild(jupiterNodoR);
        jupiterNodoR.attachChild(jupiterGeo);
        jupiterNodoR.attachChild(lunasJupiter);
        lunasJupiter.attachChild(lunaGanimedes);
        lunasJupiter.attachChild(lunaEuropa);
        lunasJupiter.attachChild(lunaCalisto);
        
        
        
        //Creacion de Saturno
        Node saturnoNodo = new Node("saturnoNodo");
        Node saturnoNodoR = new Node("saturnoNodoR");
        Sphere saturno = new Sphere(30, 30, 1);
        Geometry saturnoGeo = new Geometry("saturnoGeo", saturno);

        Texture saturnoTexture = assetManager.loadTexture("Textures/saturno.jpg");

        // Crear un nuevo material y establecer la textura
        Material matSaturno = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matSaturno.setTexture("ColorMap", saturnoTexture);

        // Asignar el material a tu geometría
        saturnoGeo.setMaterial(matSaturno);
        saturnoGeo.move(0, 0, 0);
       
        
        Node lunasSaturno = new Node("lunasSaturno");
        Sphere tetis = new Sphere(30, 30, 0.1f);
        Geometry lunaTetis = new Geometry("lunaTetis", tetis);

        // Asignar el material a tu geometría
        lunaTetis.setMaterial(matLuna);
        lunaTetis.move(0, 0, 2);

        Sphere mimis = new Sphere(30, 30, 0.1f);
        Geometry lunaMimis = new Geometry("lunaMimis", mimis);

        // Asignar el material a tu geometría
        lunaMimis.setMaterial(matLuna);
        lunaMimis.move(1, 0, 2);
        
        Sphere encetado = new Sphere(30, 30, 0.1f);
        Geometry lunaEncetado = new Geometry("lunaEncetado", encetado);

        // Asignar el material a tu geometría
        lunaEncetado.setMaterial(matLuna);
        lunaEncetado.move(3, 1, 2);
        
        saturnoNodoR.move(3, 0, -26);
        
        rootNode.attachChild(saturnoNodo);
        saturnoNodo.attachChild(saturnoNodoR);
        saturnoNodoR.attachChild(saturnoGeo);
        saturnoNodoR.attachChild(lunasSaturno);
        lunasSaturno.attachChild(lunaTetis);
        lunasSaturno.attachChild(lunaMimis);
        lunasSaturno.attachChild(lunaEncetado);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //Rotaciones al sol
        mercurioS = rootNode.getChild("mercurioNodo");
        solS = rootNode.getChild("solNodo");
        venusS = rootNode.getChild("venusNodo");
        tierraS = rootNode.getChild("tierraNodo");  
        marteS = rootNode.getChild("marteNodo"); 
        jupiterS = rootNode.getChild("jupiterNodo");
        saturnoS = rootNode.getChild("saturnoNodo");
        
        mercurioS.rotate(0, tpf/4, 0);
        solS.rotate(0, tpf, 0);
        venusS.rotate(0, tpf/8, 0);
        tierraS.rotate(0, tpf/3, 0);
        marteS.rotate(0, tpf/5, 0);
        jupiterS.rotate(0, tpf*1.1f, 0);
        saturnoS.rotate(0, tpf/2, 0);
        
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
      
        //Marte
        Node marteParent = (Node) rootNode.getChild("marteNodo"); // Convertir a Node
        if (marteParent != null) {
            marteU = marteParent.getChild("marteGeo");
            if (marteU != null) {
                marteU.rotate(0, tpf/2, 0); // Rotación en sentido contrario a la de la tierra
                Node lunaMarteParent = (Node) marteParent.getChild("marteNodoR");
                if (lunaMarteParent != null){
                    //Rotacion lunar a la tierra
                    lunasMarte = lunaMarteParent.getChild("lunasMarte");
                    lunasMarte.rotate(0, -tpf, 0);
                    //Rotacion lunar unica
                    deimos = lunaMarteParent.getChild("lunaDeimos");
                    deimos.rotate(0, -tpf, 0);
                    fobos = lunaMarteParent.getChild("lunaFobos");
                    fobos.rotate(0, -tpf, 0);
                }
           }
        }
        
        //Jupiter
        Node jupiterParent = (Node) rootNode.getChild("jupiterNodo"); // Convertir a Node
        if (jupiterParent != null) {
            jupiterU = jupiterParent.getChild("jupiterGeo");
            if (jupiterU != null) {
                jupiterU.rotate(0, tpf/2, 0); // Rotación en sentido contrario a la de la tierra
                Node lunaJupiterParent = (Node) jupiterParent.getChild("jupiterNodoR");
                if (lunaJupiterParent != null){
                    
                    lunasJupiter = lunaJupiterParent.getChild("lunasJupiter");
                    lunasJupiter.rotate(0, -tpf*2, 0);
                    //Rotacion lunar unica
                    ganimedes = lunaJupiterParent.getChild("lunaGanimedes");
                    ganimedes.rotate(0, -tpf, 0);
                    europa = lunaJupiterParent.getChild("lunaEuropa");
                    europa.rotate(0, -tpf, 0);
                    calisto = lunaJupiterParent.getChild("lunaCalisto");
                    calisto.rotate(0, -tpf, 0);
                }
           }
        }
        
        //Saturno
        Node saturnoParent = (Node) rootNode.getChild("saturnoNodo"); // Convertir a Node
        if (saturnoParent != null) {
            saturnoU = saturnoParent.getChild("saturnoGeo");
            if (saturnoU != null) {
                saturnoU.rotate(0, tpf/2, 0); // Rotación en sentido contrario a la de la tierra
                Node lunaSaturnoParent = (Node) saturnoParent.getChild("saturnoNodoR");
                if (lunaSaturnoParent != null){
                    
                    lunasSaturno = lunaSaturnoParent.getChild("lunasSaturno");
                    lunasSaturno.rotate(0, -tpf*2, 0);
                    //Rotacion lunar unica
                    tetis = lunaSaturnoParent.getChild("lunaTetis");
                    tetis.rotate(0, -tpf, 0);
                    mimis = lunaSaturnoParent.getChild("lunaMimis");
                    mimis.rotate(0, -tpf, 0);
                    encetado = lunaSaturnoParent.getChild("lunaEncetado");
                    encetado.rotate(0, -tpf, 0);
                }
           }
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
