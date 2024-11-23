package bodevelopment.client.blackout.gui.clickgui.screens;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.settings.ListSetting;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;

public class ListScreen<T> extends ClickGuiScreen {
   private double progress = 0.0D;
   private final TextField textField = new TextField();
   private final ListSetting<T> setting;
   private final EpicInterface<T, String> getName;
   private int left = 0;
   private int right = 0;

   public ListScreen(ListSetting<T> setting, EpicInterface<T, String> getName) {
      super(setting.name, 700.0F, 700.0F, true);
      this.setting = setting;
      this.getName = getName;
   }

   protected float getLength() {
      this.right = 0;
      this.left = 0;
      this.setting.list.stream().filter((item) -> {
         return this.validSearch((String)this.getName.get(item));
      }).forEach((item) -> {
         if (((List)this.setting.get()).contains(item)) {
            ++this.right;
         } else {
            ++this.left;
         }

      });
      return (float)(Math.max(this.left, this.right) * 25);
   }

   public void render() {
      float l = 10.0F - this.scroll.get();
      Iterator var2 = this.setting.list.iterator();

      Object item;
      String name;
      double d;
      double o;
      while(var2.hasNext()) {
         item = var2.next();
         if (!(l > this.height - 20.0F) && !((List)this.setting.get()).contains(item)) {
            name = (String)this.getName.get(item);
            if (this.validSearch(name)) {
               d = 1.0D;
               if (this.insideBounds() && this.mx < (double)(this.width / 2.0F)) {
                  d = Math.min(1.0D, Math.abs(this.my - (double)l - (double)(BlackOut.FONT.getHeight() / 2.0F)) / 20.0D);
               }

               o = 10.0D - d * 10.0D;
               this.text(name, 1.75F, (float)o, l, false, false, ColorUtils.lerpColor(d, Color.WHITE, Color.GRAY));
               l += 25.0F;
            }
         }
      }

      l = 10.0F - this.scroll.get();
      var2 = this.setting.list.iterator();

      while(var2.hasNext()) {
         item = var2.next();
         if (!(l > this.height - 20.0F) && ((List)this.setting.get()).contains(item)) {
            name = (String)this.getName.get(item);
            if (this.validSearch(name)) {
               d = 1.0D;
               if (this.insideBounds() && this.mx > (double)(this.width / 2.0F)) {
                  d = Math.min(1.0D, Math.abs(this.my - (double)l - (double)(BlackOut.FONT.getHeight() / 2.0F)) / 20.0D);
               }

               o = 10.0D - d * 10.0D;
               this.text(name, 1.75F, (float)((double)(this.width - BlackOut.FONT.getWidth(name) * 1.75F) - o), l, false, false, ColorUtils.lerpColor(d, Color.WHITE, Color.GRAY));
               l += 25.0F;
            }
         }
      }

      if (!this.textField.isEmpty()) {
         this.progress = Math.min(this.progress + (double)(this.frameTime * 5.0F), 1.0D);
      } else {
         this.progress = Math.max(this.progress - (double)(this.frameTime * 5.0F), 0.0D);
      }

      Color color = ColorUtils.withAlpha(GuiColorUtils.bg1, (int)Math.floor(this.progress * 255.0D));
      Color textColor = ColorUtils.withAlpha(GuiColorUtils.category, (int)Math.floor(this.progress * 255.0D));
      this.textField.setActive(true);
      this.textField.render(this.stack, 2.0F, this.mx, this.my, this.width * 0.4F, 20.0F, this.width * 0.5F, 0.0F, 12.0F, 6.0F, textColor, color);
   }

   public void onMouse(int button, boolean state) {
      if (!this.textField.click(button, state)) {
         if (state) {
            T item = this.getItem();
            if (item != null) {
               List<T> list = (List)this.setting.get();
               if (list.contains(item)) {
                  list.remove(item);
               } else {
                  list.add(item);
               }

               Managers.CONFIG.saveAll();
            }

         }
      }
   }

   private T getItem() {
      int index = (int)Math.round((this.my + (double)this.scroll.get() + 10.0D) / 25.0D) - 1;
      int i = 0;
      Iterator var3 = this.setting.list.iterator();

      while(true) {
         Object item;
         do {
            do {
               if (!var3.hasNext()) {
                  return null;
               }

               item = var3.next();
            } while(this.mx < 500.0D && ((List)this.setting.get()).contains(item));
         } while(this.mx > 500.0D && !((List)this.setting.get()).contains(item));

         if (this.validSearch((String)this.getName.get(item))) {
            if (i == index) {
               return item;
            }

            ++i;
         }
      }
   }

   private boolean validSearch(String item) {
      return item.toLowerCase().contains(this.textField.getContent().toLowerCase());
   }

   public void onKey(int key, boolean state) {
      this.textField.type(key, state);
   }
}
