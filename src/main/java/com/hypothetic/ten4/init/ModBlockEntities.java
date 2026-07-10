package com.hypothetic.ten4.init;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.device.HeatGeneratorBlockEntity;
import com.hypothetic.ten4.device.PulverizerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("all")
public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> TILES =
      DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, Ten4.ID);

  public static void trigger() {
    // just trigger the class loader
  }

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PulverizerBlockEntity>> PULVERIZER =
      TILES.register("pulverizer", () ->
          BlockEntityType.Builder.of(PulverizerBlockEntity::new, ModBlocks.PULVERIZER.get()).build(null));

  public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatGeneratorBlockEntity>> HEAT_GENERATOR =
      TILES.register("heat_generator", () ->
          BlockEntityType.Builder.of(HeatGeneratorBlockEntity::new, ModBlocks.HEAT_GENERATOR.get()).build(null));
}
