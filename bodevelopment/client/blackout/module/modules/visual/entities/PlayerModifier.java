package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.RotationUtils;
import java.util.Objects;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class PlayerModifier extends Module {
   private static PlayerModifier INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> setLeaning;
   private final Setting<Double> leaning;
   private final Setting<Boolean> moveLeaning;
   public final Setting<Boolean> forceSneak;
   public final Setting<Boolean> noAnimations;
   public final Setting<Boolean> noSwing;
   private final TimerMap<class_1657, Float> leaningMap;

   public PlayerModifier() {
      super("Player Modifier", "Modifies players.", SubCategory.ENTITIES, false);
      this.setLeaning = this.sgGeneral.b("Set Leaning", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.setLeaning;
      Objects.requireNonNull(var10008);
      this.leaning = var10001.d("Leaning", 0.0D, 0.0D, 1.0D, 0.01D, ".", var10008::get);
      var10001 = this.sgGeneral;
      Setting var10005 = this.setLeaning;
      Objects.requireNonNull(var10005);
      this.moveLeaning = var10001.b("Move Leaning", true, ".", var10005::get);
      this.forceSneak = this.sgGeneral.b("Force Sneak", true, ".");
      this.noAnimations = this.sgGeneral.b("No Animations", true, ".");
      this.noSwing = this.sgGeneral.b("No Swing", true, ".");
      this.leaningMap = new TimerMap(true);
      INSTANCE = this;
   }

   public static PlayerModifier getInstance() {
      return INSTANCE;
   }

   public float getLeaning(class_1657 player) {
      float current = this.getLeaningValue(player);
      if (!this.leaningMap.containsKey(player)) {
         this.leaningMap.add(player, current, 1.0D);
         return current;
      } else {
         double prev = (double)(Float)this.leaningMap.get(player);
         float newLeaning = class_3532.method_15363((float)class_3532.method_16436((double)class_3532.method_15363(BlackOut.mc.method_1534() / 10.0F, 0.0F, 1.0F), prev, (double)current), 0.0F, 1.0F);
         this.leaningMap.add(player, newLeaning, 1.0D);
         return newLeaning;
      }
   }

   private float getLeaningValue(class_1657 player) {
      if (!(Boolean)this.moveLeaning.get()) {
         return ((Double)this.leaning.get()).floatValue();
      } else {
         double yaw = RotationUtils.getYaw(class_243.field_1353, player.method_18798(), 0.0D);
         double yawAngle = Math.abs(RotationUtils.yawAngle(yaw, (double)player.method_36454()));
         float yawRatio = (float)class_3532.method_15350(class_3532.method_15370(yawAngle, 90.0D, 0.0D), 0.0D, 1.0D);
         float velRatio = (float)class_3532.method_15350(player.method_18798().method_37267() / 0.25D, 0.0D, 1.0D);
         return ((Double)this.leaning.get()).floatValue() * yawRatio * velRatio;
      }
   }
}
