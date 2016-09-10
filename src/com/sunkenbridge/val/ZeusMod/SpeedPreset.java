package com.sunkenbridge.val.ZeusMod;

import org.bukkit.entity.Player;

public class SpeedPreset {

	public Player player;
	
	public boolean isZeusSpeedActive = false;
	
	public float
		walkSpeed = 0.2f,
		flySpeed = 0.1f,
		zeusWalkSpeed = 0.4f,
		zeusFlySpeed = 0.4f;
	
	
	public SpeedPreset(Player player) {
		
		this.player = player;
		//walkSpeed = player.getWalkSpeed();
		//flySpeed = player.getFlySpeed();
	}
	
	
	public void activate() {
		
		float
			walk = player.getWalkSpeed(),
			fly = player.getFlySpeed();
		
		if (walk < zeusWalkSpeed)
			walkSpeed = walk;
		
		if (fly < zeusFlySpeed)
			flySpeed = fly;
		
		
		player.setWalkSpeed(zeusWalkSpeed);
		player.setFlySpeed(zeusFlySpeed);
		isZeusSpeedActive = true;
	}
	
	public void deactivate() {
	
		player.setWalkSpeed(walkSpeed);
		player.setFlySpeed(flySpeed);
		isZeusSpeedActive = false;
	}
	
	public void update() {
		
		if (player.getItemInHand().getType().equals(ZeusModPlugin.controlItem) != isZeusSpeedActive) {
			
			if (isZeusSpeedActive)
				deactivate();
			
			else
				activate();
		}
	}
	
	
	public void setZeusWalkSpeed(float speed) {
		
		zeusWalkSpeed = speed;
		if (isZeusSpeedActive)
			player.setWalkSpeed(speed);
	}
	
	public void setZeusFlySpeed(float speed) {
		
		zeusFlySpeed = speed;
		if (isZeusSpeedActive)
			player.setFlySpeed(speed);
	}
}
