package de.rexlmanu.viaversionaddon.v1_18.mixins;


import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.rexlmanu.viaversionaddon.core.Constants;
import de.rexlmanu.viaversionaddon.core.ViaVersionAddon;
import de.rexlmanu.viaversionaddon.core.handler.ViaDecodeHandler;
import de.rexlmanu.viaversionaddon.core.handler.ViaEncodeHandler;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.labymod.api.inject.LabyGuice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/Connection$1")
public class MixinConnectionChInit {

  @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
  private void onInitChannel(Channel channel, CallbackInfo ci) {
    if (channel instanceof SocketChannel && !LabyGuice.getInstance(ViaVersionAddon.class)
        .nativeVersion()) {

      UserConnection user = new UserConnectionImpl(channel, true);
      new ProtocolPipelineImpl(user);
      channel.pipeline()
          .addBefore("encoder", Constants.ENCODER_NAME, new ViaEncodeHandler(user))
          .addBefore("decoder", Constants.DECODER_NAME, new ViaDecodeHandler(user));
    }
  }
}
