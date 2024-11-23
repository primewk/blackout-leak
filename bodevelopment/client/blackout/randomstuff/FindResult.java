package bodevelopment.client.blackout.randomstuff;

import net.minecraft.class_1799;

public record FindResult(int slot, int amount, class_1799 stack) {
   public FindResult(int slot, int amount, class_1799 stack) {
      this.slot = slot;
      this.amount = amount;
      this.stack = stack;
   }

   public boolean wasFound() {
      return this.slot > -1;
   }

   public int slot() {
      return this.slot;
   }

   public int amount() {
      return this.amount;
   }

   public class_1799 stack() {
      return this.stack;
   }
}
