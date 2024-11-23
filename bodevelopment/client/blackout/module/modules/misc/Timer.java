package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class Timer extends Module {
   private static Timer INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Timer.TimerMode> mode;
   public final Setting<Double> timeMulti;
   public final Setting<Double> physicsMulti;
   private static float packets = 0.0F;
   private static float speed = -1.0F;

   public Timer() {
      super("Timer", "Speeds up your movement.", SubCategory.MISC, true);
      this.mode = this.sgGeneral.e("Mode", Timer.TimerMode.Time, ".");
      this.timeMulti = this.sgGeneral.d("Time Multiplier", 1.0D, 0.05D, 10.0D, 0.04D, "Timer speed.", () -> {
         return this.mode.get() == Timer.TimerMode.Time;
      });
      this.physicsMulti = this.sgGeneral.d("Physics Multiplier", 1.0D, 0.0D, 10.0D, 0.04D, "Multiplier for movement packets.", () -> {
         return this.mode.get() == Timer.TimerMode.Physics;
      });
      INSTANCE = this;
   }

   public static Timer getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return ((Timer.TimerMode)this.mode.get()).name();
   }

   public boolean shouldSkipListeners() {
      return BlackOut.mc.field_1687 == null;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && this.enabled && this.mode.get() == Timer.TimerMode.Physics) {
         for(packets += ((Double)this.physicsMulti.get()).floatValue() - 1.0F; packets > 0.0F; --packets) {
            BlackOut.mc.field_1724.method_6007();
         }

      }
   }

   public static void set(float newSpeed) {
      speed = newSpeed;
   }

   public static void reset() {
      speed = -69.0F;
   }

   public float getTickTime() {
      return 1000.0F / getInstance().getTPS();
   }

   public float getTPS() {
      if (this.mode.get() == Timer.TimerMode.Physics) {
         return 20.0F;
      } else if (speed <= 0.0F) {
         return this.enabled ? ((Double)this.timeMulti.get()).floatValue() * 20.0F : 20.0F;
      } else {
         return speed * 20.0F;
      }
   }

   public static enum TimerMode {
      Physics,
      Time;

      // $FF: synthetic method
      private static Timer.TimerMode[] $values() {
         return new Timer.TimerMode[]{Physics, Time};
      }
   }
}
