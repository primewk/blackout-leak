package bodevelopment.client.blackout.util.render;

import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4597;

public class ModelVertexConsumerProvider implements class_4597 {
   public final ModelVertexConsumer consumer = new ModelVertexConsumer();

   public class_4588 getBuffer(class_1921 layer) {
      return this.consumer;
   }
}
