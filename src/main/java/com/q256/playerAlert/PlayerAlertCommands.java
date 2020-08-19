package com.q256.playerAlert;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class PlayerAlertCommands extends CommandBase {
    ConfigHandler configHandler;

    public PlayerAlertCommands(ConfigHandler configHandler){
        this.configHandler = configHandler;
    }

    public String getCommandName() {
        return "playerAlert";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "§7------------------------------------------------" + "\n" +
                "§7/playerAlert add <player> §f- adds the player to your alert list" + "\n" +
                "§7/playerAlert remove <player> §f- removes the player from your alert list" + "\n" +
                "§7/playerAlert list §f- lists the players on your alert list" + "\n" +
                "§7/playerAlert clear §f- clears your alert list" + "\n" +
                "§7/playerAlert overlayX <pixels> §f- sets the X position of the overlay" + "\n" +
                "§7/playerAlert overlayY <pixels> §f- sets the Y position of the overlay" + "\n" +
                "§7/playerAlert overlayColor <red> <green> <blue> §f- sets the color of the overlay" + "\n" +
                "§7------------------------------------------------";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args==null || args.length==0){
            Utils.sendClientMessage(getCommandUsage(sender), false);
            return;
        }

        switch (args[0]){
            case "add":
                if(args.length<2) break;
                if (!args[1].matches("^\\w{3,16}$")) {
                    Utils.sendClientMessage("Invalid username. Please try again!", false);
                } else {
                    Thread playerAdder = new Thread(() -> {
                        boolean success = configHandler.addPlayer(args[1]);
                        if(success) Utils.sendClientMessage("§b"+args[1] + " §fhas been added to your alert list", false);
                        else Utils.sendClientMessage("§b"+args[1] + " §fwas already on your alert list", false);
                    });
                    playerAdder.start();
                }
                return;

            case "remove":
                if(args.length<2) break;
                if (!args[1].matches("^\\w{3,16}$")) {
                    Utils.sendClientMessage("Invalid username. Please try again!", false);
                } else {
                    Thread playerRemover = new Thread(() -> {
                        boolean success = configHandler.removePlayer(args[1]);
                        if(success) Utils.sendClientMessage("§b"+args[1] + " §fhas been removed from your alert list",false);
                        else Utils.sendClientMessage("§b"+args[1] + " §fwas not on your alert list",false);
                    });
                    playerRemover.start();
                }
                return;

            case "list":
                Thread playerLister = new Thread(() -> {
                    HashSet<String> names = configHandler.getBlockedPlayers();

                    if(names.isEmpty()){
                        Utils.sendClientMessage("None", false);
                        return;
                    }

                    StringBuilder stringBuilder = new StringBuilder("§ePlayers on your alert list: \n");
                    int namesThisLine = 0;
                    for(String ss:names){
                        if(namesThisLine>=5){
                            stringBuilder.append("\n");
                            namesThisLine = 0;
                        }
                        stringBuilder.append(ss);
                        stringBuilder.append(", ");
                        namesThisLine++;
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length()-2);
                    Utils.sendClientMessage(stringBuilder.toString(), false);
                });
                playerLister.start();
                return;

            case "clear":
                Thread playerClearer = new Thread(() -> {
                    configHandler.clearBlockedPlayers();
                    Utils.sendClientMessage("Your alert list has been cleared",false);
                });
                playerClearer.start();
                return;

            case "overlayX":
                if(args.length<2) break;

                try {
                    int xPos = Integer.parseInt(args[1]);
                    configHandler.setOverlayXPos(xPos);
                } catch (NumberFormatException exception){
                    exception.printStackTrace();
                    Utils.sendClientMessage("Invalid number", false);
                }
                return;

            case "overlayY":
                if(args.length<2) break;

                try {
                    int yPos = Integer.parseInt(args[1]);
                    configHandler.setOverlayYPos(yPos);
                } catch (NumberFormatException exception){
                    exception.printStackTrace();
                    Utils.sendClientMessage("Invalid number", false);
                }
                return;

            case "overlayColor":
                if(args.length<4) break;
                try {
                    int red = Integer.parseInt(args[1]);
                    int green = Integer.parseInt(args[2]);
                    int blue = Integer.parseInt(args[3]);

                    if(red<0 || red>255 || green<0 || green>255 || blue<0 || blue>255){
                        Utils.sendClientMessage("Color values must be between 0 and 255", false);
                        return;
                    }

                    configHandler.setOverlayColor((red<<16) + (green<<8) + (blue));
                    Utils.sendClientMessage("Overlay color set to: §cRed: §f"+red+", §aGreen: §f"+green+", §9Blue: §f"+blue, false);
                } catch (NumberFormatException exception){
                    exception.printStackTrace();
                    Utils.sendClientMessage("Invalid number", false);
                }
                return;
        }
        Utils.sendClientMessage(getCommandUsage(sender), false);
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getReducedTabCompletions(Arrays.asList("add","remove","list","clear","overlayX","overlayY","overlayColor"), args[0]);
        }
        if(args.length == 2){
            return getReducedTabCompletions(Utils.getAllPlayerNamesInWorld(), args[1]);
        }
        return null;
    }


    private ArrayList<String> getReducedTabCompletions(List<String> allSuggestions, String alreadyTyped){
        ArrayList<String> outAl = new ArrayList<>();
        for(String ss:allSuggestions){
            if(ss.toLowerCase().startsWith(alreadyTyped.toLowerCase())) outAl.add(ss);
        }
        return outAl;
    }
}
