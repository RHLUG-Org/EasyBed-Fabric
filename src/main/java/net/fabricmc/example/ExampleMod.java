package net.fabricmc.example;

import static net.minecraft.server.command.CommandManager.literal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import net.fabricmc.example.EasyBedConfig;

public class ExampleMod implements ModInitializer {
	
	HashMap<String, Float> configValues = new HashMap<>();
	GsonBuilder builder = new GsonBuilder();
	Gson gson;
	EasyBedConfig cfg;
	
	@Override
	public void onInitialize() {
		builder.setPrettyPrinting();
		gson = builder.create();
		
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
			String json = gson.toJson(EasyBedConfig.defaultConfig());
			Files.write(path, json.getBytes(), StandardOpenOption.WRITE);
			
		}
		String json_from_file = new String (Files.readAllBytes(path));
		System.out.println("JSON " + json_from_file);
		cfg = gson.fromJson(json_from_file, EasyBedConfig.class);
		System.out.println("Percentage: " + cfg.getPercentage());
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
