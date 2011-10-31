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
public abstract class AbstractDemo implements EntryPoint {

	private WebGLRenderer renderer;
	private Camera camera;


	protected Timer timer;
	protected Stats stats;
	
	public abstract void doMouseWheel(MouseWheelEvent event);
	public abstract void doLoop();
	public abstract void initialize(WebGLRenderer renderer);
	public abstract void resized(int width,int height);
	public void onModuleLoad() {
		int width=Window.getClientWidth();
		int height=Window.getClientHeight();
		renderer = THREE.WebGLRenderer();
		renderer.setSize(width,height);
		
		
		renderer.setClearColorHex(0x333333, 1);
		
		//RootLayoutPanel.get().setStyleName("transparent");
		
		WebGLCanvas canvas=new WebGLCanvas(renderer);
		final FocusPanel glCanvas=new FocusPanel(canvas);
		
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				doMouseWheel(event);
			}
		});
		//hpanel.setFocus(true);
		
		canvas.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//TODO
			}
		});
		
		glCanvas.setStyleName("clear");
		glCanvas.setWidth("100%");
		glCanvas.setHeight("100%");
		RootLayoutPanel.get().add(glCanvas);
		
		initialize(renderer);
		
		stats = Stats.insertStatsToRootPanel();
		timer = new Timer(){
			public void run(){
				doLoop();
				stats.update();
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
				int w=glCanvas.getOffsetWidth();
				int h=glCanvas.getOffsetHeight();
				resized(w,h);
				renderer.setSize(w, h);
				leftTop(dialog);
			}
		});
		HTMLPanel html=new HTMLPanel(getHtml());
		html.setWidth("100%");
		html.setHeight("20px");
		html.setStyleName("text");
		final PopupPanel dialog2=new PopupPanel();
		dialog2.add(html);
		dialog2.setPopupPosition(200, 0);
		dialog2.setWidth("100%");
		dialog2.setStyleName("transparent");
		dialog2.show();
	}
	public String getHtml(){
		return "Powerd by <a href='https://github.com/mrdoob/three.js/'>Three.js</a> & <a href='http://code.google.com/intl/en/webtoolkit/'>GWT</a>";
	}
	
	private void leftTop(PopupPanel dialog){
		int w=Window.getClientWidth();
		int h=Window.getScrollTop();
		int dw=dialog.getOffsetWidth();
		GWT.log(w+"x"+h+" offset="+dialog.getOffsetWidth());
		dialog.setPopupPosition(w-dw, h);
	}
}
