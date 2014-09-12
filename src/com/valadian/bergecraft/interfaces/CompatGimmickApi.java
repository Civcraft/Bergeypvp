package com.valadian.bergecraft.interfaces;
import org.bukkit.entity.Player;

import com.gimmicknetwork.gimmickapi.*;
import com.valadian.bergecraft.ABergMod;
import com.valadian.bergecraft.BergeyPvp;

public class CompatGimmickApi implements IDisabler{
	
	private ABergMod plugin_ = null;
	public CompatGimmickApi(ABergMod plugin)
	{
		plugin_ = plugin;
		plugin_.info("Loaded Disabler: GimmickAPI");
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return plugin_.config_.get("gimmick_api_enabled").getBool();
	}

	@Override
	public boolean isBergecraftDisabledFor(Player player) {
		// TODO Auto-generated method stub
		String pvpmode = plugin_.config_.get("gimmick_api_pvpmode").getString();
		return isEnabled() && !pvpmode.equals(GimmickAPI.getPvpModeForPlayer(player));
	}
}
