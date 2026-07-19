package com.hypothetic.ten4.core.registry.config;

import com.hypothetic.ten4.Ten4;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = Ten4.ID)
public final class ModConfigs {
  public static final CfgCommon COMMON;
  public static final CfgClient CLIENT;
  public static final CfgServer SERVER;
  public static final ModConfigSpec COMMON_SPEC;
  public static final ModConfigSpec CLIENT_SPEC;
  public static final ModConfigSpec SERVER_SPEC;
  private static final List<Runnable> HOOKS = new ArrayList<>();

  static {
    ModConfigSpec.Builder commonB = new ModConfigSpec.Builder();
    commonB.push(Ten4.ID);
    COMMON = new CfgCommon(commonB);
    commonB.pop();
    COMMON_SPEC = commonB.build();

    ModConfigSpec.Builder clientB = new ModConfigSpec.Builder();
    clientB.push(Ten4.ID);
    CLIENT = new CfgClient(clientB);
    clientB.pop();
    CLIENT_SPEC = clientB.build();

    ModConfigSpec.Builder serverB = new ModConfigSpec.Builder();
    serverB.push(Ten4.ID);
    SERVER = new CfgServer(serverB);
    serverB.pop();
    SERVER_SPEC = serverB.build();
  }

  public static void hook(Runnable r) { HOOKS.add(r); }

  @SubscribeEvent
  static void onLoad(ModConfigEvent.Loading e) {
    IConfigSpec spec = e.getConfig().getSpec();
    if (spec == COMMON_SPEC || spec == CLIENT_SPEC || spec == SERVER_SPEC) HOOKS.forEach(Runnable::run);
  }

  @SubscribeEvent
  static void onReload(ModConfigEvent.Reloading e) {
    IConfigSpec spec = e.getConfig().getSpec();
    if (spec == COMMON_SPEC || spec == CLIENT_SPEC || spec == SERVER_SPEC) HOOKS.forEach(Runnable::run);
  }
}
