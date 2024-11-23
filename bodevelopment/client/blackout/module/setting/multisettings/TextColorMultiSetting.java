package bodevelopment.client.blackout.module.setting.multisettings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.TextColorMode;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.font.CustomFontRenderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import java.awt.Color;
import net.minecraft.class_4587;

public class TextColorMultiSetting {
   public static final long initTime = System.currentTimeMillis();
   private final Setting<TextColorMode> mode;
   private final Setting<BlackOutColor> textColor;
   private final Setting<BlackOutColor> waveColor;
   private final Setting<Double> saturation;
   private final Setting<Double> frequency;
   private final Setting<Double> speed;

   public BlackOutColor getTextColor() {
      return (BlackOutColor)this.textColor.get();
   }

   public BlackOutColor getWaveColor() {
      return (BlackOutColor)this.waveColor.get();
   }

   public Boolean isRainbow() {
      return this.mode.get() == TextColorMode.Rainbow;
   }

   public Boolean isStatic() {
      return this.mode.get() == TextColorMode.Static;
   }

   public Boolean isWave() {
      return this.mode.get() == TextColorMode.Wave;
   }

   public float saturation() {
      return ((Double)this.saturation.get()).floatValue();
   }

   public float frequency() {
      return ((Double)this.frequency.get()).floatValue();
   }

   public float speed() {
      return ((Double)this.speed.get()).floatValue();
   }

   private TextColorMultiSetting(SettingGroup sg, TextColorMode dm, BlackOutColor dt, BlackOutColor dw, SingleOut<Boolean> visible, String name) {
      String text = name == null ? "Text" : name;
      this.mode = sg.e(text + " Color Mode", dm, ".");
      this.textColor = sg.c(text + " Color", dt, ".", () -> {
         return (this.mode.get() == TextColorMode.Static || this.mode.get() == TextColorMode.Wave) && (Boolean)visible.get();
      });
      this.waveColor = sg.c(text + " Wave Color", dw, ".", () -> {
         return this.mode.get() == TextColorMode.Wave && (Boolean)visible.get();
      });
      this.saturation = sg.d(text + " Saturation", 1.0D, 0.1D, 1.0D, 0.1D, ".", () -> {
         return this.mode.get() == TextColorMode.Rainbow && (Boolean)visible.get();
      });
      this.frequency = sg.d(text + " Frequency", 1.0D, 0.1D, 10.0D, 0.1D, ".", () -> {
         return (this.mode.get() == TextColorMode.Wave || this.mode.get() == TextColorMode.Rainbow) && (Boolean)visible.get();
      });
      this.speed = sg.d(text + " Speed", 1.0D, 0.1D, 10.0D, 0.1D, ".", () -> {
         return (this.mode.get() == TextColorMode.Wave || this.mode.get() == TextColorMode.Rainbow) && (Boolean)visible.get();
      });
   }

   public void render(class_4587 stack, String text, float scale, float x, float y, boolean xCenter, boolean yCenter) {
      this.render(stack, text, scale, x, y, xCenter, yCenter, false);
   }

   public void render(class_4587 stack, String text, float scale, float x, float y, boolean xCenter, boolean yCenter, boolean bold) {
      CustomFontRenderer renderer = bold ? BlackOut.BOLD_FONT : BlackOut.FONT;
      switch((TextColorMode)this.mode.get()) {
      case Static:
         renderer.text(stack, text, scale, x, y, ((BlackOutColor)this.textColor.get()).getColor(), xCenter, yCenter);
         break;
      case Wave:
         renderer.text(stack, text, scale, x, y, Color.WHITE, xCenter, yCenter, Shaders.fontwave, new ShaderSetup((setup) -> {
            setup.set("frequency", ((Double)this.frequency.get()).floatValue() * 2.0F);
            setup.set("speed", ((Double)this.speed.get()).floatValue());
            setup.color("clr1", ((BlackOutColor)this.textColor.get()).getRGB());
            setup.color("clr2", ((BlackOutColor)this.waveColor.get()).getRGB());
            setup.time(initTime);
         }));
         break;
      case Rainbow:
         renderer.text(stack, text, scale, x, y, Color.WHITE, xCenter, yCenter, Shaders.gradientfont, new ShaderSetup((setup) -> {
            setup.set("frequency", ((Double)this.frequency.get()).floatValue() / 3.0F);
            setup.set("speed", ((Double)this.speed.get()).floatValue() / 10.0F);
            setup.set("saturation", ((Double)this.saturation.get()).floatValue());
            setup.time(initTime);
         }));
      }

   }

   public static TextColorMultiSetting of(SettingGroup sg, String name) {
      return of(sg, () -> {
         return true;
      }, name);
   }

   public static TextColorMultiSetting of(SettingGroup sg, SingleOut<Boolean> visible, String name) {
      return of(sg, TextColorMode.Static, visible, name);
   }

   public static TextColorMultiSetting of(SettingGroup sg, TextColorMode dm, SingleOut<Boolean> visible, String name) {
      return of(sg, dm, new BlackOutColor(255, 255, 255, 255), new BlackOutColor(125, 125, 125, 255), visible, name);
   }

   public static TextColorMultiSetting of(SettingGroup sg, TextColorMode dm, BlackOutColor dt, BlackOutColor dw, SingleOut<Boolean> visible, String name) {
      return new TextColorMultiSetting(sg, dm, dt, dw, visible, name);
   }
}
