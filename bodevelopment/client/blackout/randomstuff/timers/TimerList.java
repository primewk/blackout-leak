package bodevelopment.client.blackout.randomstuff.timers;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimerList<T> {
   public static final List<TimerList<?>> updating = new ArrayList();
   private final List<TimerList.Timer<T>> timers = Collections.synchronizedList(new ArrayList());

   public TimerList(boolean autoUpdate) {
      if (autoUpdate) {
         updating.add(this);
      }

   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      this.update();
   }

   public void add(T value, double time) {
      this.timers.add(new TimerList.Timer(value, time));
   }

   public void addAll(Collection<? extends T> collection, double time) {
      collection.forEach((item) -> {
         this.add(item, time);
      });
   }

   public void update() {
      synchronized(this.timers) {
         this.timers.removeIf((item) -> {
            return System.currentTimeMillis() > item.endTime;
         });
      }
   }

   public void clear() {
      this.timers.clear();
   }

   public void forEach(Consumer<? super TimerList.Timer<T>> consumer) {
      synchronized(this.timers) {
         this.timers.forEach(consumer);
      }
   }

   public Map<T, Double> getMap() {
      Map<T, Double> map = new HashMap();
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         while(var3.hasNext()) {
            TimerList.Timer<T> timer = (TimerList.Timer)var3.next();
            map.put(timer.value, timer.time);
         }

         return map;
      }
   }

   public long getEndTime(T object) {
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         TimerList.Timer timer;
         do {
            if (!var3.hasNext()) {
               return -1L;
            }

            timer = (TimerList.Timer)var3.next();
         } while(!timer.value.equals(object));

         return timer.endTime;
      }
   }

   public List<TimerList.Timer<T>> getTimers() {
      return this.timers;
   }

   public List<T> getList() {
      List<T> l = new ArrayList();
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         while(var3.hasNext()) {
            TimerList.Timer<T> timer = (TimerList.Timer)var3.next();
            l.add(timer.value);
         }

         return l;
      }
   }

   public T remove(Predicate<? super TimerList.Timer<T>> predicate) {
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         TimerList.Timer timer;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            timer = (TimerList.Timer)var3.next();
         } while(!predicate.test(timer));

         this.timers.remove(timer);
         return timer.value;
      }
   }

   public boolean removeAll(Predicate<? super TimerList.Timer<T>> predicate) {
      synchronized(this.timers) {
         return this.timers.removeIf(predicate);
      }
   }

   public boolean contains(T value) {
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         TimerList.Timer timer;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            timer = (TimerList.Timer)var3.next();
         } while(!timer.value.equals(value));

         return true;
      }
   }

   public boolean contains(Predicate<TimerList.Timer<T>> predicate) {
      synchronized(this.timers) {
         Iterator var3 = this.timers.iterator();

         TimerList.Timer timer;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            timer = (TimerList.Timer)var3.next();
         } while(!predicate.test(timer));

         return true;
      }
   }

   public void replace(T value, double time) {
      this.remove((timer) -> {
         return timer.value.equals(value);
      });
      this.add(value, time);
   }

   public static class Timer<T> {
      public final T value;
      public final long startTime;
      public final long endTime;
      public final double time;

      public Timer(T value, double time) {
         this.value = value;
         this.startTime = System.currentTimeMillis();
         this.endTime = System.currentTimeMillis() + Math.round(time * 1000.0D);
         this.time = time;
      }
   }
}
