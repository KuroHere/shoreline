/* November.lol © 2023 */
package com.caspian.client.api.account.microsoft.exception;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class MicroshitException extends Exception {

  /**
   * Creates a {@link MicroshitException}
   *
   * @param data the data of why something failed
   */
  public MicroshitException(String data) {
    super(data);
  }
}
