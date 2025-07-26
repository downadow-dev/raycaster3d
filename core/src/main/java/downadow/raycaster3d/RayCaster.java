/*
   Copyright 2025 Menshikov S.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package downadow.raycaster3d;

import com.badlogic.gdx.files.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.net.*;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Input.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class RayCaster implements ApplicationListener {
    ShapeRenderer shape;
    SpriteBatch batch;
    FitViewport viewport;
    Vector2 start, dest;
    Vector2 moveStart, moveDest;
    Vector2 touch;
    BitmapFont font;
    final int WIDTH = 640, HEIGHT = 388;
    final float FOV = 1.26f, MAXDIST = 35.0f;
    float playerX = 1.5f, playerY = 1.5f, playerAngle = 0;
    float player2X = -10000000, player2Y = -10000000;
    float weaponX = -10000000, weaponY = -10000000;
    boolean game = false, wait = false;
    String url = "";
    boolean withPlayer = false, playerShoot = false;
    boolean shot = false;
    private int score = 0, setX = -1, setY = -1;
    final byte blocks[][][] = {
        {
            {2,1,1,1,1,1,1,1,1,2},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {2,1,1,1,1,1,1,1,1,2}
        }, {
            {0,0,0,0,3,3,0,0,0,0},
            {0,0,0,3,3,3,3,0,0,0},
            {0,0,3,3,3,3,3,3,0,0},
            {0,3,3,3,3,3,3,3,3,0},
            {3,3,3,3,3,3,3,3,3,3},
            {3,3,3,3,3,3,3,3,3,3},
            {0,3,3,3,3,3,3,3,3,0},
            {0,0,3,3,3,3,3,3,0,0},
            {0,0,0,3,3,3,3,0,0,0},
            {0,0,0,0,3,3,0,0,0,0}
        }, {
            {0,3,0,3,0,3,0,3,0,3},
            {3,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,3},
            {3,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,3},
            {3,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,3},
            {3,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,3},
            {3,0,3,0,3,0,3,0,3,0}
        }, {
            {5,4,4,4,4,4,4,4,4,5},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {4,4,4,4,4,4,4,4,4,4},
            {5,4,4,4,4,4,4,4,4,5}
        }
    };
    byte map[][] = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,1,1,1,1},
        {1,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,1,1,1},
        {1,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
        {1,2,2,1,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,3,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,3,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,3,1,1},
        {1,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,3,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0,0,0,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,3,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,3,0,0,0,0,0,0,0,0,0,0,0,2,1},
        {1,0,2,0,0,2,0,0,2,0,0,0,0,0,0,0,0,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,2,0,0,2,0,0,2,0,0,2,0,0,2,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,2,0,0,2,0,0,2,0,0,2,0,0,2,0,0,1,0,1,1,1,1,0,0,0,1,1,1,1,0,0,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,0,0,0,1,1,1,1,0,0,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,2,0,0,2,0,0,2,0,0,2,0,0,2,0,0,1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,3,0,1},
        {1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,3},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    Color[] colorTab;
    
    private int collision(float x, float y) {
        if(x < 0 || x >= map[0].length || y < 0 || y >= map.length || map[(int)y][(int)x] == 0) return 0;
        return blocks[map[(int)y][(int)x] - 1][(int)((y - (int)y) * 10)][(int)((x - (int)x) * 10)];
    }
    
    public void create() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new FitViewport(WIDTH, HEIGHT);
        start = new Vector2();
        dest = new Vector2();
        touch = new Vector2();
        start.set(-10000, -10000);
        dest.set(-10000, -10000);
        moveStart = new Vector2();
        moveDest = new Vector2();
        moveStart.set(-10000, -10000);
        moveDest.set(-10000, -10000);
        font = new BitmapFont(Gdx.files.internal("font/font.fnt"), false);
        font.setFixedWidthGlyphs(" ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*ЁЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮёйцукенгшщзхъфывапролджэячсмитьбю█№—");
        font.setUseIntegerPositions(false);
        
        colorTab = new Color[] {new Color(0.1f, 0.1f, 0.1f, 1),
            new Color(1.0f, 1.0f, 1.0f, 1), new Color(0.8f, 0.8f, 0.8f, 1),
            new Color(0.6f, 0.6f, 0.6f, 1), new Color(0.0f, 0.0f, 1.0f, 1),
            new Color(0.0f, 0.0f, 0.8f, 1)};
        
        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean keyTyped(char c) {
                if(game || (c < (char)32 && c != '\n' && c != '\r' && c != '\b' && c != (char)0x7F))
                    return false;
                if(c == '\b' || c == (char)0x7F) {
                    if(url.length() == 0)
                        return false;
                    String newText = "";
                    char[] chars = url.toCharArray();
                    for(int i = 0; i < chars.length - 1; i++)
                        newText += "" + chars[i];
                    url = newText;
                } else if(c != '\n' && c != '\r') {
                    url += "" + c;
                } else if(Gdx.app.getType() == Application.ApplicationType.Android)
                    Gdx.input.setOnscreenKeyboardVisible(false);
                return true;
            }
            
            public boolean keyDown(int key) {
                if(!game) return false;
                if(key == Input.Keys.W) {
                    moveStart.set(20, 20);
                    moveDest.set(20, 30);
                    return true;
                } else if(key == Input.Keys.S) {
                    moveStart.set(20, 20);
                    moveDest.set(20, 10);
                    return true;
                } else if(key == Input.Keys.A) {
                    start.set(WIDTH + 20, 20);
                    dest.set(WIDTH + 10, 20);
                    return true;
                } else if(key == Input.Keys.D) {
                    start.set(WIDTH + 10, 20);
                    dest.set(WIDTH + 20, 20);
                    return true;
                } else if(key == Input.Keys.SPACE && withPlayer) {
                    withPlayer = false;
                    return true;
                } else if(key == Input.Keys.SPACE && !wait && !shot && weaponX < -1000000 && weaponY < -1000000) {
                    weaponX = playerX;
                    weaponY = playerY;
                    final float dirX = (float)Math.cos(playerAngle);
                    final float dirY = (float)Math.sin(playerAngle);
                    withPlayer =
                        (moveDest.x > -1000 && moveDest.y > -1000 && moveDest.y > moveStart.y) ? true : false;
                    new Thread() {
                        public void run() {
                            while(weaponX > 0.5f && weaponY > 0.5f &&
                                  weaponX < map[0].length - 0.5f && weaponY < map.length - 0.5f &&
                                  collision(weaponX, weaponY) == 0) {
                                if(weaponX > player2X - 0.4f && weaponX < player2X + 0.4f &&
                                   weaponY > player2Y - 0.4f && weaponY < player2Y + 0.4f) {
                                    if(!playerShoot) {
                                        playerShoot = true;
                                        score++;
                                    }
                                    player2X = weaponX;
                                    player2Y = weaponY;
                                } else if(withPlayer) {
                                    playerX = weaponX;
                                    playerY = weaponY;
                                }
                                weaponX += dirX * 0.18f;
                                weaponY += dirY * 0.18f;
                                try { Thread.sleep(18); } catch(Exception ex) {}
                            }
                            
                            if(weaponX > 0.5f && weaponY > 0.5f &&
                               weaponX < map[0].length - 0.5f && weaponY < map.length - 0.5f &&
                               map[(int)weaponY][(int)weaponX] == 1) {
                                setX = (int)weaponX;
                                setY = (int)weaponY;
                                map[(int)weaponY][(int)weaponX] = 4;
                            }
                            try { Thread.sleep(1000); } catch(Exception ex) {}
                            playerShoot = false;
                            weaponX = -10000000;
                            weaponY = -10000000;
                        }
                    }.start();
                    return true;
                }
                return false;
            }
            
            public boolean keyUp(int key) {
                if(!game) return false;
                if(key == Input.Keys.W || key == Input.Keys.S) {
                    moveDest.set(-10000, -10000);
                    return true;
                } else if(key == Input.Keys.A || key == Input.Keys.D) {
                    dest.set(-10000, -10000);
                    return true;
                }
                return false;
            }
            
            public boolean touchDown(int x, int y, int ptr, int btn) {
                touch.set(x, y);
                viewport.unproject(touch);
                if(game) {
                    if(touch.x > WIDTH / 2 && touch.y > HEIGHT / 1.8f) return keyDown(Input.Keys.SPACE);
                    else if(touch.x > WIDTH / 2) start.set(touch.x, touch.y);
                    else moveStart.set(touch.x, touch.y);
                } else {
                    if(touch.y > HEIGHT / 2 && Gdx.app.getType() == Application.ApplicationType.Android)
                        Gdx.input.setOnscreenKeyboardVisible(true);
                    else if(touch.x < WIDTH / 2) {
                        if(!url.isEmpty()) {
                            new Thread() {
                                public void run() {
                                    Socket client = Gdx.net.newClientSocket(Protocol.TCP, url.split(":")[0], Integer.parseInt(url.split(":")[1]), new SocketHints());
                                    BufferedReader stream = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                                    playerX = 2.5f;
                                    playerY = 85.5f;
                                    game = true;
                                    while(true) {
                                        try {
                                            Thread.sleep(17);
                                            
                                            if(setY >= 0) {
                                                client.getOutputStream().write(("ball " + setX + " " + setY + "\n").getBytes(StandardCharsets.UTF_8));
                                                setX = -1;
                                                setY = -1;
                                            } else {
                                                client.getOutputStream().write((playerX + " " + playerY + (playerShoot ? (" " + player2X + " " + player2Y) : "") + "\n").getBytes(StandardCharsets.UTF_8));
                                            }
                                            String[] msg = stream.readLine().split(" ");
                                            if(msg.length < 2) continue;
                                            else if(msg.length == 3) map[Integer.parseInt(msg[2])][Integer.parseInt(msg[1])] = 4;
                                            else {
                                                if(!playerShoot) {
                                                    player2X = Float.parseFloat(msg[0]);
                                                    player2Y = Float.parseFloat(msg[1]);
                                                }
                                                if(msg.length == 4) {
                                                    shot = true;
                                                    withPlayer = false;
                                                    playerX = Float.parseFloat(msg[2]);
                                                    playerY = Float.parseFloat(msg[3]);
                                                } else shot = false;
                                            }
                                        } catch(Exception ex) {}
                                    }
                                }
                            }.start();
                        } else game = true;
                    } else if(!url.isEmpty()) {
                        new Thread() {
                            public void run() {
                                ServerSocketHints hints = new ServerSocketHints();
                                hints.backlog = 1;
                                hints.acceptTimeout = 0;
                                ServerSocket server = Gdx.net.newServerSocket(Protocol.TCP, url.split(":")[0], Integer.parseInt(url.split(":")[1]), hints);
                                wait = true;
                                game = true;
                                Socket client = server.accept(null);
                                BufferedReader stream = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
                                wait = false;
                                while(true) {
                                    try {
                                        Thread.sleep(17);
                                        
                                        String[] msg = stream.readLine().split(" ");
                                        if(msg.length < 2) continue;
                                        else if(msg.length == 3) map[Integer.parseInt(msg[2])][Integer.parseInt(msg[1])] = 4;
                                        else {
                                            if(!playerShoot) {
                                                player2X = Float.parseFloat(msg[0]);
                                                player2Y = Float.parseFloat(msg[1]);
                                            }
                                            if(msg.length == 4) {
                                                shot = true;
                                                withPlayer = false;
                                                playerX = Float.parseFloat(msg[2]);
                                                playerY = Float.parseFloat(msg[3]);
                                            } else shot = false;
                                        }
                                        
                                        if(setY >= 0) {
                                            client.getOutputStream().write(("ball " + setX + " " + setY + "\n").getBytes(StandardCharsets.UTF_8));
                                            setX = -1;
                                            setY = -1;
                                        } else {
                                            client.getOutputStream().write((playerX + " " + playerY + (playerShoot ? (" " + player2X + " " + player2Y) : "") + "\n").getBytes(StandardCharsets.UTF_8));
                                        }
                                    } catch(Exception ex) {}
                                }
                            }
                        }.start();
                    }
                }
                return true;
            }
            
            public boolean touchDragged(int x, int y, int ptr) {
                if(!game) return false;
                touch.set(x, y);
                viewport.unproject(touch);
                if(touch.x > WIDTH / 2) dest.set(touch.x, touch.y);
                else moveDest.set(touch.x, touch.y);
                return true;
            }
            
            public boolean touchUp(int x, int y, int ptr, int btn) {
                if(!game) return false;
                touch.set(x, y);
                viewport.unproject(touch);
                if(touch.x > WIDTH / 2) dest.set(-10000, -10000);
                else moveDest.set(-10000, -10000);
                return true;
            }
        });
        
        new Thread() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(18);
                        
                        if(dest.y > -1000 || dest.x > -1000) {
                            playerAngle += (float)Math.cos(Math.atan2(dest.y - start.y, dest.x - start.x)) * 0.028f;
                            if(playerAngle < 0) playerAngle = 6.283f;
                            else if(playerAngle > 6.283f) playerAngle = 0;
                        }
                        
                        if(!wait && (moveDest.y > -1000 || moveDest.x > -1000)) {
                            final float a = playerAngle - (float)Math.PI / 2 + (float)Math.atan2(moveDest.y - moveStart.y, moveStart.x - moveDest.x);
                            final float newX = playerX + (float)Math.cos(a) * 0.077f;
                            if(collision(newX, playerY) == 0) playerX = newX;
                            final float newY = playerY + (float)Math.sin(a) * 0.077f;
                            if(collision(playerX, newY) == 0) playerY = newY;
                        }
                    } catch(Exception ex) {}
                }
            }
        }.start();
    }
    
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
    }
    
    public void pause() {}
    public void resume() {}
    public void dispose() {}
    
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        viewport.getCamera().update();
        shape.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        
        if(!game) {
            font.getData().setScale(0.4f);
            batch.begin();
            if(url.isEmpty()) {
                font.setColor(Color.GRAY);
                font.draw(batch, "<addr>:<port>", 10, HEIGHT - 20);
                font.setColor(Color.WHITE);
                font.draw(batch, "[singleplayer]", 8, 20);
            } else {
                font.setColor(Color.WHITE);
                font.draw(batch, url + "█", 10, HEIGHT - 20);
                font.draw(batch, "[client]", 8, 20);
                font.draw(batch, "[server]", WIDTH / 2, 20);
            }
            batch.end();
        } else {
            shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.setColor(colorTab[0]);
            shape.rect(0, 0, WIDTH, HEIGHT / 2);
            
            for(int x = 0; x < WIDTH; x++) {
                final float rayAngle = playerAngle - FOV / 2 + x * (FOV / WIDTH);
                final float dx = (float)Math.cos(rayAngle);
                final float dy = (float)Math.sin(rayAngle);
                
                int i = 0;
                float dist = 0.03125f;
                while(dist < MAXDIST && (i = collision(playerX + dx * dist, playerY + dy * dist)) == 0)
                    dist += 0.03125f;
                if(dist >= MAXDIST) continue;
                
                shape.setColor(colorTab[i]);
                shape.rect(x, HEIGHT / 2 - HEIGHT / 2 / dist, 1, HEIGHT / dist);
            }
            findPlayer:
            for(int x = 0; x < WIDTH; x++) {
                final float rayAngle = playerAngle - FOV / 2 + x * (FOV / WIDTH);
                final float dx = (float)Math.cos(rayAngle);
                final float dy = (float)Math.sin(rayAngle);
                
                float dist = 0.03125f;
                while(dist < MAXDIST && collision(playerX + dx * dist, playerY + dy * dist) == 0) {
                    dist += 0.03125f;
                    if(playerX + dx * dist > player2X - 0.025f && playerX + dx * dist < player2X + 0.025f &&
                       playerY + dy * dist > player2Y - 0.025f && playerY + dy * dist < player2Y + 0.025f) {
                        shape.setColor(Color.GREEN);
                        shape.ellipse(x - HEIGHT / dist / 3, HEIGHT / 2 - HEIGHT / 2 / dist, HEIGHT / dist / 1.5f, HEIGHT / dist);
                        break findPlayer;
                    }
                }
            }
            findWeapon:
            for(int x = 0; x < WIDTH; x++) {
                final float rayAngle = playerAngle - FOV / 2 + x * (FOV / WIDTH);
                final float dx = (float)Math.cos(rayAngle);
                final float dy = (float)Math.sin(rayAngle);
                
                float dist = 0.5f;
                while(dist < MAXDIST && collision(playerX + dx * dist, playerY + dy * dist) == 0) {
                    dist += 0.03125f;
                    if(playerX + dx * dist > weaponX - 0.025f && playerX + dx * dist < weaponX + 0.025f &&
                       playerY + dy * dist > weaponY - 0.025f && playerY + dy * dist < weaponY + 0.025f) {
                        shape.setColor(Color.BLUE);
                        dist += 0.4f;
                        shape.ellipse(x - HEIGHT / dist / 2, HEIGHT / 2 - HEIGHT / 2 / dist, HEIGHT / dist, HEIGHT / dist);
                        break findWeapon;
                    }
                }
            }
            shape.end();
            
            if(dest.y > -1000 || dest.x > -1000) {
                shape.begin(ShapeRenderer.ShapeType.Line);
                shape.setColor(Color.CYAN);
                shape.line(start.x, start.y, dest.x, start.y);
                shape.end();
            }
            
            if(moveDest.y > -1000 || moveDest.x > -1000) {
                shape.begin(ShapeRenderer.ShapeType.Line);
                shape.setColor(Color.YELLOW);
                shape.line(moveStart.x, moveStart.y, moveDest.x, moveDest.y);
                shape.end();
            }
            
            if(score > 0 || wait) {
                font.getData().setScale(0.34f);
                font.setColor(Color.CYAN);
                batch.begin();
                font.draw(batch, (wait ? "waiting for another player..." : ("" + score)), 10, HEIGHT - 16);
                batch.end();
            }
        }
    }
}
