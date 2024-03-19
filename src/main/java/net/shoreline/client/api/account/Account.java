package net.shoreline.client.api.account;

import net.shoreline.client.api.account.microsoft.MicrosoftAuthenticator;
import net.shoreline.client.api.config.Config;
import net.shoreline.client.api.config.ConfigContainer;
import net.shoreline.client.api.config.setting.StringConfig;
import net.shoreline.client.util.Globals;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see AccountType
 * @see MicrosoftAuthenticator
 */
public class Account extends ConfigContainer implements Globals
{
    //
    Config<String> password = new StringConfig("Password", "Password login " +
            "field of the account.", "");
    //
    private final AccountType type;

    /**
     *
     *
     * @param type
     * @param username
     * @param password
     */
    public Account(AccountType type, String username, String password)
    {
        super(username);
        this.type = type;
        this.password.setValue(password);
    }

    /**
     *
     */
    public void login()
    {
//        switch (type)
//        {
//            case MICROSOFT -> Shoreline.EXECUTOR.execute(() ->
//            {
//                try
//                {
//                    setSession(AccountManager.MICROSOFT_AUTH.login(
//                            getName(), password.getValue()));
//                    Shoreline.info("Logged into MSA account {} named {}", getName(), session.getUsername());
//                } catch (MicrosoftAuthException | IOException e)
//                {
//                    Shoreline.error("Failed to login to account {}", getName());
//                    e.printStackTrace();
//                }
//            });
//            // case CRACKED -> setSession(new Session(getName(),
//            //        UUID.randomUUID().toString(), "",  Optional.empty(),
//            //        Optional.empty(), Session.AccountType.LEGACY));
//        }
    }

    protected void xboxLiveLogin()
    {

    }
    protected void xstsLogin()
    {

    }

    /**
     *
     * @return
     */
    public String getPassword()
    {
        return password.getValue();
    }

    /**
     *
     *
     * @return
     */
    public AccountType getType()
    {
        return type;
    }
}
