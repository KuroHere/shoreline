package com.momentum.impl.ui;

import net.minecraft.client.gui.GuiScreen;
<<<<<<< Updated upstream

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
=======
>>>>>>> Stashed changes

/**
 *
 */
public class ClickGuiScreen extends GuiScreen {

    @Override
    public void initGui() {

<<<<<<< Updated upstream
        // run events on all category frames
        for (Frame c : frames) {

            // run event
            c.draw(mouseX, mouseY);
        }

        // update mouse state
        MOUSE_LEFT_CLICK = false;
        MOUSE_RIGHT_CLICK = false;

        // update mouse position
        MOUSE_X = mouseX;
        MOUSE_Y = mouseY;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // left click
        if (mouseButton == 0) {

            // update mouse states
            MOUSE_LEFT_CLICK = true;
            MOUSE_LEFT_HOLD = true;
        }

        // right click
        else if (mouseButton == 1) {

            // update mouse states
            MOUSE_RIGHT_CLICK = true;
            MOUSE_RIGHT_HOLD = true;
        }

        // run events on all category frames
        for (Frame c : frames) {

            // run event
            c.click(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        // no longer holding mouse
        if (state == 0) {

            // update mouse states
            MOUSE_LEFT_HOLD = false;
            MOUSE_RIGHT_HOLD = false;

            // run events on all category frames
            for (Frame c : frames) {

                // release state
                c.release();
            }
        }
    }

    @Override
    protected void keyTyped(char p_keyTyped_1_, int p_keyTyped_2_) throws IOException {
        super.keyTyped(p_keyTyped_1_, p_keyTyped_2_);

        // run events on all category frames
        for (Frame c : frames) {

            // release state
            c.type(p_keyTyped_1_, p_keyTyped_2_);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        // turn off module
        Modules.CLICKGUI_MODULE.disable();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Gets the frames
     *
     * @return The frames
     */
    public Collection<Frame> getFrames() {
        return frames;
=======
>>>>>>> Stashed changes
    }
}
