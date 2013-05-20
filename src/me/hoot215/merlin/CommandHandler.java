/*
 * Magic plugin inspired by Merlin.
 * Copyright (C) 2013 Andrew Stevanus (Hoot215) <hoot893@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.hoot215.merlin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class CommandHandler implements CommandExecutor, TabCompleter
  {
    private final Merlin plugin = Merlin.getInstance();
    private final ExecutorService searchPool = Executors
        .newSingleThreadExecutor();
    private final String[] spellTypes = new String[Spell.Type.values().length];
    private final String[] merlinCommands1 = {"admin"};
    private final String[] merlinCommands2 = {"setlevel", "update", "download"};
    private final String[] magicCommands = {"types", "levels"};
    private final String[] spellCommands =
        new String[Spell.values().length + 1];
    private final String[] unbindCommands = {"all"};
      
      {
        Spell.Type[] types = Spell.Type.values();
        for (int i = 0; i < types.length; i++)
          {
            spellTypes[i] = types[i].getName();
          }
        spellCommands[0] = "none";
        Spell[] spells = Spell.values();
        for (int i = 0; i < spells.length; i++)
          {
            spellCommands[i + 1] = spells[i].getName().replace(' ', '_');
          }
      }
    
    public List<String> onTabComplete (CommandSender sender, Command cmd,
      String label, String[] args)
      {
        List<String> suggestions = new ArrayList<String>();
        if (cmd.getName().equals("merlin"))
          {
            if (args.length == 0)
              {
                for (String s : merlinCommands1)
                  {
                    suggestions.add(s);
                  }
              }
            else if (args.length == 1)
              {
                for (String s : merlinCommands1)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, args[0]))
                      {
                        suggestions.add(s);
                      }
                  }
              }
            else if (args.length == 2)
              {
                for (String s : merlinCommands2)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, args[1]))
                      {
                        suggestions.add(s);
                      }
                  }
              }
            else if (args.length == 3 && args[1].equalsIgnoreCase("setlevel"))
              {
                for (Player player : plugin.getServer().getOnlinePlayers())
                  {
                    if (StringUtil.startsWithIgnoreCase(player.getName(),
                        args[2]))
                      {
                        suggestions.add(player.getName());
                      }
                  }
              }
            else if (args.length == 4 && args[1].equalsIgnoreCase("setlevel"))
              {
                for (String s : spellTypes)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, args[3]))
                      {
                        suggestions.add(s);
                      }
                  }
              }
          }
        else if (cmd.getName().equals("magic"))
          {
            if (args.length == 0)
              {
                for (String s : magicCommands)
                  {
                    suggestions.add(s);
                  }
              }
            else if (args.length == 1)
              {
                for (String s : magicCommands)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, args[0]))
                      {
                        suggestions.add(s);
                      }
                  }
              }
          }
        else if (cmd.getName().equals("spell"))
          {
            if (args.length == 0)
              {
                for (String s : spellCommands)
                  {
                    suggestions.add(s);
                  }
              }
            else if (args.length >= 1)
              {
                String arg = args[0];
                for (int i = 1; i < args.length; i++)
                  {
                    arg += ' ' + args[i];
                  }
                for (String s : spellCommands)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, arg))
                      {
                        suggestions.add(s);
                      }
                  }
              }
          }
        else if (cmd.getName().equals("unbind"))
          {
            if (args.length == 0)
              {
                for (String s : unbindCommands)
                  {
                    suggestions.add(s);
                  }
              }
            else if (args.length == 1)
              {
                for (String s : unbindCommands)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, args[0]))
                      {
                        suggestions.add(s);
                      }
                  }
              }
          }
        else if (cmd.getName().equals("spells"))
          {
            if (args.length == 0)
              {
                for (String s : spellTypes)
                  {
                    suggestions.add(s);
                  }
              }
            else if (args.length == 1)
              {
                for (String s : spellTypes)
                  {
                    if (StringUtil.startsWithIgnoreCase(s, args[0]))
                      {
                        suggestions.add(s);
                      }
                  }
              }
          }
        return suggestions;
      }
    
    public boolean onCommand (final CommandSender sender, Command cmd,
      String label, String[] args)
      {
        if (cmd.getName().equals("merlin"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              return false;
            if (args.length >= 1
                && args[0].equalsIgnoreCase(merlinCommands1[0]))
              {
                if (args.length == 1)
                  return false;
                if (args[1].equalsIgnoreCase(merlinCommands2[0]))
                  {
                    if ( !sender.hasPermission("merlin.admin.changelevel"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    if (args.length == 2)
                      {
                        sender.sendMessage(ChatColor.RED
                            + "You have not specified a player, a type,"
                            + " or a level");
                        return true;
                      }
                    if (args.length == 3)
                      {
                        sender.sendMessage(ChatColor.RED
                            + "You have not specified a type or a level");
                        return true;
                      }
                    if (args.length == 4)
                      {
                        sender.sendMessage(ChatColor.RED
                            + "You have not specified a level");
                        return true;
                      }
                    if (args.length > 5)
                      {
                        sender
                            .sendMessage(ChatColor.RED + "Too many arguments");
                        return true;
                      }
                    Player player = plugin.getServer().getPlayer(args[2]);
                    if (player == null)
                      {
                        sender.sendMessage(ChatColor.RED
                            + "That player is not online");
                        return true;
                      }
                    Spell.Type type;
                    try
                      {
                        type = Spell.Type.valueOf(args[3].toUpperCase());
                      }
                    catch (IllegalArgumentException e)
                      {
                        sender.sendMessage(ChatColor.RED
                            + "That type does not exist");
                        return true;
                      }
                    int level;
                    try
                      {
                        level = Integer.parseInt(args[4]);
                      }
                    catch (NumberFormatException e)
                      {
                        sender.sendMessage(ChatColor.RED.toString() + '\''
                            + args[5] + "' is not a valid integer");
                        return true;
                      }
                    Sorcerer sorcerer =
                        plugin.getSorcererManager().getSorcerer(player);
                    sorcerer.getMagicks().setLevel(type, level);
                    sorcerer.recalculateLevel();
                    sender.sendMessage(ChatColor.LIGHT_PURPLE
                        + player.getName() + ChatColor.GREEN + "'s "
                        + type.getColour() + type.getName() + ChatColor.GREEN
                        + " level has been changed to " + ChatColor.AQUA
                        + level);
                    return true;
                  }
                if (args[1].equalsIgnoreCase(merlinCommands2[1]))
                  {
                    if ( !sender.hasPermission("merlin.admin.update"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    sender.sendMessage(ChatColor.GREEN
                        + "Checking for a newer version...");
                    plugin.addToAutoUpdaterCheckQueue(sender);
                    return true;
                  }
                if (args[1].equalsIgnoreCase(merlinCommands2[2]))
                  {
                    if ( !sender.hasPermission("merlin.admin.download"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    sender.sendMessage(ChatColor.GREEN
                        + "Downloading the latest version...");
                    plugin.addToAutoUpdaterDownloadQueue(sender);
                    return true;
                  }
                return false;
              }
            return false;
          }
        if (cmd.getName().equals("magic"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              return false;
            if (args.length == 1)
              {
                if (args[0].equalsIgnoreCase(magicCommands[0]))
                  {
                    if ( !sender.hasPermission("merlin.magic.types"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    sender.sendMessage(ChatColor.DARK_PURPLE
                        + "Types of magic:");
                    for (Spell.Type type : Spell.Type.values())
                      {
                        sender.sendMessage(type.getColour() + type.getName());
                      }
                    return true;
                  }
                if (args[0].equalsIgnoreCase(magicCommands[1]))
                  {
                    if ( !sender.hasPermission("merlin.magic.levels"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    if ( ! (sender instanceof Player))
                      {
                        sender.sendMessage(ChatColor.RED
                            + "This command can only be run as a player");
                        return true;
                      }
                    Sorcerer sorcerer =
                        plugin.getSorcererManager()
                            .getSorcerer((Player) sender);
                    sender.sendMessage(ChatColor.BLUE + "Levels for "
                        + sorcerer.getPlayer().getName() + ':');
                    for (Spell.Type type : Spell.Type.values())
                      {
                        sender.sendMessage(type.getColour() + type.getName()
                            + ChatColor.GRAY + ": "
                            + sorcerer.getMagicks().getLevel(type));
                      }
                  }
                return false;
              }
            return false;
          }
        if (cmd.getName().equals("spell"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if ( ! (sender instanceof Player))
              {
                sender.sendMessage(ChatColor.RED
                    + "This command can only be run as a player");
                return true;
              }
            Sorcerer sorcerer =
                plugin.getSorcererManager().getSorcerer(sender.getName());
            if (args.length == 0)
              {
                Spell spell = sorcerer.getCurrentSpell();
                if (spell == null)
                  {
                    sender.sendMessage(ChatColor.RED
                        + "You don't have a spell bound to that slot");
                    sender.sendMessage(ChatColor.AQUA + "Use /" + label
                        + " [spell] to bind one");
                  }
                else
                  {
                    sender.sendMessage(ChatColor.AQUA + "Currently bound: "
                        + spell.getType().getColour() + spell.getName()
                        + ChatColor.GRAY + " - " + spell.getDescription());
                  }
                return true;
              }
            if (args.length == 1)
              {
                if (args[0].equalsIgnoreCase(spellCommands[0]))
                  {
                    sorcerer.setCurrentSpell(null);
                    sender.sendMessage(ChatColor.GREEN
                        + "Your current slot has been unbound");
                    return true;
                  }
              }
            if (args.length >= 1)
              {
                StringBuilder builder = new StringBuilder(args[0]);
                for (int i = 1; i < args.length; i++)
                  {
                    builder.append(' ').append(args[i]);
                  }
                final String spellName = builder.toString().replace('_', ' ');
                Spell spell = Spell.getSpell(spellName);
                if (spell == null)
                  {
                    sender.sendMessage(ChatColor.RED
                        + "That spell does not exist!");
                    searchPool.submit(new Runnable()
                      {
                        public void run ()
                          {
                            sender.sendMessage(ChatColor.YELLOW
                                + "Did you mean " + ChatColor.BLUE
                                + Spell.search(spellName).getName()
                                + ChatColor.YELLOW + '?');
                          }
                      });
                    return true;
                  }
                sorcerer.setCurrentSpell(spell);
                sender.sendMessage(ChatColor.GREEN + "You have bound "
                    + ChatColor.DARK_AQUA + spell.getName() + ChatColor.GREEN
                    + " to your current inventory slot");
                return true;
              }
            return false;
          }
        if (cmd.getName().equals("unbind"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if ( ! (sender instanceof Player))
              {
                sender.sendMessage(ChatColor.RED
                    + "This command can only be run as a player");
                return true;
              }
            Sorcerer sorcerer =
                plugin.getSorcererManager().getSorcerer(sender.getName());
            if (args.length == 0)
              {
                if (sorcerer.getCurrentSpell() == null)
                  {
                    sender.sendMessage(ChatColor.RED
                        + "You don't have a spell bound to that slot");
                    return true;
                  }
                sorcerer.setCurrentSpell(null);
                sender.sendMessage(ChatColor.GREEN
                    + "Your current slot has been unbound");
                return true;
              }
            if (args.length == 1)
              {
                if (args[0].equalsIgnoreCase(unbindCommands[0]))
                  {
                    for (int i = 0; i < 9; i++)
                      {
                        sorcerer.getMagicks().setSpell(i, null);
                      }
                    sender.sendMessage(ChatColor.GREEN
                        + "All spells have been unbound");
                    return true;
                  }
                return false;
              }
            return false;
          }
        if (cmd.getName().equals("spells"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              {
                sender.sendMessage(ChatColor.DARK_PURPLE + "Available spells:");
                for (Spell spell : Spell.values())
                  {
                    sender.sendMessage(spell.getType().getColour()
                        + spell.getName() + ChatColor.GRAY + " - "
                        + spell.getDescription());
                  }
                return true;
              }
            if (args.length == 1)
              {
                Spell.Type type;
                try
                  {
                    type = Spell.Type.valueOf(args[0].toUpperCase());
                  }
                catch (IllegalArgumentException e)
                  {
                    sender.sendMessage(ChatColor.RED
                        + "That type does not exist");
                    return true;
                  }
                sender.sendMessage(ChatColor.DARK_PURPLE
                    + "Available spells of type " + type.getColour()
                    + type.getName());
                for (Spell spell : Spell.values())
                  {
                    if (spell.getType() != type)
                      continue;
                    sender.sendMessage(spell.getType().getColour()
                        + spell.getName() + ChatColor.GRAY + " - "
                        + spell.getDescription());
                  }
                return true;
              }
            return false;
          }
        return false;
      }
  }
