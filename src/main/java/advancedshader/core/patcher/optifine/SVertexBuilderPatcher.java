package advancedshader.core.patcher.optifine;

import org.lwjgl.opengl.GL11;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.optifine.shaders.SVertexBuilder")
public class SVertexBuilderPatcher extends Patcher {

    @MethodPatch("drawArrays(IIILbuk;)V")
    public void drawArrays(MethodNode method) {
        LabelNode singleTexture = ByteCode.Label();
        LabelNode drawEnd = ByteCode.Label();

        patch("Add check for calling drawMultiTexture", method,
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeVirtual("buk", "isMultiTexture", "()Z")),
                inject(ByteCode.IfZero(singleTexture)),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeVirtual("buk", "drawMultiTexture", "()V")),
                inject(ByteCode.Goto(drawEnd)),
                inject(singleTexture),
                ByteCode.ILoad(0),
                ByteCode.ILoad(1),
                ByteCode.ILoad(2),
                ByteCode.InvokeStatic("bus", "f", "(III)V"),
                inject(drawEnd));

        // Part 1 might be unnecessary, but just in case...?
        patch("Add gbuffers_line shader part 1", method,
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeStatic(LINESHADER, "preDrawArray", "(ILbuk;)I")),
                inject(ByteCode.IStore(0)),
                ByteCode.ILoad(0),
                ByteCode.ILoad(1),
                ByteCode.ILoad(2),
                ByteCode.InvokeStatic("bus", "f", "(III)V"),
                inject(ByteCode.InvokeStatic(LINESHADER, "postDrawArray", "()V")));

        patch("Add gbuffers_line shader part 2", method,
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeStatic(LINESHADER, "preDrawArray", "(ILbuk;)I")),
                inject(ByteCode.IStore(0)),
                ByteCode.ILoad(0),
                ByteCode.ILoad(1),
                ByteCode.ILoad(2),
                ByteCode.InvokeStatic("bus", "f", "(III)V"),
                inject(ByteCode.InvokeStatic(LINESHADER, "postDrawArray", "()V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                ByteCode.Return());

        patch("Add at_midBlock vertex attribute part 1", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "entityAttrib", "I"),
                ByteCode.IConst(3),
                ByteCode.SIPush(GL11.GL_SHORT),
                ByteCode.IConst(0),
                ByteCode.ILoad(5),
                ByteCode.ALoad(6),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glVertexAttribPointer", "(IIIZILjava/nio/ByteBuffer;)V"),
                inject(ByteCode.ILoad(5)),
                inject(ByteCode.ALoad(6)),
                inject(ByteCode.InvokeStatic(VERTEXATTRIBUTE, "setupMidBlockAttribPointer", "(ILjava/nio/ByteBuffer;)V")));

        patch("Add at_midBlock vertex attribute part 2", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "entityAttrib", "I"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glEnableVertexAttribArray", "(I)V"),
                inject(ByteCode.InvokeStatic(VERTEXATTRIBUTE, "enableMidBlockAttrib", "()V")));

        patch("Add at_midBlock vertex attribute part 3", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "entityAttrib", "I"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glDisableVertexAttribArray", "(I)V"),
                inject(ByteCode.InvokeStatic(VERTEXATTRIBUTE, "disableMidBlockAttrib", "()V")));
    }

    @MethodPatch("pushEntity(Lawt;Let;Lamy;Lbuk;)V")
    public void pushEntity(MethodNode method) {
        patch("Fix block.properties failing to match blocks correctly", method,
                remove(ByteCode.ILoad(4)),
                remove(ByteCode.ILoad(5)),
                remove(ByteCode.InvokeStatic("net/optifine/shaders/BlockAliases", "getBlockAliasId", "(II)I")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeStatic(BLOCKALIASFIX, "getBlockAliasId", "(Lawt;Let;Lamy;)I")),
                ByteCode.IStore(7),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.ILoad(7)),
                remove(ByteCode.IfLessThanZero(null)));
    }
}
