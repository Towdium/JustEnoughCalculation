package me.towdium.jecalculation.fabric_like.mixin;

import com.mojang.authlib.GameProfile;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.fabric_like.JecaPlayerRecordAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements JecaPlayerRecordAccessor {

    private RecordPlayer Jeca_record = new RecordPlayer();

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Inject(at = @At("HEAD"), method = "addAdditionalSaveData")
    public void onAddData(CompoundTag tag, CallbackInfo ci) {
        tag.put("Jeca_record", Jeca_record.serialize());
    }

    @Inject(at = @At("HEAD"), method = "readAdditionalSaveData")
    public void onReadData(CompoundTag tag, CallbackInfo ci) {
        Jeca_record.deserialize(tag.getCompound("Jeca_record"));
    }

    @Inject(at = @At("HEAD"), method = "restoreFrom")
    public void onRestore(ServerPlayer that, boolean keepEverything, CallbackInfo ci) {
        Jeca_record = ((JecaPlayerRecordAccessor) that).Jeca_getRecord();
    }

    @Override
    public RecordPlayer Jeca_getRecord() {
        return Jeca_record;
    }
}
