package com.q256.playerAlert;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetworkPlayerInfo;
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
                //get lists of all player entities and the names of players visible in tab
                //both are necessary to make sure you are in the same game as the other player and not just in the same server (Hypixel hosts up to 20 games/server)
                List<EntityPlayer> playerEntities = new ArrayList<>();
                Collection<NetworkPlayerInfo> playerInfoMap = new ArrayList<>();

                try {
                    playerEntities = new ArrayList<>(Minecraft.getMinecraft().thePlayer.worldObj.playerEntities);
                    playerInfoMap = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap();
                } catch (NullPointerException exception){
                    exception.printStackTrace();
                }

                List<String> playersInTab = new ArrayList<>();
                for(NetworkPlayerInfo playerInfo:playerInfoMap){
                    playersInTab.add(Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(playerInfo));
                }

                //conditionally add these players to the display list
                displayedPlayersLock.lock();
                displayedPlayers.clear();
                for (EntityPlayer player:playerEntities) {
                    if(player==null || player.getName()==null) continue;
                    if(player.getDisplayName().getFormattedText().contains("§k")) continue;

                    boolean visibleOnTab = false;

                    for(String playerInTab:playersInTab){
                        StringBuilder playerDisplayName = new StringBuilder(player.getDisplayName().getFormattedText().substring(2));
                        playerDisplayName.delete(playerDisplayName.length()-2, playerDisplayName.length());
                        if(playerDisplayName.toString().equals(playerInTab)) visibleOnTab = true;
                    }

                    if(!visibleOnTab) continue;

                    if (configHandler.isBlocked(player.getName())) {
                        displayedPlayers.add(player.getDisplayNameString());
                    }
                }
                displayedPlayersLock.unlock();
            }
        };
        timer.schedule(updateDisplay, 2500, 2500);
    }

}
