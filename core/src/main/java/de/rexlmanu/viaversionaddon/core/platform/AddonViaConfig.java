package de.rexlmanu.viaversionaddon.core.platform;

import com.viaversion.viaversion.api.protocol.version.BlockedProtocolVersions;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.protocol.BlockedProtocolVersionsImpl;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddonViaConfig extends AbstractViaConfig {

  // Based on Sponge ViaVersion
  private static List<String> UNSUPPORTED = Arrays.asList("anti-xray-patch", "bungee-ping-interval",
      "bungee-ping-save", "bungee-servers", "quick-move-action-fix", "nms-player-ticking",
      "velocity-ping-interval", "velocity-ping-save", "velocity-servers",
      "blockconnection-method", "change-1_9-hitbox", "change-1_14-hitbox");

  protected AddonViaConfig(File configFile) {
    super(configFile);
  }

  @Override
  public URL getDefaultConfigURL() {
    return getClass().getClassLoader().getResource("assets/viaversion/config.yml");
  }

  @Override
  protected void handleConfig(Map<String, Object> config) {

  }

  @Override
  public List<String> getUnsupportedOptions() {
    return UNSUPPORTED;
  }

  @Override
  public boolean isCheckForUpdates() {
    return false;
  }

  @Override
  public BlockedProtocolVersions blockedProtocolVersions() {
    return new BlockedProtocolVersionsImpl(new IntOpenHashSet(), -1, -1);
  }

  @Override
  public boolean isAntiXRay() {
    return false;
  }

  @Override
  public boolean isNMSPlayerTicking() {
    return false;
  }

  @Override
  public boolean is1_12QuickMoveActionFix() {
    return false;
  }

  @Override
  public String getBlockConnectionMethod() {
    return "packet";
  }

  @Override
  public boolean is1_9HitboxFix() {
    return false;
  }

  @Override
  public boolean is1_14HitboxFix() {
    return false;
  }
}
