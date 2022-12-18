package nl.gjorgdy.vanillaplus;

import io.wispforest.owo.config.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Config(name = "VanillaPlus", wrapperName = "MainConfig")
public class ConfigTemplate {

    public List<String> elevatorBlocks = new ArrayList<>(
           List.of(
                  "minecraft:purpur_block",
                  "minecraft:purpur_pillar",
                  "minecraft:purpur_slab",
                  "minecraft:purpur_stairs"
          )
    );
    public List<String> extenderBlocks = new ArrayList<>(
            List.of(
                    "minecraft:end_rod"
            )
    );
    public int elevatorRange = 8;

    public Map<String, String> generatorBlocks = new HashMap<>(
            Map.of(
                "minecraft:andesite", "minecraft:andesite",
                "minecraft:granite", "minecraft:granite",
                "minecraft:diorite", "minecraft:diorite",
                "minecraft:tuff", "minecraft:tuff",
                "minecraft:calcite", "minecraft:calcite",
                "minecraft:deepslate", "minecraft:cobbled_deepslate",
                "minecraft:sandstone", "minecraft:sandstone",
                "minecraft:sand", "minecraft:sandstone",
                "minecraft:red_sand", "minecraft:red_sandstone",
                "minecraft:red_sandstone", "minecraft:red_sandstone"
            )
    );
    public int cryingObsidianChance = 16;

    public List<String> firepoleBlocks = new ArrayList<>(
            List.of(
                    "minecraft:iron_bars"
            )
    );

}
