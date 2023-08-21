package com.caspian.client.api.render;

public interface Hideable
{
    /**
     *
     * @param drawn
     */
    void setHidden(boolean hidden);

    /**
     *
     * @return
     */
    boolean isHidden();
}
