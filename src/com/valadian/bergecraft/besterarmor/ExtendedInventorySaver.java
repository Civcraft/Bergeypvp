package com.valadian.bergecraft.besterarmor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.valadian.bergecraft.BergeyPvp;


public class ExtendedInventorySaver
{

	private HashMap<Player, Inventory> extendedInventoriesMASTER;
	
	private File directory = BergeyPvp.getPlugin().getDataFolder();
	
	
	public ExtendedInventorySaver()
	{
		this.extendedInventoriesMASTER = new HashMap<Player, Inventory>();
	}
	public ExtendedInventorySaver(HashMap<Player, Inventory> input)
	{
		this.extendedInventoriesMASTER = input;
	}
	
	public Inventory getPlayerExtendedInventory(Player player)
	{
		System.out.println(this.extendedInventoriesMASTER.get(player));
		Inventory newInventory = Bukkit.createInventory(player, 54, "Extended Inventory");
		if(this.extendedInventoriesMASTER.get(player) == null)
		{
			this.extendedInventoriesMASTER.put(player, newInventory);
			return newInventory;
		}
		return this.extendedInventoriesMASTER.get(player);
	}
	
	public void setPlayerExtendedInventory(Player player, Inventory inventory)
	{
		this.extendedInventoriesMASTER.put(player, inventory);
	}
	
	public void removePlayerExtendedInventory(Player player)
	{
		this.extendedInventoriesMASTER.remove(player);
		fileDeleter(player);
	}
	
	
	
	
	public void saveExtendedInventory(Player player) 
	{
		File extendedInventoryFile = fileCreator(player);
		Inventory playerExtendedInventory = this.extendedInventoriesMASTER.get(player);
		
		if(playerExtendedInventory == null)
		{
			return;
		}
		try 
		{
			FileOutputStream fileOut = new FileOutputStream(extendedInventoryFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			
			
			ItemStack empty = player.getInventory().getHelmet();
			empty.setType(Material.AIR);
			empty.setAmount(0);
			
			for(ItemStack item : playerExtendedInventory.getContents())
			{
				try
				{
					if(item == null)
					{
						out.writeObject(empty.serialize());
						continue;
					}
					else if(item.hasItemMeta())
					{
						out.writeObject(empty.serialize());
						player.getWorld().dropItemNaturally(player.getLocation(), item);
						continue;
					}
					else
					{
						out.writeObject(item.serialize());
						out.writeObject(empty.serialize());
					}
				}
				catch(NotSerializableException e)
				{
					player.getWorld().dropItemNaturally(player.getLocation(), item);
					out.writeObject(empty.serialize());
					
					System.out.println("INNER");
					System.out.println(e);
				}
			}

			out.close();
			fileOut.close();
		} 
		catch (Exception e) 
		{
			System.out.println("OUTER");
			e.printStackTrace();
		}
		
		this.extendedInventoriesMASTER.remove(player);
	}

	@SuppressWarnings("unchecked")
	public void loadExtendedInventory(Player player) 
	{
		File extendedInventoryFile = fileCreator(player);
		Inventory playerExtendedInventory = Bukkit.createInventory(player, 54, "Extended Inventory");
		
		ItemStack[] inventory = new ItemStack[54];
		
		ItemStack empty = player.getInventory().getHelmet();
		empty.setType(Material.AIR);
		empty.setAmount(0);
		
		try 
		{
			FileInputStream fileIn = new FileInputStream(extendedInventoryFile);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        
	        for(int loop = 0; loop < 54; loop++)
	        {
	        	try
	        	{
	        		inventory[loop] = ItemStack.deserialize((Map<String, Object>)in.readObject());
	        	}
	        	catch(Exception e)
	        	{
	        		inventory[loop] = empty;
	        		
	        		System.out.println("INNER");
	        		System.out.println(e);
	        	}
	        }
	        playerExtendedInventory.setContents(inventory);
	        in.close();
	        fileIn.close();
		} 
		catch (Exception e) 
		{
			System.out.println("OUTER");
		}
		this.extendedInventoriesMASTER.put(player, playerExtendedInventory);
	}
	
	private File fileCreator(Player player)
    {
		if (!this.directory.isDirectory()) 
		{
			this.directory.mkdir();
		}
		
		File extendedInventoryFile = new File(directory, player.getDisplayName() + "_ExtendedInventory.ser");
		
		if (!extendedInventoryFile.isFile()) 
		{
			try 
			{
				extendedInventoryFile.createNewFile();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}		
		
		return extendedInventoryFile;
    }
	
	private void fileDeleter(Player player)
	{
		if (!this.directory.isDirectory()) 
		{
			return;
		}
		
		File extendedInventoryFile = new File(directory, player.getDisplayName() + "_ExtendedInventory.ser");
		
		if (extendedInventoryFile.isFile()) 
		{
			try
			{
				System.out.println("DELETE FILE");
				extendedInventoryFile.delete();
			}
			catch(Exception e)
			{
				
			}
		}
	}
}
