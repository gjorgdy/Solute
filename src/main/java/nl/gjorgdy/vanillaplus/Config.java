package nl.gjorgdy.vanillaplus;

import com.mojang.serialization.Codec;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import net.minecraft.block.Block;

import java.util.*;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSource;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class Config {
    public List<Block> elevatorBlocks;
    public List<Block> extenderBlocks;
    public Map<Block, Block> generatorBlocks;
    //public List<Block> firepoleBlocks;

    // Construct Database URI
    private final String uri = "mongodb://helix:Pg56Uk7VwAnS4KHJDHkaxQtCaFv9Zl@hexasis.eu:27017";
    private final String database = "helix_alpha";
    public int elevatorRange;

    public int cryingObsidianChance;

    // Load config from database
    public Config() {
        uploadToDatabase();
        loadFromDatabase();
    }

    public void loadFromDatabase() {
        // Connect to database
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase(this.database);
            MongoCollection<Document> collection = database.getCollection("vanilla_plus");

            Document configDoc = collection.find(eq("_id", "Config")).first();

            List<String> elevatorBlocksList = (List<String>) configDoc.get("elevatorBlocks");
            elevatorBlocks = convertToBlocks(elevatorBlocksList);
            List<String> extenderBlocksList = (List<String>) configDoc.get("extenderBlocks");
            extenderBlocks = convertToBlocks(extenderBlocksList);
            elevatorRange = (int) configDoc.get("elevatorRange");

            Map<String, String> generatorBlocksMap = (Map<String, String>) configDoc.get("generatorBlocks");
            generatorBlocks = convertToBlocks(generatorBlocksMap);

            cryingObsidianChance = (int) configDoc.get("cryingObsidianChance");

            VanillaPlus.LOGGER.info("Config successfully loaded from Database");

        } catch (Exception e) {
            throw e;
            //VanillaPlus.LOGGER.info(e.toString());
        }
    }

    public static List<Block> convertToBlocks(List<String> strings) {
        List<Block> blocks = new ArrayList<>();
        strings.forEach( (string) -> {
            Block block = Registries.BLOCK.get(new Identifier(string));
            blocks.add(block);
        });
        return blocks;
    }

    public static Map<Block, Block> convertToBlocks(Map<String, String> strings) {
        Map<Block, Block> blocks = new HashMap<>();
        strings.forEach( (keyString, valueString) -> {
            Block keyBlock = Registries.BLOCK.get(new Identifier(keyString));
            Block valueBlock = Registries.BLOCK.get(new Identifier(valueString));
            blocks.put(keyBlock, valueBlock);
        });
        return blocks;
    }

    public void uploadToDatabase() {

        // Connect to database
        try (MongoClient mongoClient = MongoClients.create(uri)) {

            MongoDatabase database = mongoClient.getDatabase(this.database);
            MongoCollection<Document> collection = database.getCollection("vanilla_plus");

            List<String> elevatorBlocksList = Arrays.asList(
                "minecraft:purpur_block",
                "minecraft:purpur_pillar",
                "minecraft:purpur_slab",
                "minecraft:purpur_stairs"
            );
            List<String> extenderBlocksList = Arrays.asList(
                "minecraft:end_rod"
            );
            Map<String, String> generatorBlocksMap = Map.of(
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
            );

            Document configDoc = new Document().append("_id", "Config")
                    .append("elevatorBlocks", elevatorBlocksList)
                    .append("extenderBlocks", extenderBlocksList)
                    .append("elevatorRange", 8)
                    .append("generatorBlocks", generatorBlocksMap)
                    .append("cryingObsidianChance", 16);

            try {
                InsertOneResult result = collection.insertOne(configDoc);
                VanillaPlus.LOGGER.info("Config successfully imported into Database");
            } catch (MongoException me) {
                VanillaPlus.LOGGER.info("Could not load config: " + me);
                throw me;
            }

        } catch (Exception e) {
            VanillaPlus.LOGGER.info(e.toString());
        }
    }

}
