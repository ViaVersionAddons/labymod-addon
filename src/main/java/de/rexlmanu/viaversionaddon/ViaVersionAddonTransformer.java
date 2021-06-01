package de.rexlmanu.viaversionaddon;

import net.labymod.addon.AddonTransformer;
import net.labymod.api.TransformerType;

public class ViaVersionAddonTransformer extends AddonTransformer {

  @Override
  public void registerTransformers() {
    this.registerTransformer(TransformerType.VANILLA, "network.mixin.json");
  }
}
