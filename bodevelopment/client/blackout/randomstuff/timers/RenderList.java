package bodevelopment.client.blackout.randomstuff.timers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_3532;

public class RenderList<T> {
   protected final List<RenderList.Timer<T>> timers = Collections.synchronizedList(new ArrayList());

   protected RenderList() {
   }

   public void add(T value, double time) {
      this.timers.removeIf((timer) -> {
         return timer.value.equals(value);
      });
      this.timers.add(new RenderList.Timer(value, time));
   }

   public void update(RenderList.RenderConsumer<T> consumer) {
      synchronized(this.timers) {
         this.timers.removeIf((item) -> {
            if (System.currentTimeMillis() > item.endTime) {
               return true;
            } else {
               consumer.accept(item.value, (double)(System.currentTimeMillis() - item.startTime) / 1000.0D, class_3532.method_15350(class_3532.method_15370((double)System.currentTimeMillis(), (double)item.startTime, (double)item.endTime), 0.0D, 1.0D));
               return false;
            }
         });
      }
   }

   public void remove(T t) {
      synchronized(this.timers) {
         this.timers.removeIf((item) -> {
            return item.value.equals(t);
         });
      }
   }

   public void remove(Predicate<RenderList.Timer<T>> predicate) {
      synchronized(this.timers) {
         this.timers.removeIf(predicate);
      }
   }

   public boolean contains(Predicate<RenderList.Timer<T>> predicate) {
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         RenderList.Timer timer;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            timer = (RenderList.Timer)var3.next();
         } while(!predicate.test(timer));

         return true;
      }
   }

   public void clear() {
      this.timers.clear();
   }

   public static <E> RenderList<E> getList(boolean stacking) {
      return (RenderList)(stacking ? new StackingRenderList() : new RenderList());
   }

   public static class Timer<T> {
      public final T value;
      public final long startTime;
      public final long endTime;

      public Timer(T value, double time) {
         this.value = value;
         this.startTime = System.currentTimeMillis();
         this.endTime = System.currentTimeMillis() + Math.round(time * 1000.0D);
      }
   }

   @FunctionalInterface
   public interface RenderConsumer<E> {
      void accept(E var1, double var2, double var4);
   }
}
