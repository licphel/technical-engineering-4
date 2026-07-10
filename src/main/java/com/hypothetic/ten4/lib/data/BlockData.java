package com.hypothetic.ten4.lib.data;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockData {
    final DeferredHolder<Block, ? extends Block> entry;
    String enName, zhName;
    BlockStateGenerator blockState;
    String modelName;
    boolean autoItemModel;

    BlockData(DeferredHolder<Block, ? extends Block> e) { this.entry = e; }

    public BlockData enName(String s) { enName = s; return this; }
    public BlockData zhName(String s) { zhName = s; return this; }
    public BlockData modelName(String s) { modelName = s; return this; }
    public BlockData blockState(BlockStateGenerator s) { blockState = s; return this; }
    public BlockData autoItemModel() { autoItemModel = true; return this; }
}
