import com.ultreon.ultranlang.internalMain
import kotlin.system.exitProcess

suspend fun main(args: Array<String>) {
    exitProcess(internalMain(args))
}
