package advancedshader.core.patcher.optifine;

import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.optifine.shaders.config.ShaderParser")
public class ShaderParserPatcher extends Patcher {

    @MethodPatch("<clinit>()V")
    public void clinit(MethodNode method) {
        patch("Modify numerical range accepted by DRAWBUFFERS regex", method,
                remove(ByteCode.Ldc("[0-7N]*")),
                inject(ByteCode.Ldc("[0-9N]*")));

        patch("Allow RENDERTARGETS value to contain commas", method,
                remove(ByteCode.Ldc("\\s*(/\\*|//)?\\s*([A-Z]+):\\s*(\\w+)\\s*(\\*/.*|\\s*)")),
                inject(ByteCode.Ldc("\\s*(/\\*|//)?\\s*([A-Z]+):\\s*([\\w.,]+)\\s*(\\*/.*|\\s*)")));

        patch("Add recognition for Prepare and ShadowComp shaders", method,
                remove(ByteCode.Ldc(".*deferred[0-9]*\\.fsh")),
                inject(ByteCode.Ldc(".*(?:deferred|prepare|shadowcomp)[0-9]*\\.fsh")));

        patch("Recognize 'in' vertex attributes", method,
                remove(ByteCode.Ldc("\\s*attribute\\s+\\w+\\s+(\\w+).*")),
                inject(ByteCode.Ldc("\\s*(?:in|attribute)\\s+\\w+\\s+(\\w+).*")));

        patch("Recognize uniforms with layout qualifiers", method,
                remove(ByteCode.Ldc("\\s*uniform\\s+\\w+\\s+(\\w+).*")),
                inject(ByteCode.Ldc("[\\w\\s(,=)]*uniform\\s+\\w+\\s+(\\w+).*")));
    }

    @MethodPatch("getColorIndex(Ljava/lang/String;)I")
    public void getColorIndex(MethodNode method) {
        patch("Modify max colortex (7 -> 15)", method,
                remove(ByteCode.BIPush(7)),
                inject(ByteCode.BIPush(15)));
    }

    @MethodPatch("getIndex(Ljava/lang/String;Ljava/lang/String;II)I")
    public void getIndex(MethodNode method) {
        patch("Adapt larger index part 1", method,
                remove(ByteCode.ALoad(0)),
                remove(ByteCode.InvokeVirtual("java/lang/String", "length", "()I")),
                remove(ByteCode.ALoad(1)),
                remove(ByteCode.InvokeVirtual("java/lang/String", "length", "()I")),
                remove(ByteCode.IConst(1)),
                remove(ByteCode.IAdd()),
                remove(ByteCode.IfIntEqual(null)),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.IConst(-1)),
                remove(ByteCode.IReturn()));

        patch("Adapt larger index part 2", method,
                ByteCode.ALoad(0),
                ByteCode.ALoad(1),
                ByteCode.InvokeVirtual("java/lang/String", "length", "()I"),
                remove(ByteCode.InvokeVirtual("java/lang/String", "charAt", "(I)C")),
                remove(ByteCode.BIPush(48)),
                remove(ByteCode.ISub()),
                inject(ByteCode.InvokeVirtual("java/lang/String", "substring", "(I)Ljava/lang/String;")),
                inject(ByteCode.IConst(-1)),
                inject(ByteCode.InvokeStatic("Config", "parseInt", "(Ljava/lang/String;I)I")));
    }
}
