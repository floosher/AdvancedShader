package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("amm")
public class BlockRenderLayerPatcher extends Patcher {

    @MethodPatch("values()[Lamm;")
    public void values(MethodNode method) {
        patch("Newer version rendering mechanism compatibility fix", method,
                ByteCode.CheckCast("[Lamm;"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "getBlockRenderLayers", "([Lamm;)[Lamm;")),
                ByteCode.AReturn());
    }
}
