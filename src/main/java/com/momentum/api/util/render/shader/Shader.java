package com.momentum.api.util.render.shader;

import com.momentum.Momentum;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * GLSL Shader implementation for {@link ARBShaderObjects} and
 * {@link org.lwjgl.opengl.GL20}
 *
 * @author linus
 * @since 03/27/2023
 */
public class Shader
{
    // shaders impl state
    private final boolean gl21;
    private final boolean arb;

    // program shader which vert and frag are attached to
    private int program;

    // vertex and fragment shader GL20 ids
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
                program = OpenGlHelper.glCreateProgram();
                vert = OpenGlHelper.glCreateShader(GL20.GL_VERTEX_SHADER);
                frag = OpenGlHelper.glCreateShader(GL20.GL_FRAGMENT_SHADER);

                // check null, 0 equates to null state
                if (vert != GL11.GL_FALSE && frag != GL11.GL_FALSE)
                {
                    // compile exception
                    try
                    {
                        // compile shaders
                        GL20.glShaderSource(vert, resource + vertex);
                        GL20.glShaderSource(frag, resource + fragment);
                        OpenGlHelper.glCompileShader(vert);
                        OpenGlHelper.glCompileShader(frag);

                        // check compile status
                        if (OpenGlHelper.glGetShaderi(vert,
                                GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE
                                || OpenGlHelper.glGetShaderi(frag,
                                GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
                        {
                            throw new RuntimeException("Could not compile shader!");
                        }
                    }

                    // couldn't compile shader
                    catch (Exception e)
                    {
                        // delete shaders
                        OpenGlHelper.glDeleteShader(vert);
                        OpenGlHelper.glDeleteShader(frag);
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
        if (program != GL11.GL_FALSE && vert != GL11.GL_FALSE
                && frag != GL11.GL_FALSE)
        {
            OpenGlHelper.glAttachShader(program, vert);
            OpenGlHelper.glAttachShader(program, frag);
            OpenGlHelper.glLinkProgram(program);

            // check program link status
            if (OpenGlHelper.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
            {
                throw new RuntimeException("Could not link shader!");
            }

            glValidateProgram(program);
        }

        // OpenGL context
        ContextCapabilities context = GLContext.getCapabilities();
        gl21 = OpenGlHelper.openGL21;
        arb = context.GL_ARB_vertex_shader
            && context.GL_ARB_fragment_shader
            && context.GL_ARB_shader_objects;
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
        GL20.glUseProgram(program);
    }

    /**
     * Unbinds the shader program
     */
    public void unbind()
    {
        // use no program
        GL20.glUseProgram(0);
    }

    /**
     * {@link ARBShaderObjects#glValidateProgramARB(int)} override with
     * implementation for {@link org.lwjgl.opengl.GL20}
     *
     * @param program The program id
     */
    private void glValidateProgram(int program)
    {
        // arb shaders
        if (arb)
        {
            ARBShaderObjects.glValidateProgramARB(program);
        }

        // GL20
        else if (gl21)
        {
            GL20.glValidateProgram(program);
        }
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
