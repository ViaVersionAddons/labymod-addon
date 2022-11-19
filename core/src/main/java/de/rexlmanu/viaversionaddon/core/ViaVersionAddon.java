package de.rexlmanu.viaversionaddon.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Singleton;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.rexlmanu.viaversionaddon.core.loader.AddonBackwardsLoader;
import de.rexlmanu.viaversionaddon.core.loader.AddonRewindLoader;
import de.rexlmanu.viaversionaddon.core.loader.AddonViaProviderLoader;
import de.rexlmanu.viaversionaddon.core.platform.AddonViaInjector;
import de.rexlmanu.viaversionaddon.core.platform.AddonViaPlatform;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import lombok.Getter;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonListener;

@Singleton
@AddonListener
@Getter
public class ViaVersionAddon extends LabyAddon<ViaVersionConfiguration> {

  public static final ExecutorService ASYNC_EXECUTOR;
  public static final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
  public static EventLoop EVENT_LOOP;

  static {
    ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat(Constants.NAME + "-%d").build();
    ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, factory);
    try {
      Class.forName("io.netty.channel.DefaultEventLoop");
      EVENT_LOOP = new DefaultEventLoop(factory);
    } catch (ClassNotFoundException e) {
      EVENT_LOOP = new LocalEventLoopGroup(1, factory).next();
    }
    EVENT_LOOP.submit(INIT_FUTURE::join);
  }

  private final int protocolVersion;
  private final File dataFolder;

  public ViaVersionAddon() {
    this.protocolVersion = labyAPI().minecraft().getProtocolVersion();
    this.dataFolder = new File(Constants.NAME.toLowerCase());

    this.dataFolder.mkdirs();
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.logger().info("Enabled the Addon");

    Via.init(ViaManagerImpl.builder()
        .injector(new AddonViaInjector(this))
        .platform(new AddonViaPlatform(this))
        .loader(new AddonViaProviderLoader(this))
        .build());

    MappingDataLoader.enableMappingsCache();
    ((ViaManagerImpl) Via.getManager()).init();

    new AddonBackwardsLoader(new File(this.dataFolder, "backwards"));
    new AddonRewindLoader(new File(this.dataFolder, "viarewind"));

    INIT_FUTURE.complete(null);
  }

  @Override
  protected Class<ViaVersionConfiguration> configurationClass() {
    return ViaVersionConfiguration.class;
  }

  public boolean nativeVersion() {
    return ProtocolVersion.getProtocol(this.protocolVersion) == this.configuration().version().get()
        .lazyProtocolVersion().get();
  }
}
