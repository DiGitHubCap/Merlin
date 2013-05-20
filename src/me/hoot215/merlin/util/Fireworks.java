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

package me.hoot215.merlin.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public final class Fireworks
  {
    private static Method world_getHandle = null;
    private static Method nms_world_broadcastEntityEffect = null;
    private static Method firework_getHandle = null;
    
    private Fireworks()
      {
      }
    
    public static void nullStaticVariables ()
      {
        world_getHandle = null;
        nms_world_broadcastEntityEffect = null;
        firework_getHandle = null;
      }
    
    private static Method getMethod (Class<?> clazz, String method)
      {
        for (Method m : clazz.getMethods())
          {
            if (m.getName().equals(method))
              {
                return m;
              }
          }
        return null;
      }
    
    public static void detonateFirework (Location loc, boolean flicker,
      boolean trail, FireworkEffect.Type type, Color colour, Color fade)
      {
        detonateFirework(loc,
            FireworkEffect.builder().flicker(flicker).trail(trail).with(type)
                .withColor(colour).withFade(fade).build());
      }
    
    public static void detonateFirework (Location loc, boolean flicker,
      boolean trail, FireworkEffect.Type type, Iterable<Color> colours,
      Iterable<Color> fades)
      {
        detonateFirework(loc,
            FireworkEffect.builder().flicker(flicker).trail(trail).with(type)
                .withColor(colours).withFade(fades).build());
      }
    
    public static void detonateFirework (Location loc, FireworkEffect effects)
      {
        World world = loc.getWorld();
        Firework firework =
            (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
        // Credit to codename_B
        Object nms_world = null;
        Object nms_firework = null;
        if (world_getHandle == null)
          {
            world_getHandle = getMethod(world.getClass(), "getHandle");
            firework_getHandle = getMethod(firework.getClass(), "getHandle");
          }
        try
          {
            nms_world = world_getHandle.invoke(world, (Object[]) null);
            nms_firework = firework_getHandle.invoke(firework, (Object[]) null);
          }
        catch (InvocationTargetException e)
          {
            e.printStackTrace();
          }
        catch (IllegalAccessException e)
          {
            e.printStackTrace();
          }
        if (nms_world_broadcastEntityEffect == null)
          {
            nms_world_broadcastEntityEffect =
                getMethod(nms_world.getClass(), "broadcastEntityEffect");
          }
        FireworkMeta data = (FireworkMeta) firework.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(effects);
        firework.setFireworkMeta(data);
        try
          {
            nms_world_broadcastEntityEffect.invoke(nms_world, new Object[] {
                nms_firework, (byte) 17});
          }
        catch (IllegalAccessException e)
          {
            e.printStackTrace();
          }
        catch (InvocationTargetException e)
          {
            e.printStackTrace();
          }
        firework.remove();
      }
  }
