package net.shoreline.client.impl.manager.client;

import net.shoreline.client.api.account.Account;
import net.shoreline.client.api.account.microsoft.MicrosoftAuthenticator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linus
 * @see Account
 * @since 1.0
 */
public class AccountManager {
    // The Microsoft authenticator
    public static final MicrosoftAuthenticator MICROSOFT_AUTH =
            new MicrosoftAuthenticator();
    //
    private final List<Account> accounts = new ArrayList<>();

    /**
     * @param account
     */
    public void register(Account account) {
        accounts.add(account);
    }

    /**
     * @param accounts
     * @see #register(Account)
     */
    public void register(Account... accounts) {
        for (Account account : accounts) {
            register(account);
        }
    }

    /**
     *
     * @param account
     */
    public void unregister(final Account account) {
        accounts.remove(account);
    }

    /**
     * @return
     */
    public List<Account> getAccounts() {
        return accounts;
    }
}
