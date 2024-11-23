package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.ParticleMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.SoundUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_2596;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4587;
import net.minecraft.class_2824.class_5907;

public class HitEffects extends Module {
   private final SettingGroup sgEntities = this.addGroup("Entities");
   private final SettingGroup sgParticles = this.addGroup("Particles");
   private final SettingGroup sgHitSounds = this.addGroup("Hit Sounds");
   private final SettingGroup sgHitMarker = this.addGroup("Hit Marker");
   private final Setting<List<class_1299<?>>> entities;
   private final Setting<Boolean> particle;
   private final ParticleMultiSetting particles;
   private final Setting<Boolean> hitSound;
   public final Setting<HitEffects.Sound> sound;
   private final Setting<Double> volume;
   private final Setting<Double> pitch;
   private final Setting<Boolean> hitMarker;
   private final Setting<Integer> start;
   private final Setting<Integer> end;
   private final Setting<BlackOutColor> markerColor;
   private final class_4587 stack;
   private long startedDraw;

   public HitEffects() {
      super("Hit Effects", ",", SubCategory.MISC, true);
      this.entities = this.sgEntities.el("Entities", ".", class_1299.field_6097);
      this.particle = this.sgParticles.b("Draw Particles", false, ".");
      SettingGroup var10001 = this.sgParticles;
      Setting var10003 = this.particle;
      Objects.requireNonNull(var10003);
      this.particles = ParticleMultiSetting.of(var10001, (String)null, var10003::get);
      this.hitSound = this.sgHitSounds.b("Hit Sound", false, ".");
      var10001 = this.sgHitSounds;
      HitEffects.Sound var1 = HitEffects.Sound.NeverLose;
      Setting var10005 = this.hitSound;
      Objects.requireNonNull(var10005);
      this.sound = var10001.e("Sound", var1, ".", var10005::get);
      var10001 = this.sgHitSounds;
      Setting var10008 = this.hitSound;
      Objects.requireNonNull(var10008);
      this.volume = var10001.d("Volume", 1.0D, 0.0D, 10.0D, 0.1D, ".", var10008::get);
      var10001 = this.sgHitSounds;
      var10008 = this.hitSound;
      Objects.requireNonNull(var10008);
      this.pitch = var10001.d("Pitch", 1.0D, 0.0D, 10.0D, 0.1D, ".", var10008::get);
      this.hitMarker = this.sgHitMarker.b("Hit Marker", false, ".");
      var10001 = this.sgHitMarker;
      var10008 = this.hitMarker;
      Objects.requireNonNull(var10008);
      this.start = var10001.i("Start", 5, 0, 25, 1, ".", var10008::get);
      var10001 = this.sgHitMarker;
      var10008 = this.hitMarker;
      Objects.requireNonNull(var10008);
      this.end = var10001.i("End", 15, 0, 50, 1, ".", var10008::get);
      var10001 = this.sgHitMarker;
      BlackOutColor var2 = new BlackOutColor(175, 175, 175, 200);
      var10005 = this.hitMarker;
      Objects.requireNonNull(var10005);
      this.markerColor = var10001.c("Hit Marker Color", var2, ".", var10005::get);
      this.stack = new class_4587();
      this.startedDraw = System.currentTimeMillis();
   }

   @Event
   public void onSend(PacketEvent.Sent event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_2596 var3 = event.packet;
         if (var3 instanceof AccessorInteractEntityC2SPacket) {
            AccessorInteractEntityC2SPacket packet = (AccessorInteractEntityC2SPacket)var3;
            if (packet.getType().method_34211() == class_5907.field_29172) {
               class_1297 target = BlackOut.mc.field_1687.method_8469(packet.getId());
               if (target == null) {
                  return;
               }

               if (!((List)this.entities.get()).contains(target.method_5864()) || target == BlackOut.mc.field_1724) {
                  return;
               }

               this.playSounds(target);
               this.startedDraw = System.currentTimeMillis();
               if ((Boolean)this.particle.get()) {
                  this.particles.spawnParticles(BoxUtils.middle(target.method_5829()));
               }
            }
         }

      }
   }

   @Event
   public void onRender(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.drawHitMarker();
      }
   }

   private void playSounds(class_1297 target) {
      if ((Boolean)this.hitSound.get()) {
         switch((HitEffects.Sound)this.sound.get()) {
         case NeverLose:
            SoundUtils.play(((Double)this.pitch.get()).floatValue(), ((Double)this.volume.get()).floatValue(), "neverlose");
            break;
         case Skeet:
            SoundUtils.play(((Double)this.pitch.get()).floatValue(), ((Double)this.volume.get()).floatValue(), "skeet");
            break;
         case Waltuh:
            SoundUtils.play(((Double)this.pitch.get()).floatValue(), ((Double)this.volume.get()).floatValue(), "waltuh");
            break;
         case Critical:
            BlackOut.mc.field_1687.method_8486(target.method_23317(), target.method_23318() + 1.0D, target.method_23321(), class_3417.field_15016, class_3419.field_15248, ((Double)this.volume.get()).floatValue(), ((Double)this.pitch.get()).floatValue(), true);
         }

      }
   }

   private void drawHitMarker() {
      if ((Boolean)this.hitMarker.get()) {
         if (System.currentTimeMillis() - this.startedDraw <= 100L) {
            this.stack.method_22903();
            RenderUtils.unGuiScale(this.stack);
            this.stack.method_46416((float)BlackOut.mc.method_22683().method_4480() / 2.0F - 1.0F, (float)BlackOut.mc.method_22683().method_4507() / 2.0F - 1.0F, 0.0F);
            int s = (Integer)this.start.get();
            int e = (Integer)this.end.get();
            RenderUtils.fadeLine(this.stack, (float)s, (float)s, (float)e, (float)e, ((BlackOutColor)this.markerColor.get()).getRGB());
            RenderUtils.fadeLine(this.stack, (float)s, (float)(-s), (float)e, (float)(-e), ((BlackOutColor)this.markerColor.get()).getRGB());
            RenderUtils.fadeLine(this.stack, (float)(-s), (float)s, (float)(-e), (float)e, ((BlackOutColor)this.markerColor.get()).getRGB());
            RenderUtils.fadeLine(this.stack, (float)(-s), (float)(-s), (float)(-e), (float)(-e), ((BlackOutColor)this.markerColor.get()).getRGB());
            this.stack.method_22909();
         }
      }
   }

   public static enum Sound {
      Skeet,
      NeverLose,
      Waltuh,
      Critical;

      // $FF: synthetic method
      private static HitEffects.Sound[] $values() {
         return new HitEffects.Sound[]{Skeet, NeverLose, Waltuh, Critical};
      }
   }
}
