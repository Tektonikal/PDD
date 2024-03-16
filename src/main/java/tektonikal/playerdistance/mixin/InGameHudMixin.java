package tektonikal.playerdistance.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tektonikal.playerdistance.ColorModes;
import tektonikal.playerdistance.Modes;
import tektonikal.playerdistance.config.PDcfg;

import java.util.Objects;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract TextRenderer getTextRenderer();


    @Inject(at = @At("TAIL"), method = "render")
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        //if you think this is messy, you should have seen what I was trying to do before
        //today I learned a valuable lesson: I'd rather rewrite my old code than add more features to my old code
        if (PDcfg.enabled) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            assert player != null;
            String displayString;
            Entity closestToPlayer = null;
            Entity closestToRay = null;
            float distance = Integer.MAX_VALUE;
            if (getClosest(player.getPos()) != null) {
                closestToPlayer = getClosest(player.getPos());
            }
            assert client.crosshairTarget != null;
            if (getClosest(client.crosshairTarget.getPos()) != null) {
                closestToRay = getClosest(client.crosshairTarget.getPos());
            }
            if (PDcfg.mode.equals(Modes.DISTANCE)) {
                if (closestToPlayer == null || closestToRay == null) {
                    displayString = PDcfg.prefix + "None" + PDcfg.suffix;
                } else {
                    if(client.crosshairTarget.getType().equals(HitResult.Type.ENTITY)){
                        displayString = PDcfg.prefix +
                                (PDcfg.showName && PDcfg.nameFirst ? ((EntityHitResult)client.crosshairTarget).getEntity().getDisplayName().getString() + " - " : "") +
                                String.format("%.2f", player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), ((EntityHitResult)client.crosshairTarget).getEntity().getBoundingBox()))) +
                                (PDcfg.showName && !PDcfg.nameFirst ? " - " + ((EntityHitResult)client.crosshairTarget).getEntity().getDisplayName().getString() : "") +
                                PDcfg.suffix;
                        distance = (float) player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), ((EntityHitResult)client.crosshairTarget).getEntity().getBoundingBox()));
                    }
                    else{
                    displayString = PDcfg.prefix +
                            (PDcfg.showName && PDcfg.nameFirst ? closestToPlayer.getDisplayName().getString() + " - " : "") +
                            String.format("%.2f", player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), closestToPlayer.getBoundingBox()))) +
                            (PDcfg.showName && !PDcfg.nameFirst ? " - " +  closestToPlayer.getDisplayName().getString() : "") +
                            PDcfg.suffix;
                    distance = (float) player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), closestToPlayer.getBoundingBox()));
                    }
                }
            } else {
                if (closestToPlayer == null || closestToRay == null) {
                    displayString = PDcfg.prefix + "None" + PDcfg.suffix;
                } else {
                    if(client.crosshairTarget.getType().equals(HitResult.Type.ENTITY)){
                        displayString = PDcfg.prefix +
                                (PDcfg.showName && PDcfg.nameFirst ? ((EntityHitResult)client.crosshairTarget).getEntity().getDisplayName().getString() + " - " : "") +
                                String.format("%.2f", (client.crosshairTarget).getPos().distanceTo(closestPointToBox(player.getEyePos(), ((EntityHitResult)client.crosshairTarget).getEntity().getBoundingBox()))) +
                                (PDcfg.showName && !PDcfg.nameFirst ? " - " + ((EntityHitResult)client.crosshairTarget).getEntity().getDisplayName().getString() : "") +
                                PDcfg.suffix;
                        distance = (float) (client.crosshairTarget).getPos().distanceTo(closestPointToBox(player.getEyePos(), ((EntityHitResult)client.crosshairTarget).getEntity().getBoundingBox()));
                    }
                    else{
                    displayString = PDcfg.prefix +
                            (PDcfg.showName && PDcfg.nameFirst ? closestToRay.getDisplayName().getString() + " - " : "") +
                            String.format("%.2f", client.crosshairTarget.getPos().distanceTo(closestPointToBox(player.getEyePos(), closestToRay.getBoundingBox()))) +
                            (PDcfg.showName && !PDcfg.nameFirst ? " - " +  closestToRay.getDisplayName().getString() : "") +
                            PDcfg.suffix;
                        distance = (float) player.getEyePos().distanceTo(closestPointToBox(player.getEyePos(), closestToRay.getBoundingBox()));
                    }
                }
            }
            if(closestToPlayer != null && closestToRay != null){
//                renderText(matrices, displayString, getPos(displayString).x, getPos(displayString).y, distance);
                renderText(matrices, displayString, getPos(displayString).x, getPos(displayString).y, distance);
            }
            else{
//                renderText(matrices, displayString, getPos(displayString).x, getPos(displayString).y, Integer.MAX_VALUE);
                renderText(matrices, displayString, getPos(displayString).x, getPos(displayString).y, Integer.MAX_VALUE);
            }
        }
    }

    @Unique
    private void renderText(MatrixStack matrices, String text, float x, float y, double distance) {
        //just got tired of logic errors so just did it in the most easy to follow way
        try {
            if (PDcfg.changeInRange) {
                if (PDcfg.cm.equals(ColorModes.CANREACH)) {
                    this.getTextRenderer().draw(matrices, text, (int) x, (int) y, distance <= 3 ? (int) Long.parseLong(PDcfg.InRangeCol, 16) : (int) Long.parseLong(PDcfg.MainCol, 16));
//                    context.drawText(this.getTextRenderer(), text, (int) x, (int) y, distance <= 3 ? (int) Long.parseLong(PDcfg.InRangeCol, 16) : (int) Long.parseLong(PDcfg.MainCol, 16), PDcfg.shadow);
                }
                if (PDcfg.cm.equals(ColorModes.CANATTACK)) {
                    if (Objects.requireNonNull(client.crosshairTarget).getType().equals(HitResult.Type.ENTITY)) {
                        this.getTextRenderer().draw(matrices, text, (int) x, (int) y, (int) Long.parseLong(PDcfg.InRangeCol, 16));
//                        context.drawText(this.getTextRenderer(), text, (int) x, (int) y, (int) Long.parseLong(PDcfg.InRangeCol, 16), PDcfg.shadow);
                    } else {
                        this.getTextRenderer().draw(matrices, text, (int) x, (int) y, (int) Long.parseLong(PDcfg.MainCol, 16));
//                        context.drawText(this.getTextRenderer(), text, (int) x, (int) y, (int) Long.parseLong(PDcfg.MainCol, 16), PDcfg.shadow);
                    }
                }
            } else {
                this.getTextRenderer().draw(matrices, text, (int) x, (int) y, (int) Long.parseLong(PDcfg.MainCol, 16));
//                context.drawText(this.getTextRenderer(), text, (int) x, (int) y, (int) Long.parseLong(PDcfg.MainCol, 16), PDcfg.shadow);
            }
        } catch (Exception e) {
            this.getTextRenderer().draw(matrices, text, (int) x, (int) y, 0xffff0000);
//            context.drawText(this.getTextRenderer(), text, (int) x, (int) y, 0xffff0000, PDcfg.shadow);
        }
    }

    @Unique
    private Entity getClosest(Vec3d pos) {
        assert client.world != null;
        Entity cur = null;
        for(Entity e : client.world.getEntities()){
            if(e == client.player || !e.isAlive() || e.isInvisible() || e instanceof ItemEntity){
                continue;
            }
            if(cur == null){
                cur = e;
            }
            if(cur.getPos().distanceTo(pos) > e.getPos().distanceTo(pos)){
                cur = e;
            }
        }
        return cur;
    }

    @Unique
    public Vec3d closestPointToBox(Vec3d start, Box box) {
        return new Vec3d(coerceIn(start.x, box.minX, box.maxX), coerceIn(start.y, box.minY, box.maxY), coerceIn(start.z, box.minZ, box.maxZ));
    }

    @Unique
    public double coerceIn(double target, double min, double max) {
        if (target > max) {
            return max;
        }
        return Math.max(target, min);
    }

    @Unique
    public Vec2f getPos(String displayString) {
        float x = 0;
        float y = 0;
        switch (PDcfg.pos) {
            case TOP_LEFT -> {
                x = 2 + PDcfg.xOffset;
                y = 2 + PDcfg.yOffset;
            }
            case TOP_RIGHT -> {
                x = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(displayString) - 2 + PDcfg.xOffset;
                y = 2 + PDcfg.yOffset;
            }
            case BOTTOM_LEFT -> {
                x = 2 + PDcfg.xOffset;
                y = client.getWindow().getScaledHeight() - client.textRenderer.fontHeight - 2 + PDcfg.yOffset;
            }
            case BOTTOM_RIGHT -> {
                x = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(displayString) - 2 + PDcfg.xOffset;
                y = client.getWindow().getScaledHeight() - client.textRenderer.fontHeight - 2 + PDcfg.yOffset;
            }
            case CENTER -> {
                y = client.getWindow().getScaledHeight() / 2.0F + PDcfg.xOffset;
                x = client.getWindow().getScaledWidth() / 2.0F - (client.textRenderer.getWidth(displayString) / 2.0F) + PDcfg.yOffset;
            }
            default -> System.out.println("blehhh!");
        }
        return new Vec2f(x, y);
    }
}