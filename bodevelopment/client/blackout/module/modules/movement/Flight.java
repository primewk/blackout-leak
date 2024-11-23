package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.MovementUtils;
import net.minecraft.class_1268;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2828.class_2829;

public class Flight extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Flight.Mode> mode;
   private final Setting<Double> h;
   private final Setting<Double> v;
   private final Setting<Double> timer;
   private final Setting<Boolean> antiKick;
   public final Setting<Integer> delay;
   private final Setting<Double> verusSpeed;
   private final Setting<Integer> verusTicks;
   private final Setting<Flight.FallMode> fallMode;
   private final Setting<Double> verusBowSpeed;
   private final Setting<Double> verusLimit;
   private final Setting<Double> verusDMGSpeed;
   private final Setting<Double> verusDMGheight;
   private final Setting<Double> verusDMGLimit;
   private static int i = 0;
   private static int dmgFlyTicks = 0;
   private double startY;
   private static int ticks = 0;
   private static boolean jumped = false;
   private boolean changedTimer;
   private boolean damaged;

   public Flight() {
      super("Flight", "Flies", SubCategory.MOVEMENT, true);
      this.mode = this.sgGeneral.e("Mode", Flight.Mode.Motion, ".", () -> {
         return true;
      });
      this.h = this.sgGeneral.d("Horizontal", 0.5D, 0.0D, 10.0D, 0.05D, ".", () -> {
         return this.mode.get() == Flight.Mode.Motion;
      });
      this.v = this.sgGeneral.d("Vertical", 0.5D, 0.0D, 10.0D, 0.05D, ".", () -> {
         return this.mode.get() == Flight.Mode.Motion;
      });
      this.timer = this.sgGeneral.d("Timer", 1.0D, 0.05D, 10.0D, 0.05D, ".", () -> {
         return this.mode.get() == Flight.Mode.Motion;
      });
      this.antiKick = this.sgGeneral.b("Anti Kick", true, ".", () -> {
         return this.mode.get() == Flight.Mode.Motion;
      });
      this.delay = this.sgGeneral.i("Anti Kick delay", 2, 0, 20, 1, ".", () -> {
         return this.mode.get() == Flight.Mode.Motion && (Boolean)this.antiKick.get();
      });
      this.verusSpeed = this.sgGeneral.d("Verus Speed", 0.4D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.mode.get() == Flight.Mode.Verus;
      });
      this.verusTicks = this.sgGeneral.i("Verus Ticks", 1, 0, 50, 1, ".", () -> {
         return this.mode.get() == Flight.Mode.Verus;
      });
      this.fallMode = this.sgGeneral.e("Fall Mode", Flight.FallMode.Smart, ".", () -> {
         return this.mode.get() == Flight.Mode.Verus;
      });
      this.verusBowSpeed = this.sgGeneral.d("Bow Speed", 5.0D, 0.0D, 10.0D, 0.1D, ".", () -> {
         return this.mode.get() == Flight.Mode.VerusBow;
      });
      this.verusLimit = this.sgGeneral.d("Tick Limit", 20.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.mode.get() == Flight.Mode.VerusBow;
      });
      this.verusDMGSpeed = this.sgGeneral.d("Damage Speed", 9.95D, 0.0D, 10.0D, 0.05D, ".", () -> {
         return this.mode.get() == Flight.Mode.VerusDMG;
      });
      this.verusDMGheight = this.sgGeneral.d("DMG height", 3.05D, 3.05D, 10.0D, 0.05D, ".", () -> {
         return this.mode.get() == Flight.Mode.VerusDMG;
      });
      this.verusDMGLimit = this.sgGeneral.d("Fly ticks", 20.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.mode.get() == Flight.Mode.VerusDMG;
      });
      this.startY = 0.0D;
      this.changedTimer = false;
      this.damaged = false;
   }

   public void onEnable() {
      ticks = 0;
      dmgFlyTicks = 0;
      jumped = false;
      this.damaged = false;
      this.startY = BlackOut.mc.field_1724.method_23318();
      if (this.mode.get() == Flight.Mode.VerusBow) {
         Managers.NOTIFICATIONS.addNotification("Shoot yourself with a bow", this.getDisplayName(), 2.0D, Notifications.Type.Info);
      }

      if (this.mode.get() == Flight.Mode.Verus) {
         Managers.NOTIFICATIONS.addNotification("Hold blocks in your hand to prevent flagging", this.getDisplayName(), 2.0D, Notifications.Type.Info);
      }

      if (this.mode.get() == Flight.Mode.VerusDMG) {
         this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), false));
         this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + (Double)this.verusDMGheight.get(), BlackOut.mc.field_1724.method_23321(), false));
         this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), false));
         this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), true));
      }

   }

   public String getInfo() {
      return ((Flight.Mode)this.mode.get()).name();
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (jumped) {
            ++ticks;
         }

         if (this.enabled && (Double)this.timer.get() != 1.0D && this.mode.get() == Flight.Mode.Motion) {
            Timer.set(((Double)this.timer.get()).floatValue());
            this.changedTimer = true;
         }

         if (this.mode.get() == Flight.Mode.VerusDMG && this.damaged) {
            this.changedTimer = true;
            ++dmgFlyTicks;
         }

      }
   }

   public void onDisable() {
      if (this.changedTimer) {
         Timer.reset();
         this.changedTimer = false;
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         ++i;
         double y = 0.0D;
         switch((Flight.Mode)this.mode.get()) {
         case Motion:
            if (BlackOut.mc.field_1690.field_1903.method_1434()) {
               y = (Double)this.v.get();
            } else if (BlackOut.mc.field_1690.field_1832.method_1434() && !BlackOut.mc.field_1724.method_24828()) {
               y = -(Double)this.v.get();
            }

            if ((Boolean)this.antiKick.get() && i > (Integer)this.delay.get()) {
               y = Math.min(y, -0.0315D);
               i = 0;
            }

            event.setY(this, y);
            if (!Managers.ROTATION.move) {
               return;
            }

            event.setXZ(this, MovementUtils.xMovement((Double)this.h.get(), (double)Managers.ROTATION.moveYaw), MovementUtils.zMovement((Double)this.h.get(), (double)Managers.ROTATION.moveYaw));
            break;
         case Verus:
            if (BlackOut.mc.field_1724.method_23318() == this.startY) {
               if (this.jumping()) {
                  this.jump();
                  ++this.startY;
               } else if (this.sneaking()) {
                  this.jump();
                  --this.startY;
               } else if (++ticks > (Integer)this.verusTicks.get()) {
                  this.jump();
               } else {
                  event.setY(this, 0.0D);
               }
            } else {
               ticks = 0;
               if (event.originalMovement.field_1351 < 0.0D && BlackOut.mc.field_1724.method_23318() > this.startY && !this.jumping() && !this.sneaking()) {
                  switch((Flight.FallMode)this.fallMode.get()) {
                  case Slow:
                     event.setY(this, -0.1D);
                     break;
                  case VerySlow:
                     event.setY(this, -0.001D);
                     break;
                  case Smart:
                     event.setY(this, -0.3D * Math.pow(Math.abs(BlackOut.mc.field_1724.method_23318() - this.startY), 2.0D));
                  }
               }

               if (BlackOut.mc.field_1724.method_23318() + event.movement.field_1351 <= this.startY) {
                  event.setY(this, this.startY - BlackOut.mc.field_1724.method_23318());
                  Managers.PACKET.spoofOG(true);
               }
            }

            class_2338 pos = BlackOut.mc.field_1724.method_24515();
            if (!BlackOut.mc.field_1724.method_24828()) {
               this.placeBlock(class_1268.field_5808, pos.method_46558(), class_2350.field_11036, pos);
            }

            if (!Managers.ROTATION.move) {
               return;
            }

            event.setXZ(this, MovementUtils.xMovement((Double)this.verusSpeed.get(), (double)Managers.ROTATION.moveYaw), MovementUtils.zMovement((Double)this.verusSpeed.get(), (double)Managers.ROTATION.moveYaw));
            break;
         case VerusBow:
            if (BlackOut.mc.field_1724.field_6235 > 0) {
               if (!jumped) {
                  this.startY = BlackOut.mc.field_1724.method_23318();
               }

               jumped = true;
               event.setXZ(this, MovementUtils.xMovement((Double)this.verusBowSpeed.get(), (double)Managers.ROTATION.moveYaw), MovementUtils.zMovement((Double)this.verusBowSpeed.get(), (double)Managers.ROTATION.moveYaw));
               if (BlackOut.mc.field_1724.method_23318() + event.originalMovement.field_1351 < this.startY) {
                  event.setY(this, this.startY - BlackOut.mc.field_1724.method_23318());
                  Managers.PACKET.spoofOG(true);
               }
            }

            if ((double)ticks >= (Double)this.verusLimit.get()) {
               event.setXZ(this, MovementUtils.xMovement(0.2873D, (double)Managers.ROTATION.moveYaw), MovementUtils.zMovement(0.2873D, (double)Managers.ROTATION.moveYaw));
               Managers.NOTIFICATIONS.addNotification("Reached tick limit", this.getDisplayName(), 2.0D, Notifications.Type.Info);
               this.toggle();
            }
            break;
         case VerusDMG:
            if (BlackOut.mc.field_1724.field_6235 > 0) {
               this.damaged = true;
            }

            if (!this.damaged) {
               return;
            }

            event.setY(this, 0.0D);
            event.setXZ(this, MovementUtils.xMovement((Double)this.verusDMGSpeed.get(), (double)Managers.ROTATION.moveYaw), MovementUtils.zMovement((Double)this.verusDMGSpeed.get(), (double)Managers.ROTATION.moveYaw));
            Timer.set(0.1F);
            if ((double)dmgFlyTicks > (Double)this.verusDMGLimit.get()) {
               event.setXZ(this, MovementUtils.xMovement(0.2873D, (double)Managers.ROTATION.moveYaw), MovementUtils.zMovement(0.2873D, (double)Managers.ROTATION.moveYaw));
               Timer.reset();
               this.disable();
            }
         }

      }
   }

   private void jump() {
      BlackOut.mc.field_1724.method_6043();
   }

   private boolean jumping() {
      return BlackOut.mc.field_1690.field_1903.method_1434();
   }

   private boolean sneaking() {
      return BlackOut.mc.field_1690.field_1832.method_1434();
   }

   public static enum Mode {
      Motion,
      Verus,
      VerusBow,
      VerusDMG;

      // $FF: synthetic method
      private static Flight.Mode[] $values() {
         return new Flight.Mode[]{Motion, Verus, VerusBow, VerusDMG};
      }
   }

   public static enum FallMode {
      Vanilla,
      Slow,
      VerySlow,
      Smart;

      // $FF: synthetic method
      private static Flight.FallMode[] $values() {
         return new Flight.FallMode[]{Vanilla, Slow, VerySlow, Smart};
      }
   }
}
