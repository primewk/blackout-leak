package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.EntityAddEvent;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.interfaces.mixin.IEndCrystalEntity;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.mixin.accessors.AccessorPlayerMoveC2SPacket;
import bodevelopment.client.blackout.module.modules.misc.Simulation;
import bodevelopment.client.blackout.randomstuff.FakePlayerEntity;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1723;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2653;
import net.minecraft.class_2708;
import net.minecraft.class_2735;
import net.minecraft.class_2793;
import net.minecraft.class_2813;
import net.minecraft.class_2824;
import net.minecraft.class_2828;
import net.minecraft.class_2838;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_3532;
import net.minecraft.class_634;
import net.minecraft.class_2824.class_5907;
import net.minecraft.class_2828.class_2830;

public class PacketManager extends Manager {
   private boolean onGround;
   public int slot = 0;
   public class_243 pos;
   public final TimerList<Integer> ids;
   private final List<Consumer<? super class_634>> grimQueue;
   private final List<Consumer<? super class_634>> postGrimQueue;
   private boolean spoofOG;
   private boolean spoofedOG;
   public int teleportId;
   public int receivedId;
   public int prevReceived;
   public final TimerMap<Integer, class_243> validPos;
   public final TimerList<Integer> ignoreSetSlot;
   public final TimerList<class_2653> ignoredInventory;
   private final TimerList<class_2338> own;

   public PacketManager() {
      this.pos = class_243.field_1353;
      this.ids = new TimerList(true);
      this.grimQueue = new ArrayList();
      this.postGrimQueue = new ArrayList();
      this.spoofOG = false;
      this.spoofedOG = false;
      this.teleportId = 0;
      this.receivedId = 0;
      this.prevReceived = 0;
      this.validPos = new TimerMap(true);
      this.ignoreSetSlot = new TimerList(true);
      this.ignoredInventory = new TimerList(true);
      this.own = new TimerList(true);
   }

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
      this.onGround = false;
   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2868) {
         class_2868 packet = (class_2868)var3;
         if (packet.method_12442() >= 0) {
            this.slot = packet.method_12442();
            BlackOut.mc.field_1761.field_3721 = packet.method_12442();
         }
      }

      var3 = event.packet;
      if (var3 instanceof class_2828) {
         class_2828 packet = (class_2828)var3;
         this.onGround = packet.method_12273();
         if (packet.method_36171()) {
            this.pos = new class_243(packet.method_12269(0.0D), packet.method_12268(0.0D), packet.method_12274(0.0D));
         }
      }

      var3 = event.packet;
      if (var3 instanceof class_2793) {
         class_2793 packet = (class_2793)var3;
         this.teleportId = packet.method_12086();
      }

      var3 = event.packet;
      if (var3 instanceof class_2824) {
         class_2824 packet = (class_2824)var3;
         if (((AccessorInteractEntityC2SPacket)packet).getType().method_34211() == class_5907.field_29172) {
            class_1297 var4 = BlackOut.mc.field_1687.method_8469(((AccessorInteractEntityC2SPacket)packet).getId());
            if (var4 instanceof FakePlayerEntity) {
               FakePlayerEntity player = (FakePlayerEntity)var4;
               Managers.FAKE_PLAYER.onAttack(player);
            }

            if (Simulation.getInstance().hitReset()) {
               BlackOut.mc.field_1724.method_7350();
            }

            if (Simulation.getInstance().stopSprint()) {
               BlackOut.mc.field_1724.method_5728(false);
            }
         }
      }

      var3 = event.packet;
      if (var3 instanceof class_2885) {
         class_2885 packet = (class_2885)var3;
         if (this.handStack(packet.method_12546()).method_31574(class_1802.field_8301)) {
            this.own.replace(packet.method_12543().method_17777().method_10084(), 1.0D);
         }
      }

   }

   @Event
   public void onEntityAdd(EntityAddEvent.Post event) {
      class_1297 var3 = event.entity;
      if (var3 instanceof class_1511) {
         class_1511 entity = (class_1511)var3;
         if (this.own.contains((Object)entity.method_24515())) {
            ((IEndCrystalEntity)entity).blackout_Client$markOwn();
         }
      }

   }

   @Event
   public void onSend(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2828) {
         class_2828 packet = (class_2828)var3;
         if (this.spoofOG) {
            ((AccessorPlayerMoveC2SPacket)packet).setOnGround(this.spoofedOG);
            this.spoofOG = false;
         }
      }

   }

   @Event
   public void onReceive(PacketEvent.Receive.Post e) {
      class_2596 var3 = e.packet;
      if (var3 instanceof class_2708) {
         class_2708 packet = (class_2708)var3;
         class_243 vec = new class_243(packet.method_11734(), packet.method_11735(), packet.method_11738());
         int id = packet.method_11737();
         if (this.validPos.containsKey(id) && ((class_243)this.validPos.get(id)).equals(vec)) {
            e.setCancelled(true);
            this.validPos.removeKey(packet.method_11737());
         }

         this.prevReceived = this.receivedId;
         this.receivedId = packet.method_11737();
         if (!this.ids.contains((Object)id)) {
            this.teleportId = id;
         }
      }

      var3 = e.packet;
      if (var3 instanceof class_2735) {
         class_2735 packet = (class_2735)var3;
         if (this.ignoreSetSlot.contains((Object)packet.method_11803())) {
            e.setCancelled(true);
         }
      }

      var3 = e.packet;
      if (var3 instanceof class_2653) {
         class_2653 packet = (class_2653)var3;
         if (this.ignoredInventory.contains((timer) -> {
            return this.inventoryEquals(packet, (class_2653)timer.value);
         }) && !this.isItemEquals(packet)) {
            e.setCancelled(true);
         }
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      this.sendPackets();
   }

   private boolean isItemEquals(class_2653 packet) {
      return this.getStackInSlot(packet).method_31574(packet.method_11449().method_7909());
   }

   private class_1799 getStackInSlot(class_2653 packet) {
      if (packet.method_11452() == -1) {
         return null;
      } else if (packet.method_11452() == -2) {
         return BlackOut.mc.field_1724.method_31548().method_5438(packet.method_11450());
      } else if (packet.method_11452() == 0 && class_1723.method_36211(packet.method_11450())) {
         return BlackOut.mc.field_1724.field_7498.method_7611(packet.method_11450()).method_7677();
      } else {
         return packet.method_11452() == BlackOut.mc.field_1724.field_7512.field_7763 ? BlackOut.mc.field_1724.field_7512.method_7611(packet.method_11450()).method_7677() : null;
      }
   }

   public void sendPackets() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.sendList(this.grimQueue);
         this.sendList(this.postGrimQueue);
      }
   }

   private void sendList(List<Consumer<? super class_634>> list) {
      list.forEach((consumer) -> {
         this.sendPacket(BlackOut.mc.method_1562(), consumer);
      });
      list.clear();
   }

   private void sendPacket(class_634 handler, Consumer<? super class_634> consumer) {
      if (handler != null) {
         consumer.accept(handler);
      }

   }

   public void sendPacket(class_2596<?> packet) {
      this.sendPacketToList(packet, this.grimQueue);
   }

   public void sendPostPacket(class_2596<?> packet) {
      this.sendPacketToList(packet, this.postGrimQueue);
   }

   public void sendInstantly(class_2596<?> packet) {
      this.sendPacket(BlackOut.mc.method_1562(), (handler) -> {
         handler.method_52787(packet);
      });
   }

   private void sendPacketToList(class_2596<?> packet, List<Consumer<? super class_634>> list) {
      if (this.shouldBeDelayed(packet)) {
         this.addToQueue((handler) -> {
            handler.method_52787(packet);
         }, list);
      } else {
         BlackOut.mc.method_1562().method_52787(packet);
      }

   }

   public void addToQueue(Consumer<? super class_634> consumer) {
      this.addToQueue(consumer, this.grimQueue);
   }

   public void addToPostQueue(Consumer<? super class_634> consumer) {
      this.addToQueue(consumer, this.postGrimQueue);
   }

   private void addToQueue(Consumer<? super class_634> consumer, List<Consumer<? super class_634>> list) {
      if (SettingUtils.grimPackets()) {
         list.add(consumer);
      } else {
         consumer.accept(BlackOut.mc.method_1562());
      }

   }

   private boolean shouldBeDelayed(class_2596<?> packet) {
      if (!SettingUtils.grimPackets()) {
         return false;
      } else if (packet instanceof class_2824) {
         return true;
      } else if (packet instanceof class_2885) {
         return true;
      } else if (packet instanceof class_2886) {
         return true;
      } else if (packet instanceof class_2846) {
         return true;
      } else if (packet instanceof class_2879) {
         return true;
      } else if (packet instanceof class_2868) {
         return true;
      } else {
         return packet instanceof class_2813 ? true : packet instanceof class_2838;
      }
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public class_1799 getStack() {
      return BlackOut.mc.field_1724.method_31548().method_5438(this.slot);
   }

   public class_1799 stackInHand(class_1268 hand) {
      class_1799 var10000;
      switch(hand) {
      case field_5808:
         var10000 = this.getStack();
         break;
      case field_5810:
         var10000 = BlackOut.mc.field_1724.method_6079();
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public boolean isHolding(class_1792... items) {
      class_1799 stack = this.getStack();
      if (stack == null) {
         return false;
      } else {
         class_1792[] var3 = items;
         int var4 = items.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            class_1792 item = var3[var5];
            if (item.equals(stack.method_7909())) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isHolding(class_1792 item) {
      class_1799 stack = this.getStack();
      return stack == null ? false : stack.method_7909().equals(item);
   }

   public void spoofOG(boolean state) {
      this.spoofOG = true;
      this.spoofedOG = state;
   }

   public class_1799 handStack(class_1268 hand) {
      return hand == class_1268.field_5808 ? this.getStack() : BlackOut.mc.field_1724.method_6079();
   }

   public class_2793 incrementedPacket(class_243 vec3d) {
      int id = this.teleportId + 1;
      this.ids.add(id, 1.0D);
      this.validPos.add(id, vec3d, 1.0D);
      return new class_2793(id);
   }

   public class_2793 incrementedPacket2(class_243 vec3d) {
      int id = this.receivedId + 1;
      this.ids.replace(id, 1.0D);
      this.validPos.add(id, vec3d, 1.0D);
      return new class_2793(id);
   }

   public void preApply(class_2653 packet) {
      packet.method_11451(BlackOut.mc.method_1562());
      this.addInvIgnore(packet);
   }

   public void addInvIgnore(class_2653 packet) {
      this.ignoredInventory.remove((timer) -> {
         return this.inventoryEquals((class_2653)timer.value, packet);
      });
      this.ignoredInventory.add(packet, 0.3D);
   }

   private boolean inventoryEquals(class_2653 packet1, class_2653 packet2) {
      return packet1.method_11450() == packet2.method_11450() && packet1.method_11449().method_31574(packet2.method_11449().method_7909());
   }

   public void sendPreUse() {
      this.sendInstantly(new class_2830(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, this.isOnGround()));
   }

   public void sendPositionSync(class_243 pos, float yaw, float pitch) {
      yaw = class_3532.method_15393(yaw);
      if (yaw >= 0.0F) {
         yaw = -180.0F - (180.0F - yaw);
      }

      Managers.PACKET.sendInstantly(new class_2830(pos.field_1352, pos.field_1351, pos.field_1350, yaw, pitch, false));
   }
}
