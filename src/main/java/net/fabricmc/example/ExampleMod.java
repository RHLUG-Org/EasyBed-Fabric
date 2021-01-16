package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardOpenOption;

import javax.sound.sampled.AudioFormat.Encoding;

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
		
		try {
			this.loadConfigFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Apparently we have to do this from scratch;
	// Fabric has nothing built in to load configs
	private void loadConfigFile() throws IOException
	{
		Path path = Paths.get("config/easybed_config.json");
		// Using Java's NIO library
		if(!Files.exists(path))
		{
			// make a filled file
			Files.createFile(path);
			Files.write(path, "{\"percentage\": 0.5}".getBytes(), StandardOpenOption.WRITE);
		}
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
