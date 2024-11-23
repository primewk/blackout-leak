package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_2761;

public class TPSManager extends Manager {
   public double tps = 20.0D;
   private long prevWorldTime = 0L;
   private long prevTime = 0L;
   private final List<Double> list = Collections.synchronizedList(new ArrayList());

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2761) {
         class_2761 packet = (class_2761)var3;
         long var10 = packet.method_11871() - this.prevWorldTime;
         double sus = (double)var10 / ((double)(System.currentTimeMillis() - this.prevTime) / 1000.0D);
         synchronized(this.list) {
            this.list.add(0, sus);
            OLEPOSSUtils.limitList(this.list, 10);
            this.calcTps();
         }

         this.prevWorldTime = packet.method_11871();
         this.prevTime = System.currentTimeMillis();
      }
   }

   private void calcTps() {
      double average = this.calcAverage(this.list);
      this.tps = this.calcAverage(this.list.stream().sorted(Comparator.comparingDouble((d) -> {
         return Math.abs(d - average);
      })).toList().subList(0, Math.min(this.list.size(), 7)));
   }

   private double calcAverage(List<Double> values) {
      double total = 0.0D;

      double d;
      for(Iterator var4 = values.iterator(); var4.hasNext(); total += d) {
         d = (Double)var4.next();
      }

      return total / (double)values.size();
   }
}
