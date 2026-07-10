package com.hypothetic.ten4.lib.data;

import com.hypothetic.ten4.lib.block.BuiltinBlockStates;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;

public final class BlockStateDataGen {
  private BlockStateDataGen() {
  }

  public static BlockStateGenerator simple() {
    return (prov, entry, name) -> {
      BlockModelBuilder model = prov.models().cubeAll(name, prov.modLoc("block/" + name));
      prov.simpleBlock(entry.get(), model);
    };
  }

  public static BlockStateGenerator device() {
    return (prov, entry, name) -> {
      BlockModelBuilder normal = prov.models().cube(name,
              prov.modLoc("block/device_bottom"),
              prov.modLoc("block/device_top"),
              prov.modLoc("block/" + name),
              prov.modLoc("block/device_side"),
              prov.modLoc("block/device_side"),
              prov.modLoc("block/device_side"))
          .texture("particle", prov.modLoc("block/device_side"));
      BlockModelBuilder active = prov.models().cube(name + "_active",
              prov.modLoc("block/device_bottom"),
              prov.modLoc("block/device_top"),
              prov.modLoc("block/" + name + "_active"),
              prov.modLoc("block/device_side"),
              prov.modLoc("block/device_side"),
              prov.modLoc("block/device_side"))
          .texture("particle", prov.modLoc("block/device_side"));
      VariantBlockStateBuilder builder = prov.getVariantBuilder(entry.get());
      for (Direction dir : Direction.Plane.HORIZONTAL) {
        int y = switch (dir) {
          case EAST -> 90;
          case SOUTH -> 180;
          case WEST -> 270;
          default -> 0;
        };
        builder.partialState().with(BlockStateProperties.HORIZONTAL_FACING, dir)
            .with(BuiltinBlockStates.ACTIVE, false)
            .modelForState().modelFile(normal).rotationY(y).addModel()
            .partialState().with(BlockStateProperties.HORIZONTAL_FACING, dir)
            .with(BuiltinBlockStates.ACTIVE, true)
            .modelForState().modelFile(active).rotationY(y).addModel();
      }
    };
  }
}
