package io.drakon.progenitor.world

import io.drakon.progenitor.lib.ChatUtils

import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

object WorldTickHandler {

    private val tasks: MutableMap<Int, Queue<Pair<Int, Int>>> = mutableMapOf()
    private val providedChunks: MutableMap<Int, Int> = mutableMapOf()

    @SubscribeEvent
    fun onTick(evt: TickEvent.WorldTickEvent) {
        // Only on server/local world
        if (evt.world.isRemote) return

        // Pregen!
        val dim = evt.world.provider.dimension
        val queue = tasks[dim] as? Queue ?: return
        if (queue.size == 0) {
            ChatUtils.sendToAll(evt.world.minecraftServer!!, "Work queue exhausted for dim $dim; done. Syncing to disk, this may hang a moment...")
            tasks.remove(dim)
            providedChunks.remove(dim)
            evt.world.saveHandler.flush()
            return
        }

        val pos = queue.remove()
        generateChunk(evt.world, pos)

        if (queue.size % 100 == 0 && queue.size != 0) {
            ChatUtils.sendToAll(evt.world.minecraftServer!!, "Queue size in dim $dim: ${queue.size}")
        }
    }

    fun addToPregenQueue(dim: Int, chunkX: Int, chunkZ: Int) {
        if (!tasks.containsKey(dim)) {
            tasks[dim] = LinkedBlockingQueue()
        }
        tasks[dim]?.add(Pair(chunkX, chunkZ))
    }

    fun purgePregenQueues() {
        tasks.clear()
    }

    private fun generateChunk(world: World, coord: Pair<Int, Int>) {
        val dim = world.provider.dimension
        val provChunks = providedChunks[dim] ?: 0
        if (provChunks != 0 && provChunks % 1000 == 0) {
            ChatUtils.sendToAll(world.minecraftServer!!, "Flushing to disk; this may hang a moment...")
            world.saveHandler.flush()
        }
        world.chunkProvider.provideChunk(coord.first, coord.second)
        providedChunks[dim] = provChunks + 1
    }

}