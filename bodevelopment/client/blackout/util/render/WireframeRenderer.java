package bodevelopment.client.blackout.util.render;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_757;
import net.minecraft.class_897;
import net.minecraft.class_293.class_5596;

public class WireframeRenderer {
   public static final class_4587 matrixStack = new class_4587();
   public static final ModelVertexConsumerProvider provider = new ModelVertexConsumerProvider();
   public static boolean hidden = false;

   public static void renderModel(class_742 player, BlackOutColor lineColor, BlackOutColor sideColor, RenderShape shape, float tickDelta) {
      matrixStack.method_22903();
      Render3DUtils.setRotation(matrixStack);
      Render3DUtils.start();
      provider.consumer.start();
      drawEntity(player, tickDelta, provider);
      List<class_243[]> positions = provider.consumer.positions;
      if (shape.sides) {
         drawQuads(positions, (float)sideColor.red / 255.0F, (float)sideColor.green / 255.0F, (float)sideColor.blue / 255.0F, (float)sideColor.alpha / 255.0F);
      }

      if (shape.outlines) {
         drawLines(positions, (float)lineColor.red / 255.0F, (float)lineColor.green / 255.0F, (float)lineColor.blue / 255.0F, (float)lineColor.alpha / 255.0F);
      }

      matrixStack.method_22909();
   }

   public static void drawLines(List<class_243[]> positions, float red, float green, float blue, float alpha) {
      RenderSystem.setShader(class_757::method_34535);
      class_289 tessellator = class_289.method_1348();
      class_287 builder = tessellator.method_1349();
      builder.method_1328(class_5596.field_27377, class_290.field_29337);
      List<class_243[]> rendered = new ArrayList();
      positions.forEach((arr) -> {
         for(int i = 0; i < 4; ++i) {
            class_243[] line = new class_243[]{arr[i], arr[(i + 1) % 4]};
            if (!contains(rendered, line)) {
               class_243 normal = line[1].method_1020(line[0]).method_1029();
               vertex(builder, line[0], normal, red, green, blue, alpha);
               vertex(builder, line[1], normal, red, green, blue, alpha);
               rendered.add(line);
            }
         }

      });
      tessellator.method_1350();
   }

   private static boolean contains(List<class_243[]> lines, class_243[] line) {
      Iterator var2 = lines.iterator();

      class_243[] arr;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         arr = (class_243[])var2.next();
         if (line[0].equals(arr[0]) && line[1].equals(arr[1])) {
            return true;
         }
      } while(!line[0].equals(arr[1]) || !line[1].equals(arr[0]));

      return true;
   }

   public static void vertex(class_287 builder, class_243 pos, class_243 normal, float red, float green, float blue, float alpha) {
      builder.method_22912((double)((float)pos.field_1352), (double)((float)pos.field_1351), (double)((float)pos.field_1350)).method_22915(red, green, blue, alpha).method_22914((float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
   }

   public static void drawQuads(List<class_243[]> positions, float red, float green, float blue, float alpha) {
      RenderSystem.setShader(class_757::method_34540);
      class_289 tessellator = class_289.method_1348();
      class_287 builder = tessellator.method_1349();
      builder.method_1328(class_5596.field_27382, class_290.field_1576);
      positions.forEach((arr) -> {
         class_243[] var6 = arr;
         int var7 = arr.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            class_243 pos = var6[var8];
            builder.method_22912((double)((float)pos.field_1352), (double)((float)pos.field_1351), (double)((float)pos.field_1350)).method_22915(red, green, blue, alpha).method_1344();
         }

      });
      tessellator.method_1350();
   }

   public static void drawEntity(class_742 player, float tickDelta, class_4597 vertexConsumerProvider) {
      double d = class_3532.method_16436((double)tickDelta, player.field_6038, player.method_23317());
      double e = class_3532.method_16436((double)tickDelta, player.field_5971, player.method_23318());
      double f = class_3532.method_16436((double)tickDelta, player.field_5989, player.method_23321());
      float yaw = class_3532.method_16439(tickDelta, player.field_5982, player.method_36454());
      class_897<? super class_742> entityRenderer = BlackOut.mc.field_1769.field_4109.method_3953(player);
      class_243 cameraPos = BlackOut.mc.field_1773.method_19418().method_19326();
      double x = d - cameraPos.field_1352;
      double y = e - cameraPos.field_1351;
      double z = f - cameraPos.field_1350;
      class_243 vec3d = entityRenderer.method_23169(player, tickDelta);
      double d2 = x + vec3d.method_10216();
      double e2 = y + vec3d.method_10214();
      double f2 = z + vec3d.method_10215();
      matrixStack.method_22903();
      matrixStack.method_22904(d2, e2, f2);
      hidden = true;
      entityRenderer.method_3936(player, yaw, tickDelta, matrixStack, vertexConsumerProvider, 69420);
      hidden = false;
      matrixStack.method_22909();
   }
}
