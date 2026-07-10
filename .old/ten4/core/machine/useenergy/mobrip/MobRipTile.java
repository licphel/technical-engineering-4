package ten4.core.machine.useenergy.mobrip;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import ten4.lib.tile.extension.CmTileMachineRadiused;
import ten4.lib.tile.mac.IngredientType;
import ten4.lib.wrapper.SlotCm;
import ten4.util.ItemNBTHelper;
import ten4.util.SafeOperationHelper;

import java.util.List;

public class MobRipTile extends CmTileMachineRadiused
{

    public MobRipTile(BlockPos pos, BlockState state)
    {

        super(pos, state);

        info.setCap(kFE(20));
        setEfficiency(15);
        initialRadius = 8;

        addSlot(new SlotCm(this, 0, 79, 31));

    }

    public IngredientType slotType(int slot)
    {
        return IngredientType.INPUT;
    }

    public boolean valid(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof TieredItem || stack.getItem() instanceof SwordItem;
    }

    public IngredientType tankType(int tank)
    {
        return IngredientType.IGNORE;
    }

    public boolean valid(int slot, FluidStack stack)
    {
        return true;
    }

    public int inventorySize()
    {
        return 1;
    }

    public void effect()
    {
        AABB axisalignedbb = (new AABB(worldPosition)).inflate(radius);
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);

        ItemStack st1 = inventory.getItem(0);

        if(list.size() == 0) {
            return;
        }

        LivingEntity entity = SafeOperationHelper.randomInCollection(list);

        if(entity instanceof Player && ((Player) entity).isCreative()) {
            return;
        }

        float damage = 0.5f;
        if(!st1.isEmpty()) {
            if(st1.getItem() instanceof SwordItem sword) {
                damage = sword.getDamage(st1);
            }
            else if(st1.getItem() instanceof TieredItem tiered) {
                damage = tiered.getTier().getAttackDamageBonus();
            }
        }
        entity.hurt(level.damageSources().cactus(), damage);
        ItemNBTHelper.damage(st1, level, 1);
    }

    public double seconds()
    {
        return 3;
    }

}
