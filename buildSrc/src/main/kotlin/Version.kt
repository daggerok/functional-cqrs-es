import org.gradle.api.GradleException
import java.io.File

internal data class Version(val major: Int, val minor: Int, val patch: Int, val snapshot: Boolean) {
    override fun toString() = if (!snapshot) "$major.$minor.$patch"
    else "$major.$minor.$patch-SNAPSHOT"

    fun increment() = Version(major, minor, patch + 1, snapshot)
}

internal fun String.toVersion(): Version {
    val versions = this.split(".")
    val error = GradleException("cannot parse version: $this")
    if (versions.size != 3) throw error
    val major = versions[0].toIntOrNull() ?: throw error
    val minor = versions[1].toIntOrNull() ?: throw error
    val lastPart = versions[2]
    val maybePatch = lastPart.toIntOrNull()
    val snapshot = maybePatch == null && lastPart.contains("-")
    if (!snapshot && null == maybePatch) throw error
    val patch = lastPart.split("-")[0].toIntOrNull() ?: throw error
    return Version(major, minor, patch, snapshot)
}

internal const val undefined = "undefined"
fun incrementVersion(path: String = undefined /* = "./build.gradle.kts"*/,
                     prefix: String = undefined /* = "version = \""*/,
                     suffix: String = undefined /* = "\""*/) {

    val undefinedArgs = listOf(path, prefix, suffix).filter { it == undefined }
    if (undefinedArgs.isNotEmpty()) {
        println("Nothing to increment.")
        println("Arguments: path, prefix and suffix are required.")
        return
    }

    val file = File(path).absoluteFile.normalize()
    println("incrementing version for a file: $file")

    val line = file.readLines().first { it.startsWith(prefix) }
    if (line.isEmpty()) {
        println("Wasn't able to find version in $file.")
        println("Verify if used prefix: '$prefix' and suffix: '$suffix' are correct.")
        return
    }

    val version = line.replace(prefix, "").replace(suffix, "")
    val nextVersion = version.toVersion().increment().toString()
    println("$file: $version -> $nextVersion")
    //println("$path: '$prefix$version$suffix' -> '$prefix$nextVersion$suffix'")

    val oldText = file.readText()
    val newText = oldText.replace("$prefix$version$suffix", "$prefix$nextVersion$suffix")
    file.writeText(newText)
}
