package de.rexlmanu.viaversionaddon.core.version;

import com.google.common.base.Supplier;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.rexlmanu.viaversionaddon.core.ViaVersionAddon;
import lombok.Getter;
import net.labymod.api.inject.LabyGuice;

public enum Version {
  V1_19_2(ProtocolVersion.v1_19_1),
  V1_19(ProtocolVersion.v1_19),
  V1_18_2(ProtocolVersion.v1_18_2),
  V1_18(ProtocolVersion.v1_18),
  V1_17_1(ProtocolVersion.v1_17_1),
  V1_17(ProtocolVersion.v1_17),
  V1_16_5(ProtocolVersion.v1_16_4),
  V1_16_3(ProtocolVersion.v1_16_3),
  V1_16_2(ProtocolVersion.v1_16_2),
  V1_16_1(ProtocolVersion.v1_16_1),
  V1_16(ProtocolVersion.v1_16),
  V1_15_2(ProtocolVersion.v1_15_2),
  V1_15_1(ProtocolVersion.v1_15_1),
  V1_15(ProtocolVersion.v1_15),
  V1_14_4(ProtocolVersion.v1_14_4),
  V1_14_3(ProtocolVersion.v1_14_3),
  V1_14_2(ProtocolVersion.v1_14_2),
  V1_14_1(ProtocolVersion.v1_14_1),
  V1_14(ProtocolVersion.v1_14),
  V1_13_2(ProtocolVersion.v1_13_2),
  V1_13_1(ProtocolVersion.v1_13_1),
  V1_13(ProtocolVersion.v1_13),
  V1_12_2(ProtocolVersion.v1_12_2),
  V1_12_1(ProtocolVersion.v1_12_1),
  V1_12(ProtocolVersion.v1_12),
  V1_11_1(ProtocolVersion.v1_11_1),
  V1_11(ProtocolVersion.v1_11),
  V1_10(ProtocolVersion.v1_10),
  V1_9_4(ProtocolVersion.v1_9_3),
  V1_9_2(ProtocolVersion.v1_9_2),
  V1_9_1(ProtocolVersion.v1_9_1),
  V1_9(ProtocolVersion.v1_9),
  V1_8(ProtocolVersion.v1_8),
  V1_7_10(ProtocolVersion.v1_7_6),
  V1_6_4(ProtocolVersion.v_1_6_4),
  V1_5_2(ProtocolVersion.v1_5_2),
  V1_4_7(ProtocolVersion.v1_4_6),
  UNDEFINED(() -> ProtocolVersion.getProtocol(
      LabyGuice.getInstance(ViaVersionAddon.class).protocolVersion()));

  private ProtocolVersion protocolVersion;
  @Getter
  private Supplier<ProtocolVersion> lazyProtocolVersion;

  Version(ProtocolVersion protocolVersion) {
    this.protocolVersion = protocolVersion;
    this.lazyProtocolVersion = () -> protocolVersion;
  }

  Version(Supplier<ProtocolVersion> lazyProtocolVersion) {
    this.lazyProtocolVersion = lazyProtocolVersion;
  }

  public static Version fromProtocolVersion(ProtocolVersion protocol) {
    for (Version version : values()) {
      if (version.lazyProtocolVersion().get().equals(protocol)) {
        return version;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    if (this == UNDEFINED) {
      return "Native";
    }
    return this.protocolVersion.getName();
  }
}
