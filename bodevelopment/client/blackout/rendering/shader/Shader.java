package bodevelopment.client.blackout.rendering.shader;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_285;
import net.minecraft.class_287;
import net.minecraft.class_291;
import net.minecraft.class_287.class_7433;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

public class Shader {
   private final int[] currentShader = new int[1];
   private final Map<String, Shader.Uniform> uniformMap = new HashMap();
   private final List<String> absent = new ArrayList();
   private final int id;
   private final long initTime = System.currentTimeMillis();

   public Shader(String name) {
      this.id = ShaderReader.create(name);
   }

   public void render(class_287 bufferBuilder, ShaderSetup shaderSetup) {
      if (shaderSetup != null) {
         shaderSetup.setup(this);
      }

      class_7433 builtBuffer = bufferBuilder.method_1326();
      class_291 vertexBuffer = builtBuffer.method_43583().comp_749().method_43446();
      vertexBuffer.method_1353();
      vertexBuffer.method_1352(builtBuffer);
      this.draw(vertexBuffer);
   }

   private void draw(class_291 vertexBuffer) {
      this.setIf("ModelViewMat", RenderSystem.getModelViewMatrix());
      this.setIf("ProjMat", RenderSystem.getProjectionMatrix());
      this.setIf("uAlpha", Renderer.getAlpha());
      if (Renderer.getMatrices() != null) {
         this.setIf("uMatrices", Renderer.getMatrices().method_23760().method_23761());
         this.setIf("uMatrices2", Renderer.getMatrices().method_23760().method_23762());
      }

      this.setIf("uResolution", (float)BlackOut.mc.method_22683().method_4480(), (float)BlackOut.mc.method_22683().method_4507());
      this.timeIf(this.initTime);
      GL30C.glGetIntegerv(35725, this.currentShader);
      this.bind();
      vertexBuffer.method_35665();
      this.unbind();
      class_285.method_22094(this.currentShader[0]);
   }

   private boolean exists(String name) {
      if (this.absent.contains(name)) {
         return false;
      } else if (this.uniformMap.containsKey(name)) {
         return true;
      } else if (GL30C.glGetUniformLocation(this.id, name) == -1) {
         this.absent.add(name);
         return false;
      } else {
         return true;
      }
   }

   private Shader.Uniform getUniform(String uniform, int length, Shader.UniformType type) {
      return (Shader.Uniform)this.uniformMap.computeIfAbsent(uniform, (name) -> {
         return new Shader.Uniform(GL30C.glGetUniformLocation(this.id, name), type, length);
      });
   }

   public void bind() {
      class_285.method_22094(this.id);
      this.uniformMap.forEach((name, uniform) -> {
         uniform.upload();
      });
   }

   public void unbind() {
      class_285.method_22094(0);
   }

   public void set(String uniform, float f) {
      this.getUniform(uniform, 1, Shader.UniformType.Float).set(f);
   }

   public void set(String uniform, float x, float y) {
      this.getUniform(uniform, 2, Shader.UniformType.Float).set(x, y);
   }

   public void set(String uniform, float x, float y, float z) {
      this.getUniform(uniform, 3, Shader.UniformType.Float).set(x, y, z);
   }

   public void set(String uniform, float x, float y, float z, float a) {
      this.getUniform(uniform, 4, Shader.UniformType.Float).set(x, y, z, a);
   }

   public void set(String uniform, int i) {
      this.getUniform(uniform, 1, Shader.UniformType.Integer).set(i);
   }

   public void set(String uniform, Matrix4f matrix4f) {
      this.getUniform(uniform, 16, Shader.UniformType.Matrix).set(matrix4f);
   }

   public void time(long initTime) {
      this.set("time", (float)(System.currentTimeMillis() - initTime) / 1000.0F);
   }

   public void color(String uniform, int color) {
      this.set(uniform, (float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >>> 24) / 255.0F);
   }

   public void color(String uniform, BlackOutColor color) {
      this.set(uniform, (float)color.red / 255.0F, (float)color.green / 255.0F, (float)color.blue / 255.0F, (float)color.alpha / 255.0F);
   }

   public void setIf(String uniform, float f) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 1, Shader.UniformType.Float).set(f);
      }

   }

   public void setIf(String uniform, float x, float y) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 2, Shader.UniformType.Float).set(x, y);
      }

   }

   public void setIf(String uniform, float x, float y, float z) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 3, Shader.UniformType.Float).set(x, y, z);
      }

   }

   public void setIf(String uniform, float x, float y, float z, float a) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 4, Shader.UniformType.Float).set(x, y, z, a);
      }

   }

   public void setIf(String uniform, int i) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 1, Shader.UniformType.Integer).set(i);
      }

   }

   public void setIf(String uniform, Matrix3f matrix3f) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 9, Shader.UniformType.Matrix).set(matrix3f);
      }

   }

   public void setIf(String uniform, Matrix4f matrix4f) {
      if (this.exists(uniform)) {
         this.getUniform(uniform, 16, Shader.UniformType.Matrix).set(matrix4f);
      }

   }

   public void timeIf(long initTime) {
      if (this.exists("time")) {
         this.set("time", (float)(System.currentTimeMillis() - initTime) / 1000.0F);
      }

   }

   public void colorIf(String uniform, int color) {
      this.setIf(uniform, (float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >>> 24) / 255.0F);
   }

   public void colorIf(String uniform, BlackOutColor color) {
      this.setIf(uniform, (float)color.red / 255.0F, (float)color.green / 255.0F, (float)color.blue / 255.0F, (float)color.alpha / 255.0F);
   }

   private static enum UniformType {
      Integer,
      Float,
      Matrix;

      // $FF: synthetic method
      private static Shader.UniformType[] $values() {
         return new Shader.UniformType[]{Integer, Float, Matrix};
      }
   }

   private static class Uniform {
      private final int location;
      private float[] floatArray;
      private int[] intArray;
      private boolean boolValue;
      private final int length;
      private final Shader.UniformType type;

      private Uniform(int location, Shader.UniformType type, int length) {
         this.location = location;
         this.length = length;
         this.type = type;
         switch(type) {
         case Integer:
            this.floatArray = null;
            this.intArray = new int[length];
            break;
         case Float:
         case Matrix:
            this.floatArray = new float[length];
            this.intArray = null;
            break;
         default:
            throw new IllegalStateException("Unexpected value: " + type + "\n REPORT TO OLEPOSSU ON DISCORD");
         }

      }

      private void set(float... floats) {
         this.floatArray = floats;
      }

      private void set(int... ints) {
         this.intArray = ints;
      }

      private void set(Matrix4f matrix4f) {
         matrix4f.get(this.floatArray);
      }

      private void set(Matrix3f matrix3f) {
         matrix3f.get(this.floatArray);
      }

      private void set(boolean bool) {
         this.boolValue = bool;
      }

      private void upload() {
         switch(this.type) {
         case Integer:
            switch(this.length) {
            case 1:
               GL30C.glUniform1iv(this.location, this.intArray);
               return;
            case 2:
               GL30C.glUniform2iv(this.location, this.intArray);
               return;
            case 3:
               GL30C.glUniform3iv(this.location, this.intArray);
               return;
            case 4:
               GL30C.glUniform4iv(this.location, this.intArray);
               return;
            default:
               return;
            }
         case Float:
            switch(this.length) {
            case 1:
               GL30C.glUniform1fv(this.location, this.floatArray);
               return;
            case 2:
               GL30C.glUniform2fv(this.location, this.floatArray);
               return;
            case 3:
               GL30C.glUniform3fv(this.location, this.floatArray);
               return;
            case 4:
               GL30C.glUniform4fv(this.location, this.floatArray);
               return;
            default:
               return;
            }
         case Matrix:
            GL30C.glUniformMatrix4fv(this.location, false, this.floatArray);
         }

      }
   }
}
