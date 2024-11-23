package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;

public class StringSetting extends Setting<String> {
   private final TextField textField = new TextField();
   private final int id = SelectedComponent.nextId();

   public StringSetting(String name, String val, String description, SingleOut<Boolean> visible) {
      super(name, val, description, visible);
   }

   public float render() {
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      this.textField.render(this.stack, 1.8F, this.mx, this.my, (float)(this.x + 15), (float)(this.y + 35), this.width - 30.0F, 0.0F, 12.0F, 6.0F, Color.WHITE, GuiColorUtils.bg2);
      this.setValue(this.textField.getContent());
      this.textField.setActive(SelectedComponent.is(this.id));
      return this.getHeight();
   }

   public boolean onMouse(int button, boolean pressed) {
      if (button == 0 && pressed && this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && this.my > (double)this.y && this.my < (double)((float)this.y + this.getHeight())) {
         SelectedComponent.setId(this.id);
         this.textField.click(button, true);
         return true;
      } else {
         return false;
      }
   }

   public void onKey(int key, boolean pressed) {
      if (SelectedComponent.is(this.id)) {
         if (key == 257) {
            SelectedComponent.reset();
         } else {
            this.textField.type(key, pressed);
            Managers.CONFIG.saveAll();
         }
      }

   }

   public float getHeight() {
      return 55.0F;
   }

   public void write(JsonObject object) {
      object.addProperty(this.name, (String)this.get());
   }

   public void set(JsonElement element) {
      this.setValue(element.getAsString());
      this.textField.setContent((String)this.get());
   }
}
