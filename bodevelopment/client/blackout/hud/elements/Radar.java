package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Iterator;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_742;

public class Radar extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Radar.Style> style;
   public final Setting<Integer> range;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> fadeLines;
   private final BackgroundMultiSetting background;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> enemyColor;
   private final Setting<BlackOutColor> friendColor;

   public Radar() {
      super("Radar", ".");
      this.style = this.sgGeneral.e("Style", Radar.Style.Blackout, ".");
      this.range = this.sgGeneral.i("Range", 32, 0, 128, 1, ".");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background", () -> {
         return this.style.get() == Radar.Style.Blackout;
      });
      this.blur = this.sgGeneral.b("Blur", true, "Use blur", () -> {
         return this.style.get() == Radar.Style.Blackout;
      });
      this.fadeLines = this.sgGeneral.b("Fade Lines", false, ".");
      this.background = BackgroundMultiSetting.of(this.sgGeneral, () -> {
         return (Boolean)this.bg.get() && this.style.get() == Radar.Style.Blackout;
      }, "Radar");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 255, 255, 80), "Line Color");
      this.enemyColor = this.sgGeneral.c("Enemy Color", new BlackOutColor(255, 255, 255, 80), "Enemy Color");
      this.friendColor = this.sgGeneral.c("Friend Color", new BlackOutColor(100, 100, 255, 180), "Friend Color");
      this.setSize(40.0F, 40.0F);
   }

   public void render() {
      this.stack.method_22903();
      switch((Radar.Style)this.style.get()) {
      case Exhibition:
         this.setSize(42.0F, 42.0F);
         RenderUtils.drawSkeetBox(this.stack, -2.0F, -2.0F, 46.0F, 46.0F, true);
         if ((Boolean)this.fadeLines.get()) {
            RenderUtils.fadeLine(this.stack, 0.0F, 21.0F, 42.0F, 21.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
            RenderUtils.fadeLine(this.stack, 21.0F, 0.0F, 21.0F, 42.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
         } else {
            RenderUtils.line(this.stack, 1.0F, 21.0F, 41.0F, 21.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
            RenderUtils.line(this.stack, 21.0F, 1.0F, 21.0F, 41.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
         }
         break;
      case Blackout:
         this.setSize(40.0F, 40.0F);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, 40.0F, 40.0F, 4.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get()) {
            this.background.render(this.stack, 0.0F, 0.0F, 40.0F, 40.0F, 4.0F, 4.0F);
         }

         if ((Boolean)this.fadeLines.get()) {
            RenderUtils.fadeLine(this.stack, 0.0F, 20.0F, 40.0F, 20.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
            RenderUtils.fadeLine(this.stack, 20.0F, 0.0F, 20.0F, 40.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
         } else {
            RenderUtils.line(this.stack, 0.0F, 20.0F, 40.0F, 20.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
            RenderUtils.line(this.stack, 20.0F, 0.0F, 20.0F, 40.0F, ((BlackOutColor)this.lineColor.get()).getRGB());
         }
      }

      this.stack.method_46416(20.0F, 20.0F, 0.0F);
      Iterator var1 = BlackOut.mc.field_1687.method_18456().iterator();

      while(var1.hasNext()) {
         class_1657 player = (class_1657)var1.next();
         if (player != BlackOut.mc.field_1724 && this.shouldRender(player)) {
            boolean isFriend = Managers.FRIENDS.isFriend(player);
            double dist = player.method_19538().method_1020(BlackOut.mc.field_1724.method_19538()).method_37267();
            double yaw = RotationUtils.getYaw(player.method_19538());
            yaw = Math.toRadians(class_3532.method_15338(yaw - (double)BlackOut.mc.field_1724.method_36454() - 90.0D));
            float x = (float)(Math.cos(yaw) * dist);
            float z = (float)(Math.sin(yaw) * dist);
            x /= (float)(Integer)this.range.get();
            if (!(Math.abs(x) >= 1.0F)) {
               x *= 20.0F;
               z /= (float)(Integer)this.range.get();
               if (!(Math.abs(z) >= 1.0F)) {
                  z *= 20.0F;
                  this.renderEnemy(this.stack, x, z, isFriend);
               }
            }
         }
      }

      this.stack.method_22909();
   }

   public void renderEnemy(class_4587 stack, float x, float y, boolean friend) {
      if (this.style.get() == Radar.Style.Exhibition) {
         RenderUtils.quad(stack, x - 1.0F, y - 1.0F, 3.0F, 3.0F, Color.BLACK.getRGB());
         RenderUtils.quad(stack, x, y, 1.0F, 1.0F, friend ? Color.YELLOW.getRGB() : Color.RED.getRGB());
      } else {
         RenderUtils.rounded(stack, x, y, 0.0F, 0.0F, 1.0F, 0.0F, friend ? ((BlackOutColor)this.friendColor.get()).getRGB() : ((BlackOutColor)this.enemyColor.get()).getRGB(), ColorUtils.SHADOW100I);
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

      return entity != BlackOut.mc.field_1724 ? true : Freecam.getInstance().enabled;
   }

   public static enum Style {
      Blackout,
      Exhibition;

      // $FF: synthetic method
      private static Radar.Style[] $values() {
         return new Radar.Style[]{Blackout, Exhibition};
      }
   }
}
