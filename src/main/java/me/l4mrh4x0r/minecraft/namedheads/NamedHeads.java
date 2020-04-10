/* NamedHeads, a Minecraft mod that shows names above player heads
 * Copyright (C) 2014 Willem Mulder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.l4mrh4x0r.minecraft.namedheads;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = NamedHeads.MOD_ID, version = NamedHeads.VERSION)
@SideOnly(Side.CLIENT)
public class NamedHeads {

    public static final String MOD_ID = "namedheads";
    public static final String VERSION = "@@VERSION@@";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("[Named Heads] Registering on the event bus...");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(receiveCanceled = false)
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        BlockPos pos = event.getTarget().getBlockPos();
        if (pos == null) return;

        World world = event.getPlayer().getEntityWorld();
        if (world.getBlockState(pos).getBlock() != Blocks.SKULL) return;

        TileEntitySkull skull = (TileEntitySkull) world.getTileEntity(pos);
        if (skull == null
                || skull.getPlayerProfile() == null
                || skull.getPlayerProfile().getName() == null)
            return;

        AxisAlignedBB boundingBox = skull.getRenderBoundingBox();
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        if (renderManager.getFontRenderer() == null) return;

        boolean isThirdPersonFrontal = renderManager.options != null && renderManager.options.thirdPersonView == 2;
        EntityRenderer.drawNameplate(
                renderManager.getFontRenderer(),
                skull.getPlayerProfile().getName(),
                (float) ((boundingBox.maxX + boundingBox.minX) / 2 - TileEntityRendererDispatcher.staticPlayerX),
                (float) (boundingBox.maxY + 0.5 - TileEntityRendererDispatcher.staticPlayerY),
                (float) ((boundingBox.maxZ + boundingBox.minZ) / 2 - TileEntityRendererDispatcher.staticPlayerZ),
                0,
                renderManager.playerViewY, renderManager.playerViewX,
                isThirdPersonFrontal,
                false
        );

        // drawNameplate enables lighting, but it was disabled at this point in rendering
        GlStateManager.disableLighting();
    }
}
