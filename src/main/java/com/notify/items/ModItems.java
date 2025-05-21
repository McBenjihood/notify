package com.notify.items;

import com.notify.Notify;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;


public class ModItems {

    //Hinzufügen des Items
    public static final Item GUI_TRIGGER = registerItem("gui_trigger",
            new CustomItem(new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Notify.MOD_ID, "gui_trigger"))))
    );


    //Methode um ein Item zu registrieren, für Testzwecke
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM,Identifier.of(Notify.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Notify.LOGGER.info("registering ModItems for "+ Notify.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries ->{
            entries.add(GUI_TRIGGER);
        });
    }
}
