package api;

import java.util.ArrayList;
import java.util.HashMap;
import util.Photo;
import util.Poke;

/**
 *
 * @author claudia
 */
public class UpdateState {

    private static UpdateState updateState;
    HashMap<String, String> photos;
    HashMap<String, String> pokes;

    public static UpdateState getInstance() {
        if (updateState == null) {
            updateState = new UpdateState();
            updateState.photos = new HashMap<>();
            updateState.pokes = new HashMap<>();
        }
        return updateState;
    }
}
