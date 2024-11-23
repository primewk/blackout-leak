package bodevelopment.client.blackout.module;

import java.util.ArrayList;
import java.util.List;

public class SubCategory implements Category {
   public static List<SubCategory> categories = new ArrayList();
   public static SubCategory DEFENSIVE;
   public static SubCategory OFFENSIVE;
   public static SubCategory MISC_COMBAT;
   public static SubCategory MOVEMENT;
   public static SubCategory ENTITIES;
   public static SubCategory WORLD;
   public static SubCategory MISC_VISUAL;
   public static SubCategory MISC;
   public static SubCategory MEMES;
   public static SubCategory LEGIT;
   public static SubCategory CLIENT;
   public static SubCategory SETTINGS;
   private final String name;
   public final ParentCategory parent;

   public SubCategory(String name, ParentCategory parent) {
      categories.add(this);
      this.name = name;
      this.parent = parent;
   }

   public String name() {
      return this.name;
   }

   static {
      DEFENSIVE = new SubCategory("Defensive", ParentCategory.COMBAT);
      OFFENSIVE = new SubCategory("Offensive", ParentCategory.COMBAT);
      MISC_COMBAT = new SubCategory("Misc", ParentCategory.COMBAT);
      MOVEMENT = new SubCategory("Movement", ParentCategory.MOVEMENT);
      ENTITIES = new SubCategory("Entities", ParentCategory.VISUAL);
      WORLD = new SubCategory("World", ParentCategory.VISUAL);
      MISC_VISUAL = new SubCategory("Misc", ParentCategory.VISUAL);
      MISC = new SubCategory("Misc", ParentCategory.MISC);
      MEMES = new SubCategory("Memes", ParentCategory.MISC);
      LEGIT = new SubCategory("Legit", ParentCategory.LEGIT);
      CLIENT = new SubCategory("Client", ParentCategory.CLIENT);
      SETTINGS = new SubCategory("Settings", ParentCategory.CLIENT);
   }
}
