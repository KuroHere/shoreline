package net.shoreline.client.impl.manager.client;

import net.minecraft.client.session.Session;
import net.shoreline.client.api.account.msa.MSAAuthenticator;
import net.shoreline.client.api.account.type.MinecraftAccount;
import net.shoreline.client.mixin.accessor.AccessorMinecraftClient;
import net.shoreline.client.util.Globals;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xgraza
 * @see MinecraftAccount,
 * @since 03/31/24
 */
public final class AccountManager implements Globals
{
    // The Microsoft authenticator
    public static final MSAAuthenticator MSA_AUTHENTICATOR = new MSAAuthenticator();
    private final List<MinecraftAccount> accounts = new ArrayList<>();

    /**
     * @param account
     */
    public void register(MinecraftAccount account)
    {
        accounts.add(account);
    }

    /**
     * @param accounts
     * @see #register(MinecraftAccount)
     */
    public void register(final MinecraftAccount... accounts)
    {
        for (final MinecraftAccount account : accounts)
        {
            register(account);
        }
    }

    /**
     *
     * @param account
     */
    public void unregister(final MinecraftAccount account)
    {
        accounts.remove(account);
    }

    public void setSession(final Session session)
    {
        ((AccessorMinecraftClient) mc).setSession(session);
    }

    /**
     * @return
     */
    public List<MinecraftAccount> getAccounts()
    {
        return accounts;
    }
}
