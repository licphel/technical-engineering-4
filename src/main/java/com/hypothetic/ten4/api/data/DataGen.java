package com.hypothetic.ten4.api.data;

import com.hypothetic.ten4.Ten4;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;

public final class DataGen {
  static final List<DeferredHolder<Block, ? extends Block>> blocks = new ArrayList<>();
  static final Map<DeferredHolder<Block, ? extends Block>, BlockData> blockCfgs = new LinkedHashMap<>();
  static final List<DeferredHolder<Item, ? extends Item>> items = new ArrayList<>();
  static final Map<DeferredHolder<Item, ? extends Item>, ItemData> itemCfgs = new LinkedHashMap<>();
  static final Map<DeferredHolder<CreativeModeTab, ? extends CreativeModeTab>, String[]> tabTranslations = new HashMap<>();

  private DataGen() {
  }

  public static BlockData block(DeferredHolder<Block, ? extends Block> entry) {
    BlockData cfg = new BlockData(entry);
    blockCfgs.put(entry, cfg);
    blocks.add(entry);
    return cfg;
  }

  public static ItemData item(DeferredHolder<Item, ? extends Item> entry) {
    ItemData cfg = new ItemData(entry);
    itemCfgs.put(entry, cfg);
    items.add(entry);
    return cfg;
  }

  public static void tab(DeferredHolder<CreativeModeTab, ? extends CreativeModeTab> entry, String enUs, String zhCn) {
    tabTranslations.put(entry, new String[] {enUs, zhCn});
  }

  static ResourceLocation idOf(DeferredHolder<?, ?> h) {
    return h.getId();
  }

  public static void run(GatherDataEvent event) {
    DataGenerator gen = event.getGenerator();
    PackOutput out = gen.getPackOutput();

    gen.addProvider(event.includeClient(), new BlockStateProvider(out, Ten4.ID, event.getExistingFileHelper()) {
      @Override
      protected void registerStatesAndModels() {
        for (DeferredHolder<Block, ? extends Block> e : blocks) {
          BlockData cfg = blockCfgs.get(e);
          if (cfg == null || cfg.blockState == null) {
            continue;
          }
          cfg.blockState.generate(this, e, cfg.modelName != null ? cfg.modelName : idOf(e).getPath());
        }
      }
    });

    gen.addProvider(event.includeClient(), new ItemModelProvider(out, Ten4.ID, event.getExistingFileHelper()) {
      @Override
      protected void registerModels() {
        for (DeferredHolder<Item, ? extends Item> e : items) {
          ItemData cfg = itemCfgs.get(e);
          if (cfg == null || cfg.modelPath == null) {
            continue;
          }
          withExistingParent(idOf(e).getPath(), mcLoc("item/generated"))
              .texture("layer0", modLoc(cfg.modelPath));
        }
        for (DeferredHolder<Block, ? extends Block> e : blocks) {
          BlockData cfg = blockCfgs.get(e);
          if (cfg == null || !cfg.autoItemModel) {
            continue;
          }
          basicItem(e.get().asItem());
        }
      }
    });

    gen.addProvider(event.includeClient(), new LanguageProvider(out, Ten4.ID, "en_us") {
      @Override
      protected void addTranslations() {
        for (DeferredHolder<Block, ? extends Block> e : blocks) {
          BlockData c = blockCfgs.get(e);
          if (c != null && c.enName != null) {
            add("block.ten4." + idOf(e).getPath(), c.enName);
          }
        }
        for (DeferredHolder<Item, ? extends Item> e : items) {
          ItemData c = itemCfgs.get(e);
          if (c != null && c.enName != null) {
            add("item.ten4." + idOf(e).getPath(), c.enName);
          }
        }
        tabTranslations.forEach((k, v) -> add("itemGroup.ten4." + idOf(k).getPath(), v[0]));
      }
    });

    gen.addProvider(event.includeClient(), new LanguageProvider(out, Ten4.ID, "zh_cn") {
      @Override
      protected void addTranslations() {
        for (DeferredHolder<Block, ? extends Block> e : blocks) {
          BlockData c = blockCfgs.get(e);
          if (c != null && c.zhName != null) {
            add("block.ten4." + idOf(e).getPath(), c.zhName);
          }
        }
        for (DeferredHolder<Item, ? extends Item> e : items) {
          ItemData c = itemCfgs.get(e);
          if (c != null && c.zhName != null) {
            add("item.ten4." + idOf(e).getPath(), c.zhName);
          }
        }
        tabTranslations.forEach((k, v) -> add("itemGroup.ten4." + idOf(k).getPath(), v[1]));
      }
    });
  }
}
