package com.michael.e.liquislots.item;

import com.michael.e.liquislots.common.upgrade.LiquidXPUpgrade;
import com.michael.e.liquislots.common.util.LiquipackUpgrade;
import net.minecraft.item.ItemStack;

public class ItemLiquidXPUpgrade extends ItemLiquipacksBase implements ILiquipackUpgrade {

    public static final LiquidXPUpgrade UPGRADE = new LiquidXPUpgrade();

    public ItemLiquidXPUpgrade() {
        super();
    }

    @Override
    public LiquipackUpgrade getUpgradeForStack(ItemStack stack) {
        return UPGRADE;
    }
}