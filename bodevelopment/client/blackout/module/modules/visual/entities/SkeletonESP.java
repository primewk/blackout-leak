package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.WireframeRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_742;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;

public class SkeletonESP extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> friendColor;

   public SkeletonESP() {
      super("Skeleton ESP", ".", SubCategory.ENTITIES, true);
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.friendColor = this.sgGeneral.c("Friend Color", new BlackOutColor(0, 255, 255, 255), ".");
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         Iterator var2 = BlackOut.mc.field_1687.method_18456().iterator();

         while(var2.hasNext()) {
            class_742 player = (class_742)var2.next();
            if (player != BlackOut.mc.field_1724) {
               WireframeRenderer.matrixStack.method_22903();
               Render3DUtils.setRotation(WireframeRenderer.matrixStack);
               Render3DUtils.start();
               WireframeRenderer.provider.consumer.start();
               WireframeRenderer.drawEntity(player, event.tickDelta, WireframeRenderer.provider);
               List<class_243[]> positions = WireframeRenderer.provider.consumer.positions;
               RenderSystem.setShader(class_757::method_34535);
               class_289 tessellator = class_289.method_1348();
               class_287 builder = tessellator.method_1349();
               builder.method_1328(class_5596.field_27377, class_290.field_29337);
               RenderSystem.lineWidth(1.5F);
               BlackOutColor color = (BlackOutColor)(Managers.FRIENDS.isFriend(player) ? this.friendColor : this.lineColor).get();
               this.renderBones(positions, builder, (float)color.red / 255.0F, (float)color.green / 255.0F, (float)color.blue / 255.0F, (float)color.alpha / 255.0F);
               tessellator.method_1350();
               WireframeRenderer.matrixStack.method_22909();
            }
         }

      }
   }

   private void renderBones(List<class_243[]> positions, class_287 builder, float red, float green, float blue, float alpha) {
      class_243 chest = class_243.field_1353;
      class_243 ass = class_243.field_1353;
      if (positions.size() >= 36) {
         for(int i = 0; i < 6; ++i) {
            class_243 boxTop = this.average((class_243[])positions.get(i * 6));
            class_243 boxBottom = this.average((class_243[])positions.get(i * 6 + 1));
            class_243 legBottom;
            switch(i) {
            case 0:
               this.line(builder, boxTop.method_35590(boxBottom, 0.25D), boxBottom, red, green, blue, alpha);
               break;
            case 1:
               chest = boxTop.method_35590(boxBottom, 0.05D);
               ass = boxTop.method_35590(boxBottom, 0.95D);
               this.line(builder, boxTop, ass, red, green, blue, alpha);
               break;
            case 2:
            case 3:
               legBottom = boxTop.method_35590(boxBottom, 0.1D);
               class_243 handBottom = boxTop.method_35590(boxBottom, 0.9D);
               this.line(builder, legBottom, handBottom, red, green, blue, alpha);
               this.line(builder, legBottom, chest, red, green, blue, alpha);
               break;
            case 4:
            case 5:
               legBottom = boxTop.method_35590(boxBottom, 0.9D);
               this.line(builder, boxTop, legBottom, red, green, blue, alpha);
               this.line(builder, boxTop, ass, red, green, blue, alpha);
            }
         }

      }
   }

   private void line(class_287 builder, class_243 pos, class_243 pos2, float red, float green, float blue, float alpha) {
      class_243 normal = pos2.method_1020(pos).method_1029();
      builder.method_22912((double)((float)pos.field_1352), (double)((float)pos.field_1351), (double)((float)pos.field_1350)).method_22915(red, green, blue, alpha).method_22914((float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
      builder.method_22912((double)((float)pos2.field_1352), (double)((float)pos2.field_1351), (double)((float)pos2.field_1350)).method_22915(red, green, blue, alpha).method_22914((float)normal.field_1352, (float)normal.field_1351, (float)normal.field_1350).method_1344();
   }

   private class_243 average(class_243... vecs) {
      class_243 total = vecs[0];

      for(int i = 1; i < vecs.length; ++i) {
         total = total.method_1019(vecs[i]);
      }

      return total.method_1021((double)(1.0F / (float)vecs.length));
   }
}
