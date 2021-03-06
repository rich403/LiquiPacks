package com.michael.e.liquislots.item;

import com.michael.e.liquislots.Liquislots;
import com.michael.e.liquislots.Reference;
import com.michael.e.liquislots.common.upgrade.LiquipackUpgrade;
import com.michael.e.liquislots.common.util.LiquipackStack;
import com.michael.e.liquislots.common.util.LiquipackTank;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import org.lwjgl.input.Keyboard;

import java.util.Collections;
import java.util.List;

public class ItemLiquipack extends ItemArmor implements ISpecialArmor{

    public static ArmorMaterial liquipackMaterial = EnumHelper.addArmorMaterial("liquipackMaterial", 100, new int[]{0,0,0,0}, 0);

    public ItemLiquipack() {
        super(liquipackMaterial, 0, 1);
        setUnlocalizedName("liquipack");
        setCreativeTab(Liquislots.INSTANCE.tabLiquipacks);
        setTextureName(Reference.MOD_ID + ":liquipack1");
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        LiquipackStack liquipackStack = new LiquipackStack(stack);
        return liquipackStack.getArmor() != null && liquipackStack.getArmor().getItemDamage() != 0;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return Reference.MOD_ID + ":models/armor/liquipackNew.png";
    }

    public static boolean isOldFormat(ItemStack stack){
        return stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("tanks") && stack.getTagCompound().getTag("tanks").getId() == 9;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean debug) {
        if(debug && stack.getTagCompound() != null){
            String[] nbt = stack.getTagCompound().toString().split("(?<=\\G.{80})");
            Collections.addAll(info, nbt);
        }
        //Message for old format liquipacks
        if(isOldFormat(stack)){
            info.add(EnumChatFormatting.DARK_RED + "This item is from an older version,");
            info.add(EnumChatFormatting.DARK_RED + "to update it to the new version place it in a crafting table");
            return;
        }
        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
            info.add(EnumChatFormatting.AQUA.toString() + EnumChatFormatting.ITALIC + "<Press SHIFT for more info>");
        }
        else {
            LiquipackStack liquipackStack = new LiquipackStack(stack);
            LiquipackTank[] tanks = liquipackStack.getTanks();
            LiquipackUpgrade[] upgrades = liquipackStack.getUpgrades();

            if(liquipackStack.getTankCount() == 0){
                info.add(EnumChatFormatting.RED + "This item is useless without any tanks");
                info.add(EnumChatFormatting.RED + "Add tanks by putting them in a crafting");
                info.add(EnumChatFormatting.RED + "table with the liquipack");
            }

            int i = -1;
            for (LiquipackTank tank : tanks) {
                i++;
                if (tank != null) {
                    String containsText = tank.getFluid() == null ? "Nothing" : tank.getFluidAmount() + "x" + tank.getFluidType().getLocalizedName(tank.getFluid());
                    info.add(EnumChatFormatting.DARK_AQUA + "Tank " + (i + 1) + " | Capacity: " + tank.getCapacity() + "mb | Contains: " + containsText);
                }
            }

            ItemStack protection = liquipackStack.getArmor();
            if(protection != null){
                info.add(EnumChatFormatting.DARK_GREEN + "Installed Armor: " + protection.getDisplayName() + " | Damage: " + (protection.getMaxDamage() - protection.getItemDamage()) + "/" + protection.getMaxDamage());
            }

            if(upgrades.length > 0) {
                info.add(EnumChatFormatting.BLUE + "Installed Upgrades:");
                for (LiquipackUpgrade upgrade : upgrades) {
                    info.add(EnumChatFormatting.BLUE + "- " + StatCollector.translateToLocal("liquipackupgrade." + upgrade.getType().name()));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        return Liquislots.proxy.getModel();
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        LiquipackStack stack = new LiquipackStack(armor);
        if(stack.getArmor() != null){
            return ((ILiquipackArmor)stack.getArmor().getItem()).getProtectionProps(stack.getArmor());
        }
        return new ArmorProperties(0, 0, 0);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        LiquipackStack liquipackStack = new LiquipackStack(armor);
        if(liquipackStack.getArmor() == null)return 0;
        ArmorProperties protection = ((ILiquipackArmor)liquipackStack.getArmor().getItem()).getProtectionProps(armor);
        return (int)(protection.AbsorbRatio * 25D);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        LiquipackStack liquipackStack = new LiquipackStack(stack);
        ItemStack protectorStack = liquipackStack.getArmor();
        if(protectorStack != null) {
            protectorStack.damageItem(damage, entity);
            if(protectorStack.getItemDamage() < protectorStack.getMaxDamage()) {
                liquipackStack.setArmor(protectorStack);
            }
            else{
                liquipackStack.removeArmor();
            }
        }
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        ItemStack protection = new LiquipackStack(stack).getArmor();
        return protection != null ? 1.0 - (protection.getItemDamageForDisplay() / protection.getItemDamage()) : 0;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if(world.isRemote || player == null)return;
        LiquipackStack stack = new LiquipackStack(itemStack);
        for(LiquipackUpgrade upgrade : stack.getUpgrades()){
            upgrade.getType().tick(world, player, stack, upgrade);
        }
    }
}
