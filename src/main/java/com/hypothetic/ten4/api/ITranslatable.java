package com.hypothetic.ten4.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public interface ITranslatable extends StringRepresentable {
  @Override
  default String getSerializedName() {
    if (this instanceof Enum<?> e) {
      return e.name().toLowerCase();
    }
    return toString();
  }

  default String createTranslationKey() {
    return createGroupKey() + "." + getSerializedName();
  }

  default String createDescriptionKey() {
    return createTranslationKey() + ".desc";
  }

  default String createGroupKey() {
    return "";
  }

  default MutableComponent createTranslation(Object... args) {
    return Component.translatable(createTranslationKey(), args);
  }

  default MutableComponent createDescription(Object... args) {
    return Component.translatable(createDescriptionKey(), args);
  }

  default MutableComponent createGroup(Object... args) {
    return Component.translatable(createGroupKey(), args);
  }
}
