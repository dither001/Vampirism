package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.lib.util.WeightedRandomItem;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.DefaultMinionTask;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static de.teamlapen.vampirism.entity.minion.management.CollectResourcesTask.Desc;


public class CollectResourcesTask extends DefaultMinionTask<Desc> {

    private final int coolDown;
    private final List<WeightedRandomItem<ItemStack>> resources;
    private final Random rng = new Random();
    @Nullable
    private final IPlayableFaction<?> faction;


    /**
     * @param faction If given, only available to this faction
     */
    public CollectResourcesTask(@Nullable IPlayableFaction<?> faction, int coolDown, List<WeightedRandomItem<ItemStack>> resources) {
        this.coolDown = coolDown;
        this.resources = resources;
        this.faction = faction;
    }

    @Override
    public Desc activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, IMinionInventory inventory) {
        if (minion != null) {
            minion.recallMinion();
        }
        if (lord != null)
            lord.sendStatusMessage(new TranslationTextComponent(Util.makeTranslationKey("minion_task", getRegistryName()) + ".start"), true);
        return new Desc(this, this.coolDown);
    }

    @Override
    public void deactivateTask(Desc desc) {

    }

    @Override
    public boolean isAvailable(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
        return this.faction == null || this.faction == faction;
    }

    @Override
    public Desc readFromNBT(CompoundNBT nbt) {
        return new Desc(this, nbt.getInt("cooldown"));
    }

    @Override
    public void tickBackground(Desc desc, @Nonnull IMinionInventory inventory) {
        if (--desc.coolDown <= 0) {
            desc.coolDown = coolDown;
            inventory.addItemStack(WeightedRandom.getRandomItem(rng, resources).getItem().copy());
        }
    }

    public static class Desc implements IMinionTask.IMinionTaskDesc {
        private final CollectResourcesTask task;
        private int coolDown;

        public Desc(CollectResourcesTask task, int coolDown) {
            this.task = task;
            this.coolDown = coolDown;
        }

        @Override
        public IMinionTask<?> getTask() {
            return task;
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            nbt.putInt("cooldown", coolDown);
        }
    }
}
