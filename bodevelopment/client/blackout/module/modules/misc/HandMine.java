package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.BlockUtils;
import bodevelopment.client.blackout.util.InvUtils;
import net.minecraft.class_1799;
import net.minecraft.class_2338;

public class HandMine extends Module {
   private static HandMine INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> allowInventory;
   private final Setting<Double> speed;

   public HandMine() {
      super("Hand Mine", "Silently uses the best tool.", SubCategory.MISC, false);
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.InvSwitch, ".");
      this.allowInventory = this.sgGeneral.b("Allow Inventory", false, ".", () -> {
         return ((SwitchMode)this.switchMode.get()).inventory;
      });
      this.speed = this.sgGeneral.d("Speed", 1.0D, 0.0D, 2.0D, 0.02D, ".");
      INSTANCE = this;
   }

   public static HandMine getInstance() {
      return INSTANCE;
   }

   public void onEnd(class_2338 pos, Runnable packet) {
      FindResult best = this.bestSlot(pos);
      if (best.wasFound()) {
         if (this.miningDelta(pos, best.stack()) < this.miningDelta(pos, Managers.PACKET.getStack())) {
            packet.run();
         } else {
            ((SwitchMode)this.switchMode.get()).swapInstantly(best.slot());
            packet.run();
            ((SwitchMode)this.switchMode.get()).swapBackInstantly();
         }
      }
   }

   public void onInstant(class_2338 pos, Runnable packet) {
      FindResult best = this.bestSlot(pos);
      if (best.wasFound()) {
         if (this.miningDelta(pos, Managers.PACKET.getStack()) >= 1.0D) {
            packet.run();
         } else {
            ((SwitchMode)this.switchMode.get()).swapInstantly(best.slot());
            packet.run();
            ((SwitchMode)this.switchMode.get()).swapBackInstantly();
         }
      }
   }

   public float getDelta(class_2338 pos, float vanilla) {
      FindResult best = this.bestSlot(pos);
      return !best.wasFound() ? vanilla : (float)Math.max(this.miningDelta(pos, best.stack()), this.miningDelta(pos, Managers.PACKET.getStack()));
   }

   private FindResult bestSlot(class_2338 pos) {
      return InvUtils.findBest(true, ((SwitchMode)this.switchMode.get()).inventory && (Boolean)this.allowInventory.get(), (stack) -> {
         return this.miningDelta(pos, stack);
      });
   }

   private double miningDelta(class_2338 pos, class_1799 stack) {
      return BlockUtils.getBlockBreakingDelta(pos, stack) * (Double)this.speed.get();
   }
}
