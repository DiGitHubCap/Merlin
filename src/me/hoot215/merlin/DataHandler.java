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

import java.util.concurrent.LinkedBlockingQueue;

public class DataHandler implements Runnable
  {
    private final LinkedBlockingQueue<Data> queue =
        new LinkedBlockingQueue<Data>();
    private volatile boolean running = true;
    
    public DataHandler()
      {
        new Thread(this).start();
        new Thread(new AutoSaver(this)).start();
      }
    
    public void run ()
      {
        while (true)
          {
            try
              {
                Sorcerer sorcerer = queue.take().get();
                if (sorcerer == null)
                  break;
                sorcerer.getMagicks().save();
              }
            catch (InterruptedException e)
              {
                e.printStackTrace();
              }
          }
      }
    
    public void offload (Sorcerer sorcerer)
      {
        if (sorcerer == null)
          return;
        queue.add(new Data(sorcerer));
      }
    
    public void terminate ()
      {
        for (Sorcerer sorcerer : Merlin.getInstance().getSorcererManager()
            .getSorcerers())
          {
            if (sorcerer == null)
              break;
            this.offload(sorcerer);
          }
        queue.add(new Data(null));
        running = false;
      }
    
    public class AutoSaver implements Runnable
      {
        private final DataHandler dataHandler;
        
        public AutoSaver(DataHandler dataHandler)
          {
            this.dataHandler = dataHandler;
          }
        
        public void run ()
          {
            while (true)
              {
                if ( !dataHandler.running)
                  break;
                
                for (Sorcerer sorcerer : Merlin.getInstance()
                    .getSorcererManager().getSorcerers())
                  {
                    if (sorcerer == null)
                      break;
                    dataHandler.offload(sorcerer);
                  }
                
                try
                  {
                    Thread.sleep(300000);
                  }
                catch (InterruptedException e)
                  {
                    e.printStackTrace();
                  }
              }
          }
      }
  }
