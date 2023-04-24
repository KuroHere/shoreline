package com.caspian.api.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see Account
 */
public class AccountManager
{
    //
    private final List<Account> accounts = new ArrayList<>();

    /**
     *
     *
     * @param account
     */
    public void register(Account account)
    {
        accounts.add(account);
    }

    /**
     *
     *
     * @param accounts
     *
     * @see #register(Account)
     */
    public void register(Account... accounts)
    {
        for (Account account : accounts)
        {
            register(account);
        }
    }

    /**
     *
     *
     * @return
     */
    public Collection<Account> getAccounts()
    {
        return accounts;
    }
}
