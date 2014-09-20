package com.michael.e.liquislots.block;

import com.michael.e.liquislots.Liquislots;
import com.michael.e.liquislots.Reference;
import com.michael.e.liquislots.client.renderers.LiquipackIORenderer;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockLiquipackIO extends BlockContainer {

    @SideOnly(Side.CLIENT)
    private IIcon blockTexture;

    protected BlockLiquipackIO() {
        super(Material.iron);
        setBlockName("liquipackIO");
        setCreativeTab(Liquislots.INSTANCE.tabLiquipacks);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockTexture = register.registerIcon(Reference.MOD_ID + ":" + getUnlocalizedName().substring(5));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return blockTexture;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityLiquipackIO();
    }

    @Override
    public int getRenderType() {
        return LiquipackIORenderer.rendererID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(!world.isRemote){
            FMLNetworkHandler.openGui(player, Liquislots.INSTANCE, 1, world, x, y, z);
        }else{
            if(player.isSneaking())
            {
                TileEntityLiquipackIO te = (TileEntityLiquipackIO) world.getTileEntity(x, y, z);
                if(te.buffer.getFluid() != null) {
                    player.addChatComponentMessage(new ChatComponentText(te.buffer.getFluid().getFluid().getName() + " " + te.buffer.getFluid().amount));
                }
                player.addChatComponentMessage(new ChatComponentText(te.isDrainingMode() + " " + te.getTank()));
            }
        }
        return true;
    }


}
