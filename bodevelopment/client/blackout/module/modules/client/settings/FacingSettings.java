package bodevelopment.client.blackout.module.modules.client.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.Objects;
import net.minecraft.class_2199;
import net.minecraft.class_2244;
import net.minecraft.class_2248;
import net.minecraft.class_2304;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;

public class FacingSettings extends SettingsModule {
   private static FacingSettings INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> strictDir;
   public final Setting<Boolean> ncpDirection;
   public final Setting<Boolean> unblocked;
   public final Setting<Boolean> airPlace;
   public final Setting<FacingSettings.MaxHeight> maxHeight;

   public FacingSettings() {
      super("Facing", false, true);
      this.strictDir = this.sgGeneral.b("Strict Direction", false, "Doesn't place on faces which aren't in your direction.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.strictDir;
      Objects.requireNonNull(var10005);
      this.ncpDirection = var10001.b("NCP Directions", false, ".", var10005::get);
      this.unblocked = this.sgGeneral.b("Unblocked", false, "Doesn't place on faces that have block on them.");
      this.airPlace = this.sgGeneral.b("Air Place", false, "Allows placing blocks in air.");
      this.maxHeight = this.sgGeneral.e("Max Height", FacingSettings.MaxHeight.New, "Doesn't place on top sides of blocks at max height. Old: 1.12, New: 1.17+");
      INSTANCE = this;
   }

   public static FacingSettings getInstance() {
      return INSTANCE;
   }

   public PlaceData getPlaceData(class_2338 blockPos, DoublePredicate<class_2338, class_2350> predicateOR, DoublePredicate<class_2338, class_2350> predicateAND, boolean ignoreContainers) {
      class_2350 direction = null;
      boolean closestSneak = false;
      double closestDist = 1000.0D;
      class_2350[] var9 = class_2350.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         class_2350 dir = var9[var11];
         class_2338 pos = blockPos.method_10093(dir);
         boolean sneak = this.ignoreBlock(this.state(pos));
         if (!this.outOfBuildHeightCheck(pos) && (!sneak || !ignoreContainers) && (!(Boolean)this.strictDir.get() || OLEPOSSUtils.strictDir(pos, dir.method_10153(), (Boolean)this.ncpDirection.get())) && (predicateOR != null && predicateOR.test(pos, dir) || this.solid(pos) && (predicateAND == null || predicateAND.test(pos, dir)))) {
            double dist = SettingUtils.placeRangeTo(pos.method_10093(dir));
            if (direction == null || dist < closestDist) {
               closestDist = dist;
               direction = dir;
               closestSneak = sneak;
            }
         }
      }

      if ((Boolean)this.airPlace.get()) {
         return new PlaceData(blockPos, class_2350.field_11036, true, false);
      } else if (direction == null) {
         return new PlaceData((class_2338)null, (class_2350)null, false, false);
      } else {
         return new PlaceData(blockPos.method_10093(direction), direction.method_10153(), true, closestSneak);
      }
   }

   private boolean ignoreBlock(class_2680 state) {
      if (state.method_31709()) {
         return true;
      } else {
         class_2248 block = state.method_26204();
         return block instanceof class_2199 || block instanceof class_2244 || block instanceof class_2304;
      }
   }

   public class_2350 getPlaceOnDirection(class_2338 position) {
      class_2350 direction = null;
      double closestDist = 1000.0D;
      class_2350[] var5 = class_2350.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         class_2350 dir = var5[var7];
         class_2338 pos = position.method_10093(dir);
         if (!this.outOfBuildHeightCheck(pos) && (!(Boolean)this.unblocked.get() || !this.solid(pos)) && (!(Boolean)this.strictDir.get() || OLEPOSSUtils.strictDir(position, dir, (Boolean)this.ncpDirection.get()))) {
            double dist = this.dist(position, dir);
            if (direction == null || dist < closestDist) {
               closestDist = dist;
               direction = dir;
            }
         }
      }

      return direction;
   }

   private boolean solid(class_2338 pos) {
      return this.state(pos).method_51367();
   }

   private class_2680 state(class_2338 pos) {
      return Managers.BLOCK.blockState(pos);
   }

   private boolean outOfBuildHeightCheck(class_2338 pos) {
      int var10000 = pos.method_10264();
      short var10001;
      switch((FacingSettings.MaxHeight)this.maxHeight.get()) {
      case Old:
         var10001 = 255;
         break;
      case New:
         var10001 = 319;
         break;
      case Disabled:
         var10001 = 1000;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000 > var10001;
   }

   private double dist(class_2338 pos, class_2350 dir) {
      class_243 vec = new class_243((double)((float)pos.method_10263() + (float)dir.method_10148() / 2.0F), (double)((float)pos.method_10264() + (float)dir.method_10164() / 2.0F), (double)((float)pos.method_10260() + (float)dir.method_10165() / 2.0F));
      class_243 dist = BlackOut.mc.field_1724.method_33571().method_1020(vec);
      return dist.method_1033();
   }

   public static enum MaxHeight {
      Old,
      New,
      Disabled;

      // $FF: synthetic method
      private static FacingSettings.MaxHeight[] $values() {
         return new FacingSettings.MaxHeight[]{Old, New, Disabled};
      }
   }
}
