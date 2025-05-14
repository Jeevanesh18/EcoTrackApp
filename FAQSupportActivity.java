package com.example.ecotrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FAQSupportActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    List<String> listQuestions;
    HashMap<String, String> listAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqsupport);

        //return to fragment when press back
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expandableListView = findViewById(R.id.faq_expandable_list);
        initializeFAQData();

        FAQAdapter adapter = new FAQAdapter(this, listQuestions, listAnswers);
        expandableListView.setAdapter(adapter);


    }



    public void onBackClick(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initializeFAQData() {
        listQuestions = new ArrayList<>();
        listAnswers = new HashMap<>();


        listQuestions.add("Q1: How do I reset my password?");
        listAnswers.put(listQuestions.get(0),
                "Click 'Forgot Password' on the login screen, enter your email address, " +
                        "and follow the instructions sent to your email to create a new password. " +
                        "\n\nIf you still remember your old password, you can also change it by going to your profile, " +
                        "clicking on the three dots, selecting 'Modify Password,' and following the instructions.");


        listQuestions.add("Q2: How to report overflowing bins?");
        listAnswers.put(listQuestions.get(1),
                "Tap the 'Report' button on the home screen, select 'Overflowing Bin', add the location " +
                        "and a photo if possible, then submit your report.");

        listQuestions.add("Q3: How do I request a special waste pickup?");
        listAnswers.put(listQuestions.get(2),
                "Go to 'Special Pickup' in the main menu, fill in the pickup details (location, waste type, " +
                        " and submit your request. You'll receive a confirmation notification.");


        listQuestions.add("Q4: What types of waste can be collected?");
        listAnswers.put(listQuestions.get(3),
                "We collect: household waste, recyclables, large furniture, electronic waste, and hazardous " +
                        "materials. Some items may require special handling.");



        listQuestions.add("Q5: How can I contact customer support?");
        listAnswers.put(listQuestions.get(4),
                "Email us at ecotrackmanagement@yahoo.com (Mon-Fri, 9AM-5PM).");


        listQuestions.add("Q6: Why isn't the app working properly?");
        listAnswers.put(listQuestions.get(5),
                "Try these steps:\n" +
                        "1. Check your internet connection\n" +
                        "2. Restart the app\n" +
                        "3. Update to the latest version\n" +
                        "If problems persist, contact our support team.");
    }
}