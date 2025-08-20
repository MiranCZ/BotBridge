package io.github.mirancz.botbridge.server.impl.input;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class InteractionManager {

    private final ServerPlayerEntity player;
    private boolean breakingBlock = false;
    private float currentBreakingProgress = 0;
    private BlockPos currentBreakingPos;
    private int blockBreakingCooldown = 0;
    private ItemStack selectedStack = ItemStack.EMPTY;

    public InteractionManager(ServerPlayerEntity player) {
        this.player = player;
    }

    public void stopUsingItem(ServerPlayerEntity player) {
        player.stopUsingItem();
    }

    public boolean hasLimitedAttackSpeed() {
        return !player.interactionManager.isCreative();

    }

    public boolean hasCreativeInventory() {
        return player.interactionManager.isCreative();
    }

    public void cancelBlockBreaking() {
        if (this.breakingBlock) {
//            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));

            player.interactionManager.processBlockBreakingAction(this.currentBreakingPos, PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, this.player.getWorld().getTopY(), 0);
            this.breakingBlock = false;
            this.currentBreakingProgress = 0.0F;
//            this.client.world.setBlockBreakingInfo(this.client.player.getId(), this.currentBreakingPos, -1);
//            this.client.player.resetLastAttackedTicks();
        }
    }

    public boolean isBreakingBlock() {
        return breakingBlock;
    }

    public ActionResult interactItem(ServerPlayerEntity player, Hand hand) {
        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            return ActionResult.PASS;
        } else {
//            this.syncSelectedSlot();
//            MutableObject<ActionResult> mutableObject = new MutableObject();
//            this.sendSequencedPacket(this.client.world, (sequence) -> {

//            int sequence = 0; // TODO
//            PlayerInteractItemC2SPacket playerInteractItemC2SPacket = new PlayerInteractItemC2SPacket(hand, sequence, player.getYaw(), player.getPitch());

            return interactItem(hand);
        }
    }

    ActionResult interactItem(Hand hand) {
        ServerWorld serverWorld = this.player.getServerWorld();

        ItemStack itemStack = this.player.getStackInHand(hand);
        this.player.updateLastActionTime();
        if (!itemStack.isEmpty() && itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            float f = MathHelper.wrapDegrees(player.getYaw());
            float g = MathHelper.wrapDegrees(player.getPitch());
            if (g != this.player.getPitch() || f != this.player.getYaw()) {
                this.player.setAngles(f, g);
            }

            ActionResult actionResult = this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
            if (actionResult.shouldSwingHand()) {
                this.player.swingHand(hand, true);
            }

            return actionResult;
        }

        return ActionResult.PASS;
    }

    public void attackEntity(ServerPlayerEntity player, Entity entity) {
        World world = player.getWorld();

        if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity) && entity != this.player) {
            label29:
            {
                if (entity instanceof PersistentProjectileEntity) {
                    PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) entity;
                    if (!persistentProjectileEntity.isAttackable()) {
                        break label29;
                    }
                }

                ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
                if (!itemStack.isItemEnabled(world.getEnabledFeatures())) {
                    return;
                }

                this.player.attack(entity);
                return;
            }
        }
    }

    public ActionResult interactEntity(ServerPlayerEntity player, Entity entity, Hand hand) {
        return this.processInteract(hand, PlayerEntity::interact, entity);
    }

    public ActionResult interactEntityAtLocation(ServerPlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand) {
        Vec3d pos = hitResult.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());

        return this.processInteract(hand, (playerx, entityx, handx) -> {
            return entityx.interactAt(playerx, pos, handx);
        }, entity);

    }

    public boolean attackBlock(BlockPos pos, Direction side) {
        World world = player.getWorld();
        GameMode gameMode = player.interactionManager.getGameMode();

        if (player.isBlockBreakingRestricted(world, pos, gameMode)) {
            return false;
        } else if (!world.getWorldBorder().contains(pos)) {
            return false;
        }

        System.out.println("attackBlock");
        if (gameMode.isCreative()) {
            player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, side, this.player.getWorld().getTopY(), 0);
            this.blockBreakingCooldown = 5;
        }else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
            System.out.println("not breaking?" + this.isCurrentlyBreaking(pos) + " ; "+currentBreakingPos);
            if (this.breakingBlock) {
                System.out.println("stopped cuz block changed");
                player.interactionManager.processBlockBreakingAction(currentBreakingPos, PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, side, this.player.getWorld().getTopY(), 0);
            }

            BlockState blockState = world.getBlockState(pos);
            if (blockState.isAir() && blockState.calcBlockBreakingDelta(player, world, pos) >= 1.0F) {
//                this.breakBlock(pos);
            } else {
                System.out.println("start break "+pos);
                this.breakingBlock = true;
                this.currentBreakingPos = pos;
                this.selectedStack = player.getMainHandStack();
                this.currentBreakingProgress = 0.0F;

                player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, side, this.player.getWorld().getTopY(), 0);

            }

        }


        return true;
    }

    public ActionResult interactBlock(ServerPlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        ServerWorld serverWorld = this.player.getServerWorld();
        ItemStack itemStack = this.player.getStackInHand(hand);

        if (itemStack.isItemEnabled(serverWorld.getEnabledFeatures())) {
            Vec3d vec3d = blockHitResult.getPos();
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (this.player.canInteractWithBlockAt(blockPos, 1.0)) {
                Vec3d vec3d2 = vec3d.subtract(Vec3d.ofCenter(blockPos));
                double d = 1.0000001;
                if (Math.abs(vec3d2.getX()) < d && Math.abs(vec3d2.getY()) < d && Math.abs(vec3d2.getZ()) < d) {
                    Direction direction = blockHitResult.getSide();
//                    this.player.updateLastActionTime();
                    int i = this.player.getWorld().getTopY();
                    if (blockPos.getY() < i) {
                        if (/*this.requestedTeleportPos == null && */serverWorld.canPlayerModifyAt(this.player, blockPos)) {
                            ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
                            if (actionResult.isAccepted()) {
                                Criteria.ANY_BLOCK_USE.trigger(this.player, blockHitResult.getBlockPos(), itemStack.copy());
                            }

                            if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= i - 1 && canPlace(this.player, itemStack)) {
//                                Text text = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
//                                this.player.sendMessageToClient(text, true);
                            } else if (actionResult.shouldSwingHand()) {
                                this.player.swingHand(hand, true);
                            }
                            return actionResult;
                        }
                    } else {
                        Text text2 = Text.translatable("build.tooHigh", i - 1).formatted(Formatting.RED);
                        this.player.sendMessageToClient(text2, true);
                        return ActionResult.FAIL;
                    }

//                    this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
//                    this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
                } else {
//                    LOGGER.warn("Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.", this.player.getGameProfile().getName(), vec3d, blockPos);
                    return ActionResult.FAIL;
                }
            }
        }

        return ActionResult.PASS;
    }

    private static boolean canPlace(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            Item item = stack.getItem();
            return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(item);
        }
    }

    public boolean updateBlockBreakingProgress(BlockPos pos, Direction side) {
        if (this.blockBreakingCooldown > 0) {
            --this.blockBreakingCooldown;
            return true;
        }
        World world = player.getWorld();
        GameMode gameMode = player.interactionManager.getGameMode();

        if (gameMode.isCreative() && world.getWorldBorder().contains(pos)) {
            this.blockBreakingCooldown = 5;
            player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, side, this.player.getWorld().getTopY(), 0);

            return true;
        } else if (isCurrentlyBreaking(pos)) {
            System.out.println("is breaking");
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isAir()) {
                this.breakingBlock = false;
                return false;
            } else {
                this.currentBreakingProgress += blockState.calcBlockBreakingDelta(player, world, pos);

                if (this.currentBreakingProgress >= 1.0F) {
                    System.out.println("stop destroy cuz broken");
                    this.breakingBlock = false;
                    player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, side, this.player.getWorld().getTopY(), 0);

                    this.currentBreakingProgress = 0.0F;
                    this.blockBreakingCooldown = 5;
                }

                return true;
            }


        }

        return this.attackBlock(pos, side);
    }

    private boolean isCurrentlyBreaking(BlockPos pos) {
        ItemStack itemStack = player.getMainHandStack();
        return pos.equals(this.currentBreakingPos) && ItemStack.areItemsAndComponentsEqual(itemStack, this.selectedStack);
    }

    @FunctionalInterface
    private interface Interaction {
        ActionResult run(ServerPlayerEntity player, Entity entity, Hand hand);
    }

    private ActionResult processInteract(Hand hand, Interaction action, Entity entity) {
        World world = player.getWorld();

        ItemStack itemStack = this.player.getStackInHand(hand);
        if (itemStack.isItemEnabled(world.getEnabledFeatures())) {
            ItemStack itemStack2 = itemStack.copy();
            ActionResult actionResult = action.run(player, entity, hand);
            if (actionResult.isAccepted()) {
                Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(this.player, actionResult.shouldIncrementStat() ? itemStack2 : ItemStack.EMPTY, entity);
                if (actionResult.shouldSwingHand()) {
                    this.player.swingHand(hand, true);
                }
            }

            return actionResult;

        }

        return ActionResult.PASS;
    }

}
