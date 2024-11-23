package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.util.GuiColorUtils;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_2378;
import net.minecraft.class_2960;

public class RegistrySetting<T> extends ListSetting<T> {
   private static final Map<class_2378<?>, List<?>> registries = new HashMap();
   private final class_2378<T> registry;

   @SafeVarargs
   public RegistrySetting(String name, class_2378<T> registry, EpicInterface<T, String> getName, String description, SingleOut<Boolean> visible, T... val) {
      super(name, getList(registry), getName, description, visible, val);
      this.registry = registry;
   }

   private static <T> List<T> getList(class_2378<T> registry) {
      if (registries.containsKey(registry)) {
         return (List)registries.get(registry);
      } else {
         List<T> list = new ArrayList(registry.method_29722().stream().map(Entry::getValue).toList());
         registries.put(registry, list);
         return list;
      }
   }

   public float render() {
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      String text = String.valueOf(((List)this.get()).size());
      BlackOut.FONT.text(this.stack, text, 2.0F, (float)this.x + this.width - BlackOut.FONT.getWidth(text) * 2.0F, (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      return this.getHeight();
   }

   protected String join() {
      StringBuilder builder = new StringBuilder();
      boolean b = false;

      Object item;
      for(Iterator var3 = ((List)this.get()).iterator(); var3.hasNext(); builder.append(this.registry.method_10221(item))) {
         item = var3.next();
         if (!b) {
            b = true;
         } else {
            builder.append(",");
         }
      }

      return builder.toString();
   }

   public void set(JsonElement element) {
      ((List)this.get()).clear();
      String[] var2 = element.getAsString().split(",");
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String string = var2[var4];
         class_2960 id = new class_2960(string);
         if (this.registry.method_10250(id)) {
            ((List)this.get()).add(this.registry.method_10223(id));
         }
      }

   }

   public class_2378<T> getRegistry() {
      return this.registry;
   }
}
