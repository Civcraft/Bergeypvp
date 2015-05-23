package com.valadian.bergecraft;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import vg.civcraft.mc.namelayer.NameAPI;
import vg.civcraft.mc.namelayer.config.NameConfigListener;
import vg.civcraft.mc.namelayer.config.NameConfigManager;
import vg.civcraft.mc.namelayer.config.annotations.NameConfig;
import vg.civcraft.mc.namelayer.config.annotations.NameConfigs;
import vg.civcraft.mc.namelayer.config.annotations.NameConfigType;

import com.valadian.bergecraft.bergeypvp.WeaponTimer;
import com.valadian.bergecraft.besterarmor.ArmorData.ArmorType;
import com.valadian.bergecraft.besterarmor.ExtendedInventorySaver;
import com.valadian.bergecraft.besterarmor.Listeners.ArmorListener;
import com.valadian.bergecraft.besterarmor.Listeners.DecloakListener;
import com.valadian.bergecraft.besterarmor.Listeners.HorseMountListener;
import com.valadian.bergecraft.besterarmor.Managers.ArmorManager;

public class BergeyPvp extends JavaPlugin implements Listener, NameConfigListener{

	private NameConfigManager config_;
    protected final Logger log_ = getLogger();
    
    private static BergeyPvp thisPlugin;
	
	private static ArmorManager armorManager;
	
	private static ArmorListener armorListener;
	private static HorseMountListener horseListener;
	private static DecloakListener decloakListener;
	
	private static ExtendedInventorySaver extendedInventoriesSaver;
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		config_ = NameAPI.getNameConfigManager();
		config_.registerListener(this, this);
		
		besterArmorInitializationCrap();
        
        besterArmorTimedTasks();
	}
	
	public void onDisable() 
    {
    	saveInventories();
    }
	
	private void besterArmorInitializationCrap()
    {
    	thisPlugin = this;
        
        extendedInventoriesSaver = new ExtendedInventorySaver();
        
        armorManager = new ArmorManager(extendedInventoriesSaver);
        
        armorListener = new ArmorListener();
        horseListener = new HorseMountListener();
        decloakListener = new DecloakListener();        
        
        establishListeners();
    }
    
    private void establishListeners()
    {
    	getServer().getPluginManager().registerEvents(armorListener, thisPlugin);
    	getServer().getPluginManager().registerEvents(horseListener, thisPlugin);
    	getServer().getPluginManager().registerEvents(decloakListener, thisPlugin);
    }
    
    private void besterArmorTimedTasks()
    {
    	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() 
            {
                armorManager.decrimentCoolDown();
            }
        }, 0L, 20L);
        
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() 
            {                
                for(Player player : armorManager.getFireProtPlayers())
                {
                	armorManager.distributePotionEffects(player);
                }
            }
        }, 0L, 100L);
    }
    
    private void saveInventories()
    {
    	for(Player player : Bukkit.getServer().getOnlinePlayers())
    	{
    		if(ArmorManager.readArmor(player).getArmorType().equals(ArmorType.GOLD))
    		{
    			extendedInventoriesSaver.saveExtendedInventory(player);
    		}
    	}
    }
    
    public static ArmorManager getArmorManager()
    {
    	return armorManager;
    }
    
    public static ExtendedInventorySaver getExtendedInventorySaver()
    {
    	return extendedInventoriesSaver;
    }
    
    public static Plugin getPlugin()
    {
    	return thisPlugin;
    }

    HashMap<Player,WeaponTimer> cooldowns = new HashMap<Player,WeaponTimer>();
    
    @NameConfigs({
	    @NameConfig(name="bergey_pvp_weapons", def="true", type = NameConfigType.Bool),
	    @NameConfig(name="bergey_pvp_weapon_cooldown", def="3000",type=NameConfigType.Int),
	    @NameConfig(name="nerf_sharpness", def="true", type = NameConfigType.Bool),
	    @NameConfig(name="sharpness_damage_per_level", type=NameConfigType.Double, def="0.66"),
	    @NameConfig(name="nerf_strength", def="true", type = NameConfigType.Bool),
    	@NameConfig(name="strength_multiplier", type=NameConfigType.Double, def="1.5")
    })
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
    	if(event.getDamager() instanceof Player)
    	{
        	Player attacker = (Player) event.getDamager();
        	ItemStack stack = attacker.getItemInHand();
        	stack.getDurability();
        	long now = System.currentTimeMillis();
    		if(config_.get(this, "bergey_pvp_weapons").getBool())
    		{
        		int cooldown = config_.get(this, "bergey_pvp_weapon_cooldown").getInt();
    	    	if(cooldowns.containsKey(attacker) && !cooldowns.get(attacker).cancelled)
    	    	{
    				event.setCancelled(true);
    				//cooldowns.get(attacker).resetTimer();
    				//attacker.sendMessage("[Bergey Pvp] Attacking too fast");
    				return;
        		}
    	    	else
    	    	{
        	    	//log_.log(Level.INFO, "Scheduling Cooldown!");
        			//WeaponTimer timer = new WeaponTimer(attacker, stack, now, cooldown);
        			//timer.runTaskTimer(this, 0, 20/5);
        			//cooldowns.put(attacker, timer);
        			//Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new WeaponTimer(attacker, stack, now, cooldown), 0, 20/5);
    	    	}
        	}

            if (config_.get(this, "nerf_sharpness").getBool()) {
                if (!(event.getDamager() instanceof Player)) {
                    return;
                  }
                  Player player = (Player)event.getDamager();
                  ItemStack item = player.getItemInHand();
                  //Apply Strength Nerf
                  final double strengthMultiplier = config_.get(this, "strength_multiplier").getDouble();
                  if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                      if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                        final int potionLevel = effect.getAmplifier() + 1;
                        final double unbuffedDamage = event.getDamage() / (1.3 * potionLevel + 1);
                        final double newDamage = unbuffedDamage + (potionLevel * strengthMultiplier);
                        event.setDamage(newDamage);
                        break;
                      }
                    }
                  }
                  //Apply Sharp Nerf
                  int sharpness = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                  final double sharpnessOffset = config_.get(this, "sharpness_damage_per_level").getDouble();
                  if(sharpness>0){
	                  //final double unbuffedDamage = event.getDamage() / potionScale;
	                  final double newDamage = event.getDamage() - 1.25 * sharpness + sharpnessOffset * sharpness;
	                  //final double newDamage = fixedUnbuffedDamage * potionScale;
	          		  log_.log(Level.INFO, "Reducing Sharpness damage from: "+event.getDamage()+" to: "+newDamage);
	                  event.setDamage(newDamage);
                  }
            }
    	}
    }
    @NameConfigs ({
    	@NameConfig(name="bergey_armor", def="true", type = NameConfigType.Bool),
    	@NameConfig(name="bergey_armor_50_perc_mit", def="10",type=NameConfigType.Int),
    	@NameConfig(name="bergey_prot", def="true", type = NameConfigType.Bool),
    	@NameConfig(name="bergey_prot_50_perc_mit", def="7",type=NameConfigType.Int),
    	@NameConfig(name="bergey_prot_scale", def="0.33",type=NameConfigType.Double),
    })
    @EventHandler(priority = EventPriority.LOWEST) // ignoreCancelled=false
    public void onPlayerTakeDamage(EntityDamageEvent event) {
      if (!config_.get(this, "bergey_armor").getBool()) {
          return;
      }
      double damage = event.getDamage();
      if (damage <= 0.0000001D) {
        return;
      }
      DamageCause cause = event.getCause();
      if (!isCommonDamage(cause)) {
          return;
      }
      
      boolean factorProt = cause.equals(DamageCause.ENTITY_ATTACK) ||
    		    		   cause.equals(DamageCause.PROJECTILE);
      
      Entity entity = event.getEntity();
      if (!(entity instanceof Player)) {
        return;
      }
      Player defender = (Player)entity;
  	  
      double defense = getDefense(defender);
      double epf = getAverageEPF(defender);
      double bergey_epf = getAverageBergeyEPF(defender);
      
      double vanilla_reduction = defense * 0.04;
      double vanilla_protection_reduction = 0;
      if(factorProt){
    	  vanilla_protection_reduction = epf * 0.04;
      }
      double vanilla_damage_taken_ratio = (1 - vanilla_reduction) * (1 - vanilla_protection_reduction);
      
      double originalDamage = damage / vanilla_damage_taken_ratio;
      
      double bergey_reduction = defense / (defense + config_.get(this, "bergey_armor_50_perc_mit").getInt());
      double bergey_prot_reduction = 0;
      if(factorProt){
    	  bergey_prot_reduction = bergey_epf / (bergey_epf + config_.get(this, "bergey_prot_50_perc_mit").getInt()) * config_.get(this, "bergey_prot_scale").getDouble();
      }
      double bergey_damage_taken_ratio = (1 - bergey_reduction) * (1 - bergey_prot_reduction);
      
      double newDamage = originalDamage * bergey_damage_taken_ratio;
      DecimalFormat df = new DecimalFormat("#.##");
      if(factorProt) {
	      log_.log(Level.INFO, "[Vanilla] Armor: "+df.format(vanilla_reduction)+", Enchant: "+df.format(vanilla_protection_reduction)+"\n"+
	"                              [Bergey ] Armor: "+df.format(bergey_reduction)+", Enchant: "+df.format(bergey_prot_reduction)+"\n"+
	"                                        Damage Before: "+df.format(damage)+ " Damage After: "+df.format(newDamage));
      }
      else {
    	  log_.log(Level.INFO, "[Vanilla] Armor: "+df.format(vanilla_reduction)+", \n"+
    "                              [Bergey ] Armor: "+df.format(bergey_reduction)+"\n"+
	"                                        Damage Before: "+df.format(damage)+ " Damage After: "+df.format(newDamage));
      }
      event.setDamage(newDamage);
    }
    
    private boolean isCommonDamage(DamageCause cause)
    {
    	return cause.equals(DamageCause.ENTITY_ATTACK) ||
    		   cause.equals(DamageCause.PROJECTILE) ||
    		   cause.equals(DamageCause.FIRE) ||
    		   cause.equals(DamageCause.LAVA) ||
    		   cause.equals(DamageCause.CONTACT) ||
    		   cause.equals(DamageCause.ENTITY_EXPLOSION) ||
    		   cause.equals(DamageCause.LIGHTNING) ||
    		   cause.equals(DamageCause.BLOCK_EXPLOSION);
    }
    private double getDefense(Player player)
    {
	   PlayerInventory inv = player.getInventory();
	   ItemStack boots = inv.getBoots();
	   ItemStack helmet = inv.getHelmet();
	   ItemStack chest = inv.getChestplate();
	   ItemStack pants = inv.getLeggings();
	   int def = 0;
	   if(helmet!=null){
		   if(helmet.getType() == Material.LEATHER_HELMET)def = def + 1;
		   else if(helmet.getType() == Material.GOLD_HELMET)def = def + 2;
		   else if(helmet.getType() == Material.CHAINMAIL_HELMET)def = def + 2;
		   else if(helmet.getType() == Material.IRON_HELMET)def = def + 2;
		   else if(helmet.getType() == Material.DIAMOND_HELMET)def = def + 3;
	   }
	   //
	   if(boots!=null){
		   if(boots.getType() == Material.LEATHER_BOOTS)def = def + 1;
		   else if(boots.getType() == Material.GOLD_BOOTS)def = def + 1;
		   else if(boots.getType() == Material.CHAINMAIL_BOOTS)def = def + 1;
		   else if(boots.getType() == Material.IRON_BOOTS)def = def + 2;
		   else if(boots.getType() == Material.DIAMOND_BOOTS)def = def + 3;
	   }
	   //
	   if(pants!=null){
		   if(pants.getType() == Material.LEATHER_LEGGINGS)def = def + 2;
		   else if(pants.getType() == Material.GOLD_LEGGINGS)def = def + 3;
		   else if(pants.getType() == Material.CHAINMAIL_LEGGINGS)def = def + 4;
		   else if(pants.getType() == Material.IRON_LEGGINGS)def = def + 5;
		   else if(pants.getType() == Material.DIAMOND_LEGGINGS)def = def + 6;
	   }
	   //
	   if(chest!=null){
		   if(chest.getType() == Material.LEATHER_CHESTPLATE)def = def + 3;
		   else if(chest.getType() == Material.GOLD_CHESTPLATE)def = def + 5;
		   else if(chest.getType() == Material.CHAINMAIL_CHESTPLATE)def = def + 5;
		   else if(chest.getType() == Material.IRON_CHESTPLATE)def = def + 6;
		   else if(chest.getType() == Material.DIAMOND_CHESTPLATE)def = def + 8;
	   }
	   return def;
    }

    private double getAverageEPF(Player player)
    {
 	   PlayerInventory inv = player.getInventory();
 	   
 	   int epf = 0;
	   for (ItemStack armor : inv.getArmorContents()) {
		   int level = armor.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
		   if(level == 4)
			   level = 5;
		   epf += level;
	   }
	   return epf*0.75;
    }
    
    private double getAverageBergeyEPF(Player player)
    {
  	   PlayerInventory inv = player.getInventory();
  	   
  	   int epf = 0;
 	   for (ItemStack armor : inv.getArmorContents()) {
 		   epf += armor.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) * 1.25;
 	   }
 	   return epf*0.75;
    }
    
//    @Bergification(opt="bergey_logout", def="true")
//    @EventHandler(priority = EventPriority.LOWEST) // ignoreCancelled=false
//    public void onEntityLogout(PlayerQuitEvent event) {
//    
//    }
    
    @EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerJoinEvent event){
	    setMaxHealth(event.getPlayer());
	}

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCloseInventory(InventoryCloseEvent event){
    	HumanEntity human = event.getPlayer();
    	if(human instanceof Player){
    		setMaxHealth((Player) human);
    	}
    }

    @NameConfigs ({
    	@NameConfig(name="bergey_health", def="true", type = NameConfigType.Bool),
    	@NameConfig(name="bergey_base_health", def="20.0",type=NameConfigType.Double),
    	@NameConfig(name="bergey_max_bonus_health", def="20.0",type=NameConfigType.Double),
    	@NameConfig(name="bergey_health_bonus_50_perc_durability", def="850",type=NameConfigType.Double)
    })
	public void setMaxHealth(Player player){

        if (!config_.get(this, "bergey_health").getBool()) {
          return;
        }
		double maxHealth = config_.get(this, "bergey_base_health").getDouble();
		
		double durability = 0;
 	    for (ItemStack armor : player.getInventory().getArmorContents()) {
 	    	durability += armor.getType().getMaxDurability();
 	    }
 	    
 	   maxHealth += config_.get(this, "bergey_max_bonus_health").getDouble() *
 	    		durability / (durability + config_.get(this, "bergey_health_bonus_50_perc_durability").getDouble());
 	    if(maxHealth != ((Damageable) player).getMaxHealth()){
			log_.log(Level.INFO, "Setting Player: "+player.getName()+" to "+maxHealth+" health");
			if(((Damageable)player).getHealth()>maxHealth)
			{
				player.setHealth(maxHealth);
			}
			player.setMaxHealth(maxHealth);
 	    }
	}
    
	public void resetMaxHealth(Player player){
		double maxHealth = 20.0d;
 	    if(maxHealth != ((Damageable) player).getMaxHealth()){
 	    	log_.log(Level.INFO, "Setting Player: "+player.getName()+" to "+maxHealth+" health");
			if(((Damageable)player).getHealth()>maxHealth)
			{
				player.setHealth(maxHealth);
			}
			player.setMaxHealth(maxHealth);
 	    }
	}
    
    @NameConfig(name="ender_pearl_teleportation", def="false", type = NameConfigType.Bool)
      @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
      public void onTeleport(PlayerTeleportEvent event) {
        TeleportCause cause = event.getCause();
        if (cause.equals(TeleportCause.ENDER_PEARL) && 
        	!config_.get(this, "ender_pearl_teleportation").getBool()) {
        	event.setCancelled(true);
        	event.getPlayer().sendMessage("Ender pearls are disabled in Bergecraft PVP mode.");
        }
    }
//    @Override
//    public void onLoad()
//    {
//    	super.onLoad();
//    }
}
