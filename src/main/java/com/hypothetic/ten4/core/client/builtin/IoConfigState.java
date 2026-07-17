package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.FaceMode;
import com.hypothetic.ten4.api.blockentity.device.FaceModePacker;
import com.hypothetic.ten4.api.container.sync.BuiltinSyncedFields;
import com.hypothetic.ten4.api.container.sync.SyncedFieldReader;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

class IoConfigState {
  final SyncedFieldReader reader;
  int type; // 0=energy, 1=item, 2=fluid
  Direction activeDir = Direction.NORTH;

  IoConfigState(SyncedFieldReader reader) {
    this.reader = reader;
  }

  FaceMode get(Direction d) {
    int packed = packedFor(type);
    return FaceModePacker.get(packed, d);
  }

  int packedFor(int type) {
    return switch (type) {
      case 0 -> reader.getInt(BuiltinSyncedFields.ENERGY_FACES);
      case 1 -> reader.getInt(BuiltinSyncedFields.ITEM_FACES);
      default -> reader.getInt(BuiltinSyncedFields.FLUID_FACES);
    };
  }

  int autoFlags() {
    return switch (type) {
      case 0 -> reader.getInt(BuiltinSyncedFields.ENERGY_AUTO_FLAGS);
      case 1 -> reader.getInt(BuiltinSyncedFields.ITEM_AUTO_FLAGS);
      default -> reader.getInt(BuiltinSyncedFields.FLUID_AUTO_FLAGS);
    };
  }

  boolean isAutoEject() { return (autoFlags() & (1 << 25)) != 0; }
  boolean isAutoExtract() { return (autoFlags() & (1 << 26)) != 0; }

  boolean isStrictInput() {
    return type != 0 && (autoFlags() & (1 << 24)) != 0;
  }

  public Component getComponent() {
    return switch (type) {
      case 0 -> Component.translatable(Ten4.lang("misc.energy"));
      case 1 -> Component.translatable(Ten4.lang("misc.item"));
      case 2 -> Component.translatable(Ten4.lang("misc.fluid"));
      default -> Component.empty();
    };
  }
}
