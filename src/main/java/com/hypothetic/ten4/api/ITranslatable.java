package com.hypothetic.ten4.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface ITranslatable {
  String createTranslationKey();

  default String createDescriptionKey() {
    return createTranslationKey() + ".desc";
  }

  default MutableComponent createTranslation(Object... args) {
    return Component.translatable(createTranslationKey(), args);
  }

  default MutableComponent createDescription(Object... args) {
    return Component.translatable(createDescriptionKey(), args);
  }
}
