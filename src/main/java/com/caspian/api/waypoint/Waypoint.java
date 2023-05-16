package com.caspian.api.waypoint;

import com.caspian.api.config.Config;
import com.caspian.api.config.ConfigContainer;
import com.caspian.api.config.setting.NumberConfig;
import com.caspian.util.time.Timer;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Waypoint extends ConfigContainer
{
    //
    private final String ip;
    //
    private final Config<Double> xConfig = new NumberConfig<>("X", "X " +
            "position of waypoint.", 0.0D, 0.0D, Double.MAX_VALUE);
    private final Config<Double> yConfig = new NumberConfig<>("Y", "Y " +
            "position of waypoint.", 0.0D, 0.0D, Double.MAX_VALUE);
    private final Config<Double> zConfig = new NumberConfig<>("Z", "Z " +
            "position of waypoint.", 0.0D, 0.0D, Double.MAX_VALUE);
    //
    private final Timer timer;

    /**
     *
     *
     * @param name
     * @param ip
     * @param x
     * @param y
     * @param z
     */
    public Waypoint(String name, String ip, double x, double y, double z)
    {
        super(name);
        this.ip = ip;
        xConfig.setValue(x);
        yConfig.setValue(y);
        zConfig.setValue(z);
        this.timer = new Timer();
    }

    /**
     *
     *
     * @param time
     * @return
     */
    private boolean passedTime(long time)
    {
        return timer.passed(time);
    }

    /**
     *
     *
     * @return
     *
     *
     */
    public String getRef()
    {
        return String.format("%s_%s_waypoint", ip, getName().toLowerCase());
    }

    public String getIp()
    {
        return ip;
    }

    public double getX()
    {
        return xConfig.getValue();
    }

    public double getY()
    {
        return yConfig.getValue();
    }

    public double getZ()
    {
        return zConfig.getValue();
    }
}
