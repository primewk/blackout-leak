package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.keys.Key;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.keys.MouseButton;
import bodevelopment.client.blackout.keys.Pressable;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class KeyBindSetting extends Setting<KeyBind> {
   private final int id = SelectedComponent.nextId();

   public KeyBindSetting(String name, String description, SingleOut<Boolean> visible) {
      super(name, new KeyBind((Pressable)null), description, visible);
   }

   public float render() {
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)this.y + 12.5F, GuiColorUtils.getSettingText((float)this.y), false, true);
      ((KeyBind)this.get()).render(this.stack, (float)this.x + this.width - 21.0F, (float)(this.y + 12), (float)this.x + this.width, this.mx, this.my);
      return this.getHeight();
   }

   public boolean onMouse(int key, boolean pressed) {
      ((KeyBind)this.get()).onMouse(key, pressed);
      return false;
   }

   public void onKey(int key, boolean pressed) {
      ((KeyBind)this.get()).onKey(key, pressed);
   }

   public float getHeight() {
      return 30.0F;
   }

   public void write(JsonObject object) {
      if (((KeyBind)this.get()).value == null) {
         object.addProperty(this.name, "<NULL>");
      } else {
         String var10001 = this.name;
         String var10002 = ((KeyBind)this.get()).value instanceof Key ? "k+" : "m+";
         object.addProperty(var10001, var10002 + ((KeyBind)this.get()).value.key);
      }
   }

   public void set(JsonElement element) {
      String string = element.getAsString();
      if (string.equals("<NULL>")) {
         this.setValue(new KeyBind((Pressable)null));
      } else {
         String[] strings = string.split("\\+");
         String var4 = strings[0];
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 107:
            if (var4.equals("k")) {
               var5 = 0;
            }
            break;
         case 109:
            if (var4.equals("m")) {
               var5 = 1;
            }
         }

         switch(var5) {
         case 0:
            this.setValue(new KeyBind(new Key(Integer.parseInt(strings[1]))));
            break;
         case 1:
            this.setValue(new KeyBind(new MouseButton(Integer.parseInt(strings[1]))));
            break;
         default:
            this.setValue(new KeyBind((Pressable)null));
         }

      }
   }
}
