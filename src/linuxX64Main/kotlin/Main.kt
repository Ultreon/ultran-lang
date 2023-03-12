import com.ultreon.ultranlang.internalMain
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    runBlocking {
        exitProcess(internalMain(args))
    }
}
