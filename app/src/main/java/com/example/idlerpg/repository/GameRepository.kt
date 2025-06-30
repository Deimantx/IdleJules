package com.example.idlerpg.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.idlerpg.models.Player
import com.google.gson.Gson // For serializing/deserializing Player object

class GameRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "IdleRpgPrefs"
        private const val PLAYER_KEY = "playerData"
    }

    fun savePlayer(player: Player) {
        val playerJson = gson.toJson(player)
        sharedPreferences.edit().putString(PLAYER_KEY, playerJson).apply()
    }

    fun loadPlayer(): Player? {
        val playerJson = sharedPreferences.getString(PLAYER_KEY, null)
        return if (playerJson != null) {
            gson.fromJson(playerJson, Player::class.java)
        } else {
            null
        }
    }

    // Optional: Clear saved data (e.g., for a new game option)
    fun clearSavedData() {
        sharedPreferences.edit().remove(PLAYER_KEY).apply()
    }
}
