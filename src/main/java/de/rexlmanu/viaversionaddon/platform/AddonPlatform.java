package de.rexlmanu.viaversionaddon.platform;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.UnsupportedSoftware;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import de.rexlmanu.viaversionaddon.ViaVersionAddon;
import de.rexlmanu.viaversionaddon.utility.FutureTaskId;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collection;
import lombok.Getter;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AddonPlatform implements ViaPlatform<UUID> {

    @Getter
    private File dataFolder;
    @Getter
    private AddonApi api;
    private AddonConfig config;

    public AddonPlatform(File dataFolder) {
        this.dataFolder = dataFolder;
        this.api = new AddonApi();
        this.config = new AddonConfig(new File(dataFolder, "viaversion.yml"));
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(ViaVersionAddon.NAME);
    }

    @Override
    public String getPlatformName() {
        return ViaVersionAddon.NAME;
    }

    @Override
    public String getPlatformVersion() {
        return String.valueOf(ViaVersionAddon.SHARED_VERSION);
    }

    @Override
    public String getPluginVersion() {
        return "4.3.2-SNAPSHOT";
    }

    @Override
    public PlatformTask runAsync(Runnable runnable) {
        return new FutureTaskId(CompletableFuture
                .runAsync(runnable, ViaVersionAddon.getInstance().getExecutorService())
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
        return new FutureTaskId(ViaVersionAddon.getInstance().getEventLoop().submit(runnable).addListener(errorLogger()));
    }

    @Override
    public PlatformTask runSync(Runnable runnable, long ticks) {
        return new FutureTaskId(ViaVersionAddon.getInstance().getEventLoop().schedule(() -> runSync(runnable), ticks *
                50, TimeUnit.MILLISECONDS).addListener(errorLogger()));
    }

    @Override
    public PlatformTask runRepeatingSync(Runnable runnable, long ticks) {
        return new FutureTaskId(ViaVersionAddon.getInstance().getEventLoop().scheduleAtFixedRate(() -> runSync(runnable),
                0, ticks * 50, TimeUnit.MILLISECONDS).addListener(errorLogger()));
    }

    @Override
    public ViaCommandSender[] getOnlinePlayers() {
        return new ViaCommandSender[0];
    }

    @Override
    public void sendMessage(UUID uuid, String s) {

    }

    @Override
    public boolean kickPlayer(UUID uuid, String s) {
        return false;
    }

    @Override
    public boolean disconnect(UserConnection connection, String message) {
        return ViaPlatform.super.disconnect(connection, message);
    }

    @Override
    public boolean isPluginEnabled() {
        return true;
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
    public Collection<UnsupportedSoftware> getUnsupportedSoftwareClasses() {
        return ViaPlatform.super.getUnsupportedSoftwareClasses();
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

    @Override
    public boolean isProxy() {
        return false;
    }


}
