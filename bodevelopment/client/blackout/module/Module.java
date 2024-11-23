package bodevelopment.client.blackout.module;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.BindMode;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwingState;
import bodevelopment.client.blackout.enums.SwingType;
import bodevelopment.client.blackout.helpers.RotationHelper;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.visual.misc.SwingModifier;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.Settings;
import bodevelopment.client.blackout.module.setting.WarningSettingGroup;
import bodevelopment.client.blackout.module.setting.settings.KeyBindSetting;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.ChatUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.SoundUtils;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2815;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2848;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_3419;
import net.minecraft.class_3965;
import net.minecraft.class_7202;
import net.minecraft.class_7204;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848.class_2849;

public class Module extends RotationHelper {
   public final String name;
   public final String description;
   public final SubCategory category;
   public boolean enabled = false;
   public long toggleTime = 0L;
   public final List<SettingGroup> settingGroups = new ArrayList();
   public final SettingGroup sgModule = this.addGroup("Module");
   private final Setting<String> displayName;
   public final KeyBindSetting bind;
   public final Setting<BindMode> bindMode;
   public boolean wip = false;

   public Module(String name, String description, SubCategory category, boolean subscribe) {
      this.name = name;
      this.description = description;
      this.category = category;
      this.set(this);
      this.displayName = this.sgModule.s("Name", name, "");
      this.bind = (KeyBindSetting)Settings.k("Bind", "This module can be toggled by pressing this key.", (SingleOut)null);
      this.bindMode = this.sgModule.e("Bind Mode", BindMode.Toggle, ".");
      if (subscribe) {
         BlackOut.EVENT_BUS.subscribe(this, this::shouldSkipListeners);
      }

   }

   public boolean toggleable() {
      return true;
   }

   public String getFileName() {
      return this.name.replaceAll(" ", "");
   }

   public String getDisplayName() {
      String dn = (String)this.displayName.get();
      return dn.isEmpty() ? this.name : dn;
   }

   public void toggle() {
      if (this.enabled) {
         this.disable();
      } else {
         this.enable();
      }

   }

   public void silentEnable() {
      this.enable((String)null, 0, false);
   }

   public void enable() {
      this.enable((String)null, 2, true);
   }

   public void enable(String message) {
      this.enable(message, 2, true);
   }

   public void enable(String message, int time, boolean sendNotification) {
      if (!this.enabled) {
         this.onEnable();
         this.enabled = true;
         this.toggleTime = System.currentTimeMillis();
         if (sendNotification) {
            this.sendNotification(message == null ? this.getDisplayName() + class_124.field_1060 + " Enabled" : " " + message, message == null ? "Enabled " + this.getDisplayName() : message, "Module Toggle", Notifications.Type.Enable, (double)(time == 0 ? 2 : time));
            if ((Boolean)Notifications.getInstance().sound.get()) {
               SoundUtils.play(1.0F, 1.0F, "enable");
            }
         }

      }
   }

   public void silentDisable() {
      this.doDisable((String)null, 0, Notifications.Type.Disable, false);
   }

   public void disable() {
      this.disable((String)null, 2);
   }

   public void disable(String message) {
      this.disable(message, 2);
   }

   public void disable(String message, int time) {
      this.doDisable(message, time, Notifications.Type.Disable, true);
   }

   public void disable(String message, int time, Notifications.Type type) {
      this.doDisable(message, time, type, true);
   }

   private void doDisable(String message, int time, Notifications.Type type, Boolean sendNotification) {
      if (this.enabled) {
         this.onDisable();
         this.enabled = false;
         this.toggleTime = System.currentTimeMillis();
         if (sendNotification) {
            this.sendNotification(message == null ? this.getDisplayName() + class_124.field_1061 + " OFF" : " " + message, message == null ? "Disabled " + this.getDisplayName() : message, "Module Toggle", type, (double)(time == 0 ? 2 : time));
            if ((Boolean)Notifications.getInstance().sound.get()) {
               SoundUtils.play(1.0F, 1.0F, "disable");
            }
         }

      }
   }

   protected void sendNotification(String chatMessage, String text, String bigText, Notifications.Type type, double time) {
      Notifications notifications = Notifications.getInstance();
      if ((Boolean)notifications.chatNotifications.get()) {
         this.sendMessage(chatMessage);
      }

      Managers.NOTIFICATIONS.addNotification(text, bigText, time, type);
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public String getInfo() {
      return null;
   }

   protected void sendMessage(String message) {
      ChatUtils.addMessage(Notifications.getInstance().getClientPrefix() + " " + message, Objects.hash(new Object[]{this.name + "toggle"}));
   }

   protected void sendPacket(class_2596<?> packet) {
      Managers.PACKET.sendPacket(packet);
   }

   protected void sendInstantly(class_2596<?> packet) {
      Managers.PACKET.sendInstantly(packet);
   }

   protected void sendSequencedInstantly(class_7204 packetCreator) {
      if (BlackOut.mc.field_1761 != null && BlackOut.mc.field_1687 != null) {
         class_7202 sequence = BlackOut.mc.field_1687.method_41925().method_41937();
         class_2596<?> packet = packetCreator.predict(sequence.method_41942());
         this.sendInstantly(packet);
         sequence.close();
      }
   }

   protected void sendSequenced(class_7204 packetCreator) {
      if (BlackOut.mc.field_1761 != null && BlackOut.mc.field_1687 != null) {
         class_7202 sequence = BlackOut.mc.field_1687.method_41925().method_41937();
         class_2596<?> packet = packetCreator.predict(sequence.method_41942());
         this.sendPacket(packet);
         sequence.close();
      }
   }

   protected void sendSequencedPostGrim(class_7204 packetCreator) {
      if (BlackOut.mc.field_1761 != null && BlackOut.mc.field_1687 != null) {
         class_7202 sequence = BlackOut.mc.field_1687.method_41925().method_41937();
         class_2596<?> packet = packetCreator.predict(sequence.method_41942());
         Managers.PACKET.sendPostPacket(packet);
         sequence.close();
      }
   }

   protected void placeBlock(class_1268 hand, PlaceData data) {
      boolean shouldSneak = data.sneak() && !BlackOut.mc.field_1724.method_5715();
      if (shouldSneak) {
         this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12979));
      }

      this.placeBlock(hand, data.pos().method_46558(), data.dir(), data.pos());
      if (shouldSneak) {
         this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12984));
      }

   }

   protected void placeBlock(class_1268 hand, class_243 blockHitVec, class_2350 blockDirection, class_2338 pos) {
      class_1268 finalHand = (class_1268)Objects.requireNonNullElse(hand, class_1268.field_5808);
      class_243 eyes = BlackOut.mc.field_1724.method_33571();
      boolean inside = eyes.field_1352 > (double)pos.method_10263() && eyes.field_1352 < (double)(pos.method_10263() + 1) && eyes.field_1351 > (double)pos.method_10264() && eyes.field_1351 < (double)(pos.method_10264() + 1) && eyes.field_1350 > (double)pos.method_10260() && eyes.field_1350 < (double)(pos.method_10260() + 1);
      SettingUtils.swing(SwingState.Pre, SwingType.Placing, finalHand);
      this.sendSequenced((s) -> {
         return new class_2885(finalHand, new class_3965(blockHitVec, blockDirection, pos, inside), s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Placing, finalHand);
   }

   protected void interactBlock(class_1268 hand, class_243 blockHitVec, class_2350 blockDirection, class_2338 pos) {
      class_1268 finalHand = (class_1268)Objects.requireNonNullElse(hand, class_1268.field_5808);
      class_243 eyes = BlackOut.mc.field_1724.method_33571();
      boolean inside = eyes.field_1352 > (double)pos.method_10263() && eyes.field_1352 < (double)(pos.method_10263() + 1) && eyes.field_1351 > (double)pos.method_10264() && eyes.field_1351 < (double)(pos.method_10264() + 1) && eyes.field_1350 > (double)pos.method_10260() && eyes.field_1350 < (double)(pos.method_10260() + 1);
      SettingUtils.swing(SwingState.Pre, SwingType.Interact, finalHand);
      this.sendSequenced((s) -> {
         return new class_2885(finalHand, new class_3965(blockHitVec, blockDirection, pos, inside), s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Interact, finalHand);
   }

   protected void useItem(class_1268 hand) {
      class_1268 finalHand = (class_1268)Objects.requireNonNullElse(hand, class_1268.field_5808);
      if (SettingUtils.grimUsing()) {
         this.sendPacket(new class_2830(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround()));
      }

      SettingUtils.swing(SwingState.Pre, SwingType.Using, finalHand);
      this.sendSequenced((s) -> {
         return new class_2886(finalHand, s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Using, finalHand);
   }

   protected void useItemInstantly(class_1268 hand) {
      class_1268 finalHand = (class_1268)Objects.requireNonNullElse(hand, class_1268.field_5808);
      if (SettingUtils.grimUsing()) {
         this.sendPacket(new class_2830(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround()));
      }

      SettingUtils.swing(SwingState.Pre, SwingType.Using, finalHand);
      this.sendSequencedInstantly((s) -> {
         return new class_2886(finalHand, s);
      });
      SettingUtils.swing(SwingState.Post, SwingType.Using, finalHand);
   }

   protected void releaseUseItem() {
      this.sendPacket(new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033, 0));
   }

   protected void attackEntity(class_1297 entity) {
      SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
      this.sendPacket(class_2824.method_34206(entity, BlackOut.mc.field_1724.method_5715()));
      SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
      if (entity instanceof class_1511) {
         Managers.ENTITY.setSemiDead(entity.method_5628());
      }

   }

   protected void clientSwing(SwingHand swingHand, class_1268 realHand) {
      class_1268 var10000;
      switch(swingHand) {
      case MainHand:
         var10000 = class_1268.field_5808;
         break;
      case OffHand:
         var10000 = class_1268.field_5810;
         break;
      case RealHand:
         var10000 = (class_1268)Objects.requireNonNullElse(realHand, class_1268.field_5808);
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      class_1268 hand = var10000;
      BlackOut.mc.field_1724.method_23667(hand, true);
      SwingModifier.getInstance().startSwing(hand);
   }

   protected void blockPlaceSound(class_2338 pos, class_1799 stack) {
      if (stack != null) {
         this.blockPlaceSound(pos, stack.method_7909());
      }
   }

   protected void blockPlaceSound(class_2338 pos, class_1792 item) {
      if (item instanceof class_1747) {
         class_1747 blockItem = (class_1747)item;
         this.blockPlaceSound(pos, blockItem);
      }
   }

   protected void blockPlaceSound(class_2338 pos, class_1747 blockItem) {
      BlackOut.mc.field_1687.method_8486((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D, blockItem.method_19260(BlackOut.mc.field_1687.method_8320(pos)), class_3419.field_15245, 1.0F, 1.0F, true);
   }

   protected void blockPlaceSound(class_2338 pos, class_1747 blockItem, float volume, float pitch, boolean distance) {
      BlackOut.mc.field_1687.method_8486((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + 0.5D, (double)pos.method_10260() + 0.5D, blockItem.method_19260(BlackOut.mc.field_1687.method_8320(pos)), class_3419.field_15245, volume, pitch, distance);
   }

   protected SettingGroup addGroup(String name) {
      SettingGroup group = new SettingGroup(name);
      this.settingGroups.add(group);
      return group;
   }

   protected SettingGroup addGroup(String name, String warning) {
      SettingGroup group = new WarningSettingGroup(name, warning);
      this.settingGroups.add(group);
      return group;
   }

   public void readSettings(JsonObject jsonObject) {
      this.settingGroups.forEach((group) -> {
         group.settings.forEach((s) -> {
            s.read(jsonObject);
         });
      });
   }

   public void writeSettings(JsonObject jsonObject) {
      this.settingGroups.forEach((group) -> {
         group.settings.forEach((s) -> {
            s.write(jsonObject);
         });
      });
   }

   public boolean shouldSkipListeners() {
      return !this.enabled;
   }

   protected void closeInventory() {
      this.sendPacket(new class_2815(BlackOut.mc.field_1724.field_7512.field_7763));
   }

   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         boolean var10000;
         if (object instanceof Module) {
            Module module = (Module)object;
            if (module.name.equals(this.name)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }
}
