package com.almostreliable.lazierae2.gui.control;

import com.almostreliable.lazierae2.content.processor.ProcessorEntity;
import com.almostreliable.lazierae2.core.TypeEnums.BLOCK_SIDE;
import com.almostreliable.lazierae2.core.TypeEnums.IO_SETTING;
import com.almostreliable.lazierae2.core.TypeEnums.TRANSLATE_TYPE;
import com.almostreliable.lazierae2.gui.ProcessorScreen;
import com.almostreliable.lazierae2.gui.widgets.GenericButton;
import com.almostreliable.lazierae2.network.PacketHandler;
import com.almostreliable.lazierae2.network.packets.SideConfigPacket;
import com.almostreliable.lazierae2.util.GuiUtil.Tooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;

import java.util.ArrayList;
import java.util.Collection;

public final class IOControl {

    private static final int BUTTON_SIZE = 6;
    private static final int INNER_SIZE = 4;
    private static final String TEXTURE_ID = "io";

    private IOControl() {}

    @SuppressWarnings("SameParameterValue")
    public static IOButton[] setup(ProcessorScreen screen, int x, int y) {
        Collection<IOButton> buttons = new ArrayList<>();
        buttons.add(new IOButton(screen, BLOCK_SIDE.TOP, x + getPosition(1), y + getPosition(0)));
        buttons.add(new IOButton(screen, BLOCK_SIDE.LEFT, x + getPosition(0), y + getPosition(1)));
        buttons.add(new IOButton(screen, BLOCK_SIDE.FRONT, x + getPosition(1), y + getPosition(1)));
        buttons.add(new IOButton(screen, BLOCK_SIDE.RIGHT, x + getPosition(2), y + getPosition(1)));
        buttons.add(new IOButton(screen, BLOCK_SIDE.BOTTOM, x + getPosition(1), y + getPosition(2)));
        buttons.add(new IOButton(screen, BLOCK_SIDE.BACK, x + getPosition(2), y + getPosition(2)));
        return buttons.toArray(new IOButton[0]);
    }

    private static int getPosition(int offset) {
        return offset * BUTTON_SIZE;
    }

    private static final class IOButton extends GenericButton {

        private final BLOCK_SIDE side;
        private final ProcessorEntity entity;
        private final Tooltip tooltip;

        private IOButton(ProcessorScreen screen, BLOCK_SIDE side, int pX, int pY) {
            super(screen, pX, pY, BUTTON_SIZE, BUTTON_SIZE, TEXTURE_ID);
            this.side = side;
            entity = screen.getMenu().entity;
            tooltip = Tooltip.builder()
                .title("io.title")
                .blank()
                .keyEnum("io.side", TRANSLATE_TYPE.BLOCK_SIDE, () -> side)
                .keyEnum("io.current", TRANSLATE_TYPE.IO_SETTING, () -> entity.sideConfig.get(side))
                .blank()
                .line(Screen::hasShiftDown, "io.description")
                .blank(Screen::hasShiftDown)
                .shiftForInfo()
                .conditional(extendedInfo -> extendedInfo.condition(() -> side == BLOCK_SIDE.FRONT)
                    .then(Tooltip.builder().shiftClickAction("io.reset_all"))
                    .otherwise(Tooltip.builder().clickAction("io.action").shiftClickAction("io.reset")));
        }

        @Override
        public void renderButton(PoseStack stack, int mX, int mY, float partial) {
            RenderSystem.setShaderTexture(0, texture);
            blit(stack, x, y, 0, 0, BUTTON_SIZE, BUTTON_SIZE, getTextureWidth(), height);
            blit(
                stack,
                x + 1,
                y + 1,
                BUTTON_SIZE + INNER_SIZE * (float) entity.sideConfig.get(side).ordinal(),
                0,
                INNER_SIZE,
                INNER_SIZE,
                getTextureWidth(),
                height
            );
        }

        @Override
        protected void clickHandler() {
            changeMode();
            PacketHandler.CHANNEL.sendToServer(new SideConfigPacket(entity.sideConfig));
        }

        @Override
        protected int getTextureWidth() {
            return width + 4 * INNER_SIZE;
        }

        @Override
        protected int getTextureHeight() {
            return height;
        }

        @Override
        public void playDownSound(SoundManager manager) {
            if (side == BLOCK_SIDE.FRONT && !Screen.hasShiftDown()) return;
            super.playDownSound(manager);
        }

        @Override
        public void renderToolTip(PoseStack stack, int mX, int mY) {
            screen.renderComponentTooltip(stack, tooltip.build(), mX, mY);
        }

        private void changeMode() {
            var config = entity.sideConfig;
            var setting = config.get(side);

            if (Screen.hasShiftDown()) {
                if (side == BLOCK_SIDE.FRONT) {
                    config.reset();
                    return;
                }
                setting = IO_SETTING.OFF;
            } else {
                if (side == BLOCK_SIDE.FRONT) return;
                setting = switch (setting) {
                    case INPUT -> IO_SETTING.OUTPUT;
                    case OUTPUT -> IO_SETTING.IO;
                    case IO -> IO_SETTING.OFF;
                    case OFF -> IO_SETTING.INPUT;
                };
            }

            config.set(side, setting);
        }
    }
}
