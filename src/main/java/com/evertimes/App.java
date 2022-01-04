package com.evertimes;

import org.jetbrains.skija.*;
import org.jetbrains.skija.Canvas;
import org.jetbrains.skija.Paint;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Hello world!
 */
public class App {
    static boolean shouldRender = false;

    static void setShouldRender(boolean flag) {
        shouldRender = flag;
    }

    public static void main(String[] args) {
        var width = 500;
        var height = 500;
        // Create window
        glfwInit();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        long windowHandle = glfwCreateWindow(width, height, "RayTracer", NULL, NULL);
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(windowHandle);
        RayTracer tracer = new RayTracer(width, height);
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            setShouldRender(true);
            if (key == GLFW_KEY_W && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                tracer.incZView();
            } else if (key == GLFW_KEY_S && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                tracer.decZView();
            } else if (key == GLFW_KEY_A && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                tracer.decXView();
            } else if (key == GLFW_KEY_D && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                tracer.incXView();
            } else if (key == GLFW_KEY_SPACE && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                tracer.incYView();
            } else if (key == GLFW_KEY_LEFT_CONTROL && (action == GLFW_REPEAT || action == GLFW_PRESS)) {
                tracer.decYView();
            } else if (key == GLFW_KEY_RIGHT && (action == GLFW_REPEAT || action == GLFW_PRESS)){
                tracer.rotateRight();
            } else if (key == GLFW_KEY_LEFT && (action == GLFW_REPEAT || action == GLFW_PRESS)){
                tracer.rotateLeft();
            }
        });
        // Initialize OpenGL
        // Do once per app launch
        GL.createCapabilities();

        // Create Skia OpenGL context
        // Do once per app launch
        DirectContext context = DirectContext.makeGL();

        // Create render target, surface and retrieve canvas from it
        // .close() and recreate on window resize
        int fbId = GL11.glGetInteger(0x8CA6); // GL_FRAMEBUFFER_BINDING
        BackendRenderTarget renderTarget = BackendRenderTarget.makeGL(
                width,
                height,
                /*samples*/ 0,
                /*stencil*/ 8,
                fbId,
                FramebufferFormat.GR_GL_RGBA8);

        // .close() and recreate on window resize
        Surface surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getSRGB());

        // do not .close() — Surface manages its lifetime here
        Canvas canvas = surface.getCanvas();

        // Render loop
        glfwSwapBuffers(windowHandle); // wait for v-sync
        while (!glfwWindowShouldClose(windowHandle)) {
            if (shouldRender) {
                setShouldRender(false);
                int[][] array = tracer.getTracedArray();
                Paint paint = new Paint();
                paint.setStrokeWidth(1);
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        paint.setColor(array[i][j]);
                        canvas.drawPoint(i, j, paint);
                    }
                }
            }

            context.flush();
            glfwSwapBuffers(windowHandle); // wait for v-sync
            glfwPollEvents();
        }
    }
}
