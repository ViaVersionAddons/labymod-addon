package de.rexlmanu.viaversionaddon.core.platform;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.rexlmanu.viaversionaddon.core.Constants;
import de.rexlmanu.viaversionaddon.core.ViaVersionAddon;
import de.rexlmanu.viaversionaddon.core.utility.FutureTaskId;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AddonViaPlatform implements ViaPlatform<UUID> {

  private final ViaVersionAddon addon;
  private final AddonViaAPI api;
  private final AddonViaConfig config;

  public AddonViaPlatform(ViaVersionAddon addon) {
    this.addon = addon;
    this.api = new AddonViaAPI();
    this.config = new AddonViaConfig(new File(addon.dataFolder(), "config.yml"));
  }

  @Override
  public Logger getLogger() {
    return Logger.getLogger(Constants.NAME);
  }

  @Override
  public String getPlatformName() {
    return Constants.NAME;
  }

  @Override
  public String getPlatformVersion() {
    return String.valueOf(this.addon.protocolVersion());
  }

  @Override
  public String getPluginVersion() {
    return "4.3.2-SNAPSHOT";
  }

  @Override
  public PlatformTask runAsync(Runnable runnable) {
    return new FutureTaskId(CompletableFuture
        .runAsync(runnable, ViaVersionAddon.ASYNC_EXECUTOR)
        .exceptionally(throwable -> {
          if (!(throwable instanceof CancellationException)) {
            throwable.printStackTrace();
          }
          return null;
        })
    );
  }

  @Override
  public PlatformTask runSync(Runnable runnable) {
    return new FutureTaskId(ViaVersionAddon.EVENT_LOOP.submit(runnable).addListener(errorLogger()));
  }

  @Override
  public PlatformTask runSync(Runnable runnable, long ticks) {
    return new FutureTaskId(ViaVersionAddon.EVENT_LOOP.schedule(() -> runSync(runnable), ticks *
        50, TimeUnit.MILLISECONDS).addListener(errorLogger()));
  }

  @Override
  public PlatformTask runRepeatingSync(Runnable runnable, long ticks) {
    return new FutureTaskId(ViaVersionAddon.EVENT_LOOP.scheduleAtFixedRate(() -> runSync(runnable),
        0, ticks * 50, TimeUnit.MILLISECONDS).addListener(errorLogger()));

  }

  @Override
  public ViaCommandSender[] getOnlinePlayers() {
    return new ViaCommandSender[0];
  }

  @Override
  public void sendMessage(UUID uuid, String message) {

  }

  @Override
  public boolean kickPlayer(UUID uuid, String message) {
    return false;
  }

  @Override
  public boolean isPluginEnabled() {
    return true;
  }

  @Override
  public ViaAPI<UUID> getApi() {
    return this.api;
  }

  @Override
  public ViaVersionConfig getConf() {
    return this.config;
  }

  @Override
  public ConfigurationProvider getConfigurationProvider() {
    return this.config;
  }

  @Override
  public File getDataFolder() {
    return this.addon.dataFolder();
  }

  @Override
  public void onReload() {

  }

  @Override
  public JsonObject getDump() {
    return new JsonObject();
  }

  @Override
  public boolean isOldClientsAllowed() {
    return true;
  }

  @Override
  public boolean hasPlugin(String name) {
    return false;
  }

  private <T extends Future<?>> GenericFutureListener<T> errorLogger() {
    return future -> {
      if (!future.isCancelled() && future.cause() != null) {
        future.cause().printStackTrace();
      }
    };
  }
}
