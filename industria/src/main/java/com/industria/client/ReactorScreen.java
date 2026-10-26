package com.industria.client;

import com.industria.blockentity.ReactorBE;
import com.industria.menu.ReactorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ReactorScreen extends MachineScreen<ReactorMenu> {
    private Button toggle;

    public ReactorScreen(ReactorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 206);
    }

    @Override
    protected void init() {
        super.init();
        addButton(34, 96, 22, 16, Component.literal("-"), 0);
        addButton(120, 96, 22, 16, Component.literal("+"), 1);
        toggle = addButton(58, 96, 60, 16, Component.empty(), 2);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        toggle.setMessage(onOff(menu.isEnabled()));
        super.render(g, mouseX, mouseY, partialTick);
        tooltipIfHover(g, 10, 20, 12, 70,
                Component.translatable("gui.industria.energy", menu.energy(), ReactorBE.CAPACITY), mouseX, mouseY);
        tooltipIfHover(g, 154, 20, 12, 70,
                Component.translatable("gui.industria.reactor.heat", menu.heat(), ReactorBE.MELTDOWN_HEAT), mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        super.renderBg(g, partialTick, mouseX, mouseY);
        verticalBar(g, 10, 20, 12, 70, menu.energy(), ReactorBE.CAPACITY, 0xFFE03030, 0xFF3B1010);
        double frac = menu.heat() / (double) ReactorBE.MELTDOWN_HEAT;
        int color = frac < 0.5 ? 0xFF40C040 : (frac < 0.75 ? 0xFFE0C030 : 0xFFE03030);
        verticalBar(g, 154, 20, 12, 70, menu.heat(), ReactorBE.MELTDOWN_HEAT, color, 0xFF1A2A1A);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        g.drawString(font, Component.translatable("gui.industria.reactor.heat", menu.heat(), ReactorBE.MELTDOWN_HEAT), 30, 48, TEXT, false);
        g.drawString(font, Component.translatable("gui.industria.reactor.power", menu.powerLevel()), 30, 58, TEXT, false);
        g.drawString(font, Component.translatable("gui.industria.reactor.coolant", menu.waterBlocks()), 30, 68, TEXT, false);

        Component status;
        int statusColor = 0x206020;
        if (menu.heat() >= 800) {
            status = Component.translatable("gui.industria.reactor.danger");
            statusColor = 0xB02020;
        } else if (menu.isRunning()) {
            status = Component.translatable("gui.industria.reactor.running", menu.activeRods());
        } else {
            status = Component.translatable("gui.industria.reactor.stopped");
            statusColor = 0x804020;
        }
        g.drawString(font, status, 30, 78, statusColor, false);
    }
}
