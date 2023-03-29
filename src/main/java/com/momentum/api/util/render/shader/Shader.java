package com.momentum.api.util.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.momentum.Momentum;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL32C;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * GLSL Shader implementation for {@link GL32C}
 *
 * @author linus
 * @since 03/27/2023
 */
public class Shader
{
    // program shader which vert and frag are attached to
    private int program;

    // vertex and fragment shader GL32C ids
    private int vert;
    private int frag;

    // map of all samplers
    protected Map<String, Integer> samplers;

    /**
     * Creates the shader ids based on the vertex and fragment shaders
     *
     * @param vertex The vertex shader
     * @param fragment The fragment shader
     */
    public Shader(String vertex, String fragment)
    {
        // catches creation exceptions
        try
        {
            // shader resource path
            String resource = "/assets/momentum/shaders/";

            // vert and frag shader resources
            InputStream vertStream =
                    getClass().getResourceAsStream(resource + vertex);
            InputStream fragStream =
                    getClass().getResourceAsStream(resource + fragment);

            // check resources exists
            if (vertStream != null && fragStream != null)
            {
                // create ids
                program = GL32C.glCreateProgram();
                vert = GlStateManager.glCreateShader(GL32C.GL_VERTEX_SHADER);
                frag = GlStateManager.glCreateShader(GL32C.GL_FRAGMENT_SHADER);

                // check null, 0 equates to null state
                if (vert != GL32C.GL_FALSE && frag != GL32C.GL_FALSE)
                {
                    // compile exception
                    try
                    {
                        // compile shaders
                        GL32C.glShaderSource(vert, resource + vertex);
                        GL32C.glShaderSource(frag, resource + fragment);
                        GlStateManager.glCompileShader(vert);
                        GlStateManager.glCompileShader(frag);

                        // check compile status
                        if (GlStateManager.glGetShaderi(vert,
                                GL32C.GL_COMPILE_STATUS) == GL32C.GL_FALSE
                                || GlStateManager.glGetShaderi(frag,
                                GL32C.GL_COMPILE_STATUS) == GL32C.GL_FALSE)
                        {
                            throw new RuntimeException("Could not compile shader!");
                        }
                    }

                    // couldn't compile shader
                    catch (Exception e)
                    {
                        // delete shaders
                        GlStateManager.glDeleteShader(vert);
                        GlStateManager.glDeleteShader(frag);
                        e.printStackTrace();
                    }
                }
            }

            // close streams
            IOUtils.closeQuietly(vertStream, fragStream);
        }

        // creation error
        catch (Exception e)
        {
            Momentum.LOGGER.error("Could not load shader!");
            e.printStackTrace();
        }

        // check null, 0 equates to null state
        if (program != GL32C.GL_FALSE && vert != GL32C.GL_FALSE
                && frag != GL32C.GL_FALSE)
        {
            GlStateManager.glAttachShader(program, vert);
            GlStateManager.glAttachShader(program, frag);
            GlStateManager.glLinkProgram(program);

            // check program link status
            if (GlStateManager.glGetProgrami(program, GL32C.GL_LINK_STATUS) == GL32C.GL_FALSE)
            {
                throw new RuntimeException("Could not link shader!");
            }

            GL32C.glValidateProgram(program);
        }
    }

    /**
     * Binds the shader program
     */
    public void bind()
    {
        // init samplers
        if (samplers == null)
        {
            samplers = new HashMap<>();
        }

        // use program id
        GL32C.glUseProgram(program);
    }

    /**
     * Unbinds the shader program
     */
    public void unbind()
    {
        // use no program
        GL32C.glUseProgram(0);
    }

    /**
     *
     * @param id
     * @return
     */
    public Integer getSampler(String id)
    {
        return samplers.get(id);
    }
}
