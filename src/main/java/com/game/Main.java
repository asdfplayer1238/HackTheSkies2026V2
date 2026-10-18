package com.game;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final double PLAYER_SIZE = 32.0;
    private static final double PLAYER_SPEED = 240.0;

    public static void main(String[] args) {
        GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);
        errorCallback.set();
        if (!glfwInit()) {
            glfwSetErrorCallback(null);
            errorCallback.free();
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        long window = NULL;
        try {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "WASD Player Movement", NULL, NULL);
            if (window == NULL) {
                throw new IllegalStateException("Unable to create GLFW window");
            }

            glfwMakeContextCurrent(window);
            glfwSwapInterval(1);
            glfwShowWindow(window);
            GL.createCapabilities();

            Player player = new Player(
                (WINDOW_WIDTH - PLAYER_SIZE) / 2.0,
                (WINDOW_HEIGHT - PLAYER_SIZE) / 2.0
            );
            double previousFrameTime = glfwGetTime();

            while (!glfwWindowShouldClose(window)) {
                double currentFrameTime = glfwGetTime();
                double deltaTime = currentFrameTime - previousFrameTime;
                previousFrameTime = currentFrameTime;

                int[] framebufferSize = getFramebufferSize(window);
                int width = framebufferSize[0];
                int height = framebufferSize[1];
                if (width > 0 && height > 0) {
                    movePlayer(window, player, width, height, deltaTime);
                    render(width, height, player);
                    glfwSwapBuffers(window);
                }

                glfwPollEvents();
            }
        } finally {
            if (window != NULL) {
                glfwDestroyWindow(window);
            }
            glfwTerminate();
            glfwSetErrorCallback(null);
            errorCallback.free();
        }
    }

    private static int[] getFramebufferSize(long window) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            glfwGetFramebufferSize(window, width, height);
            return new int[]{width.get(0), height.get(0)};
        }
    }

    private static void movePlayer(long window, Player player, int width, int height, double deltaTime) {
        double movementX = 0.0;
        double movementY = 0.0;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) movementX -= 1.0;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) movementX += 1.0;
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) movementY -= 1.0;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) movementY += 1.0;

        if (movementX != 0.0 || movementY != 0.0) {
            double magnitude = Math.sqrt(movementX * movementX + movementY * movementY);
            player.x += movementX / magnitude * PLAYER_SPEED * deltaTime;
            player.y += movementY / magnitude * PLAYER_SPEED * deltaTime;
        }

        player.x = Math.max(0.0, Math.min(player.x, width - PLAYER_SIZE));
        player.y = Math.max(0.0, Math.min(player.y, height - PLAYER_SIZE));
    }

    private static void render(int width, int height, Player player) {
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glClearColor(0.08f, 0.10f, 0.14f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(0.2f, 0.75f, 1.0f);
        glBegin(GL_QUADS);
        glVertex2d(player.x, player.y);
        glVertex2d(player.x + PLAYER_SIZE, player.y);
        glVertex2d(player.x + PLAYER_SIZE, player.y + PLAYER_SIZE);
        glVertex2d(player.x, player.y + PLAYER_SIZE);
        glEnd();
    }

    private static final class Player {
        private double x;
        private double y;

        private Player(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
