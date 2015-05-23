package com.valadian.bergecraft.besterarmor.Listeners;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import com.valadian.bergecraft.BergeyPvp;
import com.valadian.bergecraft.besterarmor.ArmorData.ArmorType;
import com.valadian.bergecraft.besterarmor.Managers.ArmorManager;

public class HorseMountListener implements Listener
{
	private ArmorManager armorManager;
	
	public HorseMountListener()
	{
		armorManager = BergeyPvp.getArmorManager();
	}
	
	@EventHandler
	public void onHorseDamage(EntityDamageEvent event)
	{	
		if(!(event.getEntity() instanceof Horse))
		{
			return;
		}
		if(!(((Horse)event.getEntity()).getPassenger() instanceof Player))
		{
			return;
		}
		
		Player player = (Player) event.getEntity().getPassenger();
		if(ArmorManager.readArmor(player).equals(ArmorType.IRON))
		{
			for(ItemStack item : player.getInventory().getArmorContents())
			{
				short dura = item.getDurability();
				item.setDurability(dura--);
			}
			event.setCancelled(true);
			System.out.println("DAMAGE CANCELED");
			return;
		}
	}
}
