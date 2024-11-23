package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.ObsidianModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.Comparator;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2350;

public class AutoTrap extends ObsidianModule {
   private final Setting<AutoTrap.TrapMode> trapMode;
   private final class_2350[] directions;

   public AutoTrap() {
      super("Auto Trap", "Covers enemies in blocks.", SubCategory.MISC_COMBAT);
      this.trapMode = this.sgGeneral.e("Trap Mode", AutoTrap.TrapMode.Both, "");
      this.directions = new class_2350[]{class_2350.field_11043, class_2350.field_11035, class_2350.field_11034, class_2350.field_11039, class_2350.field_11036};
   }

   protected void addPlacements() {
      this.insideBlocks.forEach((pos) -> {
         class_2350[] var2 = this.directions;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_2350 dir = var2[var4];
            if (((AutoTrap.TrapMode)this.trapMode.get()).allowed(dir) && !this.blockPlacements.contains(pos.method_10093(dir)) && !this.insideBlocks.contains(pos.method_10093(dir))) {
               this.blockPlacements.add(pos.method_10093(dir));
            }
         }

      });
   }

   protected void addInsideBlocks() {
      BlackOut.mc.field_1687.method_18456().stream().filter((player) -> {
         return BlackOut.mc.field_1724.method_5739(player) < 15.0F && player != BlackOut.mc.field_1724 && !Managers.FRIENDS.isFriend(player);
      }).sorted(Comparator.comparingDouble((player) -> {
         return (double)BlackOut.mc.field_1724.method_5739(player);
      })).forEach((player) -> {
         this.addBlocks(player, this.getSize(player));
      });
   }

   protected void addBlocks(class_1297 entity, int[] size) {
      int eyeY = (int)Math.ceil(entity.method_5829().field_1325);

      for(int x = size[0]; x <= size[1]; ++x) {
         for(int z = size[2]; z <= size[3]; ++z) {
            class_2338 p = entity.method_24515().method_10069(x, 0, z).method_33096(eyeY - 1);
            if (!(BlackOut.mc.field_1687.method_8320(p).method_26204().method_9520() > 600.0F) && SettingUtils.inPlaceRange(p)) {
               this.insideBlocks.add(p);
            }
         }
      }

   }

   public static enum TrapMode {
      Top,
      Eyes,
      Both;

      public boolean allowed(class_2350 dir) {
         boolean var10000;
         switch(this) {
         case Top:
            var10000 = dir == class_2350.field_11036;
            break;
         case Eyes:
            var10000 = dir.method_10164() == 0;
            break;
         case Both:
            var10000 = true;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return var10000;
      }

      // $FF: synthetic method
      private static AutoTrap.TrapMode[] $values() {
         return new AutoTrap.TrapMode[]{Top, Eyes, Both};
      }
   }
}
