package de.rexlmanu.viaversionaddon.core.loader;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import java.io.File;
import java.util.logging.Logger;

public class AddonBackwardsLoader implements ViaBackwardsPlatform {

  private File file;

  public AddonBackwardsLoader(File file) {
    this.file = file;
    this.file.mkdir();
    this.init(this.file);
  }

  @Override
  public Logger getLogger() {
    return Logger.getLogger("ViaBackwards");
  }

  @Override
  public void disable() {

  }

  @Override
  public boolean isOutdated() {
    return false;
  }

  @Override
  public File getDataFolder() {
    return new File(this.file, "config.yml");
  }
}
