package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;

public class VampirismEntityTagProvider extends EntityTypeTagsProvider {
    public VampirismEntityTagProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerTags() {
        getBuilder(ModTags.Entities.HUNTER).add(ModEntities.hunter, ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob, ModEntities.hunter_imob, ModEntities.hunter_trainer, ModEntities.hunter_trainer_dummy, ModEntities.task_master_hunter);
        getBuilder(ModTags.Entities.VAMPIRE).add(ModEntities.vampire, ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob, ModEntities.vampire_imob, ModEntities.vampire_baron, ModEntities.task_master_vampire, ModEntities.converted_creature, ModEntities.converted_creature_imob, ModEntities.villager_converted, ModEntities.converted_sheep, ModEntities.converted_horse, ModEntities.vampire_minion);
        getBuilder(ModTags.Entities.ADVANCED_HUNTER).add(ModEntities.advanced_hunter, ModEntities.advanced_hunter_imob);
        getBuilder(ModTags.Entities.ADVANCED_VAMPIRE).add(ModEntities.advanced_vampire, ModEntities.advanced_vampire_imob);
    }
}
