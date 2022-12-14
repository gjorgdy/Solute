package nl.gjorgdy.vanillaplus.blocks;

import net.minecraft.block.Block;

public class IndestructibleBlock extends Block {

    public IndestructibleBlock(Settings settings) {
        super(settings.strength(-1.0F, 3600000.0F));
    }

}
