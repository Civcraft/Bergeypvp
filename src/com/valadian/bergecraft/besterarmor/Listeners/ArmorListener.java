package com.valadian.bergecraft.besterarmor.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import com.valadian.bergecraft.BergeyPvp;
import com.valadian.bergecraft.besterarmor.ArmorData;
import com.valadian.bergecraft.besterarmor.ArmorData.ArmorType;
import com.valadian.bergecraft.besterarmor.ArmorData.EnchantType;
import com.valadian.bergecraft.besterarmor.ExtendedInventorySaver;
import com.valadian.bergecraft.besterarmor.Managers.ArmorManager;


public class ArmorListener implements Listener
{
	private ArmorManager armorManager;
	private ExtendedInventorySaver inventorySaver;
	
	public ArmorListener()
	{
		this.armorManager = BergeyPvp.getArmorManager();
		this.inventorySaver = BergeyPvp.getExtendedInventorySaver();
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if(!(event.getPlayer() instanceof Player))
		{
			return;
		}
		
		this.armorManager.manageArmorBenefit((Player) event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerDeath(EntityDeathEvent event)
	{
		if(!(event.getEntity() instanceof Player))
		{
			return;
		}
		armorManager.manageArmorBenefit((Player) event.getEntity());
	}
	
	@EventHandler
	public void onArmorBreak(PlayerItemBreakEvent event)
	{
		if(ArmorData.ArmorType.getArmorClass(event.getBrokenItem().getType()).equals(ArmorType.NONE))
		{
			return;
		}
		
		armorManager.manageArmorBenefit(event.getPlayer());
	}
	
	@EventHandler
	public void onArmorClick(InventoryClickEvent event)
	{
		if(!(event.isRightClick()))
		{
			return;
		}
		
		if(ArmorManager.readArmor((Player) event.getWhoClicked()).getArmorType() != ArmorType.GOLD)
		{
			return;
		}
		System.out.println("RIGHT CLICK");
		
		Player player = (Player) event.getWhoClicked();
		if(ArmorType.getArmorClass(event.getCurrentItem().getType()).equals(ArmorType.GOLD))
		{
			event.setCancelled(true);
			armorManager.manageExtendedInventory(player);
		}
	}
	
	@EventHandler
	public void onVelocityChange(PlayerVelocityEvent event)
	{
		if(ArmorManager.readArmor(event.getPlayer()).getEnchantType() == EnchantType.BLASTPROT)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamager(EntityDamageEvent event)
	{
		if(!(event.getEntity() instanceof Player))
		{
			return;
		}
		
		if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		System.out.println("JOIN: " + event.getPlayer().getDisplayName());
		armorManager.manageArmorBenefit(event.getPlayer());
		
		
		if(ArmorManager.readArmor(event.getPlayer()).getArmorType() == ArmorType.GOLD)
		{
			System.out.println("LOAD INVENTORY");
			this.inventorySaver.loadExtendedInventory(event.getPlayer());
		}
		
		for(Player player : armorManager.getChainPlayers())
		{
			event.getPlayer().hidePlayer(player);
		}
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		System.out.println("LEAVE: " + event.getPlayer().getDisplayName());
		
		if(ArmorManager.readArmor(event.getPlayer()).getArmorType() == ArmorType.GOLD)
		{
			System.out.println("SAVE INVENTORY");
			this.inventorySaver.saveExtendedInventory(event.getPlayer());
		}
		
	}
}
