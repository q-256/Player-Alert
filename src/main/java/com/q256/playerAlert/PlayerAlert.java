package com.q256.playerAlert;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = PlayerAlert.MODID, name= PlayerAlert.NAME, version = PlayerAlert.VERSION, clientSideOnly = true)
public class PlayerAlert {
    public static final String MODID = "playerAlert";
    public static final String VERSION = "0.1";
    public static final String NAME = "PlayerAlert";

    ConfigHandler configHandler;
    OverlayRenderer overlayRenderer;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        configHandler = new ConfigHandler(e.getSuggestedConfigurationFile());
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        overlayRenderer = new OverlayRenderer(configHandler);

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(overlayRenderer);

        ClientCommandHandler.instance.registerCommand(new PlayerAlertCommands(configHandler));
    }


}
