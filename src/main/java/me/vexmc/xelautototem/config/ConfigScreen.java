package me.vexmc.xelautototem.config;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen {

    public static Screen getConfigBuilder(Screen parent) {
        var builder = me.shedaniel.clothconfig2.api.ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("XelAutoTotem Config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));
        ConfigCategory delay = builder.getOrCreateCategory(Text.of("Delay"));

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Enable XelAutoTotem"), ConfigManager.getConfig().enabled)
                .setTooltip(Text.of("Enable or disable the mod"))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ConfigManager.getConfig().enabled = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Legit Swap"), ConfigManager.getConfig().legitSwap)
                .setTooltip(Text.of("Screenshare proof totem-swapping, using realistic mouse movement and swapping to your hand first then swapping it to your off-hand. If disabled, uses packets to swap."))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ConfigManager.getConfig().legitSwap = newValue)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.of("Check Potion Effects"), ConfigManager.getConfig().checkPotionEffects)
                .setTooltip(Text.of("Checks to ensure you have the potion effects you get after popping totems before restocking."))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ConfigManager.getConfig().checkPotionEffects = newValue)
                .build());

        delay.addEntry(entryBuilder.startIntField(Text.of("Delay In Milliseconds"), ConfigManager.getConfig().delayInMilliseconds)
                .setTooltip(Text.of("Total delay between the totem being popped and the totem being restocked."))
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> ConfigManager.getConfig().delayInMilliseconds = newValue)
                .build());

        delay.addEntry(entryBuilder.startBooleanToggle(Text.of("Add Random Delay"), ConfigManager.getConfig().addRandomDelay)
                .setTooltip(Text.of("Add an extra random delay between 0 and the Max Random Delay."))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ConfigManager.getConfig().addRandomDelay = newValue)
                .build());

        delay.addEntry(entryBuilder.startIntSlider(Text.of("Max Random Delay"), ConfigManager.getConfig().maxRandomDelay, 0, 1000)
                .setTooltip(Text.of("The maximum random delay that can be added."))
                .setDefaultValue(220)
                .setSaveConsumer(newValue -> ConfigManager.getConfig().maxRandomDelay = newValue)
                .build());

        builder.setSavingRunnable(ConfigManager::saveConfig);

        return builder.build();
    }
}
