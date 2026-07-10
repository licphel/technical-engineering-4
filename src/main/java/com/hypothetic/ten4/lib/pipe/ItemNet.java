package com.hypothetic.ten4.lib.pipe;

import com.hypothetic.ten4.lib.capability.item.DirectionalItemHandler;
import com.hypothetic.ten4.lib.capability.item.IDirectionalItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemNet implements IItemHandler {
  private static final Map<Level, NetworkManager<IItemHandler>> MANAGERS = new HashMap<>();
  private static final Map<Level, Map<BlockPos, ItemNet>> NETS = new HashMap<>();

  private final BlockPos rootPos;
  private final IDirectionalItemProvider host;
  private final NetworkManager<IItemHandler> graph;
  private final List<Slot> consumers = new ArrayList<>(), producers = new ArrayList<>();
  private Level lastLevel;

  private ItemNet(BlockPos rootPos, IDirectionalItemProvider host, NetworkManager<IItemHandler> graph) {
    this.rootPos = rootPos;
    this.host = host;
    this.graph = graph;
  }

  private static NetworkManager<IItemHandler> manager(Level level) {
    return MANAGERS.computeIfAbsent(level, l -> new NetworkManager<>(be -> be instanceof IItemNetNode, (lvl, p, side) -> lvl.getCapability(Capabilities.ItemHandler.BLOCK, p, side)));
  }

  public static void add(Level level, BlockPos pos) {
    NetworkManager<IItemHandler> mgr = manager(level);
    mgr.onNodeAdded(level, pos);
    NETS.computeIfAbsent(level, k -> new HashMap<>()).remove(pos);
    Network<IItemHandler> net = mgr.getNetwork(pos);
    if (net != null) {
      NETS.get(level).remove(net.nodes().iterator().next());
    }
  }

  public static void remove(Level level, BlockPos pos) {
    NetworkManager<IItemHandler> mgr = manager(level);
    mgr.onNodeRemoved(level, pos);
    Map<BlockPos, ItemNet> map = NETS.get(level);
    if (map != null) {
      map.remove(pos);
    }
  }

  public static @Nullable ItemNet of(Level level, BlockPos pos, IDirectionalItemProvider host) {
    Network<IItemHandler> net = manager(level).getNetwork(pos);
    if (net == null || net.nodes().isEmpty()) {
      return null;
    }
    BlockPos root = net.nodes().iterator().next();
    if (!pos.equals(root)) {
      return null;
    }

    Map<BlockPos, ItemNet> map = NETS.computeIfAbsent(level, k -> new HashMap<>());
    ItemNet cached = map.get(root);
    if (cached != null && cached.host == host) {
      return cached;
    }

    ItemNet in = new ItemNet(root, host, manager(level));
    in.rescan(level);
    map.put(root, in);
    return in;
  }

  public static IItemHandler sideAccess(Level level, BlockPos pos, Direction side, IDirectionalItemProvider host) {
    ItemNet net = of(level, pos, host);
    return net != null ? net : new DirectionalItemHandler(host, side);
  }

  private static int findFirstEmpty(IItemHandler inv) {
    for (int i = 0; i < inv.getSlots(); i++) {
      if (inv.getStackInSlot(i).isEmpty()) {
        return i;
      }
    }
    return 0;
  }

  public void tick() {
    if (consumers.isEmpty() && producers.isEmpty()) {
      return;
    }

    IItemHandler inv = host.getInventory();
    for (Slot c : consumers) {
      IItemHandler fresh = lastLevel.getCapability(Capabilities.ItemHandler.BLOCK, c.target, c.side);
      if (fresh == null) {
        continue;
      }
      for (int srcSlot = 0; srcSlot < inv.getSlots(); srcSlot++) {
        ItemStack stack = inv.getStackInSlot(srcSlot);
        if (stack.isEmpty()) {
          continue;
        }
        int maxSend = Math.min(stack.getCount(), host.getMaxItemExtract(c.side));
        ItemStack toSend = inv.extractItem(srcSlot, maxSend, true);
        if (toSend.isEmpty()) {
          continue;
        }
        ItemStack remainder = fresh.insertItem(findFirstEmpty(fresh), toSend, true);
        int movable = toSend.getCount() - remainder.getCount();
        if (movable <= 0) {
          continue;
        }
        inv.extractItem(srcSlot, movable, false);
        fresh.insertItem(findFirstEmpty(fresh), toSend.copyWithCount(movable), false);
      }
    }
    for (Slot p : producers) {
      IItemHandler fresh = lastLevel.getCapability(Capabilities.ItemHandler.BLOCK, p.target, p.side);
      if (fresh == null) {
        continue;
      }
      int maxPull = host.getMaxItemReceive(p.side);
      for (int srcSlot = 0; srcSlot < fresh.getSlots(); srcSlot++) {
        if (maxPull <= 0) {
          break;
        }
        ItemStack extracted = fresh.extractItem(srcSlot, maxPull, false);
        if (!extracted.isEmpty()) {
          inv.insertItem(findFirstEmpty(inv), extracted, false);
          maxPull -= extracted.getCount();
        }
      }
    }
  }

  @Override
  public int getSlots() {
    return host.getInventory().getSlots();
  }

  @Override
  public ItemStack getStackInSlot(int s) {
    return host.getInventory().getStackInSlot(s);
  }

  @Override
  public ItemStack insertItem(int s, ItemStack st, boolean sim) {
    return host.getInventory().insertItem(s, st, sim);
  }

  @Override
  public ItemStack extractItem(int s, int a, boolean sim) {
    return host.getInventory().extractItem(s, a, sim);
  }

  @Override
  public int getSlotLimit(int s) {
    return host.getInventory().getSlotLimit(s);
  }

  @Override
  public boolean isItemValid(int s, ItemStack st) {
    return host.getInventory().isItemValid(s, st);
  }

  private void rescan(Level level) {
    lastLevel = level;
    consumers.clear();
    producers.clear();
    IItemHandler inv = host.getInventory();
    Network<IItemHandler> net = graph.getNetwork(rootPos);
    if (net == null) {
      return;
    }
    for (BlockPos node : net.nodes()) {
      for (Direction d : Direction.values()) {
        BlockPos target = node.relative(d);
        if (level.getBlockEntity(target) instanceof IItemNetNode) {
          continue;
        }
        IItemHandler cap = level.getCapability(Capabilities.ItemHandler.BLOCK, target, d.getOpposite());
        if (cap == null) {
          continue;
        }
        // Check if we can push to this neighbor
        for (int s = 0; s < inv.getSlots(); s++) {
          ItemStack stack = inv.getStackInSlot(s);
          if (!stack.isEmpty() && cap.insertItem(0, stack.copyWithCount(1), true).isEmpty()) {
            consumers.add(new Slot(node, d, target, cap));
            break;
          }
        }
        // Check if neighbor has items to pull
        for (int s = 0; s < cap.getSlots(); s++) {
          if (!cap.getStackInSlot(s).isEmpty()) {
            producers.add(new Slot(node, d, target, cap));
            break;
          }
        }
      }
    }
  }

  private record Slot(BlockPos cablePos, Direction side, BlockPos target, IItemHandler cap) {
  }
}
