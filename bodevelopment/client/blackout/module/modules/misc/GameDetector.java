package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.combat.offensive.Aura;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.SoundUtils;
import net.minecraft.class_1802;

public class GameDetector extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgDetect = this.addGroup("Detect");
   public final Setting<Boolean> reEnable;
   public final Setting<Boolean> disable;
   public final Setting<Boolean> capabilities;
   public final Setting<Boolean> compass;
   public final Setting<Boolean> slime;
   public final Setting<Boolean> test;
   public boolean gameStarted;
   private boolean prevState;
   private boolean disabledAura;
   private boolean disabledStealer;
   private boolean disabledManager;

   public GameDetector() {
      super("Game Detector", "Detects when a game is in progress and toggles modules based on it", SubCategory.MISC, true);
      this.reEnable = this.sgGeneral.b("Re Enable on game start", false, ".");
      this.disable = this.sgGeneral.b("Disable on game end", true, ".");
      this.capabilities = this.sgDetect.b("Capabilities", true, ".");
      this.compass = this.sgDetect.b("Compass", true, ".");
      this.slime = this.sgDetect.b("Slime", true, ".");
      this.test = this.sgDetect.b("test", true, ".");
      this.gameStarted = false;
      this.prevState = false;
      this.disabledAura = false;
      this.disabledStealer = false;
      this.disabledManager = false;
   }

   public String getInfo() {
      return this.gameStarted ? "Started" : "Waiting for start";
   }

   public void onDisable() {
      this.gameStarted = false;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1724.field_6012 >= 20) {
            if (this.gameStarted != this.prevState) {
               this.toggleModules(this.gameStarted);
               this.prevState = this.gameStarted;
            }

            this.gameStarted = !(Boolean)this.capabilities.get() || !BlackOut.mc.field_1724.method_31549().field_7478 && !BlackOut.mc.field_1724.method_31549().field_7479 && !BlackOut.mc.field_1724.method_31549().field_7480;
            if ((Boolean)this.compass.get() && InvUtils.count(true, false, (stack) -> {
               return stack.method_7909() == class_1802.field_8251;
            }) > 0) {
               this.gameStarted = false;
            }

            if ((Boolean)this.slime.get() && InvUtils.count(true, false, (stack) -> {
               return stack.method_7909() == class_1802.field_8777;
            }) > 0) {
               this.gameStarted = false;
            }

            if ((Boolean)this.test.get()) {
               this.gameStarted = false;
            }

         }
      }
   }

   private void toggleModules(boolean enable) {
      Aura auraModule = Aura.getInstance();
      Stealer stealerModule = Stealer.getInstance();
      Manager managerModule = Manager.getInstance();
      if (enable && (Boolean)this.reEnable.get()) {
         if (this.disabledAura) {
            auraModule.silentEnable();
         }

         if (this.disabledStealer) {
            stealerModule.silentEnable();
         }

         if (this.disabledManager) {
            managerModule.silentEnable();
         }

         this.sendNotification(this.getDisplayName() + " enabled some modules", "Game start detected");
      } else if ((Boolean)this.disable.get()) {
         if (auraModule.enabled) {
            auraModule.silentDisable();
            this.disabledAura = true;
         }

         if (stealerModule.enabled) {
            stealerModule.silentDisable();
            this.disabledStealer = true;
         }

         if (managerModule.enabled) {
            managerModule.silentDisable();
            this.disabledManager = true;
         }

         this.sendNotification(this.getDisplayName() + " disabled some modules", "Game end detected");
      }

   }

   private void sendNotification(String message, String bigText) {
      Notifications notifications = Notifications.getInstance();
      if ((Boolean)notifications.chatNotifications.get()) {
         String var10001 = this.getDisplayName();
         this.sendMessage(var10001 + " " + message);
      }

      Managers.NOTIFICATIONS.addNotification(message == null ? "Disabled " + this.getDisplayName() : message, bigText, 5.0D, Notifications.Type.Info);
      if ((Boolean)notifications.sound.get()) {
         SoundUtils.play(1.0F, 1.0F, "disable");
      }

   }

   @Event
   public void onGameJoin(GameJoinEvent event) {
      this.gameStarted = false;
   }

   public boolean shouldHibernate() {
      return this.enabled && this.gameStarted;
   }
}
