package net.fabricmc.example;

import net.minecraft.entity.player.PlayerEntity;

public interface PlayerEventHandler {
	void onEvent(PlayerEntity playerInfo);
}
