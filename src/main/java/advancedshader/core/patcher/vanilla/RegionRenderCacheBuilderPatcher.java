package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("bum")
public class RegionRenderCacheBuilderPatcher extends Patcher {

    @MethodPatch("<init>()V")
    public void init(MethodNode method) {
        patch("Add renderer for Tripwire render layer", method,
                ByteCode.AAStore(),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetField("bum", "a", "[Lbuk;")),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "addTripwireRenderer", "([Lbuk;)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Return());
    }
}
