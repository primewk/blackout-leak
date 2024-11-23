package bodevelopment.client.blackout.module.modules.client.settings;

import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.Objects;

public class FakeplayerSettings extends SettingsModule {
   private static FakeplayerSettings INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Double> damageMultiplier;
   public final Setting<Boolean> unlimitedTotems;
   public final Setting<Integer> totems;
   public final Setting<Integer> swapDelay;
   public final Setting<Boolean> eating;
   public final Setting<Integer> eatTime;

   public FakeplayerSettings() {
      super("Fake Player", false, false);
      this.damageMultiplier = this.sgGeneral.d("Damage Multiplier", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.unlimitedTotems = this.sgGeneral.b("Unlimited Totems", true, ".");
      this.totems = this.sgGeneral.i("Totems", 10, 0, 20, 1, ".", () -> {
         return !(Boolean)this.unlimitedTotems.get();
      });
      this.swapDelay = this.sgGeneral.i("Swap Delay", 0, 0, 20, 1, ".", () -> {
         return (Boolean)this.unlimitedTotems.get() || (Integer)this.totems.get() > 0;
      });
      this.eating = this.sgGeneral.b("Eating", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.eating;
      Objects.requireNonNull(var10008);
      this.eatTime = var10001.i("Eat Time", 10, 0, 20, 1, ".", var10008::get);
      INSTANCE = this;
   }

   public static FakeplayerSettings getInstance() {
      return INSTANCE;
   }
}
