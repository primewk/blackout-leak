package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class TickShift extends Module {
   private static TickShift INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgCharge = this.addGroup("Charge");
   public final Setting<TickShift.SmoothMode> smooth;
   public final Setting<Integer> packets;
   public final Setting<Double> timer;
   private final Setting<Boolean> step;
   public final Setting<TickShift.ChargeMode> chargeMode;
   public final Setting<Double> chargeSpeed;
   public double unSent;
   private boolean lastMoving;
   private boolean shouldResetTimer;

   public TickShift() {
      super("Tick Shift", "Stores packets when standing still and uses them when you start moving.", SubCategory.MOVEMENT, true);
      this.smooth = this.sgGeneral.e("Smoothness", TickShift.SmoothMode.Exponent, ".");
      this.packets = this.sgGeneral.i("Packets", 20, 0, 100, 1, "How many packets to store for later use.");
      this.timer = this.sgGeneral.d("Timer", 2.0D, 0.0D, 10.0D, 0.1D, "How many packets to send every movement tick.");
      this.step = this.sgGeneral.b("Use Step", false, ".");
      this.chargeMode = this.sgCharge.e("Charge Mode", TickShift.ChargeMode.Strict, ".");
      this.chargeSpeed = this.sgCharge.d("Charge Speed", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.unSent = 0.0D;
      this.lastMoving = false;
      this.shouldResetTimer = false;
      INSTANCE = this;
   }

   public static TickShift getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.unSent = 0.0D;
   }

   public void onDisable() {
      if (this.shouldResetTimer) {
         Timer.reset();
      }

      this.shouldResetTimer = false;
   }

   public String getInfo() {
      return String.valueOf(this.unSent);
   }

   @Event
   public void onTick(TickEvent.Post e) {
      if (BlackOut.mc.field_1724 != null) {
         if (this.unSent > 0.0D && this.lastMoving) {
            Timer.set(this.getTimer());
            this.lastMoving = false;
            this.shouldResetTimer = true;
         } else if (this.shouldResetTimer) {
            Timer.reset();
            this.shouldResetTimer = false;
         }

      }
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (event.movement.method_1033() > 0.0D && (!(event.movement.method_1033() > 0.0784D) || !(event.movement.method_1033() < 0.0785D))) {
         this.unSent = Math.max(0.0D, this.unSent - 1.0D);
         this.lastMoving = true;
      }

   }

   private float getTimer() {
      if (this.smooth.get() == TickShift.SmoothMode.Disabled) {
         return ((Double)this.timer.get()).floatValue();
      } else {
         double progress = 1.0D - this.unSent / (double)((float)(Integer)this.packets.get());
         if (this.smooth.get() == TickShift.SmoothMode.Exponent) {
            progress *= progress * progress * progress * progress;
         }

         return (float)(1.0D + ((Double)this.timer.get() - 1.0D) * (1.0D - progress));
      }
   }

   public boolean canCharge(boolean sent, boolean move) {
      switch((TickShift.ChargeMode)this.chargeMode.get()) {
      case Strict:
         return !sent;
      case Semi:
         return !sent || !move;
      default:
         return false;
      }
   }

   public boolean shouldStep() {
      return this.enabled && (Boolean)this.step.get() && this.shouldResetTimer;
   }

   public static enum SmoothMode {
      Disabled,
      Normal,
      Exponent;

      // $FF: synthetic method
      private static TickShift.SmoothMode[] $values() {
         return new TickShift.SmoothMode[]{Disabled, Normal, Exponent};
      }
   }

   public static enum ChargeMode {
      Strict,
      Semi;

      // $FF: synthetic method
      private static TickShift.ChargeMode[] $values() {
         return new TickShift.ChargeMode[]{Strict, Semi};
      }
   }
}
