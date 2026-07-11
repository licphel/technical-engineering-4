package com.hypothetic.ten4.lib.pipe;

import com.hypothetic.ten4.lib.capability.fluid.DirectionalFluidHandler;
import com.hypothetic.ten4.lib.capability.fluid.IDirectionalFluidProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluidNet implements IFluidHandler {
  private static final Map<Level, NetworkManager<IFluidHandler>> MANAGERS = new HashMap<>();
  private static final Map<Level, Map<BlockPos, FluidNet>> NETS = new HashMap<>();

  private final BlockPos rootPos;
  private final IDirectionalFluidProvider host;
  private final NetworkManager<IFluidHandler> graph;
  private final List<Slot> consumers = new ArrayList<>(), producers = new ArrayList<>();
  private Level lastLevel;

  private FluidNet(BlockPos rootPos, IDirectionalFluidProvider host, NetworkManager<IFluidHandler> graph) {
    this.rootPos = rootPos;
    this.host = host;
    this.graph = graph;
  }

  private static NetworkManager<IFluidHandler> manager(Level level) {
    return MANAGERS.computeIfAbsent(level, l -> new NetworkManager<>(be -> be instanceof IFluidNetNode, (lvl, p, side) -> lvl.getCapability(Capabilities.FluidHandler.BLOCK, p, side)));
  }

  public static void add(Level level, BlockPos pos) {
    NetworkManager<IFluidHandler> mgr = manager(level);
    mgr.onNodeAdded(level, pos);
    NETS.computeIfAbsent(level, k -> new HashMap<>()).remove(pos);
    Network<IFluidHandler> net = mgr.getNetwork(pos);
    if (net != null) {
      NETS.get(level).remove(net.nodes().iterator().next());
    }
  }

  public static void remove(Level level, BlockPos pos) {
    NetworkManager<IFluidHandler> mgr = manager(level);
    mgr.onNodeRemoved(level, pos);
    Map<BlockPos, FluidNet> map = NETS.get(level);
    if (map != null) {
      map.remove(pos);
    }
  }

  public static @Nullable FluidNet of(Level level, BlockPos pos, IDirectionalFluidProvider host) {
    Network<IFluidHandler> net = manager(level).getNetwork(pos);
    if (net == null || net.nodes().isEmpty()) {
      return null;
    }
    BlockPos root = net.nodes().iterator().next();
    if (!pos.equals(root)) {
      return null;
    }

    Map<BlockPos, FluidNet> map = NETS.computeIfAbsent(level, k -> new HashMap<>());
    FluidNet cached = map.get(root);
    if (cached != null && cached.host == host) {
      return cached;
    }

    FluidNet fn = new FluidNet(root, host, manager(level));
    fn.rescan(level);
    map.put(root, fn);
    return fn;
  }

  public static IFluidHandler sideAccess(Level level, BlockPos pos, Direction side, IDirectionalFluidProvider host) {
    FluidNet net = of(level, pos, host);
    return net != null ? net : new DirectionalFluidHandler(host, side);
  }

  public void tick() {
    if (consumers.isEmpty() && producers.isEmpty()) {
      return;
    }

    IFluidHandler tanks = host.getTanks();
    for (Slot c : consumers) {
      IFluidHandler fresh = lastLevel.getCapability(Capabilities.FluidHandler.BLOCK, c.target, c.side);
      if (fresh == null) {
        continue;
      }
      for (int t = 0; t < tanks.getTanks(); t++) {
        FluidStack stack = tanks.getFluidInTank(t);
        if (stack.isEmpty()) {
          continue;
        }
        int maxSend = Math.min(stack.getAmount(), host.getMaxFluidExtract(c.side));
        FluidStack drained = tanks.drain(stack.copyWithAmount(maxSend), IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
          continue;
        }
        int filled = fresh.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        if (filled < drained.getAmount()) {
          tanks.fill(drained.copyWithAmount(drained.getAmount() - filled), IFluidHandler.FluidAction.EXECUTE);
        }
      }
    }
    for (Slot p : producers) {
      IFluidHandler fresh = lastLevel.getCapability(Capabilities.FluidHandler.BLOCK, p.target, p.side);
      if (fresh == null) {
        continue;
      }
      int space = host.getMaxFluidReceive(p.side);
      for (int t = 0; t < fresh.getTanks(); t++) {
        FluidStack drained = fresh.drain(space, IFluidHandler.FluidAction.EXECUTE);
        if (!drained.isEmpty()) {
          tanks.fill(drained, IFluidHandler.FluidAction.EXECUTE);
          space -= drained.getAmount();
          if (space <= 0) {
            break;
          }
        }
      }
    }
  }

  @Override
  public int getTanks() {
    return host.getTanks().getTanks();
  }

  @Override
  public FluidStack getFluidInTank(int t) {
    return host.getTanks().getFluidInTank(t);
  }

  @Override
  public int getTankCapacity(int t) {
    return host.getTanks().getTankCapacity(t);
  }

  @Override
  public boolean isFluidValid(int t, FluidStack s) {
    return host.getTanks().isFluidValid(t, s);
  }

  @Override
  public int fill(FluidStack r, FluidAction a) {
    return host.getTanks().fill(r, a);
  }

  @Override
  public FluidStack drain(FluidStack r, FluidAction a) {
    return host.getTanks().drain(r, a);
  }

  @Override
  public FluidStack drain(int m, FluidAction a) {
    return host.getTanks().drain(m, a);
  }

  private void rescan(Level level) {
    lastLevel = level;
    consumers.clear();
    producers.clear();
    IFluidHandler tanks = host.getTanks();
    Network<IFluidHandler> net = graph.getNetwork(rootPos);
    if (net == null) {
      return;
    }
    for (BlockPos node : net.nodes()) {
      for (Direction d : Direction.values()) {
        BlockPos target = node.relative(d);
        if (level.getBlockEntity(target) instanceof IFluidNetNode) {
          continue;
        }
        IFluidHandler cap = level.getCapability(Capabilities.FluidHandler.BLOCK, target, d.getOpposite());
        if (cap == null) {
          continue;
        }
        for (int t = 0; t < tanks.getTanks(); t++) {
          FluidStack stack = tanks.getFluidInTank(t);
          if (!stack.isEmpty() && cap.fill(stack.copyWithAmount(1), IFluidHandler.FluidAction.SIMULATE) > 0) {
            consumers.add(new Slot(node, d, target, cap));
            break;
          }
        }
        for (int t = 0; t < cap.getTanks(); t++) {
          if (!cap.getFluidInTank(t).isEmpty()) {
            producers.add(new Slot(node, d, target, cap));
            break;
          }
        }
      }
    }
  }

  private record Slot(BlockPos cablePos, Direction side, BlockPos target, IFluidHandler cap) {
  }
}
