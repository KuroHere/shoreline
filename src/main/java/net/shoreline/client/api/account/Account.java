package net.shoreline.client.api.account;

import net.minecraft.client.session.Session;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.account.microsoft.MicrosoftAuthException;
import net.shoreline.client.api.account.microsoft.MicrosoftAuthenticator;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.ConfigContainer;
import net.shoreline.client.api.config.setting.BooleanConfig;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.impl.manager.client.AccountManager;
import net.shoreline.client.mixin.accessor.AccessorMinecraftClient;
import net.shoreline.client.util.Globals;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author linus
 * @see MicrosoftAuthenticator
 * @since 1.0
 */
public class Account extends ConfigContainer implements Globals {

    private boolean premium;

    Config<String> password = new StringConfig("Password", "Password login " +
            "field of the account.", "");
    Config<String> email = new StringConfig("Email",
            "The email for this account", "");
    Config<String> username = new StringConfig("Username",
            "The cached username for this account", "");


    /**
     * @param email
     * @param password
     */
    public Account(String email, String password) {
        super(email);
        this.email.setValue(email);
        this.password.setValue(password);

        // TODO: email should be further vaildated with a Regex
        premium = email.contains("@") && !password.isEmpty();

        if (!premium) {
            username.setValue(email);
        }
    }

    /**
     *
     */
    public void login() {

        if (premium) {
            Shoreline.EXECUTOR.execute(() ->
            {
                try
                {
                    ((AccessorMinecraftClient) mc).setSession(AccountManager.MICROSOFT_AUTH.login(
                            getName(), password.getValue()));
                    setUsername(mc.getSession().getUsername());
                    Shoreline.info("Logged into MSA account {} named {}", getName(), mc.getSession().getUsername());
                } catch (MicrosoftAuthException | IOException e)
                {
                    Shoreline.error("Failed to login to account {}", getName());
                    e.printStackTrace();
                }
            });
        } else {
            ((AccessorMinecraftClient) mc).setSession(new Session(getName(),
                    UUID.randomUUID(), "", Optional.empty(),
                    Optional.empty(), Session.AccountType.LEGACY));
            setUsername(getName());
        }
    }

    public String getEmail() {
        return email.getValue();
    }

    /**
     * @return
     */
    public String getPassword() {
        return password.getValue();
    }

    public String getUsername() {
        return username.getValue();
    }

    public void setUsername(final String username) {
        this.username.setValue(username);
    }

    public String getUsernameOrEmail() {

        final String username = getUsername();
        if (username == null || username.isEmpty()) {
            return getEmail();
        }

        return username;
    }

    public boolean isUsernameSet() {
        return username.getValue() != null && !username.getValue().isEmpty();
    }

    public boolean isPremium() {
        return premium;
    }
}
