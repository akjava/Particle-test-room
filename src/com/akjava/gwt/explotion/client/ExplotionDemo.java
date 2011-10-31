package com.akjava.gwt.explotion.client;

import com.akjava.gwt.stats.client.Stats;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.cameras.Camera;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.lights.Light;
import com.akjava.gwt.three.client.materials.Material;
import com.akjava.gwt.three.client.objects.Mesh;
import com.akjava.gwt.three.client.objects.ParticleSystem;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.renderers.WebGLRenderer.WebGLCanvas;
import com.akjava.gwt.three.client.scenes.Scene;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ExplotionDemo implements EntryPoint {

	private WebGLRenderer renderer;
	private Camera camera;

	private int cameraZ;
	public void onModuleLoad() {
		int width=Window.getClientWidth();
		int height=Window.getClientHeight();
		renderer = THREE.WebGLRenderer();
		renderer.setSize(width,height);
		renderer.setClearColorHex(0x333333, 1);
		
		//RootLayoutPanel.get().setStyleName("transparent");
		
		WebGLCanvas canvas=new WebGLCanvas(renderer);
		final FocusPanel hpanel=new FocusPanel(canvas);
		
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			long last;
			int z=1;
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
				cameraZ=tmp;
				last=t;
			}
		});
		//hpanel.setFocus(true);
		
		canvas.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//GWT.log("click");
			}
		});
		
		hpanel.setStyleName("clear");
		hpanel.setWidth("100%");
		hpanel.setHeight("100%");
		RootLayoutPanel.get().add(hpanel);
		//hpanel.getElement().appendChild(renderer.getDomElement());
		
		
		final Scene scene=THREE.Scene();
		//TODO fog
		
		camera = THREE.PerspectiveCamera(75,(double)width/height,1,3000);
		camera.getPosition().set(0, 0, 0);
		cameraZ=1000;
		
		
		//plate
		final Mesh mesh=THREE.Mesh(THREE.PlaneGeometry(100, 100), THREE.MeshLambertMaterial().color(0xffffff).build());
		scene.add(mesh);
		mesh.setRotation(-45,0, 45);
		
		Geometry geometry = THREE.Geometry();

		for (int i = 0; i < 4000; i++ ) {

			Vector3 vector =  THREE.Vector3( Math.random() * 2000 - 1000, Math.random() * 2000 - 1000, Math.random() * 2000 - 1000 );
			geometry.vertices().push(THREE.Vertex( vector ) );

		}
		//parameters = [ [ [1.0, 1.0, 1.0], 5 ], [ [0.95, 1, 1], 4 ], [ [0.90, 1, 1], 3 ], [ [0.85, 1, 1], 2 ], [ [0.80, 1, 1], 1 ] ];
		
		final Parameter[] parameters={new Parameter(1,1,1,5),new Parameter(0.95,1,1,4),new Parameter(0.9,1,1,3)
		,new Parameter(0.85,1,1,2),new Parameter(0.8,1,1,1)};
		
		final Material[] materials=new Material[parameters.length];
		
		for (int i = 0; i < parameters.length; i++ ) {

			int size  = parameters[i].size;
			

			//materials[i] = new THREE.ParticleBasicMaterial( { color: color, size: size } );

			materials[i] = THREE.ParticleBasicMaterial().size(size).build();
			materials[i].getColor().setHSV( parameters[i].h, parameters[i].s, parameters[i].v );

			ParticleSystem particles = THREE.ParticleSystem( geometry, materials[i] );

			particles.getRotation().setX(Math.random() * 6);
			particles.getRotation().setY(Math.random() * 6);
			particles.getRotation().setZ(Math.random() * 6);

			scene.add( particles );

		}

		
		final Light light=THREE.PointLight(0xffffff);
		light.setPosition(100, 0, 100);
		scene.add(light);
		
		final Stats stats=Stats.insertStatsToRootPanel();
		Timer timer = new Timer(){
			public void run(){
				//TODO mouse move
				
				camera.lookAt( scene.getPosition() );
				
				double time=System.currentTimeMillis() * 0.00005;
				
				
				for(int i = 0; i < scene.objects().length(); i++ ) {
					if(scene.objects().get(i).getId()==mesh.getId()){
						
					}else{
					scene.objects().get(i).getRotation().setY(time * ( i < 4 ? i+1 : - (i+1) ));
					}
				}
				
				for(int i = 0; i < materials.length; i++ ) {
					double h = ( 360 * ( parameters[i].h + time ) % 360 ) / 360;
					materials[i].getColor().setHSV( h, parameters[i].s, parameters[i].v );
				}
				
				stats.update();
				camera.getPosition().setZ(cameraZ);
				renderer.render(scene, camera);
			}
		};
		
		
		
		if(!GWT.isScript()){
			timer.scheduleRepeating(100);
		}else{
			timer.scheduleRepeating(1000/60);
		}
		
		
		final PopupPanel dialog=new PopupPanel();
		//dialog.setStyleName("transparent");
		dialog.add(new Label("Control"));
		dialog.show();
		leftTop(dialog);
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w=hpanel.getOffsetWidth();
				int h=hpanel.getOffsetHeight();
				GWT.log(w+"x"+h);
				camera = THREE.PerspectiveCamera(35,(double)w/h,.1,10000);
				camera.getPosition().set(0, 0, 50);
				//camera.setRatio((double)w/h); //somehow not work
				//camera.updateProjectionMatrix();
				renderer.setSize(w, h);
				leftTop(dialog);
			}
		});
		HTMLPanel html=new HTMLPanel("Powerd by <a href=''>Three.js</a> & GWT");
		html.setWidth("100%");
		html.setHeight("20px");
		html.setStyleName("text");
		final PopupPanel dialog2=new PopupPanel();
		dialog2.add(html);
		dialog2.setPopupPosition(200, 0);
		dialog2.setWidth("100%");
		dialog2.setStyleName("transparent");
		dialog2.show();
		
		//RootLayoutPanel.get().add(html);
	}
	
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
	
	private void leftTop(PopupPanel dialog){
		int w=Window.getClientWidth();
		int h=Window.getScrollTop();
		int dw=dialog.getOffsetWidth();
		GWT.log(w+"x"+h+" offset="+dialog.getOffsetWidth());
		dialog.setPopupPosition(w-dw, h);
	}
}
