package ru.egor_d.vircitiesbot;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Egor Danilin on 04.04.2015.
 */
public class UserSportSkill {
    @SerializedName("sport_skill_type_id")
    public int id;

    public int level;
    public String experience;
}
