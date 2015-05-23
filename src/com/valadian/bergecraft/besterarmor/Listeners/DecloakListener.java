package com.valadian.bergecraft.besterarmor.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.PlayerInventory;

import com.valadian.bergecraft.BergeyPvp;
import com.valadian.bergecraft.besterarmor.Managers.ArmorManager;


public class DecloakListener implements Listener
{
private ArmorManager armorManager;
	
	public DecloakListener()
	{
		this.armorManager = BergeyPvp.getArmorManager();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(armorManager.getChainPlayers().contains(event.getPlayer()))
		{
			armorManager.timedDeCloakPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(armorManager.getChainPlayers().contains(event.getPlayer()))
		{
			armorManager.timedDeCloakPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if(armorManager.getChainPlayers().contains(event.getPlayer()))
		{
			armorManager.timedDeCloakPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onEggThrow(PlayerEggThrowEvent event)
	{
		if(armorManager.getChainPlayers().contains(event.getPlayer()))
		{
			armorManager.timedDeCloakPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onFish(PlayerFishEvent event)
	{
		if(armorManager.getChainPlayers().contains(event.getPlayer()))
		{
			armorManager.timedDeCloakPlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlayerInventory(InventoryOpenEvent event)
	{
		System.out.println("OPEN");
		if(!(event.getPlayer() instanceof Player))
		{
			return;
		}
		if(event.getInventory() instanceof PlayerInventory)
		{
			return;
		}
		
		armorManager.timedDeCloakPlayer((Player)event.getPlayer());
	}
	
	@EventHandler
	public void onEntityDamager(EntityDamageEvent event)
	{
		if(!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		if(armorManager.getChainPlayers().contains((Player)event.getEntity()))
		{
			armorManager.timedDeCloakPlayer((Player) event.getEntity());
		}
	}
	
	@EventHandler
	public void onEntityAttack(EntityDamageByEntityEvent event)
	{
		if(!(event.getDamager() instanceof Player))
		{
			return;
		}
		
		if(armorManager.getChainPlayers().contains((Player)event.getDamager()))
		{
			armorManager.timedDeCloakPlayer((Player)event.getDamager());
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		if(!(event.getEntity().getShooter() instanceof Player))
		{
			return;
		}
		System.out.println("PROJECTILE LAUNCH");
		
		if(armorManager.getChainPlayers().contains((Player)event.getEntity().getShooter()))
		{
			armorManager.timedDeCloakPlayer((Player) event.getEntity().getShooter());
		}
	}
	
	@EventHandler
	public void onToggleSprint(PlayerToggleSprintEvent event)
	{
		if(armorManager.getChainPlayers().contains(event.getPlayer()))
		{
			armorManager.timedDeCloakPlayer(event.getPlayer());
		}
	}
}