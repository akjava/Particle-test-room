package com.akjava.gwt.explotion.client.particle;

import java.util.List;

import com.akjava.gwt.three.client.materials.ParticleBasicMaterialBuilder;
import com.akjava.gwt.three.client.objects.ParticleSystem;

public interface ParticleControler {
public List<ParticleSystem> init(ParticleBasicMaterialBuilder builder,int particleCount,double density);
public void update();
public String getName();
}
