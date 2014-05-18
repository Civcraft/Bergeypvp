package com.valadian.bergecraft.interfaces;
import org.bukkit.entity.Player;

import com.gimmicknetwork.gimmickapi.*;
import com.valadian.bergecraft.BergeyPvp;

public class CompatGimmickApi implements IDisabler{
	
	public CompatGimmickApi()
	{
		BergeyPvp.info("Loaded Disabler: GimmickAPI");
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return BergeyPvp.config_.get("gimmick_api_enabled").getBool();
	}

	@Override
	public boolean isBergecraftDisabledFor(Player player) {
		// TODO Auto-generated method stub
		String pvpmode = BergeyPvp.config_.get("gimmick_api_pvpmode").getString();
		return isEnabled() && !pvpmode.equals(GimmickAPI.getPvpModeForPlayer(player));
	}
}
