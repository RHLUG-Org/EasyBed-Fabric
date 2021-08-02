package net.fabricmc.example;

import java.util.TimerTask;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

// This separate class is done to be thread-safe, as this class will get
// a copy of playerInfo before the thread is created in run()
public class VoteTimeoutTask extends TimerTask {
	
	PlayerEntity playerInfo;
	
	public VoteTimeoutTask(PlayerEntity playerInfo) {
		this.playerInfo = playerInfo;
	}
	
	@Override
	public void run() {
		if(ExampleMod.isVoteProcess) {
			this.playerInfo.getCommandSource().getServer().getPlayerManager().broadcastChatMessage(
				new LiteralText("The vote has been active for too long, so we have reset it."), 
				MessageType.SYSTEM, 
				Util.NIL_UUID
			);
			ExampleMod.resetVoteProcess();
		}
	}
}
