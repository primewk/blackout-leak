package bodevelopment.client.blackout.module.modules.visual.world;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.HoleType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.Hole;
import bodevelopment.client.blackout.util.HoleUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_3532;
import net.minecraft.class_4588;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class HoleESP extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> range;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final Setting<Boolean> bottomLines;
   private final Setting<Boolean> bottomSide;
   private final Setting<Boolean> fadeLines;
   private final Setting<Boolean> fadeSides;
   private final Setting<Double> minHeight;
   private final Setting<Double> maxHeight;
   private final Setting<Double> breathingSpeed;
   private long prevCalc;
   private final List<Hole> holes;

   public HoleESP() {
      super("Hole ESP", "Highlights holes near you.", SubCategory.WORLD, true);
      this.range = this.sgGeneral.d("Range", 8.0D, 0.0D, 10.0D, 0.1D, "Maximum range to a hole.");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), "Line color of rendered boxes.");
      this.sideColor = this.sgGeneral.c("Side Color", new BlackOutColor(255, 0, 0, 50), "Side color of rendered boxes.");
      this.bottomLines = this.sgGeneral.b("Bottom Lines", true, "Renders lines in hole bottom.");
      this.bottomSide = this.sgGeneral.b("Bottom Side", true, "Renders bottom plane.");
      this.fadeLines = this.sgGeneral.b("Fade Lines", true, "Renders lines in fade.");
      this.fadeSides = this.sgGeneral.b("Fade Sides", true, "Renders sides in fade.");
      this.minHeight = this.sgGeneral.d("Min Height", 0.5D, -1.0D, 1.0D, 0.05D, ".");
      this.maxHeight = this.sgGeneral.d("Max Height", 1.0D, -1.0D, 1.0D, 0.05D, "How tall should the fade be.");
      this.breathingSpeed = this.sgGeneral.d("Breathing Speed", 1.0D, 0.0D, 10.0D, 0.1D, ".", () -> {
         return !((Double)this.minHeight.get()).equals(this.maxHeight.get());
      });
      this.prevCalc = 0L;
      this.holes = new ArrayList();
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (System.currentTimeMillis() - this.prevCalc > 100L) {
         this.findHoles(BlackOut.mc.field_1724.method_24515(), (int)Math.ceil((Double)this.range.get()), (Double)this.range.get() * (Double)this.range.get());
         this.prevCalc = System.currentTimeMillis();
      }

      Render3DUtils.matrices.method_22903();
      Render3DUtils.setRotation(Render3DUtils.matrices);
      Render3DUtils.start();
      if ((Boolean)this.bottomSide.get() || (Boolean)this.fadeSides.get()) {
         this.drawSides();
      }

      if ((Boolean)this.bottomLines.get() || (Boolean)this.fadeLines.get()) {
         this.drawLines();
      }

      Render3DUtils.end();
      Render3DUtils.matrices.method_22909();
   }

   private void drawSides() {
      RenderSystem.setShader(class_757::method_34540);
      class_289 tessellator = class_289.method_1348();
      class_287 bufferBuilder = tessellator.method_1349();
      bufferBuilder.method_1328(class_5596.field_27382, class_290.field_1576);
      double x = BlackOut.mc.field_1773.method_19418().method_19326().field_1352;
      double y = BlackOut.mc.field_1773.method_19418().method_19326().field_1351;
      double z = BlackOut.mc.field_1773.method_19418().method_19326().field_1350;
      Matrix4f matrix4f = Render3DUtils.matrices.method_23760().method_23761();
      float red = (float)((BlackOutColor)this.sideColor.get()).red / 255.0F;
      float green = (float)((BlackOutColor)this.sideColor.get()).green / 255.0F;
      float blue = (float)((BlackOutColor)this.sideColor.get()).blue / 255.0F;
      float alpha = (float)((BlackOutColor)this.sideColor.get()).alpha / 255.0F;
      this.holes.forEach((hole) -> {
         byte var10000;
         switch(hole.type) {
         case DoubleX:
         case Quad:
            var10000 = 2;
            break;
         default:
            var10000 = 1;
         }

         int ox = var10000;
         switch(hole.type) {
         case Quad:
         case DoubleZ:
            var10000 = 2;
            break;
         default:
            var10000 = 1;
         }

         int oz = var10000;
         float a = this.getAlpha(this.dist(hole.middle, x, y, z)) * alpha;
         Vector3f v = new Vector3f((float)((double)hole.pos.method_10263() - x), (float)((double)hole.pos.method_10264() - y), (float)((double)hole.pos.method_10260() - z));
         if ((Boolean)this.bottomSide.get()) {
            Render3DUtils.drawPlane(matrix4f, bufferBuilder, v.x, v.y, v.z, v.x + (float)ox, v.y, v.z, v.x + (float)ox, v.y, v.z + (float)oz, v.x, v.y, v.z + (float)oz, red, green, blue, a);
         }

         if ((Boolean)this.fadeSides.get()) {
            float height = this.getHeight(hole.pos);
            fadePlane(matrix4f, bufferBuilder, v.x, v.z, v.x, v.z + (float)oz, v.x, v.z + (float)oz, v.x, v.z, v.y, height, red, green, blue, a);
            fadePlane(matrix4f, bufferBuilder, v.x + (float)ox, v.z, v.x + (float)ox, v.z + (float)oz, v.x + (float)ox, v.z + (float)oz, v.x + (float)ox, v.z, v.y, height, red, green, blue, a);
            fadePlane(matrix4f, bufferBuilder, v.x, v.z, v.x + (float)ox, v.z, v.x + (float)ox, v.z, v.x, v.z, v.y, height, red, green, blue, a);
            fadePlane(matrix4f, bufferBuilder, v.x, v.z + (float)oz, v.x + (float)ox, v.z + (float)oz, v.x + (float)ox, v.z + (float)oz, v.x, v.z + (float)oz, v.y, height, red, green, blue, a);
         }

      });
      tessellator.method_1350();
   }

   private float getHeight(class_2338 pos) {
      double offset = (double)(pos.method_10263() + pos.method_10260());
      return (float)class_3532.method_16436(Math.sin(offset / 2.0D + (double)System.currentTimeMillis() * (Double)this.breathingSpeed.get() / 500.0D) / 2.0D + 0.5D, (Double)this.minHeight.get(), (Double)this.maxHeight.get());
   }

   public static void fadePlane(Matrix4f matrix4f, class_4588 vertexConsumer, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float y, float height, float r, float g, float b, float a) {
      vertexConsumer.method_22918(matrix4f, x1, y, z1).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
      vertexConsumer.method_22918(matrix4f, x2, y, z2).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
      vertexConsumer.method_22918(matrix4f, x3, y + height, z3).method_22915(r, g, b, 0.0F).method_22914(0.0F, 0.0F, 0.0F).method_1344();
      vertexConsumer.method_22918(matrix4f, x4, y + height, z4).method_22915(r, g, b, 0.0F).method_22914(0.0F, 0.0F, 0.0F).method_1344();
   }

   private void drawLines() {
      RenderSystem.setShader(class_757::method_34535);
      class_289 tessellator = class_289.method_1348();
      class_287 bufferBuilder = tessellator.method_1349();
      bufferBuilder.method_1328(class_5596.field_27377, class_290.field_29337);
      RenderSystem.lineWidth(1.5F);
      double x = BlackOut.mc.field_1773.method_19418().method_19326().field_1352;
      double y = BlackOut.mc.field_1773.method_19418().method_19326().field_1351;
      double z = BlackOut.mc.field_1773.method_19418().method_19326().field_1350;
      Matrix4f matrix4f = Render3DUtils.matrices.method_23760().method_23761();
      Matrix3f matrix3f = Render3DUtils.matrices.method_23760().method_23762();
      float red = (float)((BlackOutColor)this.lineColor.get()).red / 255.0F;
      float green = (float)((BlackOutColor)this.lineColor.get()).green / 255.0F;
      float blue = (float)((BlackOutColor)this.lineColor.get()).blue / 255.0F;
      float alpha = (float)((BlackOutColor)this.lineColor.get()).alpha / 255.0F;
      this.holes.forEach((hole) -> {
         byte var10000;
         switch(hole.type) {
         case DoubleX:
         case Quad:
            var10000 = 2;
            break;
         default:
            var10000 = 1;
         }

         int ox = var10000;
         switch(hole.type) {
         case Quad:
         case DoubleZ:
            var10000 = 2;
            break;
         default:
            var10000 = 1;
         }

         int oz = var10000;
         float a = this.getAlpha(this.dist(hole.middle, x, y, z)) * alpha;
         Vector3f v = new Vector3f((float)((double)hole.pos.method_10263() - x), (float)((double)hole.pos.method_10264() - y), (float)((double)hole.pos.method_10260() - z));
         if ((Boolean)this.bottomLines.get()) {
            this.hline(bufferBuilder, matrix4f, matrix3f, v.x, v.z, v.x, v.z + (float)oz, v.y, red, green, blue, a);
            this.hline(bufferBuilder, matrix4f, matrix3f, v.x + (float)ox, v.z, v.x + (float)ox, v.z + (float)oz, v.y, red, green, blue, a);
            this.hline(bufferBuilder, matrix4f, matrix3f, v.x, v.z, v.x + (float)ox, v.z, v.y, red, green, blue, a);
            this.hline(bufferBuilder, matrix4f, matrix3f, v.x, v.z + (float)oz, v.x + (float)ox, v.z + (float)oz, v.y, red, green, blue, a);
         }

         if ((Boolean)this.fadeLines.get()) {
            float height = this.getHeight(hole.pos);
            this.fadeLine(matrix4f, matrix3f, bufferBuilder, v.x, v.z, v.y, height, red, green, blue, a);
            this.fadeLine(matrix4f, matrix3f, bufferBuilder, v.x + (float)ox, v.z, v.y, height, red, green, blue, a);
            this.fadeLine(matrix4f, matrix3f, bufferBuilder, v.x, v.z + (float)oz, v.y, height, red, green, blue, a);
            this.fadeLine(matrix4f, matrix3f, bufferBuilder, v.x + (float)ox, v.z + (float)oz, v.y, height, red, green, blue, a);
         }

      });
      tessellator.method_1350();
   }

   private void hline(class_4588 consumer, Matrix4f matrix4f, Matrix3f matrix3f, float x, float z, float x2, float z2, float y, float r, float g, float b, float a) {
      float dx = x2 - x;
      float dz = z2 - z;
      float length = (float)Math.sqrt((double)(dx * dx + dz * dz));
      float nx = dx / length;
      float nz = dz / length;
      consumer.method_22918(matrix4f, x, y, z).method_22915(r, g, b, a).method_23763(matrix3f, nx, 0.0F, nz).method_1344();
      consumer.method_22918(matrix4f, x2, y, z2).method_22915(r, g, b, a).method_23763(matrix3f, nx, 0.0F, nz).method_1344();
   }

   public void fadeLine(Matrix4f matrix4f, Matrix3f matrix3f, class_4588 vertexConsumer, float x, float z, float y, float height, float r, float g, float b, float a) {
      vertexConsumer.method_22918(matrix4f, x, y, z).method_22915(r, g, b, a).method_23763(matrix3f, 0.0F, 1.0F, 0.0F).method_1344();
      vertexConsumer.method_22918(matrix4f, x, y + height, z).method_22915(r, g, b, 0.0F).method_23763(matrix3f, 0.0F, 1.0F, 0.0F).method_1344();
   }

   private double dist(class_243 vec3d, double x, double y, double z) {
      double dx = vec3d.field_1352 - x;
      double dy = vec3d.field_1351 - y;
      double dz = vec3d.field_1350 - z;
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   private float getAlpha(double dist) {
      return (float)class_3532.method_15350(1.0D - (dist - (Double)this.range.get() / 2.0D) / ((Double)this.range.get() / 2.0D), 0.0D, 1.0D);
   }

   private void findHoles(class_2338 center, int r, double radiusSq) {
      this.holes.clear();

      for(int x = -r; x <= r; ++x) {
         for(int y = -r; y <= r; ++y) {
            for(int z = -r; z <= r; ++z) {
               if (!((double)(x * x + y * y + z * z) > radiusSq)) {
                  class_2338 pos = center.method_10069(x, y, z);
                  Hole h = HoleUtils.getHole(pos, 3, true);
                  if (h.type != HoleType.NotHole) {
                     this.holes.add(h);
                  }
               }
            }
         }
      }

   }
}
