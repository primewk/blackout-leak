package bodevelopment.client.blackout.randomstuff.timers;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TickTimerList<T> {
   public static final List<TickTimerList<?>> updating = new ArrayList();
   public final List<TickTimerList.TickTimer<T>> timers = new ArrayList();

   public TickTimerList(boolean autoUpdate) {
      if (autoUpdate) {
         updating.add(this);
      }

   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      this.update();
   }

   public void add(T value, int ticks) {
      this.timers.add(new TickTimerList.TickTimer(value, ticks));
   }

   public void forEach(Consumer<? super T> consumer) {
      this.timers.forEach((timer) -> {
         consumer.accept(timer.value);
      });
   }

   public void update() {
      this.timers.removeIf((item) -> {
         return item.ticks-- <= 0;
      });
   }

   public void clear() {
      this.timers.clear();
   }

   public Map<T, Integer> getMap() {
      Map<T, Integer> map = new HashMap();
      Iterator var2 = this.timers.iterator();

      while(var2.hasNext()) {
         TickTimerList.TickTimer<T> timer = (TickTimerList.TickTimer)var2.next();
         map.put(timer.value, timer.ticks);
      }

      return map;
   }

   public List<T> getList() {
      List<T> l = new ArrayList();
      Iterator var2 = this.timers.iterator();

      while(var2.hasNext()) {
         TickTimerList.TickTimer<T> timer = (TickTimerList.TickTimer)var2.next();
         l.add(timer.value);
      }

      return l;
   }

   public int getTicksLeft(T object) {
      Iterator var2 = this.timers.iterator();

      TickTimerList.TickTimer timer;
      do {
         if (!var2.hasNext()) {
            return -1;
         }

         timer = (TickTimerList.TickTimer)var2.next();
      } while(!timer.value.equals(object));

      return timer.ticks;
   }

   public void remove(TickTimerList.TickTimer<T> timer) {
      this.timers.remove(timer);
   }

   public T remove(Predicate<? super TickTimerList.TickTimer<T>> predicate) {
      Iterator var2 = this.timers.iterator();

      TickTimerList.TickTimer timer;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         timer = (TickTimerList.TickTimer)var2.next();
      } while(!predicate.test(timer));

      this.timers.remove(timer);
      return timer.value;
   }

   public TickTimerList.TickTimer<T> get(Predicate<TickTimerList.TickTimer<T>> predicate) {
      Iterator var2 = this.timers.iterator();

      TickTimerList.TickTimer timer;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         timer = (TickTimerList.TickTimer)var2.next();
      } while(!predicate.test(timer));

      return timer;
   }

   public boolean contains(Predicate<TickTimerList.TickTimer<T>> predicate) {
      Iterator var2 = this.timers.iterator();

      TickTimerList.TickTimer timer;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         timer = (TickTimerList.TickTimer)var2.next();
      } while(!predicate.test(timer));

      return true;
   }

   public boolean contains(T value) {
      return this.contains((timer) -> {
         return timer.value.equals(value);
      });
   }

   public static class TickTimer<T> {
      public final T value;
      public int ticks;

      public TickTimer(T value, int ticks) {
         this.value = value;
         this.ticks = ticks;
      }
   }
}
