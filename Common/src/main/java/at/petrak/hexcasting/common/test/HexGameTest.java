package at.petrak.hexcasting.common.test;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.test.TestableAction;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class HexGameTest {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") //idk why this is a warning. it never explained it to me
    private static Optional<ServerPlayer> npc = Optional.empty();

    public static ServerPlayer getOrInitNpc(GameTestHelper ctx) {
        if (npc.isEmpty()) {
            npc = Optional.of(ctx.makeMockServerPlayerInLevel());
        }
        return npc.get();
    }

    public static CastingEnvironment createEnv(GameTestHelper ctx) {
        return createEnv(getOrInitNpc(ctx));
    }

    public static CastingEnvironment createEnv(ServerPlayer player) {
        var env = new StaffCastEnv(player, InteractionHand.MAIN_HAND);
        var ambitExtension = new CastingEnvironmentComponent.IsVecInRange() {

            @Override
            public boolean onIsVecInRange(Vec3 vec, boolean current) {
                return true;
            }

            @Override
            public Key<?> getKey() {
                return new Key<>() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                };
            }
        };
        env.addExtension(ambitExtension);

        var allowAllAccess = new CastingEnvironmentComponent.HasEditPermissionsAt() {
            @Override
            public boolean onHasEditPermissionsAt(BlockPos pos, boolean current) {
                return true;
            }

            @Override
            public Key<?> getKey() {
                return new Key<>() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                };
            }
        };
        env.addExtension(allowAllAccess);

        var freeMedia = new CastingEnvironmentComponent.ExtractMedia() {
            @Override
            public long onExtractMedia(long cost) {
                return 0;
            }

            @Override
            public Key<?> getKey() {
                return new Key<>() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                };
            }
        };
        env.addExtension(freeMedia);

        return env;
    }

    @GameTest(template = "hexcasting:test_focuscircle", batch = "hexcasting/circles")
    public void writeFocusCircle2(GameTestHelper ctx) {
        ctx.pressButton(1,3,0);
        ctx.succeedWhenEntityData(new BlockPos(1,2,1), EntityType.ITEM_FRAME,(it) -> {
            var holder = IXplatAbstractions.INSTANCE.findDataHolder(it);
            if (holder == null) return null;
            var iota = holder.readIota(ctx.getLevel());
            if (iota instanceof DoubleIota di) {
                return di.getDouble();
            }
            return null;
        },1.0);
    }

    @GameTest(template = "hexcasting:test_empty", batch = "hexcasting/iotas")
    public void testToleratesOtherPlayer(GameTestHelper ctx) {
        var iota = new EntityIota(getOrInitNpc(ctx));
        if (!iota.toleratesOther(iota)) throw new GameTestAssertException("Entity Iota does not tolerate it's self?");
        ctx.succeed();
    }

    @GameTestGenerator
    public Collection<TestFunction> generatePatternTests() {
        var output = new ArrayList<TestFunction>();
        var test =  new HashMap<ResourceLocation,ActionRegistryEntry>();
        HexActions.register(
                (are, rl) -> {
                    if (are.action() instanceof TestableAction) test.put(rl,are);
                }
        );

        for (Map.Entry<ResourceLocation,ActionRegistryEntry> entry : test.entrySet()) {
            var id = entry.getKey();
            var are = entry.getValue();
            output.addAll(((TestableAction) (are.action())).getTest(are.prototype(), id));

        }
        return output;
    }


}
