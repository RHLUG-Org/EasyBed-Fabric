package net.fabricmc.example;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class SleepEventHandler implements PlayerEventHandler {

	@Override
	public void onEvent(PlayerEntity playerInfo) {
		if(!(playerInfo instanceof ServerPlayerEntity))
            return;
		System.out.println("sleep success");
		playerInfo.getCommandSource().sendFeedback(new LiteralText("Sleeping!!! Yay!"), true);
	}


}
