package com.q256.playerAlert;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String MESSAGE_PREFIX = "[§bPlayerAlert§f]";

    public static synchronized void sendClientMessage(String text, boolean prefix) {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent((byte) 1, new ChatComponentText((prefix ? MESSAGE_PREFIX : "") + text));

        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(event.message);
        }
    }

    public static ArrayList<String> getAllPlayerNamesInWorld(){
        List<EntityPlayer> players;
        ArrayList<String> names = new ArrayList<>();
        try {
            players = Minecraft.getMinecraft().thePlayer.worldObj.playerEntities;
        } catch (NullPointerException exception){
            return names;
        }


        for(EntityPlayer player:players){
            if(player != null && player.getName()!=null) names.add(player.getName());
        }
        return names;
    }
}
