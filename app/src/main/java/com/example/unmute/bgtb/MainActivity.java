/**
 * Alexander Andersson 930224
 */

package com.example.unmute.bgtb;

import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.Toast;


/**
 * MainActivity är automatiskt implementerad av Android Studio från en preset, men har genomgåtts
 * för att se vad som händer med TabHost osv. (Det som får swipe-funktionen att fungera är dock för
 * komplicerat.) Därför lämnar jag kvar kommentarerna som autogenererats. Tilläggen här är
 * konstanter och vilka fragment som tabbarna ska leda till genom SectionsPagerAdapter.
 */
public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final int DICE_MAX_AMOUNT = 6;       //Konstanter för de tre fragmenten.
    private static final int COUNTER_MAX_AMOUNT = 6;    //

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    return DiceThrow.newInstance(position + 1);
                case 1:
                    return Counters.newInstance(position + 1);
                default:
                    return Calculator.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * Fragment 1: DiceThrow
     * Hantera antal tärningar, antal sidor på dem, och att kunna kasta dem.
     */
    public static class DiceThrow extends Fragment implements OnItemSelectedListener {

        public static class Dice {
            private int maxValue;
            private int curValue;
            private String skin;        //Är till för ev. alternativa bilder namngivna med prefix
            private ImageView image;
            private int imageId;        //Sparas för att id-nummret till bilden i en imageview inte kunde hämtas ut.

            Dice(ImageView image) {
                maxValue = 6;
                curValue = 1;
                skin = "";
                this.image = image;

                setImage();
            }

            int getMaxValue() {
                return maxValue;
            }
            int getCurValue() {
                return curValue;
            }
            int getImageId() {
                return imageId;
            }

            void setMaxValue(int maxValue) {
                if (maxValue == 6 || maxValue == 20) {
                    this.maxValue = maxValue;
                    if (maxValue < curValue) {
                        curValue = 1;
                    }
                }
            }
            void setCurValue(int curValue) {
                if (curValue <= maxValue) {
                    this.curValue = curValue;
                }
            }

            void setImage() {
                String max = Integer.toString(maxValue);
                String cur = Integer.toString(curValue);

                /*För att stämma överens med filnamn*/
                imageId = image.getResources().getIdentifier(skin + "d" + max + "_" + cur, "drawable", "com.example.unmute.bgtb");
                image.setImageResource(imageId);

            }
            void setImage(int resId) {
                imageId = resId;
                image.setImageResource(resId);
            }

            void throwDice() { //Kasta tärningen och uppdatera bild.
                Random r = new Random();
                curValue = r.nextInt(maxValue) + 1;
                setImage();
            }
        }

        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView diceImgArray[];
        Dice diceArray[];
        Spinner spnAmount;
        Spinner spnType;
        Button btnThrow;

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        String spnAmountDef; //Till för att spinners ska tilldelas rätt värden.
        String spnTypeDef;   //

        public static DiceThrow newInstance(int sectionNumber) {
            DiceThrow fragment = new DiceThrow();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.dicethrow_layout, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            pref = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            editor = pref.edit();

            btnThrow = (Button) getActivity().findViewById(R.id.btnThrow);
            btnThrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    throwDice();
                }
            });

            diceImgArray = new ImageView[DICE_MAX_AMOUNT];
            diceImgArray[0] = (ImageView) getActivity().findViewById(R.id.imgDice1);
            diceImgArray[1] = (ImageView) getActivity().findViewById(R.id.imgDice2);
            diceImgArray[2] = (ImageView) getActivity().findViewById(R.id.imgDice3);
            diceImgArray[3] = (ImageView) getActivity().findViewById(R.id.imgDice4);
            diceImgArray[4] = (ImageView) getActivity().findViewById(R.id.imgDice5);
            diceImgArray[5] = (ImageView) getActivity().findViewById(R.id.imgDice6);

            diceArray = new Dice[DICE_MAX_AMOUNT];
            for(int i = 0; i < DICE_MAX_AMOUNT; i++) {
                diceArray[i] = new Dice(diceImgArray[i]);
            }

            spnAmountDef = "";
            spnTypeDef = "";

            spnAmountDef = pref.getString("dice_amount", "6");
            spnTypeDef = pref.getString("dice_type", "d6");
            initSpinners();

            loadDice(diceArray);
        }

        @Override
        public void onPause() {
            super.onPause();
            saveDice();
        }

        /**
         * Detta är en lösning för problemet med att onPause() och onResume() inte kallas
         * vid byte av tabb. Funktionen säger istället till när fragmentet är synligt eller inte,
         * och kan då kalla på onPause()/onResume(). Motsvarande implementerades i de andra fragmenten,
         * för att Calculator ska kunna ladda in aktuella värden.
         *
         * Hittades på: http://stackoverflow.com/questions/9779397/detect-viewpager-tab-change-inside-fragment
         * http://stackoverflow.com/questions/10024739/how-to-determine-when-fragment-becomes-visible-in-viewpager
         */
        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible()) {
                // If we are becoming invisible, then...
                if (!isVisibleToUser) {
                    onPause();
                }
            }
        }

        public void initSpinners() {
            spnAmount = (Spinner) getActivity().findViewById(R.id.spnAmount);
            spnType = (Spinner) getActivity().findViewById(R.id.spnType);

            /**
             * Använder en del switch case vilket inte är skalbart, precis.
             * Anledningen är att jag inte riktigt vet hur jag ska hantera tilldelningen av
             * värden i spinners annars.
             */
            switch(spnAmountDef) {
                case "1":
                    spnAmount.setSelection(0);
                    break;
                case "2":
                    spnAmount.setSelection(1);
                    break;
                case "3":
                    spnAmount.setSelection(2);
                    break;
                case "4":
                    spnAmount.setSelection(3);
                    break;
                case "5":
                    spnAmount.setSelection(4);
                    break;
                case "6":
                    spnAmount.setSelection(5);
                    break;
                default:
                    spnAmount.setSelection(5);
                    break;
            }

            switch(spnTypeDef) {
                case "d6":
                    spnType.setSelection(0);
                    break;
                case "d20":
                    spnType.setSelection(1);
                    break;
                default:
                    spnType.setSelection(0);
                    break;
            }

            spnAmount.setOnItemSelectedListener(this);
            spnType.setOnItemSelectedListener(this);
        }

        public void throwDice() {
            for(int i = 0; i < spnAmount.getSelectedItemPosition() + 1; i++) {
                diceArray[i].throwDice();
            }
        }

        public void saveDice() {
            for(int i = 0; i < DICE_MAX_AMOUNT; i++) {
                editor.putInt("dice" + Integer.toString(i + 1) + "_max_value", diceArray[i].getMaxValue());
                editor.putInt("dice" + Integer.toString(i + 1) + "_cur_value", diceArray[i].getCurValue());
                editor.putInt("dice" + Integer.toString(i + 1) + "_image_id", diceArray[i].getImageId());
            }
            spnAmountDef = spnAmount.getSelectedItem().toString();
            spnTypeDef = spnType.getSelectedItem().toString();
            editor.putString("dice_amount", spnAmountDef);
            editor.putString("dice_type", spnTypeDef);
            editor.commit();
        }
        public void loadDice(Dice[] diceArray) {
            spnAmountDef = pref.getString("dice_amount", "6");
            spnTypeDef = pref.getString("dice_type", "d6");
            for(int i = 0; i < DICE_MAX_AMOUNT; i++) {
                diceArray[i].maxValue = (pref.getInt("dice" + Integer.toString(i + 1) + "_max_value", 6));
                diceArray[i].setCurValue(pref.getInt("dice" + Integer.toString(i + 1) + "_cur_value", 1));
            }
        }

        /**
         * Ej skalbar implementation för spinners, men får jobbet gjort.
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch(parent.getId()) {
                case R.id.spnAmount:
                    for(int i = 0; i < DICE_MAX_AMOUNT; i++) {
                        if(i < spnAmount.getSelectedItemPosition() + 1) {
                            diceArray[i].setImage();
                        }
                        else {
                            diceArray[i].setImage(0);
                        }
                    }
                    break;
                case R.id.spnType:
                    switch(spnType.getSelectedItem().toString()) {
                        case "d6":
                            for(int i = 0; i < DICE_MAX_AMOUNT; i++) {
                                diceArray[i].setMaxValue(6);
                                if(i < spnAmount.getSelectedItemPosition() + 1) {
                                    diceArray[i].setImage();
                                }
                            }
                            break;
                        case "d20":
                            for(int i = 0; i < DICE_MAX_AMOUNT; i++) {
                                diceArray[i].setMaxValue(20);
                                if(i < spnAmount.getSelectedItemPosition() + 1) {
                                    diceArray[i].setImage();
                                }
                            }
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    /**
     * Fragment 2: Counters
     * Hantera räknare, namn, bild, färg, värde.
     * Dynamiskt skapande av views i programkod.
     * Leder vidare till CounterEdit.java.
     */
    public static class Counters extends Fragment {

        public static class CounterInstance {
            private int id;                 //Håller koll på plats i layouten.
            private int defValue;           //Default Value;
            private ImageView image;
            private int imageId = 0;        //Än en gång, sparar id för att imageview inte kan ge det i efterhand.
            private int color = 0;          //Färg till colorfilter i imageview.
            private TextView txtCurValue;
            private TextView txtName;

            CounterInstance(int id, TextView txtName, TextView txtValue, ImageView image) {
                this.id = id;
                this.txtName = txtName;
                txtName.setText("Default");
                defValue = 0;
                this.txtCurValue = txtValue;
                txtValue.setText("0");
                this.image = image;
            }

            public TextView getName() {
                return txtName;
            }
            public int getId() {
                return id;
            }
            public int getDefValue() {
                return defValue;
            }
            public TextView getCurValue() {
                return txtCurValue;
            }
            public ImageView getImage() {
                return image;
            }
            public int getImageId() {
                return imageId;
            }
            public int getColor() {
                return color;
            }

            public void setName(String name) {
                txtName.setText(name);
            }

            public void setDefValue(int defValue) {
                this.defValue = defValue;
            }

            public void setCurValue(int curValue) {
                if (curValue >= 0) {
                    txtCurValue.setText(Integer.toString(curValue));
                }
            }

            public void setImage(int resId) {
                image.setImageResource(resId);
                imageId = resId;
            }

            public void setColor(int color) {
                this.color = color;
                image.setColorFilter(color);
            }
        }

        private static final String ARG_SECTION_NUMBER = "section_number";

        CounterInstance counterArray[];
        TextView txtValueArray[];
        TextView txtNameArray[];
        ImageView imgArray[];

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        //Tillagda views
        LinearLayout layout;
        ImageButton imgbtnEdit;
        ImageButton imgbtnCounterPlus;
        ImageButton imgbtnCounterMinus;
        EditText txtfldCustomAmount;

        public static Counters newInstance(int sectionNumber) {
            Counters fragment = new Counters();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.counters_layout, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            pref = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            editor = pref.edit();

            txtNameArray = new TextView[COUNTER_MAX_AMOUNT];
            txtNameArray[0] = (TextView) getActivity().findViewById(R.id.txtName1);
            txtNameArray[1] = (TextView) getActivity().findViewById(R.id.txtName2);
            txtNameArray[2] = (TextView) getActivity().findViewById(R.id.txtName3);
            txtNameArray[3] = (TextView) getActivity().findViewById(R.id.txtName4);
            txtNameArray[4] = (TextView) getActivity().findViewById(R.id.txtName5);
            txtNameArray[5] = (TextView) getActivity().findViewById(R.id.txtName6);

            txtValueArray = new TextView[COUNTER_MAX_AMOUNT];
            txtValueArray[0] = (TextView) getActivity().findViewById(R.id.txtValue1);
            txtValueArray[1] = (TextView) getActivity().findViewById(R.id.txtValue2);
            txtValueArray[2] = (TextView) getActivity().findViewById(R.id.txtValue3);
            txtValueArray[3] = (TextView) getActivity().findViewById(R.id.txtValue4);
            txtValueArray[4] = (TextView) getActivity().findViewById(R.id.txtValue5);
            txtValueArray[5] = (TextView) getActivity().findViewById(R.id.txtValue6);

            imgArray = new ImageView[COUNTER_MAX_AMOUNT];
            imgArray[0] = (ImageView) getActivity().findViewById(R.id.imgCounter1);
            imgArray[1] = (ImageView) getActivity().findViewById(R.id.imgCounter2);
            imgArray[2] = (ImageView) getActivity().findViewById(R.id.imgCounter3);
            imgArray[3] = (ImageView) getActivity().findViewById(R.id.imgCounter4);
            imgArray[4] = (ImageView) getActivity().findViewById(R.id.imgCounter5);
            imgArray[5] = (ImageView) getActivity().findViewById(R.id.imgCounter6);

            counterArray = new CounterInstance[COUNTER_MAX_AMOUNT];
            for(int i = 0; i < COUNTER_MAX_AMOUNT; i++) {
                counterArray[i] = new CounterInstance(i + 1, txtNameArray[i], txtValueArray[i], imgArray[i]);
            }
            initCounters();
        }

        @Override
        public void onPause() {
            super.onPause();

            saveCounters();
            removeUIElements();
        }

        @Override
        public void onResume() {
            super.onResume();

            loadCounters(counterArray);
        }

        /**
         * Spara när fragmentet inte syns.
         */
        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible()) {
                // If we are becoming invisible, then...
                if (!isVisibleToUser) {
                   onPause();
                }
            }
        }

        //LISTENERS

        /**
         * Flera listeners med inbyggda konstanter skapas för att kunna återanvändas.
         */

        public class CounterOnClickListener implements View.OnClickListener {
            private final CounterInstance COUNTER;

            CounterOnClickListener(final CounterInstance COUNTER) {
                this.COUNTER = COUNTER;
            }

            @Override
            public void onClick(View v) {
                if (!COUNTER.getImage().isSelected()) {
                    removeUIElements();
                    COUNTER.getImage().setSelected(true);
                    addUIElements(COUNTER);
                }
                else {
                    removeUIElements();
                }
            }
        }

        public class EditClickListener implements View.OnClickListener {

            private final CounterInstance COUNTER;

            EditClickListener(final CounterInstance COUNTER) {
                this.COUNTER = COUNTER;
            }
            @Override
            public void onClick(View v) { //Skicka data för vald counter till CounterEdit.
                Intent i = new Intent(getActivity(), CounterEdit.class);
                i.putExtra("id", COUNTER.getId());
                i.putExtra("name", COUNTER.getName().getText());
                i.putExtra("def_value", COUNTER.getDefValue());
                i.putExtra("image_id", COUNTER.getImageId());
                i.putExtra("color", COUNTER.getColor());
                startActivity(i);
            }
        }

        public class PlusClickListener implements View.OnClickListener {

            private final CounterInstance COUNTER;

            PlusClickListener(final CounterInstance COUNTER) {
                this.COUNTER = COUNTER;
            }
            @Override
            public void onClick(View v) {
                int newValue = Integer.parseInt(this.COUNTER.getCurValue().getText().toString()) + 1;
                this.COUNTER.setCurValue(newValue);
                txtfldCustomAmount.setText(Integer.toString(newValue));
            }
        }

        public class MinusClickListener implements View.OnClickListener {

            private final CounterInstance COUNTER;

            MinusClickListener(final CounterInstance COUNTER) {
                this.COUNTER = COUNTER;
            }
            @Override
            public void onClick(View v) {
                int newValue = Integer.parseInt(this.COUNTER.getCurValue().getText().toString()) - 1;
                if (newValue >= 0) {
                    this.COUNTER.setCurValue(newValue);
                    txtfldCustomAmount.setText(Integer.toString(newValue));
                }
            }
        }

        public class CustomAmountListener implements TextView.OnEditorActionListener {
            private final CounterInstance COUNTER;

            CustomAmountListener(final CounterInstance COUNTER) {
                this.COUNTER = COUNTER;
            }

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        COUNTER.setCurValue(Integer.parseInt(v.getText().toString()));
                    }
                    catch (IllegalArgumentException e) {
                        Toast toast = Toast.makeText(getActivity(), R.string.toast_not_a_number, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                return false;
            }
        }

        //METHODS
        public void saveCounters() {
            for(int i = 0; i < COUNTER_MAX_AMOUNT; i++) {
                editor.putString("counter" + Integer.toString(counterArray[i].getId()) + "_name", counterArray[i].getName().getText().toString());
                editor.putInt("counter" + Integer.toString(counterArray[i].getId()) + "_cur_value", Integer.parseInt(counterArray[i].getCurValue().getText().toString()));
                editor.putInt("counter" + Integer.toString(counterArray[i].getId()) + "_def_value", counterArray[i].getDefValue());
                editor.putInt("counter" + Integer.toString(counterArray[i].getId()) + "_image_id", counterArray[i].getImageId());
                editor.putInt("counter" + Integer.toString(counterArray[i].getId()) + "_color", counterArray[i].getColor());
                editor.commit();
            }
        }
        public void loadCounters(CounterInstance[] counterArray) {
            for(int i = 0; i < COUNTER_MAX_AMOUNT; i++) {
                String name = pref.getString("counter" + Integer.toString(counterArray[i].getId()) + "_name", "default");
                int def = pref.getInt("counter" + Integer.toString(counterArray[i].getId()) + "_def_value", 0);
                int cur = pref.getInt("counter" + Integer.toString(counterArray[i].getId()) + "_cur_value", 0);
                int imgId = pref.getInt("counter" + Integer.toString(counterArray[i].getId()) + "_image_id", R.drawable.circle);
                int color = pref.getInt("counter" + Integer.toString(counterArray[i].getId()) + "_color", 0);

                counterArray[i].setName(name);
                counterArray[i].setDefValue(def);
                counterArray[i].setCurValue(cur);
                counterArray[i].setImage(imgId);
                counterArray[i].setColor(color);
            }
        }

        public void initCounters() {
            loadCounters(counterArray);
            for(int i = 0; i < COUNTER_MAX_AMOUNT; i++) {
                counterArray[i].image.setOnClickListener(new CounterOnClickListener(counterArray[i]));
            }
        }

        /**
         * add och removeUIElements används för att ändra och lägga till views för vald counter,
         * och ta bort dem när ingen counter är vald.
         */
        public void addUIElements(CounterInstance counter) {
            layout = (LinearLayout) getActivity().findViewById(R.id.CounterEditLayout);

            LayoutParams editParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            //Procedur för att hämta in värden i dp
            int editLeftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

            editParams.setMargins(editLeftMargin, 0, 0, 0);
            editParams.gravity = Gravity.CENTER_VERTICAL;

            LayoutParams plusminusParams = editParams;
            plusminusParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            plusminusParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

            imgbtnEdit = new ImageButton(getActivity());
            imgbtnEdit.setImageResource(R.drawable.ic_menu_edit);
            imgbtnEdit.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgbtnEdit.setOnClickListener(new EditClickListener(counter));

            imgbtnCounterPlus = new ImageButton(getActivity());
            imgbtnCounterPlus.setImageResource(R.drawable.plus);
            imgbtnCounterPlus.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgbtnCounterPlus.setOnClickListener(new PlusClickListener(counter));

            imgbtnCounterMinus = new ImageButton(getActivity());
            imgbtnCounterMinus.setImageResource(R.drawable.minus);
            imgbtnCounterMinus.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imgbtnCounterMinus.setOnClickListener(new MinusClickListener(counter));

            txtfldCustomAmount = new EditText(getActivity());
            txtfldCustomAmount.setText(counter.getCurValue().getText());
            txtfldCustomAmount.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
            txtfldCustomAmount.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            txtfldCustomAmount.setOnEditorActionListener(new CustomAmountListener(counter));
            txtfldCustomAmount.setSelectAllOnFocus(true);

            layout.addView(imgbtnEdit, editParams);
            layout.addView(txtfldCustomAmount, editParams);
            layout.addView(imgbtnCounterMinus, plusminusParams);
            layout.addView(imgbtnCounterPlus, plusminusParams);
        }

        public void removeUIElements() {
            if (layout != null) {
                if (layout.getChildCount() > 0) {
                    layout.removeAllViews();
                    for (int i = 0; i < COUNTER_MAX_AMOUNT; i++) {
                        counterArray[i].getImage().setSelected(false); //Ser till att inget är valt
                    }
                }
            }
        }
    }

    /**
     * Fragment 3: Calculator
     * Ladda in tärningar och räknare, inkludera dem i en simpel miniräknare.
     */
    public static class Calculator extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public static Calculator newInstance(int sectionNumber) {
            Calculator fragment = new Calculator();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        SharedPreferences pref;

        ImageButton imgbtnDiceArray[];
        ImageButton imgbtnCounterArray[];
        TextView txtCounterArray[];

        EditText txteditInput;

        int n1 = 0;        //Första och andra talet
        int n2 = 0;        //
        String operator;        // +, -, *, eller /.

        int diceAmount;

        Button btn1;
        Button btn2;
        Button btn3;
        Button btn4;
        Button btn5;
        Button btn6;
        Button btn7;
        Button btn8;
        Button btn9;
        Button btn0;
        Button btnPoint;
        Button btnDivide;
        Button btnMultiply;
        Button btnMinus;
        Button btnPlus;
        Button btnBack;
        Button btnC;
        Button btnEquals;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.calculator_layout, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            imgbtnDiceArray = new ImageButton[DICE_MAX_AMOUNT];
            imgbtnCounterArray = new ImageButton[COUNTER_MAX_AMOUNT];
            txtCounterArray = new TextView[COUNTER_MAX_AMOUNT];

            imgbtnDiceArray[0] = (ImageButton) getActivity().findViewById(R.id.btnDice1);
            imgbtnDiceArray[1] = (ImageButton) getActivity().findViewById(R.id.btnDice2);
            imgbtnDiceArray[2] = (ImageButton) getActivity().findViewById(R.id.btnDice3);
            imgbtnDiceArray[3] = (ImageButton) getActivity().findViewById(R.id.btnDice4);
            imgbtnDiceArray[4] = (ImageButton) getActivity().findViewById(R.id.btnDice5);
            imgbtnDiceArray[5] = (ImageButton) getActivity().findViewById(R.id.btnDice6);

            imgbtnCounterArray[0] = (ImageButton) getActivity().findViewById(R.id.btnCounter1);
            imgbtnCounterArray[1] = (ImageButton) getActivity().findViewById(R.id.btnCounter2);
            imgbtnCounterArray[2] = (ImageButton) getActivity().findViewById(R.id.btnCounter3);
            imgbtnCounterArray[3] = (ImageButton) getActivity().findViewById(R.id.btnCounter4);
            imgbtnCounterArray[4] = (ImageButton) getActivity().findViewById(R.id.btnCounter5);
            imgbtnCounterArray[5] = (ImageButton) getActivity().findViewById(R.id.btnCounter6);

            txtCounterArray[0] = (TextView) getActivity().findViewById(R.id.txtCounter1);
            txtCounterArray[1] = (TextView) getActivity().findViewById(R.id.txtCounter2);
            txtCounterArray[2] = (TextView) getActivity().findViewById(R.id.txtCounter3);
            txtCounterArray[3] = (TextView) getActivity().findViewById(R.id.txtCounter4);
            txtCounterArray[4] = (TextView) getActivity().findViewById(R.id.txtCounter5);
            txtCounterArray[5] = (TextView) getActivity().findViewById(R.id.txtCounter6);

            txteditInput = (EditText) getActivity().findViewById(R.id.txteditInput);

            btn1 = (Button) getActivity().findViewById(R.id.btn1);
            btn2 = (Button) getActivity().findViewById(R.id.btn2);
            btn3 = (Button) getActivity().findViewById(R.id.btn3);
            btn4 = (Button) getActivity().findViewById(R.id.btn4);
            btn5 = (Button) getActivity().findViewById(R.id.btn5);
            btn6 = (Button) getActivity().findViewById(R.id.btn6);
            btn7 = (Button) getActivity().findViewById(R.id.btn7);
            btn8 = (Button) getActivity().findViewById(R.id.btn8);
            btn9 = (Button) getActivity().findViewById(R.id.btn9);
            btn0 = (Button) getActivity().findViewById(R.id.btn0);
            btnPoint = (Button) getActivity().findViewById(R.id.btnPoint);
            btnDivide = (Button) getActivity().findViewById(R.id.btnDivide);
            btnMultiply = (Button) getActivity().findViewById(R.id.btnMultiply);
            btnMinus = (Button) getActivity().findViewById(R.id.btnMinus);
            btnPlus = (Button) getActivity().findViewById(R.id.btnPlus);
            btnBack = (Button) getActivity().findViewById(R.id.btnBack);
            btnC = (Button) getActivity().findViewById(R.id.btnC);
            btnEquals = (Button) getActivity().findViewById(R.id.btnEquals);


            pref = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            diceAmount = Integer.parseInt(pref.getString("dice_amount", "6"));

            setButtons();
        }

        @Override
        public void onResume() {
            super.onResume();
            loadDice();
            loadCounters();
        }

        /**
         * Laddar in nya dice/counters när fragmentet är synligt
         */
        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);

            // Make sure that we are currently visible
            if (this.isVisible()) {
                onResume();
            }
        }

        //Listeners

        //Fungerar för alla tal, även de som hämtas från dice/counter.
        public class NumberOnClickListener implements View.OnClickListener {
            final int VALUE;

            NumberOnClickListener(final int VALUE) {
                this.VALUE = VALUE;
            }
            @Override
            public void onClick(View v) {
                if (n2 != 0) {
                    n2 = 0;
                    txteditInput.setText("");
                }
                txteditInput.setText(txteditInput.getText() + Integer.toString(VALUE));
            }
        }

        //Methods
        public void loadDice() {
            for (int i = 0; i < diceAmount; i++) {
                imgbtnDiceArray[i].setImageResource(pref.getInt("dice" + Integer.toString(i + 1) + "_image_id", 0));
                imgbtnDiceArray[i].setOnClickListener(new NumberOnClickListener(pref.getInt("dice" + Integer.toString(i + 1) + "_cur_value", 1)));
            }
        }

        public void loadCounters() {
            int curValue;
            for (int i = 0; i < COUNTER_MAX_AMOUNT; i++) {
                imgbtnCounterArray[i].setImageResource(pref.getInt("counter" + Integer.toString(i + 1) + "_image_id", 0));
                imgbtnCounterArray[i].setColorFilter(pref.getInt("counter" + Integer.toString(i + 1) + "_color", 0));
                curValue = pref.getInt("counter" + Integer.toString(i + 1) + "_cur_value", 0);
                imgbtnCounterArray[i].setOnClickListener(new NumberOnClickListener(curValue));
                txtCounterArray[i].setText(Integer.toString(curValue));
            }
        }

        public void setButtons() {
            btn1.setOnClickListener(new NumberOnClickListener(1));
            btn2.setOnClickListener(new NumberOnClickListener(2));
            btn3.setOnClickListener(new NumberOnClickListener(3));
            btn4.setOnClickListener(new NumberOnClickListener(4));
            btn5.setOnClickListener(new NumberOnClickListener(5));
            btn6.setOnClickListener(new NumberOnClickListener(6));
            btn7.setOnClickListener(new NumberOnClickListener(7));
            btn8.setOnClickListener(new NumberOnClickListener(8));
            btn9.setOnClickListener(new NumberOnClickListener(9));
            btn0.setOnClickListener(new NumberOnClickListener(0));

            //btnPoint.setOnClickListener(); Ej implementerad decimal

            btnDivide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operator = "/";
                    if (n1 == 0) {
                        n1 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                    }
                    else if (n2 != 0) {
                        n2 = 0;
                        txteditInput.setText("");
                    }
                    else {
                        n2 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                        n1 = n1 / n2;
                        txteditInput.setText("Result : " + Integer.toString(n1));
                    }
                }
            });

            btnMultiply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operator = "*";
                    if (n1 == 0) {
                        n1 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                    }
                    else if (n2 != 0) {
                        n2 = 0;
                        txteditInput.setText("");
                    }
                    else {
                        n2 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                        n1 = n1 * n2;
                        txteditInput.setText("Result : " + Integer.toString(n1));
                    }
                }
            });

            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operator = "-";
                    if (n1 == 0) {
                        n1 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                    }
                    else if (n2 != 0) {
                        n2 = 0;
                        txteditInput.setText("");
                    }
                    else {
                        n2 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                        n1 = n1 - n2;
                        txteditInput.setText("Result : " + Integer.toString(n1));
                    }
                }
            });

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    operator = "+";
                    if (n1 == 0) {
                        n1 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                    }
                    else if (n2 != 0) {
                        n2 = 0;
                        txteditInput.setText("");
                    }
                    else {
                        n2 = Integer.parseInt(txteditInput.getText().toString());
                        txteditInput.setText("");
                        n1 = n1 + n2;
                        txteditInput.setText("Result : " + Integer.toString(n1));
                    }
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = txteditInput.getText().toString();
                    if (text != "") {
                        if (text.length() >= 2) {
                            txteditInput.setText(text.substring(0, text.length() - 1));
                        }
                        else {
                            txteditInput.setText("");
                        }
                    }
                }
            });

            btnC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    n1 = 0;
                    n2 = 0;
                    txteditInput.setText("");
                }
            });

            btnEquals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (operator != "") {
                        if (n2 != 0) {
                            if (operator == "+") {
                                txteditInput.setText("");
                                txteditInput.setText(Integer.toString(n1));
                            }
                            else if (operator == "-") {
                                txteditInput.setText("");
                                txteditInput.setText(Integer.toString(n1));
                            }
                            else if (operator == "*") {
                                txteditInput.setText("");
                                txteditInput.setText(Integer.toString(n1));
                            }
                            else if (operator == "/") {
                                txteditInput.setText("");
                                txteditInput.setText(Integer.toString(n1));
                            }
                        }
                        else {
                            perfOperation();
                        }
                    }
                }
            });
        }

        /**
         * Väljer operator från sparad sträng, hämtar in tal, visar resultat i textfönstret.
         */
        public void perfOperation() {
            if (operator == "/") {
                n2 = Integer.parseInt(txteditInput.getText().toString());
                txteditInput.setText("");
                n1 = n1 / n2;
                txteditInput.setText(Integer.toString(n1));
            }
            else if (operator == "*") {
                n2 = Integer.parseInt(txteditInput.getText().toString());
                txteditInput.setText("");
                n1 = n1 * n2;
                txteditInput.setText(Integer.toString(n1));
            }
            else if (operator == "-") {
                n2 = Integer.parseInt(txteditInput.getText().toString());
                txteditInput.setText("");
                n1 = n1 - n2;
                txteditInput.setText(Integer.toString(n1));
            }
            else if (operator == "+") {
                n2 = Integer.parseInt(txteditInput.getText().toString());
                txteditInput.setText("");
                n1 = n1 + n2;
                txteditInput.setText(Integer.toString(n1));
            }
        }
    }
}
