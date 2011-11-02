package com.akjava.gwt.explotion.client;

import com.akjava.gwt.stats.client.Stats;
import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.renderers.WebGLRenderer.WebGLCanvas;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public abstract class AbstractDemo implements EntryPoint {

	private WebGLRenderer renderer;



	protected Timer timer;
	protected Stats stats;



	private WebGLCanvas canvas;



	private PopupPanel dialog;



	private Button hide;



	private VerticalPanel main;
	
	public WebGLCanvas getCanvas() {
		return canvas;
	}
	public abstract void onMouseWheel(MouseWheelEvent event);
	public abstract void update(WebGLRenderer renderer);
	public abstract void initialize(WebGLRenderer renderer,int width,int height);
	public abstract void resized(int width,int height);
	public void onModuleLoad() {
		int width=Window.getClientWidth();
		int height=Window.getClientHeight();
		renderer = THREE.WebGLRenderer();
		renderer.setSize(width,height);
		
		
		//renderer.setClearColorHex(0x333333, 1);
		
		//RootLayoutPanel.get().setStyleName("transparent");
		
		canvas = new WebGLCanvas(renderer);
		canvas.setClearColorHex(0);
		//final FocusPanel glCanvas=new FocusPanel(canvas);
		
		canvas.addMouseWheelHandler(new MouseWheelHandler() {
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				AbstractDemo.this.onMouseWheel(event);
			}
		});
		//hpanel.setFocus(true);
		
		canvas.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//TODO
			}
		});
		
		//canvas.setStyleName("clear");
		//glCanvas.getElement().getStyle().setBackgroundColor("#fff");
		canvas.setWidth("100%");
		canvas.setHeight("100%");
		RootLayoutPanel.get().add(canvas);
		
		initialize(renderer,width,height);
		
		stats = Stats.insertStatsToRootPanel();
		timer = new Timer(){
			public void run(){
				update(renderer);
				stats.update();
			}
		};
		
		
		
		if(!GWT.isScript()){
			timer.scheduleRepeating(100);
		}else{
			timer.scheduleRepeating(1000/60);
		}
		
		
		
		dialog = new PopupPanel();
		VerticalPanel dialogRoot=new VerticalPanel();
		dialogRoot.setSpacing(2);
		//dialog.setStyleName("transparent");
		Label label=new Label("Control");
		label.setStyleName("title");
		dialog.add(dialogRoot);
		dialogRoot.add(label);
		main = new VerticalPanel();
		main.setVisible(false);
		dialogRoot.add(main);
		
		HorizontalPanel hPanel=new HorizontalPanel();
		hPanel.setWidth("100%");
		hPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		dialogRoot.add(hPanel);
		hide = new Button("Hide Control");
		
		hide.setVisible(false);
		hide.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				main.setVisible(false);
				hide.setVisible(false);
				rightTop(dialog);
			}
		});
		hPanel.add(hide);
		label.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showControl();
			}
		});
		
		createControl(main);
		
		dialog.show();
		rightTop(dialog);
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int w=canvas.getOffsetWidth();
				int h=canvas.getOffsetHeight();
				resized(w,h);
				renderer.setSize(w, h);
				rightTop(dialog);
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
	
	protected void showControl(){
		main.setVisible(true);
		hide.setVisible(true);
		rightTop(dialog);
	}
	
	public String getHtml(){
		return "Powerd by <a href='https://github.com/mrdoob/three.js/'>Three.js</a> & <a href='http://code.google.com/intl/en/webtoolkit/'>GWT</a>";
	}
	public abstract void createControl(Panel parent);
	
	private void rightTop(PopupPanel dialog){
		int w=Window.getClientWidth();
		int h=Window.getScrollTop();
		int dw=dialog.getOffsetWidth();
		GWT.log(w+"x"+h+" offset="+dialog.getOffsetWidth());
		dialog.setPopupPosition(w-dw, h);
	}
}
