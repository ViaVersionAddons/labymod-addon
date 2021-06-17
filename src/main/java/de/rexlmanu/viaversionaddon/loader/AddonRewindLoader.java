package de.rexlmanu.viaversionaddon.loader;

import de.gerrygames.viarewind.api.ViaRewindConfigImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.logging.Logger;

@Accessors(fluent = true)
public class AddonRewindLoader implements ViaRewindPlatform {
    public AddonRewindLoader(File file) {
        ViaRewindConfigImpl config = new ViaRewindConfigImpl(new File(file, "config.yml"));
        config.reloadConfig();
        this.init(config);
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("ViaRewind");
    }
}
