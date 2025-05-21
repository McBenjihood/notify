package com.notify.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

public class CustomItem extends Item {
    public CustomItem(Settings settings) {
        super(settings);
    }

    // For Minecraft 1.21.2+
    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {


        
        return ActionResult.SUCCESS;
    }
}
