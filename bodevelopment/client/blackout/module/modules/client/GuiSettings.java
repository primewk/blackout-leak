package bodevelopment.client.blackout.module.modules.client;

import bodevelopment.client.blackout.enums.TextColorMode;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import java.util.Objects;

public class GuiSettings extends SettingsModule {
   private static GuiSettings INSTANCE;
   private final SettingGroup sgStyle = this.addGroup("Style");
   private final SettingGroup sgOpen = this.addGroup("Open");
   private final SettingGroup sgClosed = this.addGroup("Closed");
   public final TextColorMultiSetting textColor;
   public final Setting<Boolean> selectorBar;
   public final Setting<Integer> selectorGlow;
   public final Setting<BlackOutColor> selectorColor;
   public final Setting<Double> fontScale;
   public final Setting<GuiSettings.SettingGroupMode> settingGroup;
   public final Setting<Double> logoAlpha;
   public final Setting<Double> logoScale;
   public final Setting<Integer> blur;
   public final Setting<Boolean> centerX;
   public final Setting<Double> moduleX;
   public final Setting<Double> moduleY;
   public final Setting<Double> moduleScale;
   public final Setting<Double> moduleHeight;
   public final Setting<Boolean> centerXClosed;
   public final Setting<Double> moduleXClosed;
   public final Setting<Double> moduleYClosed;
   public final Setting<Double> moduleScaleClosed;
   public final Setting<Double> moduleHeightClosed;

   public GuiSettings() {
      super("GUI", true, true);
      this.textColor = TextColorMultiSetting.of(this.sgStyle, TextColorMode.Wave, () -> {
         return true;
      }, "Text");
      this.selectorBar = this.sgStyle.b("Selector Bar", false, "");
      SettingGroup var10001 = this.sgStyle;
      Setting var10008 = this.selectorBar;
      Objects.requireNonNull(var10008);
      this.selectorGlow = var10001.i("Selector Glow", 0, 0, 5, 1, ".", var10008::get);
      this.selectorColor = this.sgStyle.c("Selector Color", new BlackOutColor(40, 40, 40, 255), "Color for the selector background");
      this.fontScale = this.sgStyle.d("Font Scale", 1.0D, 0.35D, 2.0D, 0.01D, ".");
      this.settingGroup = this.sgStyle.e("Setting Group", GuiSettings.SettingGroupMode.Shadow, "");
      this.logoAlpha = this.sgStyle.d("Logo Alpha", 0.15D, 0.0D, 1.0D, 0.01D, "");
      this.logoScale = this.sgStyle.d("Logo Scale", 1.0D, 0.9D, 1.3D, 0.01D, "");
      this.blur = this.sgStyle.i("Blur", 0, 0, 20, 1, "");
      this.centerX = this.sgOpen.b("Module Center X", true, "");
      this.moduleX = this.sgOpen.d("Module X", 0.5D, 0.0D, 1.0D, 0.01D, "", () -> {
         return !(Boolean)this.centerX.get();
      });
      this.moduleY = this.sgOpen.d("Module Y", 0.5D, 0.0D, 1.0D, 0.01D, "");
      this.moduleScale = this.sgOpen.d("Module Scale", 2.0D, 0.1D, 4.0D, 0.1D, "");
      this.moduleHeight = this.sgOpen.d("Module height", 40.0D, 25.0D, 100.0D, 1.0D, "");
      this.centerXClosed = this.sgClosed.b("Closed Module Center X", true, "");
      this.moduleXClosed = this.sgClosed.d("Closed Module X", 0.5D, 0.0D, 1.0D, 0.01D, "", () -> {
         return !(Boolean)this.centerXClosed.get();
      });
      this.moduleYClosed = this.sgClosed.d("Closed Module Y", 0.5D, 0.0D, 1.0D, 0.01D, "");
      this.moduleScaleClosed = this.sgClosed.d("Closed Module Scale", 2.0D, 0.1D, 4.0D, 0.1D, "");
      this.moduleHeightClosed = this.sgClosed.d("Closed Module height", 40.0D, 25.0D, 100.0D, 1.0D, "");
      INSTANCE = this;
   }

   public static GuiSettings getInstance() {
      return INSTANCE;
   }

   public static enum SettingGroupMode {
      Line(40.0F),
      Shadow(45.0F),
      Quad(50.0F),
      None(40.0F);

      private final float height;

      private SettingGroupMode(float height) {
         this.height = height;
      }

      public float getHeight() {
         return this.height;
      }

      // $FF: synthetic method
      private static GuiSettings.SettingGroupMode[] $values() {
         return new GuiSettings.SettingGroupMode[]{Line, Shadow, Quad, None};
      }
   }
}
