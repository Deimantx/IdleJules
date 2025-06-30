package com.example.idlegame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences; // Added for SharedPreferences
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TextView moneyTextView; // Displays the current amount of money
    private TextView moneyPerSecondTextView; // Displays money generated automatically per second
    private TextView workerCountTextView; // Displays the number of hired workers
    private TextView managerCountTextView; // Displays the number of hired managers
    private Button makeMoneyButton; // Button to manually generate money
    private Button hireWorkerButton; // Button to hire a new worker
    private Button hireManagerButton; // Button to hire a new manager

    // Game State Variables
    private long money = 0; // Player's current currency
    private long moneyPerSecond = 0; // Currency generated automatically each second
    private int workerCount = 0; // Number of workers hired
    private int managerCount = 0; // Number of managers hired

    // Game Parameters - Initial values and multipliers
    private long baseMoneyPerClick = 1; // Amount of money earned per click on "Make Money" button
    private long workerCost = 10; // Initial cost to hire a worker
    private long managerCost = 100; // Initial cost to hire a manager
    private long workerPower = 1; // Money generated per worker per second
    private double workerCostIncreaseFactor = 1.15; // Factor by which worker cost increases (e.g., 1.15 for 15%)
    private double managerCostIncreaseFactor = 1.20; // Factor by which manager cost increases (e.g., 1.20 for 20%)
    private int managerWorkerBoost = 2; // Multiplier effect a manager has on worker production (e.g., 2 means managers double worker output)

    // Handler for game loop
    private final Handler gameLoopHandler = new Handler(); // Used to schedule repetitive tasks for the game's tick
    private final int GAME_LOOP_DELAY = 1000; // Delay in milliseconds for the game loop (1000ms = 1 second)

    // SharedPreferences constants for saving and loading game state
    private static final String PREFS_NAME = "IdleGamePrefs"; // Name of the SharedPreferences file
    private static final String KEY_MONEY = "money"; // Key for saving/loading money
    private static final String KEY_WORKER_COUNT = "workerCount"; // Key for saving/loading worker count
    private static final String KEY_MANAGER_COUNT = "managerCount"; // Key for saving/loading manager count
    private static final String KEY_WORKER_COST = "workerCost"; // Key for saving/loading worker cost
    private static final String KEY_MANAGER_COST = "managerCost"; // Key for saving/loading manager cost
    private static final String KEY_MONEY_PER_SECOND = "moneyPerSecond"; // Key for saving/loading money per second


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements by finding them in the layout XML
        moneyTextView = findViewById(R.id.moneyTextView);
        moneyPerSecondTextView = findViewById(R.id.moneyPerSecondTextView);
        workerCountTextView = findViewById(R.id.workerCountTextView);
        managerCountTextView = findViewById(R.id.managerCountTextView);
        makeMoneyButton = findViewById(R.id.makeMoneyButton);
        hireWorkerButton = findViewById(R.id.hireWorkerButton);
        hireManagerButton = findViewById(R.id.hireManagerButton);

        // Set up button click listeners
        // Listener for the "Make Money" button
        makeMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money += baseMoneyPerClick; // Increase money by the base amount
                updateUI(); // Refresh the display
            }
        });

        // Listener for the "Hire Worker" button
        hireWorkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (money >= workerCost) { // Check if player can afford a worker
                    money -= workerCost; // Deduct cost
                    workerCount++; // Increment worker count
                    workerCost = (long) (workerCost * workerCostIncreaseFactor); // Increase cost for next worker
                    updateMoneyPerSecond(); // Recalculate money generation rate
                    updateUI(); // Refresh the display
                }
            }
        });

        // Listener for the "Hire Manager" button
        hireManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (money >= managerCost) { // Check if player can afford a manager
                    money -= managerCost; // Deduct cost
                    managerCount++; // Increment manager count
                    managerCost = (long) (managerCost * managerCostIncreaseFactor); // Increase cost for next manager
                    updateMoneyPerSecond(); // Recalculate money generation rate (managers boost workers)
                    updateUI(); // Refresh the display
                }
            }
        });

        loadGame(); // Load saved game state when the activity is created
        updateMoneyPerSecond(); // Recalculate moneyPerSecond based on loaded worker/manager counts
        updateUI(); // Initialize the UI with loaded or default values

        startGameLoop(); // Start the main game loop for automatic money generation
    }

    /**
     * Loads the game state from SharedPreferences.
     * If no saved data is found, it uses default initial values.
     */
    private void loadGame() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        money = prefs.getLong(KEY_MONEY, 0); // Default to 0 if not found
        workerCount = prefs.getInt(KEY_WORKER_COUNT, 0);
        managerCount = prefs.getInt(KEY_MANAGER_COUNT, 0);
        workerCost = prefs.getLong(KEY_WORKER_COST, 10); // Default to initial worker cost
        managerCost = prefs.getLong(KEY_MANAGER_COST, 100); // Default to initial manager cost
        // moneyPerSecond is recalculated by updateMoneyPerSecond() after loading other values.
    }

    /**
     * Saves the current game state to SharedPreferences.
     * This includes money, counts of workers/managers, and their current costs.
     */
    private void saveGame() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_MONEY, money);
        editor.putInt(KEY_WORKER_COUNT, workerCount);
        editor.putInt(KEY_MANAGER_COUNT, managerCount);
        editor.putLong(KEY_WORKER_COST, workerCost);
        editor.putLong(KEY_MANAGER_COST, managerCost);
        editor.putLong(KEY_MONEY_PER_SECOND, moneyPerSecond); // Also save current MPS for quick restore
        editor.apply(); // Apply changes asynchronously
    }

    /**
     * Calculates the total money generated per second based on the number of workers,
     * their individual power, and any boosts from managers.
     */
    private void updateMoneyPerSecond() {
        long baseWorkerProduction = workerCount * workerPower; // Total production from workers alone
        if (managerCount > 0) {
            // Managers multiply the production of all workers
            moneyPerSecond = baseWorkerProduction * (managerCount * managerWorkerBoost);
        } else {
            moneyPerSecond = baseWorkerProduction; // No managers, so just base worker production
        }
    }

    /**
     * Updates all TextViews in the UI to reflect the current game state.
     * Also updates button texts (costs) and their enabled/disabled states.
     */
    private void updateUI() {
        moneyTextView.setText(String.format(Locale.getDefault(), "Money: %d", money));
        moneyPerSecondTextView.setText(String.format(Locale.getDefault(), "Money per second: %d", moneyPerSecond));
        workerCountTextView.setText(String.format(Locale.getDefault(), "Workers: %d", workerCount));
        managerCountTextView.setText(String.format(Locale.getDefault(), "Managers: %d", managerCount));

        makeMoneyButton.setText(String.format(Locale.getDefault(), "Make Money (+$%d)", baseMoneyPerClick));
        hireWorkerButton.setText(String.format(Locale.getDefault(), "Hire Worker (Cost: $%d)", workerCost));
        hireManagerButton.setText(String.format(Locale.getDefault(), "Hire Manager (Cost: $%d)", managerCost));

        // Enable/disable buttons based on cost
        hireWorkerButton.setEnabled(money >= workerCost); // Enable button only if player can afford it
        hireManagerButton.setEnabled(money >= managerCost); // Enable button only if player can afford it
    }

    /**
     * Starts the main game loop using a Handler to repeatedly perform actions (like adding money)
     * at a fixed interval (GAME_LOOP_DELAY).
     */
    private void startGameLoop() {
        gameLoopHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                money += moneyPerSecond; // Add automatically generated money
                updateUI(); // Refresh the display
                gameLoopHandler.postDelayed(this, GAME_LOOP_DELAY); // Schedule the next tick
            }
        }, GAME_LOOP_DELAY);
    }

    /**
     * Called when the activity is no longer visible to the user (e.g., app is minimized or another activity comes to the foreground).
     * Game state is saved here.
     */
    @Override
    protected void onPause() {
        super.onPause();
        saveGame(); // Save game state when the app is paused
        // Optional: Could pause game loop here by removing callbacks from the handler
        // gameLoopHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Called when the activity will start interacting with the user.
     * UI is refreshed, and game loop could be resumed here if it was paused.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Optional: If game loop was paused in onPause(), it should be resumed here.
        // Ensure startGameLoop() is not called multiple times if it's already running.
        // One way to manage this is to remove callbacks in onPause and re-post in onResume.
        // For this simple idle game, the loop continues running even when paused,
        // and Android handles UI updates gracefully.
        updateUI(); // Refresh UI on resume to show latest state (e.g., if money changed while paused due to external factors - not applicable here)
    }
}
