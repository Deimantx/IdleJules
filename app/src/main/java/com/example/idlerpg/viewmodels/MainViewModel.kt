package com.example.idlerpg.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idlerpg.game.GameEngine
import com.example.idlerpg.game.Location
import com.example.idlerpg.models.GearItem
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
    private val maxLogLines = 100

    // Shop LiveData
    private val _shopItems = MutableLiveData<List<GearItem>>()
    val shopItems: LiveData<List<GearItem>> = _shopItems

    // Single event LiveData for messages like "Not enough coins"
    private val _toastMessage = MutableLiveData<SingleEvent<String>>()
    val toastMessage: LiveData<SingleEvent<String>> = _toastMessage


    init {
        try {
            loadGame()
            _playerData.value = _gameEngine.getPlayerStats()
            _monsterData.value = _gameEngine.currentMonster
            _shopItems.value = _gameEngine.availableShopItems
            updateExperienceDisplay()

            _gameEngine.setOnMonsterDefeated {
                try {
                    _playerData.postValue(_gameEngine.getPlayerStats())
                    _monsterData.postValue(_gameEngine.currentMonster)
                    updateExperienceDisplay()
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error in monster defeated callback", e)
                }
            }
            _gameEngine.setOnPlayerLeveledUp { player ->
                try {
                    _playerData.postValue(player)
                    updateExperienceDisplay()
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error in level up callback", e)
                }
            }
            _gameEngine.setOnCombatLog { message ->
                try {
                    val currentLog = _combatLog.value ?: mutableListOf()
                    currentLog.add(0, message)
                    if (currentLog.size > maxLogLines) {
                        _combatLog.postValue(currentLog.take(maxLogLines).toMutableList())
                    } else {
                        _combatLog.postValue(currentLog)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("MainViewModel", "Error in combat log callback", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Critical error in ViewModel initialization", e)
            // Initialize with empty/default values to prevent crashes
            _playerData.value = null
            _monsterData.value = null
            _shopItems.value = emptyList()
            _combatLog.value = mutableListOf("Error initializing game. Please restart the app.")
        }
    }

    // New combat tick system
    fun combatTick() {
        _gameEngine.combatTick()
        _playerData.postValue(_gameEngine.getPlayerStats())
        _monsterData.postValue(_gameEngine.currentMonster)
    }

    // Location selection methods
    fun selectLocation(location: Location) {
        _gameEngine.selectLocation(location)
        _monsterData.postValue(_gameEngine.currentMonster)
    }

    fun getMonstersForLocation(location: Location): List<String> {
        return _gameEngine.getMonstersForLocation(location)
    }

    // Monster selection methods
    fun selectMonster(monsterName: String) {
        _gameEngine.selectMonster(monsterName)
        _monsterData.postValue(_gameEngine.currentMonster)
    }

    fun getAvailableMonsters(): List<String> {
        return _gameEngine.getAvailableMonsters()
    }

    fun getMonstersForPlayerLevel(): List<String> {
        return _gameEngine.getMonstersForPlayerLevel()
    }

    private fun updateExperienceDisplay() {
        val player = _gameEngine.player
        val expNeeded = _gameEngine.calculateExperienceForNextLevel(player.level)
        _playerExperienceDisplay.postValue("${player.experience} / $expNeeded")
    }

    // New stat system skill point methods
    fun spendSkillPointStrength() {
        if (_gameEngine.spendSkillPointOnStrength()) {
            _playerData.value = _gameEngine.getPlayerStats()
        } else {
            _toastMessage.value = SingleEvent("Failed to spend skill point on Strength (No points?).")
        }
    }

    fun spendSkillPointAgility() {
        if (_gameEngine.spendSkillPointOnAgility()) {
            _playerData.value = _gameEngine.getPlayerStats()
        } else {
            _toastMessage.value = SingleEvent("Failed to spend skill point on Agility (No points?).")
        }
    }

    fun spendSkillPointIntelligence() {
        if (_gameEngine.spendSkillPointOnIntelligence()) {
            _playerData.value = _gameEngine.getPlayerStats()
        } else {
            _toastMessage.value = SingleEvent("Failed to spend skill point on Intelligence (No points?).")
        }
    }

    fun spendSkillPointVitality() {
        if (_gameEngine.spendSkillPointOnVitality()) {
            _playerData.value = _gameEngine.getPlayerStats()
        } else {
            _toastMessage.value = SingleEvent("Failed to spend skill point on Vitality (No points?).")
        }
    }

    fun spendSkillPointSpirit() {
        if (_gameEngine.spendSkillPointOnSpirit()) {
            _playerData.value = _gameEngine.getPlayerStats()
        } else {
            _toastMessage.value = SingleEvent("Failed to spend skill point on Spirit (No points?).")
        }
    }

    fun buyShopItem(item: GearItem) {
        val message = _gameEngine.buyItem(item)
        _playerData.value = _gameEngine.getPlayerStats()
        _toastMessage.value = SingleEvent(message)
    }

    fun sellPlayerItem(item: GearItem) {
        val message = _gameEngine.sellItem(item)
        _playerData.value = _gameEngine.getPlayerStats() // Update player data after selling
        _toastMessage.value = SingleEvent(message)
    }

    fun equipItem(item: GearItem) {
        val message = _gameEngine.equipItem(item)
        _playerData.value = _gameEngine.getPlayerStats()
        _toastMessage.value = SingleEvent(message)
    }

    fun unequipItem(itemType: com.example.idlerpg.models.ItemType) {
        val message = _gameEngine.unequipItem(itemType)
        _playerData.value = _gameEngine.getPlayerStats()
        _toastMessage.value = SingleEvent(message)
    }

    fun saveGame() {
        _repository.savePlayer(_gameEngine.player)
        _toastMessage.value = SingleEvent("Game Saved!")
    }

    private fun loadGame() {
        try {
            val loadedPlayer = _repository.loadPlayer()
            if (loadedPlayer != null) {
                _gameEngine.player = loadedPlayer
                _combatLog.value?.add(0,"Game Loaded!")
            } else {
                _combatLog.value?.add(0,"No saved game found. Starting new game.")
            }
            // Update effective stats after loading
            _gameEngine.player.currentHp = _gameEngine.player.currentHp.coerceAtMost(_gameEngine.player.effectiveMaxHp)
            _gameEngine.player.currentMana = _gameEngine.player.currentMana.coerceAtMost(_gameEngine.player.effectiveMaxMana)
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Error loading game", e)
            _combatLog.value?.add(0,"Error loading saved game. Starting new game.")
            // Clear corrupted save data
            try {
                _repository.clearSavedData()
            } catch (clearError: Exception) {
                android.util.Log.e("MainViewModel", "Error clearing corrupted save data", clearError)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveGame()
    }

    // Legacy methods for compatibility - marked as deprecated
    @Deprecated("Manual attacks have been removed - combat is now automatic")
    fun manualAttack() {
        // Do nothing - manual attacks are removed
    }

    @Deprecated("Auto fight is now handled by combatTick")
    fun autoFightTick() {
        combatTick()
    }

    @Deprecated("Use spendSkillPointStrength instead")
    fun spendSkillPointAttack() {
        spendSkillPointStrength()
    }

    @Deprecated("Defense is now equipment-based, use spendSkillPointVitality for health")
    fun spendSkillPointDefense() {
        spendSkillPointVitality()
    }

    @Deprecated("Use spendSkillPointVitality instead")
    fun spendSkillPointMaxHp() {
        spendSkillPointVitality()
    }
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * Useful for displaying messages like Toasts or Snackbars once.
 */
open class SingleEvent<out T>(private val content: T) {
    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}
