package me.vexmc.xelautototem.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import me.vexmc.xelautototem.utils.TotemHelper;
import me.vexmc.xelautototem.config.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(GameRenderer.class)
public class TotemMixin {

    @Unique
    private ArrayList<Packet<?>> packetsToSend = new ArrayList<>();

    @Unique
    private int tickCounter = 0;
    private int delayTicks = 0;

    @Inject(at = @At("TAIL"), method = "showFloatingItem")
    private void onTotemUse(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.isOf(Items.TOTEM_OF_UNDYING)) return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);

        if (gameRenderer.getClient().player == null) return;

        if (ConfigManager.getConfig().checkPotionEffects) {
            if (!gameRenderer.getClient().player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) return;
            if (!gameRenderer.getClient().player.hasStatusEffect(StatusEffects.REGENERATION)) return;
        }

        PlayerInventory inventory = gameRenderer.getClient().player.getInventory();

        // Step 1: Check if there's a totem in the hotbar
        int hotbarSlotWithTotem = TotemHelper.findTotemInHotbar();
        if (hotbarSlotWithTotem != -1) {
            // Swap the totem from the hotbar into the offhand
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(hotbarSlotWithTotem));
            packetsToSend.add(new ClickSlotC2SPacket(inventory.player.currentScreenHandler.syncId, inventory.player.currentScreenHandler.getRevision(), 45, 0, SlotActionType.SWAP, inventory.getStack(hotbarSlotWithTotem).copy(), new Int2ObjectArrayMap<>()));
            executePackets();
            return;
        }

        // Step 2: Check for empty hotbar slots
        int emptyHotbarSlot = TotemHelper.findEmptyHotbarSlot();
        if (emptyHotbarSlot != -1) {
            // Smoothly move to the totem slot in inventory, then shift-click into hotbar
            TotemHelper.onInventoryScreenOpen(); // Start listening for the totem slot
            // The smooth movement and the swap to the hotbar will be handled when the slot is detected
            return;
        }

        // Step 3: Fallback to the current behavior (directly to offhand)
        int totemSlot = getTotemSlot(inventory);
        if (totemSlot != -1) {
            restockSlot(gameRenderer.getClient().player, totemSlot);
        }
    }

    @Unique
    private int getTotemSlot(PlayerInventory inventory) {
        for (int i = 9; i < inventory.main.size(); i++) { // Check inventory first
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1;
    }

    @Unique
    private void restockSlot(PlayerEntity player, int slot) {
        packetsToSend.add(new ClickSlotC2SPacket(player.currentScreenHandler.syncId, player.currentScreenHandler.getRevision(), slot, 0, SlotActionType.PICKUP, player.getInventory().getStack(slot).copy(), new Int2ObjectArrayMap<>()));
        packetsToSend.add(new ClickSlotC2SPacket(player.currentScreenHandler.syncId, player.currentScreenHandler.getRevision(), 45, 0, SlotActionType.PICKUP, player.getInventory().getStack(slot).copy(), new Int2ObjectArrayMap<>()));
        executePackets();
    }

    @Unique
    private void executePackets() {
        MinecraftClient.getInstance().execute(() -> {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null && !packetsToSend.isEmpty()) {
                packetsToSend.forEach(networkHandler::sendPacket);
                packetsToSend.clear();
            }
        });
    }
}
