package io.github.projectet.ae2things.definitions;

import io.github.projectet.ae2things.AE2Things;
import io.github.projectet.ae2things.storage.DiskCellInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

import static io.github.projectet.ae2things.storage.DiskCellInfo.*;

public final class AE2TComponents
{
    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, AE2Things.MOD_ID);

    public static final DataComponentType<DiskCellInfo> DISK_CELL_INFO =
            register(
                    "disk_cell_info",
                    builder -> builder.persistent(CODEC).networkSynchronized(STREAM_CODEC));

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
        return componentType;
    }
}
