package com.caspian.client.asm.accessor;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import net.minecraft.network.encryption.SignatureVerifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 *
 *
 * @author linus
 * @since 1.0
 *
 * @see MinecraftClient
 */
@Mixin(MinecraftClient.class)
public interface AccessorMinecraftClient
{
    /**
     *
     *
     * @return
     */
    @Accessor("authenticationService")
    YggdrasilAuthenticationService getAuthenticationService();

    /**
     *
     *
     * @param apiService
     */
    @Accessor
    void setUserApiService(UserApiService apiService);

    /**
     *
     *
     * @param session
     */
    @Accessor("session")
    void setSession(Session session);

    /**
     *
     *
     * @param profileKeys
     */
    @Accessor("profileKeys")
    void setProfileKeys(ProfileKeys profileKeys);

    /**
     *
     *
     * @param socialInteractionsManager
     */
    @Accessor("socialInteractionsManager")
    void setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);

    /**
     *
     *
     * @param authenticationService
     */
    @Accessor("authenticationService")
    void setAuthenticationService(YggdrasilAuthenticationService authenticationService);

    /**
     *
     *
     * @param servicesSignatureVerifier
     */
    @Accessor("servicesSignatureVerifier")
    void setServicesSignatureVerifier(SignatureVerifier servicesSignatureVerifier);

    /**
     *
     * @param skinProvider
     */
    @Accessor("skinProvider")
    void setSkinProvider(PlayerSkinProvider skinProvider);

    /**
     *
     *
     * @param sessionService
     */
    @Accessor("sessionService")
    void setSessionService(MinecraftSessionService sessionService);

    /**
     *
     *
     * @param abuseReportContext
     */
    @Accessor("abuseReportContext")
    void setAbuseReportContext(AbuseReportContext abuseReportContext);

    /**
     *
     *
     * @param itemUseCooldown
     */
    @Accessor("itemUseCooldown")
    void hookSetItemUseCooldown(int itemUseCooldown);

    /**
     *
     *
     * @return
     */
    @Accessor("itemUseCooldown")
    int hookGetItemUseCooldown();
}
