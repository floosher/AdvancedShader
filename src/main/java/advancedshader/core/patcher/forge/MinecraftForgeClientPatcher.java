package advancedshader.core.patcher.forge;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.minecraftforge.client.MinecraftForgeClient")
public class MinecraftForgeClientPatcher extends Patcher {

    @MethodPatch("getRenderLayer()Lamm;")
    public void getRenderLayer(MethodNode method) {
        patch("TRIPWIRE compatibility with other mods", method,
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "getRenderLayer", "(Lamm;)Lamm;")),
                ByteCode.AReturn());
    }
}
