package sh.foxboy.bapp.utils

class TimeUtil {
    companion object {
        fun convertTimestampToString(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diffMillis = timestamp - now
            val inFuture = diffMillis > 0
            val diffSeconds = Math.abs(diffMillis) / 1000

            // Define approximate conversion factors (in seconds)
            val secondsPerYear = 365L * 24 * 60 * 60
            val secondsPerMonth = 30L * 24 * 60 * 60
            val secondsPerWeek = 7L * 24 * 60 * 60
            val secondsPerDay = 24L * 60 * 60
            val secondsPerHour = 60L * 60
            val secondsPerMinute = 60L

            fun format(unit: String, value: Long): String {
                // Add plural "s" if value is not 1.
                val unitString = if (value == 1L) unit else "$unit" + "s"
                return if (inFuture) "in $value $unitString" else "$value $unitString ago"
            }

            return when {
                diffSeconds >= secondsPerYear -> format("year", diffSeconds / secondsPerYear)
                diffSeconds >= secondsPerMonth -> format("month", diffSeconds / secondsPerMonth)
                diffSeconds >= secondsPerWeek -> format("week", diffSeconds / secondsPerWeek)
                diffSeconds >= secondsPerDay -> format("day", diffSeconds / secondsPerDay)
                diffSeconds >= secondsPerHour -> format("hour", diffSeconds / secondsPerHour)
                diffSeconds >= secondsPerMinute -> format("minute", diffSeconds / secondsPerMinute)
                else -> format("second", diffSeconds)
            }
        }

        fun convertDurationToSeconds(duration: Map<String, Int>): Long {
            val years = duration["years"] ?: 0
            val months = duration["months"] ?: 0
            val weeks = duration["weeks"] ?: 0
            val days = duration["days"] ?: 0
            val hours = duration["hours"] ?: 0
            val minutes = duration["minutes"] ?: 0
            val seconds = duration["seconds"] ?: 0

            val secondsPerDay = 86400L // 24 * 60 * 60
            val totalSeconds = years * 365L * secondsPerDay +
                                months * 30L * secondsPerDay +
                                weeks * 7L * secondsPerDay +
                                days * secondsPerDay +
                                hours * 3600L +
                                minutes * 60L +
                                seconds

            return totalSeconds
        }

        fun parseDuration(durationString: String): Map<String, Int> {
            val regex = Regex("^(?:(\\d+)y)?(?:(\\d+)m)?(?:(\\d+)w)?(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$", RegexOption.IGNORE_CASE)
            val matchResult = regex.matchEntire(durationString.trim())

            if (matchResult != null) {
                val groups = matchResult.groupValues
                return mapOf(
                    "years" to (groups.getOrNull(1)?.toIntOrNull() ?: 0),
                    "months" to (groups.getOrNull(2)?.toIntOrNull() ?: 0),
                    "weeks" to (groups.getOrNull(3)?.toIntOrNull() ?: 0),
                    "days" to (groups.getOrNull(4)?.toIntOrNull() ?: 0),
                    "hours" to (groups.getOrNull(5)?.toIntOrNull() ?: 0),
                    "minutes" to (groups.getOrNull(6)?.toIntOrNull() ?: 0),
                    "seconds" to (groups.getOrNull(7)?.toIntOrNull() ?: 0)
                )
            } else {
                throw IllegalArgumentException("Invalid duration format: $durationString")
            }
        }
    }
}
