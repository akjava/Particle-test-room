package com.akjava.gwt.explotion.client.particle;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.three.client.THREE;
import com.akjava.gwt.three.client.core.Geometry;
import com.akjava.gwt.three.client.core.Vector3;
import com.akjava.gwt.three.client.materials.Material;
import com.akjava.gwt.three.client.materials.ParticleBasicMaterialBuilder;
import com.akjava.gwt.three.client.objects.ParticleSystem;

public class Rotation implements ParticleControler{
private double density;
private List<ParticleSystem> systems;

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
		
		particleSystem.getRotation().setX(Math.random() * 6);
		particleSystem.getRotation().setY(Math.random() * 6);
		particleSystem.getRotation().setZ(Math.random() * 6);
		return systems;
	}

	@Override
	public void update() {
		double time=System.currentTimeMillis() * 0.00005;
		for(int i=0;i<systems.size();i++){
		systems.get(i).getRotation().setY(time * ( 0 < 4 ? 0+1 : - (0+1) ));
		}
	}

	@Override
	public String getName() {
		return "Rotation";
	}

}
