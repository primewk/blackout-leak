package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Watermark extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Watermark.Mode> mode;
   private final Setting<String> extraText;
   public final Setting<Watermark.SigmaMode> sigmaMode;
   private final TextColorMultiSetting textColor;
   private final Setting<BlackOutColor> secondaryColor;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> bold;
   private final BackgroundMultiSetting background;

   public Watermark() {
      super("Watermark", "Renders the client watermark");
      this.mode = this.sgGeneral.e("Mode", Watermark.Mode.Clean, "What style to use", () -> {
         return true;
      });
      this.extraText = this.sgGeneral.s("Extra Text", "", "Added text to the client name", () -> {
         return this.mode.get() == Watermark.Mode.Exhibition && BlackOut.TYPE.isDevBuild();
      });
      this.sigmaMode = this.sgGeneral.e("Sigma Mode", Watermark.SigmaMode.SigmaJello, "What Sigma style to use", () -> {
         return this.mode.get() == Watermark.Mode.Sigma;
      });
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, () -> {
         return this.mode.get() == Watermark.Mode.Clean || this.mode.get() == Watermark.Mode.Simple || this.mode.get() == Watermark.Mode.Exhibition || this.mode.get() == Watermark.Mode.KassuK;
      }, "Text");
      this.secondaryColor = this.sgGeneral.c("Secondary Color", new BlackOutColor(255, 255, 255, 255), ".", () -> {
         return this.mode.get() == Watermark.Mode.Simple || this.mode.get() == Watermark.Mode.Exhibition || this.mode.get() == Watermark.Mode.Simple;
      });
      this.blur = this.sgGeneral.b("Blur", true, ".", () -> {
         return this.mode.get() == Watermark.Mode.Clean || this.mode.get() == Watermark.Mode.KassuK;
      });
      this.bg = this.sgGeneral.b("Background", true, ".", () -> {
         return this.mode.get() == Watermark.Mode.Clean || this.mode.get() == Watermark.Mode.KassuK;
      });
      this.bold = this.sgGeneral.b("Bold Font", true, ".", () -> {
         return this.mode.get() == Watermark.Mode.Simple;
      });
      this.background = BackgroundMultiSetting.of(this.sgGeneral, () -> {
         return this.mode.get() != Watermark.Mode.Exhibition && this.mode.get() != Watermark.Mode.Virtue && this.mode.get() != Watermark.Mode.Remix && this.mode.get() != Watermark.Mode.Sigma && this.mode.get() != Watermark.Mode.Simple && this.mode.get() != Watermark.Mode.GameSense;
      }, (String)null);
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
         String formattedTime2 = (new SimpleDateFormat("hh:mm a")).format(new Date());
         this.stack.method_22903();
         float width;
         float height;
         String extra;
         String text;
         float width;
         label80:
         switch((Watermark.Mode)this.mode.get()) {
         case Simple:
            width = BlackOut.FONT.getWidth("BlackOut") * 2.0F + BlackOut.FONT.getWidth("2.0.0");
            this.setSize(width, BlackOut.FONT.getHeight() * 2.0F);
            this.stack.method_46416(0.0F, BlackOut.FONT.getHeight() + 2.0F, 0.0F);
            this.textColor.render(this.stack, "BlackOut", 2.0F, 0.0F, 0.0F, false, true, (Boolean)this.bold.get());
            height = (Boolean)this.bold.get() ? BlackOut.BOLD_FONT.getWidth("BlackOut") * 2.0F : BlackOut.FONT.getWidth("BlackOut") * 2.0F;
            width = (Boolean)this.bold.get() ? BlackOut.BOLD_FONT.getHeight() + 4.0F : BlackOut.FONT.getHeight() + 4.0F;
            BlackOut.FONT.text(this.stack, "2.0.0", 1.0F, height, -width, ((BlackOutColor)this.secondaryColor.get()).getColor(), false, false);
            break;
         case Virtue:
            extra = "Virtue 6";
            height = BlackOut.FONT.getWidth(extra);
            this.setSize(height + 16.0F, BlackOut.FONT.getHeight() * 3.0F + 6.0F);
            RenderUtils.quad(this.stack, 0.0F, 0.0F, height + 16.0F, BlackOut.FONT.getHeight() * 3.0F + 6.0F, (new Color(125, 125, 125, 100)).getRGB());
            BlackOut.FONT.text(this.stack, extra, 1.0F, 8.0F, 2.0F, new Color(212, 212, 255, 255), false, false);
            BlackOut.FONT.text(this.stack, formattedTime2, 1.0F, 8.0F + height / 2.0F, 3.0F + BlackOut.FONT.getHeight(), new Color(230, 230, 230, 255), true, false);
            BlackOut.FONT.text(this.stack, "Fps " + BlackOut.mc.method_47599(), 1.0F, 8.0F + height / 2.0F, 4.0F + BlackOut.FONT.getHeight() * 2.0F, new Color(230, 230, 230, 255), true, false);
            RenderUtils.quad(this.stack, 0.0F, 0.0F, height + 16.0F, 1.0F, Color.BLACK.getRGB());
            RenderUtils.quad(this.stack, 0.0F, BlackOut.FONT.getHeight() * 3.0F + 6.0F, height + 16.0F, 1.0F, Color.BLACK.getRGB());
            RenderUtils.quad(this.stack, 0.0F, 0.0F, 1.0F, BlackOut.FONT.getHeight() * 3.0F + 6.0F, Color.BLACK.getRGB());
            RenderUtils.quad(this.stack, height + 16.0F, 0.0F, 1.0F, BlackOut.FONT.getHeight() * 3.0F + 7.0F, Color.BLACK.getRGB());
            break;
         case Clean:
            extra = !BlackOut.mc.method_1496() && BlackOut.mc.method_1562() != null && BlackOut.mc.method_1562().method_45734() != null ? BlackOut.mc.method_1562().method_45734().field_3761 : "Singleplayer";
            BlackOut.Type var10000 = BlackOut.TYPE;
            text = "Blackout | " + var10000 + " | " + BlackOut.mc.field_1724.method_5477().getString() + " | " + extra + " | " + BlackOut.mc.method_47599() + " fps";
            width = BlackOut.FONT.getWidth(text) + 4.0F;
            this.setSize(width, BlackOut.FONT.getHeight());
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(0.0F, 0.0F, width, BlackOut.FONT.getHeight() + 2.0F, 3.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.bg.get()) {
               this.background.render(this.stack, 0.0F, 0.0F, width, BlackOut.FONT.getHeight() + 2.0F, 3.0F, 3.0F);
            }

            this.textColor.render(this.stack, text, 1.0F, 2.0F, BlackOut.FONT.getHeight() / 2.0F + 1.0F, false, true);
            break;
         case GameSense:
            extra = !BlackOut.mc.method_1496() && BlackOut.mc.method_1562() != null && BlackOut.mc.method_1562().method_45734() != null ? BlackOut.mc.method_1562().method_45734().field_3761 : "Singleplayer";
            text = "| " + BlackOut.mc.field_1724.method_5477().getString() + " | " + BlackOut.mc.method_47599() + " fps | " + extra + " | " + formattedTime;
            width = BlackOut.FONT.getWidth("BlackOut" + text);
            this.setSize(width + 10.0F, BlackOut.FONT.getHeight() + 8.0F);
            RenderUtils.drawSkeetBox(this.stack, 0.0F, 0.0F, width + 10.0F, BlackOut.FONT.getHeight() + 8.0F, true);
            BlackOut.FONT.text(this.stack, "Black", 1.0F, 4.0F, 5.0F, new Color(255, 255, 255, 255), false, false);
            BlackOut.FONT.text(this.stack, "out", 1.0F, 4.0F + BlackOut.FONT.getWidth("Black"), 5.0F, new Color(50, 125, 50, 255), false, false);
            BlackOut.FONT.text(this.stack, text, 1.0F, BlackOut.FONT.getWidth("Blackout ") + 4.0F, 5.0F, new Color(255, 255, 255, 255), false, false);
            break;
         case Sigma:
            this.setSize(BlackOut.FONT.getWidth("Sigma") * 4.0F, BlackOut.FONT.getHeight() * 5.0F);
            switch((Watermark.SigmaMode)this.sigmaMode.get()) {
            case SigmaJello:
               BlackOut.FONT.text(this.stack, "Sigma", 4.0F, 0.0F, 0.0F, new Color(255, 255, 255, 150), false, false);
               BlackOut.FONT.text(this.stack, "Jello", 1.2F, 0.0F, 31.0F, new Color(255, 255, 255, 150), false, false);
               break label80;
            case SugmaYellow:
               BlackOut.FONT.text(this.stack, "Sugma", 4.0F, 0.0F, 0.0F, new Color(255, 255, 0, 150), false, false);
               BlackOut.FONT.text(this.stack, "Yellow", 1.2F, 0.0F, 32.0F, new Color(255, 255, 0, 150), false, false);
            default:
               break label80;
            }
         case Remix:
            this.setSize(BlackOut.FONT.getWidth("Remix v1.6.6"), BlackOut.FONT.getHeight());
            BlackOut.FONT.text(this.stack, "R", 1.0F, 1.0F, 2.0F, new Color(38, 183, 110, 255), false, false);
            BlackOut.FONT.text(this.stack, "emix v1.6.6", 1.0F, 1.0F + BlackOut.FONT.getWidth("R"), 2.0F, Color.GRAY, false, false);
            break;
         case Exhibition:
            extra = this.extraText.get() != null && !((String)this.extraText.get()).isEmpty() ? " - " + (String)this.extraText.get() : "";
            this.setSize(BlackOut.FONT.getWidth("Exhibition" + extra), BlackOut.FONT.getHeight());
            this.textColor.render(this.stack, "E", 1.0F, 0.0F, 0.0F, false, false);
            BlackOut.FONT.text(this.stack, "xhibition" + extra, 1.0F, BlackOut.FONT.getWidth("E"), 0.0F, ((BlackOutColor)this.secondaryColor.get()).getColor(), false, false);
            break;
         case KassuK:
            width = BlackOut.FONT.getWidth("BlackOut") + 8.0F;
            height = BlackOut.FONT.getHeight() - 2.0F;
            this.setSize(width, BlackOut.FONT.getHeight());
            this.stack.method_46416(2.0F, 0.0F, 0.0F);
            Color color = this.getWave(1, this.textColor.getTextColor().getColor(), this.textColor.getWaveColor().getColor());
            Color color2 = this.getWave(2, this.textColor.getTextColor().getColor(), this.textColor.getWaveColor().getColor());
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(-3.0F, 1.0F, width, height, 3.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.bg.get()) {
               this.background.render(this.stack, -3.0F, 1.0F, width, height, 3.0F, 3.0F);
            }

            RenderUtils.rounded(this.stack, -2.0F, 1.0F, 0.1F, height, 0.5F, 0.5F, color.getRGB(), color.getRGB());
            RenderUtils.rounded(this.stack, width - 4.1F, 1.0F, 0.1F, height, 0.5F, 0.5F, color2.getRGB(), color2.getRGB());
            this.textColor.render(this.stack, "BlackOut", 1.0F, 1.0F, BlackOut.FONT.getHeight() / 2.0F + 0.5F, false, true);
         }

         this.stack.method_22909();
      }
   }

   private Color getWave(int i, Color color, Color color2) {
      return ColorUtils.getWave(color, color2, 1.0D, 2.0D, i);
   }

   public static enum Mode {
      Simple,
      GameSense,
      Virtue,
      Clean,
      Sigma,
      Remix,
      KassuK,
      Exhibition;

      // $FF: synthetic method
      private static Watermark.Mode[] $values() {
         return new Watermark.Mode[]{Simple, GameSense, Virtue, Clean, Sigma, Remix, KassuK, Exhibition};
      }
   }

   public static enum SigmaMode {
      SigmaJello,
      SugmaYellow;

      // $FF: synthetic method
      private static Watermark.SigmaMode[] $values() {
         return new Watermark.SigmaMode[]{SigmaJello, SugmaYellow};
      }
   }
}
