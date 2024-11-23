package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_239.class_240;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class Sight extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> lineWidth;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<Double> fadeIn;
   private final Setting<Double> length;
   private final class_4587 stack;

   public Sight() {
      super("Sight", "Shows where people are looking at.", SubCategory.ENTITIES, true);
      this.lineWidth = this.sgGeneral.d("Line Width", 1.5D, 0.5D, 5.0D, 0.05D, ".");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), "");
      this.fadeIn = this.sgGeneral.d("Fade In", 1.0D, 0.0D, 50.0D, 0.5D, "");
      this.length = this.sgGeneral.d("Length", 5.0D, 0.0D, 50.0D, 0.5D, "");
      this.stack = new class_4587();
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.stack.method_22903();
      Render3DUtils.setRotation(this.stack);
      Render3DUtils.start();
      RenderSystem.setShader(class_757::method_34535);
      class_289 tessellator = class_289.method_1348();
      class_287 bufferBuilder = tessellator.method_1349();
      bufferBuilder.method_1328(class_5596.field_27377, class_290.field_29337);
      RenderSystem.lineWidth(((Double)this.lineWidth.get()).floatValue());
      Matrix4f matrix4f = this.stack.method_23760().method_23761();
      Matrix3f matrix3f = this.stack.method_23760().method_23762();
      class_243 camPos = BlackOut.mc.field_1773.method_19418().method_19326();
      BlackOut.mc.field_1687.method_18456().forEach((player) -> {
         if (player != BlackOut.mc.field_1724) {
            class_243 eyePos = OLEPOSSUtils.getLerpedPos(player, (double)event.tickDelta).method_1031(0.0D, (double)player.method_18381(player.method_18376()), 0.0D);
            class_243 lookPos = RotationUtils.rotationVec((double)class_3532.method_16439(BlackOut.mc.method_1488(), player.field_5982, player.method_36454()), (double)class_3532.method_16439(BlackOut.mc.method_1488(), player.field_6004, player.method_36455()), eyePos, (Double)this.fadeIn.get() + (Double)this.length.get());
            ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(eyePos, lookPos);
            class_3965 hitResult = DamageUtils.raycast(DamageUtils.raycastContext, false);
            class_243 hitPos;
            if (hitResult.method_17783() == class_240.field_1333) {
               hitPos = lookPos;
            } else {
               hitPos = hitResult.method_17784();
            }

            this.render(bufferBuilder, matrix4f, matrix3f, eyePos.method_1020(camPos), hitPos.method_1020(camPos));
         }
      });
      tessellator.method_1350();
      Render3DUtils.end();
      this.stack.method_22909();
   }

   private void render(class_287 bufferBuilder, Matrix4f matrix4f, Matrix3f matrix3f, class_243 start, class_243 end) {
      double l = start.method_1022(end);
      if (l != 0.0D) {
         double lerpDelta = (Double)this.fadeIn.get() / l;
         class_243 lerpedPos = start.method_35590(end, Math.min(lerpDelta, 1.0D));
         class_243 normal = lerpedPos.method_1020(start).method_1029();
         bufferBuilder.method_22918(matrix4f, (float)start.field_1352, (float)start.field_1351, (float)start.field_1350).method_39415(ColorUtils.withAlpha(((BlackOutColor)this.lineColor.get()).getRGB(), 0)).method_23763(matrix3f, (float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
         bufferBuilder.method_22918(matrix4f, (float)lerpedPos.field_1352, (float)lerpedPos.field_1351, (float)lerpedPos.field_1350).method_39415(ColorUtils.withAlpha(((BlackOutColor)this.lineColor.get()).getRGB(), Math.min((int)(1.0D / lerpDelta * 255.0D), 255))).method_23763(matrix3f, (float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
         if (!(lerpDelta >= 1.0D)) {
            class_243 normal2 = end.method_1020(lerpedPos).method_1029();
            bufferBuilder.method_22918(matrix4f, (float)lerpedPos.field_1352, (float)lerpedPos.field_1351, (float)lerpedPos.field_1350).method_39415(((BlackOutColor)this.lineColor.get()).getRGB()).method_23763(matrix3f, (float)normal2.field_1352, (float)normal2.field_1351, (float)normal2.field_1350).method_1344();
            bufferBuilder.method_22918(matrix4f, (float)end.field_1352, (float)end.field_1351, (float)end.field_1350).method_39415(((BlackOutColor)this.lineColor.get()).getRGB()).method_23763(matrix3f, (float)normal2.field_1352, (float)normal2.field_1351, (float)normal2.field_1350).method_1344();
         }
      }
   }
}
