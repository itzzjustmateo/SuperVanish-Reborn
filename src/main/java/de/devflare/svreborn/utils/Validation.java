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

    public static void checkNotNull(Object... objects) {
        checkNotNull(null, objects);
    }

    public static void checkIsTrue(boolean... bool) {
        checkIsTrue(null, bool);
    }

    public static void checkNotNull(String message, Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                if (message != null)
                    log(Level.SEVERE, message);
                throw new IllegalArgumentException(message == null ? "" : message);
            }
        }
    }


    public static void checkIsTrue(String message, boolean... booleans) {
        for (boolean bool : booleans) {
            if (!bool) {
                if (message != null)
                    log(Level.SEVERE, message);
                throw new IllegalArgumentException(message == null ? "" : message);
            }
        }
    }

    private static void log(Level level, String message) {
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            Bukkit.getLogger().log(level, message);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
