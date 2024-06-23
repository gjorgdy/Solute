package nl.gjorgdy.vanillaplus.utils;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityUtils {

    public static Entity[] fromIdList(World world, int[] ids) {
        Entity[] entities = new Entity[ids.length];
        for (int i = 0 ; i < ids.length ; i++ ) {
            entities[i] = world.getEntityById(ids[i]);
        }
        return entities;
    }

    public static int[] toIdList(Entity[] entities) {
        int[] id = new int[entities.length];
        for (int i = 0 ; i < entities.length ; i++ ) {
            id[i] = entities[i].getId();
        }
        return id;
    }

}
