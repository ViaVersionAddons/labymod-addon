package de.rexlmanu.viaversionaddon.core.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.rexlmanu.viaversionaddon.core.Constants;
import de.rexlmanu.viaversionaddon.core.ViaVersionAddon;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddonViaInjector implements ViaInjector {

  private final ViaVersionAddon addon;

  @Override
  public void inject() throws Exception {

  }

  @Override
  public void uninject() throws Exception {

  }

  @Override
  public int getServerProtocolVersion() throws Exception {
    return this.addon.protocolVersion();
  }

  @Override
  public JsonObject getDump() {
    return new JsonObject();
  }

  @Override
  public String getDecoderName() {
    return Constants.DECODER_NAME;
  }

  @Override
  public String getEncoderName() {
    return Constants.ENCODER_NAME;
  }
}
