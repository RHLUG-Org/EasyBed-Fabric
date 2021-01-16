package net.fabricmc.example;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public final class VoteCommand implements Command<Object> {

	@Override
	public int run(CommandContext<Object> context) throws CommandSyntaxException {
		System.out.println("Called foo with no arguments");
		return 1;
	}

}
