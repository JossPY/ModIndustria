package com.industria.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.industria.blockentity.MenuMachineBE;
import org.jetbrains.annotations.Nullable;

/** Base commune des menus : slots du joueur, shift-clic, donnees synchronisees. */
public abstract class MachineMenu extends AbstractContainerMenu {
    @Nullable
    protected final BlockEntity blockEntity;
    protected final ContainerData data;
    protected final int machineSlots;

    protected MachineMenu(MenuType<?> type, int id, @Nullable BlockEntity be, ContainerData data, int machineSlots) {
        super(type, id);
        this.blockEntity = be;
        this.data = data;
        this.machineSlots = machineSlots;
        addDataSlots(data);
    }

    /** A appeler APRES avoir ajoute les slots de la machine. */
    protected void addPlayerSlots(Inventory inv, int x, int y) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, x + col * 18, y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, x + col * 18, y + 58));
        }
    }

    public int energy() {
        return (data.get(0) & 0xFFFF) | (data.get(1) << 16);
    }

    public BlockPos pos() {
        return blockEntity != null ? blockEntity.getBlockPos() : BlockPos.ZERO;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null || blockEntity.isRemoved()) return false;
        BlockPos p = blockEntity.getBlockPos();
        return player.distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (blockEntity instanceof MenuMachineBE machine) {
            return machine.handleButton(player, id);
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int machineEnd = machineSlots;
        int invEnd = machineSlots + 36;

        if (index < machineEnd) {
            if (!moveItemStackTo(stack, machineEnd, invEnd, true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, machineEnd, false)) {
            if (index < machineEnd + 27) {
                if (!moveItemStackTo(stack, machineEnd + 27, invEnd, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack, machineEnd, machineEnd + 27, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == copy.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return copy;
    }
}
