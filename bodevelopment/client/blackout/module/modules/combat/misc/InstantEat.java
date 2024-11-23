package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_2831;
import net.minecraft.class_2828.class_5911;

public class InstantEat extends Module {
   private static InstantEat INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<InstantEat.PacketMode> packetMode;
   private final Setting<Integer> packets;
   private final Setting<List<class_1792>> items;
   private final Setting<SwitchMode> switchMode;
   private final Predicate<class_1799> predicate;
   private int packetsSent;

   public InstantEat() {
      super("Instant Eat", "Instantly eats a food item (for 1.8)", SubCategory.MISC_COMBAT, true);
      this.packetMode = this.sgGeneral.e("Packet Mode", InstantEat.PacketMode.Full, ".");
      this.packets = this.sgGeneral.i("Packets", 32, 0, 50, 1, ".");
      this.items = this.sgGeneral.il("Items", ".", class_1802.field_8463);
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, ".");
      this.predicate = (itemStack) -> {
         return ((List)this.items.get()).contains(itemStack.method_7909());
      };
      this.packetsSent = 0;
      INSTANCE = this;
   }

   public static InstantEat getInstance() {
      return INSTANCE;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      this.disable(this.doStuff());
   }

   private String doStuff() {
      class_1268 hand = OLEPOSSUtils.getHand(this.predicate);
      if (hand == null) {
         FindResult result = ((SwitchMode)this.switchMode.get()).find(this.predicate);
         if (!result.wasFound() || !((SwitchMode)this.switchMode.get()).swapInstantly(result.slot())) {
            return "No item found";
         }
      }

      if (!BlackOut.mc.field_1724.method_6115()) {
         this.useItemInstantly(hand);
      }

      for(int i = 0; i < (Integer)this.packets.get(); ++i) {
         this.sendInstantly((class_2596)((InstantEat.PacketMode)this.packetMode.get()).supplier.get());
      }

      if (hand == null) {
         ((SwitchMode)this.switchMode.get()).swapBackInstantly();
      }

      return null;
   }

   public static enum PacketMode {
      Full(() -> {
         class_243 pos = Managers.PACKET.pos;
         return new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround());
      }),
      FullOffG(() -> {
         class_243 pos = Managers.PACKET.pos;
         return new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, false);
      }),
      Rotation(() -> {
         class_243 pos = Managers.PACKET.pos;
         return new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), Managers.ROTATION.prevYaw + ((InstantEat.getInstance().packetsSent & 1) == 0 ? 0.3759F : -0.2143F), Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround());
      }),
      DoubleRotation(() -> {
         return new class_2831(Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround());
      }),
      Position(() -> {
         class_243 pos = Managers.PACKET.pos;
         return new class_2829(pos.method_10216(), pos.method_10214(), pos.method_10215(), Managers.PACKET.isOnGround());
      }),
      Og(() -> {
         return new class_5911(Managers.PACKET.isOnGround());
      });

      private final Supplier<class_2828> supplier;

      private PacketMode(Supplier<class_2828> supplier) {
         this.supplier = supplier;
      }

      // $FF: synthetic method
      private static InstantEat.PacketMode[] $values() {
         return new InstantEat.PacketMode[]{Full, FullOffG, Rotation, DoubleRotation, Position, Og};
      }
   }
}
