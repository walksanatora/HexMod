package at.petrak.hexcasting.api.test

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.test.HexGameTest
import net.minecraft.gametest.framework.GameTestAssertException
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.gametest.framework.TestFunction
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

class ActionTest(
    var pattern: HexPattern,
    var inputStack: Function<GameTestHelper, List<Iota>>,
    var expectedStack: Function<GameTestHelper, List<Iota>>,
    id: ResourceLocation,
    index: Int
) :
    TestFunction(
        "hexcasting/action",  // batch
        "hexgametest.%s_%s%s".format(id.namespace, id.path, index),  //name
        "hexcasting:test_empty",  //structure
        10,  //max ticks
        0,  //how many ticks to setup,
        true,  //must pass?
        { } //test (ignored)
    ) {
    override fun run(ctx: GameTestHelper) {
        val env = HexGameTest.createEnv(ctx)
        val image = CastingImage()
        val vm = CastingVM(image, env)
        val stackedImage = image.copy(
            stack = inputStack.apply(ctx)
        )
        vm.image = stackedImage


        vm.queueExecuteAndWrapIota(PatternIota(pattern), ctx.level)

        val pe_stack = vm.image.stack
        val expected = expectedStack.apply(ctx)
        if (pe_stack.size != expected.size) throw GameTestAssertException(
            "Post execution stack and expected stack differ in size %s vs %s".format(
                pe_stack.size,
                expected.size
            )
        )

        for (i in expected.indices) {
            val left = expected[i]
            val right = pe_stack[i]
            if (!Iota.tolerates(left, right)) throw GameTestAssertException(
                "Iota %s does not tolerate %s at index %d"
                    .format(left, right, i)
            )
        }
        ctx.succeed()
    }
}