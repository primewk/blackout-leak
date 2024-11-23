package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_7833;

public class ESP extends Module {
   private static ESP INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<List<class_1299<?>>> entityTypes;
   private final Setting<Boolean> hp;
   private final Setting<Boolean> box;
   private final Setting<Boolean> fill;
   private final Setting<Boolean> fadeFill;
   public final Setting<Boolean> renderName;
   private final Setting<ESP.NameMode> nameMode;
   private final Setting<Boolean> renderItem;
   private final Setting<BlackOutColor> txt;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> fadeColor;
   private final Setting<BlackOutColor> fillColor;
   private final Setting<BlackOutColor> mxhp;
   private final Setting<BlackOutColor> mnhp;
   private final class_4587 stack;
   private final List<class_1297> entities;
   private float progress;

   public ESP() {
      super("ESP", "Extra Sensory Perception", SubCategory.ENTITIES, true);
      this.entityTypes = this.sgGeneral.el("Entities", ".", class_1299.field_6097);
      this.hp = this.sgGeneral.b("Show HP", true, ".");
      this.box = this.sgGeneral.b("Box", true, "Draws a box around the entity");
      this.fill = this.sgGeneral.b("Box Fill", false, "Fills the box");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.fill;
      Objects.requireNonNull(var10005);
      this.fadeFill = var10001.b("Fade Fill", false, ".", var10005::get);
      this.renderName = this.sgGeneral.b("Render Name", false, ".");
      var10001 = this.sgGeneral;
      ESP.NameMode var10003 = ESP.NameMode.EntityName;
      var10005 = this.renderName;
      Objects.requireNonNull(var10005);
      this.nameMode = var10001.e("Name Mode", var10003, "", var10005::get);
      this.renderItem = this.sgGeneral.b("Render Item", false, ".");
      this.txt = this.sgGeneral.c("Text Color", new BlackOutColor(255, 255, 255, 255), ".");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 255, 255, 200), ".");
      this.fadeColor = this.sgGeneral.c("Line Fade Color", new BlackOutColor(16, 16, 16, 200), ".");
      var10001 = this.sgGeneral;
      BlackOutColor var1 = new BlackOutColor(255, 255, 255, 50);
      var10005 = this.fill;
      Objects.requireNonNull(var10005);
      this.fillColor = var10001.c("Fill Color", var1, ".", var10005::get);
      var10001 = this.sgGeneral;
      var1 = new BlackOutColor(115, 115, 255, 200);
      var10005 = this.hp;
      Objects.requireNonNull(var10005);
      this.mxhp = var10001.c("Max HP Color", var1, ".", var10005::get);
      var10001 = this.sgGeneral;
      var1 = new BlackOutColor(255, 30, 30, 200);
      var10005 = this.hp;
      Objects.requireNonNull(var10005);
      this.mnhp = var10001.c("Min HP Color", var1, ".", var10005::get);
      this.stack = new class_4587();
      this.entities = new ArrayList();
      this.progress = 0.0F;
      INSTANCE = this;
   }

   public static ESP getInstance() {
      return INSTANCE;
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.entities.clear();
         BlackOut.mc.field_1687.field_27733.method_31791((entity) -> {
            if (this.shouldRender(entity)) {
               this.entities.add(entity);
            }

         });
         this.entities.sort(Comparator.comparingDouble((entity) -> {
            return (double)(-BlackOut.mc.field_1724.method_5739(entity));
         }));
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

      return entity != BlackOut.mc.field_1724 && ((List)this.entityTypes.get()).contains(entity.method_5864());
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         GlStateManager._disableDepthTest();
         GlStateManager._enableBlend();
         GlStateManager._disableCull();
         class_243 cameraPos = BlackOut.mc.field_1773.method_19418().method_19326();
         this.entities.forEach((entity) -> {
            this.render2D((double)event.tickDelta, cameraPos, entity);
         });
      }
   }

   public void render2D(double tickDelta, class_243 cameraPos, class_1297 entity) {
      double x = class_3532.method_16436(tickDelta, entity.field_6014, entity.method_23317()) - cameraPos.field_1352;
      double y = class_3532.method_16436(tickDelta, entity.field_6036, entity.method_23318()) - cameraPos.field_1351 + (double)(entity.method_17682() / 2.0F);
      double z = class_3532.method_16436(tickDelta, entity.field_5969, entity.method_23321()) - cameraPos.field_1350;
      float s = 1.25F;
      double cameraPitch = (double)Math.abs(BlackOut.mc.field_1773.method_19418().method_19329() / 90.0F);
      double anglePitch = Math.abs(RotationUtils.getPitch(new class_243(x, y, z), class_243.field_1353) / 90.0D);
      double yaw1 = 90.0D - Math.abs(90.0D - Math.abs(RotationUtils.yawAngle((double)(BlackOut.mc.field_1773.method_19418().method_19330() + 180.0F), RotationUtils.getYaw(new class_243(x, y, z), class_243.field_1353, 0.0D))));
      double yaw = yaw1 / 90.0D;
      float width = this.getWidth(entity.method_5829(), cameraPitch * (1.0D - anglePitch) * yaw);
      float height = this.getHeight(entity.method_5829(), anglePitch);
      this.stack.method_22903();
      Render3DUtils.setRotation(this.stack);
      this.stack.method_22904(x, y, z);
      this.stack.method_22905(s, -s, s);
      this.stack.method_22907(class_7833.field_40716.rotation((float)Math.toRadians((double)(-BlackOut.mc.field_1773.method_19418().method_19330() + 180.0F))));
      this.stack.method_22907(class_7833.field_40714.rotation((float)Math.toRadians((double)BlackOut.mc.field_1773.method_19418().method_19329())));
      String name = ((ESP.NameMode)this.nameMode.get()).getName(entity);
      float textScale = 0.01F;
      if ((Boolean)this.renderName.get()) {
         BlackOut.FONT.text(this.stack, name, textScale, -width / 2.0F + BlackOut.FONT.getWidth(name) * textScale / 2.0F, -height / 2.0F - BlackOut.FONT.getHeight() * 1.2F * textScale * 1.1F, ((BlackOutColor)this.txt.get()).getColor(), true, false);
      }

      if ((Boolean)this.renderItem.get() && entity instanceof class_742 && ((class_742)entity).method_6047() != null) {
         String stackName = ((class_742)entity).method_6047().method_7964().getString();
         BlackOut.FONT.text(this.stack, stackName, textScale, -width / 2.0F + BlackOut.FONT.getWidth(stackName) * textScale / 2.0F, height / 2.0F + BlackOut.FONT.getHeight() * textScale * 1.1F, ((BlackOutColor)this.txt.get()).getColor(), true, false);
      }

      if ((Boolean)this.box.get()) {
         RenderUtils.line(this.stack, -width / 2.0F, -height / 2.0F, -width / 2.0F, height / 2.0F, ((BlackOutColor)this.lineColor.get()).getRGB(), ((BlackOutColor)this.fadeColor.get()).getRGB());
         RenderUtils.line(this.stack, width / 2.0F, -height / 2.0F, width / 2.0F, height / 2.0F, ((BlackOutColor)this.lineColor.get()).getRGB(), ((BlackOutColor)this.fadeColor.get()).getRGB());
         RenderUtils.line(this.stack, -width / 2.0F, -height / 2.0F, width / 2.0F, -height / 2.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
         RenderUtils.line(this.stack, -width / 2.0F, height / 2.0F, width / 2.0F, height / 2.0F, ((BlackOutColor)this.fadeColor.get()).getRGB());
      }

      if ((Boolean)this.fill.get() && !(Boolean)this.fadeFill.get()) {
         RenderUtils.quad(this.stack, -width / 2.0F, -height / 2.0F, width, height, ((BlackOutColor)this.fillColor.get()).getRGB());
      }

      if ((Boolean)this.fill.get() && (Boolean)this.fadeFill.get()) {
         RenderUtils.topFade(this.stack, -width / 2.0F, -height / 2.0F, width, height, ((BlackOutColor)this.fillColor.get()).getRGB());
      }

      if (entity instanceof class_1309) {
         class_1309 livingEntity = (class_1309)entity;
         float frameTime = BlackOut.mc.method_1534() / 20.0F * 4.0F;
         float targetProgress = Math.min((livingEntity.method_6032() + livingEntity.method_6067()) / livingEntity.method_6063(), 1.0F);
         float progressDelta = frameTime + frameTime * Math.abs(targetProgress - this.progress);
         if (targetProgress > this.progress) {
            this.progress = Math.min(this.progress + progressDelta, targetProgress);
         } else {
            this.progress = Math.max(this.progress - progressDelta, targetProgress);
         }

         if ((Boolean)this.hp.get()) {
            RenderUtils.quad(this.stack, -width / 2.0F - 0.05F, height / 2.0F, 0.03F, height * -this.progress, this.getColor(this.progress).getRGB());
         }
      }

      this.stack.method_22909();
   }

   private float getWidth(class_238 box, double pitch) {
      return (float)class_3532.method_16436(Math.sin(pitch * 3.141592653589793D / 2.0D), box.method_17939(), box.method_17940());
   }

   private float getHeight(class_238 box, double pitch) {
      return (float)class_3532.method_16436(Math.sin(pitch * 3.141592653589793D / 2.0D), box.method_17940(), box.method_17939());
   }

   private Color getColor(float health) {
      return ColorUtils.lerpColor((double)Math.min(health, 1.0F), ((BlackOutColor)this.mnhp.get()).getColor(), ((BlackOutColor)this.mxhp.get()).getColor());
   }

   public static enum NameMode {
      Display((entity) -> {
         return entity.method_5476().getString();
      }),
      EntityName((entity) -> {
         return entity.method_5477().getString();
      });

      private final Function<class_1297, String> function;

      private NameMode(Function<class_1297, String> function) {
         this.function = function;
      }

      private String getName(class_1297 entity) {
         return (String)this.function.apply(entity);
      }

      // $FF: synthetic method
      private static ESP.NameMode[] $values() {
         return new ESP.NameMode[]{Display, EntityName};
      }
   }
}
