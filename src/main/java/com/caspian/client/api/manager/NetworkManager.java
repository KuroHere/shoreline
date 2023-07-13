package com.caspian.client.api.manager;

import com.caspian.client.mixin.accessor.AccessorClientWorld;
import com.caspian.client.util.Globals;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

import java.util.HashSet;
import java.util.Set;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class NetworkManager implements Globals
{
    //
    private final Set<Packet<?>> cache = new HashSet<>();

    /**
     * 
     * 
     * @param p
     */
    public void sendPacket(final Packet<?> p)
    {
        cache.add(p);
        mc.player.networkHandler.sendPacket(p);
    }

    /**
     *
     *
     * @param p
     */
    public void sendSequencedPacket(final SequencedPacketCreator p)
    {
        if (mc.world != null)
        {
            PendingUpdateManager updater =
                    ((AccessorClientWorld) mc.world).hookGetPendingUpdateManager()
                            .incrementSequence();
            try
            {
                int i = updater.getSequence();
                Packet<ServerPlayPacketListener> packet = p.predict(i);
                sendPacket(packet);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                if (updater != null)
                {
                    try
                    {
                        updater.close();
                    }
                    catch (Throwable e1)
                    {
                        e1.printStackTrace();
                        e.addSuppressed(e1);
                    }
                }
                throw e;
            }
            if (updater != null)
            {
                updater.close();
            }
        }
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public boolean isCached(Packet<?> p)
    {
        return cache.remove(p);
    }
}
