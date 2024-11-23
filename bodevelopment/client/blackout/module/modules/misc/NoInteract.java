package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;

public class NoInteract extends Module {
   private static NoInteract INSTANCE;
   private final SettingGroup sgBlocks = this.addGroup("Blocks");
   private final SettingGroup sgItems = this.addGroup("Items");
   private final SettingGroup sgEntity = this.addGroup("Entity");
   private final Setting<NoInteract.NoInteractFilterMode> filterMode;
   private final Setting<List<class_1792>> whenHolding;
   private final Setting<NoInteract.NoInteractFilterMode> blockFilterMode;
   private final Setting<List<class_2248>> blocks;
   private final Setting<NoInteract.IgnoreMode> ignoreMode;
   private final Setting<NoInteract.NoInteractFilterMode> itemFilterMode;
   private final Setting<List<class_1792>> items;
   private final Setting<NoInteract.NoInteractFilterMode> filterModeEntity;
   private final Setting<List<class_1792>> whenHoldingEntity;
   private final Setting<NoInteract.NoInteractFilterMode> entityFilterMode;
   private final Setting<List<class_1299<?>>> entities;

   public NoInteract() {
      super("No Interact", "Prevents interacting with blocks and entities.", SubCategory.MISC, false);
      this.filterMode = this.sgBlocks.e("Holding Filter Mode (Block)", NoInteract.NoInteractFilterMode.Cancel, ".");
      this.whenHolding = this.sgBlocks.il("When Holding (Block)", ".", class_1802.field_8367, class_1802.field_8463);
      this.blockFilterMode = this.sgBlocks.e("Block Filter Mode", NoInteract.NoInteractFilterMode.Cancel, ".");
      this.blocks = this.sgBlocks.bl("Blocks", ".");
      this.ignoreMode = this.sgBlocks.e("Ignore Mode", NoInteract.IgnoreMode.SneakBlocks, ".");
      this.itemFilterMode = this.sgItems.e("Item Filter Mode", NoInteract.NoInteractFilterMode.Cancel, ".");
      this.items = this.sgItems.il("Items", ".");
      this.filterModeEntity = this.sgEntity.e("Holding Filter Mode (Entity)", NoInteract.NoInteractFilterMode.Cancel, ".");
      this.whenHoldingEntity = this.sgEntity.il("When Holding (Entity)", ".", class_1802.field_8367, class_1802.field_8463);
      this.entityFilterMode = this.sgEntity.e("Entity Filter Mode", NoInteract.NoInteractFilterMode.Accept, ".");
      this.entities = this.sgEntity.el("Entities", ".");
      INSTANCE = this;
   }

   public static NoInteract getInstance() {
      return INSTANCE;
   }

   public class_1269 handleBlock(class_1268 hand, class_2338 pos, SingleOut<class_1269> action) {
      class_1792 item = BlackOut.mc.field_1724.method_5998(hand).method_7909();
      if (((NoInteract.NoInteractFilterMode)this.filterMode.get()).shouldAccept(item, this.whenHolding)) {
         return (class_1269)action.get();
      } else {
         class_2248 block = BlackOut.mc.field_1687.method_8320(pos).method_26204();
         if (((NoInteract.NoInteractFilterMode)this.blockFilterMode.get()).shouldAccept(block, this.blocks)) {
            return (class_1269)action.get();
         } else {
            class_1269 actionResult;
            switch((NoInteract.IgnoreMode)this.ignoreMode.get()) {
            case Sneak:
               this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12979));
               actionResult = (class_1269)action.get();
               this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12984));
               return actionResult;
            case SneakBlocks:
               if (!(item instanceof class_1747)) {
                  return class_1269.field_5811;
               }

               this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12979));
               actionResult = (class_1269)action.get();
               this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12984));
               return actionResult;
            default:
               return class_1269.field_5811;
            }
         }
      }
   }

   public class_1269 handleEntity(class_1268 hand, class_1297 entity, SingleOut<class_1269> action) {
      class_1792 item = BlackOut.mc.field_1724.method_5998(hand).method_7909();
      if (((NoInteract.NoInteractFilterMode)this.filterModeEntity.get()).shouldAccept(item, this.whenHoldingEntity)) {
         return (class_1269)action.get();
      } else {
         return ((NoInteract.NoInteractFilterMode)this.entityFilterMode.get()).shouldAccept(entity.method_5864(), this.entities) ? (class_1269)action.get() : class_1269.field_5811;
      }
   }

   public class_1269 handleUse(class_1268 hand, SingleOut<class_1269> action) {
      return ((NoInteract.NoInteractFilterMode)this.itemFilterMode.get()).shouldAccept(BlackOut.mc.field_1724.method_5998(hand).method_7909(), this.items) ? (class_1269)action.get() : class_1269.field_5811;
   }

   public static enum NoInteractFilterMode {
      Cancel,
      Accept;

      private <T> boolean shouldAccept(T item, Setting<List<T>> list) {
         if (this == Cancel) {
            return !((List)list.get()).contains(item);
         } else {
            return ((List)list.get()).contains(item);
         }
      }

      // $FF: synthetic method
      private static NoInteract.NoInteractFilterMode[] $values() {
         return new NoInteract.NoInteractFilterMode[]{Cancel, Accept};
      }
   }

   public static enum IgnoreMode {
      Cancel,
      Sneak,
      SneakBlocks;

      // $FF: synthetic method
      private static NoInteract.IgnoreMode[] $values() {
         return new NoInteract.IgnoreMode[]{Cancel, Sneak, SneakBlocks};
      }
   }
}
