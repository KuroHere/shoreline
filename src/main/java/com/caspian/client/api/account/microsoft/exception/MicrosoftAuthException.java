/* November.lol © 2023 */
package com.caspian.client.api.account.microsoft.exception;

/**
 * @author Gavin
 * @since 1.0
 */
public class MicrosoftAuthException extends Exception
{
  /**
   * Creates a {@link MicrosoftAuthException}
   *
   * @param data the data of why something failed
   */
  public MicrosoftAuthException(String data)
  {
    super(data);
  }
}
