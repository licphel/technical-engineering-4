package com.hypothetic.ten4.api.transmission;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public abstract class Transmitter<AC, NET extends Network<AC, NET, T>, T extends Transmitter<AC, NET, T>> {
  final ITransmitterProvider tile;
  private final ConnectionType[] connectionTypes = {
      ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL,
      ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL
  };
  private final TransmitterFilter<?>[] filters = new TransmitterFilter<?>[6];
  private final TransmitterBlocker[] blockers = new TransmitterBlocker[6];
  byte currentTransmitterConnections;
  byte currentAcceptorConnections;
  private @Nullable DyeColor color;
  private @Nullable NET network;
  private boolean orphan = true;
  private float clientScale;

  protected Transmitter(ITransmitterProvider tile) {
    this.tile = tile;
  }

  public static boolean connectionBit(byte c, Direction s) {
    return (c & (1 << s.ordinal())) > 0;
  }

  public static byte setBit(byte c, boolean v, Direction s) {
    return (byte) ((c & ~(1 << s.ordinal())) | ((v ? 1 : 0) << s.ordinal()));
  }

  public @Nullable DyeColor getColor() {
    return color;
  }

  public void setColor(@Nullable DyeColor c) {
    this.color = c;
  }

  @SuppressWarnings("unchecked")
  public <F> @Nullable TransmitterFilter<F> getFilter(Direction side) {
    return (TransmitterFilter<F>) filters[side.ordinal()];
  }

  public void setFilter(Direction side, @Nullable TransmitterFilter<?> filter) {
    filters[side.ordinal()] = filter;
  }

  public @Nullable TransmitterBlocker getBlocker(Direction side) {
    return blockers[side.ordinal()];
  }

  public void setBlocker(Direction side, @Nullable TransmitterBlocker blocker) {
    blockers[side.ordinal()] = blocker;
  }

  public void rebuild() {
    if (!isRemote() && hasNetwork()) {
      TransmitterNetworkRegistry.remove(self());
      TransmitterNetworkRegistry.join(self());
    }
  }

  public boolean isColorCompatible(Transmitter<?, ?, ?> other) {
    return this.color == null || other.color == null || this.color == other.color;
  }

  public byte getTransmitterConnections() {
    return currentTransmitterConnections;
  }

  public byte getAcceptorConnections() {
    return currentAcceptorConnections;
  }

  public void applyConnectionSync(byte conn, byte acc, ConnectionType[] types, @Nullable DyeColor color) {
    currentTransmitterConnections = conn;
    currentAcceptorConnections = acc;
    for (int i = 0; i < 6 && i < types.length; i++) {
      setConnectionTypeRaw(Direction.values()[i], types[i]);
    }
    this.color = color;
  }

  public void syncClientScale(float scale) {
    this.clientScale = scale;
  }

  public float getClientScale() {
    return clientScale;
  }

  @SuppressWarnings("unchecked")
  T self() {
    return (T) this;
  }

  public BlockPos getBlockPos() {
    return tile.getBlockPos();
  }

  public @Nullable Level getLevel() {
    return tile.getLevel();
  }

  public boolean isRemote() {
    return tile.getLevel() == null || tile.getLevel().isClientSide();
  }

  public ITransmitterProvider getTile() {
    return tile;
  }

  public boolean isValid() {
    return !tile.isInvalid();
  }

  public ConnectionType getConnectionType(Direction side) {
    int i = side.ordinal();
    if (!connectionBit((byte) (currentTransmitterConnections | currentAcceptorConnections), side)) {
      return ConnectionType.NONE;
    }
    if (connectionBit(currentTransmitterConnections, side)) {
      return ConnectionType.NORMAL;
    }
    return connectionTypes[i];
  }

  public ConnectionType getConnectionTypeRaw(Direction side) {
    return connectionTypes[side.ordinal()];
  }

  public void setConnectionTypeRaw(Direction side, ConnectionType type) {
    connectionTypes[side.ordinal()] = type;
  }

  byte getAllCurrentConnections() {
    return (byte) (currentTransmitterConnections | currentAcceptorConnections);
  }

  public @Nullable NET getNetwork() {
    return network;
  }

  public boolean hasNetwork() {
    return !orphan && network != null;
  }

  public boolean isOrphan() {
    return orphan;
  }

  public void setOrphan(boolean o) {
    orphan = o;
  }

  public boolean setNetwork(@Nullable NET net, boolean requestNow) {
    if (network == net) {
      return false;
    }
    if (isRemote() && network != null) {
      network.removeTransmitter(self());
    }
    network = net;
    orphan = network == null;
    if (isRemote() && network != null) {
      network.addTransmitter(self());
    }
    if (!isRemote()) {
      if (requestNow) {
        requestsUpdate();
      } else {
        return true;
      }
    }
    return false;
  }

  public abstract NET createEmptyNetwork(UUID id);

  public abstract NET createNetworkByMerging(Collection<NET> nets);

  public abstract boolean supportsTransmission(Transmitter<?, ?, ?> other);

  protected abstract boolean isValidAcceptor(Direction side);

  public void refreshConnections() {
    if (isRemote()) {
      return;
    }
    byte pt = getPossibleTransmitterConnections();
    byte pa = getPossibleAcceptorConnections();
    byte ne = 0, removed = 0;
    if ((pt | pa) != getAllCurrentConnections()) {
      if (pt != currentTransmitterConnections) {
        byte diff = (byte) (pt ^ currentTransmitterConnections);
        ne = (byte) (diff & ~currentTransmitterConnections); // bits that appeared
        removed = (byte) (diff & currentTransmitterConnections); // bits that disappeared
      }
    }
    currentTransmitterConnections = pt;
    currentAcceptorConnections = pa;
    if (ne != 0) {
      checkReconnect(ne);
    }
    if (removed != 0) {
      notifyDisconnected(removed);
    }
    tile.sendUpdatePacket();
  }

  public void refreshConnections(Direction side) {
    if (isRemote()) {
      return;
    }
    boolean pt = getPossibleTransmitterConnection(side), pa = getPossibleAcceptorConnection(side);
    boolean had = connectionBit(getAllCurrentConnections(), side);
    currentTransmitterConnections = setBit(currentTransmitterConnections, pt, side);
    currentAcceptorConnections = setBit(currentAcceptorConnections, pa, side);
    if ((pt || pa) != had) {
      tile.sendUpdatePacket();
      // If connection was lost, notify neighbor to also refresh
      if (had) {
        notifyDisconnected(setBit((byte) 0, true, side));
      }
    }
  }

  byte getPossibleTransmitterConnections() {
    Level level = getLevel();
    if (level == null) {
      return 0;
    }

    byte b = 0;
    BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) {
      BlockEntity be = level.getBlockEntity(p.relative(d));
      if (be instanceof ITransmitterProvider tb) {
        Transmitter<?, ?, ?> o = tb.getTransmitter();
        if (supportsTransmission(o) && isColorCompatible(o)) {
          b |= (byte) (1 << d.ordinal());
        }
      }
    }
    return b;
  }

  byte getPossibleAcceptorConnections() {
    Level level = getLevel();
    if (level == null) {
      return 0;
    }

    byte b = 0;
    BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) {
      BlockPos t = p.relative(d);
      if (level.getBlockEntity(t) instanceof ITransmitterProvider) {
        continue;
      }
      if (isValidAcceptor(d)) {
        b |= (byte) (1 << d.ordinal());
      }
    }
    return b;
  }

  boolean getPossibleTransmitterConnection(Direction side) {
    Level level = getLevel();
    if (level == null) {
      return false;
    }

    BlockEntity be = level.getBlockEntity(getBlockPos().relative(side));
    if (be instanceof ITransmitterProvider tb) {
      Transmitter<?, ?, ?> o = tb.getTransmitter();
      return supportsTransmission(o) && isColorCompatible(o);
    }
    return false;
  }

  boolean getPossibleAcceptorConnection(Direction side) {
    Level level = getLevel();
    if (level == null) {
      return false;
    }

    BlockPos t = getBlockPos().relative(side);
    if (level.getBlockEntity(t) instanceof ITransmitterProvider) {
      return false;
    }
    return isValidAcceptor(side);
  }

  private void checkReconnect(byte ne) {
    Level level = getLevel();
    if (level == null) {
      return;
    }

    BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) {
      if (connectionBit(ne, d)) {
        BlockEntity be = level.getBlockEntity(p.relative(d));
        if (be instanceof ITransmitterProvider tb) {
          Transmitter<?, ?, ?> o = tb.getTransmitter();
          o.refreshConnections(d.getOpposite());
        }
      }
    }
  }

  private void notifyDisconnected(byte removed) {
    Level level = getLevel();
    if (level == null) {
      return;
    }

    BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) {
      if (connectionBit(removed, d)) {
        BlockEntity be = level.getBlockEntity(p.relative(d));
        if (be instanceof ITransmitterProvider tb) {
          Transmitter<?, ?, ?> o = tb.getTransmitter();
          o.refreshConnections(d.getOpposite());
        }
      }
    }
  }

  public void onModeChange(Direction side) {
    markDirtyAcceptor(side);
    if (getPossibleTransmitterConnections() != currentTransmitterConnections) {
      markDirtyTransmitters();
    }
    tile.notifyChanges();
  }

  public void onNeighborBlockChange(Direction side) {
    refreshConnections(side);
  }

  void markDirtyTransmitters() {
    tile.notifyChanges();
    requestsUpdate();
  }

  void markDirtyAcceptor(Direction side) {
    if (hasNetwork()) {
      assert network != null;
      network.acceptorChanged(self(), side);
    }
  }

  public void requestsUpdate() {
    tile.sendUpdatePacket();
  }

  public void validateAndTakeShare() {
    takeShare();
  }

  public abstract void takeShare();

  public CompatibleTransmitterValidator<?, ?, ?> getNewOrphanValidator() {
    return new CompatibleTransmitterValidator<>(self());
  }

  public boolean isValidTransmitterBasic(ITransmitterProvider neighborTile, Direction side) {
    Transmitter<?, ?, ?> other = neighborTile.getTransmitter();
    if (!supportsTransmission(other)) {
      return false;
    }
    // Color check: different non-null colors cannot connect
    if (color != null && other.color != null && color != other.color) {
      return false;
    }
    // Blocker check: either side can block (energy/fluid network separation)
    TransmitterBlocker b = getBlocker(side);
    if (b != null && b.isBlocked(side)) {
      return false;
    }
    b = other.getBlocker(side.getOpposite());
    if (b != null && b.isBlocked(side.getOpposite())) {
      return false;
    }
    return getConnectionTypeRaw(side) != ConnectionType.NONE
        && other.getConnectionTypeRaw(side.getOpposite()) != ConnectionType.NONE;
  }

  public void remove() {
  }

  public CompoundTag write(HolderLookup.Provider prov, CompoundTag tag) {
    tag.putByte("Tran", currentTransmitterConnections);
    tag.putByte("Acc", currentAcceptorConnections);
    tag.putIntArray("CT", getRawCT());
    if (color != null) {
      tag.putInt("Color", color.ordinal());
    }
    return tag;
  }

  public void read(HolderLookup.Provider prov, CompoundTag tag) {
    if (tag.contains("Tran")) {
      currentTransmitterConnections = tag.getByte("Tran");
    }
    if (tag.contains("Acc")) {
      currentAcceptorConnections = tag.getByte("Acc");
    }
    readRawCT(tag);
    color = tag.contains("Color") ? DyeColor.values()[tag.getInt("Color") % DyeColor.values().length] : null;
  }

  public CompoundTag getReducedUpdateTag(HolderLookup.Provider prov, CompoundTag tag) {
    tag.putByte("Tran", currentTransmitterConnections);
    tag.putByte("Acc", currentAcceptorConnections);
    tag.putIntArray("CT", getRawCT());
    if (color != null) {
      tag.putInt("Color", color.ordinal());
    }
    if (hasNetwork()) {
      assert network != null;
      tag.putUUID("Net", network.getUUID());
    }
    return tag;
  }

  public boolean handleUpdateTag(CompoundTag tag, HolderLookup.Provider prov) {
    if (tag.contains("Tran")) {
      currentTransmitterConnections = tag.getByte("Tran");
    }
    if (tag.contains("Acc")) {
      currentAcceptorConnections = tag.getByte("Acc");
    }
    if (tag.contains("CT")) {
      readRawCT(tag);
    }
    color = tag.contains("Color") ? DyeColor.values()[tag.getInt("Color") % DyeColor.values().length] : null;
    if (tag.hasUUID("Net")) {
      UUID id = tag.getUUID("Net");
      if (hasNetwork()) {
        assert network != null;
        if (network.getUUID().equals(id)) {
          return false;
        }
      }
      Network<?, ?, ?> cn = TransmitterNetworkRegistry.getClientNetwork(id);
      if (cn == null) {
        NET n = createEmptyNetwork(id);
        n.register();
        setNetwork(n, false);
      } else {
        @SuppressWarnings("unchecked") NET c = (NET) cn;
        setNetwork(c, false);
      }
    } else {
      setNetwork(null, false);
    }
    return false;
  }

  private int[] getRawCT() {
    int[] r = new int[6];
    for (int i = 0; i < 6; i++) {
      r[i] = connectionTypes[i].ordinal();
    }
    return r;
  }

  private void readRawCT(CompoundTag tag) {
    if (tag.contains("CT", Tag.TAG_INT_ARRAY)) {
      int[] r = tag.getIntArray("CT");
      for (int i = 0; i < r.length && i < 6; i++) {
        connectionTypes[i] = ConnectionType.of(r[i]);
      }
    }
  }
}
