package com.hypothetic.ten4.api.data;

import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Registered manually in {@link com.hypothetic.ten4.Ten4} constructor (Create's pattern).
 */
public final class ModDataGen {
  private ModDataGen() {
  }

  public static void gatherData(GatherDataEvent event) {
    DataGen.run(event);
  }
}
