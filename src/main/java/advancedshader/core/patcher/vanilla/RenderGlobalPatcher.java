package advancedshader.core.patcher.vanilla;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("buy")
public class RenderGlobalPatcher extends Patcher {

    @MethodPatch("a(Lvg;Lbxy;F)V")
    public void renderEntities(MethodNode method) {
        LabelNode entitiesStart = ByteCode.Label();
        LabelNode entitiesEnd = (LabelNode) patch("Add shadowEntities configuration part 1", method,
                collect(ByteCode.Label()),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                ByteCode.ILoad(21),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "endEntities", "()V"))[0];

        patch("Add shadowEntities configuration part 2", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "beginEntities", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.ILoad(21)),
                inject(ByteCode.IfZero(entitiesStart)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "isShadowPass", "Z")),
                inject(ByteCode.IfZero(entitiesStart)),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowEntities", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "isFalse", "()Z")),
                inject(ByteCode.IfNotZero(entitiesEnd)),
                inject(entitiesStart));

        LabelNode blockEntitiesStart = (LabelNode) patch("Add shadowBlockEntities configuration part 1", method,
                ByteCode.Ldc("blockentities"),
                ByteCode.InvokeVirtual("rl", "c", "(Ljava/lang/String;)V"),
                collect(ByteCode.Label()))[0];

        LabelNode blockEntitiesEnd = (LabelNode) patch("Add shadowBlockEntities configuration part 2", method,
                collect(ByteCode.Label()),
                ByteCode.LineNumber(),
                ByteCode.ILoad(21),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "endBlockEntities", "()V"))[0];

        patch("Add shadowBlockEntities configuration part 3", method,
                ByteCode.Ldc("blockentities"),
                ByteCode.InvokeVirtual("rl", "c", "(Ljava/lang/String;)V"),
                inject(ByteCode.ILoad(21)),
                inject(ByteCode.IfZero(blockEntitiesStart)),
                inject(ByteCode.GetStatic("net/optifine/shaders/Shaders", "isShadowPass", "Z")),
                inject(ByteCode.IfZero(blockEntitiesStart)),
                inject(ByteCode.GetStatic(HOOK, "shaderPackShadowBlockEntities", "Lnet/optifine/shaders/config/PropertyDefaultTrueFalse;")),
                inject(ByteCode.InvokeVirtual("net/optifine/shaders/config/PropertyDefaultTrueFalse", "isFalse", "()Z")),
                inject(ByteCode.IfNotZero(blockEntitiesEnd)));

        LabelNode lightningSkip = ByteCode.Label();

        patch("Newer version mechanism - Render lightning with entity shader part 1", method,
                ByteCode.GetStatic("net/optifine/reflect/Reflector", "ForgeTileEntity_shouldRenderInPass", "Lnet/optifine/reflect/ReflectorMethod;"),
                ByteCode.InvokeVirtual("net/optifine/reflect/ReflectorMethod", "exists", "()Z"),
                ByteCode.IStore(20),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "beginLightningShader", "()Z")),
                inject(ByteCode.IfNotZero(lightningSkip)));

        patch("Newer version mechanism - Render lightning with entity shader part 2", method,
                ByteCode.ALoad(22),
                ByteCode.DLoad(5),
                ByteCode.DLoad(7),
                ByteCode.DLoad(9),
                ByteCode.InvokeVirtual("vg", "g", "(DDD)Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.ALoad(22)),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "nextLightningEntity", "(Lvg;)V")),
                ByteCode.ALoad(0),
                ByteCode.GetField("buy", "j", "Lbzf;"),
                ByteCode.ALoad(22),
                ByteCode.FLoad(3),
                ByteCode.IConst(0),
                ByteCode.InvokeVirtual("bzf", "a", "(Lvg;FZ)V"));

        patch("Newer version mechanism - Render lightning with entity shader part 3", method,
                inject(lightningSkip),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "endLightningShader", "()V")),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                ByteCode.ALoad(0),
                ByteCode.GetField("buy", "k", "Lbsb;"),
                ByteCode.GetField("bsb", "E", "Lrl;"),
                ByteCode.Ldc("entities"),
                ByteCode.InvokeVirtual("rl", "c", "(Ljava/lang/String;)V"));
    }

    @MethodPatch("a(FI)V")
    public void renderSky(MethodNode method) {
        patch("Add SUNSET render stage configuration", method,
                ByteCode.IfNull(null),
                ByteCode.InvokeStatic("Config", "isSunMoonEnabled", "()Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("bus", "z", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ILoad(3),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "disableTexture2D", "()V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "SUNSET", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add SUN render stage configuration", method,
                ByteCode.InvokeStatic("Config", "isSunTexture", "()Z"),
                ByteCode.IfZero(null),
                inject(ByteCode.GetStatic(RENDERSTAGE, "SUN", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add MOON render stage configuration", method,
                ByteCode.InvokeStatic("Config", "isMoonTexture", "()Z"),
                ByteCode.IfZero(null),
                inject(ByteCode.GetStatic(RENDERSTAGE, "MOON", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add STARS render stage configuration", method,
                ByteCode.InvokeStatic("Config", "isStarsEnabled", "()Z"),
                ByteCode.IfZero(null),
                ByteCode.ALoad(0),
                ByteCode.GetField("buy", "k", "Lbsb;"),
                ByteCode.InvokeStatic("net/optifine/CustomSky", "hasSkyLayers", "(Lamu;)Z"),
                ByteCode.IfNotZero(null),
                inject(ByteCode.GetStatic(RENDERSTAGE, "STARS", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add VOID render stage configuration", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.GetField("buy", "h", "Lbib;"),
                ByteCode.GetField("bib", "h", "Lbud;"),
                ByteCode.FLoad(1),
                ByteCode.InvokeVirtual("bud", "f", "(F)Lbhe;"),
                ByteCode.GetField("bhe", "c", "D"),
                ByteCode.ALoad(0),
                ByteCode.GetField("buy", "k", "Lbsb;"),
                ByteCode.InvokeVirtual("bsb", "ad", "()D"),
                ByteCode.DSub(),
                ByteCode.DStore(14),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.DLoad(14),
                ByteCode.DConst(0),
                ByteCode.DCmpG(),
                ByteCode.IfGreaterEqualZero(null),
                inject(ByteCode.GetStatic(RENDERSTAGE, "VOID", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }

    @MethodPatch("a(Lvg;F)V")
    public void renderWorldBorder(MethodNode method) {
        patch("Add WORLD_BORDER render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "useProgram", "(Lnet/optifine/shaders/Program;)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "WORLD_BORDER", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Reset render stage configuration", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "popProgram", "()V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));
    }
    
    @MethodPatch("a(Lbuk;DDDDDDFFFF)V")
    public void drawBoundingBox(MethodNode method) {
        patch("Outline semi-transparency fix 1", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.DLoad(1),
                ByteCode.DLoad(9),
                ByteCode.DLoad(5),
                ByteCode.InvokeVirtual("buk", "b", "(DDD)Lbuk;"),
                ByteCode.FLoad(13),
                ByteCode.FLoad(14),
                ByteCode.FLoad(15),
                remove(ByteCode.FConst(0)),
                inject(ByteCode.FLoad(16)));

        patch("Outline semi-transparency fix 2", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.DLoad(7),
                ByteCode.DLoad(3),
                ByteCode.DLoad(11),
                ByteCode.InvokeVirtual("buk", "b", "(DDD)Lbuk;"),
                ByteCode.FLoad(13),
                ByteCode.FLoad(14),
                ByteCode.FLoad(15),
                remove(ByteCode.FConst(0)),
                inject(ByteCode.FLoad(16)));

        patch("Outline semi-transparency fix 3", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(0),
                ByteCode.DLoad(7),
                ByteCode.DLoad(9),
                ByteCode.DLoad(5),
                ByteCode.InvokeVirtual("buk", "b", "(DDD)Lbuk;"),
                ByteCode.FLoad(13),
                ByteCode.FLoad(14),
                ByteCode.FLoad(15),
                remove(ByteCode.FConst(0)),
                inject(ByteCode.FLoad(16)));

        patch("Prevent outline streaking", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.DLoad(7)),
                inject(ByteCode.DLoad(3)),
                inject(ByteCode.DLoad(5)),
                inject(ByteCode.InvokeVirtual("buk", "b", "(DDD)Lbuk;")),
                inject(ByteCode.FLoad(13)),
                inject(ByteCode.FLoad(14)),
                inject(ByteCode.FLoad(15)),
                inject(ByteCode.FConst(0)),
                inject(ByteCode.InvokeVirtual("buk", "a", "(FFFF)Lbuk;")),
                inject(ByteCode.InvokeVirtual("buk", "d", "()V")),
                ByteCode.Return());
    }
}
