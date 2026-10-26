package com.industria.blockentity;

import com.industria.util.ModEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

/** Machine avec une interface (GUI). Le clic droit ouvre le menu. */
public abstract class MenuMachineBE extends MachineBE implements MenuProvider {
    protected MenuMachineBE(BlockEntityType<?> type, BlockPos pos, BlockState state, ModEnergy energy) {
        super(type, pos, state, energy);
    }

    /** Appele cote serveur quand le joueur clique sur un bouton de l'interface. */
    public boolean handleButton(Player player, int id) {
        return false;
    }

    @Override
    public InteractionResult onUse(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, this, worldPosition);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }
}
