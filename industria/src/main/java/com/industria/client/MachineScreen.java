package com.industria.client;

import com.industria.menu.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** Ecran de base dessine par code (aucune texture requise). */
public abstract class MachineScreen<T extends MachineMenu> extends AbstractContainerScreen<T> {
    protected static final int TEXT = 0x404040;

    protected MachineScreen(T menu, Inventory inv, Component title, int width, int height) {
        super(menu, inv, title);
        this.imageWidth = width;
        this.imageHeight = height;
        this.inventoryLabelY = height - 94;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFF000000);
        g.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFFC6C6C6);
        g.fill(x + 1, y + 1, x + imageWidth - 1, y + 3, 0xFFFFFFFF);
        g.fill(x + 1, y + 1, x + 3, y + imageHeight - 1, 0xFFFFFFFF);
        g.fill(x + 1, y + imageHeight - 3, x + imageWidth - 1, y + imageHeight - 1, 0xFF555555);
        g.fill(x + imageWidth - 3, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFF555555);

        for (Slot s : menu.slots) {
            int sx = x + s.x - 1;
            int sy = y + s.y - 1;
            g.fill(sx, sy, sx + 18, sy + 18, 0xFFFFFFFF);
            g.fill(sx, sy, sx + 17, sy + 17, 0xFF373737);
            g.fill(sx + 1, sy + 1, sx + 17, sy + 17, 0xFF8B8B8B);
        }
    }

    /** Barre verticale (rx, ry relatifs a l'ecran). */
    protected void verticalBar(GuiGraphics g, int rx, int ry, int w, int h, long value, long max, int color, int emptyColor) {
        int ax = leftPos + rx;
        int ay = topPos + ry;
        g.fill(ax - 1, ay - 1, ax + w + 1, ay + h + 1, 0xFF000000);
        g.fill(ax, ay, ax + w, ay + h, emptyColor);
        int fill = max > 0 ? (int) Math.min(h, h * value / max) : 0;
        if (fill > 0) g.fill(ax, ay + h - fill, ax + w, ay + h, color);
    }

    /** Barre horizontale. */
    protected void horizontalBar(GuiGraphics g, int rx, int ry, int w, int h, long value, long max, int color, int emptyColor) {
        int ax = leftPos + rx;
        int ay = topPos + ry;
        g.fill(ax - 1, ay - 1, ax + w + 1, ay + h + 1, 0xFF000000);
        g.fill(ax, ay, ax + w, ay + h, emptyColor);
        int fill = max > 0 ? (int) Math.min(w, w * value / max) : 0;
        if (fill > 0) g.fill(ax, ay, ax + fill, ay + h, color);
    }

    protected void tooltipIfHover(GuiGraphics g, int rx, int ry, int w, int h, Component text, int mouseX, int mouseY) {
        if (isHovering(rx, ry, w, h, mouseX, mouseY)) {
            g.renderTooltip(font, text, mouseX, mouseY);
        }
    }

    protected Button addButton(int rx, int ry, int w, int h, Component label, int buttonId) {
        return addRenderableWidget(Button.builder(label, b ->
                        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, buttonId))
                .bounds(leftPos + rx, topPos + ry, w, h).build());
    }

    protected static Component onOff(boolean on) {
        return Component.translatable(on ? "gui.industria.on" : "gui.industria.off");
    }
}
