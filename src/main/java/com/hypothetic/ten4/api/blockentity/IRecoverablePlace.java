package com.hypothetic.ten4.api.blockentity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IRecoverablePlace {
  void onPlaced(@Nullable LivingEntity entity, ItemStack stack);
}
