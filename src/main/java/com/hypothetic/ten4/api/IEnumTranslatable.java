package com.hypothetic.ten4.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;

public interface IEnumTranslatable extends ITranslatable, StringRepresentable {
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

  String createGroupKey();

  default MutableComponent createGroup(Object... args) {
    return Component.translatable(createGroupKey(), args);
  }
}
