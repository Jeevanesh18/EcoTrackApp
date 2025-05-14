package com.example.ecotrack;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.List;

public class FAQAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listQuestions;
    private HashMap<String, String> listAnswers;


    public FAQAdapter(Context context, List<String> listQuestions, HashMap<String, String> listAnswers) {
        this.context = context;
        this.listQuestions = listQuestions;
        this.listAnswers = listAnswers;

    }


    @Override
    public int getGroupCount() {
        return listQuestions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1; // Each question has one answer
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listQuestions.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listAnswers.get(listQuestions.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String question = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_question_item, null);
        }

        TextView textView = convertView.findViewById(R.id.faq_question_text);
        textView.setText(question);
        textView.setTextColor(Color.parseColor("#000000"));

        Typeface typeface = ResourcesCompat.getFont(context, R.font.nunitosemibold);
        textView.setTypeface(typeface);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        String answer = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_answer_item, null);
        }

        TextView textView = convertView.findViewById(R.id.faq_answer_text);
        textView.setText(answer);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.nunitosemibold);
        textView.setTypeface(typeface);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
