package com.example.noteit;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.noteit.Activities.CreateNoteActivity;
import com.example.noteit.Activities.EditNoteActivity;
import com.example.noteit.RecyclerView.Data;
import com.example.noteit.RecyclerView.RecyclerViewAdapter;
import com.example.noteit.RecyclerView.SelectListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements  SelectListener{

    private static final String EXTRA_HEADER = "com.example.noteit.HEADER";
    private static final String EXTRA_DESCRIPTION = "com.example.noteit.DESCRIPTION";
    private static final String EXTRA_COLOR="com.example.noteit.COLOR";
    RelativeLayout mainLayout;
    ImageView insertNote,deleteAll;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization
        insertNote = findViewById(R.id.addNote);
        deleteAll=findViewById(R.id.deleteAll);
        mainLayout=findViewById(R.id.mainLayout);
        recyclerView=findViewById(R.id.recyclerView);

        ArrayList<Data> arrayList = new ArrayList<>();

        File files = getFilesDir();
        String[] fileArray = files.list();
        String[] split;
        for(String fileName : fileArray){
            fileName = fileName.replace(".txt", "");
            split=fileName.split(":");
            Data data=new Data();
            data.setHeading(split[0]);
            data.setDescription(readDescription(fileName));
            data.setColor(split[split.length-1]);
            arrayList.add(data);
        }

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        adapter=new RecyclerViewAdapter(arrayList,this);
        recyclerView.setAdapter(adapter);

        insertNote.setOnClickListener(v->{
            startActivity(new Intent(this, CreateNoteActivity.class));
        });

        deleteAll.setOnClickListener(v->{
            deleteAllMethod();
            arrayList.clear();
            adapter.notifyDataSetChanged();
        });

        //swipe to delete
        new ItemTouchHelper((new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                arrayList.remove(viewHolder.getAdapterPosition());
                deleteMethod(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                Snackbar.make(mainLayout,
                        "Note Deleted ⚡",
                        3000).show();
            }
        })).attachToRecyclerView(recyclerView);

    }
    @Override
    public void onItemClicked(Data data) {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra(EXTRA_HEADER, data.getHeading());
            intent.putExtra(EXTRA_DESCRIPTION, data.getDescription());
            intent.putExtra(EXTRA_COLOR, data.getColor()+"");
            startActivity(intent);
            adapter.notifyDataSetChanged();
    }

    //read Description
    public String readDescription(String fileName) {
        StringBuilder whole = new StringBuilder();
        try {
            String line;
            FileInputStream fis = openFileInput(fileName+".txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            while((line = reader.readLine()) != null){
                if(whole.toString().equals(""))
                    whole.append(line);
                else
                    whole.append("\n").append(line);
            }
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return whole.toString();
    }
    // delete Single File
    public void deleteMethod(int index) {
        File dir = getFilesDir();
        File file = new File(String.valueOf(dir));
        File[] files= file.listFiles();
        files[index].delete();
    }
    // deleteALl Files
    public void deleteAllMethod() {
        File dir = getFilesDir();
        File file = new File(String.valueOf(dir));
        File[] files= file.listFiles();

            if(files.length == 0) {
                Snackbar.make(mainLayout,
                    "Nothing To Delete \uD83E\uDD70",
                    3000).show();
            }
            else{
                for (File value : files)
                    value.delete();
                Snackbar.make(mainLayout,
                        "All Notes Are Deleted ⚡",
                        3000).show();
            }
    }
}