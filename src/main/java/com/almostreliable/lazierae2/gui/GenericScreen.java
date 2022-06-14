package com.almostreliable.lazierae2.gui;

import com.almostreliable.lazierae2.content.GenericMenu;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Collection;

public abstract class GenericScreen<M extends GenericMenu<?>> extends AbstractContainerScreen<M> {

    private final Collection<AbstractWidget> toRender = new ArrayList<>();

    GenericScreen(
        M menu, Inventory inventory
    ) {
        super(menu, inventory, menu.entity.getDisplayName());
    }

    @Override
    public void render(PoseStack matrix, int mX, int mY, float partial) {
        renderBackground(matrix);
        super.render(matrix, mX, mY, partial);
        renderTooltip(matrix, mX, mY);
    }

    @Override
    protected void renderTooltip(PoseStack stack, int mX, int mY) {
        super.renderTooltip(stack, mX, mY);

        // widget tooltips
        for (var widget : toRender) {
            if (widget.isHoveredOrFocused() && widget.visible) {
                widget.renderToolTip(stack, mX, mY);
            }
        }
    }

    @Override
    public boolean isHovering(int pX, int pY, int pWidth, int pHeight, double pMouseX, double pMouseY) {
        // make this public for usage in widgets
        return super.isHovering(pX, pY, pWidth, pHeight, pMouseX, pMouseY);
    }

    void addRenderable(AbstractWidget widget) {
        addRenderableWidget(widget);
        toRender.add(widget);
    }

    void addRenderables(AbstractWidget... widgets) {
        for (var widget : widgets) {
            addRenderable(widget);
        }
    }
}
