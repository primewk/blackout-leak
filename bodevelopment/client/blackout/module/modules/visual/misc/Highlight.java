package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import org.apache.commons.lang3.mutable.MutableDouble;

public class Highlight extends Module {
   private static Highlight INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Highlight.RenderMode> mode;
   private final Setting<RenderShape> shape;
   private final Setting<BlackOutColor> sideColor;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<Double> moveSpeed;
   private final Setting<Double> fadeIn;
   private final Setting<Double> fadeOut;
   private final Map<class_2338, MutableDouble> alphas;
   private class_243 middle;
   private class_243 targetPos;
   private double lx;
   private double ly;
   private double lz;
   private double tlx;
   private double tly;
   private double tlz;
   private double alpha;

   public Highlight() {
      super("Highlight", ".", SubCategory.MISC_VISUAL, true);
      this.mode = this.sgGeneral.e("Mode", Highlight.RenderMode.Fade, ".");
      this.shape = this.sgGeneral.e("Render Shape", RenderShape.Sides, ".");
      this.sideColor = this.sgGeneral.c("Side Color", new BlackOutColor(255, 0, 0, 50), "");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), "");
      this.moveSpeed = this.sgGeneral.d("Move Speed", 1.0D, 1.0D, 10.0D, 0.1D, ".", () -> {
         return this.mode.get() == Highlight.RenderMode.Move;
      });
      this.fadeIn = this.sgGeneral.d("Fade In Speed", 2.0D, 0.0D, 20.0D, 0.2D, "");
      this.fadeOut = this.sgGeneral.d("Fade Out Speed", 1.0D, 0.0D, 20.0D, 0.2D, "");
      this.alphas = new HashMap();
      this.middle = null;
      this.targetPos = null;
      this.lx = 0.0D;
      this.ly = 0.0D;
      this.lz = 0.0D;
      this.tlx = 0.0D;
      this.tly = 0.0D;
      this.tlz = 0.0D;
      this.alpha = 0.0D;
      INSTANCE = this;
   }

   public static Highlight getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_2338 pos = this.getCurrentPos();
         switch((Highlight.RenderMode)this.mode.get()) {
         case Move:
            this.alphas.clear();
            this.moveRender(this.getBox(pos), event.frameTime);
            break;
         case Fade:
            this.fadeRender(pos, event.frameTime);
         }

      }
   }

   private class_2338 getCurrentPos() {
      class_239 result = BlackOut.mc.field_1765;
      if (result instanceof class_3965) {
         class_3965 blockHitResult = (class_3965)result;
         return blockHitResult.method_17777();
      } else {
         return null;
      }
   }

   private void moveRender(class_238 box, double frameTime) {
      if (box == null) {
         this.alpha = Math.max(this.alpha - frameTime * (Double)this.fadeOut.get() * 5.0D, 0.0D);
      } else {
         this.alpha = Math.min(this.alpha + frameTime * (Double)this.fadeIn.get() * 5.0D, 1.0D);
      }

      if (box != null) {
         this.targetPos = BoxUtils.middle(box);
         this.tlx = box.method_17939();
         this.tly = box.method_17940();
         this.tlz = box.method_17941();
      }

      this.animLengths(frameTime);
      this.movePos(frameTime);
      if (this.middle != null) {
         class_238 box1 = new class_238(this.middle.method_10216() - this.lx / 2.0D, this.middle.method_10214() - this.ly / 2.0D, this.middle.method_10215() - this.lz / 2.0D, this.middle.method_10216() + this.lx / 2.0D, this.middle.method_10214() + this.ly / 2.0D, this.middle.method_10215() + this.lz / 2.0D);
         Render3DUtils.box(box1, ((BlackOutColor)this.sideColor.get()).alphaMulti(this.alpha), ((BlackOutColor)this.lineColor.get()).alphaMulti(this.alpha), (RenderShape)this.shape.get());
      }

   }

   private void animLengths(double frameTime) {
      frameTime = Math.min(frameTime, 1.0D);
      double dx = this.tlx - this.lx;
      double dy = this.tly - this.ly;
      double dz = this.tlz - this.lz;
      double adx = Math.abs(dx);
      double ady = Math.abs(dy);
      double adz = Math.abs(dz);
      this.lx += dx * frameTime * 10.0D * (adx * adx + 1.0D);
      this.ly += dy * frameTime * 10.0D * (ady * ady + 1.0D);
      this.lz += dz * frameTime * 10.0D * (adz * adz + 1.0D);
   }

   private void movePos(double frameTime) {
      if (this.targetPos != null) {
         if (this.alpha <= 0.0D) {
            this.middle = null;
         } else {
            if (this.middle == null) {
               this.middle = this.targetPos;
            }

            class_243 diff = this.targetPos.method_1020(this.middle);
            double length = diff.method_1033();
            if (Double.isNaN(length)) {
               this.middle = this.targetPos;
            } else if (!(length <= 0.0D)) {
               double delta = (Double)this.moveSpeed.get() / length * frameTime;
               if (delta >= 1.0D) {
                  this.middle = this.targetPos;
               } else {
                  this.middle = this.middle.method_1019(diff.method_1021(delta));
               }

            }
         }
      }
   }

   private void fadeRender(class_2338 pos, double frameTime) {
      if (pos != null && !this.alphas.containsKey(pos)) {
         this.alphas.put(pos, new MutableDouble(0.0D));
      }

      this.alphas.entrySet().removeIf((entry) -> {
         class_2338 p = (class_2338)entry.getKey();
         MutableDouble a = (MutableDouble)entry.getValue();
         if (p.equals(pos)) {
            a.setValue(Math.min(a.getValue() + frameTime * (Double)this.fadeIn.get() * 5.0D, 1.0D));
         } else {
            double reduced = a.getValue() - frameTime * (Double)this.fadeOut.get() * 5.0D;
            if (reduced <= 0.0D) {
               return true;
            }

            a.setValue(reduced);
         }

         class_238 box = this.getBox(p);
         if (box == null) {
            return false;
         } else {
            Render3DUtils.box(box, ((BlackOutColor)this.sideColor.get()).alphaMulti(a.getValue()), ((BlackOutColor)this.lineColor.get()).alphaMulti(a.getValue()), (RenderShape)this.shape.get());
            return false;
         }
      });
   }

   private class_238 getBox(class_2338 pos) {
      if (pos == null) {
         return null;
      } else {
         class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
         class_265 shape = state.method_26218(BlackOut.mc.field_1687, pos);
         return shape.method_1110() ? null : shape.method_1107().method_996(pos);
      }
   }

   public static enum RenderMode {
      Move,
      Fade;

      // $FF: synthetic method
      private static Highlight.RenderMode[] $values() {
         return new Highlight.RenderMode[]{Move, Fade};
      }
   }
}
