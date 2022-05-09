package at.petrak.hexcasting.common.recipe;

import at.petrak.hexcasting.api.item.DataHolderItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.HexItems;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.recipe.ingredient.UnsealedIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class SealSpellbookRecipe extends ShapelessRecipe {
    public static final SimpleRecipeSerializer<SealSpellbookRecipe> SERIALIZER =
        new SimpleRecipeSerializer<>(SealSpellbookRecipe::new);

    private static ItemStack getSealedStack() {
        ItemStack output = new ItemStack(HexItems.SPELLBOOK::get);
        ItemSpellbook.SetSealed(output, true);
        NBTHelper.putString(output, DataHolderItem.TAG_OVERRIDE_VISUALLY, "any");
        return output;
    }

    private static NonNullList<Ingredient> createIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.createWithCapacity(2);
        ingredients.add(UnsealedIngredient.of(new ItemStack(HexItems.SPELLBOOK::get)));
        ingredients.add(Ingredient.of(Items.HONEYCOMB));
        return ingredients;
    }

    public SealSpellbookRecipe(ResourceLocation id) {
        super(id, "", getSealedStack(), createIngredients());
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer inv) {
        ItemStack out = ItemStack.EMPTY;

        for (int i = 0; i < inv.getContainerSize(); i++) {
            var stack = inv.getItem(i);
            if (stack.is(HexItems.SPELLBOOK.get())) {
                out = stack.copy();
                break;
            }
        }

        if (!out.isEmpty()) {
            ItemSpellbook.SetSealed(out, true);
            out.setCount(1);
        }

        return out;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }
}

