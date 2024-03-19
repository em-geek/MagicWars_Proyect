package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    //Quaterniones
     Quaternion roll90  = new Quaternion().fromAngleAxis(FastMath.PI/256,   new Vector3f(3,1,1));
    
    //Movimientos
    public Spatial giroCubo = null;
    
    
    //Codigo de juego
    public static void main(String[] args) {
        
        AppSettings settings = new AppSettings(true);
        settings.setSettingsDialogImage("Interface/logo.png");
        settings.setTitle("Tower Defense DEMO");
        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
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
            rootNode.attachChild(estrellaGeom);
        }
        
            /** Uses Texture from jme3-test-data library! */
        ParticleEmitter fireEffect = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
        Material fireMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        //fireMat.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fireEffect.setMaterial(fireMat);
        fireEffect.setImagesX(2); fireEffect.setImagesY(2); // 2x2 texture animation
        fireEffect.setEndColor( new ColorRGBA(1f, 0f, 0f, 1f) );   // red
        fireEffect.setStartColor( new ColorRGBA(1f, 1f, 0f, 0.5f) ); // yellow
        fireEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
        fireEffect.setStartSize(0.6f);
        fireEffect.setEndSize(0.1f);
        fireEffect.setGravity(0f,0f,0f);
        fireEffect.setLowLife(0.5f);
        fireEffect.setHighLife(3f);
        fireEffect.getParticleInfluencer().setVelocityVariation(0.3f);
        rootNode.attachChild(fireEffect);

        
        
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/cara.png"));
        geom.setMaterial(mat);

        rootNode.attachChild(geom);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        float time = timer.getTimeInSeconds();
        giroCubo = rootNode.getChild("Box");
        giroCubo.rotate(roll90);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
