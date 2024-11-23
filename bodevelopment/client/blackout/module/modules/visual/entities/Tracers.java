package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1657;
import net.minecraft.class_241;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_742;

public class Tracers extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<List<class_1299<?>>> entityTypes;
   private final Setting<BlackOutColor> line;
   private final Setting<BlackOutColor> friendLine;
   private final class_4587 stack;
   private final List<class_1297> entities;

   public Tracers() {
      super("Tracers", "Traces to other entities", SubCategory.ENTITIES, true);
      this.entityTypes = this.sgGeneral.el("Entities", ".", class_1299.field_6097);
      this.line = this.sgGeneral.c("Line Color", new BlackOutColor(255, 255, 255, 100), ".");
      this.friendLine = this.sgGeneral.c("Friend Line Color", new BlackOutColor(150, 150, 255, 100), ".");
      this.stack = new class_4587();
      this.entities = new ArrayList();
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.entities.clear();
         BlackOut.mc.field_1687.field_27733.method_31791((entity) -> {
            if (this.shouldRender(entity)) {
               this.entities.add(entity);
            }

         });
         this.entities.sort(Comparator.comparingDouble((entity) -> {
            return -BlackOut.mc.field_1773.method_19418().method_19326().method_1022(entity.method_19538());
         }));
      }
   }

   @Event
   public void onRender(RenderEvent.Hud.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         this.entities.forEach((entity) -> {
            this.renderTracer((double)event.tickDelta, entity);
         });
         this.stack.method_22909();
      }
   }

   public void renderTracer(double tickDelta, class_1297 entity) {
      double x = class_3532.method_16436(tickDelta, entity.field_6014, entity.method_23317());
      double y = class_3532.method_16436(tickDelta, entity.field_6036, entity.method_23318());
      double z = class_3532.method_16436(tickDelta, entity.field_5969, entity.method_23321());
      this.stack.method_22903();
      Color color;
      if (entity instanceof class_1657 && Managers.FRIENDS.isFriend((class_1657)entity)) {
         color = ((BlackOutColor)this.friendLine.get()).getColor();
      } else {
         color = ((BlackOutColor)this.line.get()).getColor();
      }

      class_241 f = RenderUtils.getCoords(x, y + entity.method_5829().method_17940() / 2.0D, z, false);
      if (f == null) {
         this.stack.method_22909();
      } else {
         RenderUtils.line(this.stack, (float)BlackOut.mc.method_22683().method_4480() / 2.0F, (float)BlackOut.mc.method_22683().method_4507() / 2.0F, f.field_1343, f.field_1342, color.getRGB());
         this.stack.method_22909();
      }
   }

   public boolean shouldRender(class_1297 entity) {
      AntiBot antiBot = AntiBot.getInstance();
      if (antiBot.enabled && antiBot.mode.get() == AntiBot.HandlingMode.Ignore && entity instanceof class_742) {
         class_742 player = (class_742)entity;
         if (antiBot.getBots().contains(player)) {
            return false;
         }
      }

      if (!((List)this.entityTypes.get()).contains(entity.method_5864())) {
         return false;
      } else {
         return entity != BlackOut.mc.field_1724 ? true : Freecam.getInstance().enabled;
      }
   }
}
