package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

public class Clock extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Clock.Mode> mode;
   private final TextColorMultiSetting textColor;
   private final Setting<Boolean> bg;
   private final BackgroundMultiSetting background;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> rounded;
   private float textWidth;

   public Clock() {
      super("Clock", "Shows you the current time");
      this.mode = this.sgGeneral.e("Time format", Clock.Mode.Normal, "What format to use to show the time");
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10002 = this.bg;
      Objects.requireNonNull(var10002);
      this.background = BackgroundMultiSetting.of(var10001, var10002::get, (String)null);
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.rounded = this.sgGeneral.b("Rounded", true, "Renders a background", () -> {
         return (Boolean)this.bg.get() || (Boolean)this.blur.get();
      });
      this.textWidth = 0.0F;
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         String var10000;
         switch((Clock.Mode)this.mode.get()) {
         case Normal:
            var10000 = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            break;
         case American:
            var10000 = (new SimpleDateFormat("hh:mm a")).format(new Date());
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         String time = var10000;
         this.textWidth = BlackOut.FONT.getWidth(time);
         this.setSize(this.textWidth, BlackOut.FONT.getHeight());
         this.stack.method_22903();
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(0.0F, 0.0F, this.textWidth, BlackOut.FONT.getHeight(), (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get()) {
            this.background.render(this.stack, 0.0F, 0.0F, this.textWidth, BlackOut.FONT.getHeight(), (Boolean)this.rounded.get() ? 3.0F : 0.0F, 3.0F);
         }

         this.textColor.render(this.stack, time, 1.0F, 0.0F, 0.0F, false, false);
         this.stack.method_22909();
      }
   }

   public static enum Mode {
      Normal,
      American;

      // $FF: synthetic method
      private static Clock.Mode[] $values() {
         return new Clock.Mode[]{Normal, American};
      }
   }
}
