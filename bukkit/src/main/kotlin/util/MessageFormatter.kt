package sh.foxboy.bapp.utils

import java.io.File
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

class MessageFormatter(private val plugin: JavaPlugin) {

    private var messages: YamlConfiguration? = null

    // Initialize the MessageFormatter, loading the correct language file
    init {
        loadMessages()
    }

    // Load the message file for the configured language
    private fun loadMessages() {
        try {
            val language = getLanguage()
            val messagesFolder = File(plugin.dataFolder, "messages")
            if (!messagesFolder.exists()) {
                messagesFolder.mkdirs() // Create messages folder if it doesn't exist
                plugin.saveResource("messages/messages_en.yml", false)
            }
            val messagesFile = File(messagesFolder, "messages_$language.yml")
            if (!messagesFile.exists()) {
                plugin.logger.info("Messages file for language '$language' not found! Falling back to 'messages_en.yml'.")
                messages = YamlConfiguration.loadConfiguration(File(messagesFolder, "messages_en.yml"))
            } else {
                messages = YamlConfiguration.loadConfiguration(messagesFile)
            }
        } catch (e: Exception) {
            plugin.logger.warning("Error loading messages file: ${e.message}")
            e.printStackTrace()
        }
    }

    // Retrieve the configured language (defaults to 'en' if not set)
    private fun getLanguage(): String {
        return plugin.config.getString("language", "en") ?: "en" // Default to English if not set
    }

    // Retrieve and format a message by key and replace placeholders
    fun getMessage(key: String, placeholders: Map<String, String> = emptyMap(), conditions: Map<String, Any> = emptyMap()): String {
        val rawMessage = messages?.getString(key) ?: key

        // Manually replace placeholders
        var formattedMessage = rawMessage
        placeholders.forEach { (placeholder, value) ->
            formattedMessage = formattedMessage.replace("{$placeholder}", value)
        }

        val parsedMessage = applyConditionals(formattedMessage, conditions)
        return applyColorCodes(parsedMessage)
    }

    // Parse the message and handle conditional scripting like {$silent(true_value : false_value)}
    private fun applyConditionals(message: String, conditions: Map<String, Any>): String {
        val conditionalRegex = Regex("""\{\$(\w+)\(((?:(?:\\.)|[^:])*)(?<!\\):([\s\S]*?)\)\}""")

        return conditionalRegex.replace(message) { matchResult ->
            val isEscaped = matchResult.value.startsWith("\\{")
            if (isEscaped) {
                return@replace matchResult.value.substring(1)
            }

            val condition = matchResult.groupValues[1]
            val trueValue = matchResult.groupValues[2]
            val falseValue = matchResult.groupValues[3]

            // Check if the condition exists in the conditions map and evaluate it
            val conditionMet = conditions[condition] as? Boolean ?: false

            // Return the true value if the condition is met, otherwise return the false value
            if (conditionMet) trueValue.replace("\\", "") else falseValue.replace("\\", "")
        }
    }

    // Escape special characters like `{`, `}`, `\`, and `:` before processing
    private fun escapeSpecialCharacters(message: String): String {
        return message
            .replace("\\", "\\\\") // Double escape backslashes
            .replace("{", "\\{") // Escape opening curly braces
            .replace("}", "\\}") // Escape closing curly braces
            .replace(":", "\\:") // Escape colons
    }

    // Apply color codes to the message (handles both &c and &#RRGGBB format)
    private fun applyColorCodes(message: String): String {
        // var formattedMessage = escapeSpecialCharacters(message) // Escape special characters first

        // Handle standard color codes (&c, &d, etc.)
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}
