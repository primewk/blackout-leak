package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.Objects;
import net.minecraft.class_238;
import net.minecraft.class_4587;

public class CrystalChams extends Module {
   private static CrystalChams INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgSync = this.addGroup("Sync");
   public final Setting<Boolean> spawnAnimation;
   public final Setting<Double> animationTime;
   public final Setting<Double> scale;
   public final Setting<Double> bounce;
   public final Setting<Double> bounceSpeed;
   public final Setting<Double> rotationSpeed;
   public final Setting<Double> y;
   public final Setting<RenderShape> coreRenderShape;
   public final Setting<BlackOutColor> coreLineColor;
   public final Setting<BlackOutColor> coreSideColor;
   public final Setting<RenderShape> renderShape;
   public final Setting<BlackOutColor> lineColor;
   public final Setting<BlackOutColor> sideColor;
   public final Setting<RenderShape> outerRenderShape;
   public final Setting<BlackOutColor> outerLineColor;
   public final Setting<BlackOutColor> outerSideColor;
   public final Setting<Boolean> bounceSync;
   public final Setting<Boolean> rotationSync;
   public int age;
   private final class_238 box;

   public CrystalChams() {
      super("Crystal Chams", "Modifies the appearance of crystals.", SubCategory.ENTITIES, true);
      this.spawnAnimation = this.sgGeneral.b("Spawn Animation", false, "Try it and see.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.spawnAnimation;
      Objects.requireNonNull(var10008);
      this.animationTime = var10001.d("Animation Time", 0.5D, 0.0D, 1.0D, 0.01D, "Try it and see.", var10008::get);
      this.scale = this.sgGeneral.d("Scale", 1.0D, 0.0D, 10.0D, 0.1D, "Try it and see.");
      this.bounce = this.sgGeneral.d("Bounce", 0.5D, 0.0D, 10.0D, 0.1D, "Try it and see.");
      this.bounceSpeed = this.sgGeneral.d("Bounce Speed", 1.0D, 0.0D, 10.0D, 0.1D, "Try it and see.");
      this.rotationSpeed = this.sgGeneral.d("Rotation Speed", 1.0D, 0.0D, 10.0D, 0.1D, "Try it and see.");
      this.y = this.sgGeneral.d("Y", 0.0D, -5.0D, 5.0D, 0.1D, "Try it and see.");
      this.coreRenderShape = this.sgGeneral.e("Core Render Shape", RenderShape.Full, "Try it and see.");
      this.coreLineColor = this.sgGeneral.c("Core Line Color", new BlackOutColor(255, 0, 0, 255), "Try it and see.");
      this.coreSideColor = this.sgGeneral.c("Core Side Color", new BlackOutColor(255, 0, 0, 50), "Try it and see.");
      this.renderShape = this.sgGeneral.e("Middle Render Shape", RenderShape.Full, "Try it and see.");
      this.lineColor = this.sgGeneral.c("Middle Line Color", new BlackOutColor(255, 0, 0, 255), "Try it and see.");
      this.sideColor = this.sgGeneral.c("Middle Side Color", new BlackOutColor(255, 0, 0, 50), "Try it and see.");
      this.outerRenderShape = this.sgGeneral.e("Outer Render Shape", RenderShape.Full, "Try it and see.");
      this.outerLineColor = this.sgGeneral.c("Outer Line Color", new BlackOutColor(255, 0, 0, 255), "Try it and see.");
      this.outerSideColor = this.sgGeneral.c("Outer Side Color", new BlackOutColor(255, 0, 0, 50), "Try it and see.");
      this.bounceSync = this.sgSync.b("Bounce Sync", false, "Try it and see.");
      this.rotationSync = this.sgSync.b("Rotation Sync", false, "Try it and see.");
      this.age = 0;
      this.box = new class_238(-0.25D, -0.25D, -0.25D, 0.25D, 0.25D, 0.25D);
      INSTANCE = this;
   }

   public static CrystalChams getInstance() {
      return INSTANCE;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.age;
   }

   public void renderBox(class_4587 stack, int id) {
      Render3DUtils.box(stack, this.box, this.getSideColor(id), this.getLineColor(id), this.getShape(id));
   }

   private BlackOutColor getLineColor(int id) {
      BlackOutColor var10000;
      switch(id) {
      case 0:
         var10000 = (BlackOutColor)this.coreLineColor.get();
         break;
      case 1:
         var10000 = (BlackOutColor)this.lineColor.get();
         break;
      default:
         var10000 = (BlackOutColor)this.outerLineColor.get();
      }

      return var10000;
   }

   private BlackOutColor getSideColor(int id) {
      BlackOutColor var10000;
      switch(id) {
      case 0:
         var10000 = (BlackOutColor)this.coreSideColor.get();
         break;
      case 1:
         var10000 = (BlackOutColor)this.sideColor.get();
         break;
      default:
         var10000 = (BlackOutColor)this.outerSideColor.get();
      }

      return var10000;
   }

   private RenderShape getShape(int id) {
      RenderShape var10000;
      switch(id) {
      case 0:
         var10000 = (RenderShape)this.coreRenderShape.get();
         break;
      case 1:
         var10000 = (RenderShape)this.renderShape.get();
         break;
      default:
         var10000 = (RenderShape)this.outerRenderShape.get();
      }

      return var10000;
   }
}
