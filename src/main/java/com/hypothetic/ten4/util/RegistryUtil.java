package com.hypothetic.ten4.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup;

public final class RegistryUtil {
  private RegistryUtil() {
  }

  public static HolderLookup.Provider registryAccess() {
    Minecraft minecraft = Minecraft.getInstance();
    ClientLevel level = minecraft.level;
    if (level == null) {
      throw new NullPointerException("Client level must not be null.");
    }
    return level.registryAccess();
  }
}
