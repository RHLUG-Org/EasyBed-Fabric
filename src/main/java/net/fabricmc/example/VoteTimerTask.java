package net.fabricmc.example;

import java.util.TimerTask;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

// This separate class is done to be thread-safe, as this class will get
// a copy of playerInfo before the thread is created in run()
public class VoteTimerTask extends TimerTask {
	
	PlayerEntity playerInfo;
	public VoteTimerTask(PlayerEntity playerInfo) {
		this.playerInfo = playerInfo;
	}
	
	@Override
	public void run(){
		this.playerInfo.getCommandSource().sendFeedback(new LiteralText("Type /vote to turn the time to daytime."), true);
		ExampleMod.startVoteProcess(playerInfo.getCommandSource());
	}
}
