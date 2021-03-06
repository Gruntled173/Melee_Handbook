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

package com.thatkawaiiguy.meleehandbook.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thatkawaiiguy.meleehandbook.R;
import com.thatkawaiiguy.meleehandbook.other.ArrayHelper;

import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String[] mDataSet;

    private int termAmount = 0;
    private int titleTermAmount = 0;
    private int titleAmount = 0;

    private final Context mContext;

    private String query = "";

    public SearchAdapter(String[] data, int titleNum, int titleTerms, int terms, Context context, String query) {
        mDataSet = data;
        titleAmount = titleNum;
        titleTermAmount = titleTerms;
        termAmount = terms;
        mContext = context;
        this.query = query;
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String string = Html.fromHtml(ArrayHelper.getInfoString(mDataSet[position], mContext))
                .toString();
        if(ifTerm(position)){
            highlight(query, mDataSet[position], ((TermAdapter.ViewHolder) holder).getTextView());
            highlight(query, ArrayHelper.getTermInfoString(mDataSet[position], mContext),
                    ((TermAdapter.ViewHolder) holder).getTermTextView());
        } else {
            highlight(query, mDataSet[position], ((TermAdapter.ViewHolder) holder).getTextView());
            highlightAndCut(query, string, ((TermAdapter.ViewHolder) holder).getTermTextView());
        }
    }

    private void highlight(String search, String originalText, TextView textView) {
        int startPos = originalText.toLowerCase(Locale.US).indexOf(search.toLowerCase(Locale.US));
        int endPos = startPos + search.length();

        if(startPos != -1){
            Spannable spannable = new SpannableString(originalText);
            ColorStateList yellowColor = new ColorStateList(new int[][]{new int[]{}}, new
                    int[]{ContextCompat.getColor(mContext, R.color.overscroll_color)});
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1,
                    yellowColor, null);

            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannable);
        } else
            textView.setText(originalText);
    }

    private void highlightAndCut(String search, String originalText, TextView textView) {
        int startPos = originalText.toLowerCase().indexOf(search);
        int endPos = startPos + search.length();

        int wholeStart = originalText.toLowerCase().indexOf(" ", startPos - 100);
        if(wholeStart == -1) wholeStart = 0;

        if(originalText.substring(wholeStart, wholeStart + 1).equals(",")) wholeStart += 1;

        int wholeEnd = originalText.toLowerCase().indexOf(" ", endPos + 100 < originalText.length
                () - 1 ? endPos + 100 : endPos - search.length() - 1);

        if(wholeEnd == -1) wholeEnd = endPos;

        originalText = originalText.substring(wholeStart, wholeEnd).trim() + " (Click for more)";

        startPos = originalText.toLowerCase().indexOf(search);
        endPos = startPos + search.length();
        // This should always be true, just a sanity check
        if(startPos != -1) {
            Spannable spannable = new SpannableString(originalText);
            ColorStateList yellowColor = new ColorStateList(new int[][]{new int[]{}}, new
                    int[]{ContextCompat.getColor(mContext, R.color.overscroll_color)});
            TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1,
                    yellowColor, null);

            spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannable);
        } else
            textView.setText(originalText);
    }

    @Override
    public int getItemViewType(int position) {
        if(ifTerm(position))
            return 0;
        else
            return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.term_layout, parent, false);

            return new TermAdapter.ViewHolder(v);
        } else if(viewType == 1) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_layout, parent, false);

            return new TermAdapter.ViewHolder(v);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        return new TermAdapter.ViewHolder(v);
    }

    private boolean ifTerm(int position){
        return (position < titleTermAmount || (position
                < titleTermAmount + titleAmount + termAmount && position >= titleAmount + titleTermAmount));
    }
}