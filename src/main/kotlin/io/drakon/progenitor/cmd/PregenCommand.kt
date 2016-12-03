package io.drakon.progenitor.cmd

import io.drakon.progenitor.lib.ChatUtils
import io.drakon.progenitor.world.WorldTickHandler
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.command.NumberInvalidException
import net.minecraft.command.WrongUsageException
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer

class PregenCommand : CommandBase() {

    override fun execute(server: MinecraftServer, sender: ICommandSender?, args: Array<out String>) {
        if (args.size !in 1..3) {
            throw WrongUsageException("Command requires one to three parameters. See help.")
        }

        val radius: Int
        val dim: Int
        var x: Int
        var z: Int
        try {
            radius = parseInt(args[0], 0, 5000)
            if (args.size == 2) {
                dim = parseInt(args[1])
            } else {
                if (sender is EntityPlayer) {
                    dim = sender.dimension
                } else {
                    throw WrongUsageException("Console users must specify dimension.")
                }
            }
        } catch (ex: NumberInvalidException) {
            throw WrongUsageException("Fields must be an integer.")
        }

        if (args.size == 3 && sender is EntityPlayer && args[2] == "here") {
            x = sender.chunkCoordX
            z = sender.chunkCoordZ
        } else {
            val wi = server.worldServerForDimension(dim).worldInfo
            x = wi.spawnX / 16
            z = wi.spawnZ / 16
        }

        for (_x in (x - radius)..(x + radius)) {
            for (_z in (z - radius)..(z + radius)) {
                WorldTickHandler.addToPregenQueue(dim, _x, _z)
            }
        }

        ChatUtils.sendToAll(server, "Beginning pregen in dim $dim, radius $radius")
    }

    override fun getCommandName(): String = "pregen"

    override fun getCommandUsage(sender: ICommandSender?): String = "/pregen RADIUS [DIM] (here/spawn)"

    // Require Admin/Op
    override fun getRequiredPermissionLevel(): Int = 2

}