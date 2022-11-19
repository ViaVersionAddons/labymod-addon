package de.rexlmanu.viaversionaddon.core.handler;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.exception.CancelCodecException;
import com.viaversion.viaversion.exception.CancelDecoderException;
import com.viaversion.viaversion.util.PipelineUtil;
import de.rexlmanu.viaversionaddon.core.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

@ChannelHandler.Sharable
public class ViaDecodeHandler extends MessageToMessageDecoder<ByteBuf> {

  private final UserConnection info;
  private boolean handledCompression;
  private boolean skipDoubleTransform;

  public ViaDecodeHandler(UserConnection info) {
    this.info = info;
  }

  public UserConnection getInfo() {
    return info;
  }

  // https://github.com/ViaVersion/ViaVersion/blob/master/velocity/src/main/java/us/myles/ViaVersion/velocity/handlers/VelocityDecodeHandler.java
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf bytebuf, List<Object> out)
      throws Exception {
    if (!info.checkIncomingPacket()) {
      throw CancelDecoderException.generate(null);
    }
    if (!info.shouldTransformPacket()) {
      out.add(bytebuf.retain());
      return;
    }

    ByteBuf transformedBuf = ctx.alloc().buffer().writeBytes(bytebuf);
    try {
      info.transformIncoming(transformedBuf, CancelDecoderException::generate);

      out.add(transformedBuf.retain());
    } finally {
      transformedBuf.release();
    }
  }

  private void reorder(ChannelHandlerContext ctx) {
    int decoderIndex = ctx.pipeline().names().indexOf("decompress");
    if (decoderIndex == -1) {
      return;
    }
    if (decoderIndex > ctx.pipeline().names().indexOf(Constants.DECODER_NAME)) {
      ChannelHandler encoder = ctx.pipeline().get(Constants.ENCODER_NAME);
      ChannelHandler decoder = ctx.pipeline().get(Constants.DECODER_NAME);
      ctx.pipeline().remove(encoder);
      ctx.pipeline().remove(decoder);
      ctx.pipeline().addAfter("compress", Constants.ENCODER_NAME, encoder);
      ctx.pipeline().addAfter("decompress", Constants.DECODER_NAME, decoder);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (PipelineUtil.containsCause(cause, CancelCodecException.class)) {
      return;
    }
    super.exceptionCaught(ctx, cause);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof PipelineReorderEvent) {
      reorder(ctx);
    }
    super.userEventTriggered(ctx, evt);
  }

}