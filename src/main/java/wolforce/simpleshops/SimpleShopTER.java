package wolforce.simpleshops;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleShopTER implements BlockEntityRenderer<SimpleShopTileEntity> {

	public SimpleShopTER(BlockEntityRendererProvider.Context ctx) {
	}
	
	@SuppressWarnings("resource")
	@Override
	public void render(SimpleShopTileEntity tile, float partialTickTime, PoseStack matrix, MultiBufferSource buffer, int combinedLight,
			int combinedOverlay) {

		if (tile == null)
			return;

		Level world = tile.getLevel();
		if (world == null)
			return;

		BlockState state = world.getBlockState(tile.getBlockPos());
		if (!(state.getBlock() instanceof SimpleShopBlock))
			return;

		matrix.pushPose();

		switch (state.getValue(SimpleShopBlock.FACING)) {
		case SOUTH:
			matrix.translate(0.5, 0, 0.5);
			matrix.mulPose(new Quaternion(Vector3f.YN, 90, true));
			matrix.translate(-0.5, 0, -0.5);
			break;
		case WEST:
			matrix.translate(0.5, 0, 0.5);
			matrix.mulPose(new Quaternion(Vector3f.YN, 180, true));
			matrix.translate(-0.5, 0, -0.5);
			break;
		case NORTH:
			matrix.translate(0.5, 0, 0.5);
			matrix.mulPose(new Quaternion(Vector3f.YN, 270, true));
			matrix.translate(-0.5, 0, -0.5);
			break;
		default:
		}

		ItemStack renderStack = tile.getCost();
		Font font = Minecraft.getInstance().font;
		ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
		if (!renderStack.isEmpty()) {

			BakedModel itemModel = renderItem.getModel(renderStack, null, null, 0);
			boolean render3D = itemModel.isGui3d();

			if (render3D)
				Lighting.setupFor3DItems();
			else
				Lighting.setupForFlatItems();

			matrix.pushPose(); // START RENDER ITEM
			matrix.translate(-.055, .131, .35);
			matrix.mulPose(new Quaternion(Vector3f.ZN, 22.5f, true));
			matrix.scale(0, render3D ? .666f : .5f, render3D ? .666f : .5f);
			if (render3D) {
				matrix.mulPose(new Quaternion(Vector3f.ZP, 30f, true));
				matrix.translate(0, -.1, 0);
			}
			matrix.mulPose(new Quaternion(Vector3f.YN, render3D ? 45f : 90, true));
			renderItem.render(renderStack, ItemTransforms.TransformType.GROUND, false, matrix, buffer, combinedLight, combinedOverlay,
					itemModel);
			matrix.popPose(); // FINNISH RENDER ITEM

			matrix.pushPose(); // START RENDER TEXT
			matrix.translate(-.055, .132, .35);
			matrix.mulPose(new Quaternion(Vector3f.ZN, 22.5f, true));
			matrix.mulPose(new Quaternion(Vector3f.YN, 90, true));
			matrix.mulPose(new Quaternion(Vector3f.ZN, 180, true));
			matrix.mulPose(new Quaternion(Vector3f.YN, 180, true));
			matrix.scale(.028f, .028f, .028f);
			matrix.translate(4.5f, -5.8f, 0);
			if (renderStack.getCount() > 9) {
				matrix.scale(.7f, .7f, .7f);
				matrix.translate(-2, 3, 0);
			}
			font.draw(matrix, new TextComponent("x" + renderStack.getCount()), 0, 0, 0);
			matrix.popPose(); // END RENDER TEXT

		}

		ItemStack renderStack2 = tile.getOutputStack();
		if (!renderStack2.isEmpty()) {

			BakedModel itemModel = renderItem.getModel(renderStack2, null, null, 0);
			boolean render3D = itemModel.isGui3d();
			if (render3D)
				Lighting.setupFor3DItems();
			else
				Lighting.setupForFlatItems();

			matrix.pushPose();
			matrix.translate(0.5F, render3D ? .75f : .9f, 0.5F);
			long a = System.currentTimeMillis() / 40 % 360;
			matrix.mulPose(new Quaternion(Vector3f.YP, a, true));
			renderItem.render(renderStack2, ItemTransforms.TransformType.GROUND, false, matrix, buffer, combinedLight, combinedOverlay,
					itemModel);
			matrix.popPose();

			matrix.pushPose();
			matrix.scale(.025f, .025f, .025f);
			matrix.mulPose(new Quaternion(Vector3f.ZN, 180, true));
			matrix.mulPose(new Quaternion(Vector3f.YN, 90, true));
			matrix.mulPose(new Quaternion(Vector3f.XN, 90, true));
			matrix.translate(renderStack2.getCount() > 9 ? 11 : 15, -12, -27.55);
			font.draw(matrix, new TextComponent("x" + renderStack2.getCount()), 0, 0, 0);
			matrix.popPose();
		}

		matrix.popPose();
	}

}
