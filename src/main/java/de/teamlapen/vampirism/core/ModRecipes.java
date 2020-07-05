package de.teamlapen.vampirism.core;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.inventory.recipes.*;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Map;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all recipe registrations and reference.
 */
@SuppressWarnings("unused")
public class ModRecipes {
    public static final IRecipeType<IWeaponTableRecipe> WEAPONTABLE_CRAFTING_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "weapontable_crafting").toString());
    public static final IRecipeType<AlchemicalCauldronRecipe> ALCHEMICAL_CAULDRON_TYPE = IRecipeType.register(new ResourceLocation(REFERENCE.MODID, "alchemical_cauldron").toString());

    @ObjectHolder(REFERENCE.MODID + ":shaped_crafting_weapontable")
    public static final IRecipeSerializer<ShapedWeaponTableRecipe> shaped_crafting_weapontable = getNull();
    @ObjectHolder(REFERENCE.MODID + ":shapeless_crafting_weapontable")
    public static final IRecipeSerializer<ShapelessWeaponTableRecipe> shapeless_crafting_weapontable = getNull();
    @ObjectHolder(REFERENCE.MODID + ":repair_iitemwithtier")
    public static final IRecipeSerializer<ShapedRecipe> repair_iitemwithtier = getNull();
    @ObjectHolder(REFERENCE.MODID + ":alchemical_cauldron")
    public static final IRecipeSerializer<AlchemicalCauldronRecipe> alchemical_cauldron = getNull();

    public static final IConditionSerializer<?> CONFIG_CONDITION = CraftingHelper.register(new ConfigCondition.Serializer());

    private static final Map<Item, Integer> liquidColors = Maps.newHashMap();

    static void registerDefaultLiquidColors() {
        registerLiquidColor(ModItems.holy_water_bottle_normal, 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_enhanced, 0x6666FF);
        registerLiquidColor(ModItems.holy_water_bottle_ultimate, 0x6666FF);

        //registerLiquidColor(ModTags.Items.GARLIC, 0xBBBBBB); TODO 1.16 use some kind of datapack load event to (re-)register stuff or find a different solution

    }

    static void registerSerializer(IForgeRegistry<IRecipeSerializer<?>> registry) {
        registry.register(new ShapedWeaponTableRecipe.Serializer().setRegistryName(REFERENCE.MODID, "shaped_crafting_weapontable"));
        registry.register(new ShapelessWeaponTableRecipe.Serializer().setRegistryName(REFERENCE.MODID, "shapeless_crafting_weapontable"));
        registry.register(new ShapedItemWithTierRepair.Serializer().setRegistryName(REFERENCE.MODID, "repair_iitemwithtier"));
        registry.register(new AlchemicalCauldronRecipe.Serializer().setRegistryName(REFERENCE.MODID, "alchemical_cauldron"));
    }

    public static void registerLiquidColor(Item item, int color) {
        liquidColors.put(item, color);
    }

    public static void registerLiquidColor(ITag<Item> items, int color) {
        items.func_230236_b_/*getAllElements*/().forEach(item -> liquidColors.put(item, color));
    }


    /**
     * gets liquid color for item
     */
    public static int getLiquidColor(Item stack) {
        return liquidColors.getOrDefault(stack, 0x505050);
    }

}
