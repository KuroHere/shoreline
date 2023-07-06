package com.caspian.client.util.string;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class StringUtil
{
    public static final String EMPTY_STRING = "";

    /**
     *
     *
     * @param string
     * @return
     */
    public static String capitalize(final String string)
    {
        if (string.length() != 0)
        {
            return Character.toTitleCase(string.charAt(0)) + string.substring(1);
        }
        return EMPTY_STRING;
    }
}
