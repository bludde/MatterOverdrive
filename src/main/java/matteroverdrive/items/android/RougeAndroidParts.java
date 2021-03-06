/*
 * This file is part of Matter Overdrive
 * Copyright (c) 2015., Simeon Radivoev, All rights reserved.
 *
 * Matter Overdrive is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Matter Overdrive is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Matter Overdrive.  If not, see <http://www.gnu.org/licenses>.
 */

package matteroverdrive.items.android;

import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import matteroverdrive.Reference;
import matteroverdrive.api.inventory.IBionicPart;
import matteroverdrive.client.render.entity.EntityRendererRangedRougeAndroid;
import matteroverdrive.client.render.entity.EntityRendererRougeAndroid;
import matteroverdrive.entity.player.AndroidPlayer;
import matteroverdrive.proxy.ClientProxy;
import matteroverdrive.util.MOStringHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

/**
 * Created by Simeon on 5/28/2015.
 */
public class RougeAndroidParts extends BionicPart implements IBionicPart
{
    String[] names = new String[]{"head","arms","legs","chest"};
    String[] healtModifiersIDs = new String[]{"1bb8df41-63d1-4f58-92c4-43adea7528b2","73983b14-e605-40be-8567-36a9dec51d4f","29419afc-63ad-4b74-87e2-38219e867119","e4b38c80-7407-48fd-b837-8f36ae516c4d"};
    IIcon[] icons = new IIcon[names.length];

    public RougeAndroidParts(String name)
    {
        super(name);
        setHasSubtypes(true);
    }

    public void addDetails(ItemStack itemstack, EntityPlayer player, List infos)
    {
        if (itemstack.getTagCompound() != null)
        {
            if (itemstack.getTagCompound().getByte("Type") == 1)
            {
                infos.add(EnumChatFormatting.AQUA + MOStringHelper.translateToLocal("item.rouge_android_part.range"));
            }else
            {
                infos.add(EnumChatFormatting.GOLD + MOStringHelper.translateToLocal("item.rouge_android_part.melee"));
            }
        }else
        {
            infos.add(EnumChatFormatting.GOLD + MOStringHelper.translateToLocal("item.rouge_android_part.melee"));
        }
        super.addDetails(itemstack,player,infos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        for (int i = 0;i < names.length;i++)
        {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":" + "rouge_android_" + names[i]);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int i = MathHelper.clamp_int(stack.getItemDamage(), 0, 3);
        return super.getUnlocalizedName() + "." + names[MathHelper.clamp_int(i,0,names.length-1)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 0;i < names.length;i++)
        {
            list.add(new ItemStack(this,1,i));
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        int j = MathHelper.clamp_int(damage, 0, names.length-1);
        return this.icons[j];
    }

    @Override
    public int getType(ItemStack itemStack) {
        return itemStack.getItemDamage();
    }

    @Override
    public boolean affectAndroid(AndroidPlayer player, ItemStack itemStack) {
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getModifiers(AndroidPlayer player, ItemStack itemStack) {
        Multimap multimap = super.getModifiers(player,itemStack);
        if (multimap.isEmpty())
        {
            multimap.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), new AttributeModifier(UUID.fromString(healtModifiersIDs[itemStack.getItemDamage()]), MOStringHelper.translateToLocal("attribute.name." + SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName()), 1f, 0));
        }
        return multimap;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getTexture(AndroidPlayer androidPlayer, ItemStack itemStack)
    {
        if (itemStack.getTagCompound() != null)
        {
            if (itemStack.getTagCompound().getByte("Type") == 1)
            {
                return EntityRendererRangedRougeAndroid.texture;
            }
        }
        return EntityRendererRougeAndroid.texture;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getModel(AndroidPlayer androidPlayer, ItemStack itemStack)
    {
        int type = getType(itemStack);
        ModelBiped model = ClientProxy.renderHandler.modelMeleeRogueAndroidParts;
        if (itemStack.getTagCompound() != null)
        {
            if (itemStack.getTagCompound().getByte("Type") == 1)
            {
                model = ClientProxy.renderHandler.modelRangedRogueAndroidParts;
            }
        }
        model.bipedHead.showModel = type == 0;
        model.bipedHeadwear.showModel = type == 0;
        model.bipedBody.showModel = type == 3;
        model.bipedRightArm.showModel = type == 1;
        model.bipedLeftArm.showModel = type == 1;
        model.bipedRightLeg.showModel = type == 2;
        model.bipedLeftLeg.showModel = type == 2;
        return model;
    }
}
