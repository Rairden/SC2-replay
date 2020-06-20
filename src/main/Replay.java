package main;

enum Matchup {
    ZvP, ZvT, ZvZ
}

public class Replay {

    Player p1;
    Player p2;
    Matchup matchup;

    public Replay(Player p1, Player p2, Matchup m) {
        this.p1 = p1;
        this.p2 = p2;
        this.matchup = m;
    }
}
