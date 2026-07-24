package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.blockentity.device.AbstractDeviceBlockEntity;
import com.hypothetic.ten4.api.container.ContainerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;
import java.util.function.Supplier;

public class ModMenus {
  public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, Ten4.ID);
  private static final Supplier<MenuType<ContainerMenu>> DEVICE_MENU_GENERATOR = (() -> IMenuTypeExtension.create(
      (id, inv, buf) -> {
        AbstractDeviceBlockEntity be = (AbstractDeviceBlockEntity) inv.player.level().getBlockEntity(buf.readBlockPos());
        assert be != null;
        AbstractContainerMenu menu = be.createMenu(id, inv, inv.player);
        return (ContainerMenu) Objects.requireNonNull(menu);
      }));

  // DeviceTiers
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> PULVERIZER = MENUS.register("pulverizer", DEVICE_MENU_GENERATOR);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> PRESS = MENUS.register("press", DEVICE_MENU_GENERATOR);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> SMELTER = MENUS.register("smelter", DEVICE_MENU_GENERATOR);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> REFINER = MENUS.register("refiner", DEVICE_MENU_GENERATOR);
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> WATER_PUMP = MENUS.register("water_pump", DEVICE_MENU_GENERATOR);

  // Generators
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> HEAT_GENERATOR = MENUS.register("heat_generator", DEVICE_MENU_GENERATOR);

  // Storage
  public static final DeferredHolder<MenuType<?>, MenuType<ContainerMenu>> TANK = MENUS.register("tank", DEVICE_MENU_GENERATOR);
}
