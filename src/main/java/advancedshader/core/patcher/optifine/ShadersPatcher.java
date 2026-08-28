package advancedshader.core.patcher.optifine;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("net.optifine.shaders.Shaders")
public class ShadersPatcher extends Patcher {

    @MethodPatch("<clinit>()V")
    public void clinit(MethodNode method) {
        patch("Add more uniforms", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderUniforms", "Lnet/optifine/shaders/uniform/ShaderUniforms;"),
                ByteCode.Ldc("instanceId"),
                ByteCode.InvokeVirtual("net/optifine/shaders/uniform/ShaderUniforms", "make1i", "(Ljava/lang/String;)Lnet/optifine/shaders/uniform/ShaderUniform1i;"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "uniform_instanceId", "Lnet/optifine/shaders/uniform/ShaderUniform1i;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderUniforms", "Lnet/optifine/shaders/uniform/ShaderUniforms;")),
                inject(ByteCode.InvokeStatic(HOOK, "addUniforms", "(Lnet/optifine/shaders/uniform/ShaderUniforms;)V")));

        patch("Add more programs", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "programs", "Lnet/optifine/shaders/Programs;"),
                ByteCode.Ldc("final"),
                ByteCode.InvokeVirtual("net/optifine/shaders/Programs", "makeComposite", "(Ljava/lang/String;)Lnet/optifine/shaders/Program;"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "ProgramFinal", "Lnet/optifine/shaders/Program;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "programs", "Lnet/optifine/shaders/Programs;")),
                inject(ByteCode.InvokeStatic(HOOK, "addPrograms", "(Lnet/optifine/shaders/Programs;)V")));

        patch("Add colortex8-15 texture configuration", method,
                remove(ByteCode.BIPush(8)),
                inject(ByteCode.BIPush(16)),
                ByteCode.NewArray(ByteCode.T_INT),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "gbuffersFormat", "[I"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.BIPush(8)),
                inject(ByteCode.BIPush(16)),
                ByteCode.NewArray(ByteCode.T_BOOLEAN),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "gbuffersClear", "[Z"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.BIPush(8)),
                inject(ByteCode.BIPush(16)),
                ByteCode.NewArray("org/lwjgl/util/vector/Vector4f"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "gbuffersClearColor", "[Lorg/lwjgl/util/vector/Vector4f;"));

        patch("Allocate texture units for colortex8-15", method,
                remove(ByteCode.BIPush(8)),
                inject(ByteCode.BIPush(16)),
                ByteCode.NewArray(ByteCode.T_INT),
                ByteCode.Dup(),
                ByteCode.IConst(0),
                ByteCode.IConst(0),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(1),
                ByteCode.IConst(1),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(2),
                ByteCode.IConst(2),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(3),
                ByteCode.IConst(3),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(4),
                ByteCode.BIPush(7),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(5),
                ByteCode.BIPush(8),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.BIPush(6),
                ByteCode.BIPush(9),
                ByteCode.IAStore(),
                ByteCode.Dup(),
                ByteCode.BIPush(7),
                ByteCode.BIPush(10),
                ByteCode.IAStore(),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(8)),
                inject(ByteCode.BIPush(16)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(9)),
                inject(ByteCode.BIPush(17)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(10)),
                inject(ByteCode.BIPush(18)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(11)),
                inject(ByteCode.BIPush(19)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(12)),
                inject(ByteCode.BIPush(20)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(13)),
                inject(ByteCode.BIPush(21)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(14)),
                inject(ByteCode.BIPush(22)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(15)),
                inject(ByteCode.BIPush(23)),
                inject(ByteCode.IAStore()),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I"));

        patch("Expand continuous address buffer", method,
                remove(ByteCode.SIPush(285)),
                inject(ByteCode.SIPush(285 + 8 * 2)), // 8 dfb texture ping-pong
                ByteCode.BIPush(8),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramCount", "I"),
                ByteCode.IMul(),
                ByteCode.IAdd(),
                ByteCode.IConst(4),
                ByteCode.IMul(),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "bigBufferSize", "I"));

        patch("Increase framebuffer texture count to 32", method,
                remove(ByteCode.BIPush(16)),
                inject(ByteCode.BIPush(32)),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "nextIntBuffer", "(I)Ljava/nio/IntBuffer;"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "dfbColorTextures", "Ljava/nio/IntBuffer;"));

        patch("Increase Ping-Pong framebuffer count to 16", method,
                ByteCode.New("net/optifine/shaders/FlipTextures"),
                ByteCode.Dup(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTextures", "Ljava/nio/IntBuffer;"),
                remove(ByteCode.BIPush(8)),
                inject(ByteCode.BIPush(16)),
                ByteCode.InvokeSpecial("net/optifine/shaders/FlipTextures", "<init>", "(Ljava/nio/IntBuffer;I)V"));

        patch("100 Deferred shaders", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "programs", "Lnet/optifine/shaders/Programs;"),
                ByteCode.Ldc("deferred"),
                remove(ByteCode.BIPush(16)),
                inject(ByteCode.BIPush(100)),
                ByteCode.InvokeVirtual("net/optifine/shaders/Programs", "makeDeferreds", "(Ljava/lang/String;I)[Lnet/optifine/shaders/Program;"));

        patch("100 Composite shaders", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "programs", "Lnet/optifine/shaders/Programs;"),
                ByteCode.Ldc("composite"),
                remove(ByteCode.BIPush(16)),
                inject(ByteCode.BIPush(100)),
                ByteCode.InvokeVirtual("net/optifine/shaders/Programs", "makeComposites", "(Ljava/lang/String;I)[Lnet/optifine/shaders/Program;"));

        patch("Add 8bits and 16bits integer texture formats part 1", method,
                remove(ByteCode.BIPush(37)),
                inject(ByteCode.BIPush(53)),
                ByteCode.NewArray("java/lang/String"),
                ByteCode.Dup(),
                ByteCode.IConst(0),
                ByteCode.Ldc("R8"));

        patch("Add 8bits and 16bits integer texture formats part 2", method,
                ByteCode.Ldc("RGB9_E5"),
                ByteCode.AAStore(),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(37)),
                inject(ByteCode.Ldc("R8I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(38)),
                inject(ByteCode.Ldc("RG8I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(39)),
                inject(ByteCode.Ldc("RGB8I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(40)),
                inject(ByteCode.Ldc("RGBA8I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(41)),
                inject(ByteCode.Ldc("R8UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(42)),
                inject(ByteCode.Ldc("RG8UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(43)),
                inject(ByteCode.Ldc("RGB8UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(44)),
                inject(ByteCode.Ldc("RGBA8UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(45)),
                inject(ByteCode.Ldc("R16I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(46)),
                inject(ByteCode.Ldc("RG16I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(47)),
                inject(ByteCode.Ldc("RGB16I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(48)),
                inject(ByteCode.Ldc("RGBA16I")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(49)),
                inject(ByteCode.Ldc("R16UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(50)),
                inject(ByteCode.Ldc("RG16UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(51)),
                inject(ByteCode.Ldc("RGB16UI")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(52)),
                inject(ByteCode.Ldc("RGBA16UI")),
                inject(ByteCode.AAStore()),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "formatNames", "[Ljava/lang/String;"));

        patch("Add 8bits and 16bits integer texture formats part 3", method,
                remove(ByteCode.BIPush(37)),
                inject(ByteCode.BIPush(53)),
                ByteCode.NewArray(ByteCode.T_INT),
                ByteCode.Dup(),
                ByteCode.IConst(0),
                ByteCode.Ldc(GL30.GL_R8));

        patch("Add 8bits and 16bits integer texture formats part 4", method,
                ByteCode.Ldc(GL30.GL_RGB9_E5),
                ByteCode.IAStore(),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(37)),
                inject(ByteCode.Ldc(GL30.GL_R8I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(38)),
                inject(ByteCode.Ldc(GL30.GL_RG8I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(39)),
                inject(ByteCode.Ldc(GL30.GL_RGB8I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(40)),
                inject(ByteCode.Ldc(GL30.GL_RGBA8I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(41)),
                inject(ByteCode.Ldc(GL30.GL_R8UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(42)),
                inject(ByteCode.Ldc(GL30.GL_RG8UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(43)),
                inject(ByteCode.Ldc(GL30.GL_RGB8UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(44)),
                inject(ByteCode.Ldc(GL30.GL_RGBA8UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(45)),
                inject(ByteCode.Ldc(GL30.GL_R16I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(46)),
                inject(ByteCode.Ldc(GL30.GL_RG16I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(47)),
                inject(ByteCode.Ldc(GL30.GL_RGB16I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(48)),
                inject(ByteCode.Ldc(GL30.GL_RGBA16I)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(49)),
                inject(ByteCode.Ldc(GL30.GL_R16UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(50)),
                inject(ByteCode.Ldc(GL30.GL_RG16UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(51)),
                inject(ByteCode.Ldc(GL30.GL_RGB16UI)),
                inject(ByteCode.IAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.BIPush(52)),
                inject(ByteCode.Ldc(GL30.GL_RGBA16UI)),
                inject(ByteCode.IAStore()),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "formatIds", "[I"));

        patch("Add Prepare and ShadowComp texture Stages", method,
                remove(ByteCode.IConst(3)),
                inject(ByteCode.IConst(5)),
                ByteCode.NewArray("java/lang/String"),
                ByteCode.Dup(),
                ByteCode.IConst(0),
                ByteCode.Ldc("gbuffers"),
                ByteCode.AAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(1),
                ByteCode.Ldc("composite"),
                ByteCode.AAStore(),
                ByteCode.Dup(),
                ByteCode.IConst(2),
                ByteCode.Ldc("deferred"),
                ByteCode.AAStore(),
                inject(ByteCode.Dup()),
                inject(ByteCode.IConst(3)),
                inject(ByteCode.Ldc("prepare")),
                inject(ByteCode.AAStore()),
                inject(ByteCode.Dup()),
                inject(ByteCode.IConst(4)),
                inject(ByteCode.Ldc("shadowcomp")),
                inject(ByteCode.AAStore()),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "STAGE_NAMES", "[Ljava/lang/String;"));

        patch("Create ping-pong buffer for shadow rendering", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;"),
                inject(ByteCode.New(SHADOWFILPTEXTURES)),
                inject(ByteCode.Dup()),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "sfbColorTextures", "Ljava/nio/IntBuffer;")),
                inject(ByteCode.InvokeSpecial(SHADOWFILPTEXTURES, "<init>", "(Ljava/nio/IntBuffer;)V")),
                inject(ByteCode.PutStatic(HOOK, "sfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")));

        patch("Copy variable colorTextureImageUnit", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I")),
                inject(ByteCode.PutStatic(HOOK, "colorTextureImageUnit", "[I")));

        patch("Copy variable dfbColorTexturesFlip", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                inject(ByteCode.PutStatic(HOOK, "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")));

        patch("Copy variable sfbDrawBuffers", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "sfbDrawBuffers", "Ljava/nio/IntBuffer;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "sfbDrawBuffers", "Ljava/nio/IntBuffer;")),
                inject(ByteCode.PutStatic(HOOK, "sfbDrawBuffers", "Ljava/nio/IntBuffer;")));

        patch("Copy variable gbuffersFormat", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "gbuffersFormat", "[I"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersFormat", "[I")),
                inject(ByteCode.PutStatic(HOOK, "gbuffersFormat", "[I")));

        patch("Copy variable modelView", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "modelView", "Ljava/nio/FloatBuffer;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelView", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.PutStatic(HOOK, "modelView", "Ljava/nio/FloatBuffer;")));

        patch("Copy variable modelViewInverse", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "modelViewInverse", "Ljava/nio/FloatBuffer;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelViewInverse", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.PutStatic(HOOK, "modelViewInverse", "Ljava/nio/FloatBuffer;")));

        patch("Copy variable shadowModelView", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "shadowModelView", "Ljava/nio/FloatBuffer;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shadowModelView", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.PutStatic(HOOK, "shadowModelView", "Ljava/nio/FloatBuffer;")));

        patch("Copy variable shadowModelViewInverse", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "shadowModelViewInverse", "Ljava/nio/FloatBuffer;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shadowModelViewInverse", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.PutStatic(HOOK, "shadowModelViewInverse", "Ljava/nio/FloatBuffer;")));

        patch("Copy variable tempMatrixDirectBuffer", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.PutStatic(HOOK, "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;")));
    }

    @MethodPatch("initDrawBuffers(Lnet/optifine/shaders/Program;)V")
    public void initDrawBuffers(MethodNode method) {
        patch("Limit usedDrawBuffers size", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                inject(ByteCode.BIPush(8)),
                inject(ByteCode.InvokeStatic("java/lang/Math", "min", "(II)I")),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedDrawBuffers", "I"));
    }

    @MethodPatch("getDrawBuffer(Lnet/optifine/shaders/Program;Ljava/lang/String;I)I")
    public void getDrawBuffer(MethodNode method) {
        patch("Modify DrawBuffer calculation logic and expand RenderTarget usable range", method,
                ByteCode.ILoad(4),
                remove(ByteCode.BIPush(7)),
                inject(ByteCode.BIPush(15)),
                ByteCode.IfIntGreaterThan(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getToggleColorTextures", "()[Z"),
                ByteCode.ILoad(4),
                ByteCode.IConst(1),
                ByteCode.BAStore(),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.ILoad(4)),
                inject(ByteCode.ILoad(2)),
                ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0));

        patch("Recognize ShadowComp shader as shadow postprocessing shader", method,
                ByteCode.ALoad(0),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getName", "()Ljava/lang/String;")),
                inject(ByteCode.Ldc("shadow")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "startsWith", "(Ljava/lang/String;)Z")),
                inject(ByteCode.InvokeStatic("java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;")),
                inject(ByteCode.GetStatic("java/lang/Boolean", "TRUE", "Ljava/lang/Boolean;")),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramShadow", "Lnet/optifine/shaders/Program;")));

        patch("Write buffer flip configuration for ShadowComp", method,
                ByteCode.ILoad(4),
                ByteCode.IfLessThanZero(null),
                ByteCode.ILoad(4),
                ByteCode.IConst(1),
                ByteCode.IfIntGreaterThan(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getToggleColorTextures", "()[Z")),
                inject(ByteCode.ILoad(4)),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.BAStore()));

        patch("Fix incorrect shadow texture count", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedShadowColorBuffers", "I"),
                ByteCode.ILoad(4),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.IAdd()),
                ByteCode.InvokeStatic("java/lang/Math", "max", "(II)I"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedShadowColorBuffers", "I"));

        patch("Fix incorrect color texture count", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorAttachs", "I"),
                ByteCode.ILoad(4),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.IAdd()),
                ByteCode.InvokeStatic("java/lang/Math", "max", "(II)I"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedColorAttachs", "I"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                ByteCode.ILoad(4),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.IAdd()),
                ByteCode.InvokeStatic("java/lang/Math", "max", "(II)I"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"));
    }

    @MethodPatch("bindGbuffersTextures()V")
    public void bindGbuffersTextures(MethodNode method) {
        patch("Bind colortex8-15 textures for Gbuffer shaders", method,
                remove(ByteCode.ILoad(0)),
                remove(ByteCode.IConst(4)),
                remove(ByteCode.IfIntGreaterEqual(null)),
                ByteCode.IConst(4),
                ByteCode.ILoad(0),
                ByteCode.IAdd(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                ByteCode.IfIntGreaterEqual(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.Ldc(GL13.GL_TEXTURE7)),
                inject(ByteCode.Ldc(GL13.GL_TEXTURE0)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I")),
                ByteCode.ILoad(0),
                inject(ByteCode.IConst(4)),
                inject(ByteCode.IAdd()),
                inject(ByteCode.IALoad()),
                ByteCode.IAdd());
    }

    @MethodPatch("useProgram(Lnet/optifine/shaders/Program;)V")
    public void useProgram(MethodNode method) {
        patch("Bind color buffers", method,
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(MOREBUFFERS, "attachColorBuffer", "(Lnet/optifine/shaders/Program;)V")),
                ByteCode.ALoad(0),
                ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getDrawBuffers", "()Ljava/nio/IntBuffer;"),
                ByteCode.AStore(2));

        LabelNode nvl = ByteCode.Label();
        LabelNode call = ByteCode.Label();

        patch("Set new uniform values", method,
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "hasDeferredPrograms", "Z")),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "customTexturesGbuffers", "[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.IfNull(nvl)),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.Goto(call)),
                inject(nvl),
                inject(ByteCode.IConst(0)),
                inject(call),
                inject(ByteCode.InvokeStatic(HOOK, "updateUniform", "(Lnet/optifine/shaders/Program;ZZ)V")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders$1", "$SwitchMap$net$optifine$shaders$ProgramStage", "[I"));
    }

    @MethodPatch("getBufferIndexFromString(Ljava/lang/String;)I")
    public void getBufferIndexFromString(MethodNode method) {
        LabelNode failed8 = ByteCode.Label();
        LabelNode failed9 = ByteCode.Label();
        LabelNode failed10 = ByteCode.Label();
        LabelNode failed11 = ByteCode.Label();
        LabelNode failed12 = ByteCode.Label();
        LabelNode failed13 = ByteCode.Label();
        LabelNode failed14 = ByteCode.Label();
        LabelNode failed15 = ByteCode.Label();
        patch("Recognize colortex8-15 configurations", method,
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex8")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed8)),
                inject(ByteCode.BIPush(8)),
                inject(ByteCode.IReturn()),
                inject(failed8),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex9")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed9)),
                inject(ByteCode.BIPush(9)),
                inject(ByteCode.IReturn()),
                inject(failed9),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex10")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed10)),
                inject(ByteCode.BIPush(10)),
                inject(ByteCode.IReturn()),
                inject(failed10),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex11")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed11)),
                inject(ByteCode.BIPush(11)),
                inject(ByteCode.IReturn()),
                inject(failed11),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex12")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed12)),
                inject(ByteCode.BIPush(12)),
                inject(ByteCode.IReturn()),
                inject(failed12),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex13")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed13)),
                inject(ByteCode.BIPush(13)),
                inject(ByteCode.IReturn()),
                inject(failed13),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex14")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed14)),
                inject(ByteCode.BIPush(14)),
                inject(ByteCode.IReturn()),
                inject(failed14),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.Ldc("colortex15")),
                inject(ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z")),
                inject(ByteCode.IfZero(failed15)),
                inject(ByteCode.BIPush(15)),
                inject(ByteCode.IReturn()),
                inject(failed15),
                ByteCode.IConst(-1),
                ByteCode.IReturn());
    }

    @MethodPatch("setupFrameBuffer()V")
    public void setupFrameBuffer(MethodNode method) {
        patch("Increase requested texture ID count 16 -> 32", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTextures", "Ljava/nio/IntBuffer;"),
                ByteCode.InvokeVirtual("java/nio/IntBuffer", "clear", "()Ljava/nio/Buffer;"),
                remove(ByteCode.BIPush(16)),
                inject(ByteCode.BIPush(32)),
                ByteCode.InvokeVirtual("java/nio/Buffer", "limit", "(I)Ljava/nio/Buffer;"),
                ByteCode.CheckCast("java/nio/IntBuffer"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glGenTextures", "(Ljava/nio/IntBuffer;)V"));

        LabelNode label1 = ByteCode.Label();
        LabelNode label2 = ByteCode.Label();

        patch("Add color texture initialization binding check (colortex8-16 and size.buffer) part 1", method,
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "shouldBindForSetup", "(I)Z")),
                inject(ByteCode.IfZero(label1)),
                ByteCode.Ldc(GL30.GL_FRAMEBUFFER),
                ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0),
                ByteCode.ILoad(0),
                ByteCode.IAdd(),
                ByteCode.SIPush(GL11.GL_TEXTURE_2D),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;"),
                ByteCode.ILoad(0),
                ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I"),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V"),
                inject(label1));

        patch("Add color texture initialization binding check (colortex8-16 and size.buffer) part 2", method,
                inject(ByteCode.ILoad(1)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "shouldBindForSetup", "(I)Z")),
                inject(ByteCode.IfZero(label2)),
                ByteCode.Ldc(GL30.GL_FRAMEBUFFER),
                ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0),
                ByteCode.ILoad(1),
                ByteCode.IAdd(),
                ByteCode.SIPush(GL11.GL_TEXTURE_2D),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;"),
                ByteCode.ILoad(1),
                ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I"),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V"),
                inject(label2));

        patch("Modify texture size according to size.buffer part 1", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersFormat", "[I"),
                ByteCode.ILoad(0),
                ByteCode.IALoad(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "renderWidth", "I"),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "getResizedWidth", "(II)I")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "renderHeight", "I"),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "getResizedHeight", "(II)I")));

        patch("Modify texture size according to size.buffer part 2", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersFormat", "[I"),
                ByteCode.ILoad(0),
                ByteCode.IALoad(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "renderWidth", "I"),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "getResizedWidth", "(II)I")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "renderHeight", "I"),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "getResizedHeight", "(II)I")));

        patch("Modify texture size according to size.buffer part 3", method,
                ByteCode.SIPush(GL11.GL_RGBA),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "renderWidth", "I"),
                inject(ByteCode.ILoad(1)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "getResizedWidth", "(II)I")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "renderHeight", "I"),
                inject(ByteCode.ILoad(1)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "getResizedHeight", "(II)I")));

        patch("Initialize size.buffer dedicated framebuffer", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "dfb", "I"),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "initDynamicDimensions", "()V")));
    }

    @MethodPatch("uninit()V")
    public void uninit(MethodNode method) {
        patch("Delete size.buffer dedicated framebuffer", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "dfb", "I"),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "uninitDynamicDimensions", "()V")));

        patch("Delete compute shader", method,
                ByteCode.ALoad(1),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("net/optifine/shaders/Program", "setCompositeMipmapSetting", "(I)V"),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "deleteComputes", "(Lnet/optifine/shaders/Program;)V")));
    }

    @MethodPatch("beginRender(Lbib;FJ)V")
    public void beginRender(MethodNode method) {
        LabelNode label1 = ByteCode.Label();

        patch("Add framebuffer texture binding check (colortex8-16 and size.buffer)", method,
                ByteCode.IConst(0),
                ByteCode.IStore(5),
                ByteCode.Label(),
                ByteCode.Frame(),
                ByteCode.ILoad(5),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                ByteCode.IfIntGreaterEqual(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.ILoad(5)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "shouldBindForSetup", "(I)Z")),
                inject(ByteCode.IfZero(label1)),
                ByteCode.Ldc(GL30.GL_FRAMEBUFFER),
                ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0),
                ByteCode.ILoad(5),
                ByteCode.IAdd(),
                ByteCode.SIPush(GL11.GL_TEXTURE_2D),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;"),
                ByteCode.ILoad(5),
                ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I"),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V"),
                inject(label1),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Inc(5, 1));

        patch("Reset render stage configuration", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")),
                ByteCode.Ldc("end beginRender"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "checkGLError", "(Ljava/lang/String;)I"));
    }

    @MethodPatch("clearRenderBuffer()V")
    public void clearRenderBuffer(MethodNode method) {
        // Prevent 1286
        patch("Ensure color attachment 0 is bound", method,
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                inject(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                inject(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")));
        // Same as above
        patch("Ensure color attachment 1 is bound", method,
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT1)),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                remove(ByteCode.IConst(1)),
                remove(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                inject(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT1)),
                inject(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")));

        // Prevent 1282
        patch("Bind colortex2-15 texture to color buffer 2 when glClear clears it", method,
                ByteCode.Ldc(GL30.GL_FRAMEBUFFER),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(0)),
                remove(ByteCode.IAdd()),
                inject(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT2)),
                ByteCode.SIPush(GL11.GL_TEXTURE_2D),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;"),
                ByteCode.ILoad(0),
                ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getB", "(I)I"),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(0)),
                remove(ByteCode.IAdd()),
                inject(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT2)),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glDrawBuffers", "(I)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.SIPush(GL11.GL_COLOR_BUFFER_BIT),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glClear", "(I)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                // Move outside parentheses
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(0)),
                remove(ByteCode.IAdd()),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                remove(ByteCode.ILoad(0)),
                remove(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                inject(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT2)),
                inject(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(0)),
                remove(ByteCode.IAdd()),
                inject(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT2)),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL20", "glDrawBuffers", "(I)V"));

        patch("Switch framebuffer according to size.buffer part 1", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersClear", "[Z"),
                ByteCode.IConst(0),
                ByteCode.BALoad(),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "switchFramebuffer", "(I)V")));

        patch("Switch framebuffer according to size.buffer part 2", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersClear", "[Z"),
                ByteCode.IConst(1),
                ByteCode.BALoad(),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "switchFramebuffer", "(I)V")));

        patch("Switch framebuffer according to size.buffer part 3", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersClear", "[Z"),
                ByteCode.ILoad(0),
                ByteCode.BALoad(),
                ByteCode.IfNotZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Goto(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "switchFramebuffer", "(I)V")));

        patch("Switch framebuffer according to size.buffer part 4", method,
                inject(ByteCode.IConst(-1)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "switchFramebuffer", "(I)V")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbDrawBuffers", "Ljava/nio/IntBuffer;"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "setDrawBuffers", "(Ljava/nio/IntBuffer;)V"));
    }

    @MethodPatch("renderDeferred()V")
    public void renderDeferred(MethodNode method) {
        // Prevent 1282
        patch("Remove original framebuffer texture binding", method,
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(1)),
                remove(ByteCode.IAdd()),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                remove(ByteCode.ILoad(1)),
                remove(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getA", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")));
    }

    @MethodPatch("renderComposites([Lnet/optifine/shaders/Program;Z)V")
    public void renderComposites(MethodNode method) {
        // Prevent 1282
        patch("Remove original framebuffer texture binding", method,
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(2)),
                remove(ByteCode.IAdd()),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                remove(ByteCode.ILoad(2)),
                remove(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getB", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Inc(2, 1),
                ByteCode.Goto(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_DEPTH_ATTACHMENT)),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbDepthTextures", "Ljava/nio/IntBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/IntBuffer", "get", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")));

        patch("Remove flipped framebuffer texture binding", method,
                remove(ByteCode.Ldc(GL30.GL_FRAMEBUFFER)),
                remove(ByteCode.Ldc(GL30.GL_COLOR_ATTACHMENT0)),
                remove(ByteCode.ILoad(4)),
                remove(ByteCode.IAdd()),
                remove(ByteCode.SIPush(GL11.GL_TEXTURE_2D)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                remove(ByteCode.ILoad(4)),
                remove(ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "getB", "(I)I")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/EXTFramebufferObject", "glFramebufferTexture2DEXT", "(IIIII)V")));

        patch("Add Prepare and ShadowComp custom textures", method,
                inject(ByteCode.ALoad(0)),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "customTexturesDeferred", "[Lnet/optifine/shaders/ICustomTexture;"),
                inject(ByteCode.InvokeStatic(MORESTAGES, "getCustomTextures", "([Lnet/optifine/shaders/Program;[Lnet/optifine/shaders/ICustomTexture;)[Lnet/optifine/shaders/ICustomTexture;")),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "bindCustomTextures", "([Lnet/optifine/shaders/ICustomTexture;)V"));

        patch("Generate mipmap for ShadowComp", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "genCompositeMipmap", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.InvokeStatic(MORESTAGES, "genShadowCompMipmap", "()V")),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "preDrawComposite", "()V"));

        LabelNode label1 = ByteCode.Label();
        LabelNode label2 = ByteCode.Label();

        patch("Select flip buffer according to shader stage", method,
                ByteCode.ILoad(4),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.GetStatic(HOOK, "programShadowComp", "[Lnet/optifine/shaders/Program;")),
                inject(ByteCode.IfObjEqual(label1)),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                inject(ByteCode.Goto(label2)),
                inject(label1),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedShadowColorBuffers", "I")),
                inject(label2),
                ByteCode.IfIntGreaterEqual(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(3),
                ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getToggleColorTextures", "()[Z"),
                ByteCode.ILoad(4),
                ByteCode.BALoad(),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(MORESTAGES, "getFlipBuffer", "([Lnet/optifine/shaders/Program;)Lnet/optifine/shaders/FlipTextures;")),
                ByteCode.ILoad(4),
                ByteCode.InvokeVirtual("net/optifine/shaders/FlipTextures", "flip", "(I)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Ldc(GL13.GL_TEXTURE0),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(MORESTAGES, "getColorTextureImageUnit", "([Lnet/optifine/shaders/Program;)[I")),
                ByteCode.ILoad(4),
                ByteCode.IALoad(),
                ByteCode.IAdd(),
                ByteCode.InvokeStatic("bus", "g", "(I)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "dfbColorTexturesFlip", "Lnet/optifine/shaders/FlipTextures;")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(MORESTAGES, "getFlipBuffer", "([Lnet/optifine/shaders/Program;)Lnet/optifine/shaders/FlipTextures;")));

        patch("Execute compute shader", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.ALoad(3)),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "dispatchComputes", "(Lnet/optifine/shaders/Program;)V")),
                ByteCode.ALoad(3),
                ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getId", "()I"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(3),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"));
    }

    @MethodPatch("renderFinal()V")
    public void renderFinal(MethodNode method) {
        patch("Execute compute shader", method,
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramFinal", "Lnet/optifine/shaders/Program;")),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "dispatchComputes", "(Lnet/optifine/shaders/Program;)V")),
                method.instructions.getFirst());
    }

    @MethodPatch("createFragShader(Lnet/optifine/shaders/Program;Ljava/lang/String;)I")
    public void createFragShader(MethodNode method) {
        patch("Add RenderTargets comment matching", method,
                ByteCode.Ldc("Invalid draw buffers: "),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.ALoad(10),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;"),
                ByteCode.InvokeStatic("net/optifine/shaders/SMCLog", "warning", "(Ljava/lang/String;)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.ALoad(9)),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(MOREBUFFERS, "checkRenderTargets", "(Lnet/optifine/shaders/config/ShaderLine;Lnet/optifine/shaders/Program;)V")));

        patch("Shadow color texture format matching", method,
                ByteCode.ALoad(11),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "getTextureFormatFromString", "(Ljava/lang/String;)I"),
                ByteCode.IStore(13),
                inject(ByteCode.ALoad(10)),
                inject(ByteCode.ILoad(13)),
                inject(ByteCode.ALoad(11)),
                inject(ByteCode.InvokeStatic(MORESTAGES, "parseShadowFormat", "(Ljava/lang/String;ILjava/lang/String;)V")));

        patch("Shadow color texture glClear toggle matching", method,
                ByteCode.Ldc("Clear"),
                ByteCode.InvokeStatic("net/optifine/util/StrUtils", "removeSuffix", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"),
                ByteCode.AStore(10),
                inject(ByteCode.ALoad(10)),
                inject(ByteCode.InvokeStatic(MORESTAGES, "parseShadowClear", "(Ljava/lang/String;)V")));

        patch("Shadow color texture glClearColor matching", method,
                ByteCode.Ldc("ClearColor"),
                ByteCode.InvokeStatic("net/optifine/util/StrUtils", "removeSuffix", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"),
                ByteCode.AStore(10),
                inject(ByteCode.ALoad(10)),
                inject(ByteCode.ALoad(9)),
                inject(ByteCode.InvokeStatic(MORESTAGES, "parseShadowClearColor", "(Ljava/lang/String;Lnet/optifine/shaders/config/ShaderLine;)V")));

        patch("Allow final shader to configure glClear", method,
                ByteCode.Ldc("Clear"),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("net/optifine/shaders/config/ShaderLine", "isConstBoolSuffix", "(Ljava/lang/String;Z)Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(1),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderParser", "isComposite", "(Ljava/lang/String;)Z"),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderParser", "isFinal", "(Ljava/lang/String;)Z")),
                inject(ByteCode.IOr()));

        patch("Allow final shader to configure glClearColor", method,
                ByteCode.Ldc("ClearColor"),
                ByteCode.InvokeVirtual("net/optifine/shaders/config/ShaderLine", "isConstVec4Suffix", "(Ljava/lang/String;)Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(1),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderParser", "isComposite", "(Ljava/lang/String;)Z"),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderParser", "isFinal", "(Ljava/lang/String;)Z")),
                inject(ByteCode.IOr()));

        patch("Match colorimage/shadowcolorimage", method,
                ByteCode.ALoad(9),
                ByteCode.InvokeVirtual("net/optifine/shaders/config/ShaderLine", "isUniform", "()Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(9),
                ByteCode.InvokeVirtual("net/optifine/shaders/config/ShaderLine", "getName", "()Ljava/lang/String;"),
                ByteCode.AStore(10),
                inject(ByteCode.ALoad(10)),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "parseUniform", "(Ljava/lang/String;)V")));
    }

    @MethodPatch("getEnumShaderOption(Lnet/optifine/shaders/config/EnumShaderOption;)Ljava/lang/String;")
    public void getEnumShaderOption(MethodNode method) {
        LabelNode label = ByteCode.Label();

        patch("Add newer version feature option config read/write", method,
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "getEnumShaderOption", "(Lnet/optifine/shaders/config/EnumShaderOption;)Ljava/lang/String;")),
                inject(ByteCode.Dup()),
                inject(ByteCode.IfNull(label)),
                inject(ByteCode.AReturn()),
                inject(label),
                method.instructions.getFirst());
    }

    @MethodPatch("setEnumShaderOption(Lnet/optifine/shaders/config/EnumShaderOption;Ljava/lang/String;)V")
    public void setEnumShaderOption(MethodNode method) {
        LabelNode label = ByteCode.Label();

        patch("Add newer version feature option config read/write", method,
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "setEnumShaderOption", "(Lnet/optifine/shaders/config/EnumShaderOption;Ljava/lang/String;)Z")),
                inject(ByteCode.IfZero(label)),
                inject(ByteCode.Return()),
                inject(label),
                method.instructions.getFirst());
    }

    @MethodPatch("loadShaderPack()V")
    public void loadShaderPack(MethodNode method) {
        patch("Suppress anisotropic filtering check during shader loading", method,
                remove(ByteCode.InvokeStatic("Config", "isAnisotropicFiltering", "()Z")),
                inject(ByteCode.IConst(0)),
                ByteCode.IfZero(null));
    }

    @MethodPatch("loadShaderPackProperties()V")
    public void loadShaderPackProperties(MethodNode method) {
        // VRAM leak
        patch("Reload custom textures", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "resetCustomTextures", "()V")));

        patch("Suppress shader option macro for some properties part 1", method,
                remove(ByteCode.ALoad(2)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptions", "[Lnet/optifine/shaders/config/ShaderOption;")),
                remove(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseOptionSliders", "(Ljava/util/Properties;[Lnet/optifine/shaders/config/ShaderOption;)Ljava/util/Set;")),
                remove(ByteCode.PutStatic("net/optifine/shaders/Shaders", "shaderPackOptionSliders", "Ljava/util/Set;")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.ALoad(2)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptions", "[Lnet/optifine/shaders/config/ShaderOption;")),
                remove(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseProfiles", "(Ljava/util/Properties;[Lnet/optifine/shaders/config/ShaderOption;)[Lnet/optifine/shaders/config/ShaderProfile;")),
                remove(ByteCode.PutStatic("net/optifine/shaders/Shaders", "shaderPackProfiles", "[Lnet/optifine/shaders/config/ShaderProfile;")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.ALoad(2)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackProfiles", "[Lnet/optifine/shaders/config/ShaderProfile;")),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptions", "[Lnet/optifine/shaders/config/ShaderOption;")),
                remove(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseGuiScreens", "(Ljava/util/Properties;[Lnet/optifine/shaders/config/ShaderProfile;[Lnet/optifine/shaders/config/ShaderOption;)Ljava/util/Map;")),
                remove(ByteCode.PutStatic("net/optifine/shaders/Shaders", "shaderPackGuiScreens", "Ljava/util/Map;")));

        LabelNode label = ByteCode.Label();

        patch("Suppress shader option macro for some properties part 2", method,
                ByteCode.ALoad(2),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseBuffersFlip", "(Ljava/util/Properties;)V"),
                inject(ByteCode.New("net/optifine/util/PropertiesOrdered")),
                inject(ByteCode.Dup()),
                inject(ByteCode.InvokeSpecial("net/optifine/util/PropertiesOrdered", "<init>", "()V")),
                inject(ByteCode.Dup()),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPack", "Lnet/optifine/shaders/IShaderPack;")),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.PutStatic(PROPERTYFIX, "useShaderOptions", "Z")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeInterface("net/optifine/shaders/IShaderPack", "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/MacroProcessor", "process", "(Ljava/io/InputStream;Ljava/lang/String;)Ljava/io/InputStream;")),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.PutStatic(PROPERTYFIX, "useShaderOptions", "Z")),
                inject(ByteCode.DupX1()),
                inject(ByteCode.InvokeVirtual("java/util/Properties", "load", "(Ljava/io/InputStream;)V")),
                inject(ByteCode.InvokeVirtual("java/io/InputStream", "close", "()V")),
                inject(ByteCode.AStore(2)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOldLighting", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "loadFrom", "(Ljava/util/Properties;)Z")),
                inject(ByteCode.Pop()),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackSeparateAo", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "loadFrom", "(Ljava/util/Properties;)Z")),
                inject(ByteCode.Pop()),
                // Only execute during loadShaderPackProperties
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptionSliders", "Ljava/util/Set;")),
                inject(ByteCode.IfNotNull(label)),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptions", "[Lnet/optifine/shaders/config/ShaderOption;")),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseOptionSliders", "(Ljava/util/Properties;[Lnet/optifine/shaders/config/ShaderOption;)Ljava/util/Set;")),
                inject(ByteCode.PutStatic("net/optifine/shaders/Shaders", "shaderPackOptionSliders", "Ljava/util/Set;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptions", "[Lnet/optifine/shaders/config/ShaderOption;")),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseProfiles", "(Ljava/util/Properties;[Lnet/optifine/shaders/config/ShaderOption;)[Lnet/optifine/shaders/config/ShaderProfile;")),
                inject(ByteCode.PutStatic("net/optifine/shaders/Shaders", "shaderPackProfiles", "[Lnet/optifine/shaders/config/ShaderProfile;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackProfiles", "[Lnet/optifine/shaders/config/ShaderProfile;")),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackOptions", "[Lnet/optifine/shaders/config/ShaderOption;")),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseGuiScreens", "(Ljava/util/Properties;[Lnet/optifine/shaders/config/ShaderProfile;[Lnet/optifine/shaders/config/ShaderOption;)Ljava/util/Map;")),
                inject(ByteCode.PutStatic("net/optifine/shaders/Shaders", "shaderPackGuiScreens", "Ljava/util/Map;")),
                inject(label),
                // saveFinalShader
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPack", "Lnet/optifine/shaders/IShaderPack;")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeInterface("net/optifine/shaders/IShaderPack", "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;")),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/config/MacroProcessor", "process", "(Ljava/io/InputStream;Ljava/lang/String;)Ljava/io/InputStream;")),
                inject(ByteCode.Pop()));

        patch("Add terrain/entities/blockentities shadow rendering config part 1", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackShadowTranslucent", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;"),
                ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "resetValue", "()V"),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowTerrain", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "resetValue", "()V")),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowEntities", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "resetValue", "()V")),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowBlockEntities", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "resetValue", "()V")));

        patch("Add terrain/entities/blockentities shadow rendering config part 2", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPackShadowTranslucent", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;"),
                ByteCode.ALoad(2),
                ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "loadFrom", "(Ljava/util/Properties;)Z"),
                ByteCode.Pop(),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowTerrain", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "loadFrom", "(Ljava/util/Properties;)Z")),
                inject(ByteCode.Pop()),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowEntities", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "loadFrom", "(Ljava/util/Properties;)Z")),
                inject(ByteCode.Pop()),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowBlockEntities", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "loadFrom", "(Ljava/util/Properties;)Z")),
                inject(ByteCode.Pop()));

        patch("Add size.buffer configuration part 1", method,
                ByteCode.AConstNull(),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "customUniforms", "Lnet/optifine/shaders/uniform/CustomUniforms;"),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "resetBufferSizes", "()V")));

        patch("Add size.buffer configuration part 2", method,
                ByteCode.ALoad(2),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "parseBuffersFlip", "(Ljava/util/Properties;)V"),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "parseBufferSizes", "(Ljava/util/Properties;)V")));

        patch("Load Prepare and ShadowComp custom textures", method,
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "customTexturesDeferred", "[Lnet/optifine/shaders/ICustomTexture;"),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.IConst(3)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "loadCustomTextures", "(Ljava/util/Properties;I)[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.PutStatic(HOOK, "customTexturesPrepare", "[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.ALoad(2)),
                inject(ByteCode.IConst(4)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "loadCustomTextures", "(Ljava/util/Properties;I)[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.PutStatic(HOOK, "customTexturesShadowComp", "[Lnet/optifine/shaders/ICustomTexture;")));

        patch("Reset blend.<program>.<buffer> configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/EntityAliases", "reset", "()V"),
                inject(ByteCode.GetStatic(BLEND, "propBlend", "Ljava/util/Map;")),
                inject(ByteCode.InvokeInterface("java/util/Map", "clear", "()V")));
    }

    @MethodPatch("init()V")
    public void init(MethodNode method) {
        patch("Reload shader.properties after modifying shader options", method,
                ByteCode.Ldc("world"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.ILoad(3),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;"),
                ByteCode.Ldc("/"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
                ByteCode.InvokeVirtual("java/lang/StringBuilder", "toString", "()Ljava/lang/String;"),
                ByteCode.AStore(2),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "loadShaderPackProperties", "()V")));

        patch("Configure shader size.buffer", method,
                ByteCode.ALoad(4),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "updateToggleBuffers", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.ALoad(4)),
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "updateProgramSize", "(Lnet/optifine/shaders/Program;)V")));

        patch("Check if compute shader exists in Deferred shaders", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramsDeferred", "[Lnet/optifine/shaders/Program;"),
                ByteCode.ILoad(3),
                ByteCode.AALoad(),
                ByteCode.InvokeVirtual("net/optifine/shaders/Program", "getId", "()I"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramsDeferred", "[Lnet/optifine/shaders/Program;")),
                inject(ByteCode.ILoad(3)),
                inject(ByteCode.AALoad()),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "hasComputes", "(Lnet/optifine/shaders/Program;)Z")),
                inject(ByteCode.IOr()));

        patch("Check if Prepare and ShadowComp shaders exist", method,
                inject(ByteCode.InvokeStatic(MORESTAGES, "checkComposites", "()V")),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedColorAttachs", "I"));

        LabelNode label = ByteCode.Label();

        patch("Modify shadow texture rendering condition", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedColorBuffers", "I"),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedColorAttachs", "I"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedShadowColorBuffers", "I")),
                inject(ByteCode.IfZero(label)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedShadowDepthBuffers", "I")),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.InvokeStatic("java/lang/Math", "max", "(II)I")),
                inject(ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedShadowDepthBuffers", "I")),
                inject(label));

        patch("Reset shadow texture configuration", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "gbuffersClearColor", "[Lorg/lwjgl/util/vector/Vector4f;"),
                ByteCode.AConstNull(),
                ByteCode.InvokeStatic("java/util/Arrays", "fill", "([Ljava/lang/Object;Ljava/lang/Object;)V"),
                inject(ByteCode.GetStatic(HOOK, "shadowFormat", "[I")),
                inject(ByteCode.SIPush(GL11.GL_RGBA)),
                inject(ByteCode.InvokeStatic("java/util/Arrays", "fill", "([II)V")),
                inject(ByteCode.GetStatic(HOOK, "shadowClear", "[Z")),
                inject(ByteCode.IConst(1)),
                inject(ByteCode.InvokeStatic("java/util/Arrays", "fill", "([ZZ)V")),
                inject(ByteCode.GetStatic(HOOK, "shadowClearColor", "[Lorg/lwjgl/util/vector/Vector4f;")),
                inject(ByteCode.AConstNull()),
                inject(ByteCode.InvokeStatic("java/util/Arrays", "fill", "([Ljava/lang/Object;Ljava/lang/Object;)V")));

        patch("Reset colorimage binding switch", method,
                ByteCode.IConst(1),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "usedDrawBuffers", "I"),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.PutStatic(HOOK, "bindColorImages", "Z")));

        patch("Compile compute shader", method,
                inject(ByteCode.ALoad(4)),
                inject(ByteCode.Ldc("/shaders/")),
                inject(ByteCode.ALoad(6)),
                inject(ByteCode.Ldc(".csh")),
                inject(ByteCode.InvokeStatic(COMPUTESHADER, "setupComputePrograms", "(Lnet/optifine/shaders/Program;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")),
                ByteCode.ALoad(4),
                ByteCode.ALoad(9),
                ByteCode.ALoad(10),
                ByteCode.ALoad(11),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "setupProgram", "(Lnet/optifine/shaders/Program;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"));
    }

    @MethodPatch("preDrawComposite()V")
    public void preDrawComposite(MethodNode method) {
        patch("Modify buffer size", method,
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "preDrawComposite", "()V")),
                method.instructions.getFirst());

        // These two will not conflict
        patch("Modify view size for ShadowComp", method,
                inject(ByteCode.InvokeStatic(MORESTAGES, "preDrawComposite", "()V")),
                method.instructions.getFirst());
    }

    @MethodPatch("postDrawComposite()V")
    public void postDrawComposite(MethodNode method) {
        patch("Restore buffer size", method,
                inject(ByteCode.InvokeStatic(BUFFERSIZE, "postDrawComposite", "()V")),
                method.instructions.getFirst());

        patch("Restore view size for ShadowComp", method,
                inject(ByteCode.InvokeStatic(MORESTAGES, "postDrawComposite", "()V")),
                method.instructions.getFirst());
    }

    @MethodPatch("getFramebufferStatusText(I)Ljava/lang/String;")
    public void getFramebufferStatusText(MethodNode method) {
        LabelNode label = ByteCode.Label();
        LookupSwitchInsnNode lookup = (LookupSwitchInsnNode) patch("Add size.buffer related error message part 1", method,
                collect(ByteCode.LookupSwitch()))[0];

        boolean matched = false;

        for (int i = 0; i < lookup.keys.size(); i++) {
            if (lookup.keys.get(i) > EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT) {
                lookup.keys.add(i, EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT);
                lookup.labels.add(i, label);

                matched = true;

                break;
            }
        }

        if (!matched) {
            lookup.keys.add(EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT);
            lookup.labels.add(label);
        }

        patch("Add size.buffer related error message part 2", method,
                inject(label),
                inject(ByteCode.Ldc("Incomplete dimensions")),
                inject(ByteCode.AReturn()),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                ByteCode.Ldc("Unknown"),
                ByteCode.AReturn());
    }

    @MethodPatch("setupProgram(Lnet/optifine/shaders/Program;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
    public void setupProgram(MethodNode method) {
        patch("Add at_midBlock vertex attribute part 1", method,
                ByteCode.IConst(0),
                ByteCode.PutStatic("net/optifine/shaders/Shaders", "progUseTangentAttrib", "Z"),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.PutStatic(HOOK, "progUseVelocityAttrib", "Z")),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.PutStatic(HOOK, "progUseMidBlockAttrib", "Z")));

        patch("Add at_midBlock vertex attribute part 2", method,
                inject(ByteCode.ILoad(4)),
                inject(ByteCode.InvokeStatic(VERTEXATTRIBUTE, "bindAttributes", "(I)V")),
                ByteCode.ILoad(4),
                ByteCode.InvokeStatic("org/lwjgl/opengl/ARBShaderObjects", "glLinkProgramARB", "(I)V"));
    }

    @MethodPatch("createVertShader(Lnet/optifine/shaders/Program;Ljava/lang/String;)I")
    public void createVertShader(MethodNode method) {
        patch("Add at_midBlock vertex attribute", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.ALoad(9)),
                inject(ByteCode.InvokeStatic(VERTEXATTRIBUTE, "checkAttributes", "(Lnet/optifine/shaders/config/ShaderLine;)V")),
                ByteCode.ALoad(9),
                ByteCode.Ldc("countInstances"));

        patch("Core Profile version conversion", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(4),
                ByteCode.ALoad(1),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shaderPack", "Lnet/optifine/shaders/IShaderPack;"),
                ByteCode.IConst(0),
                ByteCode.ALoad(6),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("net/optifine/shaders/config/ShaderPackParser", "resolveIncludes", "(Ljava/io/BufferedReader;Ljava/lang/String;Lnet/optifine/shaders/IShaderPack;ILjava/util/List;I)Ljava/io/BufferedReader;"),
                ByteCode.AStore(4),
                inject(ByteCode.ALoad(4)),
                inject(ByteCode.InvokeStatic(COREPROFILE, "convertCoreProfile", "(Ljava/io/BufferedReader;)Ljava/io/BufferedReader;")),
                inject(ByteCode.AStore(4)));
    }

    @MethodPatch("getTextureIndex(ILjava/lang/String;)I")
    public void getTextureIndex(MethodNode method) {
        LabelNode labelGbuffer = ByteCode.Label();
        LabelNode labelComp = ByteCode.Label();

        patch("Add colortex8-15 custom textures part 1", method,
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "getBufferIndexFromString", "(Ljava/lang/String;)I")),
                inject(ByteCode.IStore(2)),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.IConst(4)),
                inject(ByteCode.IfIntLessThan(labelGbuffer)),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.BIPush(15)),
                inject(ByteCode.IfIntGreaterThan(labelGbuffer)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I")),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.IALoad()),
                inject(ByteCode.IReturn()),
                inject(labelGbuffer),
                ByteCode.ALoad(1),
                ByteCode.Ldc("texture"),
                ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z"));

        patch("Add colortex8-15 custom textures part 2", method,
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "getBufferIndexFromString", "(Ljava/lang/String;)I")),
                inject(ByteCode.IStore(2)),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.IConst(0)),
                inject(ByteCode.IfIntLessThan(labelComp)),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.BIPush(15)),
                inject(ByteCode.IfIntGreaterThan(labelComp)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "colorTextureImageUnit", "[I")),
                inject(ByteCode.ILoad(2)),
                inject(ByteCode.IALoad()),
                inject(ByteCode.IReturn()),
                inject(labelComp),
                ByteCode.ALoad(1),
                ByteCode.Ldc("colortex0"),
                ByteCode.InvokeVirtual("java/lang/String", "equals", "(Ljava/lang/Object;)Z"));

        JumpInsnNode equal2 = ByteCode.IfIntEqual(null);
        JumpInsnNode equal3 = ByteCode.IfIntEqual(null);

        equal2.label = equal3.label = ((JumpInsnNode) patch("Add Prepare and ShadowComp texture Stages", method,
                ByteCode.ILoad(0),
                ByteCode.IConst(1),
                collect(ByteCode.IfIntEqual(null)),
                ByteCode.ILoad(0),
                ByteCode.IConst(2),
                inject(equal2),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.IConst(3)),
                inject(equal3),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.IConst(4)),
                ByteCode.IfIntNotEqual(null))[0]).label;
    }

    @MethodPatch("resetCustomTextures()V")
    public void resetCustomTextures(MethodNode method) {
        patch("Delete Prepare and ShadowComp custom textures", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "customTexturesDeferred", "[Lnet/optifine/shaders/ICustomTexture;"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "deleteCustomTextures", "([Lnet/optifine/shaders/ICustomTexture;)V"),
                inject(ByteCode.GetStatic(HOOK, "customTexturesPrepare", "[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "deleteCustomTextures", "([Lnet/optifine/shaders/ICustomTexture;)V")),
                inject(ByteCode.GetStatic(HOOK, "customTexturesShadowComp", "[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "deleteCustomTextures", "([Lnet/optifine/shaders/ICustomTexture;)V")),
                inject(ByteCode.AConstNull()),
                inject(ByteCode.PutStatic(HOOK, "customTexturesPrepare", "[Lnet/optifine/shaders/ICustomTexture;")),
                inject(ByteCode.AConstNull()),
                inject(ByteCode.PutStatic(HOOK, "customTexturesShadowComp", "[Lnet/optifine/shaders/ICustomTexture;")));
    }

    @MethodPatch("setupShadowFrameBuffer()V")
    public void setupShadowFrameBuffer(MethodNode method) {
        patch("Allocate 4 shadow color textures fixedly", method,
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedShadowColorBuffers", "I")),
                inject(ByteCode.IConst(4)),
                ByteCode.InvokeVirtual("java/nio/Buffer", "limit", "(I)Ljava/nio/Buffer;"));

        patch("Set shadow ping-pong buffer and format part 1", method,
                ByteCode.SIPush(GL11.GL_TEXTURE_2D),
                ByteCode.IConst(0),
                remove(ByteCode.SIPush(GL11.GL_RGBA)),
                inject(ByteCode.GetStatic(HOOK, "shadowFormat", "[I")),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.IALoad()),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shadowMapWidth", "I"),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shadowMapHeight", "I"),
                ByteCode.IConst(0),
                remove(ByteCode.Ldc(GL12.GL_BGRA)),
                inject(ByteCode.GetStatic(HOOK, "shadowFormat", "[I")),
                inject(ByteCode.ILoad(0)),
                inject(ByteCode.IALoad()),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "getPixelFormat", "(I)I")),
                ByteCode.Ldc(GL12.GL_UNSIGNED_INT_8_8_8_8_REV),
                ByteCode.AConstNull(),
                ByteCode.CheckCast("java/nio/ByteBuffer"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glTexImage2D", "(IIIIIIIILjava/nio/ByteBuffer;)V"));

        patch("Set shadow ping-pong buffer and format part 2", method,
                inject(ByteCode.InvokeStatic(MORESTAGES, "setupShadowFlipBuffer", "()V")),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("bus", "i", "(I)V"));
    }

    @MethodPatch("updateAlphaBlend(Lnet/optifine/shaders/Program;Lnet/optifine/shaders/Program;)V")
    public void updateAlphaBlend(MethodNode method) {
        patch("Apply blend.<program>.<buffer> configuration part 1", method,
                ByteCode.InvokeStatic("bus", "unlockBlend", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(BLEND, "updateOldBlendStateIndexed", "(Lnet/optifine/shaders/Program;)V")));

        patch("Apply blend.<program>.<buffer> configuration part 2", method,
                inject(ByteCode.ALoad(1)),
                inject(ByteCode.InvokeStatic(BLEND, "updateNewBlendStateIndexed", "(Lnet/optifine/shaders/Program;)V")),
                ByteCode.Return());
    }

    @MethodPatch("getPixelFormat(I)I")
    public void getPixelFormat(MethodNode method) {
        LookupSwitchInsnNode lookup = (LookupSwitchInsnNode) patch("Add 8bits and 16bits integer texture formats", method,
                collect(ByteCode.LookupSwitch()))[0];

        lookup.keys.add(GL30.GL_R8I);
        lookup.keys.add(GL30.GL_RG8I);
        lookup.keys.add(GL30.GL_RGB8I);
        lookup.keys.add(GL30.GL_RGBA8I);
        lookup.keys.add(GL30.GL_R8UI);
        lookup.keys.add(GL30.GL_RG8UI);
        lookup.keys.add(GL30.GL_RGB8UI);
        lookup.keys.add(GL30.GL_RGBA8UI);
        lookup.keys.add(GL30.GL_R16I);
        lookup.keys.add(GL30.GL_RG16I);
        lookup.keys.add(GL30.GL_RGB16I);
        lookup.keys.add(GL30.GL_RGBA16I);
        lookup.keys.add(GL30.GL_R16UI);
        lookup.keys.add(GL30.GL_RG16UI);
        lookup.keys.add(GL30.GL_RGB16UI);
        lookup.keys.add(GL30.GL_RGBA16UI);

        lookup.keys.sort(null);

        LabelNode label = lookup.labels.get(0);

        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
        lookup.labels.add(label);
    }

    @MethodPatch("setCamera(F)V")
    public void setCamera(MethodNode method) {
        patch("Camera coordinate fix", method,
                inject(ByteCode.InvokeStatic(CAMERAFIX, "fixCamera", "()V")),
                ByteCode.Ldc("setCamera"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "checkGLError", "(Ljava/lang/String;)I"));
    }

    @MethodPatch("setCameraShadow(F)V")
    public void setCameraShadow(MethodNode method) {
        patch("Remove Gbuffer matrix uniform writing in shadow stage", method,
                remove(ByteCode.SIPush(GL11.GL_PROJECTION_MATRIX)),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "projection", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;")),
                remove(ByteCode.CheckCast("java/nio/FloatBuffer")),
                remove(ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glGetFloat", "(ILjava/nio/FloatBuffer;)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "projectionInverse", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;")),
                remove(ByteCode.CheckCast("java/nio/FloatBuffer")),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "projection", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;")),
                remove(ByteCode.CheckCast("java/nio/FloatBuffer")),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "faProjectionInverse", "[F")),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "faProjection", "[F")),
                remove(ByteCode.InvokeStatic("net/optifine/shaders/SMath", "invertMat4FBFA", "(Ljava/nio/FloatBuffer;Ljava/nio/FloatBuffer;[F[F)V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "projection", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;")),
                remove(ByteCode.Pop()),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "projectionInverse", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;")),
                remove(ByteCode.Pop()),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.SIPush(GL11.GL_MODELVIEW_MATRIX),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelView", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;")),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;"),
                ByteCode.CheckCast("java/nio/FloatBuffer"),
                ByteCode.InvokeStatic("org/lwjgl/opengl/GL11", "glGetFloat", "(ILjava/nio/FloatBuffer;)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelViewInverse", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "duplicate", "()Ljava/nio/FloatBuffer;")),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;"),
                ByteCode.CheckCast("java/nio/FloatBuffer"),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelView", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "duplicate", "()Ljava/nio/FloatBuffer;")),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;"),
                ByteCode.CheckCast("java/nio/FloatBuffer"),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "faModelViewInverse", "[F"),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "faModelView", "[F"),
                ByteCode.InvokeStatic("net/optifine/shaders/SMath", "invertMat4FBFA", "(Ljava/nio/FloatBuffer;Ljava/nio/FloatBuffer;[F[F)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelView", "Ljava/nio/FloatBuffer;")),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "tempMatrixDirectBuffer", "Ljava/nio/FloatBuffer;")),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;"),
                ByteCode.Pop(),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                remove(ByteCode.GetStatic("net/optifine/shaders/Shaders", "modelViewInverse", "Ljava/nio/FloatBuffer;")),
                remove(ByteCode.IConst(0)),
                remove(ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;")),
                remove(ByteCode.Pop()));

        patch("Camera coordinate fix", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "shadowModelViewInverse", "Ljava/nio/FloatBuffer;"),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("java/nio/FloatBuffer", "position", "(I)Ljava/nio/Buffer;"),
                inject(ByteCode.InvokeStatic(CAMERAFIX, "fixCameraShadow", "()V")),
                ByteCode.Pop(),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "uniform_gbufferProjection", "Lnet/optifine/shaders/uniform/ShaderUniformM4;"),
                ByteCode.IConst(0),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "projection", "Ljava/nio/FloatBuffer;"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "setProgramUniformMatrix4ARB", "(Lnet/optifine/shaders/uniform/ShaderUniformM4;ZLjava/nio/FloatBuffer;)V"));
    }

    @MethodPatch("getCameraPosition()Let;")
    public void getCameraPosition(MethodNode method) {
        patch("Camera coordinate fix", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "cameraPositionX", "D"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "cameraOffsetX", "I")),
                inject(ByteCode.I2D()),
                inject(ByteCode.DAdd()),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "cameraPositionY", "D"),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "cameraPositionZ", "D"),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "cameraOffsetZ", "I")),
                inject(ByteCode.I2D()),
                inject(ByteCode.DAdd()));
    }

    @MethodPatch("setEntityId(Lvg;)V")
    public void setEntityId(MethodNode method) {
        patch("Newer version entity ID mapping and lightning entity ID", method,
                ByteCode.ILoad(1),
                ByteCode.InvokeStatic("net/optifine/shaders/EntityAliases", "getEntityAliasId", "(I)I"),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "getEntityAliasID", "(ILvg;)I")),
                ByteCode.IStore(2),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ILoad(2),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isAllowNegative", "(I)I")));
    }

    @MethodPatch("beginSky()V")
    public void beginSky(MethodNode method) {
        patch("Add SKY render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "SKY", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginClouds()V")
    public void beginClouds(MethodNode method) {
        patch("Add CLOUDS render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "CLOUDS", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginEntities()V")
    public void beginEntities(MethodNode method) {
        patch("Add ENTITIES render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "ENTITIES", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginBlockEntities()V")
    public void beginBlockEntities(MethodNode method) {
        patch("Add BLOCK_ENTITIES render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "BLOCK_ENTITIES", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginLitParticles()V")
    @MethodPatch("beginParticles()V")
    public void beginParticles(MethodNode method) {
        patch("Add PARTICLES render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "PARTICLES", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginWeather()V")
    public void beginWeather(MethodNode method) {
        patch("Add RAIN_SNOW render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "RAIN_SNOW", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        JumpInsnNode skip = ByteCode.IfNotZero(null);

        skip.label = ((JumpInsnNode) patch("Skip copying depth texture when newer version rendering mechanism is enabled", method,
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(skip),
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "usedDepthBuffers", "I"),
                ByteCode.IConst(3),
                collect(ByteCode.IfIntLessThan(null)))[0]).label;
    }

    @MethodPatch("endRender()V")
    @MethodPatch("endSky()V")
    @MethodPatch("endClouds()V")
    @MethodPatch("endParticles()V")
    @MethodPatch("endWeather()V")
    public void resetRenderStage(MethodNode method) {
        patch("Reset render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("beginHand(Z)V")
    public void beginHand(MethodNode method) {
        patch("Add HAND_TRANSLUCENT render stage configuration", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramHandWater", "Lnet/optifine/shaders/Program;"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "HAND_TRANSLUCENT", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add HAND_SOLID render stage configuration", method,
                ByteCode.GetStatic("net/optifine/shaders/Shaders", "ProgramHand", "Lnet/optifine/shaders/Program;"),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "HAND_SOLID", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("endHand()V")
    public void endHand(MethodNode method) {
        patch("Reset render stage configuration", method,
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")),
                method.instructions.getFirst());
    }
}
