package at.petrak.hexcasting.api.mod;

import at.petrak.hexcasting.api.misc.ManaConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class HexConfig {
    public static ForgeConfigSpec.IntValue dustManaAmount;
    public static ForgeConfigSpec.IntValue shardManaAmount;
    public static ForgeConfigSpec.IntValue chargedCrystalManaAmount;
    public static ForgeConfigSpec.DoubleValue manaToHealthRate;

    public HexConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Mana Amounts");
        dustManaAmount = builder.comment("How much mana a single Amethyst Dust item is worth")
            .defineInRange("dustManaAmount", ManaConstants.DUST_UNIT, 0, Integer.MAX_VALUE);
        shardManaAmount = builder.comment("How much mana a single Amethyst Shard item is worth")
            .defineInRange("shardManaAmount", ManaConstants.SHARD_UNIT, 0, Integer.MAX_VALUE);
        chargedCrystalManaAmount = builder.comment("How much mana a single Charged Amethyst Crystal item is worth")
            .defineInRange("chargedCrystalManaAmount", ManaConstants.CRYSTAL_UNIT, 0, Integer.MAX_VALUE);
        manaToHealthRate = builder.comment("How many points of mana a half-heart is worth when casting from HP")
            .defineInRange("manaToHealthRate", 2 * ManaConstants.CRYSTAL_UNIT / 20.0, 0.0, Double.POSITIVE_INFINITY);
        builder.pop();
    }

    public static class Client {
        public static ForgeConfigSpec.DoubleValue patternPointSpeedMultiplier;
        public static ForgeConfigSpec.BooleanValue ctrlTogglesOffStrokeOrder;

        public Client(ForgeConfigSpec.Builder builder) {
            patternPointSpeedMultiplier = builder.comment(
                    "How fast the point showing you the stroke order on patterns moves")
                .defineInRange("patternPointSpeed", 1.0, 0.0, Double.POSITIVE_INFINITY);
            ctrlTogglesOffStrokeOrder = builder.comment(
                    "Whether the ctrl key will instead turn *off* the color gradient on patterns")
                .define("ctrlTogglesOffStrokeOrder", false);
        }
    }

    public static class Server {
        public static ForgeConfigSpec.IntValue opBreakHarvestLevel;
        public static ForgeConfigSpec.IntValue maxRecurseDepth;

        public static ForgeConfigSpec.IntValue maxSpellCircleLength;

        public static ForgeConfigSpec.ConfigValue<List<? extends String>> actionDenyList;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("Spells");
            maxRecurseDepth = builder.comment("How many times a spell can recursively cast other spells")
                .defineInRange("maxRecurseDepth", 64, 0, Integer.MAX_VALUE);
            opBreakHarvestLevel = builder.comment(
                "The harvest level of the Break Block spell.",
                "0 = wood, 1 = stone, 2 = iron, 3 = diamond, 4 = netherite."
            ).defineInRange("opBreakHarvestLevel", 3, 0, 4);
            builder.pop();

            builder.push("Spell Circles");
            maxSpellCircleLength = builder.comment("The maximum number of slates in a spell circle")
                .defineInRange("maxSpellCircleLength", 256, 4, Integer.MAX_VALUE);
            builder.pop();

            actionDenyList = builder.comment(
                    "Resource locations of disallowed actions. Trying to cast one of these will result in a mishap.")
                .defineList("actionDenyList", List.of(),
                    obj -> obj instanceof String s && ResourceLocation.isValidResourceLocation(s));
        }


        /**
         * i'm not kidding look upon net.minecraftforge.common.TierSortingRegistry and weep
         */
        public static Tier getOpBreakHarvestLevelBecauseForgeThoughtItWasAGoodIdeaToImplementHarvestTiersUsingAnHonestToGodTopoSort() {
            return switch (opBreakHarvestLevel.get()) {
                case 0 -> Tiers.WOOD;
                case 1 -> Tiers.STONE;
                case 2 -> Tiers.IRON;
                case 3 -> Tiers.DIAMOND;
                case 4 -> Tiers.NETHERITE;
                default -> throw new RuntimeException("unreachable");
            };
        }
    }
}
