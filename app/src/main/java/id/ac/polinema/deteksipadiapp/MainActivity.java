package id.ac.polinema.deteksipadiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragments.BioFragment;
import fragments.HomeFragment;
import fragments.KatamFragment;
import fragments.PupukFragment;
import fragments.SettingFragment;
import id.ac.polinema.appmusic.R;
import id.ac.polinema.deteksipadiapp.testing.ColorDetection;
import id.ac.polinema.deteksipadiapp.testing.TestingBentuk;
import id.ac.polinema.deteksipadiapp.testing.TestingWarna;
import id.ac.polinema.deteksipadiapp.testing.Thresholding;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new BioFragment());
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        // beri listener pada saat item/menu bottomnavigation terpilih
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return false;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.action_home:
                fragment = new BioFragment();
                break;
            case R.id.action_katam:
                fragment = new KatamFragment();
                break;
            case R.id.action_pupuk:
                fragment = new PupukFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private void openfragment(HomeFragment fragment) {
        openFragment(fragment, false);
    }

    public void btnToast(View view) {
        Intent intent = new Intent(this, TestingWarna.class);
        startActivity(intent);
    }
    public void btnToast2(View view) {
        Intent intent = new Intent(this, TestingBentuk.class);
        startActivity(intent);
    }
    public void btnToast3(View view) {
        Intent intent = new Intent(this, Thresholding.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO: open settings here using openFragment()
            openFragment(new SettingFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    private void openFragment(Fragment fragment) {
        openFragment(fragment, false);
    }

    private void openFragment(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (addToBackstack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

}