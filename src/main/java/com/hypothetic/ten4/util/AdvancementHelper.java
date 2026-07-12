package com.hypothetic.ten4.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class AdvancementHelper {
  private AdvancementHelper() {
  }

  public static void giveAdvancement(ResourceLocation rl, Player player) {
    if (!(player instanceof ServerPlayer serverPlayer)) {
      return;
    }

    AdvancementHolder adv = serverPlayer.server.getAdvancements().get(rl);
    if (adv == null) {
      return;
    }

    AdvancementProgress ap = serverPlayer.getAdvancements().getOrStartProgress(adv);
    if (!ap.isDone()) {
      for (String criterion : ap.getCompletedCriteria()) {
        serverPlayer.getAdvancements().award(adv, criterion);
      }
    }
  }
}
