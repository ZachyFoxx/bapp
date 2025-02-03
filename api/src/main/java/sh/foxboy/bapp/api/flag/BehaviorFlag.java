/*
 * Copyright (c) 2025 Zachery Elliot <notzachery@gmail.com>. All rights reserved.
 * Licensed under the MIT license, see LICENSE for more information.
 */
package sh.foxboy.bapp.api.flag;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

/**
 * Reputation flag and weights for users.
 */
public enum BehaviorFlag {
    // User Behavior & Trustworthiness
    NEW_ACCOUNT(0.2),
    FREQUENT_REPORTS(0.3),
    BAN_EVASION(0.9),
    CHARGEBACK_RISK(0.5),

    // Financial & Payment Flags
    FRAUD(0.9),
    SCAMMING_REAL(0.9),
    SCAMMING_GAME(0.5),

    // Bot & API Usage Flags
    EXCESSIVE_API_REQUESTS(0.3),
    HIGH_FAILURE_RATE(0.5),

    // Cheats
    CHEATING_GENERAL(0.7),
    CHEATING_SEVERE(0.9),
    COMBAT_HACKS(0.9),
    MOVEMENT_HACKS(0.9),
    XRAY(0.6),
    AUTO_CLICKER(0.6),
    FAST_PLACE(0.6),
    ANTI_CHEAT_BYPASS(0.9),

    // Community & Social Flags
    TOXICITY_REPORTS(0.4),
    IMPERSONATION(0.5),
    MULTIPLE_ACCOUNTS_SAME_IP(0.2),
    MULTIPLE_ACCOUNTS_DIFFERENT_IP(0.4),

    // Security & Exploits
    SUSPICIOUS_IP_ACTIVITY(0.5),
    EXPLOIT_ATTEMPT(0.9),
    UNAUTHORIZED_ACCESS_ATTEMPT(0.9),
    
    // MISC
    SILENT(0.0); // silent punishment flag

    // Define a static set of critical flags for quick lookup
    private final double severity;

    // Define a static set of critical flags for quick lookup
    private static final Set<BehaviorFlag> CRITICAL_FLAGS = EnumSet.of(
        BAN_EVASION,
        CHARGEBACK_RISK,
        SCAMMING_REAL,
        FRAUD,
        EXPLOIT_ATTEMPT,
        UNAUTHORIZED_ACCESS_ATTEMPT,
        CHEATING_SEVERE,
        COMBAT_HACKS,
        MOVEMENT_HACKS,
        XRAY,
        ANTI_CHEAT_BYPASS
    );

    BehaviorFlag(double reputationScore) {
        this.severity = reputationScore;
    }

    /**
     * Checks if the flag is considered a critical security risk.
     * @return true if the flag is a major security risk.
     */
    public boolean isCritical() {
        return CRITICAL_FLAGS.contains(this);
    }

    /**
     * Get the reputation score associated with the flag.
     * @return The reputation score, between 0 (untrusted) and 1 (trusted).
     */
    public double getReputationScore() {
        return severity;
    }

    /**
     * Get the Flags from an ordinal value.
     * @param ordinal The ordinal value.
     * @return The corresponding Flags.
     * @throws IllegalArgumentException if the ordinal is out of range.
     */
    @NotNull
    public static BehaviorFlag fromOrdinal(@NotNull Integer ordinal) {
        BehaviorFlag[] values = BehaviorFlag.values();
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IllegalArgumentException(
                "Invalid ordinal for Flags: " + ordinal
            );
        }
        return values[ordinal];
    }

    public static int encodeFlags(List<BehaviorFlag> flags) {
        int bitmask = 0;
        for (BehaviorFlag flag : flags) {
            bitmask |= (1 << flag.ordinal());
        }
        return bitmask;
    }

    public static List<BehaviorFlag> decodeFlags(int bitmask) {
        List<BehaviorFlag> flags = new ArrayList<>();
        for (BehaviorFlag flag : BehaviorFlag.values()) {
            if ((bitmask & (1 << flag.ordinal())) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
