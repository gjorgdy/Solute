package nl.gjorgdy.vanillaplus;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import nl.gjorgdy.vanillaplus.callbacks.FluidBlockCallback;
import nl.gjorgdy.vanillaplus.callbacks.PlayerJumpCallback;
import nl.gjorgdy.vanillaplus.callbacks.PlayerSneakCallback;
import nl.gjorgdy.vanillaplus.functions.CustomGenerator;
import nl.gjorgdy.vanillaplus.functions.Elevator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaPlus implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("VanillaPlus");
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Be prepared for your Quality of Life to be improved");

		// Assign Elevator
		Elevator elevator = new Elevator();
		elevator.registerBlock(Blocks.PURPUR_BLOCK);
		elevator.registerBlock(Blocks.PURPUR_PILLAR);
		elevator.registerBlock(Blocks.PURPUR_SLAB);
		elevator.registerBlock(Blocks.PURPUR_STAIRS);
		PlayerJumpCallback.EVENT.register(player -> {
			elevator.moveVertical(player, true);
			return ActionResult.SUCCESS;
		});
		PlayerSneakCallback.EVENT.register(player -> {
			elevator.moveVertical(player, false);
			return ActionResult.SUCCESS;
		});
		// Cobblegen
		CustomGenerator cobbleGen = new CustomGenerator();
		cobbleGen.registerBlock(Blocks.ANDESITE, Blocks.ANDESITE);
		cobbleGen.registerBlock(Blocks.GRANITE, Blocks.GRANITE);
		cobbleGen.registerBlock(Blocks.DIORITE, Blocks.DIORITE);
		cobbleGen.registerBlock(Blocks.TUFF, Blocks.TUFF);
		cobbleGen.registerBlock(Blocks.CALCITE, Blocks.CALCITE);
		cobbleGen.registerBlock(Blocks.DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
		cobbleGen.registerBlock(Blocks.COBBLED_DEEPSLATE, Blocks.COBBLED_DEEPSLATE);
		cobbleGen.registerBlock(Blocks.SANDSTONE, Blocks.SANDSTONE);
		cobbleGen.registerBlock(Blocks.SAND, Blocks.SANDSTONE);
		cobbleGen.registerBlock(Blocks.RED_SAND, Blocks.RED_SANDSTONE);
		FluidBlockCallback.EVENT.register((world, blockPos) -> {
			cobbleGen.replace(world, blockPos);
			return ActionResult.SUCCESS;
		});
	}
}
