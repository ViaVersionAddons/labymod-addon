package de.rexlmanu.viaversionaddon.mixin;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.rexlmanu.viaversionaddon.ViaVersionAddon;
import de.rexlmanu.viaversionaddon.handler.ViaDecodeHandler;
import de.rexlmanu.viaversionaddon.handler.ViaEncodeHandler;
import de.rexlmanu.viaversionaddon.handler.CommonTransformer;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/NetworkManager$1")
public class MixinNetworkManagerInCh {

    @Inject(method = "initChannel", at = @At(value = "TAIL"), remap = false)
    private void onInitChannel(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel && ViaVersionAddon.SHARED_VERSION != ViaVersionAddon.getInstance().getVersion()) {
            System.out.println("its working");

            UserConnection user = new UserConnectionImpl(channel, true);
            new ProtocolPipelineImpl(user);

            channel.pipeline()
                    .addBefore("encoder", CommonTransformer.HANDLER_ENCODER_NAME, new ViaEncodeHandler(user))
                    .addBefore("decoder", CommonTransformer.HANDLER_DECODER_NAME, new ViaDecodeHandler(user));
        }
    }

}
