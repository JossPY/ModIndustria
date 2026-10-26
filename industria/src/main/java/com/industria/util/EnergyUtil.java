package com.industria.util;

import com.industria.blockentity.CableBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnergyUtil {
    /**
     * Envoie de l'energie aux voisins capables d'en recevoir.
     * Si la source est un cable, elle n'envoie qu'aux cables moins remplis (evite les allers-retours).
     */
    public static void push(Level level, BlockPos pos, ModEnergy src, int maxPerSide, boolean sourceIsCable) {
        for (Direction dir : Direction.values()) {
            if (src.getEnergyStored() <= 0) return;
            BlockEntity be = level.getBlockEntity(pos.relative(dir));
            if (be == null) continue;
            if (sourceIsCable && be instanceof CableBE other
                    && other.getEnergy().getEnergyStored() >= src.getEnergyStored()) {
                continue;
            }
            int sent = be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
                    .map(storage -> storage.canReceive()
                            ? storage.receiveEnergy(Math.min(maxPerSide, src.getEnergyStored()), false)
                            : 0)
                    .orElse(0);
            if (sent > 0) src.removeEnergyInternal(sent);
        }
    }
}
