package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_2848;
import net.minecraft.class_3532;
import net.minecraft.class_2848.class_2849;

public class ElytraFly extends Module {
   private static ElytraFly INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgSpeed = this.addGroup("Speed");
   public final Setting<ElytraFly.Mode> mode;
   private final Setting<Integer> bounceDelay;
   private final Setting<Double> slowPitch;
   private final Setting<Double> fastPitch;
   private final Setting<Double> horizontal;
   private final Setting<Double> up;
   private final Setting<Double> speed;
   private final Setting<Double> upMultiplier;
   private final Setting<Double> down;
   private final Setting<Boolean> smartFall;
   private final Setting<Double> fallSpeed;
   private boolean moving;
   private float yaw;
   private float pitch;
   private float p;
   private double velocity;
   private int sinceFalling;
   private int sinceJump;
   private boolean sus;

   public ElytraFly() {
      super("Elytra Fly", ".", SubCategory.MOVEMENT, true);
      this.mode = this.sgGeneral.e("Mode", ElytraFly.Mode.Control, "How to sprint");
      this.bounceDelay = this.sgSpeed.i("Bounce Delay", 1, 0, 20, 1, "How many blocks to move each tick horizontally.", () -> {
         return this.mode.get() == ElytraFly.Mode.Bounce;
      });
      this.slowPitch = this.sgSpeed.d("Slow Pitch", 50.0D, 0.0D, 90.0D, 1.0D, "How many blocks to move each tick horizontally.", () -> {
         return this.mode.get() == ElytraFly.Mode.Bounce;
      });
      this.fastPitch = this.sgSpeed.d("Fast Pitch", 35.0D, 0.0D, 90.0D, 1.0D, "How many blocks to move each tick horizontally.", () -> {
         return this.mode.get() == ElytraFly.Mode.Bounce;
      });
      this.horizontal = this.sgSpeed.d("Horizontal Speed", 1.0D, 0.0D, 5.0D, 0.1D, "How many blocks to move each tick horizontally.", () -> {
         return this.mode.get() == ElytraFly.Mode.Wasp;
      });
      this.up = this.sgSpeed.d("Up Speed", 1.0D, 0.0D, 5.0D, 0.1D, "How many blocks to move up each tick.", () -> {
         return this.mode.get() == ElytraFly.Mode.Wasp;
      });
      this.speed = this.sgSpeed.d("Speed", 1.0D, 0.0D, 5.0D, 0.1D, "How many blocks to move up each tick.", () -> {
         return this.mode.get() == ElytraFly.Mode.Control;
      });
      this.upMultiplier = this.sgSpeed.d("Up Multiplier", 1.0D, 0.0D, 5.0D, 0.1D, "How many times faster should we fly up.", () -> {
         return this.mode.get() == ElytraFly.Mode.Control;
      });
      this.down = this.sgSpeed.d("Down Speed", 1.0D, 0.0D, 5.0D, 0.1D, "How many blocks to move down each tick.", () -> {
         return this.mode.get() == ElytraFly.Mode.Control;
      });
      this.smartFall = this.sgSpeed.b("Smart Fall", true, "Only falls down when looking down.", () -> {
         return this.mode.get() == ElytraFly.Mode.Wasp;
      });
      this.fallSpeed = this.sgSpeed.d("Fall Speed", 0.01D, 0.0D, 1.0D, 0.1D, "How many blocks to fall down each tick.", () -> {
         return this.mode.get() == ElytraFly.Mode.Control;
      });
      INSTANCE = this;
   }

   public static ElytraFly getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return this.mode.get() == ElytraFly.Mode.Bounce ? String.format("%.1f, %.1f", this.getPitch(), BlackOut.mc.field_1724.method_18798().method_37267() * 20.0D) : ((ElytraFly.Mode)this.mode.get()).name();
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      switch((ElytraFly.Mode)this.mode.get()) {
      case Wasp:
         this.waspTick(event);
         break;
      case Control:
         this.controlTick(event);
      }

   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (this.mode.get() == ElytraFly.Mode.Bounce && BlackOut.mc.field_1724 != null) {
         BlackOut.mc.field_1724.method_5728(true);
      }
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (this.mode.get() == ElytraFly.Mode.Bounce && BlackOut.mc.field_1724 != null) {
         if (!BlackOut.mc.field_1690.field_1903.method_1434()) {
            this.sus = false;
         }

         BlackOut.mc.field_1724.method_5728(true);
         if (this.sinceFalling <= 1 && BlackOut.mc.field_1724.method_24828()) {
            BlackOut.mc.field_1724.method_6043();
            this.sinceJump = 0;
            if (BlackOut.mc.field_1690.field_1903.method_1434()) {
               this.sus = true;
            }
         } else if (this.sinceJump > (Integer)this.bounceDelay.get() && BlackOut.mc.field_1724.method_23668()) {
            Managers.PACKET.sendInstantly(new class_2848(BlackOut.mc.field_1724, class_2849.field_12982));
         }

         ++this.sinceJump;
         this.sinceFalling = BlackOut.mc.field_1724.method_6128() ? 0 : this.sinceFalling + 1;
      }
   }

   public boolean isBouncing() {
      if (this.mode.get() != ElytraFly.Mode.Bounce) {
         return false;
      } else {
         return BlackOut.mc.field_1724.method_6128() || this.sinceFalling < 5;
      }
   }

   public float getPitch() {
      return this.sus ? BlackOut.mc.field_1724.method_36455() : class_3532.method_37166(((Double)this.slowPitch.get()).floatValue(), ((Double)this.fastPitch.get()).floatValue(), (float)BlackOut.mc.field_1724.method_18798().method_1033());
   }

   public void waspTick(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null) {
         if (BlackOut.mc.field_1724.method_6128()) {
            this.updateWaspMovement();
            this.pitch = BlackOut.mc.field_1724.method_36455();
            double cos = Math.cos(Math.toRadians((double)(this.yaw + 90.0F)));
            double sin = Math.sin(Math.toRadians((double)(this.yaw + 90.0F)));
            double x = this.moving ? cos * (Double)this.horizontal.get() : 0.0D;
            double y = -(Double)this.fallSpeed.get();
            double z = this.moving ? sin * (Double)this.horizontal.get() : 0.0D;
            if ((Boolean)this.smartFall.get()) {
               y *= Math.abs(Math.sin(Math.toRadians((double)this.pitch)));
            }

            if (BlackOut.mc.field_1690.field_1832.method_1434() && !BlackOut.mc.field_1690.field_1903.method_1434()) {
               y = -(Double)this.down.get();
            }

            if (!BlackOut.mc.field_1690.field_1832.method_1434() && BlackOut.mc.field_1690.field_1903.method_1434()) {
               y = (Double)this.up.get();
            }

            event.set(this, x, y, z);
            BlackOut.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
         }
      }
   }

   private void updateWaspMovement() {
      float yaw = BlackOut.mc.field_1724.method_36454();
      float f = BlackOut.mc.field_1724.field_3913.field_3905;
      float s = BlackOut.mc.field_1724.field_3913.field_3907;
      if (f > 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -45.0F : (s < 0.0F ? 45.0F : 0.0F);
      } else if (f < 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -135.0F : (s < 0.0F ? 135.0F : 180.0F);
      } else {
         this.moving = s != 0.0F;
         yaw += s > 0.0F ? -90.0F : (s < 0.0F ? 90.0F : 0.0F);
      }

      this.yaw = yaw;
   }

   public void controlTick(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null) {
         if (BlackOut.mc.field_1724.method_6128()) {
            this.updateControlMovement();
            this.pitch = 0.0F;
            boolean movingUp = false;
            if (!BlackOut.mc.field_1690.field_1832.method_1434() && BlackOut.mc.field_1690.field_1903.method_1434() && this.velocity > (Double)this.speed.get() * 0.4D) {
               this.p = (float)Math.min((double)this.p + 0.1D * (double)(1.0F - this.p) * (double)(1.0F - this.p) * (double)(1.0F - this.p), 1.0D);
               this.pitch = Math.max(Math.max(this.p, 0.0F) * -90.0F, -90.0F);
               movingUp = true;
               this.moving = false;
            } else {
               this.velocity = (Double)this.speed.get();
               this.p = -0.2F;
            }

            this.velocity = this.moving ? (Double)this.speed.get() : Math.min(this.velocity + Math.sin(Math.toRadians((double)this.pitch)) * 0.08D, (Double)this.speed.get());
            double cos = Math.cos(Math.toRadians((double)(this.yaw + 90.0F)));
            double sin = Math.sin(Math.toRadians((double)(this.yaw + 90.0F)));
            double x = this.moving && !movingUp ? cos * (Double)this.speed.get() : (movingUp ? this.velocity * Math.cos(Math.toRadians((double)this.pitch)) * cos : 0.0D);
            double y = this.pitch < 0.0F ? this.velocity * (Double)this.upMultiplier.get() * -Math.sin(Math.toRadians((double)this.pitch)) * this.velocity : -(Double)this.fallSpeed.get();
            double z = this.moving && !movingUp ? sin * (Double)this.speed.get() : (movingUp ? this.velocity * Math.cos(Math.toRadians((double)this.pitch)) * sin : 0.0D);
            y *= Math.abs(Math.sin(Math.toRadians(movingUp ? (double)this.pitch : (double)BlackOut.mc.field_1724.method_36455())));
            if (BlackOut.mc.field_1690.field_1832.method_1434() && !BlackOut.mc.field_1690.field_1903.method_1434()) {
               y = -(Double)this.down.get();
            }

            event.set(this, x, y, z);
            BlackOut.mc.field_1724.method_18800(0.0D, 0.0D, 0.0D);
         }
      }
   }

   private void updateControlMovement() {
      float yaw = BlackOut.mc.field_1724.method_36454();
      float f = BlackOut.mc.field_1724.field_3913.field_3905;
      float s = BlackOut.mc.field_1724.field_3913.field_3907;
      if (f > 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -45.0F : (s < 0.0F ? 45.0F : 0.0F);
      } else if (f < 0.0F) {
         this.moving = true;
         yaw += s > 0.0F ? -135.0F : (s < 0.0F ? 135.0F : 180.0F);
      } else {
         this.moving = s != 0.0F;
         yaw += s > 0.0F ? -90.0F : (s < 0.0F ? 90.0F : 0.0F);
      }

      this.yaw = yaw;
   }

   public static enum Mode {
      Wasp,
      Control,
      Bounce;

      // $FF: synthetic method
      private static ElytraFly.Mode[] $values() {
         return new ElytraFly.Mode[]{Wasp, Control, Bounce};
      }
   }
}
