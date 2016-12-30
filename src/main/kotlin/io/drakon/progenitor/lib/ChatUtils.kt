package io.drakon.progenitor.lib

import io.drakon.progenitor.Progenitor
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString

object ChatUtils {

    fun sendToAll(server: MinecraftServer, msg: String) {
        Progenitor.log.info(msg)
        server.playerList.sendMessage(TextComponentString("[Pregen] $msg"))
    }

}