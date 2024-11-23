package bodevelopment.client.blackout.module.setting.multisettings;

import bodevelopment.client.blackout.enums.RoundedColorMode;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_4587;

public class RoundedColorMultiSetting {
   private final Setting<RoundedColorMode> mode;
   private final Setting<BlackOutColor> roundedColor;
   private final Setting<BlackOutColor> shadowColor;
   private final Setting<BlackOutColor> waveColor;
   private final Setting<Double> saturation;
   private final Setting<Double> frequency;
   private final Setting<Double> speed;

   public BlackOutColor getRoundedColor() {
      return (BlackOutColor)this.roundedColor.get();
   }

   public BlackOutColor getWaveColor() {
      return (BlackOutColor)this.waveColor.get();
   }

   public Boolean isRainbow() {
      return this.mode.get() == RoundedColorMode.Rainbow;
   }

   public Boolean isStatic() {
      return this.mode.get() == RoundedColorMode.Static;
   }

   public Boolean isWave() {
      return this.mode.get() == RoundedColorMode.Wave;
   }

   public double saturation() {
      return (Double)this.saturation.get();
   }

   private RoundedColorMultiSetting(SettingGroup sg, RoundedColorMode dm, BlackOutColor dt, BlackOutColor dw, SingleOut<Boolean> visible, String name) {
      String text = name == null ? "Rounded" : name;
      this.mode = sg.e(text + " Color Mode", dm, ".");
      this.roundedColor = sg.c(text + " Color", dt, ".", () -> {
         return (this.mode.get() == RoundedColorMode.Static || this.mode.get() == RoundedColorMode.Wave) && (Boolean)visible.get();
      });
      this.waveColor = sg.c(text + " Wave Color", dw, ".", () -> {
         return this.mode.get() == RoundedColorMode.Wave && (Boolean)visible.get();
      });
      this.shadowColor = sg.c(text + " Shadow Color", dt, ".", () -> {
         return this.mode.get() == RoundedColorMode.Static && (Boolean)visible.get();
      });
      this.saturation = sg.d(text + " Saturation", 1.0D, 0.1D, 1.0D, 0.1D, ".", () -> {
         return this.mode.get() == RoundedColorMode.Rainbow && (Boolean)visible.get();
      });
      this.frequency = sg.d(text + " Frequency", 1.0D, 0.1D, 10.0D, 0.1D, ".", () -> {
         return (this.mode.get() == RoundedColorMode.Wave || this.mode.get() == RoundedColorMode.Rainbow) && (Boolean)visible.get();
      });
      this.speed = sg.d(text + " Speed", 1.0D, 0.1D, 10.0D, 0.1D, ".", () -> {
         return (this.mode.get() == RoundedColorMode.Wave || this.mode.get() == RoundedColorMode.Rainbow) && (Boolean)visible.get();
      });
   }

   public void render(class_4587 stack, float x, float y, float w, float h, float r, float sr) {
      ThemeSettings themeSettings = ThemeSettings.getInstance();
      switch((RoundedColorMode)this.mode.get()) {
      case Static:
         RenderUtils.rounded(stack, x, y, w, h, r, sr, ((BlackOutColor)this.roundedColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
         break;
      case Wave:
         RenderUtils.fadeRounded(stack, x, y, w, h, r, sr, ((BlackOutColor)this.roundedColor.get()).getRGB(), ((BlackOutColor)this.waveColor.get()).getRGB(), ((Double)this.frequency.get()).floatValue(), ((Double)this.speed.get()).floatValue());
         break;
      case Rainbow:
         RenderUtils.rainbowRounded(stack, x, y, w, h, r, sr, ((Double)this.saturation.get()).floatValue(), ((Double)this.frequency.get()).floatValue() / 5.0F, ((Double)this.speed.get()).floatValue() / 10.0F);
      }

   }

   public static RoundedColorMultiSetting of(SettingGroup sg, String name) {
      return of(sg, () -> {
         return true;
      }, name);
   }

   public static RoundedColorMultiSetting of(SettingGroup sg, SingleOut<Boolean> visible, String name) {
      return of(sg, RoundedColorMode.Static, visible, name);
   }

   public static RoundedColorMultiSetting of(SettingGroup sg, RoundedColorMode dm, SingleOut<Boolean> visible, String name) {
      return of(sg, dm, new BlackOutColor(255, 255, 255, 255), new BlackOutColor(125, 125, 125, 255), visible, name);
   }

   public static RoundedColorMultiSetting of(SettingGroup sg, RoundedColorMode dm, BlackOutColor rc, BlackOutColor sc, SingleOut<Boolean> visible, String name) {
      return new RoundedColorMultiSetting(sg, dm, rc, sc, visible, name);
   }
}
