package bodevelopment.client.blackout.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
   public final Map<Class<?>, List<EventBus.Listener>> listeners = new ConcurrentHashMap();

   public void subscribe(Object object, ISkip skip) {
      Iterator var3 = this.getListeners(new ArrayList(), object.getClass(), object, skip).iterator();

      while(var3.hasNext()) {
         EventBus.Listener listener = (EventBus.Listener)var3.next();
         Class<?> clazz = listener.method.getParameters()[0].getType();
         if (this.listeners.containsKey(clazz)) {
            ((List)this.listeners.get(clazz)).add(listener);
         } else {
            List<EventBus.Listener> list = new ArrayList();
            list.add(listener);
            this.listeners.put(clazz, list);
         }
      }

   }

   public void unsubscribe(Object object) {
      this.listeners.values().forEach((list) -> {
         list.removeIf((listener) -> {
            return listener.object.equals(object);
         });
      });
   }

   private List<EventBus.Listener> getListeners(List<EventBus.Listener> list, Class<?> clazz, Object object, ISkip skip) {
      Method[] var5 = clazz.getDeclaredMethods();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Method method = var5[var7];
         if (method.isAnnotationPresent(Event.class)) {
            int priority = ((Event)method.getAnnotation(Event.class)).eventPriority();
            list.add(this.getIndex(list, priority), new EventBus.Listener(object, method, skip, priority));
         }
      }

      if (clazz.getSuperclass() != null) {
         this.getListeners(list, clazz.getSuperclass(), object, skip);
      }

      return list;
   }

   public <T> T post(T object) {
      if (this.listeners.containsKey(object.getClass())) {
         ((List)this.listeners.get(object.getClass())).forEach((l) -> {
            try {
               if (!l.skip.shouldSkip()) {
                  l.method.invoke(l.object, object);
               }

            } catch (InvocationTargetException | IllegalAccessException var3) {
               throw new RuntimeException(var3);
            }
         });
      }

      return object;
   }

   private int getIndex(List<EventBus.Listener> l, int priority) {
      for(int i = 0; i < l.size(); ++i) {
         if (((EventBus.Listener)l.get(i)).priority > priority) {
            return i;
         }
      }

      return l.size();
   }

   public static record Listener(Object object, Method method, ISkip skip, int priority) {
      public Listener(Object object, Method method, ISkip skip, int priority) {
         this.object = object;
         this.method = method;
         this.skip = skip;
         this.priority = priority;
      }

      public Object object() {
         return this.object;
      }

      public Method method() {
         return this.method;
      }

      public ISkip skip() {
         return this.skip;
      }

      public int priority() {
         return this.priority;
      }
   }
}
