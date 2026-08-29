package advancedshader.core.patcher.vanilla;

import org.lwjgl.opengl.GL11;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import advancedshader.core.patcher.Patcher;
import advancedshader.core.patcher.Patcher.ByteCode;
import advancedshader.core.patcher.Patcher.Patch;

@Patch("buq")
public class EntityRendererPatcher extends Patcher {

    @MethodPatch("a(IFJ)V")
    public void renderWorldPass(MethodNode method) {
        patch("Add Prepare shader", method,
                ByteCode.FLoad(2),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "setCamera", "(F)V"),
                inject(ByteCode.InvokeStatic(MORESTAGES, "renderPrepare", "()V")));

/*
        Sky
            +ifne terrain
        Cloud
            +label terrain
        Terrain
        Entities
        BlockEntities
            +ifne destroy
            +label outline
        Outline
        Debug
            +ifne hand
            +label destroy
        Destroy
            +ifne outline
            +label particle
        Lit Particle (not allowed to Lit)
        Particle
            +ifeq weather
            +render cloud
            +label weather
        RainSnow <- depthtex2 old
        WorldBorder
            +ifne cloudabove
            +label hand
        Hand <- depthtex2 new
        Deferred Composite
        Translucent
            +render tripwire
        Entity Debug
            +ifne particle
            +label cloudabove
            +ifne handwater
        Cloud (Above)
            +label handwater
        Hand (Translucent)
        Final Composite
*/

        LabelNode terrain = ByteCode.Label();
        LabelNode outline = ByteCode.Label();
        LabelNode destroy = ByteCode.Label();
        LabelNode particle = ByteCode.Label();
        LabelNode weather = ByteCode.Label();
        LabelNode hand = ByteCode.Label();
        LabelNode cloudabove = ByteCode.Label();
        LabelNode handwater = ByteCode.Label();

        LabelNode beginNoLitParticles = ByteCode.Label();
        LabelNode checkParticleEnd = ByteCode.Label();

        /*
         * inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
         * inject(ByteCode.IfNotZero(terrain)),
         */

        patch("Newer version rendering mechanism - Skip vanilla cloud rendering part 1", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                ByteCode.ALoad(0),
                ByteCode.IConst(0),
                ByteCode.FLoad(2),
                ByteCode.InvokeSpecial("buq", "a", "(IF)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.SIPush(GL11.GL_SMOOTH),
                ByteCode.InvokeStatic("bus", "j", "(I)V"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(terrain)));

        patch("Newer version rendering mechanism - Insert terrain rendering jump point", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(terrain),
                ByteCode.ALoad(0),
                ByteCode.GetField("buq", "h", "Lbib;"),
                ByteCode.GetField("bib", "B", "Lrl;"),
                ByteCode.Ldc("prepareterrain"),
                ByteCode.InvokeVirtual("rl", "c", "(Ljava/lang/String;)V"));

        patch("Newer version rendering mechanism - Advance block breaking effect rendering order part 1", method,
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(destroy)),
                inject(outline),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ILoad(8),
                ByteCode.IfZero(null),
                ByteCode.ALoad(0),
                ByteCode.GetField("buq", "h", "Lbib;"),
                ByteCode.GetField("bib", "s", "Lbhc;"),
                ByteCode.IfNull(null),
                ByteCode.ALoad(11),
                ByteCode.GetStatic("bcz", "h", "Lbcz;"),
                ByteCode.InvokeVirtual("vg", "a", "(Lbcz;)Z"),
                ByteCode.IfNotZero(null));

        patch("Newer version rendering mechanism - Postpone particle rendering part 1", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(hand)),
                inject(destroy),
                ByteCode.ALoad(6),
                ByteCode.GetField("buy", "x", "Ljava/util/Map;"),
                ByteCode.InvokeInterface("java/util/Map", "isEmpty", "()Z"),
                ByteCode.IfNotZero(null));

        patch("Newer version rendering mechanism - Advance block breaking effect rendering order part 2", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(outline)),
                inject(particle),
                ByteCode.SIPush(GL11.GL_SRC_ALPHA),
                ByteCode.SIPush(GL11.GL_ONE_MINUS_SRC_ALPHA),
                ByteCode.IConst(GL11.GL_ONE),
                ByteCode.IConst(GL11.GL_ZERO),
                ByteCode.InvokeStatic("bus", "a", "(IIII)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("bus", "l", "()V"));

        patch("Newer version rendering mechanism - Render all particles in textured shader", method,
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(beginNoLitParticles)),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "beginLitParticles", "()V"),
                inject(ByteCode.Goto(checkParticleEnd)),
                inject(beginNoLitParticles),
                inject(ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "beginParticles", "()V")),
                inject(checkParticleEnd));

        patch("Newer version rendering mechanism - Render clouds before rain/snow particles", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfZero(weather)),
                inject(ByteCode.ALoad(0)),
                inject(ByteCode.ALoad(6)),
                inject(ByteCode.FLoad(2)),
                inject(ByteCode.ILoad(1)),
                inject(ByteCode.DLoad(12)),
                inject(ByteCode.DLoad(14)),
                inject(ByteCode.DLoad(16)),
                inject(ByteCode.InvokeSpecial("buq", "a", "(Lbuy;FIDDD)V")),
                inject(weather),
                ByteCode.IConst(0),
                ByteCode.InvokeStatic("bus", "a", "(Z)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("Config", "isShaders", "()Z"),
                ByteCode.IfZero(null),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "isRainDepth", "()Z"));

        patch("Newer version rendering mechanism - Postpone particle rendering part 2", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(6),
                ByteCode.ALoad(11),
                ByteCode.FLoad(2),
                ByteCode.InvokeVirtual("buy", "a", "(Lvg;F)V"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(cloudabove)),
                inject(hand));

        patch("Newer version rendering mechanism - Render TRIPWIRE terrain", method,
                ByteCode.InvokeStatic("net/optifine/shaders/Shaders", "endWater", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.ALoad(6)),
                inject(ByteCode.FLoad(2)),
                inject(ByteCode.F2D()),
                inject(ByteCode.ILoad(1)),
                inject(ByteCode.ALoad(11)),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "renderTripwireTerrain", "(Lbuy;DILvg;)V")),
                ByteCode.GetStatic("net/optifine/reflect/Reflector", "ForgeHooksClient_setRenderPass", "Lnet/optifine/reflect/ReflectorMethod;"));

        patch("Newer version rendering mechanism - Postpone particle rendering part 3", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(particle)),
                inject(cloudabove),
                ByteCode.SIPush(GL11.GL_FLAT),
                ByteCode.InvokeStatic("bus", "j", "(I)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.IConst(1),
                ByteCode.InvokeStatic("bus", "a", "(Z)V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("bus", "q", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("bus", "l", "()V"),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.InvokeStatic("bus", "p", "()V"),
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "isForwardVersion", "()Z")),
                inject(ByteCode.IfNotZero(handwater)),
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.ALoad(11),
                ByteCode.GetField("vg", "q", "D"),
                ByteCode.ALoad(11),
                ByteCode.InvokeVirtual("vg", "by", "()F"),
                ByteCode.F2D(),
                ByteCode.DAdd());

        patch("Newer version rendering mechanism - Skip vanilla cloud rendering part 2", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(handwater),
                ByteCode.GetStatic("net/optifine/reflect/Reflector", "ForgeHooksClient_dispatchRenderLast", "Lnet/optifine/reflect/ReflectorMethod;"));

        patch("Add TERRAIN_TRANSLUCENT render stage configuration", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.GetStatic(RENDERSTAGE, "TERRAIN_TRANSLUCENT", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")),
                ByteCode.ALoad(6),
                ByteCode.GetStatic("amm", "d", "Lamm;"),
                ByteCode.FLoad(2),
                ByteCode.F2D(),
                ByteCode.ILoad(1),
                ByteCode.ALoad(11),
                ByteCode.InvokeVirtual("buy", "a", "(Lamm;DILvg;)I"),
                ByteCode.Pop(),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add OUTLINE render stage configuration", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                ByteCode.Frame(),
                inject(ByteCode.GetStatic(RENDERSTAGE, "OUTLINE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")),
                ByteCode.ALoad(6),
                ByteCode.ALoad(18),
                ByteCode.ALoad(0),
                ByteCode.GetField("buq", "h", "Lbib;"),
                ByteCode.GetField("bib", "s", "Lbhc;"),
                ByteCode.IConst(0),
                ByteCode.FLoad(2),
                ByteCode.InvokeVirtual("buy", "a", "(Laed;Lbhc;IF)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Add DEBUG render stage configuration", method,
                ByteCode.Label(),
                ByteCode.LineNumber(),
                inject(ByteCode.GetStatic(RENDERSTAGE, "DEBUG", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")),
                ByteCode.ALoad(0),
                ByteCode.GetField("buq", "h", "Lbib;"),
                ByteCode.GetField("bib", "p", "Lbyg;"),
                ByteCode.FLoad(2),
                ByteCode.LLoad(3),
                ByteCode.InvokeVirtual("byg", "a", "(FJ)V"),
                inject(ByteCode.GetStatic(RENDERSTAGE, "NONE", "Ladvancedshader/Hook$RenderStage;")),
                inject(ByteCode.InvokeStatic(RENDERSTAGE, "setRenderStage", "(Ladvancedshader/Hook$RenderStage;)V")));

        patch("Newer version rendering mechanism copy depthtex2", method,
                inject(ByteCode.InvokeStatic(FORWARDFEATURES, "prepareHand", "()V")),
                ByteCode.ALoad(0),
                ByteCode.FLoad(2),
                ByteCode.ILoad(1),
                ByteCode.InvokeStatic("net/optifine/shaders/ShadersRender", "renderHand0", "(Lbuq;FI)V"));
    }
}
