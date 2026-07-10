package ten4.core.machine.useenergy.beacon;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import ten4.core.item.upgrades.LevelupPotion;
import ten4.lib.tile.extension.CmTileMachineRadiused;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.tile.option.Type;
import ten4.lib.wrapper.SlotCm;

import java.util.List;

public class BeaconTile extends CmTileMachineRadiused
{

    public BeaconTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

        info.setCap(kFE(20));
        setEfficiency(300);
        initialRadius = 32;

        addSlot(new SlotCm(this, 0, 79, 31));

    }

    public int inventorySize()
    {
        return 1;
    }

    @Override
    public Type typeOf()
    {
        return Type.MACHINE_EFFECT;
    }

    public IngredientType slotType(int slot)
    {
        return IngredientType.INPUT;
    }

    public boolean valid(int slot, ItemStack stack)
    {
        return true;
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return true;
    }

    public void effect()
    {
        AABB axisalignedbb = (new AABB(worldPosition)).inflate(radius).expandTowards(0, level.getHeight(), 0);
        List<Player> list = level.getEntitiesOfClass(Player.class, axisalignedbb);

        ItemStack its = inventory.getItem(0);
        PotionContents pt = its.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        if(its.isEmpty() || pt == PotionContents.EMPTY) {
            return;
        }

      Iterable<MobEffectInstance> effects = pt.getAllEffects();
        for(Player Player : list) {
            effects.forEach(effect -> {
                Player.addEffect(new MobEffectInstance(effect.getEffect(), 40 * 10, match(), true, true));
            });
        }
    }

    public boolean conditionStart()
    {
        return !inventory.getItem(0).isEmpty();
    }

    public double seconds()
    {
        return 10;
    }

    private int match()
    {
        if(upgradeSlots.countUpgrade(LevelupPotion.class) > 0) {
            return 1;
        }
        return 0;
    }

}
