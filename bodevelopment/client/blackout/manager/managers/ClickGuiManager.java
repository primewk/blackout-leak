package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.gui.clickgui.ClickGui;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.manager.Managers;

public class ClickGuiManager extends Manager {
   public final ClickGui CLICK_GUI = new ClickGui();

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onKey(KeyEvent event) {
      if (event.key == 344 && event.pressed && BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1755 == null || BlackOut.mc.field_1755 instanceof ClickGui) {
            this.toggle();
         }

      }
   }

   public void openScreen(ClickGuiScreen screen) {
      if (BlackOut.mc.field_1755 instanceof ClickGui) {
         this.CLICK_GUI.setScreen(screen);
      } else {
         Managers.HUD.HUD_EDITOR.setScreen(screen);
      }

   }

   private void toggle() {
      if (this.CLICK_GUI.isOpen()) {
         if (System.currentTimeMillis() - this.CLICK_GUI.toggleTime < 500L) {
            return;
         }
      } else if (System.currentTimeMillis() - this.CLICK_GUI.toggleTime < 250L) {
         return;
      }

      this.CLICK_GUI.toggleTime = System.currentTimeMillis();
      if (BlackOut.mc.field_1755 instanceof ClickGui) {
         this.CLICK_GUI.onClick(MouseButtonEvent.get(0, false));
         this.CLICK_GUI.setOpen(false);
      } else {
         this.CLICK_GUI.setOpen(true);
         BlackOut.mc.method_1507(this.CLICK_GUI);
      }

   }
}
