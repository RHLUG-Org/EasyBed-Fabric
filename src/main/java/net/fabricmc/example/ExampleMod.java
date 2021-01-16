package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.*;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

public class ExampleMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		
		System.out.println("EasyBed initialized!");
		CommandRegistrationCallback.EVENT.register(
            this::register
		);

	}
	
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		// Inject the vote command
		Command vc = new VoteCommand();
	
		dispatcher.register(literal("vote").executes(
				context -> {
//					MinecraftServer internalServer = context.getSource().getMinecraftServer();
//					internalServer.sendSystemMessage(new LiteralText("You voted!"), Util.NIL_UUID);
		            ServerCommandSource theSource = context.getSource();
		            theSource.sendFeedback(new LiteralText("You voted!"), true);
					
					return 1;
				}
        ));
	}

}
