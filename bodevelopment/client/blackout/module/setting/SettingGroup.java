package bodevelopment.client.blackout.module.setting;

import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1299;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2378;

public class SettingGroup {
   public final String name;
   public final List<Setting<?>> settings = new ArrayList();

   public SettingGroup(String name) {
      this.name = name;
   }

   public Setting<Boolean> b(String name, boolean value, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.b(name, value, description, visible));
   }

   public Setting<Double> d(String name, double value, double min, double max, double step, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.d(name, value, min, max, step, description, visible));
   }

   public <T extends Enum<?>> Setting<T> e(String name, T value, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.e(name, value, description, visible));
   }

   public Setting<Integer> i(String name, int value, int min, int max, int step, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.i(name, value, min, max, step, description, visible));
   }

   public Setting<KeyBind> k(String name, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.k(name, description, visible));
   }

   public Setting<String> s(String name, String value, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.s(name, value, description, visible));
   }

   public Setting<BlackOutColor> c(String name, BlackOutColor value, String description, SingleOut<Boolean> visible) {
      return this.a(Settings.c(name, value, description, visible));
   }

   public Setting<List<class_2248>> bl(String name, String description, SingleOut<Boolean> visible, class_2248... value) {
      return this.a(Settings.bl(name, description, visible, value));
   }

   public Setting<List<class_1792>> il(String name, String description, SingleOut<Boolean> visible, class_1792... value) {
      return this.a(Settings.il(name, description, visible, value));
   }

   public Setting<List<class_1299<?>>> el(String name, String description, SingleOut<Boolean> visible, class_1299<?>... value) {
      return this.a(Settings.el(name, description, visible, value));
   }

   public <T> Setting<List<T>> r(String name, String description, SingleOut<Boolean> visible, class_2378<T> registry, EpicInterface<T, String> getName, T... value) {
      return this.a(Settings.r(name, description, visible, registry, getName, value));
   }

   public <T> Setting<List<T>> l(String name, String description, SingleOut<Boolean> visible, List<T> list, EpicInterface<T, String> getName, T... value) {
      return this.a(Settings.l(name, description, visible, list, getName, value));
   }

   public Setting<Boolean> b(String name, boolean value, String description) {
      return this.a(Settings.b(name, value, description, (SingleOut)null));
   }

   public Setting<Double> d(String name, double value, double min, double max, double step, String description) {
      return this.a(Settings.d(name, value, min, max, step, description, (SingleOut)null));
   }

   public <T extends Enum<?>> Setting<T> e(String name, T value, String description) {
      return this.a(Settings.e(name, value, description, (SingleOut)null));
   }

   public Setting<Integer> i(String name, int value, int min, int max, int step, String description) {
      return this.a(Settings.i(name, value, min, max, step, description, (SingleOut)null));
   }

   public Setting<KeyBind> k(String name, String description) {
      return this.a(Settings.k(name, description, (SingleOut)null));
   }

   public Setting<String> s(String name, String value, String description) {
      return this.a(Settings.s(name, value, description, (SingleOut)null));
   }

   public Setting<BlackOutColor> c(String name, BlackOutColor value, String description) {
      return this.a(Settings.c(name, value, description, (SingleOut)null));
   }

   public Setting<List<class_2248>> bl(String name, String description, class_2248... value) {
      return this.a(Settings.bl(name, description, (SingleOut)null, value));
   }

   public Setting<List<class_1792>> il(String name, String description, class_1792... value) {
      return this.a(Settings.il(name, description, (SingleOut)null, value));
   }

   public Setting<List<class_1299<?>>> el(String name, String description, class_1299<?>... value) {
      return this.a(Settings.el(name, description, (SingleOut)null, value));
   }

   public <T> Setting<List<T>> r(String name, String description, class_2378<T> registry, EpicInterface<T, String> getName, T... value) {
      return this.a(Settings.r(name, description, (SingleOut)null, registry, getName, value));
   }

   public <T> Setting<List<T>> l(String name, String description, List<T> list, EpicInterface<T, String> getName, T... value) {
      return this.a(Settings.l(name, description, (SingleOut)null, list, getName, value));
   }

   private <T> Setting<T> a(Setting<T> setting) {
      this.settings.add(setting);
      return setting;
   }
}
