/*
 * Copyright © 2026, SuperVanish Reborn contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.devflare.svreborn.utils;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public abstract class Validation {

    private static final boolean BUKKIT_AVAILABLE;

    static {
        boolean found = false;
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            found = true;
        } catch (ClassNotFoundException ignored) {
        }
        BUKKIT_AVAILABLE = found;
    }

    public static void checkNotNull(Object... objects) {
        checkNotNull("Validation failed", objects);
    }

    public static void checkIsTrue(boolean... bool) {
        checkIsTrue("Validation failed", bool);
    }

    public static void checkNotNull(String message, Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                String msg = message == null ? "Validation failed" : message;
                log(Level.SEVERE, msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }


    public static void checkIsTrue(String message, boolean... booleans) {
        for (boolean bool : booleans) {
            if (!bool) {
                String msg = message == null ? "Validation failed" : message;
                log(Level.SEVERE, msg);
                throw new IllegalArgumentException(msg);
            }
        }
    }

    private static void log(Level level, String message) {
        if (BUKKIT_AVAILABLE) {
            Bukkit.getLogger().log(level, message);
        } else {
            System.err.println(level + ": " + message);
        }
    }
}
