package io.redstudioragnarok.fbp.particles;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.renderer.CubeBatchRenderer;
import io.redstudioragnarok.fbp.renderer.RenderType;
import io.redstudioragnarok.fbp.renderer.color.ColorUtil;
import io.redstudioragnarok.fbp.renderer.light.LightUtil;
import io.redstudioragnarok.fbp.renderer.texture.TextureUtil;
import io.redstudioragnarok.fbp.utils.MathUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static io.redstudioragnarok.fbp.FBP.mc;

public class FBPParticleRain extends ParticleDigging {

	float AngleY;

	double particleHeight, prevParticleScale, prevParticleHeight, prevParticleAlpha;
	double scalar = FBP.scaleMult;
	double endMult = 1;

	public FBPParticleRain(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBlockState state) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);

		this.sourcePos = new BlockPos(xCoordIn, yCoordIn, zCoordIn);

		AngleY = (float) (FBP.random.nextDouble() * 45);

		this.motionX = xSpeedIn;
		this.motionY = -ySpeedIn;
		this.motionZ = zSpeedIn;

		this.particleGravity = 0.025F;

		particleMaxAge = (int) FBP.random.nextDouble(50, 70);

		this.particleAlpha = 0;
		this.particleScale = 0;

		this.canCollide = true;

		if (FBP.randomFadingSpeed)
			endMult *= FBP.random.nextDouble(0.85, 1);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		prevParticleAlpha = particleAlpha;
		prevParticleScale = particleScale;
		prevParticleHeight = particleHeight;

		if (!mc.isGamePaused()) {
			particleAge++;

			if (posY < mc.player.posY - (mc.gameSettings.renderDistanceChunks * 9))
				setExpired();

			if (!onGround) {
				if (this.particleAge < this.particleMaxAge) {
					double max = scalar * 0.5;

					if (particleScale < max) {
						if (FBP.randomFadingSpeed)
							particleScale += 0.05 * endMult;
						else
							particleScale += 0.05;

						if (particleScale > max)
							particleScale = (float) max;

						particleHeight = particleScale;
					}

					if (particleAlpha < 0.65) {
						if (FBP.randomFadingSpeed)
							particleAlpha += 0.085 * endMult;
						else
							particleAlpha += 0.085;

						if (particleAlpha > 0.65)
							particleAlpha = 0.65F;
					}
				} else
					setExpired();
			}

			if (world.getBlockState(new BlockPos(posX, posY, posZ)).getMaterial().isLiquid())
				setExpired();

			motionY -= 0.04 * this.particleGravity;

			move(motionX, motionY, motionZ);

			motionY *= 1;

			if (onGround) {
				motionX = 0;
				motionY = -0.25;
				motionZ = 0;

				if (particleHeight > 0.075)
					particleHeight *= 0.725;

				float max = (float) scalar * 4.25F;

				if (particleScale < max) {
					particleScale += max / 10;

					if (particleScale > max)
						particleScale = max;
				}

				if (particleScale >= max / 2) {
					if (FBP.randomFadingSpeed)
						particleAlpha *= 0.75 * endMult;
					else
						particleAlpha *= 0.75;

					if (particleAlpha <= 0.001)
						setExpired();
				}
			}
		}

		this.particleRed = (float) mc.world.getSkyColor(mc.player, 0).x;
		this.particleGreen = MathUtil.clampMaxFirst((float) (mc.world.getSkyColor(mc.player, 0).y + 0.25F), 0.25F, 1);
		this.particleBlue = MathUtil.clampMaxFirst((float) (mc.world.getSkyColor(mc.player, 0).z + 0.5F), 0.5F, 1);

		if (this.particleGreen > 1)
			particleGreen = 1;
		if (this.particleBlue > 1)
			particleBlue = 1;
	}

	@Override
	public void move(double x, double y, double z) {
		double X = x;
		double Y = y;
		double Z = z;

		List<AxisAlignedBB> list = this.world.getCollisionBoxes(null, this.getBoundingBox().expand(x, y, z));

		for (AxisAlignedBB axisalignedbb : list) {
			y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
		}

		this.setBoundingBox(this.getBoundingBox().offset(0, y, 0));

		for (AxisAlignedBB axisalignedbb : list) {
			x = axisalignedbb.calculateXOffset(this.getBoundingBox(), x);
		}

		this.setBoundingBox(this.getBoundingBox().offset(x, 0, 0));

		for (AxisAlignedBB axisalignedbb : list) {
			z = axisalignedbb.calculateZOffset(this.getBoundingBox(), z);
		}

		this.setBoundingBox(this.getBoundingBox().offset(0, 0, z));

		this.resetPositionToBB();

		this.onGround = y != Y && Y < 0;

		if (x != X)
			motionX *= 0.69;
		if (z != Z)
			motionZ *= 0.69;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		if (!FBP.enabled && particleMaxAge != 0)
			particleMaxAge = 0;

		float x = (float) (prevPosX + (posX - prevPosX) * partialTicks - interpPosX);
		float y = (float) (prevPosY + (posY - prevPosY) * partialTicks - interpPosY);
		float z = (float) (prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ);

		int brightness = getBrightnessForRender(partialTicks);

		float alpha = (float) (prevParticleAlpha + (particleAlpha - prevParticleAlpha) * partialTicks);

		float scale = (float) (prevParticleScale + (particleScale - prevParticleScale) * partialTicks);
		scale *= 0.1F;
		float height = (float) (prevParticleHeight + (particleHeight - prevParticleHeight) * partialTicks);
		height *= 0.1F;

		y += height;

		CubeBatchRenderer.renderCube(RenderType.BLOCK_TEXTURE_ITEM_LIGHTING, x, y, z, 0.0F, AngleY, 0.0F, scale, height, scale,
				TextureUtil.particleTexCoordProvider(particleTexture, particleTextureJitterX, particleTextureJitterY, particleTextureIndexX, particleTextureIndexY),
				ColorUtil.uniformColorProvider(particleRed, particleGreen, particleBlue, alpha),
				LightUtil.uniformLightCoordProvider(brightness));
	}

	@Override
	public int getBrightnessForRender(float partialTick) {
		return LightUtil.getCombinedLight((float) posX, (float) posY, (float) posZ);
	}
}
