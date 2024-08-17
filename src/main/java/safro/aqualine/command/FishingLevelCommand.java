package safro.aqualine.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import safro.aqualine.Aqualine;
import safro.aqualine.AqualineConfig;
import safro.aqualine.api.FishingLevel;

import java.util.Collection;

public class FishingLevelCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fishinglevel")
                .then(Commands.literal("get").executes(context -> get(context.getSource(), context.getSource().getPlayerOrException())))
                .then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players())
                        .then(Commands.argument("level", IntegerArgumentType.integer(0, AqualineConfig.maxFishingLevel))
                                .executes(context -> set(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "level"))))))
        );
    }

    private static int get(CommandSourceStack source, ServerPlayer player) {
        FishingLevel data = player.getData(Aqualine.FISHING_LEVEL);
        source.sendSuccess(() -> Component.translatable("command.aqualine.get_level", data.getLevel(), data.getXp(), data.getXpToNext()), false);
        return 1;
    }

    private static int set(CommandSourceStack source, Collection<? extends ServerPlayer> players, int level) {
        int size = players.size();
        for (ServerPlayer player : players) {
            if (level == 0) {
                player.setData(Aqualine.FISHING_LEVEL, new FishingLevel());
                player.getData(Aqualine.FISHING_LEVEL).updateHealthBonus(player);
            } else {
                FishingLevel data = player.getData(Aqualine.FISHING_LEVEL);
                data.setTo(level);
                data.updateHealthBonus(player);
                player.setData(Aqualine.FISHING_LEVEL, data);
            }
        }
        source.sendSuccess(() -> Component.translatable("command.aqualine.set_level", size, level), true);
        return size;
    }
}
