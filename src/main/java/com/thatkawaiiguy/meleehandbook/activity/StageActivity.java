/*
    This file is part of Melee Handbook.

    Melee Handbook is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Melee Handbook is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Melee Handbook.  If not, see <http://www.gnu.org/licenses/>
 */

package com.thatkawaiiguy.meleehandbook.activity;

import android.os.Bundle;

import com.thatkawaiiguy.meleehandbook.R;

public class StageActivity extends ImageInfoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (optionPicked) {
            case "Battlefield":
                infoImage.setImageResource(R.drawable.battlefieldbox);
                break;
            case "Dream Land":
                infoImage.setImageResource(R.drawable.dreamlandbox);
                break;
            case "Final Destination":
                infoImage.setImageResource(R.drawable.fdbox);
                break;
            case "Fountain of Dreams":
                infoImage.setImageResource(R.drawable.fodbox);
                break;
            case "Kongo Jungle (SSB)":
                infoImage.setImageResource(R.drawable.kongo);
                break;
            case "Pokemon Stadium":
                infoImage.setImageResource(R.drawable.pokestadiumbox);
                break;
            case "Yoshi's Story":
                infoImage.setImageResource(R.drawable.ystorybox);
                break;
        }
    }
}
