package com.bezman.model;

public class Rank extends BaseModel
{

    private int id;

    private int level, rankLevel;

    private String rank, levelName;

    private int xpRequired;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getRank()
    {
        return rank;
    }

    public void setRank(String rank)
    {
        this.rank = rank;
    }

    public int getXpRequired()
    {
        return xpRequired;
    }

    public void setXpRequired(int xpRequired)
    {
        this.xpRequired = xpRequired;
    }

    public String getLevelName()
    {
        return levelName;
    }

    public void setLevelName(String levelName)
    {
        this.levelName = levelName;
    }

    public int getRankLevel()
    {
        return rankLevel;
    }

    public void setRankLevel(int rankLevel)
    {
        this.rankLevel = rankLevel;
    }
}
