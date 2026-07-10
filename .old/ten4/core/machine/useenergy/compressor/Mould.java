package ten4.core.machine.useenergy.compressor;

import ten4.init.TabInit;
import ten4.init.template.DefItem;

public class Mould extends DefItem
{

    public Mould()
    {
        super(build(1));
    }

    public void fillGroup()
    {
        TabInit.TOOLS.add(this::getDefaultInstance);
    }
}
