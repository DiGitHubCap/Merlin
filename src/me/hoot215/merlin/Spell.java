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

import java.util.HashMap;
import java.util.Map;

import me.hoot215.merlin.spells.*;
import me.hoot215.merlin.util.Levenshtein;

import org.bukkit.ChatColor;
import org.bukkit.event.Event;

public enum Spell
  {
    SPARK(
        new Spark(),
        0,
        Type.FIRE,
        0,
        new Trigger[] {Trigger.LEFT_CLICK_BLOCK, Trigger.RIGHT_CLICK_BLOCK,
            Trigger.DAMAGE},
        new int[] {50, 50, 100},
        5),
    FERTILIZE(new Fertilize(), Type.EARTH, 50, 5),
    NATURALIZE(new Naturalize(), Type.EARTH, 40, 4),
    SMITE(new Smite(), 0, Type.FIRE, 5, new Trigger[] {Trigger.LEFT_CLICK_AIR,
        Trigger.LEFT_CLICK_BLOCK, Trigger.RIGHT_CLICK_AIR,
        Trigger.RIGHT_CLICK_BLOCK, Trigger.DAMAGE}, new int[] {500, 500, 500,
        500, 150}, 15),
    HEAL(new Heal(), Type.HEALING, 100, 5),
    BLINK(new Blink(), Type.TELEPORTATION, 0, 0),
    FIRE_RING(new FireRing(), Type.FIRE, 150, 15),
    ENCHANT_LEATHER_PROTECTION(
        new EnchantLeatherProtection(),
        Type.ENCHANTMENT,
        100,
        10),
    ENCHANT_IRON_PROTECTION(
        new EnchantIronProtection(),
        0,
        Type.ENCHANTMENT,
        3,
        200,
        20),
    ENCHANT_GOLD_PROTECTION(
        new EnchantGoldProtection(),
        0,
        Type.ENCHANTMENT,
        4,
        250,
        25),
    ENCHANT_DIAMOND_PROTECTION(
        new EnchantDiamondProtection(),
        0,
        Type.ENCHANTMENT,
        10,
        500,
        50),
    ENCHANT_RESPIRATION(
        new EnchantRespiration(),
        0,
        Type.ENCHANTMENT,
        5,
        300,
        30),
    ENCHANT_AQUA_AFFINITY(
        new EnchantAquaAffinity(),
        0,
        Type.ENCHANTMENT,
        6,
        500,
        50),
    ENCHANT_THORNS(new EnchantThorns(), 0, Type.ENCHANTMENT, 7, 600, 60),
    ENCHANT_UNBREAKING(new EnchantUnbreaking(), 0, Type.ENCHANTMENT, 8, 700, 70),
    ENCHANT_SHARPNESS(new EnchantSharpness(), 0, Type.ENCHANTMENT, 9, 800, 80),
    ENCHANT_SMITE(new EnchantSmite(), 0, Type.ENCHANTMENT, 11, 1000, 100),
    ENCHANT_BANE_OF_ARTHROPODS(
        new EnchantBaneOfArthropods(),
        0,
        Type.ENCHANTMENT,
        6,
        500,
        50),
    ENCHANT_KNOCKBACK(
        new EnchantKnockback(),
        0,
        Type.ENCHANTMENT,
        12,
        1100,
        110),
    ENCHANT_FIRE_ASPECT(
        new EnchantFireAspect(),
        0,
        Type.ENCHANTMENT,
        13,
        1200,
        120),
    ENCHANT_LOOTING(new EnchantLooting(), 0, Type.ENCHANTMENT, 13, 1200, 120),
    ENCHANT_EFFICIENCY(
        new EnchantEfficiency(),
        0,
        Type.ENCHANTMENT,
        14,
        1300,
        130),
    ENCHANT_SILK_TOUCH(
        new EnchantSilkTouch(),
        0,
        Type.ENCHANTMENT,
        15,
        1400,
        140),
    ENCHANT_FORTUNE(new EnchantFortune(), 0, Type.ENCHANTMENT, 16, 1500, 150),
    ENCHANT_POWER(new EnchantPower(), 0, Type.ENCHANTMENT, 17, 1600, 160),
    ENCHANT_PUNCH(new EnchantPunch(), 0, Type.ENCHANTMENT, 18, 1700, 170),
    ENCHANT_INFINITY(new EnchantInfinity(), 0, Type.ENCHANTMENT, 25, 2500, 250);
    
    private static final Map<String, Spell> SPELLS =
        new HashMap<String, Spell>();
    
    private String name;
    private String description;
    private String permission;
    private MagicSpell magicSpell;
    private int levelRequirement;
    private Type type;
    private int typeLevelRequirement;
    private Trigger[] triggers;
    private int[] costs;
    private int exp;
    
    Spell(MagicSpell magicSpell, Type type, int cost, int exp)
      {
        this(magicSpell, 0, type, 0, new Trigger[] {Trigger.LEFT_CLICK_AIR,
            Trigger.LEFT_CLICK_BLOCK, Trigger.RIGHT_CLICK_AIR,
            Trigger.RIGHT_CLICK_BLOCK}, new int[] {cost, cost, cost, cost}, exp);
      }
    
    Spell(MagicSpell magicSpell, Type type, Trigger[] triggers, int[] costs,
      int exp)
      {
        this(magicSpell, 0, type, 0, triggers, costs, exp);
      }
    
    Spell(MagicSpell magicSpell, int levelRequirement, Type type,
      int typeLevelRequirement, int cost, int exp)
      {
        this(magicSpell, levelRequirement, type, typeLevelRequirement,
            new Trigger[] {Trigger.LEFT_CLICK_AIR, Trigger.LEFT_CLICK_BLOCK,
                Trigger.RIGHT_CLICK_AIR, Trigger.RIGHT_CLICK_BLOCK}, new int[] {
                cost, cost, cost, cost}, exp);
      }
    
    Spell(MagicSpell magicSpell, int levelRequirement, Type type,
      int typeLevelRequirement, Trigger[] triggers, int[] costs, int exp)
      {
        SpellHandler spellHandler =
            magicSpell.getClass().getAnnotation(SpellHandler.class);
        name = spellHandler.name();
        description = spellHandler.description();
        permission = spellHandler.permission();
        this.magicSpell = magicSpell;
        this.levelRequirement = levelRequirement;
        this.type = type;
        this.typeLevelRequirement = typeLevelRequirement;
        this.triggers = triggers;
        this.costs = costs;
        this.exp = exp;
      }
    
    public String getName ()
      {
        return name;
      }
    
    public String getDescription ()
      {
        return description;
      }
    
    public String getPermission ()
      {
        return permission;
      }
    
    public int getLevelRequirement ()
      {
        return levelRequirement;
      }
    
    public Type getType ()
      {
        return type;
      }
    
    public int getTypeLevelRequirement ()
      {
        return typeLevelRequirement;
      }
    
    public int getCost (int actionIndex)
      {
        return costs[actionIndex];
      }
    
    public int getExp ()
      {
        return exp;
      }
    
    public int isActivatedBy (Trigger trigger)
      {
        if (trigger == null)
          return -1;
        for (int i = 0; i < triggers.length; i++)
          {
            if (triggers[i] == trigger)
              return i;
          }
        return -1;
      }
    
    public boolean cast (Sorcerer sorcerer, Trigger trigger, Event event)
      {
        return magicSpell.cast(sorcerer, trigger, event);
      }
    
    public static Spell getSpell (String spellName)
      {
        return SPELLS.get(spellName.toLowerCase());
      }
    
    public static Spell search (String spellName)
      {
        Spell closestMatch = null;
        int distance = -1;
        for (Spell s : Spell.values())
          {
            int d = Levenshtein.getDistance(spellName, s.getName());
            if (d < distance || distance == -1)
              {
                closestMatch = s;
                distance = d;
              }
          }
        return closestMatch;
      }
    
    static
      {
        for (Spell spell : Spell.values())
          {
            SPELLS.put(spell.getName().toLowerCase(), spell);
          }
      }
    
    public enum Type
      {
        AIR("Air", ChatColor.GRAY),
        WATER("Water", ChatColor.BLUE),
        EARTH("Earth", ChatColor.DARK_GREEN),
        FIRE("Fire", ChatColor.DARK_RED),
        HEALING("Healing", ChatColor.RED),
        ILLUSION("Illusion", ChatColor.DARK_GRAY),
        NECROMANCY("Necromancy", ChatColor.LIGHT_PURPLE),
        SCRYING("Scrying", ChatColor.YELLOW),
        TELEPORTATION("Teleportation", ChatColor.GREEN),
        TELEKINESIS("Telekinesis", ChatColor.AQUA),
        ENCHANTMENT("Enchantment", ChatColor.GOLD),
        DRAGONLORD("Dragonlord", ChatColor.BLACK);
        
        private String name;
        private ChatColor colour;
        
        Type(String name, ChatColor colour)
          {
            this.name = name;
            this.colour = colour;
          }
        
        public String getName ()
          {
            return name;
          }
        
        public ChatColor getColour ()
          {
            return colour;
          }
      }
  }
