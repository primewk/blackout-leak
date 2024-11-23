package bodevelopment.client.blackout.enums;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.ParentCategory;
import java.util.function.Predicate;

public enum ConfigType {
   Combat((module) -> {
      return module.category.parent == ParentCategory.COMBAT;
   }),
   Movement((module) -> {
      return module.category.parent == ParentCategory.MOVEMENT;
   }),
   Visual((module) -> {
      return module.category.parent == ParentCategory.VISUAL;
   }),
   Misc((module) -> {
      return module.category.parent == ParentCategory.MISC;
   }),
   Legit((module) -> {
      return module.category.parent == ParentCategory.LEGIT;
   }),
   Client((module) -> {
      return module.category.parent == ParentCategory.CLIENT;
   }),
   HUD((Predicate)null),
   Binds((Predicate)null);

   public final Predicate<Module> predicate;

   private ConfigType(Predicate<Module> predicate) {
      this.predicate = predicate;
   }

   // $FF: synthetic method
   private static ConfigType[] $values() {
      return new ConfigType[]{Combat, Movement, Visual, Misc, Legit, Client, HUD, Binds};
   }
}
