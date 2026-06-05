package io.github.mortuusars.envelope.world.entity.ai.goal;

import io.github.mortuusars.envelope.world.entity.Pigeon;
import io.github.mortuusars.envelope.world.mail.MailService;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.List;
public class PigeonLocateMailboxGoal extends Goal {
    protected final Pigeon pigeon;

    public PigeonLocateMailboxGoal(Pigeon pigeon) {
        this.pigeon = pigeon;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return MailService.operatesIn(pigeon.level())
              && !pigeon.isDelivering()
              && pigeon.canStartDelivery()
              && pigeon.getMailboxHandler().getLocateCooldown() <= 0
              && pigeon.getMailboxHandler().getTargetPos() == null
              && pigeon.level().getRandom().nextFloat() < 0.05;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        pigeon.getMailboxHandler().resetLocateCooldown();
        List<BlockPos> mailboxes = findNearbyAvailableMailboxes();
        if (!mailboxes.isEmpty()) {
            for (BlockPos pos : mailboxes) {
                if (!pigeon.getMailboxHandler().isTargetBlacklisted(pos)) {
                    pigeon.getMailboxHandler().setTargetPos(pos);
                    return;
                }
            }

            pigeon.getMailboxHandler().clearBlacklist();
            pigeon.getMailboxHandler().setTargetPos(mailboxes.getFirst());
        }
    }

    private List<BlockPos> findNearbyAvailableMailboxes() {
        Vec3 origin = pigeon.position();
        return MailService.of((ServerLevel) pigeon.level()).getMailboxes()
              .findNearbyAvailable((ServerLevel) pigeon.level(), origin, 20);
    }
}
