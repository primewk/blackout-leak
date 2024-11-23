package bodevelopment.client.blackout.module.setting.multisettings;

import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import java.util.function.Function;
import net.minecraft.class_243;

public class ParticleMultiSetting {
   private static Function<Double, class_243> randomMotion = (speed) -> {
      double yaw = Math.random() * 2.0D * 3.141592653589793D;
      double pitch = Math.random() * 2.0D * 3.141592653589793D;
      double c = Math.abs(Math.cos(pitch));
      return new class_243(speed * Math.cos(yaw) * c, speed * -Math.sin(pitch), speed * Math.sin(yaw) * c);
   };
   public final Setting<ParticleMultiSetting.ParticleMode> mode;
   public final Setting<Integer> particles;
   public final Setting<Double> velocity;
   public final Setting<Double> time;
   public final Setting<Double> friction;
   public final Setting<BlackOutColor> color;
   public final Setting<BlackOutColor> shadowColor;

   public ParticleMultiSetting(SettingGroup sg, String name, SingleOut<Boolean> visible) {
      if (name == null) {
         name = "";
      } else {
         name = name + " ";
      }

      this.mode = sg.e(name + "Particle Mode", ParticleMultiSetting.ParticleMode.Normal, ".", visible);
      this.particles = sg.i(name + "Particles", 25, 0, 100, 1, ".", visible);
      this.velocity = sg.d(name + "Particle Velocity", 0.5D, 0.0D, 1.0D, 0.01D, ".", visible);
      this.time = sg.d(name + "Particle Time", 1.0D, 0.0D, 5.0D, 0.05D, ".", visible);
      this.friction = sg.d(name + "Particle Friction", 0.9D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.mode.get() == ParticleMultiSetting.ParticleMode.Normal && (Boolean)visible.get();
      });
      this.color = sg.c(name + "Particle Color", new BlackOutColor(255, 255, 255, 255), ".", visible);
      this.shadowColor = sg.c(name + "Particle Shadow Color", new BlackOutColor(255, 255, 255, 255), ".", visible);
   }

   public void spawnParticles(class_243 vec) {
      this.spawnParticles(vec, randomMotion);
   }

   public void spawnParticles(class_243 vec, Function<Double, class_243> getMotion) {
      for(int i = 0; i < (Integer)this.particles.get(); ++i) {
         this.spawnParticle(vec, (class_243)getMotion.apply((Double)this.velocity.get() * (0.75D + Math.random() * 0.25D)));
      }

   }

   private void spawnParticle(class_243 vec, class_243 motion) {
      switch((ParticleMultiSetting.ParticleMode)this.mode.get()) {
      case Normal:
         Managers.PARTICLE.addFriction(vec, motion, (Double)this.friction.get(), (Double)this.time.get(), ((BlackOutColor)this.color.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
         break;
      case Bouncy:
         Managers.PARTICLE.addBouncy(vec, motion, (Double)this.time.get(), ((BlackOutColor)this.color.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
      }

   }

   public static ParticleMultiSetting of(SettingGroup sg) {
      return of(sg, (String)null);
   }

   public static ParticleMultiSetting of(SettingGroup sg, String name) {
      return of(sg, name, () -> {
         return true;
      });
   }

   public static ParticleMultiSetting of(SettingGroup sg, String name, SingleOut<Boolean> visible) {
      return new ParticleMultiSetting(sg, name, visible);
   }

   public static enum ParticleMode {
      Normal,
      Bouncy;

      // $FF: synthetic method
      private static ParticleMultiSetting.ParticleMode[] $values() {
         return new ParticleMultiSetting.ParticleMode[]{Normal, Bouncy};
      }
   }
}
