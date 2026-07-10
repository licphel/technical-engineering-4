package ten4.lib.tile.mac;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import ten4.TConst;
import ten4.init.BlockInit;
import ten4.init.ContInit;
import ten4.init.TileInit;
import ten4.lib.wrapper.IntArrayCm;
import ten4.util.ComponentHelper;
import ten4.util.SafeOperationHelper;

public abstract class CmTileEntity extends BlockEntity implements MenuProvider
{

    public static CmTileEntity ofType(BlockEntityType<?> type, BlockPos... pos)
    {
        return (CmTileEntity) type.create(
                pos.length > 0 ? pos[0] : BlockPos.ZERO,
                BlockInit.getBlock(SafeOperationHelper.regNameOf(type)).defaultBlockState()
        );
    }

    public IntArrayCm data = ContInit.createDefaultIntArr();
    public Component component;
    public String id;

    boolean init;

    public CmTileEntity(String key, BlockPos pos, BlockState state)
    {
        super(TileInit.getType(key), pos, state);
        component = ComponentHelper.translated(TConst.modid + "." + key);
        id = key;
    }

    public void readTileData(CompoundTag nbt, HolderLookup.Provider registries)
    {
    }

    public void writeTileData(CompoundTag nbt, HolderLookup.Provider registries)
    {
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider registries)
    {
        readTileData(nbt, registries);
        super.loadAdditional(nbt, registries);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compound, HolderLookup.@NotNull Provider registries)
    {
        writeTileData(compound, registries);
        super.saveAdditional(compound, registries);
    }

    boolean loaded;

    @Override
    public void onLoad()
    {
        super.onLoad();
        loaded = true;
    }

    protected int globalTimer = 0;

    public int getTileAliveTime()
    {
        return globalTimer;
    }

    public void serverTick()
    {

        if(level == null) {
            return;
        }

        if(!level.isClientSide()) {
            globalTimer++;
            if(!init) {
                init = true;
                whenPlaceToWorld();
            }
            update();
            endTick();
        }

        updateRemote();

    }

    public void endTick()
    {
    }

    public void whenPlaceToWorld()
    {
    }

    public void updateRemote()
    {
    }

    public void update()
    {
    }

    public @NotNull Component getDisplayName()
    {
        return component;
    }

}
