package com.industria.blockentity;

import com.industria.menu.ReactorMenu;
import com.industria.registry.ModBlockEntities;
import com.industria.registry.ModItems;
import com.industria.util.EnergyUtil;
import com.industria.util.ModEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Containers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reacteur nucleaire.
 * - 4 emplacements de barres de combustible
 * - Niveau de puissance reglable (0 a 100 %) : production, chaleur et usure des barres proportionnelles
 * - Refroidissement : 0.35 par bloc d'eau (source) adjacent + dissipation naturelle
 * - Interrupteur marche/arret + signal redstone = arret d'urgence
 * - Chaleur >= 1000 : FUSION (explosion)
 */
public class ReactorBE extends MenuMachineBE {
    public static final int ROD_SLOTS = 4;
    public static final int FE_PER_ROD = 120;
    public static final double HEAT_PER_ROD = 0.4;
    public static final double COOL_PER_WATER = 0.35;
    public static final double AMBIENT_COOLING = 0.001;
    public static final int MELTDOWN_HEAT = 1000;
    public static final double RADIATION_HEAT = 600.0;
    public static final int CAPACITY = 200000;
    public static final int DATA_COUNT = 8;

    private final ItemStackHandler rods = new ItemStackHandler(ROD_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModItems.URANIUM_FUEL_ROD.get());
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> rods);

    private double heat = 0.0;
    private double wear = 0.0;
    private int powerLevel = 100;
    private boolean enabled = true;
    private int activeRods = 0;
    private int waterBlocks = 0;
    private boolean running = false;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored() & 0xFFFF;
                case 1 -> energy.getEnergyStored() >>> 16;
                case 2 -> (int) heat;
                case 3 -> powerLevel;
                case 4 -> enabled ? 1 : 0;
                case 5 -> running ? 1 : 0;
                case 6 -> activeRods;
                case 7 -> waterBlocks;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public ReactorBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REACTOR.get(), pos, state, new ModEnergy(CAPACITY, 0, 1000));
    }

    public ItemStackHandler getRods() {
        return rods;
    }

    @Override
    public void serverTick() {
        Level lvl = level;
        boolean powered = lvl.hasNeighborSignal(worldPosition);
        boolean roomForEnergy = energy.getEnergyStored() + FE_PER_ROD * ROD_SLOTS <= energy.getMaxEnergyStored();
        running = enabled && !powered && powerLevel > 0 && roomForEnergy;
        double scale = powerLevel / 100.0;

        int active = 0;
        if (running) {
            wear += scale;
            int damage = (int) wear;
            wear -= damage;
            for (int i = 0; i < ROD_SLOTS; i++) {
                ItemStack stack = rods.getStackInSlot(i);
                if (!stack.is(ModItems.URANIUM_FUEL_ROD.get())) continue;
                active++;
                if (damage > 0) {
                    stack.setDamageValue(stack.getDamageValue() + damage);
                    if (stack.getDamageValue() >= stack.getMaxDamage()) {
                        rods.setStackInSlot(i, new ItemStack(ModItems.DEPLETED_FUEL_ROD.get()));
                    }
                }
            }
        }
        activeRods = active;

        energy.addEnergyInternal((int) (active * FE_PER_ROD * scale));
        heat += active * HEAT_PER_ROD * scale;

        int water = 0;
        for (Direction dir : Direction.values()) {
            FluidState fluid = lvl.getFluidState(worldPosition.relative(dir));
            if (fluid.is(FluidTags.WATER) && fluid.isSource()) water++;
        }
        waterBlocks = water;
        heat -= water * COOL_PER_WATER;
        heat -= heat * AMBIENT_COOLING;
        if (heat < 0) heat = 0;

        if (heat >= MELTDOWN_HEAT) {
            meltdown();
            return;
        }

        if (heat >= RADIATION_HEAT && lvl.getGameTime() % 40 == 0) {
            AABB zone = new AABB(worldPosition).inflate(10);
            for (Player p : lvl.getEntitiesOfClass(Player.class, zone)) {
                p.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
            }
        }

        EnergyUtil.push(lvl, worldPosition, energy, 1000, false);
        if (lvl.getGameTime() % 20 == 0) setChanged();
    }

    private void meltdown() {
        Level lvl = level;
        BlockPos pos = worldPosition;
        lvl.removeBlock(pos, false);
        lvl.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                12.0f, Level.ExplosionInteraction.BLOCK);
    }

    @Override
    public boolean handleButton(Player player, int id) {
        switch (id) {
            case 0 -> powerLevel = Math.max(0, powerLevel - 10);
            case 1 -> powerLevel = Math.min(100, powerLevel + 10);
            case 2 -> enabled = !enabled;
            default -> {
                return false;
            }
        }
        setChanged();
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ReactorMenu(id, inv, this, data);
    }

    @Override
    public void dropContents() {
        if (level == null) return;
        for (int i = 0; i < ROD_SLOTS; i++) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),
                    worldPosition.getZ(), rods.getStackInSlot(i));
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCap.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Rods", rods.serializeNBT());
        tag.putDouble("Heat", heat);
        tag.putInt("PowerLevel", powerLevel);
        tag.putBoolean("Enabled", enabled);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        rods.deserializeNBT(tag.getCompound("Rods"));
        heat = tag.getDouble("Heat");
        powerLevel = tag.contains("PowerLevel") ? tag.getInt("PowerLevel") : 100;
        enabled = !tag.contains("Enabled") || tag.getBoolean("Enabled");
    }
}
