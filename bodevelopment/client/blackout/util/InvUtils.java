package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.misc.Simulation;
import bodevelopment.client.blackout.randomstuff.FindResult;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2653;
import net.minecraft.class_2813;
import net.minecraft.class_2838;
import net.minecraft.class_2868;

public class InvUtils {
   private static int[] slots;
   public static int pickSlot = -1;
   public static int prevSlot = -1;

   public static int count(boolean hotbar, boolean inventory, Predicate<class_1799> predicate) {
      int count = 0;

      for(int i = hotbar ? 0 : 9; i < (inventory ? BlackOut.mc.field_1724.method_31548().method_5439() : 9); ++i) {
         class_1799 stack = BlackOut.mc.field_1724.method_31548().method_5438(i);
         if (stack != null && predicate.test(stack)) {
            count += stack.method_7947();
         }
      }

      return count;
   }

   public static FindResult find(boolean hotbar, boolean inventory, class_1792 item) {
      return find(hotbar, inventory, (stack) -> {
         return stack.method_7909() == item;
      });
   }

   public static FindResult find(boolean hotbar, boolean inventory, Predicate<class_1799> predicate) {
      if (BlackOut.mc.field_1724 != null) {
         for(int i = hotbar ? 0 : 9; i < (inventory ? BlackOut.mc.field_1724.method_31548().method_5439() : 9); ++i) {
            class_1799 stack = BlackOut.mc.field_1724.method_31548().method_5438(i);
            if (stack != null && predicate.test(stack)) {
               return new FindResult(i, stack.method_7947(), stack);
            }
         }
      }

      return new FindResult(-1, 0, (class_1799)null);
   }

   public static FindResult findNullable(boolean hotbar, boolean inventory, class_1792 item) {
      return findNullable(hotbar, inventory, (stack) -> {
         return stack.method_7909() == item;
      });
   }

   public static FindResult findNullable(boolean hotbar, boolean inventory, Predicate<class_1799> predicate) {
      if (BlackOut.mc.field_1724 != null) {
         for(int i = hotbar ? 0 : 9; i < (inventory ? BlackOut.mc.field_1724.method_31548().method_5439() : 9); ++i) {
            class_1799 stack = BlackOut.mc.field_1724.method_31548().method_5438(i);
            if (predicate.test(stack)) {
               return new FindResult(i, stack.method_7947(), stack);
            }
         }
      }

      return new FindResult(-1, 0, (class_1799)null);
   }

   public static FindResult findBest(boolean hotbar, boolean inventory, EpicInterface<class_1799, Double> test) {
      if (BlackOut.mc.field_1724 != null) {
         double bestValue = Double.NEGATIVE_INFINITY;
         FindResult best = null;

         for(int i = hotbar ? 0 : 9; i < (inventory ? BlackOut.mc.field_1724.method_31548().method_5439() : 9); ++i) {
            class_1799 stack = BlackOut.mc.field_1724.method_31548().method_5438(i);
            double value = (Double)test.get(stack);
            if (best == null || value > bestValue) {
               bestValue = value;
               best = new FindResult(i, stack.method_7947(), stack);
            }
         }

         if (best != null) {
            return best;
         }
      }

      return new FindResult(-1, 0, (class_1799)null);
   }

   public static int getId(int slot) {
      class_1703 screen = BlackOut.mc.field_1724.field_7512;
      int length = screen.field_7761.size();
      return slot < 9 ? length + slot - 10 : slot + length - 46;
   }

   public static void clickF(int slot) {
      clickSlot(slot, 40, class_1713.field_7791);
   }

   public static void clickSlot(int slot, int button, class_1713 action) {
      class_1703 handler = BlackOut.mc.field_1724.field_7512;
      interactSlot(handler.field_7763, getId(slot), button, action);
   }

   private static void clickSlotInstantly(int slot, int button, class_1713 action) {
      class_1703 handler = BlackOut.mc.field_1724.field_7512;
      interactSlot(handler.field_7763, getId(slot), button, action, true);
   }

   public static void interactSlot(int syncId, int slotId, int button, class_1713 actionType) {
      interactSlot(syncId, slotId, button, actionType, false);
   }

   public static void interactHandler(int slot, int button, class_1713 actionType) {
      interactSlot(BlackOut.mc.field_1724.field_7512.field_7763, slot, button, actionType);
   }

   public static void interactSlot(int syncId, int slotId, int button, class_1713 actionType, boolean instant) {
      class_1703 screenHandler = BlackOut.mc.field_1724.field_7512;
      class_2371<class_1735> defaultedList = screenHandler.field_7761;
      int i = defaultedList.size();
      ArrayList<class_1799> list = Lists.newArrayListWithCapacity(i);
      Iterator var9 = defaultedList.iterator();

      while(var9.hasNext()) {
         class_1735 slot = (class_1735)var9.next();
         list.add(slot.method_7677().method_7972());
      }

      screenHandler.method_7593(slotId, button, actionType, BlackOut.mc.field_1724);
      Int2ObjectOpenHashMap<class_1799> int2ObjectMap = new Int2ObjectOpenHashMap();

      for(int j = 0; j < i; ++j) {
         class_1799 itemStack = (class_1799)list.get(j);
         class_1799 itemStack2 = ((class_1735)defaultedList.get(j)).method_7677();
         if (!class_1799.method_7973(itemStack, itemStack2)) {
            int2ObjectMap.put(j, itemStack2.method_7972());
         }
      }

      if (instant) {
         Managers.PACKET.sendInstantly(new class_2813(syncId, screenHandler.method_37421(), slotId, button, actionType, screenHandler.method_34255().method_7972(), int2ObjectMap));
      } else {
         Managers.PACKET.sendPacket(new class_2813(syncId, screenHandler.method_37421(), slotId, button, actionType, screenHandler.method_34255().method_7972(), int2ObjectMap));
      }

   }

   public static void pickSwap(int slot) {
      pickSlot = slot;
      sendPick(slot, false);
   }

   public static void pickSwapInstantly(int slot) {
      pickSlot = slot;
      sendPick(slot, true);
   }

   public static void pickSwapBack() {
      if (pickSlot >= 0) {
         sendPick(pickSlot, false);
         pickSlot = -1;
      }

   }

   public static void pickSwapBackInstantly() {
      if (pickSlot >= 0) {
         sendPick(pickSlot, true);
         pickSlot = -1;
      }

   }

   private static void sendPick(int slot, boolean instant) {
      if (instant) {
         Managers.PACKET.sendInstantly(new class_2838(slot));
      } else {
         Managers.PACKET.sendPacket(new class_2838(slot));
      }

      if (Simulation.getInstance().pickSwitch()) {
         int hbSlot = BlackOut.mc.field_1724.method_31548().method_7386();
         Managers.PACKET.ignoreSetSlot.replace(hbSlot, 0.3D);
         BlackOut.mc.field_1724.method_31548().field_7545 = hbSlot;
         class_1799 stack1 = BlackOut.mc.field_1724.method_31548().method_5438(slot);
         class_1799 stack2 = BlackOut.mc.field_1724.method_31548().method_5438(hbSlot);
         Managers.PACKET.preApply(new class_2653(-2, 0, hbSlot, stack1));
         Managers.PACKET.preApply(new class_2653(-2, 0, slot, stack2));
         Managers.PACKET.addInvIgnore(new class_2653(0, 0, getId(slot), stack1));
         Managers.PACKET.addInvIgnore(new class_2653(0, 0, getId(hbSlot), stack2));
      }
   }

   public static void invSwap(int slot) {
      clickSlot(slot, Managers.PACKET.slot, class_1713.field_7791);
      slots = new int[]{slot, Managers.PACKET.slot};
   }

   public static void invSwapInstantly(int slot) {
      clickSlotInstantly(slot, Managers.PACKET.slot, class_1713.field_7791);
      slots = new int[]{slot, Managers.PACKET.slot};
   }

   public static void invSwapBack() {
      if (slots != null) {
         clickSlot(slots[0], slots[1], class_1713.field_7791);
      }
   }

   public static void invSwapBackInstantly() {
      if (slots != null) {
         clickSlotInstantly(slots[0], slots[1], class_1713.field_7791);
      }
   }

   public static void swap(int to) {
      prevSlot = BlackOut.mc.field_1724.method_31548().field_7545;
      BlackOut.mc.field_1724.method_31548().field_7545 = to;
      syncSlot(false);
   }

   public static void swapInstantly(int to) {
      prevSlot = BlackOut.mc.field_1724.method_31548().field_7545;
      BlackOut.mc.field_1724.method_31548().field_7545 = to;
      syncSlot(true);
   }

   private static void syncSlot(boolean instant) {
      int i = BlackOut.mc.field_1724.method_31548().field_7545;
      if (i != Managers.PACKET.slot) {
         if (instant) {
            Managers.PACKET.sendInstantly(new class_2868(i));
         } else {
            Managers.PACKET.sendPacket(new class_2868(i));
         }
      }

   }

   public static void swapBack() {
      if (prevSlot >= 0) {
         swap(prevSlot);
         prevSlot = -1;
      }
   }

   public static void swapBackInstantly() {
      if (prevSlot >= 0) {
         swapInstantly(prevSlot);
         prevSlot = -1;
      }
   }
}
