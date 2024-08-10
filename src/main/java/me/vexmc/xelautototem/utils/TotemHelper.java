package me.vexmc.xelautototem.utils;

import me.vexmc.xelautototem.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.awt.*;
import java.util.Random;

public class TotemHelper {

    private static boolean listeningForNextDraw = false;
    private static int totemX = -1;
    private static int totemY = -1;
    private static final Random random = new Random();

    public static void onInventoryScreenOpen() {
        listeningForNextDraw = true;
        totemX = -1;
        totemY = -1;
    }

    public static boolean isListeningForTotemSlot() {
        return listeningForNextDraw;
    }

    public static void stopListeningForTotemSlot() {
        listeningForNextDraw = false;
    }

    public static void setTotemSlotCoordinates(int x, int y) {
        int randomOffsetX = random.nextInt(7) - 3; // Random number between -3 and 3
        int randomOffsetY = random.nextInt(7) - 3; // Random number between -3 and 3

        totemX = x + 8 + randomOffsetX;  // Center the cursor on the slot and add random offset
        totemY = y + 8 + randomOffsetY;  // Center the cursor on the slot and add random offset
    }

    public static void smoothMoveCursorToTotemSlot() {
        if (totemX == -1 || totemY == -1) return;

        try {
            Robot robot = new Robot();
            Window win = MinecraftClient.getInstance().getWindow();
            double scaleFactor = win.getScaleFactor();

            int finalX = (int) (totemX * scaleFactor);
            int finalY = (int) (totemY * scaleFactor);

            Point currentMousePos = MouseInfo.getPointerInfo().getLocation();
            int startX = currentMousePos.x;
            int startY = currentMousePos.y;

            // Generate a random height for the parabolic arc
            int arcHeight = random.nextInt(30) + 20;

            // Calculate delays
            int totalDelay = getTotalDelay();
            int steps = 50;
            int delayPerStep = totalDelay / steps;

            // Lock the cursor by continuously correcting its position if moved
            for (int i = 0; i < steps; i++) {
                double t = (double) i / (steps - 1);
                double smoothStep = t * t * (3 - 2 * t); // Smoothstep interpolation

                // Parabolic interpolation with a small randomized height
                int x = startX + (int) ((finalX - startX) * smoothStep);
                int y = startY + (int) ((finalY - startY) * smoothStep - (4 * arcHeight * smoothStep * (1 - smoothStep)));

                // Correct the cursor position if the user tries to move it
                Point currentPos = MouseInfo.getPointerInfo().getLocation();
                if (currentPos.x != x || currentPos.y != y) {
                    robot.mouseMove(x, y);
                } else {
                    robot.mouseMove(x, y);
                }

                try {
                    Thread.sleep(delayPerStep);
                } catch (InterruptedException ignored) {
                }
            }

            // Ensure the mouse is exactly on the slot at the end
            robot.mouseMove(finalX, finalY);

        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static int getTotalDelay() {
        int baseDelay = ConfigManager.getConfig().delayInMilliseconds;
        if (ConfigManager.getConfig().addRandomDelay) {
            int randomDelay = random.nextInt(ConfigManager.getConfig().maxRandomDelay + 1);
            return baseDelay + randomDelay;
        }
        return baseDelay;
    }

    public static int findEmptyHotbarSlot() {
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inventory.main.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public static int findTotemInHotbar() {
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        for (int i = 0; i < 9; i++) {
            if (inventory.main.get(i).isOf(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        return -1;
    }
}
