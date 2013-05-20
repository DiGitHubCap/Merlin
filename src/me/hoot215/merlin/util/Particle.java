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

import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum Particle
  {
    HUGE_EXPLOSION("hugeexplosion"),
    LARGE_EXPLODE("largeexplode"),
    FIREWORKS_SPARK("fireworksSpark"),
    BUBBLE("bubble"),
    SUSPEND("suspend"),
    DEPTH_SUSPEND("depthSuspend"),
    TOWN_AURA("townaura"),
    CRIT("crit"),
    MAGIC_CRIT("magicCrit"),
    MOB_SPELL("mobSpell"),
    MOB_SPELL_AMBIENT("mobSpellAmbient"),
    SPELL("spell"),
    INSTANT_SPELL("instantSpell"),
    WITCH_MAGIC("witchMagic"),
    NOTE("note"),
    PORTAL("portal"),
    ENCHANTMENT_TABLE("enchantmenttable"),
    EXPLODE("explode"),
    FLAME("flame"),
    LAVA("lava"),
    FOOTSTEP("footstep"),
    SPLASH("splash"),
    LARGE_SMOKE("largesmoke"),
    CLOUD("cloud"),
    RED_DUST("reddust"),
    SNOWBALL_POOF("snowballpoof"),
    DRIP_WATER("dripWater"),
    DRIP_LAVA("dripLava"),
    SNOW_SHOVEL("snowshovel"),
    SLIME("slime"),
    HEART("heart"),
    ANGRY_VILLAGER("angryVillager"),
    HAPPY_VILLAGER("happyVillager"),
    ICONCRACK("iconcrack_"),
    TILECRACK("tilecrack_");
    
    private static Class<?> nms_packet;
    private static Class<?> nms_packet63WorldParticles;
    private static volatile Method craftPlayer_getHandle;
    private static volatile Method playerConnection_sendPacket;
    
    static
      {
        for (Package pkg : Package.getPackages())
          {
            try
              {
                nms_packet = Class.forName(pkg.getName() + ".Packet");
                nms_packet63WorldParticles =
                    Class.forName(pkg.getName() + ".Packet63WorldParticles");
                break;
              }
            catch (ClassNotFoundException e)
              {
                continue;
              }
          }
      }
    
    private String name;
    
    Particle(String name)
      {
        this.name = name;
      }
    
    public void sendToPlayer (Player player, Location location, float offsetX,
      float offsetY, float offsetZ, float speed, int count)
      {
        try
          {
            Object packet = nms_packet63WorldParticles.newInstance();
            Reflection.setValue(packet, "a", name);
            Reflection.setValue(packet, "b", (float) location.getX());
            Reflection.setValue(packet, "c", (float) location.getY());
            Reflection.setValue(packet, "d", (float) location.getZ());
            Reflection.setValue(packet, "e", offsetX);
            Reflection.setValue(packet, "f", offsetY);
            Reflection.setValue(packet, "g", offsetZ);
            Reflection.setValue(packet, "h", speed);
            Reflection.setValue(packet, "i", count);
            if (craftPlayer_getHandle == null)
              {
                craftPlayer_getHandle =
                    player.getClass().getMethod("getHandle", new Class[] {});
              }
            Object playerConnection =
                Reflection.getValue(craftPlayer_getHandle.invoke(player),
                    "playerConnection");
            if (playerConnection_sendPacket == null)
              {
                playerConnection_sendPacket =
                    playerConnection.getClass().getMethod("sendPacket",
                        nms_packet);
              }
            playerConnection_sendPacket.invoke(playerConnection, packet);
          }
        catch (IllegalAccessException e)
          {
            e.printStackTrace();
          }
        catch (InvocationTargetException e)
          {
            e.printStackTrace();
          }
        catch (NoSuchMethodException e)
          {
            e.printStackTrace();
          }
        catch (InstantiationException e)
          {
            e.printStackTrace();
          }
      }
    
    public static void nullStaticVariables ()
      {
        nms_packet = null;
        nms_packet63WorldParticles = null;
        craftPlayer_getHandle = null;
        playerConnection_sendPacket = null;
      }
  }
