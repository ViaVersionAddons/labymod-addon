package de.rexlmanu.viaversionaddon.v1_8.mixins;

import de.rexlmanu.viaversionaddon.core.handler.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.NetworkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

  @Shadow
  private Channel channel;

  @Inject(method = "setCompressionTreshold", at = @At("RETURN"))
  private void reorderCompression(int compressionThreshold, CallbackInfo ci) {
    channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
  }
}
