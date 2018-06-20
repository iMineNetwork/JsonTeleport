package nl.imine.command;

import nl.imine.service.EditingService;
import nl.imine.vision.VisionType;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class JsonTeleportFinishCommand implements CommandExecutor {

    private static final Text VISION_TYPE_ARGUMENT_KEY = Text.of("isNightVision");
    private static final Text REQUIRED_ITEM_ARGUMENT_KEY = Text.of("requiredItem");
    private static final Text NO_PERMISSION_ARGUMENT_KEY = Text.of("noPermission");

    private final EditingService editingService;

    public JsonTeleportFinishCommand(EditingService editingService) {
        this.editingService = editingService;
    }

    public static CommandSpec commandSpec(EditingService editingService) {
        return CommandSpec.builder()
                .arguments(
                        GenericArguments.optional(GenericArguments.enumValue(VISION_TYPE_ARGUMENT_KEY, VisionType.class)),
                        GenericArguments.optional(GenericArguments.string(REQUIRED_ITEM_ARGUMENT_KEY)),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of(NO_PERMISSION_ARGUMENT_KEY)))
                )
                .executor(new JsonTeleportFinishCommand(editingService))
                .description(Text.of("Finish the current edit and save it to disk"))
                .extendedDescription(Text.of("Finalizes the current edit by gathering the last data such as NightVision, Item Requirements or NoPermission messages"))
                .build();
    }

    @Override
    @NonnullByDefault
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            VisionType visionType = (VisionType) commandContext.getOne(VISION_TYPE_ARGUMENT_KEY).orElse(VisionType.NONE);
            String requiredItem = (String) commandContext.getOne(REQUIRED_ITEM_ARGUMENT_KEY).orElse(null);
            String noPermissionMessage = (String) commandContext.getOne(NO_PERMISSION_ARGUMENT_KEY).orElse(null);
            editingService.finishEdit((Player) commandSource, visionType, requiredItem, noPermissionMessage);
        }
        return CommandResult.builder()
                .build();
    }
}
