package com.bigchadguys.fishing.util;

import java.util.Random;

public class FishingMinigameLogic {
    public static final int PROGRESS_BAR_WIDTH = 180;
    public static final int HOOK_WIDTH = 11;
    public static final float VERTICAL_OSCILLATION_SPEED = 0.05f;
    public static final float BASE_HOOK_SPEED = 0.25f;
    public static final float BONUS_RATE = 0.0015f;
    public static final float BONUS_DECREASE_RATE = 0.00125f;
    public static final int CHEST_WIDTH = 16;
    public static final int CHEST_HEIGHT = 16;
    public static final int HOOK_TRACK_OFFSET_X = 8;
    public static final int HOOK_TRACK_OFFSET_Y = 8;
    public static final int VERTICAL_BOB_AMPLITUDE = 1;
    public static final float TREASURE_HOVER_THRESHOLD = 120f;
    public static final float INITIAL_BONUS_PROGRESS = 0.25f;
    public static final int CHEST_Y_OFFSET = 9;

    private final float targetSpeed;
    public float hookXPos;
    public float hookTime;
    public float bonusProgress;
    public boolean hasTouchedFish;
    public boolean hasActivatedHook;
    public boolean isButtonHeld;
    public boolean treasureVisible;
    public boolean treasureActive;
    public boolean treasureClaimed;
    public int treasureChestX;
    public int treasureChestY;
    public float treasureHoverTime;
    public float chestVerticalAnimTime;
    public float scaledHookSpeed;
    public float scaledBonusRate;
    public float scaledChestHoverThreshold;
    public final float difficultyMultiplier;
    public final float fishWidth;
    public final int rodTier;
    public final Random random = new Random();
    public float currentTargetPos;
    public float targetStartPos;
    public float targetGoalPos;
    public int interpolationTicksLeft;
    public int totalInterpolationTicks;
    private float cachedSinHookTime;

    public FishingMinigameLogic(int rodTier, float fishWidth, float difficultyMultiplier, boolean treasureVisible) {
        this.rodTier = rodTier;
        this.fishWidth = fishWidth;
        this.difficultyMultiplier = difficultyMultiplier;
        this.treasureVisible = treasureVisible;
        this.targetSpeed = 0.25f * difficultyMultiplier;
        hookXPos = PROGRESS_BAR_WIDTH * 0.25f;
        hookTime = 0f;
        bonusProgress = INITIAL_BONUS_PROGRESS;
        hasActivatedHook = false;
        hasTouchedFish = false;
        isButtonHeld = false;
        treasureActive = false;
        treasureClaimed = false;
        treasureHoverTime = 0f;
        chestVerticalAnimTime = 0f;
        scaledHookSpeed = BASE_HOOK_SPEED * (1 + (rodTier - 1) * 0.1f);
        scaledBonusRate = BONUS_RATE * (1 + (rodTier - 1) * 0.1f);
        scaledChestHoverThreshold = TREASURE_HOVER_THRESHOLD / (1 + (rodTier - 1) * 0.1f);
        currentTargetPos = PROGRESS_BAR_WIDTH * 0.75f;
        setNewTargetGoal();
        cachedSinHookTime = (float) Math.sin(hookTime);
    }

    private void setNewTargetGoal() {
        targetStartPos = currentTargetPos;
        float maxPos = PROGRESS_BAR_WIDTH - fishWidth;
        targetGoalPos = random.nextFloat() * maxPos;
        float distance = Math.abs(targetGoalPos - currentTargetPos);
        totalInterpolationTicks = (int) Math.ceil(distance / targetSpeed);
        if (totalInterpolationTicks < 1) {
            totalInterpolationTicks = 1;
        }
        interpolationTicksLeft = totalInterpolationTicks;
    }

    public void tick() {
        hookTime += VERTICAL_OSCILLATION_SPEED;
        cachedSinHookTime = (float) Math.sin(hookTime);
        if (hasActivatedHook) {
            hookXPos += isButtonHeld ? scaledHookSpeed : -scaledHookSpeed;
            float hookMax = PROGRESS_BAR_WIDTH - HOOK_WIDTH;
            hookXPos = Math.max(0f, Math.min(hookXPos, hookMax));
            boolean fishCollision = hookXPos < currentTargetPos + fishWidth && hookXPos + HOOK_WIDTH > currentTargetPos;
            if (fishCollision) {
                bonusProgress = Math.min(bonusProgress + scaledBonusRate, 1.0f);
                hasTouchedFish = true;
                if (treasureVisible && !treasureActive) {
                    treasureActive = true;
                    treasureChestX = random.nextInt(PROGRESS_BAR_WIDTH - CHEST_WIDTH + 1);
                    int targetYOffset = (int)(((cachedSinHookTime + 1) / 2) * 6);
                    treasureChestY = HOOK_TRACK_OFFSET_Y + targetYOffset + CHEST_Y_OFFSET;
                }
            } else if (hasTouchedFish) {
                bonusProgress = Math.max(bonusProgress - BONUS_DECREASE_RATE, 0f);
            }
        }
        if (interpolationTicksLeft > 0) {
            float t = (float)(totalInterpolationTicks - interpolationTicksLeft + 1) / totalInterpolationTicks;
            currentTargetPos = targetStartPos + t * (targetGoalPos - targetStartPos);
            interpolationTicksLeft--;
        } else {
            setNewTargetGoal();
        }
        chestVerticalAnimTime += 0.05f;
        if (treasureActive && !treasureClaimed) {
            int verticalOffset = (int)(Math.sin(chestVerticalAnimTime) * VERTICAL_BOB_AMPLITUDE);
            int hookScreenX = HOOK_TRACK_OFFSET_X + (int) hookXPos;
            int hookYOffset = (int)(((cachedSinHookTime + 1) / 2) * 4) * -1;
            int hookScreenY = HOOK_TRACK_OFFSET_Y + hookYOffset;
            if (hookScreenX < treasureChestX + CHEST_WIDTH && hookScreenX + HOOK_WIDTH > treasureChestX &&
                    hookScreenY < treasureChestY + verticalOffset + CHEST_HEIGHT && hookScreenY + CHEST_HEIGHT > treasureChestY + verticalOffset) {
                treasureHoverTime++;
                if (treasureHoverTime >= scaledChestHoverThreshold) {
                    treasureClaimed = true;
                }
            } else {
                treasureHoverTime = 0f;
            }
        }
    }

    public boolean isGameOver() {
        return bonusProgress <= 0f || bonusProgress >= 1.0f;
    }

    public int getHookScreenX() {
        return HOOK_TRACK_OFFSET_X + (int) hookXPos;
    }

    public int getHookScreenY() {
        int hookYOffset = (int)(((cachedSinHookTime + 1) / 2) * 4) * -1;
        return HOOK_TRACK_OFFSET_Y + hookYOffset;
    }

    public int getCurrentTargetX() {
        return HOOK_TRACK_OFFSET_X + (int) currentTargetPos;
    }

    public int getCurrentTargetYOffset() {
        return (int)(((cachedSinHookTime + 1) / 2) * 6);
    }
}
