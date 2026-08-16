package com.hypothetic.ten4.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;

public class LiquidUnitBlock extends DeviceBlock {
    public static final MapCodec<LiquidUnitBlock> CODEC = simpleCodec(LiquidUnitBlock::new);

    public LiquidUnitBlock(Properties p) {
        super(p);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}