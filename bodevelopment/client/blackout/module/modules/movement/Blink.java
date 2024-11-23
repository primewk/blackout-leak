package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.defensive.Surround;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class Blink extends Module {
   private static Blink INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Blink.BlinkMode> blinkMode;
   private final Setting<Integer> packets;
   private final Setting<Integer> ticks;
   private final Setting<Boolean> disableSurround;
   private final Setting<Boolean> render;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private int delayed;
   private class_238 box;
   private int time;

   public Blink() {
      super("Blink", "Basically fakes huge lag.", SubCategory.MOVEMENT, true);
      this.blinkMode = this.sgGeneral.e("Mode", Blink.BlinkMode.Normal, ".");
      this.packets = this.sgGeneral.i("Packets", 10, 0, 50, 1, "Disabled after sending this many packets.");
      this.ticks = this.sgGeneral.i("Ticks", 100, 0, 100, 1, ".");
      this.disableSurround = this.sgGeneral.b("Disable On Surround", false, ".");
      this.render = this.sgGeneral.b("Render", true, ".");
      this.renderShape = this.sgGeneral.e("Render Shape", RenderShape.Full, "Which parts should be rendered.");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideColor = this.sgGeneral.c("Side Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.delayed = 0;
      this.box = null;
      this.time = 0;
      INSTANCE = this;
   }

   public static Blink getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.delayed = 0;
      this.time = 0;
      if (BlackOut.mc.field_1724 != null) {
         this.box = BlackOut.mc.field_1724.method_5829();
         class_243 serverPos = Managers.PACKET.pos;
         this.box = new class_238(serverPos.field_1352 - this.box.method_17939() / 2.0D, serverPos.field_1351, serverPos.field_1350 - this.box.method_17941() / 2.0D, serverPos.field_1352 + this.box.method_17939() / 2.0D, serverPos.field_1351 + this.box.method_17940(), serverPos.field_1350 + this.box.method_17941() / 2.0D);
      }

   }

   public String getInfo() {
      int var10000 = this.delayed;
      return var10000 + "/" + this.packets.get();
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.time;
      if (BlackOut.mc.field_1724 == null || BlackOut.mc.field_1687 == null || (Integer)this.ticks.get() > 0 && this.time > (Integer)this.ticks.get()) {
         this.disable();
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (this.enabled) {
         if ((Boolean)this.disableSurround.get() && Surround.getInstance().enabled) {
            this.disable(this.getDisplayName() + "disabled, enabled surround");
         }

         if (this.box != null && (Boolean)this.render.get()) {
            Render3DUtils.box(this.box, (BlackOutColor)this.sideColor.get(), (BlackOutColor)this.lineColor.get(), (RenderShape)this.renderShape.get());
         }

      }
   }

   public boolean onSend() {
      if (!this.shouldDelay()) {
         return false;
      } else {
         ++this.delayed;
         if ((Integer)this.packets.get() > 0 && this.delayed >= (Integer)this.packets.get()) {
            if (this.blinkMode.get() == Blink.BlinkMode.Normal) {
               String var10001 = this.getDisplayName();
               this.disable(var10001 + " reached the limit of " + this.packets.get() + " packets");
            }

            return true;
         } else {
            return true;
         }
      }
   }

   public boolean shouldDelay() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         boolean var10000;
         switch((Blink.BlinkMode)this.blinkMode.get()) {
         case Damage:
            var10000 = BlackOut.mc.field_1724.field_6235 > 0 && ((Integer)this.packets.get() == 0 || BlackOut.mc.field_1724.field_6235 < (Integer)this.packets.get());
            break;
         case Normal:
            var10000 = true;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         return var10000;
      } else {
         return false;
      }
   }

   public static enum BlinkMode {
      Damage,
      Normal;

      // $FF: synthetic method
      private static Blink.BlinkMode[] $values() {
         return new Blink.BlinkMode[]{Damage, Normal};
      }
   }
}
