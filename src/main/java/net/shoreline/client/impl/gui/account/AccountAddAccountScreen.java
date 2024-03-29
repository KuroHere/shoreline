package net.shoreline.client.impl.gui.account;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.shoreline.client.api.account.Account;
import net.shoreline.client.api.account.AccountType;
import net.shoreline.client.init.Managers;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * @author xgraza
 * @since 03/28/24
 */
public final class AccountAddAccountScreen extends Screen {

    private final Screen parent;
    private TextFieldWidget email, password;

    public AccountAddAccountScreen(final Screen parent) {
        super(Text.of("Add or Create an Alt Account"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        clearChildren();

        addDrawableChild(email = new TextFieldWidget(client.textRenderer, width / 2 - 75, height / 2 - 30, 150, 20, Text.of("")));
        addDrawableChild(password = new TextFieldWidget(client.textRenderer, width / 2 - 75, height / 2 - 5, 150, 20, Text.of("")));

        email.setPlaceholder(Text.of("Email or Username..."));
        password.setPlaceholder(Text.of("Password (Optional)"));

        final String[] options = {"Login", "Add", "Login & Add", "Login via Browser", "Go Back"};
        for (int i = 0; i < options.length; ++i) {
            final String option = options[i];

            // i fucking HATE java!!
            int finalI = i;
            addDrawableChild(ButtonWidget.builder(Text.of(option), (button) -> onButtonAction(finalI))
                    .dimensions(width / 2 - 72, height / 2 + 20 + (i * 22), 145, 20)
                    .build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(client.textRenderer, "*",
                email.getX() - 10,
                email.getY() + (email.getHeight() / 2) - (client.textRenderer.fontHeight / 2),
                (email.getText().length() >= 3 ? Color.green : Color.red).getRGB());

        context.drawCenteredTextWithShadow(client.textRenderer,
                "Add an Account", width / 2, height / 2 - 120, -1);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (keyCode == GLFW_KEY_ESCAPE) {
            client.setScreen(parent);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void onButtonAction(final int id) {

        final String accountEmail = email.getText();
        final String accountPassword = password.getText();

        // nuh uh
        if (accountEmail.length() < 3 && id != 4) {
            return;
        }

        AccountType type = AccountType.CRACKED;

        // TODO: use proper checking (ex RegExp)
        if ((accountEmail.contains("@") && accountEmail.contains(".")) && !accountPassword.isEmpty()) {
            type = AccountType.MICROSOFT;
        }

        final Account account = new Account(type, accountEmail, accountPassword);

        if (id == 0) {
            account.login();
        } else if (id == 1 || id == 2) {
            Managers.ACCOUNT.register(account);
            client.setScreen(parent);

            if (id == 2) {
                account.login();
            }
        } else {
            client.setScreen(parent);
        }

    }
}
