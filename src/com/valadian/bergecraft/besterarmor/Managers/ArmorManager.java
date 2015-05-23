package com.valadian.bergecraft.besterarmor.Managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.valadian.bergecraft.BergeyPvp;
import com.valadian.bergecraft.besterarmor.ArmorData;
import com.valadian.bergecraft.besterarmor.ArmorData.ArmorType;
import com.valadian.bergecraft.besterarmor.ArmorData.EnchantType;
import com.valadian.bergecraft.besterarmor.ExtendedInventorySaver;

public class ArmorManager 
{
	private HashMap<Player, ArmorData> currentArmors = new HashMap<Player, ArmorData>();
	private HashMap<Player, Long> deCloakTimers = new HashMap<Player, Long>();
	
	private ArrayList<Player> goldPlayers = new ArrayList<Player>();
	private ArrayList<Player> firePlayers = new ArrayList<Player>();
	
	private ExtendedInventorySaver extendedInventorySaver;
	
	private int seconds = 10;
	private final long MILLISECOND = 1000;
	
	public ArmorManager(ExtendedInventorySaver saver)
	{
		this.extendedInventorySaver = saver;
	}
	
	// venk was here
	public static ArmorData readArmor(Player player)
	{
		ArmorType armorType = null;
		EnchantType enchantType = null;
		boolean hasThorns = false;
		
		for(ItemStack item : player.getInventory().getArmorContents())
		{ 	
			ArmorType armorTemp = ArmorType.getArmorClass(item.getType());
			EnchantType enchantTemp = EnchantType.getArmorEnchantments(item);
			
			if(armorType == null)
			{
				armorType = armorTemp;
			}
			if(enchantType == null)
			{
				enchantType = enchantTemp;
			}
			
			if(item.containsEnchantment(Enchantment.THORNS) && item.getEnchantmentLevel(Enchantment.THORNS) == 2)
			{
				hasThorns = true;
			}
			
			if(!armorType.equals(armorTemp))
			{
				armorType = ArmorType.NONE;
				enchantType = EnchantType.NONE;
				break;
			}	
			if(!enchantType.equals(enchantTemp) || enchantType.equals(EnchantType.NONE))
			{
				enchantType = EnchantType.NONE;
			}
		}
		
		return new ArmorData(armorType, enchantType, hasThorns);
		
	}
	
	public void manageArmorBenefit(Player player)
	{
		
		ArmorData armorData = readArmor(player);
		
		if(this.currentArmors.get(player) == null)
		{
			this.currentArmors.put(player, armorData);
			establishArmorBenefit(player, armorData.getArmorType());
			establishEnchantBenefit(player, armorData.getEnchantType());
		}
		else
		{
			if(armorData.getArmorType() == ArmorType.NONE)
			{
				removeArmorBenefit(player, this.currentArmors.get(player).getArmorType());
			}
			else if(armorData.getArmorType() != this.currentArmors.get(player).getArmorType())
			{
				removeArmorBenefit(player, this.currentArmors.get(player).getArmorType());
				establishArmorBenefit(player, armorData.getArmorType());
			}
			
			if(armorData.getEnchantType() == EnchantType.NONE)
			{
				removeEnchantBenefit(player, this.currentArmors.get(player).getEnchantType());
			}
			else if(armorData.getEnchantType() != this.currentArmors.get(player).getEnchantType())
			{
				removeEnchantBenefit(player, this.currentArmors.get(player).getEnchantType());
				establishEnchantBenefit(player, armorData.getEnchantType());
			}
			this.currentArmors.put(player, armorData);
		}
		
		
		this.currentArmors.put(player, armorData);		
	}
	
	public void establishArmorBenefit(Player player, ArmorType armorType)
	{
		
		switch(armorType)
		{
			case LEATHER:
				PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);
				PotionEffect jump = new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2);
				
				player.addPotionEffect(speed);
				player.addPotionEffect(jump);
				break;
				
			case GOLD:
				this.goldPlayers.add(player);
				dropExtendedInventory(player, BergeyPvp.getArmorManager().extendedInventorySaver.getPlayerExtendedInventory(player));
				break;
				
			case CHAINMAIL:
				this.deCloakTimers.put(player, null);
				cloakPlayer(player);
				break;
				
			default:
				break;
		
		}
		
	}
	
	public void removeArmorBenefit(Player player, ArmorType armorType)
	{
		switch(armorType)
		{
			case LEATHER:
				player.removePotionEffect(PotionEffectType.SPEED);
				player.removePotionEffect(PotionEffectType.JUMP);
				break;
				
			case GOLD:
				this.goldPlayers.remove(player);
				dropExtendedInventory(player, this.extendedInventorySaver.getPlayerExtendedInventory(player));
				break;
				
			case CHAINMAIL:
				this.deCloakTimers.remove(player);
				deCloakPlayer(player);
				break;
				
			default:
				break;
		
		}
	}
	
	public void establishEnchantBenefit(Player player, EnchantType enchantType)
	{
		if(enchantType == EnchantType.FIREPROT)
		{
			this.firePlayers.add(player);
		}
	}
	
	public void removeEnchantBenefit(Player player, EnchantType enchantType)
	{
		if(enchantType == EnchantType.FIREPROT)
		{
			this.firePlayers.remove(player);
		}
	}
	
	public void manageExtendedInventory(Player player)
	{		
		player.openInventory(this.extendedInventorySaver.getPlayerExtendedInventory(player));
	}
	
	public void dropExtendedInventory(Player player, Inventory extendedInventory)
	{		
		for(ItemStack item : extendedInventory.getContents())
		{
			if(item != null)
			{
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			}
		}
		
		this.extendedInventorySaver.removePlayerExtendedInventory(player);
	}
	public static void cloakPlayer(Player player)
	{
		for(Player otherPlayer : BergeyPvp.getPlugin().getServer().getOnlinePlayers())
		{
			otherPlayer.hidePlayer(player);
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));
		player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2f, .5f);
		System.out.println(player.getDisplayName() + " CLOAKED");
	}
	
	public static void deCloakPlayer(Player player)
	{
		for(Player otherPlayer : BergeyPvp.getPlugin().getServer().getOnlinePlayers())
		{
			otherPlayer.showPlayer(player);
		}
		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 2f, .5f);
		System.out.println(player.getDisplayName() + " DECLOAKED");
	}
	
	public void timedDeCloakPlayer(Player player)
	{
		deCloakPlayer(player);
		
		this.deCloakTimers.put(player, new Long(System.currentTimeMillis() + (this.MILLISECOND * this.seconds)));
	}
	
	public void decrimentCoolDown()
	{
		for(Player player : this.deCloakTimers.keySet())
		{
			if(this.deCloakTimers.get(player) == null)
			{
				continue;
			}
			
			long time = this.deCloakTimers.get(player);
			
			if(time < System.currentTimeMillis())
			{
				if(!player.isSprinting())
				{
					cloakPlayer(player);
					this.deCloakTimers.put(player, null);
				}
				else
				{
					this.deCloakTimers.put(player, new Long(System.currentTimeMillis() + (this.MILLISECOND * this.seconds)));
				}
			}
			else
			{
				this.deCloakTimers.put(player, time);
			}
		}
	}
	
	public void distributePotionEffects(Player player)
	{
		Collection<PotionEffect> activePotionTypes = player.getActivePotionEffects();
		Collection<PotionEffect> activePotionEffects = new ArrayList<PotionEffect>();
		
		for(PotionEffect effect : activePotionTypes)
		{
			activePotionEffects.add(new PotionEffect(effect.getType(), 180, effect.getAmplifier()));
		}
		
		for(Entity entity : player.getNearbyEntities(10, 10, 10))
		{
			if(entity instanceof Player)
			{
				for(PotionEffect effect : activePotionEffects)
				{
					((Player)entity).removePotionEffect(effect.getType());
				}
				((Player) entity).addPotionEffects(activePotionEffects);
			}
		}
	}
	
	public ArrayList<Player> getGoldPlayers()
	{
		return this.goldPlayers;
	}
	
	public Set<Player> getChainPlayers()
	{
		return this.deCloakTimers.keySet();
	}
	
	public ArrayList<Player> getFireProtPlayers()
	{
		return this.firePlayers;
	}
}
