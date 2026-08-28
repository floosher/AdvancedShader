package advancedshader.core.patcher.optifine;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.optifine.CustomSky")
public class CustomSkyPatcher extends Patcher {

    @MethodPatch("renderSky(Lamu;Lcdr;F)V")
    public void renderSky(MethodNode method) {
        patch("Add CUSTOM_SKY render stage configuration", method,
                ByteCode.Return(),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.GetStatic(RENDERSTAGE, "CUSTOM_SKY", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }
}
