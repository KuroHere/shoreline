package net.shoreline.client.api.account;

import net.minecraft.client.session.Session;
import net.shoreline.client.Shoreline;
import net.shoreline.client.api.account.microsoft.MicrosoftAuthException;
import net.shoreline.client.api.account.microsoft.MicrosoftAuthenticator;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.ConfigContainer;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.impl.manager.client.AccountManager;
import net.shoreline.client.mixin.accessor.AccessorMinecraftClient;
import net.shoreline.client.util.Globals;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * @author linus
 * @see AccountType
 * @see MicrosoftAuthenticator
 * @since 1.0
 */
public class Account extends ConfigContainer implements Globals {
    //
    private final AccountType type;

    //
    Config<String> password = new StringConfig("Password", "Password login " +
            "field of the account.", "");
    Config<String> username = new StringConfig("Username",
            "The Minecraft username for this account", "");

    /**
     * @param type
     * @param username
     * @param password
     */
    public Account(AccountType type, String username, String password) {
        super(username);
        this.type = type;
        this.password.setValue(password);
    }

    /**
     *
     */
    public void login() {
        switch (type)
        {
            case MICROSOFT -> Shoreline.EXECUTOR.execute(() ->
            {
                try
                {
                    ((AccessorMinecraftClient) mc).setSession(AccountManager.MICROSOFT_AUTH.login(
                            getName(), password.getValue()));
                    username.setValue(mc.getSession().getUsername());
                    Shoreline.info("Logged into MSA account {} named {}", getName(), mc.getSession().getUsername());
                } catch (MicrosoftAuthException | IOException e)
                {
                    Shoreline.error("Failed to login to account {}", getName());
                    e.printStackTrace();
                }
            });
             case CRACKED -> {
                 ((AccessorMinecraftClient) mc).setSession(new Session(getName(),
                         UUID.randomUUID(), "", Optional.empty(),
                         Optional.empty(), Session.AccountType.LEGACY));
                 username.setValue(getName());
             }
        }
    }

    /**
     * @return
     */
    public String getPassword() {
        return password.getValue();
    }

    /**
     * @return
     */
    public AccountType getType() {
        return type;
    }

    public String getUsername() {
        final String value = username.getValue();
        if (value == null || value.isEmpty()) {
            return getName();
        }
        return value;
    }

    public boolean isUsernameSet() {
        return username.getValue() != null && !username.getValue().isEmpty();
    }
}
