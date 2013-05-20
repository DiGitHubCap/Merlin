/*
 * Hoot215's plugin auto-updater.
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

package me.hoot215.updater;

import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class AutoUpdaterDownloadQueue implements Runnable
  {
    private final AutoUpdater autoUpdater;
    private final String pluginName;
    private final LinkedBlockingQueue<WrappedCommandSender> queue =
        new LinkedBlockingQueue<WrappedCommandSender>();
    
    public AutoUpdaterDownloadQueue(AutoUpdater autoUpdater)
      {
        this.autoUpdater = autoUpdater;
        pluginName = autoUpdater.getPluginName();
      }
    
    public void add (WrappedCommandSender sender)
      {
        queue.add(sender);
      }
    
    public void run ()
      {
        while (true)
          {
            try
              {
                CommandSender sender = queue.take().get();
                if (sender == null)
                  break;
                autoUpdater.updateCheck();
                autoUpdater.downloadLatestVersion();
                sender.sendMessage(ChatColor.GREEN + "The latest version of "
                    + ChatColor.RED + pluginName + ChatColor.GREEN
                    + " has been downloaded! Reload for the changes to"
                    + " take effect");
              }
            catch (InterruptedException e)
              {
                e.printStackTrace();
              }
          }
      }
  }
