package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.ColorRenderer;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.class_1299;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix4f;

public class FeetESP extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<List<class_1299<?>>> entities;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> fill;
   private final Setting<BlackOutColor> line;

   public FeetESP() {
      super("FeetESP", "Shows the feet hitbox does not show feet pictures", SubCategory.ENTITIES, true);
      this.entities = this.sgGeneral.el("Entities", ".", class_1299.field_6097);
      this.renderShape = this.sgGeneral.e("Render Shape", RenderShape.Full, "Which parts of boxes should be rendered.");
      this.fill = this.sgGeneral.c("Fill Color", new BlackOutColor(255, 255, 255, 80), "Fill Color");
      this.line = this.sgGeneral.c("Line Color", new BlackOutColor(255, 255, 255, 120), "Fill Color");
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         Render3DUtils.matrices.method_22903();
         Render3DUtils.setRotation(Render3DUtils.matrices);
         Render3DUtils.start();
         class_243 vec = BlackOut.mc.field_1773.method_19418().method_19326();
         Render3DUtils.matrices.method_22904(-vec.field_1352, -vec.field_1351, -vec.field_1350);
         ColorRenderer renderer = ColorRenderer.getInstance();
         if (((RenderShape)this.renderShape.get()).sides) {
            this.drawVertexes(false, renderer);
         }

         if (((RenderShape)this.renderShape.get()).outlines) {
            this.drawVertexes(true, renderer);
         }

         Render3DUtils.end();
         Render3DUtils.matrices.method_22909();
      }
   }

   private void drawVertexes(boolean outline, ColorRenderer renderer) {
      class_289 tessellator = class_289.method_1348();
      class_287 bufferBuilder = tessellator.method_1349();
      if (outline) {
         RenderSystem.setShader(class_757::method_34535);
         bufferBuilder.method_1328(class_5596.field_27377, class_290.field_29337);
         RenderSystem.lineWidth(1.5F);
      } else {
         renderer.startRender(Render3DUtils.matrices, class_5596.field_27382);
      }

      BlackOutColor color = outline ? (BlackOutColor)this.line.get() : (BlackOutColor)this.fill.get();
      float r = (float)color.red / 255.0F;
      float g = (float)color.green / 255.0F;
      float b = (float)color.blue / 255.0F;
      float a = (float)color.alpha / 255.0F;
      BlackOut.mc.field_1687.method_18112().forEach((entity) -> {
         if (((List)this.entities.get()).contains(entity.method_5864())) {
            class_238 box = entity.method_5829();
            class_243 pos = (new class_243(entity.field_6014, entity.field_6036, entity.field_5969)).method_35590(entity.method_19538(), (double)BlackOut.mc.method_1488());
            float minX = (float)(pos.field_1352 - box.method_17939() / 2.0D);
            float maxX = (float)(pos.field_1352 + box.method_17939() / 2.0D);
            float minZ = (float)(pos.field_1350 - box.method_17941() / 2.0D);
            float maxZ = (float)(pos.field_1350 + box.method_17941() / 2.0D);
            float y = (float)pos.field_1351;
            if (outline) {
               Matrix4f matrix4f = Render3DUtils.matrices.method_23760().method_23761();
               bufferBuilder.method_22918(matrix4f, minX, y, minZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, minX, y, maxZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, minX, y, maxZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, maxX, y, maxZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, maxX, y, maxZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, maxX, y, minZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, maxX, y, minZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
               bufferBuilder.method_22918(matrix4f, minX, y, minZ).method_22915(r, g, b, a).method_22914(0.0F, 0.0F, 0.0F).method_1344();
            } else {
               renderer.vertex(minX, y, minZ, r, g, b, a);
               renderer.vertex(minX, y, maxZ, r, g, b, a);
               renderer.vertex(maxX, y, maxZ, r, g, b, a);
               renderer.vertex(maxX, y, minZ, r, g, b, a);
            }

         }
      });
      if (outline) {
         tessellator.method_1350();
      } else {
         renderer.endRender();
      }

   }
}
