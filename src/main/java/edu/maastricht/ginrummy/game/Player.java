package edu.maastricht.ginrummy.game;

public enum Player
{
    NONE,
    P1,
    P2;

    public Player getNextPlayer() {
        if(this == Player.P1)
        {
            return Player.P2;
        }
        else if(this == Player.P2)
        {
            return Player.P1;
        }
        throw new IllegalStateException();
    }
}
