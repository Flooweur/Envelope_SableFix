package io.github.mortuusars.envelope.world.block;

import io.github.mortuusars.envelope.integration.sable.MovingStructureCompat;
import io.github.mortuusars.envelope.world.mail.MailService;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Pigeonholes {
    private final List<PigeonholeBlockEntity> entries = new ArrayList<>();

    public static Pigeonholes of(ServerLevel level) {
        return MailService.of(level).getPigeonholes();
    }

    public void register(PigeonholeBlockEntity blockEntity) {
        if (!entries.contains(blockEntity)) {
            entries.add(blockEntity);
        }
    }

    public void unregister(PigeonholeBlockEntity blockEntity) {
        entries.remove(blockEntity);
    }

    public List<PigeonholeBlockEntity> findWithinRange(ServerLevel level, Vec3 origin, double range) {
        List<PigeonholeBlockEntity> result = new ArrayList<>();
        for (Iterator<PigeonholeBlockEntity> iterator = entries.iterator(); iterator.hasNext(); ) {
            PigeonholeBlockEntity blockEntity = iterator.next();
            if (blockEntity.isRemoved()) {
                iterator.remove();
                continue;
            }
            if (MovingStructureCompat.isWithinRange(level, origin, blockEntity.getBlockPos(), range)) {
                result.add(blockEntity);
            }
        }
        return result;
    }
}
