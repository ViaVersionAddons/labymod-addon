package launch;

import com.google.common.base.Strings;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.launchwrapper.Launch;

public class AddonLauncher {

  public static void main(String[] args) {
    Map<String, String> arguments = new HashMap();
    hackNatives();
    arguments.put("version", "LabyMod-Addon-Environment");
    arguments.put("assetIndex", System.getenv("assetIndex"));
    arguments.put("assetsDir", System.getenv().getOrDefault("assetDirectory", "assets"));
    arguments.put("accessToken", "LabyMod");
    arguments.put("userProperties", "{}");
    arguments.put("tweak", System.getenv("tweakClass"));
    List<String> argumentList = new ArrayList();
    arguments.forEach((k, v) -> {
      argumentList.add("--" + k);
      argumentList.add(v);
    });
    Launch.main(argumentList.toArray(new String[0]));
  }

  /**
   * The method already describes itself. This is a hacky way to load the Minecraft natives.
   */
  private static void hackNatives() {
    String paths = System.getProperty("java.library.path");
    String nativesDir = System.getenv().get("nativesDirectory");
    paths = Strings.isNullOrEmpty(paths) ? nativesDir : paths + File.pathSeparator + nativesDir;

    System.setProperty("java.library.path", paths);

    try {
      Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
      sysPathsField.setAccessible(true);
      sysPathsField.set(null, null);
    } catch (Throwable ignored) {
    }

  }
}
