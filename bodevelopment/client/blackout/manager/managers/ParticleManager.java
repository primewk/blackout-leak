package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_5253.class_5254;

public class ParticleManager extends Manager {
   private final TimerList<ParticleManager.Particle> particles = new TimerList(true);

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return BlackOut.mc.field_1687 == null || BlackOut.mc.field_1724 == null;
      });
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      this.particles.forEach((timer) -> {
         ((ParticleManager.Particle)timer.value).tick();
      });
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      class_243 cameraPos = BlackOut.mc.field_1773.method_19418().method_19326();
      class_4587 stack = Render3DUtils.matrices;
      stack.method_22903();
      Render3DUtils.setRotation(stack);
      GlStateManager._disableDepthTest();
      GlStateManager._enableBlend();
      GlStateManager._disableCull();
      this.particles.forEach((timer) -> {
         ((ParticleManager.Particle)timer.value).render((double)this.calcAlpha(class_3532.method_15350((double)(System.currentTimeMillis() - timer.startTime) / 1000.0D / timer.time, 0.0D, 1.0D)), stack, cameraPos);
      });
      stack.method_22909();
   }

   private float calcAlpha(double delta) {
      if (delta < 0.1D) {
         return (float)(delta * 10.0D);
      } else {
         return delta > 0.5D ? (float)(1.0D - (delta - 0.5D) * 2.0D) : 1.0F;
      }
   }

   public void addBouncy(class_243 pos, class_243 motion, double time, int color, int shadowColor) {
      this.particles.add(new ParticleManager.BouncyParticle(pos, motion, color, shadowColor), time);
   }

   public void addFriction(class_243 pos, class_243 motion, double friction, double time, int color, int shadowColor) {
      this.particles.add(new ParticleManager.FrictionParticle(pos, motion, friction, color, shadowColor), time);
   }

   private static int alphaMulti(int c, double alpha) {
      int r = class_5254.method_27765(c);
      int g = class_5254.method_27766(c);
      int b = class_5254.method_27767(c);
      int a = class_5254.method_27762(c);
      int alp = (int)Math.round((double)a * alpha);
      return (alp & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255;
   }

   private static class BouncyParticle implements ParticleManager.Particle {
      private class_243 pos;
      private class_243 prev;
      private double motionX;
      private double motionY;
      private double motionZ;
      private final int color;
      private final int shadowColor;

      private BouncyParticle(class_243 pos, class_243 motion, int color, int shadowColor) {
         this.pos = pos;
         this.prev = pos;
         this.motionX = motion.field_1352;
         this.motionY = motion.field_1351;
         this.motionZ = motion.field_1350;
         this.color = color;
         this.shadowColor = shadowColor;
         this.tick();
      }

      public void tick() {
         this.prev = this.pos;
         class_238 box = class_238.method_30048(this.pos, 0.05D, 0.05D, 0.05D);
         if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, box.method_1012(this.motionX, 0.0D, 0.0D))) {
            this.motionX = this.doTheBounciness(this.motionX);
         }

         if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, box.method_1012(0.0D, this.motionY, 0.0D))) {
            this.motionY = this.doTheBounciness(this.motionY);
         }

         if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, box.method_1012(0.0D, 0.0D, this.motionZ))) {
            this.motionZ = this.doTheBounciness(this.motionZ);
         }

         this.pos = this.pos.method_1031(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.98D;
         this.motionZ *= 0.98D;
         this.motionY = (this.motionY - 0.08D) * 0.98D;
      }

      private double doTheBounciness(double motion) {
         return motion * -0.7D;
      }

      public void render(double alpha, class_4587 stack, class_243 cameraPos) {
         double x = class_3532.method_16436((double)BlackOut.mc.method_1488(), this.prev.field_1352, this.pos.field_1352) - cameraPos.field_1352;
         double y = class_3532.method_16436((double)BlackOut.mc.method_1488(), this.prev.field_1351, this.pos.field_1351) - cameraPos.field_1351;
         double z = class_3532.method_16436((double)BlackOut.mc.method_1488(), this.prev.field_1350, this.pos.field_1350) - cameraPos.field_1350;
         stack.method_22903();
         stack.method_22904(x, y, z);
         stack.method_22905(0.02F, 0.02F, 0.02F);
         stack.method_22907(BlackOut.mc.field_1773.method_19418().method_23767());
         stack.method_22903();
         RenderUtils.rounded(stack, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 5.0F, ParticleManager.alphaMulti(this.color, alpha), ParticleManager.alphaMulti(this.shadowColor, alpha));
         stack.method_22909();
         stack.method_22909();
      }
   }

   private static class FrictionParticle implements ParticleManager.Particle {
      private class_243 pos;
      private class_243 prev;
      private class_243 motion;
      private final double friction;
      private final int color;
      private final int shadowColor;

      private FrictionParticle(class_243 pos, class_243 motion, double friction, int color, int shadowColor) {
         this.pos = pos;
         this.prev = pos;
         this.motion = motion;
         this.friction = friction;
         this.color = color;
         this.shadowColor = shadowColor;
         this.tick();
      }

      public void tick() {
         this.prev = this.pos;
         this.pos = this.pos.method_1019(this.motion = this.motion.method_1021(this.friction));
      }

      public void render(double alpha, class_4587 stack, class_243 cameraPos) {
         double x = class_3532.method_16436((double)BlackOut.mc.method_1488(), this.prev.field_1352, this.pos.field_1352) - cameraPos.field_1352;
         double y = class_3532.method_16436((double)BlackOut.mc.method_1488(), this.prev.field_1351, this.pos.field_1351) - cameraPos.field_1351;
         double z = class_3532.method_16436((double)BlackOut.mc.method_1488(), this.prev.field_1350, this.pos.field_1350) - cameraPos.field_1350;
         stack.method_22903();
         stack.method_22904(x, y, z);
         stack.method_22905(0.02F, 0.02F, 0.02F);
         stack.method_22907(BlackOut.mc.field_1773.method_19418().method_23767());
         stack.method_22903();
         RenderUtils.rounded(stack, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 5.0F, ParticleManager.alphaMulti(this.color, alpha), ParticleManager.alphaMulti(this.shadowColor, alpha));
         stack.method_22909();
         stack.method_22909();
      }
   }

   private interface Particle {
      void tick();

      void render(double var1, class_4587 var3, class_243 var4);
   }
}
