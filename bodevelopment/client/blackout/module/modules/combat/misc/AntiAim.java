package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.RotationUtils;
import java.util.Iterator;
import net.minecraft.class_1297;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_3532;
import net.minecraft.class_4537;
import net.minecraft.class_742;

public class AntiAim extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgIgnore = this.addGroup("Ignore");
   private final Setting<AntiAim.Mode> mode;
   private final Setting<AntiAim.YawMode> yawMode;
   private final Setting<Double> range;
   private final Setting<Double> spinSpeed;
   private final Setting<Double> csgoYawMin;
   private final Setting<Double> csgoYawMax;
   private final Setting<Double> csgoPitchMin;
   private final Setting<Double> csgoPitchMax;
   private final Setting<Double> csgoSpeed;
   private final Setting<Double> customYaw;
   private final Setting<Double> customPitch;
   private final Setting<AntiAim.IgnoreMode> iExp;
   private final Setting<AntiAim.IgnoreMode> iPearl;
   private final Setting<AntiAim.IgnoreMode> iBow;
   private final Setting<AntiAim.IgnoreMode> iPotion;
   private double spinYaw;
   private long prevCsgo;
   private double csgoYaw;
   private double csgoPitch;

   public AntiAim() {
      super("Anti Aim", "Funi conter stik module.", SubCategory.MISC_COMBAT, true);
      this.mode = this.sgGeneral.e("Mode", AntiAim.Mode.Custom, ".");
      this.yawMode = this.sgGeneral.e("Yaw Mode", AntiAim.YawMode.Normal, ".", () -> {
         return this.mode.get() != AntiAim.Mode.Spin && this.mode.get() != AntiAim.Mode.Enemy;
      });
      this.range = this.sgGeneral.d("Range", 20.0D, 0.0D, 500.0D, 5.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.Enemy;
      });
      this.spinSpeed = this.sgGeneral.d("Spin Speed", 5.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.Spin;
      });
      this.csgoYawMin = this.sgGeneral.d("CSGO Yaw Min", -180.0D, -180.0D, 180.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.CSGO;
      });
      this.csgoYawMax = this.sgGeneral.d("CSGO Yaw Max", 180.0D, -180.0D, 180.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.CSGO;
      });
      this.csgoPitchMin = this.sgGeneral.d("CSGO Pitch Min", -90.0D, -90.0D, 90.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.CSGO;
      });
      this.csgoPitchMax = this.sgGeneral.d("CSGO Pitch Max", 90.0D, -90.0D, 90.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.CSGO;
      });
      this.csgoSpeed = this.sgGeneral.d("CSGO Speed", 5.0D, 0.0D, 50.0D, 1.0D, "How many times to update rotations each second.", () -> {
         return this.mode.get() == AntiAim.Mode.CSGO;
      });
      this.customYaw = this.sgGeneral.d("Yaw", 45.0D, -180.0D, 180.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.Custom;
      });
      this.customPitch = this.sgGeneral.d("Pitch", 90.0D, -90.0D, 90.0D, 1.0D, ".", () -> {
         return this.mode.get() == AntiAim.Mode.Custom;
      });
      this.iExp = this.sgIgnore.e("Ignore Experience", AntiAim.IgnoreMode.Down, ".");
      this.iPearl = this.sgIgnore.e("Ignore Pearl", AntiAim.IgnoreMode.FullIgnore, ".");
      this.iBow = this.sgIgnore.e("Ignore Bow", AntiAim.IgnoreMode.FullIgnore, ".");
      this.iPotion = this.sgIgnore.e("Ignore Potion", AntiAim.IgnoreMode.FullIgnore, ".");
      this.prevCsgo = 0L;
      this.csgoYaw = 0.0D;
      this.csgoPitch = 0.0D;
   }

   public void onEnable() {
      this.spinYaw = 0.0D;
   }

   public String getInfo() {
      return ((AntiAim.Mode)this.mode.get()).name();
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      double yaw = 0.0D;
      double pitch = 0.0D;
      class_742 target;
      switch((AntiAim.Mode)this.mode.get()) {
      case Enemy:
         target = this.getEnemy((Double)this.range.get());
         if (target != null) {
            yaw = RotationUtils.getYaw((class_1297)target);
         } else {
            yaw = (double)BlackOut.mc.field_1724.method_36454();
         }
         break;
      case Spin:
         this.spinYaw += (Double)this.spinSpeed.get();
         yaw = this.spinYaw;
         pitch = 0.0D;
         break;
      case CSGO:
         if ((double)System.currentTimeMillis() > (double)this.prevCsgo + 1000.0D / (Double)this.csgoSpeed.get()) {
            this.csgoYaw = class_3532.method_16436(Math.random(), (Double)this.csgoYawMin.get(), (Double)this.csgoYawMax.get());
            this.csgoPitch = class_3532.method_16436(Math.random(), (Double)this.csgoPitchMin.get(), (Double)this.csgoPitchMax.get());
            this.prevCsgo = System.currentTimeMillis();
         }

         yaw = this.csgoYaw;
         pitch = this.csgoPitch;
         break;
      case Custom:
         yaw = (Double)this.customYaw.get();
         pitch = (Double)this.customPitch.get();
      }

      if (this.mode.get() != AntiAim.Mode.Spin && this.mode.get() != AntiAim.Mode.Enemy) {
         switch((AntiAim.YawMode)this.yawMode.get()) {
         case RelativeOwn:
            yaw += (double)BlackOut.mc.field_1724.method_36454();
            break;
         case RelativeEnemy:
            target = this.getEnemy(0.0D);
            if (target != null) {
               yaw += RotationUtils.getYaw((class_1297)target);
            }
         }
      }

      AntiAim.IgnoreMode ignoreMode = this.getIgnore();
      if (ignoreMode == AntiAim.IgnoreMode.FullIgnore || ignoreMode == AntiAim.IgnoreMode.IgnoreYaw) {
         yaw = (double)BlackOut.mc.field_1724.method_36454();
      }

      switch(ignoreMode) {
      case FullIgnore:
      case IgnorePitch:
         pitch = (double)BlackOut.mc.field_1724.method_36455();
         break;
      case Down:
         pitch = 90.0D;
         break;
      case Up:
         pitch = -90.0D;
      }

      this.rotate((float)yaw, (float)pitch, RotationType.InstantOther, "");
   }

   private class_742 getEnemy(double r) {
      class_742 target = null;
      double dist = 1000.0D;
      Iterator var6 = BlackOut.mc.field_1687.method_18456().iterator();

      while(true) {
         class_742 player;
         double d;
         do {
            do {
               do {
                  do {
                     if (!var6.hasNext()) {
                        return target;
                     }

                     player = (class_742)var6.next();
                  } while(player == BlackOut.mc.field_1724);
               } while(Managers.FRIENDS.isFriend(player));
            } while(player.method_6032() <= 0.0F);

            d = (double)BlackOut.mc.field_1724.method_5739(player);
         } while(d > r && r > 0.0D);

         if (d < dist) {
            target = player;
            dist = d;
         }
      }
   }

   private AntiAim.IgnoreMode getIgnore() {
      AntiAim.IgnoreMode ignoreMode = this.getIgnore(BlackOut.mc.field_1724.method_6047().method_7909());
      if (ignoreMode == AntiAim.IgnoreMode.Disabled) {
         ignoreMode = this.getIgnore(BlackOut.mc.field_1724.method_6079().method_7909());
      }

      return ignoreMode;
   }

   private AntiAim.IgnoreMode getIgnore(class_1792 item) {
      if (item == class_1802.field_8287) {
         return (AntiAim.IgnoreMode)this.iExp.get();
      } else if (item == class_1802.field_8634) {
         return (AntiAim.IgnoreMode)this.iPearl.get();
      } else if (item != class_1802.field_8102 && item != class_1802.field_8399) {
         return item instanceof class_4537 ? (AntiAim.IgnoreMode)this.iPotion.get() : AntiAim.IgnoreMode.Disabled;
      } else {
         return (AntiAim.IgnoreMode)this.iBow.get();
      }
   }

   public static enum Mode {
      Enemy,
      Spin,
      CSGO,
      Custom;

      // $FF: synthetic method
      private static AntiAim.Mode[] $values() {
         return new AntiAim.Mode[]{Enemy, Spin, CSGO, Custom};
      }
   }

   public static enum YawMode {
      Normal,
      RelativeOwn,
      RelativeEnemy;

      // $FF: synthetic method
      private static AntiAim.YawMode[] $values() {
         return new AntiAim.YawMode[]{Normal, RelativeOwn, RelativeEnemy};
      }
   }

   public static enum IgnoreMode {
      FullIgnore,
      IgnoreYaw,
      IgnorePitch,
      Down,
      Up,
      Disabled;

      // $FF: synthetic method
      private static AntiAim.IgnoreMode[] $values() {
         return new AntiAim.IgnoreMode[]{FullIgnore, IgnoreYaw, IgnorePitch, Down, Up, Disabled};
      }
   }
}
