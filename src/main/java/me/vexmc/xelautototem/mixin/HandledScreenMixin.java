package me.vexmc.xelautototem.mixin;

import me.vexmc.xelautototem.utils.TotemHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow protected int x;
    @Shadow protected int y;

    @Inject(method = "drawSlot", at = @At("HEAD"))
    public void drawItemInSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        ItemStack stack = slot.getStack();
        if (stack.isOf(Items.TOTEM_OF_UNDYING) && TotemHelper.isListeningForTotemSlot()) {
            TotemHelper.setTotemSlotCoordinates(slot.x + x, slot.y + y);
            TotemHelper.stopListeningForTotemSlot();

            // Now move the cursor smoothly to the detected slot
            TotemHelper.smoothMoveCursorToTotemSlot();
        }
    }
}
