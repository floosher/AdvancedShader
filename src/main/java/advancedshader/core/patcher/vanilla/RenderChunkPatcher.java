package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("bxr")
public class RenderChunkPatcher extends Patcher {

    @MethodPatch("fixBlockLayer(Lawt;Lamm;)Lamm;")
    public void fixBlockLayer(MethodNode method) {
        patch("Change tripwire block render layer to Tripwire", method,
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "replaceRenderLayer", "(Lawt;Lamm;)Lamm;")), // Trust me, Pilot (obfuscation does not matter here = =)
                inject(ByteCode.AStore(2)),
                method.instructions.getFirst());
    }

    @MethodPatch("g()V")
    public void multModelviewMatrix(MethodNode method) {
        patch("Remove obscure chunk scaling (may affect ray tracing)", method,
                remove(ByteCode.ALoad(0)),
                remove(ByteCode.GetField("bxr", "k", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.InvokeStatic("bus", "a", "(Ljava/nio/FloatBuffer;)V")));
    }
}
