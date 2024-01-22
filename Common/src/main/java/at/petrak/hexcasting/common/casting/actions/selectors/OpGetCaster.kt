package at.petrak.hexcasting.common.casting.actions.selectors

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.test.ActionTest
import at.petrak.hexcasting.api.test.TestableAction
import at.petrak.hexcasting.common.test.HexGameTest
import net.minecraft.resources.ResourceLocation

object OpGetCaster : ConstMediaAction, TestableAction {
    override val argc = 0

    override fun execute(args: List<Iota>, ctx: CastingEnvironment): List<Iota> {
        if (ctx.caster == null)
            return null.asActionResult

        ctx.assertEntityInRange(ctx.caster)
        return ctx.caster.asActionResult
    }

    override fun getTest(pat: HexPattern, id: ResourceLocation): List<ActionTest> {
        return mutableListOf(
            ActionTest(pat,
                {listOf()},
                {listOf(EntityIota(HexGameTest.getOrInitNpc(it)))},
                id,0
            )
        )
    }
}
