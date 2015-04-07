package progark.mafia.mafiagame.activities;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Perÿyvind on 23/03/2015.
 */
public interface IPlayStoreActivity {
    boolean getConnectionStatus();

    GoogleApiClient getGoogleApiClient();
}
