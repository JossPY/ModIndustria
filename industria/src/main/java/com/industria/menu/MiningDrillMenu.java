package com.industria.menu;

import com.industria.blockentity.MiningDrillBE;
import com.industria.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MiningDrillMenu extends MachineMenu {
    /** Slot de sortie : on peut seulement retirer des objets. */
    private static class OutputSlot extends SlotItemHandler {
        OutputSlot(IItemHandler handler, int index, int x, int y) {
            super(handler, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }

    public MiningDrillMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(MiningDrillBE.DATA_COUNT));
    }

    public MiningDrillMenu(int id, Inventory inv, BlockEntity be, ContainerData data) {
        super(ModMenus.MINING_DRILL.get(), id, be, data, MiningDrillBE.BUFFER_SLOTS);
        IItemHandler buffer = be instanceof MiningDrillBE d ? d.getBuffer() : new ItemStackHandler(MiningDrillBE.BUFFER_SLOTS);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new OutputSlot(buffer, col + row * 9, 8 + col * 18, 66 + row * 18));
            }
        }
        addPlayerSlots(inv, 8, 134);
    }

    public int currentY() {
        return data.get(2);
    }

    public boolean isEnabled() {
        return data.get(3) != 0;
    }

    public boolean isFinished() {
        return data.get(4) != 0;
    }

    public int radius() {
        return data.get(5);
    }

    public boolean isFull() {
        return data.get(6) != 0;
    }

    public int progressPercent() {
        return data.get(7);
    }

    public boolean redstonePaused() {
        return data.get(8) != 0;
    }
}
