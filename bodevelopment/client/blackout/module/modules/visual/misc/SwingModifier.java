package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1268;

public class SwingModifier extends Module {
   private static SwingModifier INSTANCE;
   private final SettingGroup sgMainHand = this.addGroup("Main Hand");
   private final SettingGroup sgOffHand = this.addGroup("Off Hand");
   private final Setting<Double> mainSpeed;
   private final Setting<Double> mainStart;
   private final Setting<Double> mainEnd;
   private final Setting<Double> mainStartY;
   private final Setting<Double> mainEndY;
   private final Setting<Boolean> mainReset;
   private final Setting<Double> offSpeed;
   private final Setting<Double> offStart;
   private final Setting<Double> offEnd;
   private final Setting<Double> offStartY;
   private final Setting<Double> offEndY;
   private final Setting<Boolean> offReset;
   private static boolean mainSwinging = false;
   private float mainProgress;
   private boolean offSwinging;
   private float offProgress;

   public SwingModifier() {
      super("Swing Modifier", "Modifies swing rendering.", SubCategory.MISC_VISUAL, true);
      this.mainSpeed = this.sgMainHand.d("Main Speed", 1.0D, 0.0D, 10.0D, 0.05D, "Speed of swinging.");
      this.mainStart = this.sgMainHand.d("Main Start", 0.0D, 0.0D, 10.0D, 0.05D, "Starts swing at this progress.");
      this.mainEnd = this.sgMainHand.d("Main End", 1.0D, 0.0D, 10.0D, 0.05D, "Ends swing at this progress.");
      this.mainStartY = this.sgMainHand.d("Main Start Y", 0.0D, -10.0D, 10.0D, 0.05D, "Hand Y value in the beginning.");
      this.mainEndY = this.sgMainHand.d("Main End Y", 0.0D, -10.0D, 10.0D, 0.05D, "Hand Y value in the end.");
      this.mainReset = this.sgMainHand.b("Main Reset", false, "Resets swing when swinging again.");
      this.offSpeed = this.sgOffHand.d("Off Speed", 1.0D, 0.0D, 10.0D, 0.05D, "Speed of swinging.");
      this.offStart = this.sgOffHand.d("Off Start", 0.0D, 0.0D, 10.0D, 0.05D, "Starts swing at this progress.");
      this.offEnd = this.sgOffHand.d("Off End", 1.0D, 0.0D, 10.0D, 0.05D, "Ends swing at this progress.");
      this.offStartY = this.sgOffHand.d("Off Start Y", 0.0D, -10.0D, 10.0D, 0.05D, "Hand Y value in the beginning.");
      this.offEndY = this.sgOffHand.d("Off End Y", 0.0D, -10.0D, 10.0D, 0.05D, "Hand Y value in the end.");
      this.offReset = this.sgOffHand.b("Off Reset", false, "Resets swing when swinging again.");
      this.mainProgress = 0.0F;
      this.offSwinging = false;
      this.offProgress = 0.0F;
      INSTANCE = this;
   }

   public static SwingModifier getInstance() {
      return INSTANCE;
   }

   public void startSwing(class_1268 hand) {
      if (hand == class_1268.field_5808) {
         if ((Boolean)this.mainReset.get() || !mainSwinging) {
            this.mainProgress = 0.0F;
            mainSwinging = true;
         }
      } else if ((Boolean)this.offReset.get() || !this.offSwinging) {
         this.offProgress = 0.0F;
         this.offSwinging = true;
      }

   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      if (mainSwinging) {
         if (this.mainProgress >= 1.0F) {
            mainSwinging = false;
            this.mainProgress = 0.0F;
         } else {
            this.mainProgress = (float)((double)this.mainProgress + event.frameTime * (Double)this.mainSpeed.get());
         }
      }

      if (this.offSwinging) {
         if (this.offProgress >= 1.0F) {
            this.offSwinging = false;
            this.offProgress = 0.0F;
         } else {
            this.offProgress = (float)((double)this.offProgress + event.frameTime * (Double)this.offSpeed.get());
         }
      }

   }

   public float getSwing(class_1268 hand) {
      return hand == class_1268.field_5808 ? (float)((Double)this.mainStart.get() + ((Double)this.mainEnd.get() - (Double)this.mainStart.get()) * (double)this.mainProgress) : (float)((Double)this.offStart.get() + ((Double)this.offEnd.get() - (Double)this.offStart.get()) * (double)this.offProgress);
   }

   public float getY(class_1268 hand) {
      return hand == class_1268.field_5808 ? (float)((Double)this.mainStartY.get() + ((Double)this.mainEndY.get() - (Double)this.mainStartY.get()) * (double)this.mainProgress) / -10.0F : (float)((Double)this.offStartY.get() + ((Double)this.offEndY.get() - (Double)this.offStartY.get()) * (double)this.offProgress) / -10.0F;
   }
}
