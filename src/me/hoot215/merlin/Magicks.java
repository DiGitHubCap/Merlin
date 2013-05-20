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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Magicks
  {
    private final File file;
    private final Properties properties = new Properties();
    private final Object slotsMutex = new Object();
    private Spell[] slots = new Spell[9];
    private Map<Spell.Type, Integer> levels = Collections
        .synchronizedMap(new HashMap<Spell.Type, Integer>());
    private Map<Spell.Type, Integer> levelExp = Collections
        .synchronizedMap(new HashMap<Spell.Type, Integer>());
    
    public Magicks(String playerName)
      {
        this(new File(Merlin.getInstance().getDataFolder(), "data"
            + File.separatorChar + playerName.toLowerCase()));
      }
    
    public Magicks(File file)
      {
        this.file = file;
        try
          {
            if ( !file.isFile())
              {
                file.getParentFile().mkdirs();
                file.createNewFile();
              }
            properties.load(new FileInputStream(file));
            this.load();
            this.save();
          }
        catch (IOException e)
          {
            e.printStackTrace();
          }
      }
    
    public Spell getSpell (int slot)
      {
        synchronized (slotsMutex)
          {
            return slots[slot];
          }
      }
    
    public int getLevel (Spell.Type type)
      {
        return levels.get(type);
      }
    
    public int getExp (Spell.Type type)
      {
        return levelExp.get(type);
      }
    
    public void setSpell (int slot, Spell spell)
      {
        synchronized (slotsMutex)
          {
            slots[slot] = spell;
          }
      }
    
    public void addLevels (Spell.Type type, int levels)
      {
        if (levels <= 0)
          return;
        this.levels.put(type, this.levels.get(type) + levels);
      }
    
    public void removeLevels (Spell.Type type, int levels)
      {
        if (levels <= 0)
          return;
        this.setLevel(type, this.levels.get(type) - levels);
      }
    
    public void setLevel (Spell.Type type, int level)
      {
        if (level < 0)
          {
            level = 0;
          }
        levels.put(type, level);
      }
    
    public boolean addExp (Spell.Type type, int exp)
      {
        if (exp <= 0)
          return false;
        return this.setExp(type, levelExp.get(type) + exp);
      }
    
    public void removeExp (Spell.Type type, int exp)
      {
        if (exp <= 0)
          return;
        this.setExp(type, levelExp.get(type) - exp);
      }
    
    public boolean setExp (Spell.Type type, int exp)
      {
        if (exp < 0)
          {
            exp = 0;
          }
        if (exp > levels.get(type) * 100)
          {
            levelExp.put(type, 0);
            this.addLevels(type, 1);
            return true;
          }
        else
          {
            levelExp.put(type, exp);
            return false;
          }
      }
    
    public void load ()
      {
        synchronized (slotsMutex)
          {
            for (int i = 0; i < slots.length; i++)
              {
                String slot = properties.getProperty("slot" + i, "null");
                if ( !slot.equals("null"))
                  {
                    try
                      {
                        slots[i] = Spell.valueOf(slot);
                      }
                    catch (IllegalArgumentException e)
                      {
                        properties.setProperty("slot" + i, "null");
                      }
                  }
              }
          }
        for (Spell.Type type : Spell.Type.values())
          {
            levels.put(type,
                Integer.parseInt(properties.getProperty(type.toString(), "1")));
            levelExp.put(
                type,
                Integer.parseInt(properties.getProperty(type.toString()
                    + "-EXP", "0")));
          }
      }
    
    public void save ()
      {
        try
          {
            synchronized (slotsMutex)
              {
                for (int i = 0; i < slots.length; i++)
                  {
                    Spell spell = slots[i];
                    if (spell == null)
                      {
                        properties.setProperty("slot" + i, "null");
                      }
                    else
                      {
                        properties.setProperty("slot" + i, spell.toString());
                      }
                  }
              }
            for (Entry<Spell.Type, Integer> entry : levels.entrySet())
              {
                properties.setProperty(entry.getKey().toString(), entry
                    .getValue().toString());
              }
            for (Entry<Spell.Type, Integer> entry : levelExp.entrySet())
              {
                properties.setProperty(entry.getKey().toString() + "-EXP",
                    entry.getValue().toString());
              }
            file.getParentFile().mkdirs();
            properties.store(new FileOutputStream(file), null);
          }
        catch (IOException e)
          {
            e.printStackTrace();
          }
      }
  }
