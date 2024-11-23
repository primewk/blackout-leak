package bodevelopment.client.blackout.randomstuff.timers;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TimerMap<E, T> {
   public static final List<TimerMap<?, ?>> updating = new ArrayList();
   public final Map<E, TimerMap.Timer<T>> timers = new ConcurrentHashMap();

   public TimerMap(boolean autoUpdate) {
      if (autoUpdate) {
         updating.add(this);
      }

   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      this.update();
   }

   public void add(E key, T value, double time) {
      this.timers.remove(key);
      this.timers.put(key, new TimerMap.Timer(value, time));
   }

   public void update() {
      this.remove((key, value) -> {
         return System.currentTimeMillis() > value.endTime;
      });
   }

   public T get(E key) {
      return ((TimerMap.Timer)this.timers.get(key)).value;
   }

   public void clear() {
      this.timers.clear();
   }

   public T removeKey(E key) {
      TimerMap.Timer<T> value = (TimerMap.Timer)this.timers.remove(key);
      return value == null ? null : value.value;
   }

   public T remove(DoublePredicate<E, TimerMap.Timer<T>> predicate) {
      Iterator var2 = this.timers.entrySet().iterator();

      Entry entry;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         entry = (Entry)var2.next();
      } while(!predicate.test(entry.getKey(), (TimerMap.Timer)entry.getValue()));

      this.timers.remove(entry.getKey());
      return ((TimerMap.Timer)entry.getValue()).value;
   }

   public boolean contains(DoublePredicate<E, TimerMap.Timer<T>> predicate) {
      Iterator var2 = this.timers.entrySet().iterator();

      Entry entry;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entry = (Entry)var2.next();
      } while(!predicate.test(entry.getKey(), (TimerMap.Timer)entry.getValue()));

      return true;
   }

   public boolean containsKey(E key) {
      return this.timers.containsKey(key);
   }

   public boolean containsValue(T value) {
      Iterator var2 = this.timers.entrySet().iterator();

      Entry entry;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entry = (Entry)var2.next();
      } while(((TimerMap.Timer)entry.getValue()).value != value);

      return true;
   }

   public static class Timer<T> {
      public final T value;
      public final long endTime;
      public final double time;

      public Timer(T value, double time) {
         this.value = value;
         this.endTime = System.currentTimeMillis() + Math.round(time * 1000.0D);
         this.time = time;
      }
   }
}
