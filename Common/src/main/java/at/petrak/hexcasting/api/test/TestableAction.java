package at.petrak.hexcasting.api.test;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TestableAction {
    List<ActionTest> getTest(@NotNull HexPattern pat, @NotNull ResourceLocation id);
}
