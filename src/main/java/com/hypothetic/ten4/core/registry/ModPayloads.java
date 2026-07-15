package com.hypothetic.ten4.core.registry;

import com.hypothetic.ten4.Ten4;
import com.hypothetic.ten4.api.network.device.DeviceConfigPayload;
import com.hypothetic.ten4.api.network.device.IoFacePayload;
import com.hypothetic.ten4.api.network.device.SigmodePayload;
import com.hypothetic.ten4.api.network.duct.DuctConnectionPayload;
import com.hypothetic.ten4.api.network.duct.DuctEnergyPayload;
import com.hypothetic.ten4.api.network.duct.DuctFluidPayload;
import com.hypothetic.ten4.api.network.duct.DuctItemPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Ten4.ID)
public final class ModPayloads {
  @SubscribeEvent
  public static void onRegisterPayloads(RegisterPayloadHandlersEvent e) {
    PayloadRegistrar r = e.registrar(Ten4.VERSION);
    r.playToServer(IoFacePayload.TYPE, IoFacePayload.CODEC, IoFacePayload::handle);
    r.playToServer(SigmodePayload.TYPE, SigmodePayload.CODEC, SigmodePayload::handle);
    r.playToServer(DeviceConfigPayload.TYPE, DeviceConfigPayload.CODEC, DeviceConfigPayload::handle);
    r.playToClient(DuctConnectionPayload.TYPE, DuctConnectionPayload.CODEC, DuctConnectionPayload::handle);
    r.playToClient(DuctEnergyPayload.TYPE, DuctEnergyPayload.CODEC, DuctEnergyPayload::handle);
    r.playToClient(DuctItemPayload.TYPE, DuctItemPayload.CODEC, DuctItemPayload::handle);
    r.playToClient(DuctFluidPayload.TYPE, DuctFluidPayload.CODEC, DuctFluidPayload::handle);
  }
}
