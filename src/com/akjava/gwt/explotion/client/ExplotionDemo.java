package com.akjava.gwt.explotion.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.akjava.gwt.explotion.client.particle.Explotion;
import com.akjava.gwt.explotion.client.particle.Galaxy;
import com.akjava.gwt.explotion.client.particle.ParticleControler;
import com.akjava.gwt.explotion.client.particle.Rain;
import com.akjava.gwt.explotion.client.particle.Rotation;
import com.akjava.gwt.html5.client.ColorPickWidget;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.cameras.Camera;
import com.akjava.gwt.three.client.extras.ImageUtils;
import com.akjava.gwt.three.client.gwt.BlendingValue;
import com.akjava.gwt.three.client.gwt.BlendingValueList;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.materials.Material;
import com.akjava.gwt.three.client.materials.ParticleBasicMaterialBuilder;
import com.akjava.gwt.three.client.objects.Mesh;
import com.akjava.gwt.three.client.objects.ParticleSystem;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.scenes.Scene;
import com.akjava.gwt.three.client.textures.Texture;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ValueListBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ExplotionDemo extends AbstractDemo {


	private Camera camera;

	private int cameraZ;
	
	int width;
	int height;
	@Override
	public void initialize(WebGLRenderer renderer,int width,int height) {

		this.width=width;
		this.height=height;
		
		cameraZ=1000;
		scene=createScene();
	}
	
	private Scene createScene(){
		//this.scene=null;//really need?
		Scene scene = THREE.Scene();
		
		camera = THREE.PerspectiveCamera(75,(double)width/height,1,3000);
		camera.getPosition().set(0, 0, 0);
		
		
		
		mesh = THREE.Mesh(THREE.PlaneGeometry(500, 500), THREE.MeshLambertMaterial().color(0xffffff).build());
		if(showPlane){
		scene.add(mesh);
		}
		mesh.setRotation(-45,0, 45);
		
		

		
		//parameters = [ [ [1.0, 1.0, 1.0], 5 ], [ [0.95, 1, 1], 4 ], [ [0.90, 1, 1], 3 ], [ [0.85, 1, 1], 2 ], [ [0.80, 1, 1], 1 ] ];
		
		parameters = new Parameter[] {new Parameter(1,1,1,5),new Parameter(0.95,1,1,4),new Parameter(0.9,1,1,3)
		,new Parameter(0.85,1,1,2),new Parameter(0.8,1,1,1)};
		
		materials = new Material[parameters.length];
		
		
		Texture texture=ImageUtils.loadTexture(textureUrl);
		//TODO fix some problem.
		for (int i = 0; i < parameters.length && i<maxLayer; i++ ) {
			
			//int size  = parameters[i].size;
			int size=particleSize;
			

			//materials[i] = new THREE.ParticleBasicMaterial( { color: color, size: size } );

			ParticleBasicMaterialBuilder builder=THREE.ParticleBasicMaterial().size(size);
			builder.blending(blending);
			builder.color(foreground);
			
			if(useTexture){
				builder.map(texture);
				builder.transparent(useTransparent);
			}
			

			materials[i] = builder.build();
			if(randomColor){
			materials[i].getColor().setHSV( parameters[i].h, parameters[i].s, parameters[i].v );
			}else{
			//	GWT.log("before:"+Integer.toHexString(materials[i].getColor().getHex()));
			//materials[i].getColor().setRGB(255, 255, 255);	
			//GWT.log("after :"+Integer.toHexString(materials[i].getColor().getHex()));
			//materials[i].getColor().setIntRGB(255, 255, 255);	
			}
			
			
			
			
			
			
			double dratio=(double)density/100;
			particleSystems=particleControler.init(builder, particleCount,dratio);

			for(ParticleSystem particleSystem:particleSystems){
				particleSystem.setSortParticles(sortParticle);
				scene.add( particleSystem );
			}
			/*
			particles.getRotation().setX(Math.random() * 6);
			particles.getRotation().setY(Math.random() * 6);
			particles.getRotation().setZ(Math.random() * 6);
*/
			

			
		}

		light = THREE.PointLight(0xffffff);
		light.setPosition(100, 0, 100);
		scene.add(light);
		
		/*
		
		*/
		return scene;
	}
	private int maxLayer=1;
	
	private class Parameter{
		public Parameter(double h,double s,double v,int size){
			this.h=h;
			this.s=s;
			this.v=v;
			this.size=size;
		}
		public double h;
		public double s;
		public double v;
		public int size;
	}
	


	@Override
	public void update(WebGLRenderer renderer) {
		getCanvas().setClearColorHex(background);
		Scene scene;
		if(needCreateScene){
			scene=createScene();//some times gwt miss catch,if just replace scene.
			needCreateScene=false;
		}else{
			scene=this.scene;
		}
		
		camera.lookAt( scene.getPosition() );
		
		
		
		
		particleControler.update();
		
		
		double time=System.currentTimeMillis() * 0.00005;
		/*
		for(int i = 0; i < scene.objects().length(); i++ ) {
			if(scene.objects().get(i).getId()==mesh.getId() || scene.objects().get(i).getId()==light.getId()){
				//ignore light & plate
			}else{
			scene.objects().get(i).getRotation().setY(time * ( i < 4 ? i+1 : - (i+1) ));
			}
		}*/
		
		if(randomColor){
		
		for(int i = 0; i < particleSystems.size();i++ ) {
			ParticleSystem p=particleSystems.get(i);
			Material material=p.materials().get(0);
			double h = ( 360 * ( parameters[i].h + time ) % 360 ) / 360;
			material.getColor().setHSV( h, parameters[i].s, parameters[i].v );
		}
		
		}
		camera.getPosition().incrementX((mouseX - camera.getPosition().getX() ) * 0.05);
		camera.getPosition().incrementY((-mouseY - camera.getPosition().getY() ) * 0.05);
		
		
		//camera.getPosition().setX(mouseX);
		//camera.getPosition().setY(mouseY);
		camera.getPosition().setZ(cameraZ);
		renderer.render(scene, camera);
		this.scene=scene;
	}
	
	//for wheel
	long last;
	int z=1;

	private Parameter[] parameters;

	private Material[] materials;

	private Mesh mesh;

	private Scene scene;
	@Override
	public void onMouseWheel(MouseWheelEvent event) {
		
		
			long t=System.currentTimeMillis();
			if(last+100>t){
				z*=2;
			}else{
				z=1;
			}
			//GWT.log("wheel:"+event.getDeltaY());
			int tmp=cameraZ+event.getDeltaY()*z;
			tmp=Math.max(0, tmp);
			tmp=Math.min(4000, tmp);
			cameraZ=tmp;
			last=t;
		
			//GWT.log("camera:"+cameraZ);
	}

	
	@Override
	public void resized(int width, int height) {
		camera = THREE.PerspectiveCamera(75,(double)width/height,1,3000);
		this.width=width;
		this.height=height;
	}


	private boolean needCreateScene;//create scene in loop
	
	
	private boolean useTexture=true;
	private boolean useTransparent=true;
	private boolean randomColor=true;
	
	private int density=100;
	private int particleSize=20;
	private int particleCount=4000;
	private Light light;

	private boolean sortParticle=true;//better on large particle
	private Light light2;
	
	private int blending;
	private int foreground=0xffffff;
	private int background;
	private String textureUrl;

	private BlendingValueList blendingList;

	private CheckBox textureCheck;

	private SelectionFlowPanel<Image> images;

	private ColorPickWidget baseColor;

	private ColorPickWidget backgroundColor;

	private CheckBox changeColorCheck;

	private HTML5InputRange sizeyRange;

	private HTML5InputRange countRange;

	private HTML5InputRange densityRange;

	private CheckBox sortParticleCheck;

	private CheckBox useTransparentCheck;
	
	private ParticleControler particleControler=new Rain();

	private List<ParticleSystem> particleSystems;

	private boolean showPlane=true;
	private ValueListBox<ParticleControler> particleControlers;

	private CheckBox showPlaneCheck;
	private Label createTitleLabel(String text){
		Label label=new Label(text);
		label.setStylePrimaryName("title");
		return label;
	}
	
	private Label createRangeLabel(final String text,final HTML5InputRange range){
		final Label label=new Label();
		label.setText(text+":"+range.getValue());
		label.setStylePrimaryName("title");
		range.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				label.setText(text+":"+range.getValue());
			}
		});
		return label;
	}
	@Override
	public void createControl(Panel parent) {
		
		HorizontalPanel hp=new HorizontalPanel();
		parent.add(hp);
		Button update=new Button("Update Particles");
		update.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateParticles();
			}
		});
		hp.add(update);
		
		
		Button share=new Button("Share");
		share.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				updateParticles();
				shareUrl();
			}
		});
		hp.add(share);
		
		Token token=new Token();
		background=token.getHexInt("bg", background);
		particleSize=token.getInt("psize", particleSize);
		particleCount=token.getInt("pcount", particleCount);
		sortParticle=token.getBoolean("sort", sortParticle);
		useTransparent=token.getBoolean("transparent", useTransparent);
		blending=token.getInt("blend", blending);
		randomColor=token.getBoolean("randam", randomColor);
		foreground=token.getHexInt("fg", foreground);
		String imageName=token.getString("texture", null);
		if(imageName!=null){
			useTexture=true;
		}
		density=token.getInt("density", density);
		String motionName=token.getString("motion", null);
		showPlane=token.getBoolean("plane", showPlane);
		
		
		parent.setWidth("164px");
		
		parent.add(createTitleLabel("Background"));
		backgroundColor = new ColorPickWidget();
		backgroundColor.setValue(background);
		parent.add(backgroundColor);
		
		
		
		
		
		
		
		
		sizeyRange = new HTML5InputRange(1, 100, particleSize);
		parent.add(createRangeLabel("Particle Size",sizeyRange));
		parent.add(sizeyRange);
		
		
		countRange = new HTML5InputRange(1, 5000, particleCount);
		parent.add(createRangeLabel("Particle Count",countRange));
		parent.add(countRange);
		
		
		
		
		
		
		sortParticleCheck = new CheckBox("Particle.sortParticles");
		sortParticleCheck.setValue(sortParticle);
		parent.add(sortParticleCheck);
		
useTransparentCheck = new CheckBox("use transparent");
useTransparentCheck.setValue(useTransparent);
		parent.add(useTransparentCheck);
		
		parent.add(createTitleLabel("Blending"));
		blendingList = new BlendingValueList();
		
		if(!BlendingValue.isvalid(blending)){
			blending=0;
		}
		
		blendingList.setValue(BlendingValue.values.get(blending));
		parent.add(blendingList);
		
		parent.add(createTitleLabel("Foreground"));
		changeColorCheck = new CheckBox("Random color");
		changeColorCheck.setValue(randomColor);
		parent.add(changeColorCheck);
		baseColor = new ColorPickWidget();
		baseColor.setValue(foreground);
		baseColor.setVisible(!randomColor);
		parent.add(baseColor);
		changeColorCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				baseColor.setVisible(!event.getValue());
			}
		});
		
		parent.add(createTitleLabel("Texture"));
		textureCheck = new CheckBox("use texture");
		textureCheck.setValue(useTexture);
		parent.add(textureCheck);
		images = new SelectionFlowPanel<Image>("selection");
		images.getElement().getStyle().setBackgroundColor("#888");
		String[] imgs={"particle3.png","particle5.png","particle6.png","candle.png","particle1.png","particle2.png","particle4.png","particle7.png","sword.png",
				"box.png","spark2.png",
				"book.png","clock.png","crystal.png",
				"cup.png","dish.png","ribbon.png",
				"shield.png","vegitable.png","particle8.png","particle9.png","particle11.png","star2.png","shoot.png"
		};
		
		for(int i=0;i<imgs.length;i++){
			Image img=new Image("img/"+imgs[i]);
			images.add(img);
			if(i==0){
				img.addLoadHandler(new LoadHandler() {
					
					@Override
					public void onLoad(LoadEvent event) {
						updateParticles();
					}
				});
			}
		}
		int findIndex=0;
		for(int i=0;i<imgs.length;i++){
			if(imgs[i].equals(imageName)){
				findIndex=i;
				break;
			}
		}
		images.select(findIndex);
		parent.add(images);
		textureCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				images.setVisible(event.getValue());
				
			}
		});
		densityRange = new HTML5InputRange(1, 200, density);
		parent.add(createRangeLabel("Density",densityRange));
		parent.add(densityRange);
		
		
		
		parent.add(createTitleLabel("Motion"));
		List<ParticleControler> particles=Arrays.asList(new Rain(),new Galaxy(),new Explotion(),new Rotation());
		
		particleControlers = new ValueListBox<ParticleControler>(new Renderer<ParticleControler>() {
			@Override
			public String render(ParticleControler object) {
				return object.getName();
			}

			@Override
			public void render(ParticleControler object, Appendable appendable)
					throws IOException {
			}
		});
		int motionIndex=0;
		for(int i=0;i<particles.size();i++){
			if(particles.get(i).getName().equals(motionName)){
				motionIndex=i;
				break;
			}
		}
		particleControlers.setValue(particles.get(motionIndex));
		particleControlers.setAcceptableValues(particles);
		parent.add(particleControlers);
		
		showPlaneCheck = new CheckBox("Show Plane-Geometry");
		showPlaneCheck.setValue(showPlane);
		parent.add(showPlaneCheck);
		
		
		
		
		
		
		
		showControl();
	}
	private void shareUrl(){
		Map<String,String> map=new HashMap<String,String>();
		map.put("bg",toHexColor(background));
		map.put("psize", ""+particleSize);
		map.put("pcount", ""+particleCount);
		map.put("sort", ""+sortParticle);
		map.put("transparent", ""+useTransparent);
		
		
		BlendingValue bv=blendingList.getValue();
		
		
		map.put("blend", ""+bv.getValue());
		map.put("randam", ""+randomColor);
		map.put("fg", ""+toHexColor(foreground));
		
		if(useTexture){
		String imgName=textureUrl;
		int ind=imgName.lastIndexOf("/");
		imgName=imgName.substring(ind+1);
		map.put("texture", ""+imgName);
		}
		map.put("density", ""+density);
		map.put("motion", ""+particleControler.getName());
		map.put("plane", ""+showPlane);
		
		History.newItem(Token.createToken(map));
	}
	private String toHexColor(int value){
		String ret=Integer.toHexString(value);
		while(ret.length()<6){
			ret="0"+ret;
		}
		return ret;
	}
	private void updateParticles(){
		useTexture=textureCheck.getValue();
		randomColor=changeColorCheck.getValue();
		particleSize=sizeyRange.getValue();
		particleCount=countRange.getValue();
		
		density=densityRange.getValue();
		needCreateScene=true;
		blending=blendingList.getValue().getValue();
		sortParticle=sortParticleCheck.getValue();
		useTransparent=useTransparentCheck.getValue();
		foreground=baseColor.getValue();
		background=backgroundColor.getValue();
		textureUrl=images.getSelection().getUrl();
		particleControler=particleControlers.getValue();
		showPlane=showPlaneCheck.getValue();
	}
	@Override
	public String getHtml(){
		return "Particle Test Room for Chrome by <a href='http://www.akjava.com'>Aki</a> "+super.getHtml()+" ,assets from <a href='http://opengameart.org/'>opengameart.org</a>";
	}

	private int mouseX;
	private int mouseY;
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		mouseX=event.getX()-width/2;
		mouseY=event.getY()-height/2;
	}
	
	
}
