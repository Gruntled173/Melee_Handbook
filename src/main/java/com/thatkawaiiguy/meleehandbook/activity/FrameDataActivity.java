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

import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.thatkawaiiguy.meleehandbook.fragment.FrameInfoDialogFragment;
import com.thatkawaiiguy.meleehandbook.other.FrameDataHelper;
import com.thatkawaiiguy.meleehandbook.other.Preferences;
import com.thatkawaiiguy.meleehandbook.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FrameDataActivity extends AppCompatActivity {
    private int frame = 0;
    private int mTotal = 0;

    private Button nextBtn;
    private Button firstBtn;
    private Button backBtn;
    private Button playBtn;

    private SlidrInterface inter;

    private boolean paused = false;
    private boolean running = false;

    private String movePicked = "";
    private String characterPicked = "";
    private String characterPickedTitle = "";

    private TextView iasa;
    private TextView frameNumber;
    private TextView totalFrame;
    private TextView landingLag;

    private ImageView frameImage;

    private final ClickListener listener = new ClickListener();
    private final LongClickListener longListener = new LongClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().hasExtra("bundle") && savedInstanceState == null)
            savedInstanceState = getIntent().getExtras().getBundle("bundle");
        Preferences.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.framedata_layout);
        inter = Slidr.attach(this);

        Bundle mainData = getIntent().getExtras();
        if(mainData == null)
            return;
        characterPickedTitle = mainData.getString("option");
        movePicked = mainData.getString("frame");

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(characterPickedTitle + "'s " + movePicked);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        frameNumber = (TextView) findViewById(R.id.frameNumber);
        totalFrame = (TextView) findViewById(R.id.totalFrame);
        landingLag = (TextView) findViewById(R.id.landinglagText);
        iasa = (TextView) findViewById(R.id.iasaText);

        frameImage = (ImageView) findViewById(R.id.frameImage);

        firstBtn = (Button) findViewById(R.id.firstBtn);
        backBtn = (Button) findViewById(R.id.backBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        playBtn = (Button) findViewById(R.id.playBtn);

        switch(characterPickedTitle) {
            case "Dr. Mario":
                characterPicked = "doctor";
                break;
            case "Captain Falcon":
                characterPicked = "falcon";
                break;
            case "Ice Climbers":
                characterPicked = "climbers";
                break;
            case "Samus Aran":
                characterPicked = "samus";
                break;
            case "Princess Peach":
                characterPicked = "peach";
                break;
            default:
                characterPicked = characterPickedTitle.toLowerCase();
        }

        movePicked = FrameDataHelper.setShortMovePicked(movePicked);
        doStuff();
    }

    private void setFrame() {
        InputStream is = null;
        try {
            is = getResources().getAssets().open(characterPicked + File.separator +
                    movePicked + File.separator + frame + ".jpg");
        } catch(IOException e) {
            e.printStackTrace();
        }
        frameImage.setImageBitmap(BitmapFactory.decodeStream(is));
        frameNumber.setText(String.valueOf(frame + 1));
        if(!running)
            inter.unlock();
        else
            inter.lock();
    }

    private void interruptPlay() {
        listener.getHandler().removeCallbacks(listener.getRunnable());
        longListener.getHandler().removeCallbacks(longListener.getRunnable());
        running = false;
        inter.unlock();
    }

    private void doStuff() {
        InputStream is = null;
        String[] filelist = {"#@#", "#@$@#"};
        try {
            is = getResources().getAssets().open(characterPicked + File.separator +
                    movePicked + File.separator + frame + ".jpg");
            filelist = getResources().getAssets().list(characterPicked + File.separator +
                    movePicked);
        } catch(IOException e) {
            e.printStackTrace();
        }
        mTotal = filelist.length;
        totalFrame.setText(String.valueOf(mTotal));

        frameImage.setImageBitmap(BitmapFactory.decodeStream(is));

        landingLag.setText(FrameDataHelper.getLandLag(characterPickedTitle, movePicked));
        iasa.setText(FrameDataHelper.getIASA(characterPickedTitle, movePicked));

        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running) {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    frame = 0;
                    setFrame();
                } else {
                    interruptPlay();
                    firstBtn.performClick();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running) {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    if(frame > 0) {
                        frame--;
                        setFrame();
                    }
                } else {
                    interruptPlay();
                    backBtn.performClick();
                }
            }
        });
        backBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!running) {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    if(frame >= 10) {
                        frame -= 10;
                        setFrame();
                    }
                } else {
                    interruptPlay();
                    backBtn.performLongClick();
                }
                return true;
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!running) {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    if(frame < Integer.parseInt((String) totalFrame.getText()) - 1) {
                        frame++;
                        setFrame();
                    }
                } else {
                    interruptPlay();
                    nextBtn.performClick();
                }
            }
        });
        nextBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!running) {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    if(frame <= Integer.parseInt((String) totalFrame.getText()) - 11) {
                        frame += 10;
                        setFrame();
                    } else {
                        frame = Integer.parseInt((String) totalFrame.getText()) - 1;
                        setFrame();
                    }
                } else {
                    interruptPlay();
                    nextBtn.performLongClick();
                }
                return true;
            }
        });

        playBtn.setOnClickListener(listener);
        playBtn.setOnLongClickListener(longListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                interruptPlay();
                this.finish();
                return true;
            case R.id.action_info:
                interruptPlay();
                DialogFragment newFragment = new FrameInfoDialogFragment();
                newFragment.show(getFragmentManager(), "Frame Data info");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        interruptPlay();
        super.onStop();
    }

    class ClickListener implements View.OnClickListener {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(frame < Integer.parseInt((String) totalFrame.getText()) - 1) {
                    frame++;
                    setFrame();
                    running = true;
                } else {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    paused = false;
                    running = false;
                    playBtn.performClick();
                }
            }
        };

        @Override
        public void onClick(View v) {
            if(!running) {
                if(paused) {
                    playBtn.setBackgroundResource(R.drawable.pauseicon);
                    for(int i = frame; i < mTotal; i++)
                        handler.postDelayed(runnable, 52 * (i - (frame - 1)));
                } else {
                    frame = 0;
                    playBtn.setBackgroundResource(R.drawable.pauseicon);
                    for(int i = frame; i < mTotal; i++)
                        handler.postDelayed(runnable, 52 * (i - (frame - 1)));
                }
            } else {
                playBtn.setBackgroundResource(R.drawable.playicon);
                interruptPlay();
                paused = true;
            }
        }

        public Handler getHandler() {return handler;}

        public Runnable getRunnable() {return runnable;}
    }

    class LongClickListener implements View.OnLongClickListener {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(frame < Integer.parseInt((String) totalFrame.getText()) - 1) {
                    frame++;
                    setFrame();
                    running = true;
                } else {
                    playBtn.setBackgroundResource(R.drawable.playicon);
                    paused = false;
                    running = false;
                    playBtn.performLongClick();
                }
            }
        };

        @Override
        public boolean onLongClick(View v) {
            if(!running) {
                if(!paused) {
                    frame = 0;
                    playBtn.setBackgroundResource(R.drawable.pauseicon);
                    for(int i = frame; i < mTotal; i++)
                        handler.postDelayed(runnable, 21 * (i - (frame - 1)));
                } else {
                    playBtn.setBackgroundResource(R.drawable.pauseicon);
                    for(int i = frame; i < mTotal; i++)
                        handler.postDelayed(runnable, 21 * (i - (frame - 1)));
                }
            } else {
                playBtn.setBackgroundResource(R.drawable.playicon);
                interruptPlay();
                paused = true;
            }
            return true;
        }

        public Handler getHandler() {return handler;}

        public Runnable getRunnable() {return runnable;}
    }
}
