package com.hypothetic.ten4.core.client.builtin;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.client.ComponentedContainerScreen;
import com.hypothetic.ten4.api.client.components.Panel;
import com.hypothetic.ten4.api.client.gui.EnhancedGuiGraphics;
import com.hypothetic.ten4.api.client.gui.TextureRegion;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Objects;

import static com.hypothetic.ten4.core.client.builtin.BuiltinComponents.BEEP;

public class IoPanel extends Panel {
  public IoPanel(ComponentedContainerScreen<ContainerMenu> screen, TextureRegion tabBg, int type) {
    super(tabBg, 91, 103, type);

    IoFlagReader state = new IoFlagReader(screen.getMenu().fieldsReader());
    BlockPos pos = screen.getMenu().getBlockEntity().getBlockPos();
    Direction facing = screen.getMenu().getBlockEntity().getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

    addChild(new IoTypeButton(38, 21, 12, 12, state).withClickSound(BEEP));

    addChild(new IoFaceButton(46, 40, Direction.UP, screen, state, "up").withClickSound(BEEP));
    addChild(new IoFaceButton(28, 58, facing.getClockWise(), screen, state, "left").withClickSound(BEEP));
    addChild(new IoFaceButton(46, 58, facing, screen, state, "front").withClickSound(BEEP));
    addChild(new IoFaceButton(64, 58, facing.getCounterClockWise(), screen, state, "right").withClickSound(BEEP));
    addChild(new IoFaceButton(46, 76, Direction.DOWN, screen, state, "down").withClickSound(BEEP));
    addChild(new IoFaceButton(64, 76, facing.getOpposite(), screen, state, "back").withClickSound(BEEP));

    addChild(new IoFlagButton(10, 45, 26, 218, state, pos, 25,
        state::isAutoEject, "auto_eject").withClickSound(BEEP));
    addChild(new IoFlagButton(10, 60, 0, 218, state, pos, 26,
        state::isAutoExtract, "auto_extract").withClickSound(BEEP));
    addChild(new IoFlagButton(10, 75, 0, 204, state, pos, 24,
        state::isStrictInput, "strict_input").withClickSound(BEEP));
  }

  @Override
  protected TextureRegion getIcon() {
    return Objects.requireNonNull(TextureRegion.of(BuiltinComponents.TEXTURE, 130, 185, 11, 11));
  }

  @Override
  protected Component getTitle() {
    return Component.translatable(Ten4.lang("misc.io_label"));
  }

  @Override
  protected void renderBody(EnhancedGuiGraphics g, int px, int py, int pw, int ph) {
    g.draw(TextureRegion.of(BuiltinComponents.TEXTURE, 105, 198, 77, 58), px + 7, py + 37);
  }
}
