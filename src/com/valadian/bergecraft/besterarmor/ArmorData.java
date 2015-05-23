package com.valadian.bergecraft.besterarmor;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ArmorData 
{
	
	private ArmorType armorType;
	private EnchantType enchantType;
	private boolean hasThorns;
	
	
	public ArmorData(ArmorType armorType, EnchantType enchantType, boolean thorns)
	{
		this.armorType = armorType;
		this.enchantType = enchantType;
		this.hasThorns = thorns;
	}
	
	public ArmorData()
	{
		armorType = ArmorType.NONE;
		enchantType = EnchantType.NONE;
		hasThorns = false;
	}
	
	public ArmorType getArmorType()
	{
		return this.armorType;
	}
	
	public EnchantType getEnchantType()
	{
		return this.enchantType;
	}
	
	public boolean getThorns()
	{
		return this.hasThorns;
	}
	
	
	public void setArmorType(ArmorType type)
	{
		this.armorType = type;
	}
	
	public void setEnchantTYpe(EnchantType type)
	{
		this.enchantType = type;
	}
	
	public void setThorns(boolean thorns)
	{
		this.hasThorns = thorns;
	}
			
	
	public enum ArmorType
	{
		LEATHER(new Material[]{Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}),
		CHAINMAIL(new Material[]{Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS}),
		GOLD(new Material[]{Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS}),
		IRON(new Material[]{Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS}),
		DIAMOND(new Material[]{Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS}),
		NONE();
		
		ArrayList<Material> pieces = new ArrayList<Material>();
		private ArmorType(Material crap[])
		{
			this.pieces.addAll(Arrays.asList(crap));
		}
		private ArmorType()
		{
			
		}
		
		public static ArmorType getArmorClass(Material armorItem)
		{
			for(ArmorType type : ArmorType.values())
			{
				if(type.pieces.contains(armorItem))
				{
					return type;
				}
			}
			
			return NONE;
		}
	}
	
	public enum EnchantType
	{
		PROT(Enchantment.PROTECTION_ENVIRONMENTAL, 4),
		PROJECTILEPROT(Enchantment.PROTECTION_PROJECTILE, 4),
		BLASTPROT(Enchantment.PROTECTION_EXPLOSIONS, 4),
		FIREPROT(Enchantment.PROTECTION_FIRE, 4),
		NONE();
		
		Enchantment enchant;
		int enchantLevel;
		private EnchantType(Enchantment enchant, int level)
		{
			this.enchant = enchant;
			this.enchantLevel = level;
		}
		private EnchantType()
		{
			enchant = null;
			enchantLevel = 0;
		}
		
		public static EnchantType getArmorEnchantments(ItemStack armorPiece)
		{
			EnchantType enchantmentType = NONE;
			for(Enchantment enchant : armorPiece.getEnchantments().keySet())
			{
				for(EnchantType type : EnchantType.values())
				{
					if((type.enchant != null) && (type.enchant.equals(enchant) && (armorPiece.getEnchantments().get(enchant) == type.enchantLevel)))
					{
						enchantmentType = type;
						break;
					}
				}
				
			}
			
			return enchantmentType;
		}
	}
}
