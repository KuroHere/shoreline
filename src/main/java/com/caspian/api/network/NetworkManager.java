package com.caspian.api.network;

import com.caspian.asm.accessor.AccessorClientWorld;
import com.caspian.util.Globals;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class NetworkManager implements Globals
{
    /**
     * 
     * 
     * @param p
     */
    public void sendPacket(Packet<?> p) 
    {
        
    }
    
    public void sendSequencedPacket(SequencedPacketCreator p) 
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
}
