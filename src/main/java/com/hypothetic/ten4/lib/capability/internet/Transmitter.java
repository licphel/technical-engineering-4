package com.hypothetic.ten4.lib.capability.internet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Generic per-cable connection state and network membership.
 * ACCEPTOR = capability type on external blocks (IEnergyStorage / IItemHandler / IFluidHandler).
 * Pattern: ported from Mekanism's Transmitter.
 */
public abstract class Transmitter<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, TRANSMITTER>,
                                  TRANSMITTER extends Transmitter<ACCEPTOR, NETWORK, TRANSMITTER>> {

  static boolean connectionBit(byte c, Direction s) { return (c & (1 << s.ordinal())) > 0; }
  static byte setBit(byte c, boolean v, Direction s) { return (byte) ((c & ~(1 << s.ordinal())) | ((v ? 1 : 0) << s.ordinal())); }

  private ConnectionType[] connectionTypes = {
      ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL,
      ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL
  };
  byte currentTransmitterConnections;
  byte currentAcceptorConnections;

  public byte getTransmitterConnections() { return currentTransmitterConnections; }
  public byte getAcceptorConnections() { return currentAcceptorConnections; }

  // Client-side energy buffer tracking (synced via CableSyncPayload)
  private long clientBuffer, clientCapacity;

  public long getClientBuffer() { return clientBuffer; }
  public long getClientCapacity() { return clientCapacity; }
  public float getClientFillRatio() { return clientCapacity > 0 ? (float) clientBuffer / clientCapacity : 0; }

  public void applySyncData(byte conn, byte acc, ConnectionType[] types, long buffer, long capacity) {
    currentTransmitterConnections = conn;
    currentAcceptorConnections = acc;
    for (int i = 0; i < 6 && i < types.length; i++)
      setConnectionTypeRaw(Direction.values()[i], types[i]);
    this.clientBuffer = buffer;
    this.clientCapacity = capacity;
  }

  private NETWORK network;
  private boolean orphan = true;
  final ITransmitterTile tile;

  protected Transmitter(ITransmitterTile tile) { this.tile = tile; }

  @SuppressWarnings("unchecked")
  TRANSMITTER self() { return (TRANSMITTER) this; }

  public BlockPos getBlockPos() { return tile.getBlockPos(); }
  public Level getLevel() { return tile.getLevel(); }
  public boolean isRemote() { return tile.getLevel() == null || tile.getLevel().isClientSide(); }
  public ITransmitterTile getTile() { return tile; }
  public boolean isValid() { return !tile.isInvalid(); }

  // --- Connection types ---

  public ConnectionType getConnectionType(Direction side) {
    int i = side.ordinal();
    if (!connectionBit((byte) (currentTransmitterConnections | currentAcceptorConnections), side)) return ConnectionType.NONE;
    if (connectionBit(currentTransmitterConnections, side)) return ConnectionType.NORMAL;
    return connectionTypes[i];
  }
  public ConnectionType getConnectionTypeRaw(Direction side) { return connectionTypes[side.ordinal()]; }
  public void setConnectionTypeRaw(Direction side, ConnectionType type) { connectionTypes[side.ordinal()] = type; }
  byte getAllCurrentConnections() { return (byte) (currentTransmitterConnections | currentAcceptorConnections); }

  // --- Network ---

  public NETWORK getNetwork() { return network; }
  public boolean hasNetwork() { return !orphan && network != null; }
  public boolean isOrphan() { return orphan; }
  public void setOrphan(boolean o) { orphan = o; }

  public boolean setNetwork(NETWORK net, boolean requestNow) {
    if (network == net) return false;
    if (isRemote() && network != null) network.removeTransmitter(self());
    network = net;
    orphan = network == null;
    if (isRemote() && network != null) network.addTransmitter(self());
    if (!isRemote()) { if (requestNow) requestsUpdate(); else return true; }
    return false;
  }

  public abstract NETWORK createEmptyNetwork(UUID id);
  public abstract NETWORK createNetworkByMerging(Collection<NETWORK> nets);
  public abstract boolean supportsTransmission(Transmitter<?, ?, ?> other);
  protected abstract boolean isValidAcceptor(Direction side);
  protected abstract @Nullable Transmitter<?, ?, ?> getTransmitter(ITransmitterTile t);

  // --- Refresh ---

  public void refreshConnections() {
    if (isRemote()) return;
    byte pt = getPossibleTransmitterConnections();
    byte pa = getPossibleAcceptorConnections();
    byte ne = 0;
    if ((pt | pa) != getAllCurrentConnections()) {
      if (pt != currentTransmitterConnections) { ne = (byte) (pt ^ currentTransmitterConnections); ne &= ~currentTransmitterConnections; }
    }
    currentTransmitterConnections = pt;
    currentAcceptorConnections = pa;
    if (ne != 0) checkReconnect(ne);
    tile.sendUpdatePacket();
  }

  public void refreshConnections(Direction side) {
    if (isRemote()) return;
    boolean pt = getPossibleTransmitterConnection(side), pa = getPossibleAcceptorConnection(side);
    boolean ch = (pt || pa) != connectionBit(getAllCurrentConnections(), side);
    currentTransmitterConnections = setBit(currentTransmitterConnections, pt, side);
    currentAcceptorConnections = setBit(currentAcceptorConnections, pa, side);
    if (ch) tile.sendUpdatePacket();
  }

  byte getPossibleTransmitterConnections() {
    byte b = 0; BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) {
      BlockEntity be = getLevel().getBlockEntity(p.relative(d));
      if (be instanceof ITransmitterTile tb) {
        Transmitter<?, ?, ?> o = getTransmitter(tb);
        if (o != null && supportsTransmission(o)) b |= (byte) (1 << d.ordinal());
      }
    }
    return b;
  }

  byte getPossibleAcceptorConnections() {
    byte b = 0; BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) {
      if (getConnectionTypeRaw(d) == ConnectionType.NONE) continue;
      BlockPos t = p.relative(d);
      if (getLevel().getBlockEntity(t) instanceof ITransmitterTile) continue;
      if (isValidAcceptor(d)) b |= (byte) (1 << d.ordinal());
    }
    return b;
  }

  boolean getPossibleTransmitterConnection(Direction side) {
    BlockEntity be = getLevel().getBlockEntity(getBlockPos().relative(side));
    if (be instanceof ITransmitterTile tb) { Transmitter<?, ?, ?> o = getTransmitter(tb); return o != null && supportsTransmission(o); }
    return false;
  }

  boolean getPossibleAcceptorConnection(Direction side) {
    if (getConnectionTypeRaw(side) == ConnectionType.NONE) return false;
    BlockPos t = getBlockPos().relative(side);
    if (getLevel().getBlockEntity(t) instanceof ITransmitterTile) return false;
    return isValidAcceptor(side);
  }

  private void checkReconnect(byte ne) {
    if (hasNetwork()) return; BlockPos p = getBlockPos();
    for (Direction d : Direction.values()) if (connectionBit(ne, d)) {
      BlockEntity be = getLevel().getBlockEntity(p.relative(d));
      if (be instanceof ITransmitterTile tb) { Transmitter<?, ?, ?> o = getTransmitter(tb); if (o != null) o.refreshConnections(d.getOpposite()); }
    }
  }

  public void onModeChange(Direction side) { markDirtyAcceptor(side); if (getPossibleTransmitterConnections() != currentTransmitterConnections) markDirtyTransmitters(); tile.setChanged(); }
  public void onNeighborBlockChange(Direction side) { refreshConnections(side); }

  void markDirtyTransmitters() { tile.notifyTileChange(); if (hasNetwork()) TransmitterNetworkRegistry.invalidateTransmitter(self()); }
  void markDirtyAcceptor(Direction side) { if (hasNetwork()) network.acceptorChanged(self(), side); }

  public void requestsUpdate() { tile.sendUpdatePacket(); }
  public void validateAndTakeShare() { takeShare(); }
  public abstract void takeShare();
  public void remove() {}

  // --- Persistence ---

  public CompoundTag write(HolderLookup.Provider prov, CompoundTag tag) {
    tag.putByte("conn", currentTransmitterConnections); tag.putByte("acc", currentAcceptorConnections); tag.putIntArray("ct", getRawCT()); return tag;
  }
  public void read(HolderLookup.Provider prov, CompoundTag tag) {
    if (tag.contains("conn")) currentTransmitterConnections = tag.getByte("conn");
    if (tag.contains("acc")) currentAcceptorConnections = tag.getByte("acc");
    readRawCT(tag);
  }
  public CompoundTag getReducedUpdateTag(HolderLookup.Provider prov, CompoundTag tag) {
    tag.putByte("conn", currentTransmitterConnections); tag.putByte("acc", currentAcceptorConnections); tag.putIntArray("ct", getRawCT());
    if (hasNetwork()) tag.putUUID("net", network.getUUID()); return tag;
  }
  public boolean handleUpdateTag(CompoundTag tag, HolderLookup.Provider prov) {
    if (tag.contains("conn")) currentTransmitterConnections = tag.getByte("conn");
    if (tag.contains("acc")) currentAcceptorConnections = tag.getByte("acc");
    if (tag.contains("ct")) readRawCT(tag);
    if (tag.hasUUID("net")) {
      UUID id = tag.getUUID("net");
      if (hasNetwork() && network.getUUID().equals(id)) return false;
      DynamicNetwork<?, ?, ?> cn = TransmitterNetworkRegistry.getClientNetwork(id);
      if (cn == null) { NETWORK n = createEmptyNetwork(id); n.register(); setNetwork(n, false); }
      else { @SuppressWarnings("unchecked") NETWORK c = (NETWORK) cn; setNetwork(c, false); }
    } else setNetwork(null, false);
    return false;
  }
  private int[] getRawCT() { int[] r = new int[6]; for (int i = 0; i < 6; i++) r[i] = connectionTypes[i].ordinal(); return r; }
  private void readRawCT(CompoundTag tag) { if (tag.contains("ct", Tag.TAG_INT_ARRAY)) { int[] r = tag.getIntArray("ct"); for (int i = 0; i < r.length && i < 6; i++) connectionTypes[i] = ConnectionType.VALUES[r[i] % ConnectionType.VALUES.length]; } }
}
