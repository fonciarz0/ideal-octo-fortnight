package com.example.myfabricmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class MyFabricMod implements ModInitializer {

    // A flag to indicate whether the mod has performed the actions once
    private boolean done = false;

    @Override
    public void onInitialize() {
        // Register a listener for when the client joins a server
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // Reset the flag
            done = false;
        });

        // Register a listener for every client tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Check if the client is in game and the mod has not done the actions yet
            if (client.world != null && !done) {
                // Get the player inventory
                PlayerInventory inventory = client.player.getInventory();
                // Get the first item in the hotbar
                ItemStack firstItem = inventory.getStack(0);
                // Check if the first item is not empty
                if (!firstItem.isEmpty()) {
                    // Switch to the first item slot
                    inventory.selectedSlot = 0;
                    // Check if the first item is a clock
                    if (firstItem.getItem() == Items.CLOCK) {
                        // Right click with the clock
                        client.interactionManager.interactItem(client.player, client.world, inventory.getMainHandStack());
                        // Check if the client is in a handled screen
                        if (client.currentScreen instanceof HandledScreen) {
                            // Get the handled screen
                            HandledScreen<?> screen = (HandledScreen<?>) client.currentScreen;
                            // Get the chest slot index
                            int chestSlot = screen.getScreenHandler().slots.size() - 1;
                            // Throw out the chest item from the chest slot
                            client.interactionManager.clickSlot(screen.getScreenHandler().syncId, chestSlot, 0, SlotActionType.THROW, client.player);
                        }
                    }
                }
                // Set the flag to true
                done = true;
            }
        });
    }
}
