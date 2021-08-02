package net.fabricmc.example;

import static net.minecraft.server.command.CommandManager.literal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;


public class ExampleMod implements ModInitializer {
	
	static final long DAY_LEN = 24000L;
	
	HashMap<String, Float> configValues = new HashMap<>();
	GsonBuilder builder = new GsonBuilder();
	Gson gson;
	static EasyBedConfig cfg;
	static HashSet<String> votedPlayers = new HashSet<>();
	
	static boolean isVoteProcess = false;
	static int numPlayersVoted = 0;
	
	private static boolean hasVoted(String playerUuid)
	{
		return votedPlayers.contains(playerUuid);
	}
	
	private static int getPlayersNeeded(ServerCommandSource cs)
	{
		// round up (majority percent * total num players)
		return (int) Math.ceil(cfg.getPercentage()*cs.getWorld().getPlayers().size());
	}
	
	public static void resetVoteProcess()
	{
		isVoteProcess = false;
		numPlayersVoted = 0;
		votedPlayers = new HashSet<>();
	}
	
	private static String getVoteInfoText(ServerCommandSource cs)
	{
		return "Currently there are " + numPlayersVoted + 
		" players that have voted. We need " + getPlayersNeeded(cs) + " to change the time.";
	}
	
	// *** TODO 2/27 ***
	// 3 second delay (for spawnpoint => vote)
	// text message clickable (TellRaw command) so you can click /vote
	// TellRaw => onClick event when click text, in JSON format (Bukkit)
	// could also do color/clickable
	// have a timeout for the vote after certain time it's been open
	
	private static void voteThingy(ServerCommandSource cs)
	{
		String playerUuid = "";
		try {
			playerUuid = cs.getPlayer().getUuidAsString();
		} catch (CommandSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		if(isVoteProcess)
        {
        	System.out.println(playerUuid);
        	if(hasVoted(playerUuid)) {
            	cs.sendFeedback(new LiteralText("Already voted!"), false);
        	}
        	else
        	{
        		numPlayersVoted++;
        		votedPlayers.add(playerUuid);
        	}
    		cs.getServer().getPlayerManager().broadcastChatMessage(
    				new LiteralText(getVoteInfoText(cs)), 
    				MessageType.SYSTEM,
    				Util.NIL_UUID);
    		
    		if(numPlayersVoted >= getPlayersNeeded(cs)) {
    			changeToDaytime(cs);
			}
        }
        else 
        {
        	cs.sendFeedback(new LiteralText("The vote process is not active"), true);
        }
	}
	
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
		            voteThingy(context.getSource());
					return 1;
				}
        ));
	}
	
	public static void startVoteProcess(ServerCommandSource cs)
	{
		if(!isVoteProcess)
		{
			isVoteProcess = true;
		}
		voteThingy(cs);
	}
	
	public static void changeToDaytime(ServerCommandSource cs)
	{
		// change only if during night time threshold or there are thunderstorms
		if(cs.getWorld().getTimeOfDay() % DAY_LEN > DAY_LEN/2 || cs.getWorld().isThundering())
		{
			String thunderToConcat = cs.getWorld().isThundering() ? " and the thunder has been cleared" : "";
			cs.getServer().getPlayerManager().broadcastChatMessage(
					new LiteralText("Vote Successful!!! It is now daytime" + thunderToConcat + "."), 
					MessageType.SYSTEM,
					Util.NIL_UUID
			);
	
			// change to a tick value in the future so we don't unintentionally go "backwards in time"
			long sameTimeNextDayTicks = cs.getWorld().getTimeOfDay() + DAY_LEN;
			// this will change the time to 0 by subtracting the offset
			cs.getWorld().setTimeOfDay(sameTimeNextDayTicks - (sameTimeNextDayTicks % DAY_LEN));
			// https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/command/defaults/WeatherCommand.java
	        int randomDuration = (300 + new Random().nextInt(600)) * 20;
			cs.getWorld().setWeather(randomDuration, randomDuration, false, false);
			resetVoteProcess();
		}
		else {
			cs.getServer().getPlayerManager().broadcastChatMessage(
					new LiteralText("The vote percentage has been reached, but we did not change\n"
							+ "to daytime because it's already day or there is no thunder.\n"
							+ "The vote count has been reset to 0."), 
					MessageType.SYSTEM,
					Util.NIL_UUID
			);
			resetVoteProcess();
		}
	}

}
