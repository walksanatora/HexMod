package at.petrak.hexcasting.common.casting.actions.stack

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapShameOnYou
import at.petrak.hexcasting.api.test.ActionTest
import at.petrak.hexcasting.api.test.TestableAction
import net.minecraft.resources.ResourceLocation

object OpDuplicateN : ConstMediaAction, TestableAction {
    override val argc: Int
        get() = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val count = args.getPositiveInt(1, argc)

        if (count > 1000) {
            throw MishapShameOnYou()
        }

        return (List(count) { args[0] })
    }

    override fun getTest(pat: HexPattern, id: ResourceLocation): List<ActionTest> = listOf(
        ActionTest(pat,{listOf(DoubleIota(3.0),DoubleIota(1.0))}, {listOf(DoubleIota(3.0))},id,0),
        ActionTest(pat,{listOf(DoubleIota(1.0),DoubleIota(3.0))}, {listOf(DoubleIota(1.0),DoubleIota(1.0),DoubleIota(1.0))},id,1)
    )
}
