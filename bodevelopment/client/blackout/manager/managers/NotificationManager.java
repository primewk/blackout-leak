package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_4587;

public class NotificationManager extends Manager {
   private final List<NotificationManager.Notification> notifications = Collections.synchronizedList(new ArrayList());
   private float y = 0.0F;
   private final class_4587 stack = new class_4587();

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onRender2D(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1755 == null && BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.y = 100.0F;
         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         synchronized(this.notifications) {
            this.notifications.removeIf((notification) -> {
               if (System.currentTimeMillis() > notification.startTime + notification.time) {
                  return true;
               } else {
                  this.y += Notifications.getInstance().render(this.stack, notification, this.y);
                  return false;
               }
            });
         }

         this.stack.method_22909();
      }
   }

   public void addNotification(String text, String bigText, double time, Notifications.Type type) {
      if ((Boolean)Notifications.getInstance().hudNotifications.get()) {
         this.notifications.add(0, new NotificationManager.Notification(text, bigText, time, type));
      }

   }

   public static class Notification {
      public final String text;
      public final String bigText;
      public final Notifications.Type type;
      public final long startTime;
      public final long time;

      public Notification(String text, String bigText, double time, Notifications.Type type) {
         this.text = text;
         this.bigText = bigText;
         this.type = type;
         this.startTime = System.currentTimeMillis();
         this.time = Math.round(time * 1000.0D);
      }
   }
}
