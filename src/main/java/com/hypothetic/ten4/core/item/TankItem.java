package com.hypothetic.ten4.core.item;

import com.hypothetic.ten4.core.client.renderer.TankItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TankItem extends BlockItem {

    public TankItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return TankItemRenderer.INSTANCE;
            }
        });
    }

    public static void setStoredFluid(ItemStack stack, FluidStack fluid, HolderLookup.Provider provider) {
        CompoundTag beTag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tankTag = new CompoundTag();
        if (!fluid.isEmpty()) {
            tankTag.put("Fluid", fluid.saveOptional(provider));
        }
        beTag.put("FluidTank", tankTag);
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
    }

    @Nullable
    public static FluidStack getStoredFluid(ItemStack stack, HolderLookup.Provider provider) {
        CustomData cd = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (cd.isEmpty()) return null;
        CompoundTag beTag = cd.copyTag();
        if (!beTag.contains("FluidTank")) return null;
        CompoundTag tankTag = beTag.getCompound("FluidTank");
        if (tankTag.contains("Fluid")) {
            return FluidStack.parseOptional(provider, tankTag.getCompound("Fluid"));
        }
        return null;
    }
}
