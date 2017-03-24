package me.boxcubed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GameScreen implements Screen{
    public Environment environment;
    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    public Model model;
    List<ModelPack> models;
    List<ModelPack> deleteModels=new ArrayList<>();
    Texture tex;
    ModelPack player;
    AssetManager assets;
 public GameScreen() {
	 create();
}
    public void create() {
    	models=new ArrayList<>();
    	tex=new Texture("logo.png");
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 10f, 0f);
        cam.lookAt(0,0,-50);
        cam.near = 1f;
        cam.far = 3000f;
        cam.update();
        assets=new AssetManager();
        assets.load("models/ship/ship.g3db", Model.class);
        while(!assets.update()){}
        
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
        		/*new Material(TextureAttribute.createDiffuse(tex))*/new Material(ColorAttribute.createDiffuse(Color.GOLD)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        
        
        
        
       /* player=new ModelPack( new ModelInstance(modelBuilder.createBox(2f, 2f, 2f,
        		new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg")))),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates)),
        		new Vector3(0, 0, -20), new Quaternion());*/
        
         player=new ModelPack( new ModelInstance(assets.get("models/ship/ship.g3db",Model.class)),
		new Vector3(0, 0, -20), new Quaternion());
         //player.model.transform.scale(3,3,3);
         player.scale.add(3);
         player.rotation.setFromAxis(Vector3.Y, 180);
         player.updateTransform();
        //player.model.transform.rotate(Vector3.Y,180);        		
        		
        		
        		
       
       ModelInstance modelIns=new ModelInstance(model);
        
        models.add(new ModelPack(modelIns, new Vector3(0, 0, -100), new Quaternion()));
        
    

        camController = new CameraInputController(cam);
        
        

    }
    Random random=new Random();
    float elapsedTime=0;
    boolean top=false;
    void update(float delta){
    	elapsedTime+=delta;
    	camController.update();
    	
    	models.forEach(model->{
    		model.location.add(0, 0, 0.7f*delta*100);
    		model.updateTransform();
    		if(model.location.z>5)deleteModels.add(model);
    	});
    	models.removeAll(deleteModels);
    	deleteModels.clear();
    	//System.out.println(delta*100);
    	if(elapsedTime>0.03){
    		models.add(new ModelPack(genRandomColorInstance(), 
    				new Vector3(random.nextInt(600)-300, 0, random.nextInt(900)-1000), new Quaternion()));
    		elapsedTime=0;
    	}
    	
    	//cam.lookAt(player.location);
    	
    	float roll=0;
    	
    	if(Gdx.input.isKeyPressed(Keys.D)){
    		/*if(player.location.x<50)
			player.location.x+=1;
			player.updateTransform();*/
    		models.forEach(model->{model.location.x-=0.7f*delta*100;model.updateTransform();});
    		roll=45;
		}
    	if(Gdx.input.isKeyPressed(Keys.A)){
    		/*if(player.location.x>-50)
			player.location.x-=1;
			player.updateTransform();
			cam.rotate(50, 0, 0, 0);*/
    		models.forEach(model->{model.location.x+=0.7f*delta*100;model.updateTransform();});
    		roll=-45;
    	}
    	
    		player.rotation.setEulerAngles(180, 0, roll);
    	
    	
    	if(Gdx.input.isKeyPressed(Keys.SPACE)&&player.location.y<10&&!top){
    		player.location.y+=0.7*delta*100;
    		player.rotation.setEulerAngles(180, -90, roll);
    		if(player.location.y>=10)top=true;
    	}else if(player.location.y>0){
    		player.location.y-=0.35*delta*100;
    		
    		
    	}else if(player.location.y<=0){
    		top=false;
    	}
    	cam.position.x=player.location.x;
    	cam.update();
    	if(Gdx.input.isKeyJustPressed(Keys.R)){
    		dispose();
    		create();
        
    }
    	player.updateTransform();
    	//player.model.transform.rotate(0, 1, 0, player.location.y);
    	//player.model.transform.getRotation(player.rotation);
		
    	
    
    }
    @Override
    public void render(float delta) {
    	update(delta);
        

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        //instance.transform.translate(0.06f,0,0);
       // instance.transform.rotate(1, 0, 0, 1);
        modelBatch.begin(cam);
        
        modelBatch.render(player.model,environment);
        models.forEach(model->modelBatch.render(model.model,environment));
        modelBatch.end();
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
    class ModelPack{
    	public ModelInstance model;
    	public Vector3 location;
    	public Quaternion rotation;
    	public Vector3 scale=new Vector3();
    	public ModelPack(ModelInstance model, Vector3 location, Quaternion rotation) {
			super();
			this.model = model;
			this.location = location;
			this.rotation = rotation;
			
			scale.add(1);
			updateTransform();
		}
		
    	
    	public void updateTransform(){
    		model.transform.setToTranslationAndScaling(location, scale);
    		model.transform.rotate(rotation);
    	}
    }
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}
	private ModelInstance genRandomColorInstance(){
		ModelInstance ret=new ModelInstance(model);
		Color at=Color.YELLOW;
		switch(random.nextInt(4)){
		case 0: at=Color.GOLD;
				break;
		case 1: at=Color.RED;
			break;
		case 2: at=Color.BLUE;
		  	break;
		  	
			
		}
		
		ret.materials.get(0).set(ColorAttribute.createDiffuse(at));
		
		return ret;
	}
}
