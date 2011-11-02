package com.akjava.gwt.explotion.client;

import com.akjava.gwt.html5.client.ColorPickWidget;
import com.akjava.gwt.html5.client.HTML5InputRange;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.cameras.Camera;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.extras.ImageUtils;
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
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

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
		this.scene=null;//really need?
		Scene scene = THREE.Scene();
		
		camera = THREE.PerspectiveCamera(75,(double)width/height,1,3000);
		camera.getPosition().set(0, 0, 0);
		
		
		
		mesh = THREE.Mesh(THREE.PlaneGeometry(100, 100), THREE.MeshLambertMaterial().color(0xffffff).build());
		scene.add(mesh);
		mesh.setRotation(-45,0, 45);
		
		Geometry geometry = THREE.Geometry();

		for (int i = 0; i < particleCount; i++ ) {

			Vector3 vector =  THREE.Vector3( Math.random() * 2000 - 1000, Math.random() * 2000 - 1000, Math.random() * 2000 - 1000 );
			if(lowRange){
				double dratio=(double)density/100;
				vector.setX(vector.getX()*dratio);
				vector.setY(vector.getY()*dratio);
				vector.setZ(vector.getZ()*dratio);
			}
			geometry.vertices().push(THREE.Vertex( vector ) );

		}
		//parameters = [ [ [1.0, 1.0, 1.0], 5 ], [ [0.95, 1, 1], 4 ], [ [0.90, 1, 1], 3 ], [ [0.85, 1, 1], 2 ], [ [0.80, 1, 1], 1 ] ];
		
		parameters = new Parameter[] {new Parameter(1,1,1,5),new Parameter(0.95,1,1,4),new Parameter(0.9,1,1,3)
		,new Parameter(0.85,1,1,2),new Parameter(0.8,1,1,1)};
		
		materials = new Material[parameters.length];
		
		
		Texture texture=ImageUtils.loadTexture(textureUrl);
		
		for (int i = 0; i < parameters.length && i<maxLayer; i++ ) {
			
			//int size  = parameters[i].size;
			int size=particleSize;
			

			//materials[i] = new THREE.ParticleBasicMaterial( { color: color, size: size } );

			ParticleBasicMaterialBuilder builder=THREE.ParticleBasicMaterial().size(size);
			builder.blending(blending);
			builder.color(color);
			
			if(useTexture){
				builder.map(texture);
				builder.transparent(useTransparent);
				//builder.blending(THREE.NormalBlending);
				
				
			}
			
			//TODO layer support
			materials[i] = builder.build();
			if(changeColor){
			materials[i].getColor().setHSV( parameters[i].h, parameters[i].s, parameters[i].v );
			}else{
			//	GWT.log("before:"+Integer.toHexString(materials[i].getColor().getHex()));
			//materials[i].getColor().setRGB(255, 255, 255);	
			//GWT.log("after :"+Integer.toHexString(materials[i].getColor().getHex()));
			//materials[i].getColor().setIntRGB(255, 255, 255);	
			}
			
			ParticleSystem particles = THREE.ParticleSystem( geometry, materials[i] );
			particles.setSortParticles(sortParticle);

			particles.getRotation().setX(Math.random() * 6);
			particles.getRotation().setY(Math.random() * 6);
			particles.getRotation().setZ(Math.random() * 6);

			scene.add( particles );

			
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
		
		double time=System.currentTimeMillis() * 0.00005;
		
		
		for(int i = 0; i < scene.objects().length(); i++ ) {
			if(scene.objects().get(i).getId()==mesh.getId() || scene.objects().get(i).getId()==light.getId()){
				//ignore light & plate
			}else{
			scene.objects().get(i).getRotation().setY(time * ( i < 4 ? i+1 : - (i+1) ));
			}
		}
		
		if(changeColor){
		for(int i = 0; i < materials.length && i<maxLayer; i++ ) {
			double h = ( 360 * ( parameters[i].h + time ) % 360 ) / 360;
			materials[i].getColor().setHSV( h, parameters[i].s, parameters[i].v );
		}
		}
		
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
	
	private boolean lowRange=true;
	private boolean useTexture;
	private boolean useTransparent=true;
	private boolean changeColor=true;
	
	private int density=100;
	private int particleSize=4;
	private int particleCount=4000;
	private Light light;

	private boolean sortParticle;
	private Light light2;
	
	private int blending;
	private int color;
	private int background;
	private String textureUrl;
	
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
		parent.setWidth("164px");
		
		parent.add(createTitleLabel("Background"));
		final ColorPickWidget backgroundColor=new ColorPickWidget();
		backgroundColor.setColor(background);
		parent.add(backgroundColor);
		
		
		final CheckBox textureCheck=new CheckBox("use texture");
		parent.add(textureCheck);
		
		final CheckBox changeColorCheck=new CheckBox("change color");
		changeColorCheck.setValue(changeColor);
		parent.add(changeColorCheck);
		
		
		
		final HTML5InputRange sizeyRange=new HTML5InputRange(1, 100, particleSize);
		parent.add(createRangeLabel("Particle Size",sizeyRange));
		parent.add(sizeyRange);
		
		
		final HTML5InputRange countRange=new HTML5InputRange(1, 5000, particleCount);
		parent.add(createRangeLabel("Particle Count",countRange));
		parent.add(countRange);
		
		
		
		final HTML5InputRange densityRange=new HTML5InputRange(1, 200, density);
		parent.add(createRangeLabel("Density",densityRange));
		parent.add(densityRange);
		
		
		final CheckBox sortParticleCheck=new CheckBox("sort particle");
		
		parent.add(sortParticleCheck);
		
final CheckBox useTransparentCheck=new CheckBox("use transparent");
useTransparentCheck.setValue(useTransparent);
		parent.add(useTransparentCheck);
		
		parent.add(createTitleLabel("Blending"));
		final BlendingValueList blendingList=new BlendingValueList();
		parent.add(blendingList);
		
		parent.add(createTitleLabel("Foreground"));
		final ColorPickWidget baseColor=new ColorPickWidget();
		parent.add(baseColor);
		
		parent.add(createTitleLabel("Texture"));
		final SelectionFlowPanel<Image> images=new SelectionFlowPanel<Image>("selection");
		String[] imgs={"particle2.png","particle4.png","particle7.png","spark1.png",
				"particle2.png","particle4.png","particle7.png","spark1.png",
				"particle2.png","particle4.png","particle7.png","spark1.png",
				"particle2.png","particle4.png","particle7.png","spark1.png"};
		for(int i=0;i<imgs.length;i++){
			images.add(new Image("img/"+imgs[i]));
		}
		images.select(0);
		parent.add(images);
		
		
		Button update=new Button("Update Particles");
		update.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				useTexture=textureCheck.getValue();
				changeColor=changeColorCheck.getValue();
				particleSize=sizeyRange.getValue();
				particleCount=countRange.getValue();
				
				density=densityRange.getValue();
				needCreateScene=true;
				blending=blendingList.getValue().getValue();
				sortParticle=sortParticleCheck.getValue();
				useTransparent=useTransparentCheck.getValue();
				color=baseColor.getColor();
				background=backgroundColor.getColor();
				textureUrl=images.getSelection().getUrl();
			}
		});
		parent.add(update);
		
		showControl();
	}
	
	
}
