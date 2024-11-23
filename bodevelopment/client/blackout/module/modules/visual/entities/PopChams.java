package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PopEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.WireframeRenderer;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4050;
import net.minecraft.class_4587;
import net.minecraft.class_572;
import net.minecraft.class_583;
import net.minecraft.class_742;
import net.minecraft.class_7833;
import net.minecraft.class_897;
import net.minecraft.class_922;

public class PopChams extends Module {
   private static PopChams INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> time;
   private final Setting<Double> y;
   private final Setting<Double> scale;
   private final Setting<Boolean> enemy;
   private final Setting<Boolean> friends;
   private final Setting<Boolean> self;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final TimerList<PopChams.Pop> pops;
   private final class_4587 matrixStack;

   public PopChams() {
      super("Pop Chams", ".", SubCategory.ENTITIES, true);
      this.time = this.sgGeneral.d("Time", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.y = this.sgGeneral.d("Y", 0.0D, -5.0D, 5.0D, 0.1D, ".");
      this.scale = this.sgGeneral.d("Scale", 1.0D, 0.0D, 5.0D, 0.1D, ".");
      this.enemy = this.sgGeneral.b("Enemy", true, ".");
      this.friends = this.sgGeneral.b("Friends", true, ".");
      this.self = this.sgGeneral.b("Self", false, ".");
      this.renderShape = this.sgGeneral.e("Render Shape", RenderShape.Full, "Which parts of boxes should be rendered.");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 255, 255, 255), "Fill Color");
      this.sideColor = this.sgGeneral.c("Side Color", new BlackOutColor(255, 255, 255, 50), "Side Color");
      this.pops = new TimerList(true);
      this.matrixStack = new class_4587();
      INSTANCE = this;
   }

   public static PopChams getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.matrixStack.method_22903();
         Render3DUtils.setRotation(this.matrixStack);
         this.pops.forEach((timer) -> {
            this.renderPop((PopChams.Pop)timer.value, (float)class_3532.method_15370((double)System.currentTimeMillis(), (double)timer.startTime, (double)timer.endTime));
         });
         this.matrixStack.method_22909();
      }
   }

   @Event
   public void onPop(PopEvent event) {
      if (this.shouldRender(event.player)) {
         this.pops.add(new PopChams.Pop(event.player), (Double)this.time.get());
      }

   }

   private boolean shouldRender(class_742 player) {
      if (player == BlackOut.mc.field_1724) {
         return (Boolean)this.self.get();
      } else {
         return Managers.FRIENDS.isFriend(player) ? (Boolean)this.friends.get() : (Boolean)this.enemy.get();
      }
   }

   private void renderPop(PopChams.Pop pop, float progress) {
      Render3DUtils.start();
      WireframeRenderer.provider.consumer.start();
      this.drawPlayer(pop.player, pop, (Double)this.y.get() * (double)progress, class_3532.method_16439(progress, 0.9375F, ((Double)this.scale.get()).floatValue() * 0.9375F));
      List<class_243[]> positions = WireframeRenderer.provider.consumer.positions;
      BlackOutColor sides = ((BlackOutColor)this.sideColor.get()).alphaMulti((double)(1.0F - progress));
      BlackOutColor lines = ((BlackOutColor)this.lineColor.get()).alphaMulti((double)(1.0F - progress));
      if (((RenderShape)this.renderShape.get()).sides) {
         WireframeRenderer.drawQuads(positions, (float)sides.red / 255.0F, (float)sides.green / 255.0F, (float)sides.blue / 255.0F, (float)sides.alpha / 255.0F);
      }

      if (((RenderShape)this.renderShape.get()).outlines) {
         WireframeRenderer.drawLines(positions, (float)lines.red / 255.0F, (float)lines.green / 255.0F, (float)lines.blue / 255.0F, (float)lines.alpha / 255.0F);
      }

   }

   private void drawPlayer(class_742 player, PopChams.Pop pop, double extraY, float scale) {
      class_897<? super class_742> entityRenderer = BlackOut.mc.field_1769.field_4109.method_3953(player);
      class_243 cameraPos = BlackOut.mc.field_1773.method_19418().method_19326();
      double x = pop.x - cameraPos.field_1352;
      double y = pop.y - cameraPos.field_1351 + extraY;
      double z = pop.z - cameraPos.field_1350;
      this.matrixStack.method_22903();
      this.matrixStack.method_22904(x, y, z);
      WireframeRenderer.hidden = true;
      this.renderModel((class_922)entityRenderer, pop, scale);
      WireframeRenderer.hidden = false;
      this.matrixStack.method_22909();
   }

   private void renderModel(class_922<? super class_742, class_583<class_742>> renderer, PopChams.Pop pop, float scale) {
      class_572<class_742> model = (class_572)renderer.method_4038();
      this.matrixStack.method_22903();
      model.field_3447 = pop.swingProgress;
      model.field_3449 = pop.riding;
      model.field_3448 = false;
      float h = pop.bodyYaw;
      float j = pop.headYaw;
      float k = j - h;
      float l;
      if (pop.hasVehicle) {
         h = pop.vehicleYaw;
         k = j - h;
         l = class_3532.method_15363(class_3532.method_15393(k), -85.0F, 85.0F);
         h = j - l;
         if (l * l > 2500.0F) {
            h += l * 0.2F;
         }

         k = j - h;
      }

      float m = pop.pitch;
      if (pop.flip) {
         m *= -1.0F;
         k *= -1.0F;
      }

      float n;
      if (pop.sleeping) {
         class_2350 direction = pop.sleepDir;
         if (direction != null) {
            n = pop.eyeHeight;
            this.matrixStack.method_46416((float)(-direction.method_10148()) * n, 0.0F, (float)(-direction.method_10165()) * n);
         }
      }

      l = pop.animationProgress;
      this.matrixStack.method_22905(scale, -scale, -scale);
      this.matrixStack.method_46416(0.0F, -1.501F, 0.0F);
      float o = 0.0F;
      if (!pop.hasVehicle) {
         n = Math.min(pop.limbSpeed, 1.0F);
         o = pop.limbPos;
      } else {
         n = 0.0F;
      }

      this.matrixStack.method_22907(class_7833.field_40716.rotation((float)Math.toRadians((double)h)));
      model.field_3396 = pop.leaningPitch;
      model.method_17087(pop.player, o, n, l, k, m);
      model.method_2828(this.matrixStack, WireframeRenderer.provider.getBuffer((class_1921)null), 69420, 0, 1.0F, 1.0F, 1.0F, 1.0F);
      this.matrixStack.method_22909();
   }

   private static class Pop {
      private final class_742 player;
      private final boolean riding;
      private final double x;
      private final double y;
      private final double z;
      private final boolean flip;
      private final float pitch;
      private final float bodyYaw;
      private final float headYaw;
      private final float swingProgress;
      private final boolean hasVehicle;
      private final float vehicleYaw;
      private final float eyeHeight;
      private final float animationProgress;
      private final float leaningPitch;
      private final float limbSpeed;
      private final float limbPos;
      private final boolean sleeping;
      private final class_2350 sleepDir;

      public Pop(class_742 player) {
         this.player = player;
         float tickDelta = BlackOut.mc.method_1488();
         this.riding = player.method_5765();
         this.bodyYaw = class_3532.method_16439(tickDelta, player.field_6220, player.field_6283);
         this.headYaw = class_3532.method_16439(tickDelta, player.field_6259, player.field_6241);
         class_1297 veh = player.method_5854();
         if (veh instanceof class_1309) {
            class_1309 livingEntity = (class_1309)veh;
            this.hasVehicle = true;
            this.vehicleYaw = class_3532.method_17821(tickDelta, livingEntity.field_6220, livingEntity.field_6283);
         } else {
            this.hasVehicle = false;
            this.vehicleYaw = 0.0F;
         }

         this.flip = class_922.method_38563(player);
         this.pitch = class_3532.method_16439(tickDelta, player.field_6004, player.method_36455());
         this.eyeHeight = player.method_18381(class_4050.field_18076) - 0.1F;
         this.animationProgress = (float)player.field_6012 + tickDelta;
         this.leaningPitch = player.method_6024(tickDelta);
         this.limbSpeed = player.field_42108.method_48570(tickDelta);
         this.limbPos = player.field_42108.method_48572(tickDelta);
         this.swingProgress = player.method_6055(tickDelta);
         this.x = class_3532.method_16436((double)tickDelta, player.field_6038, player.method_23317());
         this.y = class_3532.method_16436((double)tickDelta, player.field_5971, player.method_23318());
         this.z = class_3532.method_16436((double)tickDelta, player.field_5989, player.method_23321());
         this.sleeping = player.method_41328(class_4050.field_18078);
         this.sleepDir = player.method_18401();
      }
   }
}
