package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("bid")
public class GameSettingsPatcher extends Patcher {

    @MethodPatch("setOptionFloatValueOF(Lbid$a;F)V")
    public void setOptionFloatValueOF(MethodNode method) {
        patch("Suppress anisotropic filtering option check for shaders", method,
                ByteCode.ILoad(3),
                remove(ByteCode.IConst(1)),
                inject(ByteCode.BIPush(16)),
                ByteCode.IfIntLessEqual(null),
                ByteCode.InvokeStatic("Config", "isShaders", "()Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Ldc("of.message.af.shaders1"));

        LabelNode label = ByteCode.Label();

        patch("Reload shaders after modifying anisotropic filtering", method,
                ByteCode.PutField("bid", "ofAfLevel", "I"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.GetField("bid", "at", "Lbib;"),
                ByteCode.InvokeVirtual("bib", "f", "()V"),
                inject(ByteCode.InvokeStatic("Config", "isShaders", "()Z")),
                inject(ByteCode.IfZero(label)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "uninit", "()V")),
                inject(label));
    }
}
