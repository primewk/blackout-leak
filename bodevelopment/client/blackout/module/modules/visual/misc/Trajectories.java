package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.interfaces.functional.DoubleConsumer;
import bodevelopment.client.blackout.interfaces.functional.DoubleFunction;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.offensive.BowSpam;
import bodevelopment.client.blackout.module.modules.visual.entities.Trails;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_239.class_240;
import net.minecraft.class_293.class_5596;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Trajectories extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Integer> maxTicks;
   private final Setting<Double> fadeLength;
   private final Setting<Boolean> playerVelocity;
   public final Setting<Trails.ColorMode> colorMode;
   private final Setting<Double> saturation;
   private final Setting<BlackOutColor> clr;
   private final Setting<BlackOutColor> clr1;
   private final Map<class_1792, Trajectories.SimulationData> dataMap;

   public Trajectories() {
      super("Trajectories", "Draws a trajectory when holding throwable items or a bow.", SubCategory.MISC_VISUAL, true);
      this.maxTicks = this.sgGeneral.i("Max Ticks", 500, 0, 500, 5, ".");
      this.fadeLength = this.sgColor.d("Fade Length", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.playerVelocity = this.sgGeneral.b("Player Velocity", true, ".");
      this.colorMode = this.sgColor.e("Color Mode", Trails.ColorMode.Custom, "What color to use");
      this.saturation = this.sgColor.d("Rainbow Saturation", 0.8D, 0.0D, 1.0D, 0.1D, ".", () -> {
         return this.colorMode.get() == Trails.ColorMode.Rainbow;
      });
      this.clr = this.sgColor.c("Line Color", new BlackOutColor(255, 255, 255, 255), ".", () -> {
         return this.colorMode.get() != Trails.ColorMode.Rainbow;
      });
      this.clr1 = this.sgColor.c("Wave Color", new BlackOutColor(175, 175, 175, 255), ".", () -> {
         return this.colorMode.get() != Trails.ColorMode.Rainbow;
      });
      this.dataMap = new HashMap();
      this.initMap();
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_1799 itemStack = BlackOut.mc.field_1724.method_6047();
         class_1792 item = itemStack.method_7909();
         if (this.dataMap.containsKey(item)) {
            Trajectories.SimulationData data = (Trajectories.SimulationData)this.dataMap.get(item);
            class_4587 stack = Render3DUtils.matrices;
            stack.method_22903();
            Render3DUtils.setRotation(stack);
            Render3DUtils.start();
            float yaw = Managers.ROTATION.getNextYaw();
            this.draw(data, this.getVelocity((double[])data.speed.apply(itemStack), yaw, 0.0D), itemStack, event.tickDelta, stack);
            if (this.hasMulti(itemStack)) {
               this.draw(data, this.getVelocity((double[])data.speed.apply(itemStack), yaw, -10.0D), itemStack, event.tickDelta, stack);
               this.draw(data, this.getVelocity((double[])data.speed.apply(itemStack), yaw, 10.0D), itemStack, event.tickDelta, stack);
            }

            Render3DUtils.end();
            stack.method_22909();
         }
      }
   }

   private void rotateVelocity(double[] velocity, class_243 opposite, double yaw) {
      Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(yaw * 0.01745329238474369D, opposite.field_1352, opposite.field_1351, opposite.field_1350);
      class_243 velocityVec = new class_243(velocity[0], velocity[1], velocity[2]);
      Vector3f vector3f = velocityVec.method_46409().rotate(quaternionf);
      velocity[0] = (double)vector3f.x;
      velocity[1] = (double)vector3f.y;
      velocity[2] = (double)vector3f.z;
   }

   private boolean hasMulti(class_1799 itemStack) {
      if (!(itemStack.method_7909() instanceof class_1764)) {
         return false;
      } else {
         return class_1890.method_8225(class_1893.field_9108, itemStack) > 0;
      }
   }

   private void draw(Trajectories.SimulationData data, double[] velocity, class_1799 itemStack, float tickDelta, class_4587 stack) {
      class_239 hitResult = this.drawLine(data, velocity, itemStack, tickDelta, stack);
      if (hitResult != null) {
         Matrix4f matrix4f = stack.method_23760().method_23761();
         class_243 camPos = BlackOut.mc.field_1773.method_19418().method_19326();
         RenderSystem.setShader(class_757::method_34540);
         class_289 tessellator = class_289.method_1348();
         class_287 bufferBuilder = tessellator.method_1349();
         Color color = this.getColor();
         float r = (float)color.getRed() / 255.0F;
         float g = (float)color.getGreen() / 255.0F;
         float b = (float)color.getBlue() / 255.0F;
         float a = (float)color.getAlpha() / 255.0F;
         if (hitResult instanceof class_3965) {
            class_3965 blockHitResult = (class_3965)hitResult;
            bufferBuilder.method_1328(class_5596.field_29345, class_290.field_1576);
            class_243 pos = blockHitResult.method_17784().method_1020(camPos);
            double width = 0.25D;
            switch(blockHitResult.method_17780()) {
            case field_11033:
            case field_11036:
               this.renderCircle(bufferBuilder, matrix4f, (rad) -> {
                  return (float)(pos.field_1352 + Math.cos(rad) * width);
               }, (rad) -> {
                  return (float)pos.field_1351;
               }, (rad) -> {
                  return (float)(pos.field_1350 + Math.sin(rad) * width);
               }, r, g, b, a);
               break;
            case field_11043:
            case field_11035:
               this.renderCircle(bufferBuilder, matrix4f, (rad) -> {
                  return (float)(pos.field_1352 + Math.cos(rad) * width);
               }, (rad) -> {
                  return (float)(pos.field_1351 + Math.sin(rad) * width);
               }, (rad) -> {
                  return (float)pos.field_1350;
               }, r, g, b, a);
               break;
            case field_11039:
            case field_11034:
               this.renderCircle(bufferBuilder, matrix4f, (rad) -> {
                  return (float)pos.field_1352;
               }, (rad) -> {
                  return (float)(pos.field_1351 + Math.cos(rad) * width);
               }, (rad) -> {
                  return (float)(pos.field_1350 + Math.sin(rad) * width);
               }, r, g, b, a);
            }
         } else if (hitResult instanceof class_3966) {
            class_3966 entityHitResult = (class_3966)hitResult;
            bufferBuilder.method_1328(class_5596.field_29344, class_290.field_1576);
            class_238 box = OLEPOSSUtils.getLerpedBox(entityHitResult.method_17782(), (double)tickDelta).method_989(-camPos.field_1352, -camPos.field_1351, -camPos.field_1350);
            Render3DUtils.drawOutlines(stack, bufferBuilder, (float)box.field_1323, (float)box.field_1322, (float)box.field_1321, (float)box.field_1320, (float)box.field_1325, (float)box.field_1324, r, g, b, a);
         }

         tessellator.method_1350();
      }
   }

   private void renderCircle(class_287 bufferBuilder, Matrix4f matrix4f, Function<Double, Float> x, Function<Double, Float> y, Function<Double, Float> z, float r, float g, float b, float a) {
      for(double ar = 0.0D; ar <= 360.0D; ar += 9.0D) {
         double rad = Math.toRadians(ar);
         bufferBuilder.method_22918(matrix4f, (Float)x.apply(rad), (Float)y.apply(rad), (Float)z.apply(rad)).method_22915(r, g, b, a).method_1344();
      }

   }

   private class_239 drawLine(Trajectories.SimulationData data, double[] velocity, class_1799 itemStack, float tickDelta, class_4587 stack) {
      class_243 pos = (class_243)data.startPos.apply(itemStack, tickDelta);
      Matrix4f matrix4f = stack.method_23760().method_23761();
      RenderSystem.setShader(class_757::method_34540);
      class_289 tessellator = class_289.method_1348();
      class_287 bufferBuilder = tessellator.method_1349();
      bufferBuilder.method_1328(class_5596.field_29345, class_290.field_1576);
      MutableDouble dist = new MutableDouble(0.0D);
      this.vertex(bufferBuilder, matrix4f, pos, pos, dist);
      class_238 box = this.getBox(pos, data);

      for(int i = 0; i < (Integer)this.maxTicks.get(); ++i) {
         class_243 prevPos = pos;
         pos = pos.method_1031(velocity[0], velocity[1], velocity[2]);
         ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(prevPos, pos, class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
         class_239 blockHitResult = DamageUtils.raycast(DamageUtils.raycastContext, false);
         class_3966 entityHitResult = class_1675.method_37226(BlackOut.mc.field_1687, BlackOut.mc.field_1724, prevPos, pos, box.method_1012(velocity[0], velocity[1], velocity[2]).method_1014(1.0D), (entity) -> {
            return entity != BlackOut.mc.field_1724 && this.canHit(entity);
         }, 0.3F);
         boolean blockValid = blockHitResult.method_17783() != class_240.field_1333;
         boolean entityValid = entityHitResult != null && entityHitResult.method_17783() == class_240.field_1331;
         Object hitResult;
         if (blockValid && entityValid) {
            if (prevPos.method_1022(entityHitResult.method_17784()) < prevPos.method_1022(blockHitResult.method_17784())) {
               hitResult = entityHitResult;
            } else {
               hitResult = blockHitResult;
            }
         } else if (blockValid) {
            hitResult = blockHitResult;
         } else if (entityValid) {
            hitResult = entityHitResult;
         } else {
            hitResult = null;
         }

         if (hitResult != null) {
            this.vertex(bufferBuilder, matrix4f, ((class_239)hitResult).method_17784(), prevPos, dist);
            tessellator.method_1350();
            return (class_239)hitResult;
         }

         data.physics.accept(box, velocity);
         box = this.getBox(pos, data);
         this.vertex(bufferBuilder, matrix4f, pos, prevPos, dist);
      }

      tessellator.method_1350();
      return null;
   }

   private void vertex(class_287 bufferBuilder, Matrix4f matrix4f, class_243 pos, class_243 prevPos, MutableDouble dist) {
      DoubleConsumer<class_243, Double> consumer = (vec, d) -> {
         Color color = this.withAlpha(this.getColor(), this.getAlpha(d));
         class_243 camPos = BlackOut.mc.field_1773.method_19418().method_19326();
         bufferBuilder.method_22918(matrix4f, (float)(vec.field_1352 - camPos.field_1352), (float)(vec.field_1351 - camPos.field_1351), (float)(vec.field_1350 - camPos.field_1350)).method_22915((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F).method_1344();
      };
      double totalDist = prevPos.method_1022(pos);
      if (dist.getValue() <= (Double)this.fadeLength.get()) {
         for(double i = 1.0D; i < 30.0D; ++i) {
            double delta = i / 30.0D;
            consumer.accept(prevPos.method_35590(pos, delta), dist.getValue() + i / 30.0D * totalDist);
         }
      } else {
         consumer.accept(pos, dist.getValue());
      }

      dist.add(totalDist);
   }

   private Color withAlpha(Color color, float alpha) {
      return new Color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F * alpha);
   }

   private float getAlpha(double dist) {
      return (float)Math.min(dist / (Double)this.fadeLength.get(), 1.0D);
   }

   private class_238 getBox(class_243 pos, Trajectories.SimulationData data) {
      return new class_238(pos.field_1352 - data.width / 2.0D, pos.field_1351, pos.field_1350 - data.width / 2.0D, pos.field_1352 + data.width / 2.0D, pos.field_1351 + data.height, pos.field_1350 + data.width / 2.0D);
   }

   private boolean canHit(class_1297 entity) {
      if (!entity.method_49108()) {
         return false;
      } else {
         return !BlackOut.mc.field_1724.method_5794(entity);
      }
   }

   private Color getColor() {
      Color color = Color.WHITE;
      switch((Trails.ColorMode)this.colorMode.get()) {
      case Custom:
         color = ((BlackOutColor)this.clr.get()).getColor();
         break;
      case Rainbow:
         int rainbowColor = ColorUtils.getRainbow(4.0F, ((Double)this.saturation.get()).floatValue(), 1.0F, 150L);
         color = new Color(rainbowColor >> 16 & 255, rainbowColor >> 8 & 255, rainbowColor & 255, ((BlackOutColor)this.clr.get()).alpha);
         break;
      case Wave:
         color = ColorUtils.getWave(((BlackOutColor)this.clr.get()).getColor(), ((BlackOutColor)this.clr1.get()).getColor(), 1.0D, 1.0D, 1);
      }

      return color;
   }

   private double[] getVelocity(double[] d, float yaw, double simulation) {
      double[] velocity = new double[]{(double)(-class_3532.method_15374(yaw * 0.017453292F) * class_3532.method_15362(Managers.ROTATION.getNextPitch() * 0.017453292F)), (double)(-class_3532.method_15374((Managers.ROTATION.getNextPitch() + (float)d[1]) * 0.017453292F)), (double)(class_3532.method_15362(yaw * 0.017453292F) * class_3532.method_15362(Managers.ROTATION.getNextPitch() * 0.017453292F))};
      if (simulation != 0.0D) {
         this.rotateVelocity(velocity, RotationUtils.rotationVec((double)yaw, (double)(Managers.ROTATION.getNextPitch() - 90.0F), 1.0D), simulation);
      }

      velocity[0] *= d[0];
      velocity[1] *= d[0];
      velocity[2] *= d[0];
      if ((Boolean)this.playerVelocity.get()) {
         velocity[0] += BlackOut.mc.field_1724.method_18798().field_1352;
         if (!BlackOut.mc.field_1724.method_24828()) {
            velocity[1] += BlackOut.mc.field_1724.method_18798().field_1351;
         }

         velocity[2] += BlackOut.mc.field_1724.method_18798().field_1350;
      }

      return velocity;
   }

   private void initMap() {
      double[] snowball = new double[]{1.5D, 0.0D};
      double[] exp = new double[]{0.7D, -20.0D};
      this.put(0.25D, 0.25D, (stack, tickDelta) -> {
         return OLEPOSSUtils.getLerpedPos(BlackOut.mc.field_1724, (double)tickDelta).method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()) - 0.1D, 0.0D);
      }, (stack) -> {
         return snowball;
      }, (box, vel) -> {
         double f = OLEPOSSUtils.inWater(box) ? 0.8D : 0.99D;
         vel[0] *= f;
         vel[1] *= f;
         vel[2] *= f;
         vel[1] -= 0.03D;
      }, class_1802.field_8543, class_1802.field_8803);
      this.put(0.5D, 0.5D, (stack, tickDelta) -> {
         return OLEPOSSUtils.getLerpedPos(BlackOut.mc.field_1724, (double)tickDelta).method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()) - 0.1D, 0.0D);
      }, (stack) -> {
         BowSpam bowSpam = BowSpam.getInstance();
         int i;
         if (bowSpam.enabled && BlackOut.mc.field_1690.field_1904.method_1434()) {
            i = (Integer)bowSpam.charge.get();
         } else {
            i = stack.method_7935() - BlackOut.mc.field_1724.method_6014();
         }

         float f = Math.max(class_1753.method_7722(i), 0.1F);
         return new double[]{(double)f * 3.0D, 0.0D};
      }, (box, vel) -> {
         double f = OLEPOSSUtils.inWater(box) ? 0.6D : 0.99D;
         vel[0] *= f;
         vel[1] *= f;
         vel[2] *= f;
         vel[1] -= 0.05D;
      }, class_1802.field_8102);
      this.put(0.5D, 0.5D, (stack, tickDelta) -> {
         return OLEPOSSUtils.getLerpedPos(BlackOut.mc.field_1724, (double)tickDelta).method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()) - (class_1764.method_7772(stack, class_1802.field_8639) ? 0.15D : 0.1D), 0.0D);
      }, (stack) -> {
         return new double[]{class_1764.method_7772(stack, class_1802.field_8639) ? 1.6D : 3.15D, 0.0D};
      }, (box, vel) -> {
         if (!class_1764.method_7772(BlackOut.mc.field_1724.method_6047(), class_1802.field_8639)) {
            double f = OLEPOSSUtils.inWater(box) ? 0.6D : 0.99D;
            vel[0] *= f;
            vel[1] *= f;
            vel[2] *= f;
            vel[1] -= 0.05D;
         }
      }, class_1802.field_8399);
      this.put(0.25D, 0.25D, (stack, tickDelta) -> {
         return OLEPOSSUtils.getLerpedPos(BlackOut.mc.field_1724, (double)tickDelta).method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()) - 0.1D, 0.0D);
      }, (stack) -> {
         return exp;
      }, (box, vel) -> {
         double f = OLEPOSSUtils.inWater(box) ? 0.8D : 0.99D;
         vel[0] *= f;
         vel[1] *= f;
         vel[2] *= f;
         vel[1] -= 0.07D;
      }, class_1802.field_8287);
      this.put(0.25D, 0.25D, (stack, tickDelta) -> {
         return OLEPOSSUtils.getLerpedPos(BlackOut.mc.field_1724, (double)tickDelta).method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()) - 0.1D, 0.0D);
      }, (stack) -> {
         return snowball;
      }, (box, vel) -> {
         double f = OLEPOSSUtils.inWater(box) ? 0.8D : 0.99D;
         vel[0] *= f;
         vel[1] *= f;
         vel[2] *= f;
         vel[1] -= 0.03D;
      }, class_1802.field_8634);
   }

   private void put(double width, double height, DoubleFunction<class_1799, Float, class_243> startPos, Function<class_1799, double[]> speed, DoubleConsumer<class_238, double[]> physics, class_1792... items) {
      class_1792[] var9 = items;
      int var10 = items.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         class_1792 item = var9[var11];
         this.dataMap.put(item, new Trajectories.SimulationData(width, height, startPos, speed, physics));
      }

   }

   private static record SimulationData(double width, double height, DoubleFunction<class_1799, Float, class_243> startPos, Function<class_1799, double[]> speed, DoubleConsumer<class_238, double[]> physics) {
      private SimulationData(double width, double height, DoubleFunction<class_1799, Float, class_243> startPos, Function<class_1799, double[]> speed, DoubleConsumer<class_238, double[]> physics) {
         this.width = width;
         this.height = height;
         this.startPos = startPos;
         this.speed = speed;
         this.physics = physics;
      }

      public double width() {
         return this.width;
      }

      public double height() {
         return this.height;
      }

      public DoubleFunction<class_1799, Float, class_243> startPos() {
         return this.startPos;
      }

      public Function<class_1799, double[]> speed() {
         return this.speed;
      }

      public DoubleConsumer<class_238, double[]> physics() {
         return this.physics;
      }
   }
}
