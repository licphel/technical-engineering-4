package ten4.core.machine.pole;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import ten4.lib.tile.mac.CmTileMachine;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.tile.mac.TransferManager;
import ten4.lib.tile.option.Type;

public class PoleTile extends CmTileMachine
{

    public BlockPos bind;

    public PoleTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

    }

    public int inventorySize()
    {
        return 0;
    }

    @Override
    public void readTileData(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.readTileData(nbt, registries);
        if(nbt.contains("bind")) {
            bind = BlockPos.of(nbt.getLong("bind"));
        }
    }

    @Override
    public void writeTileData(CompoundTag nbt, HolderLookup.Provider registries)
    {
        super.writeTileData(nbt, registries);
        if(bind != null) {
            nbt.putLong("bind", bind.asLong());
        }
    }

    @Override
    public Type typeOf()
    {
        return Type.NON_MAC;
    }

    public IngredientType slotType(int slot)
    {
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, ItemStack stack)
    {
        return false;
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return false;
    }

    @Override
    public IEnergyStorage getEnergyHandler(Direction d)
    {
        if(d != Direction.DOWN) {
            return null;
        }
        if(bind == null) {
            return null;
        }
        BlockEntity be = level.getBlockEntity(bind.below());
        if(be == null) {
            return null;
        }
        return level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, bind.below(), Direction.UP);
    }

    @Override
    public IItemHandler getItemHandler(Direction d)
    {
        return null;
    }

    @Override
    public void update()
    {
    }

    @Override
    protected boolean hasFaceCapability(int cap, Direction d)
    {
        if(cap != TransferManager.CAP_ENERGY) {
            return false;
        }
        return super.hasFaceCapability(cap, d);
    }

}
