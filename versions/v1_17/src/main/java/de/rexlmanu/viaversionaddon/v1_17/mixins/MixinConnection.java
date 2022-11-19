package de.rexlmanu.viaversionaddon.v1_17.mixins;


import de.rexlmanu.viaversionaddon.core.handler.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class MixinConnection {

  @Shadow
  private Channel channel;

  @Inject(method = "setupCompression", at = @At("RETURN"))
  private void reorderCompression(int $$0, boolean $$1, CallbackInfo ci) {
    channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
  }
}
