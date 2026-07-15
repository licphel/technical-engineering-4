package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.item.IAugment;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.world.item.Item;

import java.util.Set;

public class GeneralAugmentItem extends Item implements IAugment<AugmentableDeviceBlockEntity> {
  private final Set<AugmentableField> fields;
  private final Int2IntFunction modifier;

  public GeneralAugmentItem(Properties properties, Int2IntFunction modifier, AugmentableField... fields) {
    super(properties);
    this.modifier = modifier;
    this.fields = Set.of(fields);
  }

  @Override
  public int modifier(AugmentableField field, int value) {
    if (fields.contains(field)) {
      return modifier.applyAsInt(value);
    }
    return value;
  }
}
