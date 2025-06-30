package com.example.idlerpg.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idlerpg.game.GameEngine
import com.example.idlerpg.models.Monster
import com.example.idlerpg.models.Player
import com.example.idlerpg.repository.GameRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _gameEngine = GameEngine()
    private val _repository = GameRepository(application.applicationContext)

    // Player LiveData
    private val _playerData = MutableLiveData<Player>()
    val playerData: LiveData<Player> = _playerData

    private val _playerExperienceDisplay = MutableLiveData<String>()
    val playerExperienceDisplay: LiveData<String> = _playerExperienceDisplay

    // Monster LiveData
    private val _monsterData = MutableLiveData<Monster?>()
    val monsterData: LiveData<Monster?> = _monsterData

    // Combat Log LiveData
    private val _combatLog = MutableLiveData<MutableList<String>>(mutableListOf())
    val combatLog: LiveData<MutableList<String>> = _combatLog
    private val maxLogLines = 100 // Keep the log from growing indefinitely

    init {
        loadGame() // Load game state first
        _playerData.value = _gameEngine.getPlayerStats()
        _monsterData.value = _gameEngine.currentMonster
        updateExperienceDisplay()

        _gameEngine.setOnMonsterDefeated {
            // This callback might not be strictly needed here if UI updates through LiveData
            _playerData.postValue(_gameEngine.getPlayerStats()) // Update player stats (coins, xp)
            _monsterData.postValue(_gameEngine.currentMonster) // New monster
            updateExperienceDisplay()
        }
        _gameEngine.setOnPlayerLeveledUp { player ->
            _playerData.postValue(player)
            updateExperienceDisplay()
        }
        _gameEngine.setOnCombatLog { message ->
            val currentLog = _combatLog.value ?: mutableListOf()
            currentLog.add(0, message) // Add new messages to the top
            if (currentLog.size > maxLogLines) {
                _combatLog.postValue(currentLog.take(maxLogLines).toMutableList())
            } else {
                _combatLog.postValue(currentLog)
            }
        }
    }

    fun manualAttack() {
        _gameEngine.fightTick()
        _playerData.value = _gameEngine.getPlayerStats() // Update player after attack (HP)
        _monsterData.value = _gameEngine.currentMonster // Update monster after attack (HP)
    }

    fun autoFightTick() {
        _gameEngine.fightTick()
        _playerData.postValue(_gameEngine.getPlayerStats())
        _monsterData.postValue(_gameEngine.currentMonster)
    }

    private fun updateExperienceDisplay() {
        val player = _gameEngine.player
        val expNeeded = _gameEngine.calculateExperienceForNextLevel(player.level)
        _playerExperienceDisplay.postValue("${player.experience} / $expNeeded")
    }

    fun saveGame() {
        _repository.savePlayer(_gameEngine.player)
        _combatLog.value?.add(0, "Game Saved!")
    }

    private fun loadGame() {
        val loadedPlayer = _repository.loadPlayer()
        if (loadedPlayer != null) {
            _gameEngine.player = loadedPlayer
            _combatLog.value?.add(0,"Game Loaded!")
        } else {
            _combatLog.value?.add(0,"No saved game found. Starting new game.")
        }
        // Ensure currentHp is not greater than maxHp after loading, could happen if maxHp formula changes
        _gameEngine.player.currentHp = _gameEngine.player.currentHp.coerceAtMost(_gameEngine.player.maxHp)
    }

    override fun onCleared() {
        super.onCleared()
        saveGame() // Save game when ViewModel is cleared (e.g. activity destroyed)
    }
}
