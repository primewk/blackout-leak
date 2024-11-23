package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class UtilsManager extends Manager {
   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onRenderWorld(RenderEvent.World.Post event) {
      RenderUtils.onRender();
   }

   @Event
   public void onJoin(GameJoinEvent event) {
      DamageUtils.raycastContext = new class_3959(new class_243(0.0D, 0.0D, 0.0D), new class_243(0.0D, 0.0D, 0.0D), class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
   }
}
