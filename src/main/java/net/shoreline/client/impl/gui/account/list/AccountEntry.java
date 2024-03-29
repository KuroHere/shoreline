package net.shoreline.client.impl.gui.account.list;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.shoreline.client.api.account.Account;
import net.shoreline.client.util.Globals;
import net.shoreline.client.util.render.TextureDownloader;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

/**
 * @author xgraza
 * @since 03/28/24
 */
public class AccountEntry extends AlwaysSelectedEntryListWidget.Entry<AccountEntry> implements Globals {

    private static final TextureDownloader FACE_DOWNLOADER = new TextureDownloader();

    private final Account account;
    private long lastClickTime = -1;

    public AccountEntry(Account account) {
        this.account = account;
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

        context.drawTextWithShadow(mc.textRenderer,
                Text.of(account.getUsername()),
                x + 20, y + (entryHeight / 2) - (mc.textRenderer.fontHeight / 2), -1);

        {
            final String searchFor = account.isUsernameSet()
                    ? account.getUsername()
                    : account.getName();
            final String id = "face_" + searchFor.toLowerCase();

            if (!FACE_DOWNLOADER.exists(id)) {
                if (!FACE_DOWNLOADER.isDownloading(id)) {
                    FACE_DOWNLOADER.downloadTexture(id,
                            "https://minotar.net/helm/" + searchFor + "/15", false);
                }

                return;
            }

            final Identifier texture = FACE_DOWNLOADER.get(id);
            if (texture != null) {
                context.drawTexture(texture, x + 2, y + 2, 0, 0, 15, 15, 15, 15);
            }

        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW_MOUSE_BUTTON_1) {

            // fuck this game
            final long time = System.currentTimeMillis() - lastClickTime;
            if (time > 0L && time < 500L) {
                account.login();
            }

            lastClickTime = System.currentTimeMillis();
            return false;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public Text getNarration() {
        return Text.of(account.getUsername());
    }

    public Account getAccount() {
        return account;
    }
}
