package com.yongaishide.chaosworld.mixin.ftbquests;

import com.yongaishide.chaosworld.compat.ftbquests.HiddenTaskTypes;
import dev.ftb.mods.ftbquests.client.gui.quests.AddTaskButton;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mixin(value = AddTaskButton.class, remap = false)
public class AddTaskButtonMixin {
    @Redirect(method = "onClicked", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"), remap = false)
    private Collection<TaskType> chaosworld$filterTaskTypes(Map<ResourceLocation, TaskType> types) {
        List<TaskType> visible = new ArrayList<>();
        for (TaskType type : types.values()) {
            if (!HiddenTaskTypes.HIDDEN.contains(type.getTypeId())) {
                visible.add(type);
            }
        }
        return visible;
    }
}
