package com.caspian.client.util.string;

/**
 *
 *
 * @author bon55, linus
 * @since 1.0
 */
public class EnumFormatter
{
    /**
     * Formats an enum
     *
     * @param in The enum to format
     * @return The formatted enum
     */
    public static String formatEnum(final Enum<?> in)
    {
        String name = in.name();
        // no capitalization
        if (!name.contains("_"))
        {
            char firstChar = name.charAt(0);
            String suffixChars = name.split(String.valueOf(firstChar), 2)[1];
            return String.valueOf(firstChar).toUpperCase() + suffixChars.toLowerCase();
        }
        String[] names = name.split("_");
        StringBuilder nameToReturn = new StringBuilder();
        for (String n : names)
        {
            char firstChar = n.charAt(0);
            String suffixChars = n.split(String.valueOf(firstChar), 2)[1];
            nameToReturn.append(String.valueOf(firstChar).toUpperCase())
                    .append(suffixChars.toLowerCase());
        }
        return nameToReturn.toString();
    }

    /**
     * Capitalises a given string
     *
     * @param in The string to capitalise
     * @return The string with the first letter capitalised
     */
    public static String capitalise(final String in)
    {
        if (in.length() != 0)
        {
            return Character.toTitleCase(in.charAt(0)) + in.substring(1);
        }
        return "";
    }
}
