package bodevelopment.client.blackout.module.setting;

import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.module.setting.settings.BoolSetting;
import bodevelopment.client.blackout.module.setting.settings.ColorSetting;
import bodevelopment.client.blackout.module.setting.settings.DoubleSetting;
import bodevelopment.client.blackout.module.setting.settings.EnumSetting;
import bodevelopment.client.blackout.module.setting.settings.IntSetting;
import bodevelopment.client.blackout.module.setting.settings.KeyBindSetting;
import bodevelopment.client.blackout.module.setting.settings.ListSetting;
import bodevelopment.client.blackout.module.setting.settings.RegistrySetting;
import bodevelopment.client.blackout.module.setting.settings.StringSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import java.util.List;
import net.minecraft.class_1299;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2378;
import net.minecraft.class_7923;

public class Settings {
   public static Setting<Boolean> b(String name, boolean value, String description, SingleOut<Boolean> visible) {
      return new BoolSetting(name, value, description, visible);
   }

   public static Setting<Double> d(String name, double value, double min, double max, double step, String description, SingleOut<Boolean> visible) {
      return new DoubleSetting(name, value, min, max, step, description, visible);
   }

   public static <T extends Enum<?>> Setting<T> e(String name, T value, String description, SingleOut<Boolean> visible) {
      return new EnumSetting(name, value, description, visible);
   }

   public static Setting<Integer> i(String name, int value, int min, int max, int step, String description, SingleOut<Boolean> visible) {
      return new IntSetting(name, value, min, max, step, description, visible);
   }

   public static Setting<KeyBind> k(String name, String description, SingleOut<Boolean> visible) {
      return new KeyBindSetting(name, description, visible);
   }

   public static Setting<String> s(String name, String value, String description, SingleOut<Boolean> visible) {
      return new StringSetting(name, value, description, visible);
   }

   public static Setting<BlackOutColor> c(String name, BlackOutColor value, String description, SingleOut<Boolean> visible) {
      return new ColorSetting(name, value, description, visible);
   }

   public static Setting<List<class_2248>> bl(String name, String description, SingleOut<Boolean> visible, class_2248... value) {
      return new RegistrySetting(name, class_7923.field_41175, (block) -> {
         return block.method_9518().getString();
      }, description, visible, value);
   }

   public static Setting<List<class_1792>> il(String name, String description, SingleOut<Boolean> visible, class_1792... value) {
      return r(name, description, visible, class_7923.field_41178, (item) -> {
         return item.method_7848().getString();
      }, value);
   }

   public static Setting<List<class_1299<?>>> el(String name, String description, SingleOut<Boolean> visible, class_1299<?>... value) {
      return r(name, description, visible, class_7923.field_41177, (entity) -> {
         return entity.method_5897().getString();
      }, value);
   }

   public static <T> Setting<List<T>> r(String name, String description, SingleOut<Boolean> visible, class_2378<T> registry, EpicInterface<T, String> getName, T... value) {
      return new RegistrySetting(name, registry, getName, description, visible, value);
   }

   public static <T> Setting<List<T>> l(String name, String description, SingleOut<Boolean> visible, List<T> list, EpicInterface<T, String> getName, T... value) {
      return new ListSetting(name, list, getName, description, visible, value);
   }
}
