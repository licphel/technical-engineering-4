package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSoundEvents {
  public static final DeferredRegister<SoundEvent> SES = DeferredRegister.create(Registries.SOUND_EVENT, Ten4.ID);

  public static final DeferredHolder<SoundEvent, SoundEvent> BEEP = register("beep");
  public static final DeferredHolder<SoundEvent, SoundEvent> DEVICE_NOISE_0 = register("device_noise_0");
  public static final DeferredHolder<SoundEvent, SoundEvent> DEVICE_NOISE_1 = register("device_noise_1");
  public static final DeferredHolder<SoundEvent, SoundEvent> DEVICE_NOISE_2 = register("device_noise_2");

  private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
    return SES.register(name, () -> SoundEvent.createVariableRangeEvent(Ten4.id(name)));
  }
}
