package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.item.IAugment;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.world.item.Item;

public class PowerModifierAugmentItem extends Item implements IAugment<AugmentableDeviceBlockEntity> {
  final Int2IntFunction modifier;

  public PowerModifierAugmentItem(Properties properties, Int2IntFunction modifier) {
    super(properties);
    this.modifier = modifier;
  }

  @Override
  public int modifier(AugmentableField field, int value) {
    if (field == AugmentableField.EFFICIENCY) {
      return modifier.applyAsInt(value);
    }
    return value;
  }
}
