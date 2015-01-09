package tda2.insa.com.be_covoiturage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Settings extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final ListView listview = (ListView) findViewById(R.id.listView);
        String[] values = new String[] { "Nom: ", "Prenom", "Email",
                "Telephone" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        //list.set(0, "Nom2");
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
               // view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                //            @Override
                //            public void run() {
//                                Log.w("dead", "test");

//                            }
//                        });
                if (adapter.getItem(position) == "Nom"){
                    Log.w("list", "testNom");
                    Intent edit = new Intent(Settings.this, EditSetting.class);
                    startActivity(edit);
                } else if (adapter.getItem(position) == "Prenom") {
                    Log.w("list", "testPrenom");
                    Intent edit = new Intent(Settings.this, EditSetting.class);
                    startActivity(edit);
                } else if (adapter.getItem(position) == "Driver") {
                    Log.w("list", "testDriver");
                    Intent edit = new Intent(Settings.this, EditSetting.class);
                    startActivity(edit);
                } else if(adapter.getItem(position) == "Email") {
                    Log.w("list", "testEmail");
                    Intent edit = new Intent(Settings.this, EditSetting.class);
                    startActivity(edit);
                } else if(adapter.getItem(position) == "Tel") {
                    Log.w("list", "testTel");
                    Intent edit = new Intent(Settings.this, EditSetting.class);
                    startActivity(edit);
                }
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button3){
            Log.w("bouton", "testButton");
        }
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
