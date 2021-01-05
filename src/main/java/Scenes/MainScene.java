package Scenes;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import toodee.Camera;
import toodee.Scene;
import toodee.Window;
import toodee.renderer.Shader;
import toodee.renderer.Texture;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class MainScene extends Scene {

    private Shader defaultShader;
    private Texture testTexture;

    private float[] vertexArray = {
            // position               // color                      //UV Coordinates
            100.0f,   0.0f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,      1, 1,   // Bottom right 0
              0.0f, 100.0f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,      0, 0,   // Top left     1
            100.0f, 100.0f, 0.0f,      1.0f, 0.0f, 1.0f, 1.0f,      1, 0,   // Top right    2
              0.0f,   0.0f, 0.0f,      1.0f, 1.0f, 0.0f, 1.0f,      0, 1    // Bottom left  3
    };

    private int[] elementArray = {
            2, 1, 0, //Top Right Triangle
            0, 1, 3  // Bottom Left Triangle
    };

    private int vaoID, vboID, eboID;

    public MainScene() {


    }

    @Override
    public void init() {

        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();
        this.testTexture = new Texture("assets/textures/testImage.jpg");

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        System.out.println();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {

        defaultShader.use();

        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTexture.bind();

        glBindVertexArray(vaoID);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        defaultShader.detach();

    }
}
