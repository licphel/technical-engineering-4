package com.hypothetic.ten4.api.item;

import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DeviceItem extends Item {
  public DeviceItem(Properties properties) {
    super(properties);
  }

  public static ItemStack createStorage(AbstractDeviceBlockEntity blockEntity) {
    return ItemStack.EMPTY;//TODO
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
    super.appendHoverText(stack, ctx, list, flag);
  }
}
