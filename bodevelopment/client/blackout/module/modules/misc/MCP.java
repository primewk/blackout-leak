package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1802;

public class MCP extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<SwitchMode> mode;
   private final Setting<Boolean> swing;
   private final Setting<SwingHand> swingHand;

   public MCP() {
      super("MCP", "Throws a pearl", SubCategory.MISC, true);
      this.mode = this.sgGeneral.e("Mode", SwitchMode.Normal, "How to switch.");
      this.swing = this.sgGeneral.b("Swing", false, "Renders swing animation when placing throwing a peal");
      this.swingHand = this.sgGeneral.e("Swing Hand", SwingHand.RealHand, "Which hand should be swung.");
   }

   @Event
   public void mouseClick(MouseButtonEvent event) {
      if ((BlackOut.mc.field_1724 != null || BlackOut.mc.field_1687 != null) && BlackOut.mc.field_1755 == null) {
         if (event.button == 2) {
            class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8634);
            FindResult result = ((SwitchMode)this.mode.get()).find(class_1802.field_8634);
            if (result.wasFound() || hand != null) {
               if (hand != null || ((SwitchMode)this.mode.get()).swap(result.slot())) {
                  this.useItem(hand);
                  if ((Boolean)this.swing.get()) {
                     this.clientSwing((SwingHand)this.swingHand.get(), hand);
                  }

                  if (hand == null) {
                     ((SwitchMode)this.mode.get()).swapBack();
                  }

               }
            }
         }
      }
   }
}
