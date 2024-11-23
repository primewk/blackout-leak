package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.Stats;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.FriendsManager;
import bodevelopment.client.blackout.manager.managers.StatsManager;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.class_742;

public class StatsHUD extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgData = this.addGroup("Data");
   private final Setting<StatsHUD.TargetMode> targetMode;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final TextColorMultiSetting textColor;
   private final BackgroundMultiSetting background;
   private final Setting<Boolean> hole;
   private final Setting<Boolean> phased;
   private final Setting<Boolean> pops;
   private final Setting<Boolean> eaten;
   private final Setting<Boolean> bottles;
   private final Setting<Boolean> moved;
   private final Setting<Boolean> damage;

   public StatsHUD() {
      super("Stats", ".");
      this.targetMode = this.sgGeneral.e("Target", StatsHUD.TargetMode.Enemy, ".");
      this.bg = this.sgGeneral.b("Background", true, ".");
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.hole = this.sgData.b("In Hole", true, ".");
      this.phased = this.sgData.b("Phased", true, ".");
      this.pops = this.sgData.b("Pops", true, ".");
      this.eaten = this.sgData.b("Eaten", true, ".");
      this.bottles = this.sgData.b("Bottles", true, ".");
      this.moved = this.sgData.b("Moved", true, ".");
      this.damage = this.sgData.b("Damage Taken", true, ".");
      this.setSize(50.0F, 50.0F);
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   public void render() {
      class_742 target = this.getTarget();
      if (target != null) {
         StatsManager.TrackerData data = Managers.STATS.getStats(target);
         if (data != null) {
            int statCount = this.statCount();
            this.stack.method_22903();
            this.setSize(Math.max(50.0F, BlackOut.FONT.getWidth(target.method_7334().getName()) * 1.5F + 20.0F), BlackOut.FONT.getHeight() * 1.5F + (float)statCount * BlackOut.FONT.getHeight() + 10.0F);
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(0.0F, 0.0F, this.getWidth() / this.getScale(), this.getHeight() / this.getScale(), 3.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.bg.get()) {
               this.background.render(this.stack, 0.0F, 0.0F, this.getWidth() / this.getScale(), this.getHeight() / this.getScale(), 3.0F, 3.0F);
            }

            this.textColor.render(this.stack, target.method_7334().getName(), 1.5F, this.getWidth() / 2.0F / this.getScale(), 0.0F, true, false);
            this.stack.method_22904(0.0D, (double)BlackOut.FONT.getHeight() * 1.5D + 10.0D, 0.0D);
            Stats[] var4 = Stats.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Stats stat = var4[var6];
               if (this.shouldRender(stat)) {
                  this.textColor.render(this.stack, this.getStat(stat, data), 1.0F, 0.0F, 0.0F, false, true);
                  this.stack.method_46416(0.0F, BlackOut.FONT.getHeight(), 0.0F);
               }
            }

            this.stack.method_22909();
         }
      }
   }

   private String getStat(Stats stat, StatsManager.TrackerData data) {
      String var10000;
      long var3;
      switch(stat) {
      case Hole:
         var3 = (long)data.inHoleFor;
         var10000 = "In Hole: " + OLEPOSSUtils.getTimeString(var3 * 50L);
         break;
      case Phased:
         var3 = (long)data.phasedFor;
         var10000 = "Phased: " + OLEPOSSUtils.getTimeString(var3 * 50L);
         break;
      case Pops:
         var10000 = "Pops: " + data.pops;
         break;
      case Eaten:
         var10000 = "Eaten: " + data.eaten;
         break;
      case Bottles:
         var10000 = "Bottles: " + data.bottles;
         break;
      case Moved:
         var10000 = "Moved: " + data.blocksMoved;
         break;
      case Damage:
         var10000 = String.format("Damage: %.1f", data.damage);
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private boolean shouldRender(Stats stat) {
      Setting var10000;
      switch(stat) {
      case Hole:
         var10000 = this.hole;
         break;
      case Phased:
         var10000 = this.phased;
         break;
      case Pops:
         var10000 = this.pops;
         break;
      case Eaten:
         var10000 = this.eaten;
         break;
      case Bottles:
         var10000 = this.bottles;
         break;
      case Moved:
         var10000 = this.moved;
         break;
      case Damage:
         var10000 = this.damage;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return (Boolean)var10000.get();
   }

   private int statCount() {
      int stats = 0;
      Stats[] var2 = Stats.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Stats stat = var2[var4];
         if (this.shouldRender(stat)) {
            ++stats;
         }
      }

      return stats;
   }

   private class_742 getTarget() {
      Object var10000;
      switch((StatsHUD.TargetMode)this.targetMode.get()) {
      case Enemy:
         var10000 = this.getClosest((player) -> {
            return player != BlackOut.mc.field_1724 && !Managers.FRIENDS.isFriend(player);
         });
         break;
      case Friend:
         FriendsManager var10001 = Managers.FRIENDS;
         Objects.requireNonNull(var10001);
         var10000 = this.getClosest(var10001::isFriend);
         break;
      case Own:
         var10000 = BlackOut.mc.field_1724;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return (class_742)var10000;
   }

   private class_742 getClosest(Predicate<class_742> predicate) {
      double closestDist = 0.0D;
      class_742 closest = null;
      Iterator var5 = BlackOut.mc.field_1687.method_18456().iterator();

      while(true) {
         class_742 player;
         double d;
         do {
            do {
               if (!var5.hasNext()) {
                  return closest;
               }

               player = (class_742)var5.next();
               d = (double)BlackOut.mc.field_1724.method_5739(player);
            } while(!predicate.test(player));
         } while(closest != null && d > closestDist);

         closest = player;
         closestDist = d;
      }
   }

   public static enum TargetMode {
      Enemy,
      Friend,
      Own;

      // $FF: synthetic method
      private static StatsHUD.TargetMode[] $values() {
         return new StatsHUD.TargetMode[]{Enemy, Friend, Own};
      }
   }
}
