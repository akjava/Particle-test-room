package com.akjava.gwt.explotion.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectionFlowPanel<T extends Widget> extends FlowPanel implements ClickHandler{
private String selectionStyle;
private T selection;
	public SelectionFlowPanel(String style){
		super();
		this.selectionStyle=style;
	}
	
	@Override
	public void add(Widget widget){
		super.add(widget);
		if(widget instanceof HasClickHandlers){
			((HasClickHandlers)widget).addClickHandler(this);
		}
	}
	@Override
	public void onClick(ClickEvent event) {
		select((T)event.getSource());
	}
	
	public void select(T widget){
		for(int i=0;i<getChildren().size();i++){
			Widget w=getChildren().get(i);
			w.removeStyleName(selectionStyle);
		}
		widget.addStyleName(selectionStyle);
		selection=widget;
	}
	public void select(int index){
		select((T)getChildren().get(index));
	}
	
	public T getSelection(){
	return selection;
	}
}
