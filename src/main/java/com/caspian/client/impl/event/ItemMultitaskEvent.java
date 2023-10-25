package com.caspian.client.impl.event;

import com.caspian.client.api.event.Cancelable;
import com.caspian.client.api.event.Event;

/**
 * Allows mining and eating at the same time
 *
 * @see com.caspian.client.mixin.MixinMinecraftClient
 */
@Cancelable
public class ItemMultitaskEvent extends Event
{

}
