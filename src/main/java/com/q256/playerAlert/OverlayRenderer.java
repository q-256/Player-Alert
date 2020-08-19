package com.q256.playerAlert;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.LinkedQueue;

import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantLock;

public class OverlayRenderer {
    ConfigHandler configHandler;
    HashSet<String> displayedPlayers = new HashSet<>();
    ReentrantLock displayedPlayersLock = new ReentrantLock();

    public OverlayRenderer(ConfigHandler configHandler){
        this.configHandler = configHandler;
        startTimer();
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent event){
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        displayedPlayersLock.lock();
        int offset = 0;
        for(String player:displayedPlayers){
            fontRenderer.drawString(player, configHandler.getOverlayXPos(), configHandler.getOverlayYPos() + offset, configHandler.getOverlayColor());
            offset += fontRenderer.FONT_HEIGHT;
        }
        displayedPlayersLock.unlock();
    }

    private void startTimer() {
        Timer timer = new Timer();
        TimerTask updateDisplay = new TimerTask() {
            public void run() {
                displayedPlayersLock.lock();
                displayedPlayers.clear();
                for (String name : Utils.getAllPlayerNamesInWorld()) {
                    if (configHandler.isBlocked(name)) {
                        displayedPlayers.add(name);
                    }
                }
                displayedPlayersLock.unlock();
            }
        };
        timer.schedule(updateDisplay, 2500, 2500);
    }

}
