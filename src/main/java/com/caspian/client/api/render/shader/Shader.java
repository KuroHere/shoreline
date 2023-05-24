package com.caspian.client.api.render.shader;

import com.caspian.client.Caspian;
import org.lwjgl.opengl.GL32C;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 * @author linus
 * @since 1.0
 */
public class Shader
{
    // program which vert and frag are attached to
    private int program;

    // vertex and fragment shader GL32C ids
    private int vert;
    private int frag;

    // map of all samplers
    private final Map<String, Integer> samplers = new HashMap<>();

    /**
     * Compiles a {@link GL32C} shader given the file name of the vertex and
     * fragment <tt>GLSL</tt> shader files. The shader is attached and linked
     * to a generated program id.
     *
     * @param vertex The vertex shader
     * @param fragment The fragment shader
     *
     * @see GL32C#glCompileShader(int)
     * @see GL32C#glAttachShader(int, int)
     * @see GL32C#glLinkProgram(int)
     */
    public Shader(String vertex, String fragment)
    {
        try
        {
            String resourcePath = "/assets/caspian/shader/";
            InputStream vertStream =
                    getClass().getResourceAsStream(resourcePath + vertex);
            InputStream fragStream =
                    getClass().getResourceAsStream(resourcePath + fragment);

            if (vertStream != null && fragStream != null)
            {
                program = GL32C.glCreateProgram();
                vert = GL32C.glCreateShader(GL32C.GL_VERTEX_SHADER);
                frag = GL32C.glCreateShader(GL32C.GL_FRAGMENT_SHADER);

                // 0 equates to null state
                if (vert != GL32C.GL_FALSE && frag != GL32C.GL_FALSE)
                {
                    try (vertStream; fragStream)
                    {
                        GL32C.glShaderSource(vert, resourcePath + vertex);
                        GL32C.glShaderSource(frag, resourcePath + fragment);
                        GL32C.glCompileShader(vert);
                        GL32C.glCompileShader(frag);

                        // Shader compile status
                        if (GL32C.glGetShaderi(vert, GL32C.GL_COMPILE_STATUS) == GL32C.GL_FALSE
                                || GL32C.glGetShaderi(frag, GL32C.GL_COMPILE_STATUS) == GL32C.GL_FALSE) {
                            throw new RuntimeException("Could not compile shader");
                        }
                    }

                    catch (Exception e)
                    {
                        Caspian.error("Could not compile shader %s", fragment);
                        e.printStackTrace();
                        GL32C.glDeleteShader(vert);
                        GL32C.glDeleteShader(frag);
                    }
                }
            }
        }

        // creation error
        catch (Exception e)
        {
            Caspian.error("Could not load shader %s", fragment);
            e.printStackTrace();
        }

        // check null, 0 equates to null state
        if (program != GL32C.GL_FALSE && vert != GL32C.GL_FALSE && frag != GL32C.GL_FALSE)
        {
            GL32C.glAttachShader(program, vert);
            GL32C.glAttachShader(program, frag);
            GL32C.glLinkProgram(program);

            // program link status
            if (GL32C.glGetProgrami(program, GL32C.GL_LINK_STATUS) == GL32C.GL_FALSE)
            {
                throw new RuntimeException("Could not link shader!");
            }

            GL32C.glValidateProgram(program);
        }
    }

    /**
     *
     */
    public void bind()
    {
        GL32C.glUseProgram(program);
    }

    /**
     *
     */
    public void unbind()
    {
        GL32C.glUseProgram(0);
    }

    /**
     *
     *
     * @param id
     * @return
     */
    public Integer getSampler(String id)
    {
        return samplers.get(id);
    }
}
