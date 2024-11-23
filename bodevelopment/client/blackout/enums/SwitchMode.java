package bodevelopment.client.blackout.enums;

import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.InvUtils;
import java.util.function.Predicate;
import net.minecraft.class_1792;
import net.minecraft.class_1799;

public enum SwitchMode {
   Disabled(false, false),
   Normal(true, false),
   Silent(true, false),
   InvSwitch(true, true),
   PickSilent(true, true);

   public final boolean hotbar;
   public final boolean inventory;

   private SwitchMode(boolean h, boolean i) {
      this.hotbar = h;
      this.inventory = i;
   }

   public void swapBack() {
      switch(this) {
      case Silent:
         InvUtils.swapBack();
         break;
      case InvSwitch:
         InvUtils.invSwapBack();
         break;
      case PickSilent:
         InvUtils.pickSwapBack();
      }

   }

   public boolean swap(int slot) {
      switch(this) {
      case Silent:
      case Normal:
         InvUtils.swap(slot);
         return true;
      case InvSwitch:
         InvUtils.invSwap(slot);
         return true;
      case PickSilent:
         InvUtils.pickSwap(slot);
         return true;
      default:
         return false;
      }
   }

   public void swapBackInstantly() {
      switch(this) {
      case Silent:
         InvUtils.swapBackInstantly();
         break;
      case InvSwitch:
         InvUtils.invSwapBackInstantly();
         break;
      case PickSilent:
         InvUtils.pickSwapBackInstantly();
      }

   }

   public boolean swapInstantly(int slot) {
      switch(this) {
      case Silent:
      case Normal:
         InvUtils.swapInstantly(slot);
         return true;
      case InvSwitch:
         InvUtils.invSwapInstantly(slot);
         return true;
      case PickSilent:
         InvUtils.pickSwapInstantly(slot);
         return true;
      default:
         return false;
      }
   }

   public FindResult find(Predicate<class_1799> predicate) {
      return InvUtils.find(this.hotbar, this.inventory, predicate);
   }

   public FindResult find(class_1792 item) {
      return InvUtils.find(this.hotbar, this.inventory, item);
   }

   // $FF: synthetic method
   private static SwitchMode[] $values() {
      return new SwitchMode[]{Disabled, Normal, Silent, InvSwitch, PickSilent};
   }
}
