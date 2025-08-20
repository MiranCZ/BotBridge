package io.github.mirancz.botbridge.server.impl.input;

import io.github.mirancz.botbridge.api.input.InputKey;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class MouseInput {

    private final ServerPlayerEntity player;
    private final InteractionManager interactionManager;
    private int attackCooldown = 0;
    private int itemUseCooldown = 0;
    public HitResult crosshairTarget;

    public MouseInput(ServerPlayerEntity player) {
        this.player = player;
        this.interactionManager = new InteractionManager(player);
    }

    public void tick(InputKey useKey, InputKey attackKey) {
        if (attackCooldown > 0) attackCooldown--;
        if (itemUseCooldown > 0) itemUseCooldown--;

        updateCrosshairTarget(1);
        // TODO
        /*for(int i = 0; i < 9; ++i) {
            boolean bl = saveToolbarActivatorKey.isPressed();
            boolean bl2 = loadToolbarActivatorKey.isPressed();
            if (hotbarKeys[i].wasPressed()) {
                if (player.isSpectator()) {
                    this.inGameHud.getSpectatorHud().selectSlot(i);
                } else if (!player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
                    player.getInventory().selectedSlot = i;
                } else {
                    CreativeInventoryScreen.onHotbarKeyPress(this, i, bl2, bl);
                }
            }
        }*/



        /*while(inventoryKey.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
                player.openRidingInventory();
            } else {
                this.tutorialManager.onInventoryOpened();
                this.setScreen(new InventoryScreen(player));
            }
        }*/


        /*while(swapHandsKey.wasPressed()) {
            if (!player.isSpectator()) {
                this.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            }
        }*/

        /*while(dropKey.wasPressed()) {
            if (!player.isSpectator() && player.dropSelectedItem(Screen.hasControlDown())) {
                player.swingHand(Hand.MAIN_HAND);
            }
        }*/


        System.out.println("TICK");
        boolean bl3 = false;
        if (player.isUsingItem()) {
            System.out.println("IS USING AN ITEM!");
            if (!useKey.isPressed()) {
                System.out.println("Not pressed");
                this.interactionManager.stopUsingItem(player);
            }

            while(true) {
                if (!attackKey.wasPressed()) {
                    while(useKey.wasPressed()) {
                    }

                    break;
                }
            }
        } else {
            while(attackKey.wasPressed()) {
                bl3 |= this.doAttack();
            }

            while(useKey.wasPressed()) {
                System.out.println("starting item use!");
                this.doItemUse();
            }
            
        }

        if (useKey.isPressed() && this.itemUseCooldown == 0 && !player.isUsingItem()) {
            System.out.println("Outer loop item use");
            this.doItemUse();
        }

        this.handleBlockBreaking(/*this.currentScreen == null && */!bl3 && attackKey.isPressed() /*&& this.mouse.isCursorLocked()*/);
    }

    public void updateCrosshairTarget(float tickDelta) {
        Entity cameraEntity = player.getCameraEntity();
        if (cameraEntity == null) {
            return;
        }

//                this.client.getProfiler().push("pick");
        double d = player.getBlockInteractionRange();
        double e = player.getEntityInteractionRange();
        HitResult hitResult = this.findCrosshairTarget(cameraEntity, d, e, 1);
        crosshairTarget = hitResult;

        /*Entity targetedEntity;
        if (hitResult instanceof EntityHitResult entityHitResult) {
            targetedEntity = entityHitResult.getEntity();
        } else {
            targetedEntity = null;
        }*/

//        var10000.targetedEntity = targetedEntity;
//                this.client.getProfiler().pop();
    }

    private HitResult findCrosshairTarget(Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
        double d = Math.max(blockInteractionRange, entityInteractionRange);
        double e = MathHelper.square(d);
        Vec3d vec3d = camera.getCameraPosVec(tickDelta);
        HitResult hitResult = camera.raycast(d, tickDelta, false);
        double f = hitResult.getPos().squaredDistanceTo(vec3d);
        if (hitResult.getType() != HitResult.Type.MISS) {
            e = f;
            d = Math.sqrt(f);
        }

        Vec3d vec3d2 = camera.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float g = 1.0F;
        Box box = camera.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, vec3d, vec3d3, box, (entity) -> {
            return !entity.isSpectator() && entity.canHit();
        }, e);
        return entityHitResult != null && entityHitResult.getPos().squaredDistanceTo(vec3d) < f ? ensureTargetInRange(entityHitResult, vec3d, entityInteractionRange) : ensureTargetInRange(hitResult, vec3d, blockInteractionRange);
    }

    private static HitResult ensureTargetInRange(HitResult hitResult, Vec3d cameraPos, double interactionRange) {
        Vec3d vec3d = hitResult.getPos();
        if (!vec3d.isInRange(cameraPos, interactionRange)) {
            Vec3d vec3d2 = hitResult.getPos();
            Direction direction = Direction.getFacing(vec3d2.x - cameraPos.x, vec3d2.y - cameraPos.y, vec3d2.z - cameraPos.z);
            return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
        } else {
            return hitResult;
        }
    }


    private void handleBlockBreaking(boolean breaking) {
        if (!breaking) {
            this.attackCooldown = 0;
        }

        World world = player.getWorld();
        if (this.attackCooldown <= 0 && !this.player.isUsingItem()) {
            if (breaking && this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (!world.getBlockState(blockPos).isAir()) {
                    Direction direction = blockHitResult.getSide();
                    if (this.interactionManager.updateBlockBreakingProgress(blockPos, direction)) {
//                        this.particleManager.addBlockBreakingParticles(blockPos, direction);
                        this.player.swingHand(Hand.MAIN_HAND);
                    }
                }

            } else {
                this.interactionManager.cancelBlockBreaking();
            }
        }
    }


    private boolean doAttack() {
        World world = player.getWorld();

        if (this.attackCooldown > 0) {
            return false;
        } else if (this.crosshairTarget == null) {
//            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasLimitedAttackSpeed()) {
                this.attackCooldown = 10;
            }

            return false;
        } /*else if (this.player.isRiding()) { // TODO implement
            return false;
        }*/ else {
            ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
            if (!itemStack.isItemEnabled(world.getEnabledFeatures())) {
                return false;
            } else {
                boolean bl = false;
                switch (this.crosshairTarget.getType()) {
                    case ENTITY:
                        this.interactionManager.attackEntity(this.player, ((EntityHitResult)this.crosshairTarget).getEntity());
                        break;
                    case BLOCK:
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        if (!world.getBlockState(blockPos).isAir()) {
                            this.interactionManager.attackBlock(blockPos, blockHitResult.getSide());
                            if (world.getBlockState(blockPos).isAir()) {
                                bl = true;
                            }
                            break;
                        }
                    case MISS:
                        if (this.interactionManager.hasLimitedAttackSpeed()) {
                            this.attackCooldown = 10;
                        }

                        this.player.resetLastAttackedTicks();
                }

                this.player.swingHand(Hand.MAIN_HAND, true);
                return bl;
            }
        }
    }

    private void doItemUse() {
        if (this.interactionManager.isBreakingBlock()) {
            return;
        }
        this.itemUseCooldown = 4;
            /*if (this.player.isRiding()) { // TODO implement
                return;
            }*/
        if (this.crosshairTarget == null) {
//            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }

        World world = player.getWorld();

        Hand[] var1 = Hand.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Hand hand = var1[var3];
            ItemStack itemStack = this.player.getStackInHand(hand);
            if (!itemStack.isItemEnabled(world.getEnabledFeatures())) {
                return;
            }

            if (this.crosshairTarget != null) {
                switch (this.crosshairTarget.getType()) {
                    case ENTITY:
                        EntityHitResult entityHitResult = (EntityHitResult)this.crosshairTarget;
                        Entity entity = entityHitResult.getEntity();
                        if (!world.getWorldBorder().contains(entity.getBlockPos())) {
                            return;
                        }

                        ActionResult actionResult = this.interactionManager.interactEntityAtLocation(this.player, entity, entityHitResult, hand);
                        if (!actionResult.isAccepted()) {
                            actionResult = this.interactionManager.interactEntity(this.player, entity, hand);
                        }

                        if (actionResult.isAccepted()) {
                            if (actionResult.shouldSwingHand()) {
                                this.player.swingHand(hand);
                            }

                            return;
                        }
                        break;
                    case BLOCK:
                        BlockHitResult blockHitResult = (BlockHitResult)this.crosshairTarget;
                        int i = itemStack.getCount();
                        ActionResult actionResult2 = this.interactionManager.interactBlock(this.player, hand, blockHitResult);
                        if (actionResult2.isAccepted()) {
                            if (actionResult2.shouldSwingHand()) {
                                this.player.swingHand(hand);
                                if (!itemStack.isEmpty() && (itemStack.getCount() != i || this.interactionManager.hasCreativeInventory())) {
//                                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                                }
                            }

                            return;
                        }

                        if (actionResult2 == ActionResult.FAIL) {
                            return;
                        }
                }
            }

            if (!itemStack.isEmpty()) {
                ActionResult actionResult3 = this.interactionManager.interactItem(this.player, hand);
                if (actionResult3.isAccepted()) {
                    if (actionResult3.shouldSwingHand()) {
                        this.player.swingHand(hand);
                    }

//                    this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                    return;
                }
            }
        }

    }

}
