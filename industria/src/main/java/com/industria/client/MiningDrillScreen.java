package com.industria.client;

import com.industria.blockentity.MiningDrillBE;
import com.industria.menu.MiningDrillMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MiningDrillScreen extends MachineScreen<MiningDrillMenu> {
    private Button toggle;
    private Button area;

    public MiningDrillScreen(MiningDrillMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 216);
        this.inventoryLabelY = 124;
    }

    @Override
    protected void init() {
        super.init();
        toggle = addButton(104, 18, 64, 14, Component.empty(), 0);
        area = addButton(104, 34, 64, 14, Component.empty(), 1);
        addButton(104, 50, 64, 14, Component.translatable("gui.industria.drill.restart"), 2);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        toggle.setMessage(Component.translatable("gui.industria.power", onOff(menu.isEnabled())));
        int size = menu.radius() * 2 + 1;
        area.setMessage(Component.translatable("gui.industria.drill.area", size, size));
        super.render(g, mouseX, mouseY, partialTick);
        tooltipIfHover(g, 8, 18, 10, 38,
                Component.translatable("gui.industria.energy", menu.energy(), MiningDrillBE.CAPACITY), mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        super.renderBg(g, partialTick, mouseX, mouseY);
        verticalBar(g, 8, 18, 10, 38, menu.energy(), MiningDrillBE.CAPACITY, 0xFFE03030, 0xFF3B1010);
        horizontalBar(g, 24, 54, 72, 5, menu.progressPercent(), 100, 0xFF40A0E0, 0xFF102030);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        g.drawString(font, Component.translatable("gui.industria.drill.layer", menu.currentY()), 24, 20, TEXT, false);

        Component state;
        if (menu.isFinished()) {
            state = Component.translatable("gui.industria.drill.done");
        } else if (!menu.isEnabled()) {
            state = Component.translatable("gui.industria.drill.stopped");
        } else if (menu.redstonePaused()) {
            state = Component.translatable("gui.industria.drill.redstone");
        } else if (menu.isFull()) {
            state = Component.translatable("gui.industria.drill.full");
        } else {
            state = Component.translatable("gui.industria.drill.working");
        }
        g.drawString(font, state, 24, 32, TEXT, false);
        g.drawString(font, Component.translatable("gui.industria.drill.output"), 24, 43, TEXT, false);
    }
}
