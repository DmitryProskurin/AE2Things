package io.github.projectet.ae2things.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record DiskCellInfo(String uuid, long storedItemsCount) {

    public static DiskCellInfo DEFAULT = new DiskCellInfo("", 0);

    public Boolean isDefault() { return this == DEFAULT; }

    public UUID uuidAsUUID() {
        return UUID.fromString(uuid);
    }

    public static final Codec<DiskCellInfo> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("diskuuid").forGetter(DiskCellInfo::uuid),
                    Codec.LONG.fieldOf("items_count").forGetter(DiskCellInfo::storedItemsCount)
            ).apply(instance, DiskCellInfo::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, DiskCellInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            DiskCellInfo::uuid,
            ByteBufCodecs.VAR_LONG,
            DiskCellInfo::storedItemsCount,
            DiskCellInfo::new);
}
