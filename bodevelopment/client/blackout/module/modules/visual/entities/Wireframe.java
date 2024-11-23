package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.WireframeRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_742;

public class Wireframe extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final List<class_742> player;

   public Wireframe() {
      super("Wireframe", "Draws a wireframe of players", SubCategory.ENTITIES, true);
      this.renderShape = this.sgGeneral.e("Render Shape", RenderShape.Full, ".");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideColor = this.sgGeneral.c("Side Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.player = new ArrayList();
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.player.clear();
         BlackOut.mc.field_1687.field_27733.method_31791((entity) -> {
            if (entity instanceof class_742 && this.shouldRender(entity)) {
               this.player.add((class_742)entity);
            }

         });
         this.player.sort(Comparator.comparingDouble((entity) -> {
            return (double)(-BlackOut.mc.field_1724.method_5739(entity));
         }));
      }
   }

   public boolean shouldRender(class_1297 entity) {
      AntiBot antiBot = AntiBot.getInstance();
      if (antiBot.enabled && antiBot.mode.get() == AntiBot.HandlingMode.Ignore && antiBot.getBots().contains(this.player)) {
         return false;
      } else {
         return entity != BlackOut.mc.field_1724;
      }
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         GlStateManager._disableDepthTest();
         GlStateManager._enableBlend();
         GlStateManager._disableCull();
         this.player.forEach((entity) -> {
            WireframeRenderer.renderModel(entity, (BlackOutColor)this.lineColor.get(), (BlackOutColor)this.sideColor.get(), (RenderShape)this.renderShape.get(), BlackOut.mc.method_1488());
         });
      }
   }
}
