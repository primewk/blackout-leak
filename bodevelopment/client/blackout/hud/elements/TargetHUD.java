package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.StatsManager;
import bodevelopment.client.blackout.module.modules.combat.offensive.Aura;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoCrystal;
import bodevelopment.client.blackout.module.modules.combat.offensive.BedAura;
import bodevelopment.client.blackout.module.modules.combat.offensive.PistonCrystal;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.RoundedColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.font.CustomFontRenderer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Iterator;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_640;
import net.minecraft.class_742;

public class TargetHUD extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<TargetHUD.TargetMode> targetMode;
   private final Setting<TargetHUD.RenderType> renderType;
   private final Setting<Double> renderHeight;
   private final Setting<Double> dist;
   private final Setting<Double> targetRange;
   public final Setting<TargetHUD.Mode> mode;
   public final Setting<TargetHUD.ArmorCount> countMode;
   private final Setting<Boolean> hp;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> shadow;
   private final BackgroundMultiSetting background;
   private final Setting<BlackOutColor> textColor;
   private final Setting<BlackOutColor> secondaryColor;
   private final RoundedColorMultiSetting healthBar;
   private final RoundedColorMultiSetting armorBar;
   private float delta;
   private float progress;
   private float armorProgress;
   private class_742 target;
   private class_742 renderTarget;
   private class_243 renderPos;
   private class_2960 renderSkin;

   public TargetHUD() {
      super("Target HUD", ".");
      this.targetMode = this.sgGeneral.e("Target Mode", TargetHUD.TargetMode.ModuleTarget, ".");
      this.renderType = this.sgGeneral.e("Render Type", TargetHUD.RenderType.Hud, ".");
      this.renderHeight = this.sgGeneral.d("Render Height", 0.75D, 0.0D, 1.0D, 0.05D, ".", () -> {
         return this.renderType.get() == TargetHUD.RenderType.Player;
      });
      this.dist = this.sgGeneral.d("Distance From Target", 0.25D, 0.0D, 1.0D, 0.05D, ".", () -> {
         return this.renderType.get() == TargetHUD.RenderType.Player;
      });
      this.targetRange = this.sgGeneral.d("Target Range", 20.0D, 0.0D, 200.0D, 2.0D, ".", () -> {
         return this.targetMode.get() == TargetHUD.TargetMode.Closest;
      });
      this.mode = this.sgGeneral.e("Mode", TargetHUD.Mode.Blackout, ".");
      this.countMode = this.sgGeneral.e("Armor Count Mode", TargetHUD.ArmorCount.Average, ".", () -> {
         return this.mode.get() == TargetHUD.Mode.BlackoutNew;
      });
      this.hp = this.sgGeneral.b("HP text", false, ".", () -> {
         return this.mode.get() == TargetHUD.Mode.Blackout;
      });
      this.blur = this.sgGeneral.b("Blur", true, ".", () -> {
         return this.mode.get() != TargetHUD.Mode.Exhibition;
      });
      this.shadow = this.sgGeneral.b("Shadow", true, ".", () -> {
         return this.mode.get() != TargetHUD.Mode.Exhibition;
      });
      this.background = BackgroundMultiSetting.of(this.sgColor, () -> {
         return this.mode.get() != TargetHUD.Mode.ExhibitionNew;
      }, (String)null);
      this.textColor = this.sgColor.c("Text Color", new BlackOutColor(255, 255, 255, 255), "Text Color");
      this.secondaryColor = this.sgColor.c("Secondary Text Color", new BlackOutColor(220, 60, 90, 255), ".", () -> {
         return this.mode.get() == TargetHUD.Mode.Arsenic;
      });
      this.healthBar = RoundedColorMultiSetting.of(this.sgColor, "Bar");
      this.armorBar = RoundedColorMultiSetting.of(this.sgColor, () -> {
         return this.mode.get() == TargetHUD.Mode.BlackoutNew;
      }, "Armor Bar");
      this.delta = 0.0F;
      this.progress = 0.0F;
      this.armorProgress = 0.0F;
      this.target = null;
      this.renderTarget = null;
      this.renderPos = class_243.field_1353;
      this.setSize(10.0F, 10.0F);
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.updateTarget();
         if (this.target != null) {
            this.setRendering(this.target);
         }

         if (this.renderTarget == null) {
            this.setRendering(BlackOut.mc.field_1724);
         }

         this.setSize(this.getRenderWidth(), this.getRenderHeight());
         if (this.renderType.get() == TargetHUD.RenderType.Hud) {
            this.renderTargetHUD(false);
         }

      }
   }

   public void onRemove() {
      BlackOut.EVENT_BUS.unsubscribe(this);
   }

   @Event
   public void onRender(RenderEvent.Hud.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         if (this.renderType.get() != TargetHUD.RenderType.Hud && this.renderTarget != null) {
            this.stack.method_22903();
            RenderUtils.unGuiScale(this.stack);
            double yaw = Math.toRadians(RotationUtils.getYaw(this.renderPos, BlackOut.mc.field_1773.method_19418().method_19326(), 0.0D));
            class_241 f = RenderUtils.getCoords(this.renderPos.field_1352 + Math.cos(yaw) * (Double)this.dist.get(), this.renderPos.field_1351 + this.renderTarget.method_5829().method_17940() * (Double)this.renderHeight.get(), this.renderPos.field_1350 + Math.sin(yaw) * (Double)this.dist.get(), true);
            if (f == null) {
               this.stack.method_22909();
            } else {
               this.stack.method_46416(f.field_1343, f.field_1342, 0.0F);
               this.stack.method_22905(this.getScale() * 2.0F, this.getScale() * 2.0F, 0.0F);
               this.renderTargetHUD(true);
               this.stack.method_22909();
            }
         }
      }
   }

   private void setRendering(class_742 player) {
      this.renderTarget = player;
      this.renderPos = new class_243(class_3532.method_16436((double)BlackOut.mc.method_1488(), this.renderTarget.field_6014, this.renderTarget.method_23317()), class_3532.method_16436((double)BlackOut.mc.method_1488(), this.renderTarget.field_6036, this.renderTarget.method_23318()), class_3532.method_16436((double)BlackOut.mc.method_1488(), this.renderTarget.field_5969, this.renderTarget.method_23321()));
   }

   private void renderTargetHUD(boolean center) {
      if (this.target != null) {
         this.delta = Math.min(this.delta + this.frameTime, 1.0F);
      } else {
         this.delta = Math.max(this.delta - this.frameTime, 0.0F);
      }

      float health = this.renderTarget.method_6032();
      float nameScale = this.renderTarget.method_5477().getString().length() >= 12 ? 0.7F : 0.9F;
      float renderHealth = this.renderTarget.method_6032() + this.renderTarget.method_6067();
      float renderScale = (float)AnimUtils.easeOutQuart((double)this.delta);
      float colorHealth = Math.min((this.renderTarget.method_6032() + this.renderTarget.method_6067()) / this.renderTarget.method_6063(), 1.0F);
      float targetProgress = Math.min(health / 20.0F, 1.0F);
      float progressDelta = this.frameTime + this.frameTime * Math.abs(targetProgress - this.progress);
      if (targetProgress > this.progress) {
         this.progress = Math.min(this.progress + progressDelta, targetProgress);
      } else {
         this.progress = Math.max(this.progress - progressDelta, targetProgress);
      }

      float armorTargetProgress = Math.min(this.getDurability(this.renderTarget), 1.0F);
      float armorProgressDelta = this.frameTime + this.frameTime * Math.abs(armorTargetProgress - this.armorProgress);
      if (armorTargetProgress > this.armorProgress) {
         this.armorProgress = Math.min(this.armorProgress + armorProgressDelta, armorTargetProgress);
      } else {
         this.armorProgress = Math.max(this.armorProgress - armorProgressDelta, armorTargetProgress);
      }

      Color yes = new Color(0, 0, 0, 85);
      this.stack.method_22903();
      this.stack.method_46416(this.getRenderWidth() / 2.0F, center ? 0.0F : this.getRenderHeight() / 2.0F, 0.0F);
      this.stack.method_22905(renderScale, renderScale, 1.0F);
      float prevAlpha = Renderer.getAlpha();
      Renderer.setAlpha(renderScale);
      this.stack.method_22903();
      this.stack.method_46416(this.getRenderWidth() / -2.0F, this.getRenderHeight() / -2.0F, 0.0F);
      String dist;
      float textX;
      float var10000;
      String txt;
      float x;
      switch((TargetHUD.Mode)this.mode.get()) {
      case Blackout:
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 105.0F, 20.0F, 3.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, 105.0F, 20.0F, 3.0F, 3.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), nameScale, 27.0F, 1.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         this.healthBar.render(this.stack, 27.0F, 15.0F, 70.0F * this.progress, 1.0F, 2.0F, 3.0F);
         CustomFontRenderer var25 = BlackOut.FONT;
         int var10001 = Math.round(renderHealth);
         float width = var25.getWidth("HP: " + var10001) * 0.6F;
         x = 27.0F + 70.0F * this.progress - width;
         textX = x <= 27.0F ? 27.0F : x;
         if ((Boolean)this.hp.get()) {
            BlackOut.FONT.text(this.stack, "HP: " + Math.round(renderHealth), 0.6F, textX, 8.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         }

         this.drawFace(this.stack, 1.1F, -1.0F, -1.0F);
         break;
      case ExhibitionNew:
         RenderUtils.rounded(this.stack, 0.0F, 0.0F, 105.0F, 28.0F, 3.0F, (Boolean)this.shadow.get() ? 5.0F : 0.0F, (new Color(25, 25, 25, 255)).getRGB(), (new Color(0, 0, 0, 100)).getRGB());
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString() + " HP: " + Math.round(renderHealth), 0.7F, 27.0F, 1.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         RenderUtils.rounded(this.stack, 28.0F, 11.0F, 72.0F * this.progress, 1.0F, 2.0F, 0.0F, (new Color(255, 202, 24, 255)).getRGB(), 0);
         RenderUtils.rounded(this.stack, 28.0F, 11.0F, 72.0F * this.progress, 1.0F, 1.0F, 0.0F, (new Color(255, 242, 0, 255)).getRGB(), 0);
         RenderUtils.rounded(this.stack, 6.0F, 8.0F, 12.0F, 12.0F, 5.0F, 0.0F, (new Color(45, 45, 45, 255)).getRGB(), 0);
         this.drawArmor(this.stack, this.renderTarget, 32.0F, 19.0F);
         this.drawFace(this.stack, 1.1F, 1.0F, 3.0F);
         break;
      case BlackoutInfo:
         txt = "0";
         class_640 entry = BlackOut.mc.method_1562().method_2871(this.renderTarget.method_5667());
         if (entry != null) {
            txt = String.valueOf(entry.method_2959());
         }

         boolean naked = !this.getArmor(this.renderTarget);
         StatsManager.TrackerData trackerData = Managers.STATS.getStats(this.renderTarget);
         int popAmount = trackerData == null ? 0 : trackerData.pops;
         String info = "HP: " + Math.round(renderHealth) + " Ping: " + txt + "ms Pops: " + popAmount;
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 120.0F, 50.0F, 3.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, 120.0F, 50.0F, 3.0F, 3.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 1.0F, 60.0F, 5.0F, ((BlackOutColor)this.textColor.get()).getColor(), true, true);
         RenderUtils.quad(this.stack, 5.0F, 3.0F + BlackOut.FONT.getHeight(), 110.0F, 1.0F, (new Color(0, 0, 0, 100)).getRGB());
         this.healthBar.render(this.stack, 5.0F, 3.0F + BlackOut.FONT.getHeight(), 110.0F * this.progress, 1.0F, 0.0F, 0.0F);
         BlackOut.FONT.text(this.stack, info, 0.8F, 60.0F, 12.0F + BlackOut.FONT.getHeight(), ((BlackOutColor)this.textColor.get()).getColor(), true, true);
         RenderUtils.rounded(this.stack, 20.0F, 24.0F + BlackOut.FONT.getHeight(), 80.0F, 10.0F, 3.0F, 3.0F, (new Color(0, 0, 0, 80)).getRGB(), (new Color(0, 0, 0, 40)).getRGB());
         if (naked) {
            BlackOut.FONT.text(this.stack, "Naked!", 0.8F, 60.0F, 30.0F + BlackOut.FONT.getHeight(), ((BlackOutColor)this.textColor.get()).getColor(), true, true);
         } else {
            this.drawArmor(this.stack, this.renderTarget, 20.0F, 24.0F + BlackOut.FONT.getHeight());
         }
         break;
      case Old:
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 108.0F, 24.0F, 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, 108.0F, 24.0F, 0.0F, 3.0F);
         this.healthBar.render(this.stack, 25.0F, 14.0F, 4.0F * health, 8.0F, 0.0F, 0.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 1.0F, 25.0F, 4.0F, Color.WHITE, false, false);
         BlackOut.FONT.text(this.stack, "HP: " + Math.round(renderHealth), 0.8F, 65.0F, 18.0F, Color.WHITE, true, true);
         this.drawFace(this.stack, 1.0F, 2.0F, 2.0F);
         break;
      case Tenacity:
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 115.0F, 26.0F, 6.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, 115.0F, 26.0F, 6.0F, 3.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 1.0F, 70.0F, 0.0F, ((BlackOutColor)this.textColor.get()).getColor(), true, false);
         RenderUtils.rounded(this.stack, 32.0F, 25.0F, 80.0F * this.progress, 0.2F, 1.0F, 0.0F, yes.getRGB(), yes.getRGB());
         this.healthBar.render(this.stack, 32.0F, 25.0F, 80.0F * this.progress, 0.2F, 1.0F, 0.0F);
         var10000 = this.renderTarget.method_6032() / this.renderTarget.method_6063();
         txt = Math.round(var10000 * 100.0F) + "%";
         BlackOut.FONT.text(this.stack, txt, 0.8F, 32.0F + 80.0F * this.progress - BlackOut.FONT.getWidth(txt) * 0.8F, 17.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         this.drawFace(this.stack, 1.2F, 0.0F, 1.0F);
         break;
      case Tenacity2:
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 100.0F, 26.0F, 6.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, 100.0F, 26.0F, 6.0F, 3.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 1.0F, 65.0F, 32.0F - BlackOut.FONT.getHeight() * 4.0F, ((BlackOutColor)this.textColor.get()).getColor(), true, false);
         RenderUtils.rounded(this.stack, 31.0F, 13.2F, 68.0F, 0.4F, 1.2F, 0.0F, yes.getRGB(), yes.getRGB());
         this.healthBar.render(this.stack, 31.0F, 13.2F, 68.0F * this.progress, 0.4F, 1.2F, 0.0F);
         var10000 = this.renderTarget.method_6032() / this.renderTarget.method_6063();
         txt = Math.round(var10000 * 100.0F) + "%";
         dist = Math.round(this.renderTarget.method_5739(BlackOut.mc.field_1724)) + "m";
         BlackOut.FONT.text(this.stack, txt + " " + dist, 0.8F, 65.0F, BlackOut.FONT.getHeight() * 2.5F, ((BlackOutColor)this.textColor.get()).getColor(), true, false);
         this.drawFace(this.stack, 1.2F, 0.0F, 1.0F);
         break;
      case BlackoutNew:
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 100.0F, 20.0F, 3.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, 100.0F, 20.0F, 3.0F, 3.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 0.75F, 27.0F, 1.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         RenderUtils.rounded(this.stack, 27.0F, 11.0F, 70.0F, 0.1F, 1.0F, 0.0F, (new Color(0, 0, 0, 100)).getRGB(), (new Color(0, 0, 0, 100)).getRGB());
         this.healthBar.render(this.stack, 27.0F, 11.0F, 70.0F * this.progress, 0.1F, 1.0F, 1.0F);
         RenderUtils.rounded(this.stack, 27.0F, 18.0F, 70.0F, 0.1F, 1.0F, 0.0F, (new Color(0, 0, 0, 100)).getRGB(), (new Color(0, 0, 0, 100)).getRGB());
         this.armorBar.render(this.stack, 27.0F, 18.0F, 70.0F * this.getDurability(this.renderTarget), 0.1F, 1.0F, 1.0F);
         txt = "HP: " + Math.round(renderHealth);
         x = 99.0F - BlackOut.FONT.getWidth(txt) * 0.75F;
         BlackOut.FONT.text(this.stack, txt, 0.75F, x, 1.0F, new Color(150, 150, 150, 255), false, false);
         this.drawFace(this.stack, 1.1F, -1.0F, -1.0F);
         break;
      case Arsenic:
         txt = "Name: " + this.renderTarget.method_5477().getString();
         dist = "HP: " + Math.round(renderHealth);
         textX = Math.max(BlackOut.FONT.getWidth(txt), BlackOut.FONT.getWidth(dist));
         float height = BlackOut.FONT.getHeight() * 2.0F;
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, textX, height, 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, 0.0F, 0.0F, textX, height, 0.0F, 3.0F);
         this.healthBar.render(this.stack, 0.0F, -1.0F, textX * this.progress, 1.0F, 0.0F, 0.0F);
         BlackOut.FONT.text(this.stack, "Name: ", 1.0F, 0.0F, 1.0F, ((BlackOutColor)this.secondaryColor.get()).getColor(), false, false);
         BlackOut.FONT.text(this.stack, "HP: ", 1.0F, 0.0F, BlackOut.FONT.getHeight() + 1.0F, ((BlackOutColor)this.secondaryColor.get()).getColor(), false, false);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 1.0F, BlackOut.FONT.getWidth("Name: "), 1.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         BlackOut.FONT.text(this.stack, String.valueOf(Math.round(renderHealth)), 1.0F, BlackOut.FONT.getWidth("HP: "), BlackOut.FONT.getHeight() + 1.0F, ((BlackOutColor)this.textColor.get()).getColor(), false, false);
         break;
      case Exhibition:
         RenderUtils.quad(this.stack, 0.0F, 0.0F, 114.0F, 32.0F, (new Color(0, 0, 0, 150)).getRGB());
         RenderUtils.quad(this.stack, 1.0F, 1.0F, 30.0F, 30.0F, (new Color(200, 200, 200, 255)).getRGB());
         this.drawFace(this.stack, 1.4F, 2.0F, 2.0F);
         BlackOut.FONT.text(this.stack, this.renderTarget.method_5477().getString(), 1.0F, 34.0F, 2.0F, ((BlackOutColor)this.textColor.get()).getRGB(), false, false);
         RenderUtils.quad(this.stack, 34.0F, 3.0F + BlackOut.FONT.getHeight(), 3.7F * health, 3.0F, (new Color(220, 220, 0, 255)).getRGB());
         RenderUtils.quad(this.stack, 34.0F, 2.5F + BlackOut.FONT.getHeight(), 74.0F, 0.5F, Color.BLACK.getRGB());
         RenderUtils.quad(this.stack, 34.0F, 6.0F + BlackOut.FONT.getHeight(), 74.0F, 0.5F, Color.BLACK.getRGB());

         for(int i = 0; i < 11; ++i) {
            RenderUtils.quad(this.stack, (float)(33.5D + 7.4D * (double)i), 2.5F + BlackOut.FONT.getHeight(), 0.5F, 4.0F, Color.BLACK.getRGB());
         }

         BlackOut.FONT.text(this.stack, "HP: " + Math.round(renderHealth) + ": Dist: " + Math.round(BlackOut.mc.field_1724.method_5739(this.renderTarget)), 0.6F, 34.0F, 16.0F, ((BlackOutColor)this.textColor.get()).getRGB(), false, false);
         BlackOut.FONT.text(this.stack, "Yaw: " + Math.round(this.renderTarget.method_36454()) + " Pitch: " + Math.round(this.renderTarget.method_36455()) + " BodyYaw: " + Math.round(this.renderTarget.field_6283), 0.6F, 34.0F, 16.0F + BlackOut.FONT.getHeight() * 0.6F, ((BlackOutColor)this.textColor.get()).getRGB(), false, false);
         BlackOut.FONT.text(this.stack, "TOG: 0 HURT: " + this.renderTarget.field_6235 + " TE: " + this.renderTarget.field_6012, 0.6F, 34.0F, 16.0F + BlackOut.FONT.getHeight() * 1.2F, ((BlackOutColor)this.textColor.get()).getRGB(), false, false);
      }

      Renderer.setAlpha(prevAlpha);
      this.stack.method_22909();
      this.stack.method_22909();
   }

   private float getRenderWidth() {
      float var10000;
      switch((TargetHUD.Mode)this.mode.get()) {
      case Blackout:
      case ExhibitionNew:
         var10000 = 105.0F;
         break;
      case BlackoutInfo:
         var10000 = 120.0F;
         break;
      case Old:
         var10000 = 108.0F;
         break;
      case Tenacity:
         var10000 = 115.0F;
         break;
      case Tenacity2:
      case BlackoutNew:
         var10000 = 100.0F;
         break;
      case Arsenic:
         var10000 = 50.0F;
         break;
      case Exhibition:
         var10000 = 114.0F;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private float getRenderHeight() {
      float var10000;
      switch((TargetHUD.Mode)this.mode.get()) {
      case Blackout:
      case BlackoutNew:
         var10000 = 20.0F;
         break;
      case ExhibitionNew:
         var10000 = 28.0F;
         break;
      case BlackoutInfo:
         var10000 = 50.0F;
         break;
      case Old:
         var10000 = 24.0F;
         break;
      case Tenacity:
      case Tenacity2:
         var10000 = 26.0F;
         break;
      case Arsenic:
         var10000 = 18.0F;
         break;
      case Exhibition:
         var10000 = 32.0F;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private void updateTarget() {
      this.target = null;
      switch((TargetHUD.TargetMode)this.targetMode.get()) {
      case ModuleTarget:
         this.moduleTarget();
         break;
      case Closest:
         this.closestTarget();
      }

      if (this.target != null) {
         this.renderSkin = this.target.method_52814().comp_1626();
      }

   }

   private void moduleTarget() {
      if (!AutoCrystal.getInstance().enabled || (this.target = AutoCrystal.getInstance().targetedPlayer) == null) {
         if (!Aura.getInstance().enabled || (this.target = Aura.targetedPlayer) == null) {
            if (!BedAura.getInstance().enabled || (this.target = BedAura.targetedPlayer) == null) {
               if (PistonCrystal.getInstance().enabled) {
                  this.target = PistonCrystal.targetedPlayer;
               }

            }
         }
      }
   }

   private void closestTarget() {
      double distance = Double.MAX_VALUE;
      Iterator var3 = BlackOut.mc.field_1687.method_18456().iterator();

      while(var3.hasNext()) {
         class_742 player = (class_742)var3.next();
         if (player != BlackOut.mc.field_1724 && !Managers.FRIENDS.isFriend(player) && !((double)player.method_5739(BlackOut.mc.field_1724) > (Double)this.targetRange.get())) {
            double d = (double)BlackOut.mc.field_1724.method_5739(player);
            if (d < distance) {
               this.target = player;
               distance = d;
            }
         }
      }

   }

   private void drawFace(class_4587 stack, float scale, float x, float y) {
      float size = scale * 20.0F;
      if (this.renderSkin != null) {
         if (this.mode.get() == TargetHUD.Mode.Old || this.mode.get() == TargetHUD.Mode.Exhibition) {
            TextureRenderer.renderQuad(stack, x, y, size, size, 0.125F, 0.125F, 0.25F, 0.25F, BlackOut.mc.method_1531().method_4619(this.renderSkin).method_4624());
         }

         if (this.mode.get() != TargetHUD.Mode.Tenacity && this.mode.get() != TargetHUD.Mode.Tenacity2) {
            TextureRenderer.renderFitRounded(stack, x, y, size, size, 0.125F, 0.125F, 0.25F, 0.25F, 5.0F, 40, BlackOut.mc.method_1531().method_4619(this.renderSkin).method_4624());
         } else {
            TextureRenderer.renderFitRounded(stack, x, y, size, size, 0.125F, 0.125F, 0.25F, 0.25F, 12.0F, 40, BlackOut.mc.method_1531().method_4619(this.renderSkin).method_4624());
         }
      }

   }

   private void drawArmor(class_4587 stack, class_1657 player, float x, float y) {
      int i;
      class_1799 itemStack;
      switch((TargetHUD.Mode)this.mode.get()) {
      case ExhibitionNew:
         for(i = 0; i < 4; ++i) {
            itemStack = (class_1799)player.method_31548().field_7548.get(3 - i);
            RenderUtils.rounded(stack, x + (float)(i * 18), y, 8.0F, 8.0F, 2.0F, 0.0F, (new Color(45, 45, 45, 255)).getRGB(), 0);
            RenderUtils.rounded(stack, x + (float)(i * 18) + 1.0F, y + 1.0F, 6.0F, 6.0F, 2.0F, 0.0F, (new Color(25, 25, 25, 255)).getRGB(), 0);
            if (!itemStack.method_7960()) {
               RenderUtils.renderItem(stack, itemStack.method_7909(), x - 4.0F + (float)(i * 18), y - 4.0F, 10.0F);
            }
         }

         return;
      case BlackoutInfo:
         for(i = 0; i < 4; ++i) {
            itemStack = (class_1799)player.method_31548().field_7548.get(3 - i);
            if (!itemStack.method_7960()) {
               RenderUtils.renderItem(stack, itemStack.method_7909(), x + (float)(i * 20), y - 3.0F, 16.0F);
            }
         }
      }

   }

   private boolean getArmor(class_1657 entity) {
      for(int i = 0; i < 4; ++i) {
         if (!entity.method_31548().method_7372(i).method_7960()) {
            return true;
         }
      }

      return false;
   }

   private float getDurability(class_1657 entity) {
      float durability = 0.0F;
      int armors = 0;
      float lowest = 1.0F;

      for(int i = 0; i < 4; ++i) {
         if (entity.method_31548().method_7372(i).method_7960()) {
            lowest = 0.0F;
         } else {
            class_1799 itemStack = entity.method_31548().method_7372(i);
            ++armors;
            switch((TargetHUD.ArmorCount)this.countMode.get()) {
            case Average:
               durability += (float)Math.round((float)((itemStack.method_7936() - itemStack.method_7919()) * 100) / (float)itemStack.method_7936());
               return durability / 100.0F / (float)armors;
            case Lowest:
               durability = (float)Math.round((float)((itemStack.method_7936() - itemStack.method_7919()) * 100) / (float)itemStack.method_7936()) / 100.0F;
               if (durability < lowest) {
                  lowest = durability;
               }
            }
         }
      }

      return lowest;
   }

   public static enum TargetMode {
      ModuleTarget,
      Closest;

      // $FF: synthetic method
      private static TargetHUD.TargetMode[] $values() {
         return new TargetHUD.TargetMode[]{ModuleTarget, Closest};
      }
   }

   public static enum RenderType {
      Hud,
      Player;

      // $FF: synthetic method
      private static TargetHUD.RenderType[] $values() {
         return new TargetHUD.RenderType[]{Hud, Player};
      }
   }

   public static enum Mode {
      Old,
      Blackout,
      BlackoutInfo,
      ExhibitionNew,
      Tenacity,
      Tenacity2,
      BlackoutNew,
      Arsenic,
      Exhibition;

      // $FF: synthetic method
      private static TargetHUD.Mode[] $values() {
         return new TargetHUD.Mode[]{Old, Blackout, BlackoutInfo, ExhibitionNew, Tenacity, Tenacity2, BlackoutNew, Arsenic, Exhibition};
      }
   }

   public static enum ArmorCount {
      Average,
      Lowest;

      // $FF: synthetic method
      private static TargetHUD.ArmorCount[] $values() {
         return new TargetHUD.ArmorCount[]{Average, Lowest};
      }
   }

   public static enum ColorMode {
      Dynamic,
      Rainbow,
      Custom,
      Wave;

      // $FF: synthetic method
      private static TargetHUD.ColorMode[] $values() {
         return new TargetHUD.ColorMode[]{Dynamic, Rainbow, Custom, Wave};
      }
   }
}
