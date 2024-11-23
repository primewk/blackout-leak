package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.ChatUtils;
import net.minecraft.class_2596;
import net.minecraft.class_7439;

public class AuthMe extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<String> password;
   private final Setting<Double> delay;
   private final Setting<Boolean> passwordConfirm;
   private long time;
   private boolean register;

   public AuthMe() {
      super("Auth Me", "Automatically logs in", SubCategory.MISC, true);
      this.password = this.sgGeneral.s("Password", "topShotta", "The password used");
      this.delay = this.sgGeneral.d("Delay", 2.5D, 0.0D, 5.0D, 0.1D, "Delay between receiving message and sending one.");
      this.passwordConfirm = this.sgGeneral.b("Password Confirm", true, ".");
      this.time = -1L;
      this.register = false;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.time >= 0L && !((double)(System.currentTimeMillis() - this.time) < (Double)this.delay.get() * 1000.0D)) {
            ChatUtils.sendMessage(this.getMessage());
            Managers.NOTIFICATIONS.addNotification("Attempted to " + (this.register ? "register" : "login"), this.getDisplayName(), 2.0D, Notifications.Type.Info);
            this.time = -1L;
         }
      }
   }

   @Event
   public void onSend(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_7439) {
         class_7439 packet = (class_7439)var3;
         String msg = packet.comp_763().getString();
         if ((double)(System.currentTimeMillis() - this.time) > (Double)this.delay.get() * 1000.0D + 500.0D) {
            if (msg.contains("/register")) {
               this.time = System.currentTimeMillis();
               this.register = true;
            } else if (msg.contains("/login")) {
               this.time = System.currentTimeMillis();
               this.register = false;
            }
         }
      }

   }

   private String getMessage() {
      return this.register ? "/register " + (String)this.password.get() + ((Boolean)this.passwordConfirm.get() ? " " + (String)this.password.get() : "") : "/login " + (String)this.password.get();
   }
}
