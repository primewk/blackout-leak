package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.module.modules.misc.PingSpoof;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_2596;

public class PingManager extends Manager {
   private final List<PingManager.DelayedPacket> sending = Collections.synchronizedList(new ArrayList());

   public void update() {
      PingSpoof spoof = PingSpoof.getInstance();
      spoof.refresh();
      long ping = (long)spoof.getPing();
      synchronized(this.sending) {
         this.sending.removeIf((d) -> {
            if (System.currentTimeMillis() < d.time() + ping) {
               return false;
            } else {
               d.runnable().run();
               return true;
            }
         });
      }
   }

   public boolean shouldDelay(class_2596<?> packet) {
      PingSpoof spoof = PingSpoof.getInstance();
      return !spoof.enabled ? false : spoof.shouldDelay(packet);
   }

   public void addSend(Runnable runnable) {
      this.sending.add(new PingManager.DelayedPacket(runnable, System.currentTimeMillis()));
   }

   private static record DelayedPacket(Runnable runnable, long time) {
      private DelayedPacket(Runnable runnable, long time) {
         this.runnable = runnable;
         this.time = time;
      }

      public Runnable runnable() {
         return this.runnable;
      }

      public long time() {
         return this.time;
      }
   }
}
