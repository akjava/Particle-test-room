package com.akjava.gwt.explotion.client.particle;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.core.Vertex;
import com.akjava.gwt.three.client.materials.ParticleBasicMaterialBuilder;
import com.akjava.gwt.three.client.objects.ParticleSystem;

public class Rain implements ParticleControler{
	
	Vector3[] velocity;
	private ArrayList<ParticleSystem> systems;
	private double density;
	@Override
	public List<ParticleSystem> init(ParticleBasicMaterialBuilder builder,int size,double density) {
		systems = new ArrayList<ParticleSystem>();
		Geometry geometry = THREE.Geometry();
		this.density=density;
		
		for(int i=0;i<size;i++){
			int px= (int) (Math.random() * 2000 - 1000);
			int py= (int) (Math.random() * 2000 - 1000);
			int pz= (int) (Math.random() * 2000 - 1000);
			
			px*=density;
			py*=density;
			pz*=density;
			
			geometry.vertices().push(THREE.Vertex( THREE.Vector3(px, py, pz ) ));
		}
		
		ParticleSystem particleSystem=THREE.ParticleSystem(geometry,builder.build());
		systems.add(particleSystem);
		
		
		velocity=new Vector3[size];
		for(int i=0;i<size;i++){
			velocity[i]=THREE.Vector3(0,-Math.random(),0);
		}
		return systems;
	}

	@Override
	public void update() {
		for(ParticleSystem particleSystem:systems){
		particleSystem.getRotation().incrementY(0.001);
		Geometry particles=particleSystem.getGeometry();
		
		for(int i=0;i<velocity.length;i++){
			Vertex v=particles.vertices().get(i);
			if(v.getPosition().getY()<-200*4*density){
				v.getPosition().setY(200*4*density);
				velocity[i].setY(0);
			}
			
			velocity[i].incrementY(-Math.random() * .1);
	        
	       
	        v.getPosition().addSelf(
	        		velocity[i]);
		}
		
		particles.setDirtyVertices(true);
		}
	}

	@Override
	public String getName() {
		return "Rain";
	}

}
