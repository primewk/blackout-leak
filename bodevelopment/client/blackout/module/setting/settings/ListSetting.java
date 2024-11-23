package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.screens.ListScreen;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.GuiColorUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListSetting<T> extends Setting<List<T>> {
   public final List<T> list;
   private final EpicInterface<T, String> getName;

   @SafeVarargs
   public ListSetting(String name, List<T> list, EpicInterface<T, String> getName, String description, SingleOut<Boolean> visible, T... val) {
      super(name, new ArrayList(Arrays.stream(val).toList()), description, visible);
      this.list = list;
      this.getName = getName;
   }

   public float render() {
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      String text = String.valueOf(((List)this.get()).size());
      BlackOut.FONT.text(this.stack, text, 2.0F, (float)this.x + this.width - BlackOut.FONT.getWidth(text) * 2.0F, (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      return this.getHeight();
   }

   public boolean onMouse(int key, boolean pressed) {
      if (key == 0 && pressed && this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && this.my > (double)this.y && this.my < (double)((float)this.y + this.getHeight())) {
         Managers.CLICK_GUI.openScreen(new ListScreen(this, this.getName));
         return true;
      } else {
         return false;
      }
   }

   public float getHeight() {
      return 26.0F;
   }

   public void write(JsonObject jsonObject) {
      jsonObject.addProperty(this.name, this.join());
   }

   protected String join() {
      StringBuilder builder = new StringBuilder();
      boolean b = false;

      Object item;
      for(Iterator var3 = ((List)this.get()).iterator(); var3.hasNext(); builder.append((String)this.getName.get(item))) {
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
      Map<String, T> names = new HashMap();
      this.list.forEach((item) -> {
         names.put((String)this.getName.get(item), item);
      });
      String[] var3 = element.getAsString().split(",");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String string = var3[var5];
         if (names.containsKey(string)) {
            ((List)this.get()).add(names.get(string));
         }
      }

   }
}
