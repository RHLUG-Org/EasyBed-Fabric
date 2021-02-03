package net.fabricmc.example.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.fabricmc.example.PlayerEventHandler;
import net.fabricmc.example.SleepEventHandler;
import net.fabricmc.example.WakeEventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	SleepEventHandler se = new SleepEventHandler();
	WakeEventHandler we = new WakeEventHandler();

//	@Inject(at = @At("HEAD"), method = "init()V")
//	private void init(CallbackInfo info) {
//		System.out.println("SPE MIXIN This line is printed by an example mod mixin!");
//	}
	
	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
		System.out.println("SPE MIXIN This line is printed by an example mod mixin!");
	}

	@Inject(method = "sleep", at = @At("TAIL"))
    public void onSleep(BlockPos pos, CallbackInfo ci) {
        se.onEvent(this);
    }

    @Inject(method = "wakeUp", at = @At("RETURN"))
    private void onWakeUp(boolean b1, boolean b2, CallbackInfo info) {
        we.onEvent(this);
    }
	
}
