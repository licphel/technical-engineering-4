package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.api.blockentity.device.AugmentableDeviceBlockEntity;
import com.hypothetic.ten4.api.capability.item.ItemInventory;
import com.hypothetic.ten4.api.capability.item.ItemSlot;
import com.hypothetic.ten4.api.capability.item.SlotOption;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Objects;

public class DeviceItem extends BlockItem {
  public DeviceItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> lst, TooltipFlag f) {
    super.appendHoverText(stack, ctx, lst, f);

    if (ctx.registries() == null) {
      return;
    }

    CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    if (!tag.isEmpty()) {
      ItemInventory augments = new ItemInventory();
      for (int i = 0; i < AugmentableDeviceBlockEntity.AUGMENT_CAPACITY; i++) {
        augments.add(new ItemSlot(SlotOption.BOTH));
      }
      augments.fromTag(tag, Objects.requireNonNull(ctx.registries()));

      for (int i = 0; i < augments.size(); i++) {
        if (augments.getItem(i).isEmpty()) {
          continue;
        }
        lst.add(augments.getItem(i).getDisplayName().copy().withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
      }
    }
  }
}
