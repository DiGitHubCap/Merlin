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

import me.hoot215.updater.WrappedCommandSender;

public class AutoUpdaterQueue
  {
    private final AutoUpdaterCheckQueue checkQueue;
    private final AutoUpdaterDownloadQueue downloadQueue;
    
    public AutoUpdaterQueue(AutoUpdater autoUpdater)
      {
        checkQueue = new AutoUpdaterCheckQueue(autoUpdater);
        downloadQueue = new AutoUpdaterDownloadQueue(autoUpdater);
      }
    
    public void start ()
      {
        new Thread(checkQueue).start();
        new Thread(downloadQueue).start();
      }
    
    public void addCheck (WrappedCommandSender sender)
      {
        checkQueue.add(sender);
      }
    
    public void addDownload (WrappedCommandSender sender)
      {
        downloadQueue.add(sender);
      }
  }
