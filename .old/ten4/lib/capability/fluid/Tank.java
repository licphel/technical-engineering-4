package ten4.lib.capability.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class Tank extends FluidTank
{

    public Tank(int capacity)
    {
        super(capacity);
    }

    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return super.isFluidValid(tank, stack)
                && getFluid().getFluid() == stack.getFluid();
    }

    public Tank copy()
    {
        Tank t = new Tank(capacity);
        t.setFluid(getFluid());
        return t;
    }

}
