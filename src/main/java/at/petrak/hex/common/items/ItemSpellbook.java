package at.petrak.hex.common.items;

import at.petrak.hex.common.casting.CastingContext;
import at.petrak.hex.common.casting.SpellDatum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class ItemSpellbook extends Item {
    public static String TAG_SELECTED_PAGE = "page_idx";
    // this is a CompoundTag of string numerical keys to SpellData\
    // it is 1-indexed, so that 0/0 can be the special case of "it is empty"
    public static String TAG_PAGES = "pages";

    public ItemSpellbook(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,
            TooltipFlag isAdvanced) {
        var tag = stack.getOrCreateTag();
        if (tag.contains(TAG_SELECTED_PAGE)) {
            var pageIdx = tag.getInt(TAG_SELECTED_PAGE);
            var pages = tag.getCompound(ItemSpellbook.TAG_PAGES);
            tooltip.add(new TranslatableComponent("hex.spellbook.tooltip.page", pageIdx, HighestPage(pages)));

            var key = String.valueOf(pageIdx);
            if (pages.contains(key)) {
                var datum = pages.getCompound(String.valueOf(pageIdx));
                // I know this is ugly i dont care
                tooltip.add(new TextComponent(datum.toString()));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        var tag = stack.getOrCreateTag();
        if (ArePagesEmpty(tag)) {
            tag.putInt(TAG_SELECTED_PAGE, 0);
        } else if (!tag.contains(TAG_SELECTED_PAGE)) {
            tag.putInt(TAG_SELECTED_PAGE, 1);
        } else {
            var pageIdx = tag.getInt(TAG_SELECTED_PAGE);
            if (pageIdx == 0) {
                tag.putInt(TAG_SELECTED_PAGE, 1);
            }
        }
    }

    public static boolean ArePagesEmpty(CompoundTag tag) {
        return !tag.contains(ItemSpellbook.TAG_PAGES) ||
                tag.getCompound(ItemSpellbook.TAG_PAGES).isEmpty();
    }

    public static void WriteDatum(CompoundTag tag, SpellDatum<?> datum) {
        int idx;
        if (tag.contains(TAG_SELECTED_PAGE)) {
            idx = tag.getInt(TAG_SELECTED_PAGE);
            // But we want to write to page *1* to start if this is our first page
            if (idx == 0 && ArePagesEmpty(tag)) {
                idx = 1;
            }
        } else {
            idx = 1;
        }
        var key = String.valueOf(idx);
        if (tag.contains(TAG_PAGES)) {
            tag.getCompound(TAG_PAGES).put(key, datum.serializeToNBT());
        } else {
            var pagesTag = new CompoundTag();
            pagesTag.put(key, datum.serializeToNBT());
            tag.put(TAG_PAGES, pagesTag);
        }
    }

    @Nullable
    public static SpellDatum<?> ReadDatum(CompoundTag tag, CastingContext ctx) {
        int idx;
        if (tag.contains(TAG_SELECTED_PAGE)) {
            idx = tag.getInt(TAG_SELECTED_PAGE);
        } else {
            idx = 0;
        }
        var key = String.valueOf(idx);
        if (tag.contains(TAG_PAGES)) {
            var pagesTag = tag.getCompound(TAG_PAGES);
            if (pagesTag.contains(key)) {
                return SpellDatum.DeserializeFromNBT(pagesTag.getCompound(key), ctx);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static int HighestPage(CompoundTag tag) {
        var highestKey = tag.getAllKeys().stream().flatMap(s -> {
            try {
                return Stream.of(Integer.parseInt(s));
            } catch (NumberFormatException e) {
                return Stream.empty();
            }
        }).max(Integer::compare);
        return highestKey.orElse(0);
    }

    public static void RotatePageIdx(CompoundTag tag, boolean increase) {
        int newIdx;
        if (ArePagesEmpty(tag)) {
            newIdx = 0;
        } else if (tag.contains(TAG_SELECTED_PAGE)) {
            var delta = increase ? 1 : -1;
            newIdx = Math.max(1, tag.getInt(TAG_SELECTED_PAGE) + delta);
        } else {
            newIdx = 1;
        }
        tag.putInt(TAG_SELECTED_PAGE, newIdx);
    }
}
