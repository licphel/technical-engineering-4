package com.hypothetic.ten4.api.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import net.minecraft.world.item.Item;

public class MuteAugment extends Item implements IAugment<AugmentableDeviceBlockEntity> {
  public MuteAugment(Properties properties) {
    super(properties);
  }
}
