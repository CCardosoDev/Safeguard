package util;

/**
 *
 * @author claudia
 */
public class Poke {

    private String pokeID;
    private String pokeType;

    public Poke(String pokeID, String pokeType) {
        this.pokeID = pokeID;
        this.pokeType = pokeType;
    }

    public String getPokeID() {
        return pokeID;
    }

    public String getPokeType() {
        return pokeType;
    }
}
