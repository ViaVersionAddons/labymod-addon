package de.rexlmanu.viaversionaddon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import de.rexlmanu.viaversionaddon.loader.AddonBackwardsLoader;
import de.rexlmanu.viaversionaddon.loader.AddonRewindLoader;
import de.rexlmanu.viaversionaddon.loader.AddonViaProviderLoader;
import de.rexlmanu.viaversionaddon.menu.ProtocolScreen;
import de.rexlmanu.viaversionaddon.platform.AddonInjector;
import de.rexlmanu.viaversionaddon.platform.AddonPlatform;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.Tabs;
import net.labymod.settings.elements.SettingsElement;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Getter
public class ViaVersionAddon extends LabyModAddon {

    public static final String NAME = "ViaVersionAddon";

    public static final int SHARED_VERSION = 754;

    @Getter
    private static ViaVersionAddon instance;

    private final CompletableFuture<Void> initFuture = new CompletableFuture<>();

    private ExecutorService executorService;
    private EventLoop eventLoop;

    private File dataFolder;

    @Getter
    @Setter
    private int version;

    public ViaVersionAddon() {
        ViaVersionAddon.instance = this;

        ThreadFactory factory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("ViaVersionAddon-%d")
                .build();
        this.executorService = Executors.newFixedThreadPool(
                8,
                factory

        );

        this.eventLoop = new DefaultEventLoop(factory).next();
        this.eventLoop.submit(initFuture::join);

        this.dataFolder = new File(NAME.toLowerCase());

        this.version = SHARED_VERSION;

        this.dataFolder.mkdir();
    }

    @Override
    public void onEnable() {
        Via.init(
                ViaManagerImpl.builder()
                        .injector(new AddonInjector())
                        .platform(new AddonPlatform(this.dataFolder))
                        .loader(new AddonViaProviderLoader())
                        .build()
        );

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();


        new AddonBackwardsLoader(new File(this.dataFolder, "backwards"));
        new AddonRewindLoader(new File(this.dataFolder, "viarewind"));

        this.initFuture.complete(null);

        Tabs.registerTab("Protocol", ProtocolScreen.class);
    }

    @Override
    public void loadConfig() {

    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
    }
}
