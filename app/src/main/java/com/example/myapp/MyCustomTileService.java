package com.example.myapp;

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class MyCustomTileService extends TileService {

    // Called when the tile is added to the Quick Settings panel
    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    // Called when the tile becomes visible to the user
    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTileState();
    }

    // Called when the tile is no longer visible (panel is collapsed)
    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    // Called when the user removes the tile from Quick Settings
    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    // Handles the tap event on the tile
    @Override
    public void onClick() {
        super.onClick();
        
        Tile tile = getQsTile();
        if (tile == null) return;

        // Toggle logic: Switch between Active and Inactive states
        if (tile.getState() == Tile.STATE_INACTIVE) {
            tile.setState(Tile.STATE_ACTIVE);
            // TODO: Insert the background action you want to turn ON here
        } else {
            tile.setState(Tile.STATE_INACTIVE);
            // TODO: Insert the background action you want to turn OFF here
        }

        // Push the visual changes back to the system
        tile.updateTile(); 
    }

    // Helper method to sync the UI with your application's actual data state
    private void updateTileState() {
        Tile tile = getQsTile();
        if (tile != null) {
            boolean isFeatureEnabled = checkMyFeatureStatus(); // Your custom check
            
            if (isFeatureEnabled) {
                tile.setState(Tile.STATE_ACTIVE); // Highlighted state
            } else {
                tile.setState(Tile.STATE_INACTIVE); // Dimmed/Off state
            }
            tile.updateTile();
        }
    }

    private boolean checkMyFeatureStatus() {
        // Return your app's actual feature state from SharedPreferences or Database
        return false; 
    }
}
