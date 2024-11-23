package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.EntityAddEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1511;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2824.class_5907;

public class EntityManager extends Manager {
   private final TimerList<Integer> renderDead = new TimerList(true);
   private final TimerList<class_2338> spawningItems = new TimerList(true);
   private final TimerList<Integer> semiDead = new TimerList(true);
   private final TimerList<class_2338> waitingToRemoveItem = new TimerList(true);
   private final TimerList<Integer> attacked = new TimerList(true);

   public void setDead(int id, boolean full) {
      if (full) {
         BlackOut.mc.field_1687.method_2945(id, class_5529.field_26998);
      } else {
         this.renderDead.add(id, 1.0D);
      }

   }

   public void setSemiDead(int i) {
      this.semiDead.add(i, 0.3D);
   }

   public void addSpawning(class_2338 pos) {
      this.spawningItems.add(pos, 2.0D);
   }

   public void removeSpawning(class_2338 pos) {
      this.spawningItems.remove((timer) -> {
         return ((class_2338)timer.value).equals(pos);
      });
   }

   public boolean containsItem(class_2338 pos) {
      return this.spawningItems.contains((Object)pos);
   }

   public void removeItems(class_2338 pos) {
      this.spawningItems.remove((p) -> {
         if (((class_2338)p.value).equals(pos)) {
            this.waitingToRemoveItem.add(pos, 0.5D);
            return true;
         } else {
            return false;
         }
      });
   }

   public boolean isDead(int id) {
      return this.semiDead.contains((Object)id);
   }

   public boolean shouldRender(int id) {
      return !this.renderDead.contains((Object)id);
   }

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onEntity(EntityAddEvent.Post event) {
      if (event.entity.method_5864() == class_1299.field_6052) {
         class_2338 pos = event.entity.method_24515();
         if (this.spawningItems.contains((Object)pos)) {
            this.removeSpawning(pos);
         }

         if (this.waitingToRemoveItem.contains((Object)pos)) {
            this.waitingToRemoveItem.remove((timer) -> {
               return ((class_2338)timer.value).equals(pos);
            });
            this.setSemiDead(event.id);
         }
      }

   }

   @Event
   public void packetSendEvent(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2824) {
         class_2824 packet = (class_2824)var3;
         AccessorInteractEntityC2SPacket packetAccessor = (AccessorInteractEntityC2SPacket)packet;
         if (packetAccessor.getType().method_34211() == class_5907.field_29172) {
            int id = packetAccessor.getId();
            class_1297 entity = BlackOut.mc.field_1687.method_8469(id);
            if (entity instanceof class_1511 && !this.attacked.contains((Object)id)) {
               class_2338 center = entity.method_24515();
               class_2350[] var7 = class_2350.values();
               int var8 = var7.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  class_2350 dir = var7[var9];
                  this.removeItems(center.method_10093(dir));
               }
            }

            this.attacked.replace(id, 0.25D);
         }

      }
   }
}
