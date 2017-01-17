package io.drakon.progenitor

import io.drakon.progenitor.cmd.PregenCommand
import io.drakon.progenitor.world.WorldTickHandler
import net.minecraftforge.common.MinecraftForge

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * Progenitor - pregen tool.
 *
 * @author Arkan <arkan@drakon.io>
 */
@Mod(modid = "progenitor", name = "Progenitor", modLanguage = "kotlin", modLanguageAdapter = "io.drakon.progenitor.lib.KotlinAdapter", acceptableRemoteVersions = "*")
object Progenitor {

    val log: Logger = LogManager.getLogger("Progenitor")

    @EventHandler
    fun preinit(evt: FMLPreInitializationEvent) {
        log.info("Preinit.")
    }

    @EventHandler
    fun init(evt: FMLInitializationEvent) {
        log.info("Init.")
    }

    @EventHandler
    fun postinit(evt:FMLPostInitializationEvent) {
        log.info("Postinit.")
    }

    @EventHandler
    fun serverStarting(evt:FMLServerStartingEvent) {
        log.info("Server starting; registering commands.")
        evt.registerServerCommand(PregenCommand())
        MinecraftForge.EVENT_BUS.register(WorldTickHandler)
    }

    @EventHandler
    fun serverStopping(evt:FMLServerStoppingEvent) {
        log.info("Server shutting down; clearing queues.")
        WorldTickHandler.purgePregenQueues()
    }

}