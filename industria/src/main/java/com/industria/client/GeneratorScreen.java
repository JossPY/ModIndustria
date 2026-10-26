package com.industria.client;

import com.industria.blockentity.GeneratorBE;
import com.industria.menu.GeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GeneratorScreen extends MachineScreen<GeneratorMenu> {
    private Button toggle;

    public GeneratorScreen(GeneratorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title, 176, 166);
    }

    @Override
    protected void init() {
        super.init();
        toggle = addButton(70, 52, 70, 16, Component.empty(), 0);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        toggle.setMessage(Component.translatable("gui.industria.power", onOff(menu.isEnabled())));
        super.render(g, mouseX, mouseY, partialTick);
        tooltipIfHover(g, 148, 20, 14, 56,
                Component.translatable("gui.industria.energy", menu.energy(), GeneratorBE.CAPACITY), mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        super.renderBg(g, partialTick, mouseX, mouseY);
        verticalBar(g, 148, 20, 14, 56, menu.energy(), GeneratorBE.CAPACITY, 0xFFE03030, 0xFF3B1010);
        horizontalBar(g, 44, 58, 16, 4, menu.burnTime(), Math.max(1, menu.burnTotal()), 0xFFFF9020, 0xFF3B2A10);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        super.renderLabels(g, mouseX, mouseY);
        g.drawString(font, Component.translatable("gui.industria.output", GeneratorBE.PRODUCTION), 70, 24, TEXT, false);
        Component state = menu.burnTime() > 0
                ? Component.translatable("gui.industria.burning", menu.burnTime() / 20)
                : Component.translatable("gui.industria.idle");
        g.drawString(font, state, 70, 38, TEXT, false);
    }
}
