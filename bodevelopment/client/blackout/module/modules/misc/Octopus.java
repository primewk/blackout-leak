package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.mixin.IMinecraftClient;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.SettingUtils;

public class Octopus extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<KeyBind> slot1;
   private final Setting<KeyBind> slot2;
   private final Setting<KeyBind> slot3;
   private final Setting<KeyBind> slot4;
   private final Setting<KeyBind> slot5;
   private final Setting<KeyBind> slot6;
   private final Setting<KeyBind> slot7;
   private final Setting<KeyBind> slot8;
   private final Setting<KeyBind> slot9;
   private int toUse;

   public Octopus() {
      super("Octopus", "Silently uses items from slots.", SubCategory.MISC, true);
      this.slot1 = this.sgGeneral.k("Slot 1", ".");
      this.slot2 = this.sgGeneral.k("Slot 2", ".");
      this.slot3 = this.sgGeneral.k("Slot 3", ".");
      this.slot4 = this.sgGeneral.k("Slot 4", ".");
      this.slot5 = this.sgGeneral.k("Slot 5", ".");
      this.slot6 = this.sgGeneral.k("Slot 6", ".");
      this.slot7 = this.sgGeneral.k("Slot 7", ".");
      this.slot8 = this.sgGeneral.k("Slot 8", ".");
      this.slot9 = this.sgGeneral.k("Slot 9", ".");
      this.toUse = -1;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (this.toUse >= 0) {
         this.use(this.toUse);
      }

      this.toUse = -1;
   }

   @Event
   public void onKey(KeyEvent event) {
      if (event.pressed) {
         int pressed = this.pressedSlot(event.key, false);
         if (pressed >= 0) {
            this.handleClick(pressed);
         }

      }
   }

   @Event
   public void onMouse(MouseButtonEvent event) {
      if (event.pressed) {
         int pressed = this.pressedSlot(event.button, true);
         if (pressed >= 0) {
            this.handleClick(pressed);
         }

      }
   }

   private void handleClick(int slot) {
      if (SettingUtils.grimPackets()) {
         this.toUse = slot;
      } else {
         this.use(slot);
      }
   }

   private void use(int slot) {
      boolean switched = false;
      if (BlackOut.mc.field_1761.field_3721 != slot) {
         InvUtils.swap(slot);
         switched = true;
      }

      ((IMinecraftClient)BlackOut.mc).blackout_Client$useItem();
      if (switched) {
         InvUtils.swapBack();
      }

   }

   private int pressedSlot(int key, boolean mouse) {
      for(int i = 0; i < 9; ++i) {
         KeyBind b = (KeyBind)this.getSetting(i).get();
         if (!mouse && b.isKey(key)) {
            return i;
         }

         if (mouse && b.isMouse(key)) {
            return i;
         }
      }

      return -1;
   }

   private Setting<KeyBind> getSetting(int slot) {
      Setting var10000;
      switch(slot) {
      case 0:
         var10000 = this.slot1;
         break;
      case 1:
         var10000 = this.slot2;
         break;
      case 2:
         var10000 = this.slot3;
         break;
      case 3:
         var10000 = this.slot4;
         break;
      case 4:
         var10000 = this.slot5;
         break;
      case 5:
         var10000 = this.slot6;
         break;
      case 6:
         var10000 = this.slot7;
         break;
      case 7:
         var10000 = this.slot8;
         break;
      case 8:
         var10000 = this.slot9;
         break;
      default:
         throw new IllegalStateException("Unexpected value: " + slot);
      }

      return var10000;
   }
}
