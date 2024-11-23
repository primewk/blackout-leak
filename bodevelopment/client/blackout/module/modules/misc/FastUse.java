package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.mixin.IMinecraftClient;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class FastUse extends Module {
   private static FastUse INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<List<class_1792>> items;
   public final Setting<FastUse.Timing> timing;
   public final Setting<Integer> delayTicks;
   public final Setting<Double> delaySeconds;
   public final Setting<FastUse.RotationMode> rotate;
   private long prevUse;

   public FastUse() {
      super("Fast Use", "Uses items faster.", SubCategory.MISC, true);
      this.items = this.sgGeneral.il("Items", "", class_1802.field_8287);
      this.timing = this.sgGeneral.e("Timing", FastUse.Timing.Tick, "");
      this.delayTicks = this.sgGeneral.i("Delay Ticks", 1, 0, 4, 1, ".", () -> {
         return this.timing.get() == FastUse.Timing.Tick;
      });
      this.delaySeconds = this.sgGeneral.d("Delay Seconds", 0.2D, 0.0D, 1.0D, 0.002D, ".", () -> {
         return this.timing.get() == FastUse.Timing.Render;
      });
      this.rotate = this.sgGeneral.e("Rotate EXP", FastUse.RotationMode.Normal, ".", () -> {
         return ((List)this.items.get()).contains(class_1802.field_8287);
      });
      this.prevUse = 0L;
      INSTANCE = this;
   }

   public static FastUse getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return ((FastUse.Timing)this.timing.get()).name();
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (this.timing.get() == FastUse.Timing.Tick) {
         class_1799 stack = this.getStack();
         if (this.isValid(stack)) {
            this.rotateIfNeeded(this.getStack());
         }
      }
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      if (this.timing.get() == FastUse.Timing.Render) {
         class_1799 stack = this.getStack();
         if (this.isValid(stack)) {
            if (!this.rotateIfNeeded(this.getStack())) {
               if (!((double)(System.currentTimeMillis() - this.prevUse) < (Double)this.delaySeconds.get() * 1000.0D)) {
                  this.prevUse = System.currentTimeMillis();
                  ((IMinecraftClient)BlackOut.mc).blackout_Client$useItem();
               }
            }
         }
      }
   }

   public boolean rotateIfNeeded(class_1799 stack) {
      if (stack.method_31574(class_1802.field_8287)) {
         switch((FastUse.RotationMode)this.rotate.get()) {
         case Vanilla:
            BlackOut.mc.field_1724.method_36457(90.0F);
            return Math.abs(Managers.ROTATION.prevPitch - 90.0F) > 1.0F;
         case Normal:
            return !this.rotatePitch(90.0F, RotationType.Other, "exp");
         case Instant:
            return !this.rotatePitch(90.0F, RotationType.InstantOther, "exp");
         }
      }

      return false;
   }

   public boolean isValid(class_1799 stack) {
      return stack == null ? false : ((List)this.items.get()).contains(stack.method_7909());
   }

   public class_1799 getStack() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && !BlackOut.mc.field_1724.method_6115() && BlackOut.mc.field_1690.field_1904.method_1434()) {
         class_1799 stack = BlackOut.mc.field_1724.method_6047();
         return stack != null && !stack.method_7960() ? stack : null;
      } else {
         return null;
      }
   }

   public static enum Timing {
      Tick,
      Render;

      // $FF: synthetic method
      private static FastUse.Timing[] $values() {
         return new FastUse.Timing[]{Tick, Render};
      }
   }

   public static enum RotationMode {
      Vanilla,
      Normal,
      Instant,
      Disabled;

      // $FF: synthetic method
      private static FastUse.RotationMode[] $values() {
         return new FastUse.RotationMode[]{Vanilla, Normal, Instant, Disabled};
      }
   }
}
