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

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.hoot215.merlin.util.Fireworks;
import me.hoot215.merlin.util.Particle;
import me.hoot215.updater.AutoUpdater;
import me.hoot215.updater.AutoUpdaterQueue;
import me.hoot215.updater.WrappedCommandSender;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Merlin extends JavaPlugin
  {
    private static Merlin instance;
    private final ExecutorService particlePool = Executors
        .newSingleThreadExecutor();
    private final ExecutorService glassPool = Executors.newCachedThreadPool();
    private AutoUpdaterQueue autoUpdaterQueue;
    private SorcererManager sorcererManager;
    private DataHandler dataHandler;
    private CommandHandler commandHandler;
    
    public static Merlin getInstance ()
      {
        return instance;
      }
    
    public DataHandler getDataHandler ()
      {
        return dataHandler;
      }
    
    public SorcererManager getSorcererManager ()
      {
        return sorcererManager;
      }
    
    public void addToAutoUpdaterCheckQueue (CommandSender sender)
      {
        autoUpdaterQueue.addCheck(new WrappedCommandSender(sender));
      }
    
    public void addToAutoUpdaterDownloadQueue (CommandSender sender)
      {
        autoUpdaterQueue.addDownload(new WrappedCommandSender(sender));
      }
    
    public void createParticles (Location loc, boolean block,
      final Particle particle, final int count)
      {
        this.createParticles(loc, block, particle, 0, count);
      }
    
    public void createParticles (Location loc, boolean block,
      final Particle particle, final float speed, final int count)
      {
        final Location finalLoc =
            block ? loc.add(0.5, 0.5, 0.5).clone() : loc.clone();
        final Chunk chunk = finalLoc.getChunk();
        particlePool.submit(new Runnable()
          {
            public void run ()
              {
                int xMin = chunk.getX() - 1;
                int xMax = chunk.getX() + 1;
                int zMin = chunk.getZ() - 1;
                int zMax = chunk.getZ() + 1;
                for (int x = xMin; x <= xMax; x++)
                  {
                    for (int z = zMin; z <= zMax; z++)
                      {
                        for (Entity e : chunk.getEntities())
                          {
                            if (e instanceof Player)
                              {
                                particle.sendToPlayer((Player) e, finalLoc, 0,
                                    0, 0, speed, count);
                              }
                          }
                      }
                  }
              }
          });
      }
    
    public void shatterGlass (Sorcerer sorcerer, int mana)
      {
        final int radius = mana / 200;
        final Location loc = sorcerer.getPlayer().getLocation().clone();
        glassPool.submit(new Runnable()
          {
            public void run ()
              {
                int xMax = loc.getBlockX() + radius;
                int yMax = loc.getBlockY() + (int) (radius * (3.0 / 4.0));
                int zMax = loc.getBlockZ() + radius;
                for (int x = loc.getBlockX() - radius; x <= xMax; x++)
                  {
                    for (int y = loc.getBlockY(); y <= yMax; y++)
                      {
                        for (int z = loc.getBlockZ() - radius; z <= zMax; z++)
                          {
                            final int finalX = x;
                            final int finalY = y;
                            final int finalZ = z;
                            instance
                                .getServer()
                                .getScheduler()
                                .scheduleSyncDelayedTask(instance,
                                    new Runnable()
                                      {
                                        public void run ()
                                          {
                                            Block block =
                                                loc.getWorld().getBlockAt(
                                                    finalX, finalY, finalZ);
                                            Material mat = block.getType();
                                            if (mat == Material.GLASS
                                                || mat == Material.THIN_GLASS)
                                              {
                                                block.breakNaturally();
                                                instance.createParticles(
                                                    block.getLocation(), true,
                                                    Particle.EXPLODE, 3);
                                                loc.getWorld().playSound(loc,
                                                    Sound.GLASS, radius, 1);
                                              }
                                          }
                                      });
                            try
                              {
                                Thread.sleep(5);
                              }
                            catch (InterruptedException e)
                              {
                                e.printStackTrace();
                              }
                          }
                      }
                  }
              }
          });
      }
    
    public void nullStaticVariables ()
      {
        try
          {
            Particle.nullStaticVariables();
            Fireworks.nullStaticVariables();
          }
        catch (NoClassDefFoundError e)
          {
          }
      }
    
    @Override
    public void onDisable ()
      {
        this.unregisterPermissions();
        try
          {
            dataHandler.terminate();
            this.nullStaticVariables();
          }
        catch (NoClassDefFoundError e)
          {
          }
        
        instance = null;
        
        this.getLogger().info("Is now disabled");
      }
    
    @Override
    public void onEnable ()
      {
        instance = this;
        sorcererManager = new SorcererManager();
        sorcererManager.startManaRegeneration();
        dataHandler = new DataHandler();
        commandHandler = new CommandHandler();
        
        this.registerEvents();
        this.registerPermissions();
        
        this.getCommand("merlin").setExecutor(commandHandler);
        this.getCommand("magic").setExecutor(commandHandler);
        this.getCommand("spell").setExecutor(commandHandler);
        this.getCommand("unbind").setExecutor(commandHandler);
        this.getCommand("spells").setExecutor(commandHandler);
        
        Player[] onlinePlayers = this.getServer().getOnlinePlayers();
        for (int i = 0; i < onlinePlayers.length; i++)
          {
            sorcererManager.loadSorcerer(onlinePlayers[i]);
          }
        
        try
          {
            AutoUpdater autoUpdater = new AutoUpdater(this);
            autoUpdater.start();
            autoUpdaterQueue = new AutoUpdaterQueue(autoUpdater);
            autoUpdaterQueue.start();
          }
        catch (MalformedURLException e)
          {
            e.printStackTrace();
          }
        
        this.getLogger().info("Is now enabled");
      }
    
    private void registerEvents ()
      {
        this.getServer().getPluginManager()
            .registerEvents(new PlayerListener(), this);
      }
    
    private void registerPermissions ()
      {
        this.getServer().getScheduler()
            .scheduleSyncDelayedTask(this, new Runnable()
              {
                public void run ()
                  {
                    PluginManager pm = instance.getServer().getPluginManager();
                    Map<String, Boolean> children =
                        new HashMap<String, Boolean>();
                    Map<Spell.Type, Permission> types =
                        new HashMap<Spell.Type, Permission>();
                    Map<Spell.Type, Map<String, Boolean>> typeChildren =
                        new HashMap<Spell.Type, Map<String, Boolean>>();
                    for (Spell spell : Spell.values())
                      {
                        Map<String, Boolean> spellTypeChildren =
                            typeChildren.get(spell.getType());
                        if (spellTypeChildren == null)
                          {
                            typeChildren.put(spell.getType(),
                                spellTypeChildren =
                                    new HashMap<String, Boolean>());
                          }
                        pm.addPermission(new Permission(spell.getPermission(),
                            spell.getDescription()));
                        spellTypeChildren.put(spell.getPermission(), true);
                      }
                    for (Spell.Type type : Spell.Type.values())
                      {
                        String permName =
                            "merlin.cast." + type.toString().toLowerCase()
                                + ".*";
                        Permission perm =
                            new Permission(permName, "Gives access to all "
                                + type.getName() + " spells", typeChildren
                                .get(type));
                        types.put(type, perm);
                        pm.addPermission(perm);
                        children.put(permName, true);
                      }
                    pm.addPermission(new Permission("merlin.cast.*",
                        "Gives access to all spells", children));
                    Permission standard = null;
                    for (Permission perm : instance.getDescription()
                        .getPermissions())
                      {
                        if (perm.getName().equals("merlin.standard"))
                          {
                            standard = perm;
                            break;
                          }
                      }
                    standard.getChildren().put("merlin.cast.*", true);
                    standard.recalculatePermissibles();
                    pm.removePermission(standard);
                    pm.addPermission(standard);
                  }
              });
      }
    
    public void unregisterPermissions ()
      {
        PluginManager pm = this.getServer().getPluginManager();
        for (Permission perm : pm.getPermissions())
          {
            if (perm.getName().startsWith("merlin"))
              {
                pm.removePermission(perm);
              }
          }
      }
  }
