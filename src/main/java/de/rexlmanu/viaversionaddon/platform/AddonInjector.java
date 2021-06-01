package de.rexlmanu.viaversionaddon.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.rexlmanu.viaversionaddon.ViaVersionAddon;
import de.rexlmanu.viaversionaddon.handler.CommonTransformer;

public class AddonInjector implements ViaInjector {
    @Override
    public void inject() throws Exception {

    }

    @Override
    public void uninject() throws Exception {

    }

    @Override
    public int getServerProtocolVersion() throws Exception {
        return ViaVersionAddon.SHARED_VERSION;
    }

    @Override
    public String getEncoderName() {
        return CommonTransformer.HANDLER_ENCODER_NAME;
    }

    @Override
    public String getDecoderName() {
        return CommonTransformer.HANDLER_DECODER_NAME;
    }

    @Override
    public JsonObject getDump() {
        return new JsonObject();
    }
}
