package com.hypothetic.ten4.lib.data;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

@FunctionalInterface
public interface BlockStateGenerator {
  void generate(BlockStateProvider prov, DeferredHolder<Block, ? extends Block> entry, String modelName);
}
