package net.fabricmc.example;

import java.util.Timer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SleepEventHandler implements PlayerEventHandler {

	@Override
	public void onEvent(PlayerEntity playerInfo) {
		if (!(playerInfo instanceof ServerPlayerEntity))
			return;
		// TODO Wait 3 seconds in the future and cancel if person is out of bed
		Timer timer = new Timer();
		timer.schedule(new VoteTimerTask(playerInfo), 1);
	}

}
