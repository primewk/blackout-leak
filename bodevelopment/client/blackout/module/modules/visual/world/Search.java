package bodevelopment.client.blackout.module.modules.visual.world;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.BlockStateEvent;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.framebuffer.FrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_1923;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2818;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import net.minecraft.class_631.class_3681;
import org.joml.Matrix4f;

public class Search extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> dynamicBox;
   private final Setting<Boolean> instantScan;
   private final Setting<Integer> scanSpeed;
   private final Setting<BlackOutColor> fillColor;
   private final Setting<Integer> bloom;
   private final Setting<BlackOutColor> bloomColor;
   private final Setting<Boolean> onlyExposed;
   private final List<class_2338> positions;
   private final List<class_1923> toScan;
   private final List<class_1923> prevChunks;

   public Search() {
      super("Search", "Highlights blocks.", SubCategory.WORLD, true);
      this.blocks = this.sgGeneral.bl("Blocks", "");
      this.dynamicBox = this.sgGeneral.b("Dynamic Box", true, ".");
      this.instantScan = this.sgGeneral.b("Instant Scan", false, ".");
      this.scanSpeed = this.sgGeneral.i("Scan Speed", 1, 1, 10, 1, "Chunks per frame.", () -> {
         return !(Boolean)this.instantScan.get();
      });
      this.fillColor = this.sgGeneral.c("Fill Color", new BlackOutColor(255, 0, 0, 50), "");
      this.bloom = this.sgGeneral.i("Bloom", 5, 0, 10, 1, ".");
      this.bloomColor = this.sgGeneral.c("Bloom Color", new BlackOutColor(255, 0, 0, 100), "");
      this.onlyExposed = this.sgGeneral.b("Only Exposed", false, ".");
      this.positions = Collections.synchronizedList(new ArrayList());
      this.toScan = new ArrayList();
      this.prevChunks = new ArrayList();
   }

   public void onEnable() {
      this.reset();
   }

   @Event
   public void onJoin(GameJoinEvent event) {
      this.reset();
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null) {
         this.checkChunks();
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.find();
      this.render();
   }

   @Event
   public void onRenderHud(RenderEvent.Hud.Pre event) {
      FrameBuffer buffer = Managers.FRAME_BUFFER.getBuffer("search");
      FrameBuffer bloomBuffer = Managers.FRAME_BUFFER.getBuffer("search-bloom");
      RenderUtils.renderBufferWith(buffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
         setup.color("clr", ((BlackOutColor)this.fillColor.get()).getRGB());
      }));
      if ((Integer)this.bloom.get() > 0) {
         bloomBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
         bloomBuffer.bind(true);
         RenderUtils.renderBufferWith(buffer, Shaders.screentex, new ShaderSetup((setup) -> {
            setup.set("alpha", 1.0F);
         }));
         bloomBuffer.unbind();
         RenderUtils.blurBufferBW("search-bloom", (Integer)this.bloom.get() + 1);
         bloomBuffer.bind(true);
         Renderer.setTexture(buffer.getTexture(), 1);
         RenderUtils.renderBufferWith(bloomBuffer, Shaders.subtract, new ShaderSetup((setup) -> {
            setup.set("uTexture0", 0);
            setup.set("uTexture1", 1);
         }));
         bloomBuffer.unbind();
         RenderUtils.renderBufferWith(bloomBuffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
            setup.color("clr", ((BlackOutColor)this.bloomColor.get()).getRGB());
         }));
      }
   }

   @Event
   public void onState(BlockStateEvent event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.onBlock(event.state.method_26204(), event.pos, true);
      }

   }

   private void reset() {
      this.prevChunks.clear();
      this.positions.clear();
   }

   private void checkChunks() {
      List<class_1923> current = new ArrayList();
      class_3681 map = BlackOut.mc.field_1687.method_2935().field_16246;

      for(int i = 0; i < map.field_16251.length(); ++i) {
         class_2818 chunk = (class_2818)map.field_16251.get(i);
         if (chunk != null) {
            class_1923 pos = chunk.method_12004();
            if (!this.prevChunks.contains(pos)) {
               this.addScan(pos);
            }

            this.prevChunks.remove(pos);
            current.add(pos);
         }
      }

      this.prevChunks.forEach(this::unScan);
      this.prevChunks.clear();
      this.prevChunks.addAll(current);
   }

   private void unScan(class_1923 pos) {
      this.toScan.remove(pos);
      this.positions.removeIf((block) -> {
         return block.method_10263() >= pos.method_8326() && block.method_10263() <= pos.method_8327() && block.method_10260() >= pos.method_8328() && block.method_10260() <= pos.method_8329();
      });
   }

   private void render() {
      FrameBuffer buffer = Managers.FRAME_BUFFER.getBuffer("search");
      buffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
      buffer.bind(true);
      Render3DUtils.matrices.method_22903();
      Render3DUtils.setRotation(Render3DUtils.matrices);
      Render3DUtils.start();
      Matrix4f matrix4f = Render3DUtils.matrices.method_23760().method_23761();
      class_243 camPos = BlackOut.mc.field_1773.method_19418().method_19326();
      RenderSystem.setShader(class_757::method_34540);
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27382, class_290.field_1576);
      this.positions.forEach((pos) -> {
         this.renderBox(bufferBuilder, matrix4f, pos, camPos);
      });
      class_286.method_43433(bufferBuilder.method_1326());
      Render3DUtils.end();
      Render3DUtils.matrices.method_22909();
      buffer.unbind();
   }

   private void renderBox(class_287 bufferBuilder, Matrix4f matrix4f, class_2338 pos, class_243 camPos) {
      class_238 box = this.getBox(pos);
      float minX = (float)(box.field_1323 - camPos.field_1352);
      float maxX = (float)(box.field_1320 - camPos.field_1352);
      float minY = (float)(box.field_1322 - camPos.field_1351);
      float maxY = (float)(box.field_1325 - camPos.field_1351);
      float minZ = (float)(box.field_1321 - camPos.field_1350);
      float maxZ = (float)(box.field_1324 - camPos.field_1350);
      this.drawQuads(bufferBuilder, matrix4f, minX, maxX, minY, maxY, minZ, maxZ);
   }

   private void drawQuads(class_287 bufferBuilder, Matrix4f matrix4f, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
      if (minY > 0.0F) {
         this.vertex(bufferBuilder, matrix4f, minX, minY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, minY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, minY, maxZ);
         this.vertex(bufferBuilder, matrix4f, minX, minY, maxZ);
      } else if (maxY < 0.0F) {
         this.vertex(bufferBuilder, matrix4f, minX, maxY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, maxY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, maxY, maxZ);
         this.vertex(bufferBuilder, matrix4f, minX, maxY, maxZ);
      }

      if (minX > 0.0F) {
         this.vertex(bufferBuilder, matrix4f, minX, minY, minZ);
         this.vertex(bufferBuilder, matrix4f, minX, maxY, minZ);
         this.vertex(bufferBuilder, matrix4f, minX, maxY, maxZ);
         this.vertex(bufferBuilder, matrix4f, minX, minY, maxZ);
      } else if (maxX < 0.0F) {
         this.vertex(bufferBuilder, matrix4f, maxX, minY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, maxY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, maxY, maxZ);
         this.vertex(bufferBuilder, matrix4f, maxX, minY, maxZ);
      }

      if (minZ > 0.0F) {
         this.vertex(bufferBuilder, matrix4f, minX, minY, minZ);
         this.vertex(bufferBuilder, matrix4f, minX, maxY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, maxY, minZ);
         this.vertex(bufferBuilder, matrix4f, maxX, minY, minZ);
      } else if (maxZ < 0.0F) {
         this.vertex(bufferBuilder, matrix4f, minX, minY, maxZ);
         this.vertex(bufferBuilder, matrix4f, minX, maxY, maxZ);
         this.vertex(bufferBuilder, matrix4f, maxX, maxY, maxZ);
         this.vertex(bufferBuilder, matrix4f, maxX, minY, maxZ);
      }

   }

   private void vertex(class_287 bufferBuilder, Matrix4f matrix4f, float x, float y, float z) {
      bufferBuilder.method_22918(matrix4f, x, y, z).method_22915(1.0F, 1.0F, 1.0F, 1.0F).method_22914(0.0F, 0.0F, 0.0F).method_1344();
   }

   private class_238 getBox(class_2338 pos) {
      if ((Boolean)this.dynamicBox.get()) {
         class_265 shape = BlackOut.mc.field_1687.method_8320(pos).method_26218(BlackOut.mc.field_1687, pos);
         if (!shape.method_1110()) {
            return shape.method_1107().method_996(pos);
         }
      }

      return BoxUtils.get(pos);
   }

   private void find() {
      if ((Boolean)this.instantScan.get()) {
         this.toScan.forEach(this::scan);
         this.toScan.clear();
      } else {
         for(int i = 0; i < (Integer)this.scanSpeed.get(); ++i) {
            if (this.toScan.isEmpty()) {
               return;
            }

            this.scan((class_1923)this.toScan.get(0));
            this.toScan.remove(0);
         }
      }

   }

   private void scan(class_1923 pos) {
      for(int x = pos.method_8326(); x <= pos.method_8327(); ++x) {
         for(int y = -64; y <= 319; ++y) {
            for(int z = pos.method_8328(); z <= pos.method_8329(); ++z) {
               class_2338 p = new class_2338(x, y, z);
               if (((List)this.blocks.get()).contains(BlackOut.mc.field_1687.method_8320(p).method_26204())) {
                  this.positions.add(p);
               }
            }
         }
      }

   }

   private void addScan(class_1923 pos) {
      if (!this.toScan.contains(pos)) {
         this.toScan.add(pos);
      }

   }

   private void onBlock(class_2248 block, class_2338 pos, boolean first) {
      boolean valid = ((List)this.blocks.get()).contains(block);
      class_2350[] var5;
      int var6;
      int var7;
      class_2350 dir;
      class_2338 offsetPos;
      if (valid && (Boolean)this.onlyExposed.get()) {
         valid = false;
         var5 = class_2350.values();
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            dir = var5[var7];
            offsetPos = pos.method_10093(dir);
            if (!BlackOut.mc.field_1687.method_8320(offsetPos).method_26204().field_23155.field_20721) {
               valid = true;
               break;
            }
         }
      }

      if (valid) {
         if (!this.positions.contains(pos)) {
            this.positions.add(pos);
         }
      } else {
         this.positions.remove(pos);
      }

      if (first) {
         var5 = class_2350.values();
         var6 = var5.length;

         for(var7 = 0; var7 < var6; ++var7) {
            dir = var5[var7];
            offsetPos = pos.method_10093(dir);
            this.onBlock(BlackOut.mc.field_1687.method_8320(offsetPos).method_26204(), offsetPos, false);
         }
      }

   }
}
