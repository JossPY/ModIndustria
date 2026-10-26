package com.industria.menu;

import com.industria.blockentity.ReactorBE;
import com.industria.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ReactorMenu extends MachineMenu {
    public ReactorMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, inv.player.level().getBlockEntity(buf.readBlockPos()), new SimpleContainerData(ReactorBE.DATA_COUNT));
    }

    public ReactorMenu(int id, Inventory inv, BlockEntity be, ContainerData data) {
        super(ModMenus.REACTOR.get(), id, be, data, ReactorBE.ROD_SLOTS);
        IItemHandler rods = be instanceof ReactorBE r ? r.getRods() : new ItemStackHandler(ReactorBE.ROD_SLOTS);
        for (int i = 0; i < ReactorBE.ROD_SLOTS; i++) {
            addSlot(new SlotItemHandler(rods, i, 52 + i * 18, 24));
        }
        addPlayerSlots(inv, 8, 122);
    }

    public int heat() {
        return data.get(2);
    }

    public int powerLevel() {
        return data.get(3);
    }

    public boolean isEnabled() {
        return data.get(4) != 0;
    }

    public boolean isRunning() {
        return data.get(5) != 0;
    }

    public int activeRods() {
        return data.get(6);
    }

    public int waterBlocks() {
        return data.get(7);
    }
}
