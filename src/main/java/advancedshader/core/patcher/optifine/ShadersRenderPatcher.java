package advancedshader.core.patcher.optifine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.optifine.shaders.ShadersRender")
public class ShadersRenderPatcher extends Patcher {

    @MethodPatch("renderShadowMap(Lbuq;IFJ)V")
    public void renderShadowMap(MethodNode method) {
        LabelNode label = (LabelNode) patch("Add shadowTerrain config part 1", method,
                ByteCode.Ldc("shadow terrain cutout"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "checkGLError", "(Ljava/lang/String;)I"),
                ByteCode.Pop(),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(5),
                ByteCode.InvokeVirtual("bib", "N", "()Lcdr;"),
                ByteCode.GetStatic("cdp", "g", "Lnf;"),
                ByteCode.InvokeVirtual("cdr", "b", "(Lnf;)Lcds;"),
                ByteCode.InvokeInterface("cds", "a", "()V"),
                collect(ByteCode.Label()))[0];

        patch("Add shadowTerrain config part 2", method,
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowTerrain", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "isFalse", "()Z")),
                inject(ByteCode.IfNotZero(label)),
                ByteCode.InvokeStatic("bus", "d", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(6),
                ByteCode.GetStatic("amm", "a", "Lamm;"),
                ByteCode.FLoad(2),
                ByteCode.F2D(),
                ByteCode.IConst(2),
                ByteCode.ALoad(9),
                ByteCode.InvokeVirtual("buy", "a", "(Lamm;DILvg;)I"),
                ByteCode.Pop());

        patch("Configure shadow texture glClear", method,
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glClear", "(I)V")),
                inject(ByteCode.Pop()),
                inject(ByteCode.InvokeStatic(MORESTAGES, "clearShadowMap", "()V")));

        patch("Add ShadowComp shader", method,
                ByteCode.Ldc("shadow postprocess"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "checkGLError", "(Ljava/lang/String;)I"),
                ByteCode.Pop(),
                inject(ByteCode.InvokeStatic(MORESTAGES, "renderShadowComp", "()V")));

        patch("Execute compute shader", method,
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramShadow", "Lnet/optifine/shaders/Program;")),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "dispatchComputes", "(Lnet/optifine/shaders/Program;)V")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramShadow", "Lnet/optifine/shaders/Program;"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"));

        patch("Camera coordinate fix", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "preShadowPassThirdPersonView", "I"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.ALoad(5)),
                remove(ByteCode.GetField("bib", "t", "Lbid;")),
                remove(ByteCode.IConst(1)),
                remove(ByteCode.PutField("bid", "aw", "I")));
    }

    @MethodPatch("preRenderChunkLayer(Lamm;)V")
    public void preRenderChunkLayer(MethodNode method) {
        patch("Bind at_midBlock vertex attribute", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "entityAttrib", "I"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glEnableVertexAttribArray", "(I)V"),
                inject(ByteCode.GetStatic(HOOK, "midBlockAttrib", "I")),
                inject(ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glEnableVertexAttribArray", "(I)V")));
    }

    @MethodPatch("postRenderChunkLayer(Lamm;)V")
    public void postRenderChunkLayer(MethodNode method) {
        patch("Unbind at_midBlock vertex attribute", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "entityAttrib", "I"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glDisableVertexAttribArray", "(I)V"),
                inject(ByteCode.GetStatic(HOOK, "midBlockAttrib", "I")),
                inject(ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glDisableVertexAttribArray", "(I)V")));
    }

    @MethodPatch("setupArrayPointersVbo()V")
    public void setupArrayPointersVbo(MethodNode method) {
        patch("Configure at_midBlock vertex attribute", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "entityAttrib", "I"),
                ByteCode.IConst(3),
                ByteCode.SIPush(GL11.GL_SHORT),
                ByteCode.IConst(0),
                ByteCode.BIPush(56),
                ByteCode.Ldc(48L),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glVertexAttribPointer", "(IIIZIJ)V"),
                inject(ByteCode.GetStatic(HOOK, "midBlockAttrib", "I")),
                inject(ByteCode.IConst(3)),
                inject(ByteCode.SIPush(GL11.GL_BYTE)),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.BIPush(56)),
                inject(ByteCode.Ldc(52L)),
                inject(ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glVertexAttribPointer", "(IIIZIJ)V")));
    }

    @MethodPatch("beginTerrainSolid()V")
    public void beginTerrainSolid(MethodNode method) {
        patch("Add TERRAIN_SOLID render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "TERRAIN_SOLID", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginTerrainCutoutMipped()V")
    public void beginTerrainCutoutMipped(MethodNode method) {
        patch("Add TERRAIN_CUTOUT_MIPPED render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "TERRAIN_CUTOUT_MIPPED", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginTerrainCutout()V")
    public void beginTerrainCutout(MethodNode method) {
        patch("Add TERRAIN_CUTOUT render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "TERRAIN_CUTOUT", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginBlockDamage()V")
    public void beginBlockDamage(MethodNode method) {
        patch("Add DESTROY render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "DESTROY", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("endTerrain()V")
    @MethodPatch("endBlockDamage()V")
    public void resetRenderStage(MethodNode method) {
        patch("Reset render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }
}
