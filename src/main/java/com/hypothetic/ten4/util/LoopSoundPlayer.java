package com.hypothetic.ten4.util;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public abstract class LoopSoundPlayer {
  private float playBeginStamp;
  private final float length;
  private SoundEvent current;

  protected LoopSoundPlayer(float length) {
    this.length = length;
  }

  public boolean tryPlay(SoundEvent event) {
    if (current == null || isDone()) {
      playBeginStamp = System.currentTimeMillis();
      current = event;
      play(event);
      return true;
    }
    return false;
  }

  protected abstract void play(SoundEvent event);

  public boolean isDone() {
    return System.currentTimeMillis() - playBeginStamp >= 1000 * length;
  }

  public final static class ServerSide extends LoopSoundPlayer {
    private final Level level;
    private final BlockPos pos;
    private final SoundSource src;

    public ServerSide(Level level, BlockPos pos, SoundSource src, float length) {
      super(length);
      this.level = level;
      this.pos = pos;
      this.src = src;
    }

    @Override
    protected void play(SoundEvent event) {
      level.playSound(null, pos, event, src, 1.0F, (float) Math.random() * 0.05F + 0.95F);
    }
  }
}
