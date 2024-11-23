package bodevelopment.client.blackout.module.setting.multisettings;

import bodevelopment.client.blackout.enums.BackgroundType;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_4587;

public class BackgroundMultiSetting {
   private final Setting<BackgroundType> mode;
   private final Setting<BlackOutColor> roundedColor;
   private final Setting<BlackOutColor> shadowColor;
   private final Setting<BlackOutColor> secondaryColor;
   private final Setting<Boolean> shadow;
   private final Setting<Double> speed;

   public BlackOutColor getRoundedColor() {
      return (BlackOutColor)this.roundedColor.get();
   }

   public BlackOutColor getSecondaryColor() {
      return (BlackOutColor)this.secondaryColor.get();
   }

   public Boolean isAnimated() {
      return this.mode.get() == BackgroundType.Animated;
   }

   public Boolean isStatic() {
      return this.mode.get() == BackgroundType.Static;
   }

   private BackgroundMultiSetting(SettingGroup sg, BackgroundType dm, BlackOutColor rc, BlackOutColor sc, BlackOutColor shdw, SingleOut<Boolean> visible, String name) {
      String text = name == null ? "Background" : name;
      this.mode = sg.e(text + " Type", dm, ".");
      this.roundedColor = sg.c(text + " Color", rc, ".", () -> {
         return (this.mode.get() == BackgroundType.Static || this.mode.get() == BackgroundType.Animated) && (Boolean)visible.get();
      });
      this.secondaryColor = sg.c(text + " Secondary Color", sc, ".", () -> {
         return this.mode.get() == BackgroundType.Animated && (Boolean)visible.get();
      });
      this.shadow = sg.b(text + " Shadow", true, "Do we use a shadow effect");
      this.shadowColor = sg.c(text + " Shadow Color", shdw, ".", () -> {
         return this.mode.get() == BackgroundType.Static && (Boolean)visible.get() && (Boolean)this.shadow.get();
      });
      this.speed = sg.d(text + " Speed", 1.0D, 0.1D, 10.0D, 0.1D, ".", () -> {
         return this.mode.get() == BackgroundType.Animated && (Boolean)visible.get();
      });
   }

   public void render(class_4587 stack, float x, float y, float w, float h, float r, float sr) {
      ThemeSettings themeSettings = ThemeSettings.getInstance();
      switch((BackgroundType)this.mode.get()) {
      case Static:
         RenderUtils.rounded(stack, x, y, w, h, r, (Boolean)this.shadow.get() ? sr : 0.0F, ((BlackOutColor)this.roundedColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
         break;
      case Animated:
         RenderUtils.tenaRounded(stack, x, y, w, h, r, (Boolean)this.shadow.get() ? sr : 0.0F, ((BlackOutColor)this.roundedColor.get()).getRGB(), ((BlackOutColor)this.secondaryColor.get()).getRGB(), ((Double)this.speed.get()).floatValue());
      }

   }

   public static BackgroundMultiSetting of(SettingGroup sg, String name) {
      return of(sg, () -> {
         return true;
      }, name);
   }

   public static BackgroundMultiSetting of(SettingGroup sg, SingleOut<Boolean> visible, String name) {
      return of(sg, BackgroundType.Static, visible, name);
   }

   public static BackgroundMultiSetting of(SettingGroup sg, BackgroundType dm, SingleOut<Boolean> visible, String name) {
      return of(sg, dm, new BlackOutColor(0, 0, 0, 50), new BlackOutColor(25, 25, 25, 50), new BlackOutColor(0, 0, 0, 100), visible, name);
   }

   public static BackgroundMultiSetting of(SettingGroup sg, BackgroundType dm, BlackOutColor rc, BlackOutColor sc, BlackOutColor shdw, SingleOut<Boolean> visible, String name) {
      return new BackgroundMultiSetting(sg, dm, rc, sc, shdw, visible, name);
   }
}
